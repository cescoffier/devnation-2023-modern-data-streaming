package me.escoffier.device;

public class TemperatureWithLocation {

    public final String deviceId;
    public final double value;
    public final long timestamp;
    public final String location;

    public TemperatureWithLocation(Temperature temperature, String location) {
        this.deviceId = temperature.deviceId;
        this.value = temperature.value;
        this.timestamp = temperature.timestamp;
        this.location = location;
    }
}
