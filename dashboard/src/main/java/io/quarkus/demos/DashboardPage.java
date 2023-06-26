package io.quarkus.demos;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import me.escoffier.device.Device;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Path("/")
public class DashboardPage {

    @Inject
    Template index;

    @Inject
    MockDeviceTool mockDeviceTool;

    @RestClient
    DeviceClient deviceClient;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance get() {
        if(!ConfigUtils.isProfileActive("dev")) {
            Map<String, List<Device>> deviceLocationMap = deviceClient.get().stream().collect(groupingBy(Device::location));
            return index.data("deviceLocationMap", deviceLocationMap);
        } else {
            Map<String, List<Device>> deviceLocationMap = mockDeviceTool.mockDeviceList().stream().collect(groupingBy(Device::location));
            return index.data("deviceLocationMap", deviceLocationMap);
        }
    }

}
