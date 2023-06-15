package me.escoffier.devce.enrichement;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeviceFixture {


    @Transactional
    public void init(@Observes StartupEvent ignored) {
        System.out.println("Device fixture running...");

        if (DeviceLocation.count() == 0) {
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
        }

    }


    DeviceLocation create(String id, String location) {
        DeviceLocation dl = new DeviceLocation();
        dl.device = id;
        dl.location = location;
        return dl;
    }

}
