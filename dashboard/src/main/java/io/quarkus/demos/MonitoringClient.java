package io.quarkus.demos;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import me.escoffier.device.Counters;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/monitoring")
@RegisterRestClient(configKey = "monitoring")
public interface MonitoringClient {

    @GET
    Counters get();
}
