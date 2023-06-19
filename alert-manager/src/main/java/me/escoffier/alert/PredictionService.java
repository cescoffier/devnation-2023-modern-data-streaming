package me.escoffier.alert;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://picture-analyzer-devnation.apps.cluster-9lgs8.9lgs8.sandbox272.opentlc.com/")
public interface PredictionService {

    @Path("/predictions")
    @POST
    PredictionResponse analyze(PredictionRequest request);

    record PredictionRequest(byte[] image){}

    record PredictionResponse(Detections[] detections) {}

    record Detections(Box box, String label, double score) {}

    record Box(double xMax, double xMin, double yMax, double yMin) {}
}
