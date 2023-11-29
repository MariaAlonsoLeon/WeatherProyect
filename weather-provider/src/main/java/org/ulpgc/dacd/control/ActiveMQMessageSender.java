package org.ulpgc.dacd.control;

import com.google.gson.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.Weather;

import javax.jms.*;
import java.time.Instant;
import java.util.List;

public class ActiveMQMessageSender {
    private static final String QUEUE_NAME = "prediction.Weather";
    private final String brokerURL;

    public ActiveMQMessageSender(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public static void sendMessage(List<Weather> weathers) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_BROKER_URL);
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(QUEUE_NAME);

            System.out.println(weathers.size());
            for(Weather weather: weathers){
                String jsonMessage = weatherListToJson(weather);
                MessageProducer producer = session.createProducer(topic);

                TextMessage message = session.createTextMessage(jsonMessage);
                producer.send(message);
                //System.out.println("Message sent to the queue: " + jsonMessage);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static String weatherListToJson(Weather weather) {
        Gson gson = prepareGson();
        return gson.toJson(weather);
    }

    private static Gson prepareGson() {
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

