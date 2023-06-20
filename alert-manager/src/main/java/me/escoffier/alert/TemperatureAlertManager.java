package me.escoffier.alert;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import me.escoffier.device.TemperatureAlert;
import me.escoffier.device.TemperatureWithLocation;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TemperatureAlertManager {

    @Inject
    PredictionAndMessageCounter counter;

    @Incoming("enriched-temperatures")
    @Outgoing("temperature-alerts")
    TemperatureAlert detect(TemperatureWithLocation temperature) {
        counter.inc("enriched-temperatures");
        // Detect abnormal variations for a specific device.
        if (temperature.value > 30 || temperature.value < 10) {
            // Something wrong is happening.
            counter.inc("temperature-alerts");
            return new TemperatureAlert(temperature.deviceId, temperature.location, temperature.value);
        }
        return null;
    }
}
