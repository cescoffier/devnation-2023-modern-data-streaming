package me.escoffier.alert;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import me.escoffier.device.TemperatureAlert;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("temperatures")
public class TemperatureResource {

    @Channel("temperature-alerts") Multi<TemperatureAlert> alerts;

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<TemperatureAlert> stream() {
        return alerts;
    }


}
