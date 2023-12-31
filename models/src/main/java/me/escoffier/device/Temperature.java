package me.escoffier.device;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Temperature {

    public final String deviceId;
    public final double value;
    public final ZonedDateTime timestamp;

    public Temperature() {
        deviceId = null;
        value = Double.MIN_VALUE;
        timestamp = null;
    }

    public Temperature(String deviceId, double value) {
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = LocalDateTime.now().atZone(ZoneId.of("UTC"));
    }
}
