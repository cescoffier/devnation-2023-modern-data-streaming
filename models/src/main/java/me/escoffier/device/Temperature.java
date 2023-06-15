package me.escoffier.device;

public class Temperature {

    public final String deviceId;
    public final double value;
    public final long timestamp;

    public Temperature(String deviceId, double value) {
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }
}
