package com.msc.spring.producer.spring.amqp;/***************************************************************
 * Copyright (c) 2020 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import java.io.Serializable;

/**
 * Created by annadowling on 2020-01-16.
 */

public class Message implements Serializable {

    private String notificationType;
    private String msg;


    public Message(String notificationType, String msg) {
        this.notificationType = notificationType;
        this.msg = msg;
    }
}