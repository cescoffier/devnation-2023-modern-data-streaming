package io.quarkus.demos;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.sse.SseEventSource;
import me.escoffier.device.Device;
import me.escoffier.device.TemperatureAlert;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class TemperatureAlertReceiver {

    @Inject
    MockDeviceTool mockDeviceTool;

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


    @Scheduled(every = "1m")
    void clearAlerts() {
        int size = temperatureAlertList.size();
        temperatureAlertList.clear();
        log.infof("Cleared the alert list of %d alerts",size);

//        if (ConfigUtils.isProfileActive("dev")) {
////            TemperatureAlert tempAlert = new TemperatureAlert("t-0008","Stockholm",50);
//            List<Device> deviceList = mockDeviceTool.mockDeviceList();
//            Random rand = new Random(System.currentTimeMillis());
//            int numberOfErrors = rand.nextInt(deviceList.size() / 2 + 1);
//            List<TemperatureAlert> randomAlerts = deviceList.subList(0, numberOfErrors).stream().map(d -> new TemperatureAlert(d.id(), d.location(), -10.0)).collect(Collectors.toList());
//            temperatureAlertList.addAll(randomAlerts);
//        }

    }


}
