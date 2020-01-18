package com.msc.spring.producer.message;/***************************************************************
 * Copyright (c) 2020 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * Created by annadowling on 2020-01-16.
 */

public class Message implements Serializable {

    @Value("${message.exchangeName}")
    private static String messageType;

    @Value("${rabbitmq.exchangeName}")
    private static String messageText;
}