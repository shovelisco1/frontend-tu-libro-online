package com.evaluacion_1.ev_1.service;

import com.evaluacion_1.ev_1.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LibroEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(LibroEventConsumer.class);

    private static final int MAX_INTENTOS = 3;

    @RabbitListener(
            queues = RabbitMQConfig.COLA_LIBROS,
            ackMode = "MANUAL"
    )
    public void recibir(
            String mensaje,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {

        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {

            try {
                if (mensaje == null || mensaje.isBlank()) {
                    throw new IllegalArgumentException("Mensaje vacío");
                }

                log.info("Evento recibido y procesado: {}", mensaje);

            } catch (IllegalArgumentException e) {
                log.warn(
                        "Error en intento {}/{}: {}",
                        intento,
                        MAX_INTENTOS,
                        e.getMessage()
                );

                if (intento == MAX_INTENTOS) {
                    log.error("Se agotaron los intentos. Enviando a DLQ.");

                    channel.basicNack(deliveryTag, false, false);
                    return;
                }

                try {
                    Thread.sleep(1000L * intento);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();

                    log.error("Reintentos interrumpidos", ex);
                    channel.basicNack(deliveryTag, false, false);
                    return;
                }

                continue;
            }

            channel.basicAck(deliveryTag, false);
            return;
        }
    }
}