package me.escoffier.device;

public record Device(Device.Kind kind, String id, String location) {
    public enum Kind {
        CAMERA,
        THERMOMETER
    }
}
