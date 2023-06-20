package me.escoffier.alert;


import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.temporal.ChronoUnit;

@RegisterRestClient(baseUri = "stork://prediction-service")
public interface PredictionService {

    @Path("/predictions")
    @POST
    @Retry(maxRetries = 2, maxDuration = 20, durationUnit = ChronoUnit.SECONDS)
    PredictionResponse analyze(PredictionRequest request);

    record PredictionRequest(byte[] image){}

    record PredictionResponse(Detections[] detections) {}

    record Detections(Box box, String label, double score) {}

    record Box(double xMax, double xMin, double yMax, double yMin) {}
}
