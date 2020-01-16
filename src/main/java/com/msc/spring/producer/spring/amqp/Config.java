package com.msc.spring.producer.spring.amqp;/***************************************************************
 * Copyright (c) 2020 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

/**
 * Created by annadowling on 2020-01-15.
 */

@Configuration
public class Config {
    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Bean
    Queue queue() {
        return new Queue(rabbitMQProperties.getQueueName(), false);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(rabbitMQProperties.getExchangeName());
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(rabbitMQProperties.getExchangeName());
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitMQProperties.getRoutingKey());
    }

//    @Bean
//    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
//                                             MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(rabbitMQProperties.getQueueName());
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean
//    MessageListenerAdapter listenerAdapter(RabbitMqListener listener) {
//        return new MessageListenerAdapter(listener, "listen");
//    }
}
