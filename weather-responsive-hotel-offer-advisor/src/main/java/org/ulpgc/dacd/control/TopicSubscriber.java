package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.control.exceptions.EventReceiverException;
import org.ulpgc.dacd.control.handlers.Handler;
import javax.jms.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TopicSubscriber implements Subscriber {
    private static final Logger logger = Logger.getLogger(TopicSubscriber.class.getName());
    private Connection connection;
    private Session session;
    private final Map<String, Handler> topicHandlers;
    private final String brokerUrl;
    private final List<String> topicNames;
    private final String clientID;

    public TopicSubscriber(String brokerUrl, List<String> topicNames, String clientID, Map<String, Handler> topicHandlers) {
        this.brokerUrl = brokerUrl;
        this.topicNames = topicNames;
        this.clientID = clientID;
        this.topicHandlers = topicHandlers;
    }


    @Override
    public void start() throws EventReceiverException {
        try {
            initializeConnection();
            initializeSession();
            for (String topicName : topicNames) {
                initializeMessageListener(topicName);
            }
        } catch (JMSException e) {
            throw new EventReceiverException("Error creating listener", e);
        }
    }

    private void initializeConnection() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.setClientID(clientID);
        connection.start();
    }

    private void initializeSession() throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void initializeMessageListener(String topicName) throws JMSException {
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createDurableSubscriber(topic, clientID + topicName);
        consumer.setMessageListener(message -> processMessage(message, topicName));
    }

    private void processMessage(Message message, String topicName) {
        if (message instanceof TextMessage) {
            try {
                String receivedMessage = ((TextMessage) message).getText();
                Handler handler = topicHandlers.get(topicName);
                System.out.println(message);
                if (handler != null) { handler.handleEvent(receivedMessage);
                } else { System.out.println("No handler registered for topic: " + topicName);
                }
            } catch (JMSException e) { logger.log(Level.SEVERE, "Error processing message", e);
            }
        }
    }
}
