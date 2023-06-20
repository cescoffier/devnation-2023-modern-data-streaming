package me.escoffier.device.enrichement;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import me.escoffier.device.Device;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class DeviceResourceTest {

    @Test
    void test() {
        List<Device> list = RestAssured.get("/devices")
                .as(new TypeRef<List<Device>>() {
                });

        assertThat(list).allSatisfy(d -> {
            assertThat(d.id()).isNotBlank();
            assertThat(d.location()).isNotBlank();
            if (d.id().startsWith("t")) {
                assertThat(d.kind()).isEqualTo(Device.Kind.THERMOMETER);
            }
            if (d.id().startsWith("c")) {
                assertThat(d.kind()).isEqualTo(Device.Kind.CAMERA);
            }
        });
    }

}