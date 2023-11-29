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
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            System.out.println("Received message: " + textMessage.getText());
                            weatherJson.add(textMessage.getText());
                            latch.countDown();
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            System.out.println("Waiting for messages. Please wait...");

            // Esperar hasta que se haya procesado al menos un mensaje
            latch.await();

            consumer.close();
            session.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return weatherJson;
    }

    // Método para esperar a que se reciban mensajes antes de salir del programa principal
    public void waitForMessages() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
