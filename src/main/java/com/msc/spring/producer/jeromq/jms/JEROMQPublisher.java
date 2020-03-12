package com.msc.spring.producer.jeromq.jms;

import com.msc.spring.producer.message.MessageUtils;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

import java.util.Map;


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

    @Value("${multi.thread.enabled}")
    private boolean multiThreaded;

    final String errorMessage = "Exception encountered = ";

    @Autowired
    private MessageUtils messageUtils;

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
                Integer messageVolume = messageUtils.messageVolume;

                for (int i = 0; i <= messageVolume; i++) {
                    publisher.sendMore("B");

                    String messageText = messageUtils.generateMessage();
                    Map<String, String> messageMap = messageUtils.formatMessage(messageText, "JEROMQ");
                    messageUtils.saveMessage(messageMap);

                    byte[] mapBytes = messageUtils.convertMapToBytes(messageMap);

                    System.out.println("Sending JEROMQ Message " + i);
                    if (multiThreaded) {
                        sendMessageMultiThread(publisher, mapBytes);
                    } else {
                        publisher.send(mapBytes);
                    }
                }
                publisher.close();
                context.term();
            } catch (Exception e) {
                System.out.println(errorMessage + e.getLocalizedMessage());

            }
        }
    }

    @Async
    void sendMessageMultiThread(ZMQ.Socket publisher, byte[] mapBytes) throws Exception {
        publisher.send(mapBytes);
    }
}
