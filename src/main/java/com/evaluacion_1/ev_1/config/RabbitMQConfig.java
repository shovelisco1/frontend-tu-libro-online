package com.evaluacion_1.ev_1.config;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String EXCHANGE_LIBROS = "biblioteca.exchange";
    public static final String COLA_LIBROS = "libros.eventos";
    public static final String CLAVE_LIBROS = "libro.evento";

    public static final String EXCHANGE_DLQ = "biblioteca.dlx";
    public static final String COLA_DLQ = "libros.eventos.dlq";
    public static final String CLAVE_DLQ = "libro.evento.fallido";

    @Bean
    public DirectExchange bibliotecaExchange() {
        return new DirectExchange(EXCHANGE_LIBROS);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DLQ);
    }

    @Bean
    public Queue colaLibros() {
        return QueueBuilder.durable(COLA_LIBROS)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLQ)
                .withArgument("x-dead-letter-routing-key", CLAVE_DLQ)
                .build();
    }

    @Bean
    public Queue colaMensajesFallidos() {
        return QueueBuilder.durable(COLA_DLQ).build();
    }

    @Bean
    public Binding bindingLibros() {
        return BindingBuilder
                .bind(colaLibros())
                .to(bibliotecaExchange())
                .with(CLAVE_LIBROS);
    }

    @Bean
    public Binding bindingDeadLetter() {
        return BindingBuilder
                .bind(colaMensajesFallidos())
                .to(deadLetterExchange())
                .with(CLAVE_DLQ);
    }
}