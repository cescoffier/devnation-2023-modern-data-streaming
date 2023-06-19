package me.escoffier.alert;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Disabled // Manual test only.
class PredictionServiceTest {

    @RestClient PredictionService prediction;

    @Test
    void test() {
        byte[] img = load("src/test/resources/snapshots/rabbit-2.jpg");
        var predictions = prediction.analyze(new PredictionService.PredictionRequest(img));
        System.out.println(found(predictions.detections()));
    }


    private byte[] load(String path) {
        File f = new File(path);
        try {
            return Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> found(PredictionService.Detections[] predictions) {
       return Arrays.stream(predictions)
                .filter(d -> d.score() * 100 > 30)
                .map(d -> d.label())
                .collect(Collectors.toSet());
    }

}