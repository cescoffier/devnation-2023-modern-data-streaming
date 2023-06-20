package me.escoffier.device.enrichement;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.restassured.RestAssured;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import me.escoffier.device.Temperature;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
class MonitoringResourceTest {


    @InjectKafkaCompanion
    KafkaCompanion companion;

    @Inject MessageCounter counter;

    @Test
    void test() {
        counter.setTtl(Duration.ofSeconds(15));
        MonitoringResource.Counters counters = RestAssured.get("/monitoring")
                .as(MonitoringResource.Counters.class);
        assertThat(counters.numberOfSnapshots()).isZero();
        assertThat(counters.numberOfTemperatures()).isZero();

        companion.produce(new Serde<String>() {
                    @Override
                    public Serializer<String> serializer() {
                        return new StringSerializer();
                    }

                    @Override
                    public Deserializer<String> deserializer() {
                        return new StringDeserializer();
                    }
                }, new Serde<Temperature>() {
                    @Override
                    public Serializer<Temperature> serializer() {
                        return new ObjectMapperSerializer<>() {
                            @Override
                            public byte[] serialize(String topic, Temperature data) {
                                return super.serialize(topic, data);
                            }
                        };
                    }

                    @Override
                    public Deserializer<Temperature> deserializer() {
                        return new ObjectMapperDeserializer<>(Temperature.class);
                    }
                })
                .fromRecords(Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                        .map(i -> new Temperature("t-0001", i * 10.0))
                        .map(temp -> new ProducerRecord<>("temperatures", "t-0001", temp))
                        .collect(Collectors.toList()))
                        .awaitCompletion();

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            var c = RestAssured.get("/monitoring")
                    .as(MonitoringResource.Counters.class);
            assertThat(c.numberOfSnapshots()).isZero();
            assertThat(c.numberOfTemperatures()).isEqualTo(10);
        });

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            var c = RestAssured.get("/monitoring")
                    .as(MonitoringResource.Counters.class);
            assertThat(c.numberOfSnapshots()).isZero();
            assertThat(c.numberOfTemperatures()).isZero();
        });

    }


}