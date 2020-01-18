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
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by annadowling on 2020-01-15.
 */

@Component
public class SpringAMQPPublisher {
    @Autowired
    private AmqpTemplate rabbitAMQPTemplate;

    @Value("${rabbitmq.exchangeName}")
    private String exchangeName;

    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    @Value("${message.volume}")
    private int messageVolume;

    @Value("${spring.amqp.enabled}")
    private boolean springAMQPEnabled;

    public void sendMessage() {
        if (springAMQPEnabled) {
            int i = 0;

            Message message = new Message();
            while (i < messageVolume) {
                System.out.println("Sending Message = " + message.toString());
                rabbitAMQPTemplate.convertAndSend(exchangeName, routingKey, message);
                i++;
            }
        }
    }
}
