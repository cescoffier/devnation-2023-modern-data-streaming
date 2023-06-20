package me.escoffier.device.enrichement;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import me.escoffier.device.Device;

@ApplicationScoped
public class DeviceFixture {


    @Transactional
    public void init(@Observes StartupEvent ignored) {
        System.out.println("Initializing...");
        create(Device.Kind.THERMOMETER, "t-0001", "Paris").persist();
        create(Device.Kind.THERMOMETER, "t-0002", "Paris").persist();
        create(Device.Kind.THERMOMETER, "t-0003", "Berlin").persist();
        create(Device.Kind.THERMOMETER, "t-0004", "Stockholm").persist();
        create(Device.Kind.THERMOMETER, "t-0005", "Berlin").persist();
        create(Device.Kind.THERMOMETER, "t-0006", "Berlin").persist();
        create(Device.Kind.THERMOMETER, "t-0007", "Stockholm").persist();
        create(Device.Kind.THERMOMETER, "t-0008", "Stockholm").persist();
        create(Device.Kind.THERMOMETER, "t-0009", "Paris").persist();
        create(Device.Kind.THERMOMETER, "t-0010", "Stockholm").persist();

        create(Device.Kind.CAMERA, "c-0001", "Stockholm").persist();
        create(Device.Kind.CAMERA, "c-0002", "Paris").persist();
        create(Device.Kind.CAMERA, "c-0003", "Stockholm").persist();
        create(Device.Kind.CAMERA, "c-0004", "Stockholm").persist();
        create(Device.Kind.CAMERA, "c-0005", "Paris").persist();
        create(Device.Kind.CAMERA, "c-0006", "Stockholm").persist();
        create(Device.Kind.CAMERA, "c-0007", "Paris").persist();
        create(Device.Kind.CAMERA, "c-0008", "Berlin").persist();
    }


    DeviceEntity create(Device.Kind kind, String id, String location) {
        DeviceEntity dl = new DeviceEntity();
        dl.kind = kind;
        dl.deviceId = id;
        dl.location = location;
        return dl;
    }

}
