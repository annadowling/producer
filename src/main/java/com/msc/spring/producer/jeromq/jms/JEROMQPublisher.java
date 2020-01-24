package com.msc.spring.producer.jeromq.jms;/***************************************************************
 * Copyright (c) 2020 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import com.msc.spring.producer.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

/**
 * Created by annadowling on 2020-01-16.
 */

@Component
@ConditionalOnProperty(prefix = "jeromq", name = "enabled", havingValue = "true")
public class JEROMQPublisher {

    @Value("${zeromq.address}")
    private String bindAddress;

    @Value("${message.volume}")
    private int messageVolume;

    @Value("${jeromq.enabled}")
    private boolean jeroMQEnabled;


    @Bean
    public void setUpJMQProducerAndSendMessage() {
        if (jeroMQEnabled) {
            ZMQ.Context ctx = ZMQ.context(1);

            ZMQ.Socket publisher = ctx.socket(ZMQ.PUB);
            publisher.bind(bindAddress);

            Message message = new Message();

            int i = 0;
            while (i < messageVolume) {
                System.out.println("Sending Message = " + message.toString());
                publisher.send(message.toString());
                i++;
            }
            publisher.close();
            ctx.close();
        }
    }
}
