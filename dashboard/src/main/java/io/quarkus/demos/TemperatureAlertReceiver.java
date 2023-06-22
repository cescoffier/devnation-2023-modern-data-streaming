package io.quarkus.demos;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;
import me.escoffier.device.TemperatureAlert;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class TemperatureAlertReceiver {

    private final List<TemperatureAlert> temperatureAlertList = new CopyOnWriteArrayList<>();

    @ConfigProperty(name="dashboard.alert-manager.url")
    String alertManagerBaseUrl;

    Logger log = Logger.getLogger(TemperatureAlertReceiver.class);

    public void onStart(@Observes StartupEvent startupEvent) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(alertManagerBaseUrl + "/temperatures");
        try(SseEventSource eventSource = SseEventSource.target(target).build()) {
            eventSource.register( event -> {
                TemperatureAlert temperatureAlert = event.readData(TemperatureAlert.class);
                if(temperatureAlert.device()!=null) {
                    temperatureAlertList.add(temperatureAlert);
                }
                log.info("Received event " + temperatureAlert);
            });
            eventSource.open();
        }
    }

    public List<TemperatureAlert> getAlerts() {
        return temperatureAlertList;
    }


}
