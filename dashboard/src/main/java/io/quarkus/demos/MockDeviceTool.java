package io.quarkus.demos;

import jakarta.enterprise.context.ApplicationScoped;
import me.escoffier.device.Device;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MockDeviceTool {

    public List<Device> mockDeviceList() {
        List<Device> deviceList = new ArrayList<>();
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0001", "Paris"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0002", "Paris"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0003", "Berlin"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0004", "Stockholm"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0005", "Berlin"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0006", "Berlin"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0007", "Stockholm"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0008", "Stockholm"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0009", "Paris"));
        deviceList.add(create(Device.Kind.THERMOMETER, "t-0010", "Stockholm"));

        deviceList.add(create(Device.Kind.CAMERA, "c-0001", "Stockholm"));
        deviceList.add(create(Device.Kind.CAMERA, "c-0002", "Paris"));
        deviceList.add(create(Device.Kind.CAMERA, "c-0003", "Stockholm"));
        deviceList.add(create(Device.Kind.CAMERA, "c-0004", "Stockholm"));
        deviceList.add(create(Device.Kind.CAMERA, "c-0005", "Paris"));
        deviceList.add(create(Device.Kind.CAMERA, "c-0006", "Stockholm"));
        deviceList.add(create(Device.Kind.CAMERA, "c-0007", "Paris"));
        deviceList.add(create(Device.Kind.CAMERA, "c-0008", "Berlin"));
        return deviceList;
    }

    private Device create(Device.Kind kind,String id, String location) {
        return new Device(kind,id,location);
    }
}
