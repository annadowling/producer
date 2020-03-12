package com.msc.spring.producer.java.client;

import com.msc.spring.producer.message.MessageUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    @Value("${rabbitmq.durable}")
    private boolean durableQueue;

    @Value("${rabbitmq.virtualhost}")
    private String virtualHost;

    @Value("${rabbitmq.exchangeName}")
    private String exchangeName;

    @Value("${rabbitmq.routingKey}")
    private String routingKey;

    @Value("${rabbitmq.java.client.enabled}")
    private boolean rabbitJavaClientEnabled;

    @Value("${multi.thread.enabled}")
    private boolean multiThreaded;

    Channel channel;

    final String errorMessage = "Exception encountered = ";

    @Autowired
    private MessageUtils messageUtils;

    @Bean
    public void setUpRMQProducerAndSendMessage() {
        if (rabbitJavaClientEnabled) {
            Channel channel = createChannelConnection();
            try {

                int i = 0;
                while (i < messageUtils.messageVolume) {
                    String messageText = messageUtils.generateMessage();
                    Map<String, String> messageMap = messageUtils.formatMessage(messageText, "RABBITMQ JAVA CLIENT");
                    messageUtils.saveMessage(messageMap);
                    byte[] mapBytes = messageUtils.convertMapToBytes(messageMap);

                    System.out.println("Sending RABBITMQ Client Message " + i);
                    if (multiThreaded) {
                        sendMessageMultiThread(channel, exchangeName, routingKey, mapBytes);
                    } else {
                        channel.basicPublish(exchangeName, routingKey, null, mapBytes);
                    }
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

    @Async
    void sendMessageMultiThread(Channel channel, String exchangeName, String routingKey, byte[] mapBytes) throws Exception {
        channel.basicPublish(exchangeName, routingKey, null, mapBytes);
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
            channel = connection.createChannel();

            channel.queueDeclare(queueName, durableQueue, false, false, null);
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
