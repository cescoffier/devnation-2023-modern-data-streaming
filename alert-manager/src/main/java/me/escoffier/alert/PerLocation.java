package me.escoffier.alert;

import io.smallrye.reactive.messaging.keyed.KeyValueExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import me.escoffier.device.SnapshotWithLocation;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.lang.reflect.Type;

@ApplicationScoped
public class PerLocation implements KeyValueExtractor {
    @Override
    public boolean canExtract(Message<?> message, Type type, Type type1) {
        return true;
    }

    @Override
    public Object extractKey(Message<?> message, Type type) {
        return ((SnapshotWithLocation) message.getPayload()).location;
    }

    @Override
    public Object extractValue(Message<?> message, Type type) {
        return message.getPayload();
    }
}
