package io.quarkus.demos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;


@Path("/dashboard")
@Singleton
public class SSEMessageSimulator {


    private static final AtomicLong messageIndex = new AtomicLong(0);
    private static Random rand = new Random();

    
    @GET
    @Path("/message-stats")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<MessageStats> streamMessageStats() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1)).map(SSEMessageSimulator::randomEvent);
    }

    public static MessageStats randomEvent(Long id) {
        MessageStats stat = new MessageStats();
        stat.setId(id);
        stat.setTime(LocalDateTime.now());
        stat.setAvaragePerSecond(getRandomAverage());
        stat.setIndex(messageIndex.incrementAndGet());

        return stat;
    }

    private static double getRandomAverage() {
        double minValue = 20.0;
        double maxValue = 500.0;
        return minValue + rand.nextDouble()*(maxValue-minValue);
    }


    
}
