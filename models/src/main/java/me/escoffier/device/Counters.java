package me.escoffier.device;

public record Counters(long numberOfEnrichedTemperatures, long numberOfPredictions, long numberOfTemperatureAlerts,
                       long numberOfRabbitAlerts) {
}
