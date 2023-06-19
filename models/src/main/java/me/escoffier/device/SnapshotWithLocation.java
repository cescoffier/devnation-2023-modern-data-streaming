package me.escoffier.device;

import java.time.ZonedDateTime;

public class SnapshotWithLocation {

    public final String deviceId;
    public final byte[] picture;
    public final ZonedDateTime timestamp;
    public final String location;

    public SnapshotWithLocation() {
        this.deviceId = null;
        this.picture = null;
        this.timestamp = null;
        this.location = null;
    }

    public SnapshotWithLocation(Snapshot snapshot, String location) {
        this.deviceId = snapshot.deviceId;
        this.picture = snapshot.picture;
        this.timestamp = snapshot.timestamp;
        this.location = location;
    }
}
