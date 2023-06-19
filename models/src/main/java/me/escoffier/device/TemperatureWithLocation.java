package me.escoffier.device;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class TemperatureWithLocation {

    public final String deviceId;
    public final double value;
    public final ZonedDateTime timestamp;
    public final String location;

    public TemperatureWithLocation() {
        this.deviceId = null;
        this.value = Double.MIN_VALUE;
        this.timestamp = null;
        this.location = null;
    }

    public TemperatureWithLocation(Temperature temperature, String location) {
        this.deviceId = temperature.deviceId;
        this.value = temperature.value;
        this.timestamp = temperature.timestamp;
        this.location = location;
    }
}
