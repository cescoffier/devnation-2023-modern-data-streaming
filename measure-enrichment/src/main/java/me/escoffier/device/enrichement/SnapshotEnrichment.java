package me.escoffier.device.enrichement;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import me.escoffier.device.Snapshot;
import me.escoffier.device.SnapshotWithLocation;
import me.escoffier.device.Temperature;
import me.escoffier.device.TemperatureWithLocation;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class SnapshotEnrichment {

    @Incoming("cameras")
    @Outgoing("enriched-snapshots")
    @Blocking
    @Transactional
    Record<String, SnapshotWithLocation> enrich(Snapshot snapshot) {
        String location = lookup(snapshot.deviceId);
        System.out.println("Enriching snapshot from " + snapshot.deviceId + " => " + location);
        return Record.of(snapshot.deviceId, new SnapshotWithLocation(snapshot, location));
    }

    String lookup(String deviceId) {
        return DeviceLocation.findLocationForDevice(deviceId);
    }
}
