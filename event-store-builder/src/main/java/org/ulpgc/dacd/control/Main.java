package org.ulpgc.dacd.control;
import javax.jms.JMSException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) throws JMSException {
        Listener listener = new Listener();
        EventController controller = new EventController(listener, new FileWeatherStore(args[0]));
        controller.execute();
    }

}