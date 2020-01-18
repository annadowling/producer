package com.msc.spring.producer.spring.amqp;/***************************************************************
 * Copyright (c) 2020 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import com.msc.spring.producer.message.Message;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by annadowling on 2020-01-15.
 */

@Component
public class SpringAMQPPublisher {

    @Autowired
    Message message;

    @Value("${rabbitmq.exchangeName}")
    private String exchangeName;

    @Value("${rabbitmq.queueName}")
    private String queueName;

    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    @Value("${message.volume}")
    private int messageVolume;

    @Value("${spring.amqp.enabled}")
    private boolean springAMQPEnabled;

    @Bean
    public void sendMessage() {
        if (springAMQPEnabled) {
            ConnectionFactory connectionFactory = new CachingConnectionFactory();
            AmqpAdmin admin = new RabbitAdmin(connectionFactory);
            admin.declareQueue(new Queue(queueName));
            AmqpTemplate template = new RabbitTemplate(connectionFactory);

            int i = 0;
            while (i < messageVolume) {
                System.out.println("Sending Message = " + message.toString());
                template.convertAndSend(exchangeName, routingKey, message);
                i++;
            }
        }
    }
}
