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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class RabbitAlertReceiver {

    private final Map<String,RabbitAlert> rabbitAlertMap = new ConcurrentHashMap<>();

    @ConfigProperty(name="dashboard.alert-manager.url")
    String alertManagerBaseUrl;

    Logger log = Logger.getLogger(RabbitAlertReceiver.class);

    @SuppressWarnings("unused")
    public void onStart(@Observes StartupEvent startupEvent) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(alertManagerBaseUrl + "/rabbits");
        try(SseEventSource eventSource = SseEventSource.target(target).build()) {
            eventSource.register( event -> {
                RabbitAlert rabbitAlert = event.readData(RabbitAlert.class);
                if(rabbitAlert.location()!=null) {
                    rabbitAlertMap.putIfAbsent(rabbitAlert.location(),rabbitAlert);
                }
                log.info("Received event " + rabbitAlert);
            });
            eventSource.open();
        }
    }

    public List<RabbitAlert> getAlerts() {
        return rabbitAlertMap.values().stream().toList();
    }


    @Scheduled(every = "1m")
    void clearAlerts() {
        int size = rabbitAlertMap.size();
        rabbitAlertMap.clear();
        log.infof("Cleared the alert list of %d alerts",size);

    }


}
