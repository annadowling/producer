package com.msc.spring.producer.java.client;

import com.msc.spring.producer.message.Message;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Created by annadowling on 2020-01-16.
 */

@Component
@ConditionalOnProperty(prefix = "rabbitmq.java.client", name = "enabled", havingValue = "true")
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

    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    @Value("${message.volume}")
    private int messageVolume;

    @Value("${rabbitmq.java.client.enabled}")
    private boolean rabbitJavaClientEnabled;

    Channel channel;

    final String errorMessage = "Exception encountered = ";

    @Bean
    public void setUpRMQProducerAndSendMessage() {
        if (rabbitJavaClientEnabled) {
            Channel channel = createChannelConnection();
            try {
                Message message = new Message();

                int i = 0;
                while (i < messageVolume) {
                    String messageConversion = message.toString();
                    System.out.println("Sending Message = " + messageConversion);
                    channel.basicPublish(exchangeName, routingKey, null, messageConversion.getBytes(StandardCharsets.UTF_8));
                    i++;
                }
            } catch (Exception e) {
                System.out.println(errorMessage + e.getLocalizedMessage());
            } finally {
                try {
                    channel.close();
                } catch (Exception e) {
                    System.out.println(errorMessage + e.getLocalizedMessage());

                }
            }
        }
    }


    /**
     * Channel to RabbitMQ server used for declaring architecture(queues, exchanges, bindings)
     * and publishing messages.
     *
     * @return Channel
     */

    Channel createChannelConnection() {
        ConnectionFactory factory = createConnection();
        try {
            Connection connection = factory.newConnection();

            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            channel.queueBind(queueName, exchangeName, routingKey);

        } catch (Exception e) {
            System.out.println(errorMessage + e.getLocalizedMessage());
        }

        return channel;
    }

    /**
     * Creates connection to RabbitMQ server using specific env variables
     *
     * @return ConnectionFactory
     */
    ConnectionFactory createConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setHost(host);
            factory.setPassword(rabbitPassWord);
            factory.setUsername(rabbitUserName);
            factory.setPort(port);
            factory.setVirtualHost(virtualHost);
        } catch (Exception e) {
            System.out.println(errorMessage + e.getLocalizedMessage());
        }
        return factory;
    }

}
