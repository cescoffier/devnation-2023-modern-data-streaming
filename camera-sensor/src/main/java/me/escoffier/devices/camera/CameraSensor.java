package me.escoffier.devices.camera;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import me.escoffier.device.Snapshot;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class CameraSensor implements Runnable {

    private final ScheduledExecutorService executor;
    private final String id;
    private final Duration period;
    private final Emitter<Snapshot> emitter;
    private final boolean misbehave;

    private final Random random = new Random();

    private volatile boolean done = false;

    @Inject
    Logger logger;

    @Inject
    public CameraSensor(ScheduledExecutorService executor,
                        @ConfigProperty(name = "device.id") String id,
                        @ConfigProperty(name = "device.period") Duration period,
                        @ConfigProperty(name = "device.misbehavior") boolean misbehavior,
                        @Channel("cameras") Emitter<Snapshot> emitter
    ) {
        this.executor = executor;
        this.id = id;
        this.period = period;
        this.emitter = emitter;
        this.misbehave = misbehavior;
    }

    public void init(@Observes StartupEvent ignored) {
        this.executor.schedule(this, period.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void stop(@Observes ShutdownEvent ignored) {
        done = true;
    }

    @Override
    public void run() {
        boolean correct = !misbehave || !random.nextBoolean();

        try {
            Snapshot snapshot;
            String name;
            if (correct) {
                int index = random.nextInt(3) + 1;
                name = "snapshots/factory-" + index + ".jpg";
                URL url = CameraSensor.class.getClassLoader().getResource(name);
                byte[] img = IOUtils.toByteArray(url);
                snapshot = new Snapshot(id, img);
            } else {
                if (random.nextBoolean()) {
                    name = "snapshots/rabbit-1.jpg";
                } else {
                    name = "snapshots/rabbit-2.jpg";
                }
                URL url = CameraSensor.class.getClassLoader().getResource(name);
                byte[] img = IOUtils.toByteArray(url);
                snapshot = new Snapshot(id, img);
            }
            System.out.println("File: " + name);
            if (!done) {
                emitter.send(snapshot)
                        .thenAccept(x -> logger.infof("Device '%s' sent picture '%s'", id, name));
                this.executor.schedule(this, period.toMillis(), TimeUnit.MILLISECONDS);
            }
        } catch (IOException e) {
            logger.error("Unable to capture a snapshot", e);
            throw new RuntimeException(e);
        }
    }
}
