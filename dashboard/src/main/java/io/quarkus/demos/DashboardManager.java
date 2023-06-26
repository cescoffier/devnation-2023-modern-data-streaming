package io.quarkus.demos;

import io.quarkus.runtime.configuration.ConfigUtils;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import me.escoffier.device.Device;
import me.escoffier.device.RabbitAlert;
import me.escoffier.device.TemperatureAlert;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.groupingBy;


@Path("/dashboard")
@Singleton
public class DashboardManager {

    @Inject
    RabbitAlertReceiver rabbitAlertReceiver;

    @Inject
    TemperatureAlertReceiver temperatureAlertReceiver;

    @Inject
    MockDeviceTool mockDeviceTool;


    @RestClient
    MonitoringClient monitoringClient;

    @RestClient
    DeviceClient deviceClient;


    private static final AtomicLong messageIndex = new AtomicLong(0);
    private static final AtomicLong imagedProcessCount = new AtomicLong(0);
    private static Random rand = new Random();


//    @GET
//    @Path("/stats")
//    @RestStreamElementType(MediaType.APPLICATION_JSON)
//    public Multi<Counters> streamStats() {
//        return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map( l -> monitoringClient.get());
//    }

    @GET
    @Path("/stats/average-image-processed")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Long> streamImageStats() {
        if(ConfigUtils.isProfileActive("dev")) {
            return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map( l -> Math.round(getRandomAverage(0,40)));
        } else {
            return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map(l -> monitoringClient.get().numberOfPredictions());
        }
    }

    @GET
    @Path("/stats/average-temperature-enrichment")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Long> streamTemperatureStats() {
        if(ConfigUtils.isProfileActive("dev")) {
            return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map( l -> Math.round(getRandomAverage(20,500)));
        } else {
            return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map(l -> monitoringClient.get().numberOfEnrichedTemperatures());
        }
    }



//    @GET
//    @Path("/alarms")
//    @RestStreamElementType(MediaType.APPLICATION_JSON)
//    public Multi<List<Alarm>> streamRandomAlerts() {
//        return Multi.createFrom().ticks().every(Duration.ofSeconds(10)).map(DashboardManager::randomListOfAlarms);
//    }


    @GET
    @Path("/rabbit-alarms")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<List<RabbitAlert>> streamRabbitAlerts() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(10)).map(l-> {
            return rabbitAlertReceiver.getAlerts();
        });
    }


    @GET
    @Path("/temperature-alarms")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<List<TemperatureAlert>> streamTempAlerts() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(10)).map(l-> {
            return temperatureAlertReceiver.getAlerts();
        });
    }


    @GET
    @Path("/devices")
    public Map<String,List<Device>> devices() {
        if(!ConfigUtils.isProfileActive("dev")) {
            return deviceClient.get().stream().collect(groupingBy(Device::location));
        } else {
            return mockDeviceTool.mockDeviceList().stream().collect(groupingBy(Device::location));
        }
    }


    private static double getRandomAverage(int minValue,int maxValue) {
        return minValue + rand.nextDouble()*(maxValue-minValue);
    }




}
