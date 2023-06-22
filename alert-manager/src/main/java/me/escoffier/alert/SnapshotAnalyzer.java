package me.escoffier.alert;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.keyed.Keyed;
import io.smallrye.reactive.messaging.keyed.KeyedMulti;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import me.escoffier.device.RabbitAlert;
import me.escoffier.device.SnapshotWithLocation;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class SnapshotAnalyzer {

    @RestClient
    PredictionService predictions;

    @Inject
    PredictionAndMessageCounter counter;

    record Snapshots(List<SnapshotWithLocation> list, String location) {
    }


    @Incoming("enriched-snapshots")
    @Outgoing("grouped-snapshots")
    public Multi<Snapshots> detect(@Keyed(PerLocation.class) KeyedMulti<String, SnapshotWithLocation> snapshot) {
        return snapshot
                .invoke(() -> counter.inc("enriched-snapshots"))
                .group().intoLists().every(Duration.ofSeconds(5))
                .map(list -> new Snapshots(list, snapshot.key()));
    }

    @Incoming("grouped-snapshots")
    @Outgoing("rabbit-alerts")
    @Blocking(ordered = false)
    public RabbitAlert analyze(Snapshots snapshots) {
        var containRabbits = containsTooManyRabbits(snapshots.list());
        if (containRabbits != null) {
            counter.inc("rabbit-alerts");
            System.out.println("Producing a rabbit alert for " + snapshots.location());
            return new RabbitAlert(snapshots.location(), containRabbits.picture);
        }
        return null;
    }

    private SnapshotWithLocation containsTooManyRabbits(List<SnapshotWithLocation> snapshots) {
        for (SnapshotWithLocation snapshot : snapshots) {
            counter.inc("predictions");
            var res = predictions.analyze(new PredictionService.PredictionRequest(snapshot.picture));
            var objects = extractObjects(res);
            if (objects.contains("Rabbit")) {
                return snapshot;
            }
        }
        return null;
    }


    private Set<String> extractObjects(PredictionService.PredictionResponse response) {
        return Arrays.stream(response.detections())
                .filter(d -> d.score() * 100 > 30)
                .map(d -> d.label())
                .collect(Collectors.toSet());
    }

}
