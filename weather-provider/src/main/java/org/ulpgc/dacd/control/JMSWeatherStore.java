package org.ulpgc.dacd.control;

import com.google.gson.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.Weather;

import javax.jms.*;
import java.time.Instant;

public class JMSWeatherStore implements TopicSender{
    private static final String QUEUE_NAME = "prediction.Weather";
    private final String brokerURL;

    public JMSWeatherStore(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    @Override
    public void sendMessage(Weather weather) {
        try (Connection connection = createConnection()) {
            connection.setClientID("PredictionProvider");
            connection.start();
            Session session = createSession(connection);
            Topic topic = createTopic(session);

            String jsonMessage = weatherToJson(weather);
            sendMessageToTopic(session, topic, jsonMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private Connection createConnection() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
        return connectionFactory.createConnection();
    }

    private Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private Topic createTopic(Session session) throws JMSException {
        return session.createTopic(QUEUE_NAME);
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
                .setPrettyPrinting()
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
