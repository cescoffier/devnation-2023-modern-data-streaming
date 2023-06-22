package me.escoffier.alert.http;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import me.escoffier.device.RabbitAlert;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.time.Duration;

@Path("rabbits")
public class RabbitAlertResource {

    @Channel("rabbit-alerts")
    Multi<RabbitAlert> alerts;

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<RabbitAlert> stream() {
        return Multi.createBy().merging().streams(Multi.createFrom().ticks().every(Duration.ofSeconds(10))
                        .map(l -> new RabbitAlert(null, null)),
                alerts);
    }


}
