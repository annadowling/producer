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
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by annadowling on 2020-01-16.
 */

@Component
public class Message implements Serializable {

    @Value("${message.exchangeName}")
    private String messageType;

    @Value("${rabbitmq.exchangeName}")
    private String messageText;
}