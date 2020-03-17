package com.msc.spring.producer.jeromq.jms;

import com.msc.spring.producer.message.MessageUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(JEROMQPublisher.class);

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
                LOGGER.info("Starting Publisher..");
                publisher.setIdentity("B".getBytes());

                // for testing setting sleep at 100ms to ensure started.
                Thread.sleep(100l);
                Integer messageVolume = messageUtils.messageVolume;

                for (int i = 1; i <= messageVolume; i++) {
                    publisher.sendMore("B");

                    String messageText = messageUtils.generateMessage();
                    Map<String, String> messageMap = messageUtils.formatMessage(messageText, "JEROMQ");
                    messageUtils.saveMessage(messageMap, multiThreaded);

                    byte[] mapBytes = messageUtils.convertMapToBytes(messageMap);

                    LOGGER.info("Sending JEROMQ Message " + i);
                    if (multiThreaded) {
                        sendMessageMultiThread(publisher, mapBytes);
                    } else {
                        publisher.send(mapBytes);
                    }
                    LOGGER.info("Completed Sending JEROMQ Messages");
                }
                publisher.close();
                context.term();
            } catch (Exception e) {
                LOGGER.info(errorMessage + e.getLocalizedMessage());

            }
        }
    }

    @Async
    void sendMessageMultiThread(ZMQ.Socket publisher, byte[] mapBytes) throws Exception {
        publisher.send(mapBytes);
    }
}
