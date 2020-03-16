package com.msc.spring.producer.spring.amqp;

import com.msc.spring.producer.java.client.RabbitMQPublisher;
import com.msc.spring.producer.message.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by annadowling on 2020-01-15.
 */

@Component
@ConditionalOnProperty(prefix = "spring.amqp", name = "enabled", havingValue = "true")
public class SpringAMQPPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAMQPPublisher.class);

    @Value("${rabbitmq.exchangeName}")
    private String exchangeName;

    @Value("${rabbitmq.queueName}")
    private String queueName;

    @Value("${rabbitmq.routingKey}")
    private String routingKey;

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

    @Value("${spring.amqp.enabled}")
    private boolean springAMQPEnabled;

    @Value("${multi.thread.enabled}")
    private boolean multiThreaded;

    @Autowired
    private MessageUtils messageUtils;


    @Bean
    public void setUpSpringProducerAndSendMessage() {
        if (springAMQPEnabled) {
            ConnectionFactory connectionFactory = returnConnection();
            RabbitTemplate template = new RabbitTemplate(connectionFactory);

            String messageText = messageUtils.generateMessage();

            int i = 1;
            while (i <= messageUtils.messageVolume) {
                Map<String, String> messageMap = messageUtils.formatMessage(messageText, "SPRING AMQP");
                messageUtils.saveMessage(messageMap, multiThreaded);

                LOGGER.info("Sending SPRING AMQP Message " + i);
                if (multiThreaded) {
                    sendMessageMultiThread(template, exchangeName, routingKey, messageMap);
                } else {
                    template.convertAndSend(exchangeName, routingKey, messageMap);
                }
                i++;
            }
            LOGGER.info("Completed Sending SPRING AMQP Messages");
        }
    }

    @Async
    void sendMessageMultiThread(RabbitTemplate template, String exchangeName, String routingKey, Map<String, String> messageMap) {
        template.convertAndSend(exchangeName, routingKey, messageMap);
    }


    /**
     * Creates connection to RabbitMQ server using specific env variables
     *
     * @return CachingConnectionFactory
     */
    CachingConnectionFactory returnConnection() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(rabbitUserName);
        connectionFactory.setPassword(rabbitPassWord);
        connectionFactory.setPort(port);
        connectionFactory.setVirtualHost(virtualHost);

        declareRabbitArchitecture(connectionFactory);
        return connectionFactory;
    }

    /**
     * Used for declaring architecture(queues, exchanges, bindings)
     *
     * @param connectionFactory
     */
    void declareRabbitArchitecture(CachingConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        DirectExchange topicExchange = new DirectExchange(exchangeName, durableQueue, false);
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);

        admin.declareQueue(new Queue(queueName, false));
        admin.declareExchange(topicExchange);
        admin.declareBinding(binding);
    }

}
