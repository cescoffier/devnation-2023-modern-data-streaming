package io.quarkus.demos;

import io.quarkus.qute.TemplateExtension;
import me.escoffier.device.RabbitAlert;

@TemplateExtension
public class ImageBase64TemplateExtension {

    public static String imageAsBase64(RabbitAlert alert) {
        return new String(alert.snapshot());
    }
}
