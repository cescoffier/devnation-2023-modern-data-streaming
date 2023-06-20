package me.escoffier.alert;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/monitoring")
public class MonitoringResource {


    record Counters(long numberOfEnrichedTemperatures, long numberOfPredictions, long numberOfTemperatureAlerts,
                    long numberOfRabbitAlerts) {
    }

    @Inject
    PredictionAndMessageCounter counter;

    @GET
    public Counters get() {
        return new Counters(counter.count("enriched-temperatures"),
                counter.count("predictions"), counter.count("temperature-alerts"), counter.count("rabbit-alerts"));
    }
}
