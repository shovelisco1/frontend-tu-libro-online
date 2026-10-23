package com.evaluacion_1.ev_1.service;

import com.evaluacion_1.ev_1.config.RabbitMQConfig;
import com.evaluacion_1.ev_1.model.Libro;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class LibroEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public LibroEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarPrestamo(Libro libro) {
        String mensaje = "PRESTAMO_CONFIRMADO"
                + "|id=" + libro.getId()
                + "|titulo=" + libro.getTitulo();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_LIBROS,
                RabbitMQConfig.CLAVE_LIBROS,
                mensaje
        );
    }
}