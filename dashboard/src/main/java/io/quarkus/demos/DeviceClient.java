package io.quarkus.demos;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import me.escoffier.device.Device;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/devices")
@RegisterRestClient(configKey = "devices")
public interface DeviceClient {

    @GET
    List<Device> get();
}
