package com.msc.spring.producer.jeromq.jms;

import com.msc.spring.producer.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${jeromq.enabled}")
    private boolean jeroMQEnabled;

    final String errorMessage = "Exception encountered = ";

    @Autowired
    private Message message;

    @Bean
    public void setUpJEROMQProducerAndSendMessage() {
        if (jeroMQEnabled) {
            try (ZMQ.Context context = ZMQ.context(1);
                 ZMQ.Socket publisher = context.socket(ZMQ.PUB);
            ) {
                publisher.bind(bindAddress);
                System.out.println("Starting Publisher..");
                publisher.setIdentity("B".getBytes());
                // for testing setting sleep at 100ms to ensure started.
                Integer messageVolume = message.messageVolume;

                for (int i = 0; i <= messageVolume; i++) {
                    publisher.sendMore("B");

                    String messageText = message.generateMessage();
                    boolean isSent = publisher.send("( " + message.messageType + System.currentTimeMillis() + "):" + i + messageText);
                    System.out.println("JEROMQ Message was sent " + i + " , " + isSent);
                }
                publisher.close();
                context.term();
            } catch (Exception e) {
                System.out.println(errorMessage + e.getLocalizedMessage());

            }
        }
    }
}
