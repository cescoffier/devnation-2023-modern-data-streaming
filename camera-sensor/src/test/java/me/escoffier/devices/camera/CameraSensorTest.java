package me.escoffier.devices.camera;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import me.escoffier.device.Snapshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
class CameraSensorTest {
    @InjectKafkaCompanion
    KafkaCompanion companion;

    @Inject
    ObjectMapper mapper;

    @Test
    void verifyEmission() {
        ConsumerTask<String, String> snapshots = companion.consume(String.class).fromTopics("cameras", 2);
        snapshots.awaitCompletion();

        snapshots.forEach(cr -> {
            try {
                var snap = mapper.readValue(cr.value(), Snapshot.class);
                Assertions.assertNotNull(snap.deviceId);
                Assertions.assertNotNull(snap.picture);
            } catch (JsonProcessingException e) {
                Assertions.fail(e);
            }
        });

    }
}