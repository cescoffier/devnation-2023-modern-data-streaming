package io.quarkus.demos;

import io.quarkus.runtime.configuration.ConfigUtils;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import me.escoffier.device.Counters;
import me.escoffier.device.Device;
import me.escoffier.device.RabbitAlert;
import me.escoffier.device.TemperatureAlert;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestStreamElementType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    @RestClient
    MonitoringClient monitoringClient;

    @RestClient
    DeviceClient deviceClient;


    private static final AtomicLong messageIndex = new AtomicLong(0);
    private static final AtomicLong imagedProcessCount = new AtomicLong(0);
    private static Random rand = new Random();


    @GET
    @Path("/stats")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Counters> streamStats() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map( l -> monitoringClient.get());
    }

    @GET
    @Path("/stats/average-image-processed")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Long> streamImageStats() {
        if(ConfigUtils.isProfileActive("dev")) {
            return Multi.createFrom().ticks().every(Duration.ofSeconds(30)).map( l -> Math.round(getRandomAverage(0,40)));
        } else {
            return Multi.createFrom().ticks().every(Duration.ofSeconds(30)).map(l -> monitoringClient.get().numberOfPredictions());
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
            return mockDeviceList().stream().collect(groupingBy(Device::location));
        }
    }


    private static double getRandomAverage(int minValue,int maxValue) {
        return minValue + rand.nextDouble()*(maxValue-minValue);
    }

    private static AlarmType getRandomAlarmType() {
        int randAlarmType = rand.nextInt(3);
        switch (randAlarmType) {
            case 0 : return AlarmType.INFORMATION;
            case 1 : return AlarmType.WARNING;
            case 2 : return AlarmType.CRITICAL;
            default : return AlarmType.INFORMATION;
        }
    }


    public static List<Alarm> randomListOfAlarms(Long id) {
        
        var alarmList = new ArrayList<Alarm>();

        int alarms = rand.nextInt(5); //Generate a maximum of 5 alarms.

        for(int i=0; i<alarms; i++) {
            var alarm = new Alarm();
            alarm.setType(getRandomAlarmType());
            alarm.setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            alarm.setMachineId("Unknown");
            alarmList.add(alarm);
        }

        return alarmList;

    }

    private List<Device> mockDeviceList() {
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
