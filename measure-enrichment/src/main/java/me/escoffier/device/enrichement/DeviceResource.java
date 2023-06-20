package me.escoffier.device.enrichement;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import me.escoffier.device.Device;

import java.util.List;
import java.util.stream.Collectors;

@Path("/devices")
public class DeviceResource {

    @GET
    public List<Device> getDevices() {
        return DeviceEntity.<DeviceEntity>streamAll().map(DeviceEntity::toDevice).collect(Collectors.toList());
    }

}
