package org.ulpgc.dacd.control;

import com.google.gson.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.Weather;
import javax.jms.*;
import java.time.Instant;
public class JMSWeatherStore implements WeatherStore {
    private final String topicName;
    private final String brokerUrl;
    private final String clientId;

    public JMSWeatherStore(String brokerUrl, String topicName, String clientId) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.clientId = clientId;
    }

    @Override
    public void save(Weather weather) throws StoreException {
        try (Connection connection = createConnection()) {
            connection.setClientID(clientId);
            connection.start();
            Session session = createSession(connection);
            Topic topic = createTopic(session);
            String jsonMessage = weatherToJson(weather);
            sendMessageToTopic(session, topic, jsonMessage);
        } catch (JMSException e) {
            throw new StoreException(e.getMessage());
        }
    }

    private Connection createConnection() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        return connectionFactory.createConnection();
    }

    private Session createSession(Connection connection) throws JMSException {
        try {
            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new JMSException("Error creating JMS session: " + e.getMessage());
        }
    }

    private Topic createTopic(Session session) throws JMSException {
        try {
            return session.createTopic(topicName);
        } catch (JMSException e) {
            throw new JMSException("Error creating JMS topic: " + e.getMessage());
        }
    }

    private void sendMessageToTopic(Session session, Topic topic, String jsonMessage) throws JMSException {
        MessageProducer producer = session.createProducer(topic);
        TextMessage message = session.createTextMessage(jsonMessage);
        producer.send(message);
        System.out.println("Message sent to the queue: " + jsonMessage);
    }

    private String weatherToJson(Weather weather) {
        Gson gson = prepareGson();
        return gson.toJson(weather);
    }

    private Gson prepareGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantSerializer())
                .create();
    }

    private static class InstantSerializer implements JsonSerializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
