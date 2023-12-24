package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.control.exceptions.EventReceiverException;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TopicSubscriber implements Subscriber {
    private static final Logger logger = Logger.getLogger(TopicSubscriber.class.getName());
    private Connection connection;
    private Session session;
    private final HandlerFactory handlerFactory;
    private final String brokerUrl;
    private final String topicName;
    private final String clientID;

    public TopicSubscriber(String brokerUrl, String topicName, String clientID) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.clientID = clientID;
        this.handlerFactory = new HandlerFactory();
    }

    @Override
    public void start() throws EventReceiverException {
        try {
            initializeConnection();
            initializeSession();
            initializeMessageListener();
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

    private void initializeMessageListener() throws JMSException {
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createDurableSubscriber(topic, clientID + topicName);
        consumer.setMessageListener(this::processMessage);
    }

    private void processMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                String receivedMessage = ((TextMessage) message).getText();
                Handler handler = handlerFactory.create(getHandlerType(message));
                handler.handleEvent(receivedMessage);
                System.out.println("Received message: " + receivedMessage);
            } catch (JMSException e) {
                logger.log(Level.SEVERE, "Error processing message", e);
            }
        }
    }

    private String getHandlerType(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            try {
                String receivedMessage = ((TextMessage) message).getText();
                JsonObject jsonObject = JsonParser.parseString(receivedMessage).getAsJsonObject();
                if (jsonObject.has("ss")) {
                    return jsonObject.get("ss").getAsString();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error parsing JSON or extracting 'ss'", e);
            }
        }
        return "default";
    }
}
