package me.escoffier.device;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TemperatureSensor implements Runnable {

    final ScheduledExecutorService executor;
    final Duration period;
    final String id;
    final double delta;
    final Random random = new Random();
    final Emitter<Temperature> emitter;
    final private double base;
    final boolean misbehave;

    private volatile boolean done = false;

    @Inject
    Logger logger;

    @Inject
    public TemperatureSensor(
            ScheduledExecutorService executor,
            @ConfigProperty(name = "device.id") String id,
            @ConfigProperty(name = "temperature.base") double base,
            @ConfigProperty(name = "temperature.delta") double delta,
            @ConfigProperty(name = "device.period") Duration period,
            @ConfigProperty(name = "device.misbehavior") boolean misbehavior,
            @Channel("temperatures") Emitter<Temperature> emitter
    ) {
        this.executor = executor;
        this.id = id;
        this.base = base;
        this.delta = delta;
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

        Temperature temp;
        if (correct) {
            // Compute the new value
            // Variation around base in [base-delta, base+delta]
            var factor = random.nextBoolean() ? 1 : -1;
            var variation = random.nextDouble(0, delta);
            var nv = Math.round(((base + factor * variation) * 100)) / 100.00;
            temp = new Temperature(id, nv);
        } else {
            // Send a completely random temperature
            var nv = Math.round((random.nextDouble(-1000, 1000)) / 100) / 100.00;
            temp = new Temperature(id, nv);
        }

        if (!done) {
            var v = temp.value;
            emitter.send(temp)
                    .thenAccept(x -> logger.infof("Device '%s' sent temperature '%,.2f'", id, v));
            this.executor.schedule(this, period.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

}
