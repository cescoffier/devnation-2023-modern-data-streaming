package me.escoffier.device.enrichement;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import me.escoffier.device.Device;

@Entity
@Table(name = "devices")
public class DeviceEntity extends PanacheEntity {

    public String deviceId;
    public Device.Kind kind;
    public String location;


    public static String findLocationForDevice(String id) {
        DeviceEntity match = find("deviceId", id).firstResult();
        if (match == null) {
            return null;
        } else {
            return match.location;
        }
    }

    public Device toDevice() {
        return new Device(kind, deviceId, location);
    }
}
