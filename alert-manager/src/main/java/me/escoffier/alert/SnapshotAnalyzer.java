package me.escoffier.alert;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
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

    record Snapshots(List<SnapshotWithLocation> list, String location) {};

    @Incoming("enriched-snapshots")
    @Outgoing("grouped-snapshots")
    public Multi<Snapshots> detect(Multi<SnapshotWithLocation> snapshot) {
        return snapshot
                .group().by(s -> s.location) // KeyedMulti once we use 3.2
                .flatMap(group -> {
                    String location = group.key();
                    System.out.println("snapshot from location " + location);
                    return group.group().intoLists().every(Duration.ofSeconds(5)) // + 0
                            .map(l -> new Snapshots(l, location));
                });
    }

    @Incoming("grouped-snapshots")
    @Outgoing("rabbit-alerts")
    @Blocking
    public RabbitAlert analyze(Snapshots snapshots) {
        if (containsTooManyRabbits(snapshots.list())) {
            return new RabbitAlert(snapshots.location());
        }
        return null;
    }

    private boolean containsTooManyRabbits(List<SnapshotWithLocation> snapshots) {
        return snapshots.stream()
                .map(s -> predictions.analyze(new PredictionService.PredictionRequest(s.picture)))
                .map(pr -> extractObjects(pr))
                .anyMatch(s -> s.contains("Rabbit"));
    }


    private Set<String> extractObjects(PredictionService.PredictionResponse response) {
        return Arrays.stream(response.detections())
                .filter(d -> d.score() * 100 > 30)
                .map(d -> d.label())
                .collect(Collectors.toSet());
    }

}
