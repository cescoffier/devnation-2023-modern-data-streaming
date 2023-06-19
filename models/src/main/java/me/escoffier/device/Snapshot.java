package me.escoffier.device;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Snapshot {

    public final String deviceId;
    public final ZonedDateTime timestamp;
    public final byte[] picture;

    public Snapshot() {
        deviceId = null;
        timestamp = null;
        picture = null;
    }

    public Snapshot(String deviceId, byte[] picture) {
        this.deviceId = deviceId;
        this.timestamp = LocalDateTime.now().atZone(ZoneId.of("UTC"));
        this.picture = picture;
    }
}
