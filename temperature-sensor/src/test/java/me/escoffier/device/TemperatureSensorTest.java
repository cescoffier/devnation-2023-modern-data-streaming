package me.escoffier.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
class TemperatureSensorTest {

    @InjectKafkaCompanion
    KafkaCompanion companion;

    @Inject
    ObjectMapper mapper;

    @Test
    void verifyEmission() {
        ConsumerTask<String, String> temperatures = companion.consume(String.class).fromTopics("temperatures", 2);
        temperatures.awaitCompletion();

        temperatures.forEach(cr -> {
            try {
                System.out.println(cr.value());
                var temp = mapper.readValue(cr.value(), Temperature.class);
                Assertions.assertNotNull(temp.deviceId);
                Assertions.assertTrue(temp.value >= 10);
                System.out.println(temp.timestamp);
            } catch (JsonProcessingException e) {
                Assertions.fail(e);
            }
        });

    }


}