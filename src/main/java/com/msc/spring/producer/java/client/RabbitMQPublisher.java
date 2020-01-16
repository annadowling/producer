package com.msc.spring.producer.java.client;/***************************************************************
 * Copyright (c) 2020 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import com.msc.spring.producer.message.Message;
import com.msc.spring.producer.spring.amqp.RabbitMQProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

/**
 * Created by annadowling on 2020-01-16.
 */

public class RabbitMQPublisher {

    @Autowired
    private static RabbitMQProperties rabbitMQProperties;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQProperties.getHost());

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(rabbitMQProperties.getQueueName(), true, false, false, null);

            Message message = new Message("Request", "TEST Message");
            sendMessage(message, channel);
        }
    }

    public static void sendMessage(Message message, Channel channel) throws Exception{
        String messageConversion = message.toString();
        System.out.println("Sending Message = " + messageConversion);
        channel.basicPublish(rabbitMQProperties.getExchangeName(), rabbitMQProperties.getQueueName(), null, messageConversion.getBytes(StandardCharsets.UTF_8));


    }
}
