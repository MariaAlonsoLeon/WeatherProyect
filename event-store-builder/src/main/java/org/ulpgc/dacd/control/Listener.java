package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.control.exceptions.WeatherReceiverException;

import javax.jms.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Listener implements WeatherReceiver {

    private CountDownLatch latch;
    private String brokerUrl;
    private String topicName;

    public Listener(String brokerUrl, String topicName) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.latch = new CountDownLatch(40);
    }

    @Override
    public ArrayList<String> getWeather() throws WeatherReceiverException {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            ArrayList<String> weatherJson = new ArrayList<>();
            Connection connection = connectionFactory.createConnection();
            try {
                setupConnection(connection);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Topic topic = createTopic(session, topicName);
                MessageConsumer consumer = createMessageConsumer(session, topic, weatherJson);
                System.out.println("Waiting for messages. Please wait...");
                waitForMessages();
                consumer.close();
                session.close();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException e) {
                        throw new WeatherReceiverException("Error al cerrar la conexión", e);
                    }
                }
            }
            return weatherJson;
        } catch (Exception e) {
            throw new WeatherReceiverException("Error general en la obtención de datos meteorológicos", e);
        }
    }

    private void setupConnection(Connection connection) throws JMSException {
        connection.setClientID("123");
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
        MessageConsumer consumer = session.createDurableSubscriber(topic, "María");

        consumer.setMessageListener(message -> {
            try {
                processTextMessage(message, weatherJson);
            } catch (WeatherReceiverException e) {
                throw new RuntimeException(e);
            }
        });

        return consumer;
    }

    private void waitForMessages() throws WeatherReceiverException {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new WeatherReceiverException("Error en la espera de mensajes", e);
        }
    }

    private void processTextMessage(Message message, ArrayList<String> weatherJson) throws WeatherReceiverException {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println("Received message: " + textMessage.getText());
                weatherJson.add(textMessage.getText());
                latch.countDown();
            }
        } catch (JMSException e) {
            throw new WeatherReceiverException("Error en el procesamiento de mensajes de texto", e);
        }
    }
}
