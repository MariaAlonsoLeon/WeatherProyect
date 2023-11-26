package org.ulpgc.dacd.control;

import com.google.gson.Gson;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.Weather;

import javax.jms.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Listener implements WeatherReceiver{

    public Listener() {}

    @Override
    public ArrayList<Weather> getWeather() {
        String brokerUrl = "tcp://localhost:61616";

        String topicName = "prediciton.Weather";

        ConnectionFactory connectionFactory = (ConnectionFactory) new ActiveMQConnectionFactory(brokerUrl);

        try {
            Connection connection = connectionFactory.createConnection();

            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(topicName);

            MessageConsumer consumer = session.createConsumer(topic);

            ArrayList<String> weatherJson = new ArrayList<>();
            ArrayList<Weather> weathers = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(40);

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

            try {
                if (latch.await(5, TimeUnit.MINUTES)) {
                    System.out.println("No more messages. Exiting...");
                } else {
                    System.out.println("Timed out waiting for messages. Exiting...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            consumer.close();
            session.close();
            connection.close();

            for(String weatherString: weatherJson){
                weathers.add(jsonToWeather(weatherString));
            }
            return weathers;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }

    public static Weather jsonToWeather(String jsonWeather){
        Gson gson = new Gson();
        return gson.fromJson(jsonWeather, Weather.class);
    }
}