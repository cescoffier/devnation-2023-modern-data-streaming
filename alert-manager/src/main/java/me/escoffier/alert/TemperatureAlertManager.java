package me.escoffier.alert;

import jakarta.enterprise.context.ApplicationScoped;
import me.escoffier.device.TemperatureAlert;
import me.escoffier.device.TemperatureWithLocation;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class TemperatureAlertManager {

    Map<String, Double> last = new HashMap<>();

    @Incoming("enriched-temperatures")
    @Outgoing("temperature-alerts")
    TemperatureAlert detect(TemperatureWithLocation temperature) {
        // Detect abnormal variations for a specific device.
        var lastValue = last.getOrDefault(temperature.deviceId, temperature.value);
        if (Math.abs(lastValue - temperature.value) > 5.0) {
            // Something wrong is happening.
            return new TemperatureAlert(temperature.deviceId, temperature.location, temperature.value);
        }
        return null;
    }


}
