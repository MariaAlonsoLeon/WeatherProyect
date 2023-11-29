package org.ulpgc.dacd.control;

import com.google.gson.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Listener implements WeatherReceiver {

    private CountDownLatch latch;

    public Listener() {
        this.latch = new CountDownLatch(40);
    }

    @Override
    public ArrayList<String> getWeather() throws JMSException {
        String brokerUrl = "tcp://localhost:61616";
        String topicName = "prediction.Weather";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        ArrayList<String> weatherJson = new ArrayList<>();
        Connection connection = connectionFactory.createConnection();

        try {
            setupConnection(connection);
            Session session = createSession(connection);
            Topic topic = createTopic(session, topicName);
            MessageConsumer consumer = createMessageConsumer(session, topic, weatherJson);

            System.out.println("Waiting for messages. Please wait...");
            waitForMessages();

            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            handleException(e);
        }

        return weatherJson;
    }

    private void setupConnection(Connection connection) throws JMSException {
        connection.start();
    }

    private Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private Topic createTopic(Session session, String topicName) throws JMSException {
        return session.createTopic(topicName);
    }

    private MessageConsumer createMessageConsumer(Session session, Topic topic, ArrayList<String> weatherJson)
            throws JMSException {
        MessageConsumer consumer = session.createConsumer(topic);

        consumer.setMessageListener(message -> processTextMessage(message, weatherJson));

        return consumer;
    }

    private void waitForMessages() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            handleException(e);
        }
    }

    private void processTextMessage(Message message, ArrayList<String> weatherJson) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received message: " + textMessage.getText());
                weatherJson.add(textMessage.getText());
                latch.countDown();
            }
        } catch (JMSException e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
    }
}
