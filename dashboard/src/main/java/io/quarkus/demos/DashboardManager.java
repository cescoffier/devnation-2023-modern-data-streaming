package io.quarkus.demos;

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
    @Path("/message-stats")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<MessageStats> streamMessageStats() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map(DashboardManager::randomMessageEvent);
    }


    @GET
    @Path("/image-processed-stats")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<ImageProcessedStats> streamImageStats() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map(DashboardManager::randomImageProcessedStats);
    }

    @GET
    @Path("/stats")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Counters> streamStats() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(2)).map( l -> monitoringClient.get());
    }


    @GET
    @Path("/alarms")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<List<Alarm>> streamRandomAlerts() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(10)).map(DashboardManager::randomListOfAlarms);
    }


    @GET
    @Path("/rabbits")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<List<RabbitAlert>> streamRabbitAlerts() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(10)).map(l-> {
            return rabbitAlertReceiver.getAlerts();
        });
    }

    @GET
    @Path("/temperatures")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<List<TemperatureAlert>> streamTempAlerts() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(10)).map(l-> {
            return temperatureAlertReceiver.getAlerts();
        });
    }

    @GET
    @Path("/devices")
    public Map<String,List<Device>> devices() {
        return deviceClient.get().stream().collect(groupingBy(Device::location));
    }




    public static MessageStats randomMessageEvent(Long id) {
        MessageStats stat = new MessageStats();
        stat.setId(id);
        stat.setTime(LocalDateTime.now());
        stat.setAvaragePerSecond(getRandomAverage(20,500));
        stat.setIndex(messageIndex.incrementAndGet());

        return stat;
    }

    public static ImageProcessedStats randomImageProcessedStats(Long id) {
        ImageProcessedStats stat = new ImageProcessedStats();
        stat.setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        stat.setTotalProcessed(imagedProcessCount.incrementAndGet());
        stat.setAverageProcessed(getRandomAverage(5, 30));
        return stat;
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







    
}
