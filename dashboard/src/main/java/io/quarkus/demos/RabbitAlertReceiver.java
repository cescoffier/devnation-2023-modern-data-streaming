package io.quarkus.demos;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;
import me.escoffier.device.RabbitAlert;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RabbitAlertReceiver {

    private final List<RabbitAlert> rabbitAlertList = new ArrayList<>();

    @ConfigProperty(name="dashboard.alert-manager.url")
    String alertManagerBaseUrl;

    Logger log = Logger.getLogger(RabbitAlertReceiver.class);

    public void onStart(@Observes StartupEvent startupEvent) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(alertManagerBaseUrl + "/rabbits");
        try(SseEventSource eventSource = SseEventSource.target(target).build()) {
            eventSource.register( event -> {
                RabbitAlert temperatureAlert = event.readData(RabbitAlert.class);
                if(temperatureAlert.location()!=null) {
                    rabbitAlertList.add(temperatureAlert);
                }
                log.info("Received event " + temperatureAlert);
            });
            eventSource.open();
        }
    }

    public List<RabbitAlert> getAlerts() {
        return rabbitAlertList;
    }


    @Scheduled(every = "10m")
    void clearAlerts() {
        int size = rabbitAlertList.size();
        rabbitAlertList.clear();
        log.infof("Cleared the alert list of %d alerts",size);

    }


}
