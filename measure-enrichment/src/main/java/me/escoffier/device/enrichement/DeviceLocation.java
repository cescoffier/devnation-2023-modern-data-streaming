package me.escoffier.device.enrichement;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class DeviceLocation extends PanacheEntity {

    public String device;
    public String location;

    public static String findLocationForDevice(String id) {
        DeviceLocation match = find("device", id).firstResult();
        if (match == null) {
            return null;
        } else {
            return match.location;
        }
    }
}
