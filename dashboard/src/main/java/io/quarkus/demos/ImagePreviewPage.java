package io.quarkus.demos;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import me.escoffier.device.RabbitAlert;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.reactive.RestPath;

import java.io.IOException;
import java.net.URL;
import java.util.Base64;

@Path("/rabbit-image")
public class ImagePreviewPage {

    public static final String DEFAULT_IMAGE = "snapshots/no-image.png";

    @Inject
    RabbitAlertReceiver rabbitAlertReceiver;

    @Inject
    Template rabbit;

    @GET
    @Path("/{location}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance alertForLocation(@RestPath String location) {
        RabbitAlert alert = rabbitAlertReceiver.getAlerts().stream().filter(rabbitAlert -> location.equals(rabbitAlert.location())).findFirst().orElseGet(() -> getFallbackRabbitAlert(location));
        AlertForTemplate aft = new AlertForTemplate();
        aft.imageAsBase64 = Base64.getEncoder().encodeToString(alert.snapshot());
        return rabbit.data("alert",aft);
    }

    public static class AlertForTemplate {
        public String imageAsBase64;

        public String imageAsBase64() {
            return imageAsBase64;
        }
    }

    private static RabbitAlert getFallbackRabbitAlert(String location) {
        try {
            URL url = ImagePreviewPage.class.getClassLoader().getResource(DEFAULT_IMAGE);
            byte[] defaultImage = IOUtils.toByteArray(url);
            RabbitAlert fallbackRabbitAlert = new RabbitAlert(location,defaultImage);
            return fallbackRabbitAlert;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}