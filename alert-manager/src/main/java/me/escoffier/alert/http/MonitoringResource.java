package me.escoffier.alert.http;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import me.escoffier.alert.PredictionAndMessageCounter;
import me.escoffier.device.Counters;

@Path("/monitoring")
public class MonitoringResource {


    @Inject
    PredictionAndMessageCounter counter;

    @GET
    public Counters get() {
        return new Counters(counter.count("enriched-temperatures"),
                counter.count("predictions"), counter.count("temperature-alerts"), counter.count("rabbit-alerts"));
    }
}
