package me.escoffier.device.enrichement;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import me.escoffier.device.Temperature;
import me.escoffier.device.TemperatureWithLocation;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class TemperatureEnrichment {
    private final MessageCounter counter;

    public TemperatureEnrichment(MessageCounter counter) {
        this.counter = counter;
    }
    @Incoming("temperatures")
    @Outgoing("enriched-temperatures")
    @Blocking
    @Transactional
    Record<String, TemperatureWithLocation> enrich(Temperature temperature) {
        counter.inc("temperatures");
        String location = lookup(temperature.deviceId);
        return Record.of(temperature.deviceId, new TemperatureWithLocation(temperature, location));
    }

    String lookup(String deviceId) {
        return DeviceEntity.findLocationForDevice(deviceId);
    }
}
