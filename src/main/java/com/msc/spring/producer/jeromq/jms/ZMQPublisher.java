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
import org.zeromq.ZMQ;

/**
 * Created by annadowling on 2020-01-16.
 */

public class ZMQPublisher {

    @Value("${zeromq.address}")
    private static String bindAddress;

    public static void main(String[] args) throws Exception {
        ZMQ.Context ctx = ZMQ.context(1);

        ZMQ.Socket pub = ctx.socket(ZMQ.PUB);

        pub.bind(bindAddress);

        Message message = new Message("Request", "TEST Message");
        sendMessage(message, pub);

        pub.close();
        ctx.close();
    }

    public static void sendMessage(Message msg,  ZMQ.Socket publisher){
        System.out.println("Sending Message = " + msg.toString());
        publisher.send(msg.toString());
    }
}
