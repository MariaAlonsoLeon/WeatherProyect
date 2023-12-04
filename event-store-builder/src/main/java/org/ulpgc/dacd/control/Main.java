package org.ulpgc.dacd.control;
import org.ulpgc.dacd.control.exceptions.WeatherReceiverException;

import javax.jms.JMSException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) throws WeatherReceiverException, JMSException {
        String brokerUrl = "tcp://localhost:61616";
        String topicName = "prediction.Weather";
        Listener listener = new Listener(brokerUrl, topicName);
        EventController controller = new EventController(listener, new FileWeatherStore(args[0]));
        controller.execute();
    }
}