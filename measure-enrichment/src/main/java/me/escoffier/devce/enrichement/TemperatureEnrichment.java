package me.escoffier.devce.enrichement;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import me.escoffier.device.Temperature;
import me.escoffier.device.TemperatureWithLocation;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class TemperatureEnrichment {

    @Incoming("temperatures")
    @Outgoing("enriched-temperatures")
    @Blocking
    Record<String, TemperatureWithLocation> enrich(Temperature temperature) {
        String location = lookup(temperature.deviceId);
        return Record.of(temperature.deviceId, new TemperatureWithLocation(temperature, location));
    }

    String lookup(String deviceId) {
        return DeviceLocation.findLocationForDevice(deviceId);
    }
}
