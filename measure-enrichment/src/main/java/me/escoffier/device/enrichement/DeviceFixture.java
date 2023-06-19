package me.escoffier.device.enrichement;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeviceFixture {


    @Transactional
    public void init(@Observes StartupEvent ignored) {
        System.out.println("Device fixture running...");

//        if (DeviceLocation.count() == 0) {
            System.out.println("Initializing...");
            create("t-0001", "Paris").persist();
            create("t-0002", "Paris").persist();
            create("t-0003", "Berlin").persist();
            create("t-0004", "Stockholm").persist();
            create("t-0005", "Berlin").persist();
            create("t-0006", "Berlin").persist();
            create("t-0007", "Stockholm").persist();
            create("t-0008", "Stockholm").persist();
            create("t-0009", "Paris").persist();
            create("t-0010", "Stockholm").persist();

            create("c-0001", "Stockholm").persist();
            create("c-0002", "Paris").persist();
            create("c-0003", "Stockholm").persist();
            create("c-0004", "Stockholm").persist();
            create("c-0005", "Paris").persist();
            create("c-0006", "Stockholm").persist();
            create("c-0007", "Paris").persist();
            create("c-0008", "Berlin").persist();

//        }

    }


    DeviceLocation create(String id, String location) {
        DeviceLocation dl = new DeviceLocation();
        dl.device = id;
        dl.location = location;
        return dl;
    }

}
