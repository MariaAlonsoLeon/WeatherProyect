package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMQMessageSender {
    private static final String QUEUE_NAME = "prediction.Weather";
    private final String brokerURL;

    public ActiveMQMessageSender(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public static void sendMessage(String jsonMessage) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(QUEUE_NAME);
            MessageProducer producer = session.createProducer(topic);

            TextMessage message = session.createTextMessage(jsonMessage);
            producer.send(message);

            System.out.println("Message sent to the queue: " + jsonMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
