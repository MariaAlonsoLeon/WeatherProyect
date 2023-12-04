package org.ulpgc.dacd.control;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.control.exceptions.WeatherReceiverException;

import java.util.ArrayList;
import java.util.List;

public class Listener implements WeatherReceiver {
    private final Connection connection;
    private final Session session;
    private final Topic topic;
    private final MessageConsumer consumer;

    public Listener(String brokerUrl, String topicName) throws WeatherReceiverException {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            connection = connectionFactory.createConnection();
            connection.setClientID("weatherClient");
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            consumer = session.createDurableSubscriber(topic, "weatherSubscription");
        } catch (JMSException e) {
            throw new WeatherReceiverException("Error creating listener", e);
        }
    }

    @Override
    public ArrayList<String> getWeather() throws WeatherReceiverException {
        List<String> eventList = new ArrayList<>();
        try {
            System.out.println("Prueba");
            for (int i = 0; i < 40; i++) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println(textMessage.getText());
                    eventList.add(textMessage.getText());
                }
            }
        } catch (JMSException e) {
            throw new WeatherReceiverException("Error receiving weather events", e);
        }

        return (ArrayList<String>) eventList;
    }
}
