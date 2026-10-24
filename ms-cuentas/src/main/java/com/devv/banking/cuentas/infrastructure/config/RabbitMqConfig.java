package com.devv.banking.cuentas.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String CLIENTES_EXCHANGE = "banking.clientes.exchange";
    public static final String CLIENTES_ROUTING_PATTERN = "clientes.event.#";
    public static final String CLIENTES_SYNC_QUEUE = "cuentas.clientes.sync.queue";

    public static final String CLIENTES_DLX = "banking.clientes.dlx";
    public static final String CLIENTES_DLQ = "cuentas.clientes.sync.dlq";
    public static final String CLIENTES_DLQ_ROUTING_KEY = "clientes.event.dead";

    @Bean
    public TopicExchange clientesExchange() {
        return new TopicExchange(CLIENTES_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange clientesDeadLetterExchange() {
        return new DirectExchange(CLIENTES_DLX, true, false);
    }

    @Bean
    public Queue clientesSyncQueue() {
        return QueueBuilder.durable(CLIENTES_SYNC_QUEUE)
                .withArgument("x-dead-letter-exchange", CLIENTES_DLX)
                .withArgument("x-dead-letter-routing-key", CLIENTES_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue clientesDeadLetterQueue() {
        return QueueBuilder.durable(CLIENTES_DLQ).build();
    }

    @Bean
    public Binding clientesSyncBinding(Queue clientesSyncQueue, TopicExchange clientesExchange) {
        return BindingBuilder.bind(clientesSyncQueue)
                .to(clientesExchange)
                .with(CLIENTES_ROUTING_PATTERN);
    }

    @Bean
    public Binding clientesDlqBinding(Queue clientesDeadLetterQueue, DirectExchange clientesDeadLetterExchange) {
        return BindingBuilder.bind(clientesDeadLetterQueue)
                .to(clientesDeadLetterExchange)
                .with(CLIENTES_DLQ_ROUTING_KEY);
    }

    /**
     * Configura inferencia de tipos JSON sin acoplar paquetes Java entre ms-clientes y ms-cuentas.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
