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
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Created by annadowling on 2020-01-16.
 */

@Component
public class RabbitMQPublisher {

    @Value("${rabbitmq.queueName}")
    private String queueName;

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private Integer port;

    @Value("${rabbitmq.username}")
    private String rabbitUserName;

    @Value("${rabbitmq.password}")
    private String rabbitPassWord;

    @Value("${rabbitmq.virtualhost}")
    private String virtualHost;

    @Value("${rabbitmq.exchangeName}")
    private String exchangeName;

    @Value("${message.volume}")
    private int messageVolume;

    @Value("${rabbitmq.java.client.enabled}")
    private boolean rabbitJavaClientEnabled;

    @Bean
    public void setUpClientAndSendMessage() throws Exception {
        if (rabbitJavaClientEnabled) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPassword(rabbitPassWord);
            factory.setUsername(rabbitUserName);
            factory.setPort(port);
            factory.setVirtualHost(virtualHost);

            Message message = new Message();

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.queueDeclare(queueName, true, false, false, null);

                int i = 0;
                while (i < messageVolume) {
                    String messageConversion = message.toString();
                    System.out.println("Sending Message = " + messageConversion);
                    channel.basicPublish(exchangeName, queueName, null, messageConversion.getBytes(StandardCharsets.UTF_8));
                    i++;
                }
            }
        }
    }
}
