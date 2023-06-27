package io.quarkus.demos;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import me.escoffier.device.Device;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@SuppressWarnings("unused")
@Path("/devices")
@RegisterRestClient(configKey = "devices")
public interface DeviceClient {

    @SuppressWarnings("unused")
    @GET
    List<Device> get();
}
