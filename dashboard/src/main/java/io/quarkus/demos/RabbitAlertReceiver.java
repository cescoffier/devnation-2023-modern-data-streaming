package io.quarkus.demos;

import io.quarkus.runtime.ConfigConfig;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
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
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class RabbitAlertReceiver {

    private final List<RabbitAlert> rabbitAlertList = new CopyOnWriteArrayList<>();

    @ConfigProperty(name="dashboard.alert-manager.url")
    String alertManagerBaseUrl;

    Logger log = Logger.getLogger(RabbitAlertReceiver.class);

    public void onStart(@Observes StartupEvent startupEvent) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(alertManagerBaseUrl + "/rabbits");
        try(SseEventSource eventSource = SseEventSource.target(target).build()) {
            eventSource.register( event -> {
                RabbitAlert rabbitAlert = event.readData(RabbitAlert.class);
                if(rabbitAlert.location()!=null) {
                    rabbitAlertList.add(rabbitAlert);
                }
                log.info("Received event " + rabbitAlert);
            });
            eventSource.open();
        }
    }

    public List<RabbitAlert> getAlerts() {
        return rabbitAlertList;
    }


    @Scheduled(every = "1m")
    void clearAlerts() {
        int size = rabbitAlertList.size();
        rabbitAlertList.clear();
        log.infof("Cleared the alert list of %d alerts",size);

//        if (ConfigUtils.isProfileActive("dev")) {
//            RabbitAlert rabbitAlert = new RabbitAlert("Stockholm",null);
//            rabbitAlertList.add(rabbitAlert);
//        }

    }


}
