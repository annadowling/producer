package com.msc.spring.producer.spring.amqp;

import com.msc.spring.producer.message.MessageUtils;
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
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by annadowling on 2020-01-15.
 */

@Component
@ConditionalOnProperty(prefix = "spring.amqp", name = "enabled", havingValue = "true")
public class SpringAMQPPublisher {

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

    @Value("${rabbitmq.virtualhost}")
    private String virtualHost;

    @Value("${spring.amqp.enabled}")
    private boolean springAMQPEnabled;

    @Autowired
    private MessageUtils messageUtils;


    @Bean
    public void setUpSpringProducerAndSendMessage() {
        if (springAMQPEnabled) {
            ConnectionFactory connectionFactory = returnConnection();
            RabbitTemplate template = new RabbitTemplate(connectionFactory);

            String messageText = messageUtils.generateMessage();

            int i = 0;
            while (i < messageUtils.messageVolume) {
                Map<String, String> messageMap = messageUtils.formatMessage(messageText, "SPRING AMQP");
                messageUtils.saveMessage(messageMap);

                System.out.println("Sending SPRING AMQP Message = " + messageText);
                template.convertAndSend(exchangeName, routingKey, messageMap);
                i++;
            }
        }
    }


    /**
     * Creates connection to RabbitMQ server using specific env variables
     * @return CachingConnectionFactory
     */
    CachingConnectionFactory returnConnection(){
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
     * @param connectionFactory
     */
    void declareRabbitArchitecture(CachingConnectionFactory connectionFactory){
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        DirectExchange topicExchange = new DirectExchange(exchangeName);
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);

        admin.declareQueue(new Queue(queueName));
        admin.declareExchange(topicExchange);
        admin.declareBinding(binding);
    }

}
