package me.escoffier.device.enrichement;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/monitoring")
public class MonitoringResource {


    record Counters(long numberOfTemperatures, long numberOfSnapshots) {
    }

    @Inject
    MessageCounter counter;

    @GET
    public Counters get() {
        return new Counters(counter.count("temperatures"), counter.count("snapshots"));
    }
}
