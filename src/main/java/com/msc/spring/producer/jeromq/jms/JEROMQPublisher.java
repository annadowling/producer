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

    final String errorMessage = "Exception encountered = ";

    @Bean
    public void setUpJMQProducerAndSendMessage() {
        if (jeroMQEnabled) {
            try (ZMQ.Context context = ZMQ.context(1);
                 ZMQ.Socket publisher = context.socket(ZMQ.PUB);
            ) {
                publisher.bind(bindAddress);
                System.out.println("Starting Publisher..");
                publisher.setIdentity("B".getBytes());
                // for testing setting sleep at 100ms to ensure started.
                Thread.sleep(100l);
                for (int i = 1; i <= 10; i++) {
                    publisher.sendMore("B");
                    boolean isSent = publisher.send("X(" + System.currentTimeMillis() + "):" + i);
                    System.out.println("Message was sent " + i + " , " + isSent);
                }
                publisher.close();
                context.term();
            } catch (Exception e) {
                System.out.println(errorMessage + e.getLocalizedMessage());

            }
        }
    }
}
