package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.EventReceiverException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.severe("Base directory not provided. Exiting...");
            return;
        }

        String baseDirectory = args[0];

        // Create instances of required classes
        DataLakeAccessor dataLakeAccessor = new DataLakeAccessor(baseDirectory);
        FlightHandler flightHandler = new FlightHandler();
        WeatherHandler weatherHandler = new WeatherHandler();
        DataMartBuilder dataMartBuilder = new DataMartBuilder(dataLakeAccessor, flightHandler, weatherHandler);

        // Create and start the subscribers
        Subscriber flightSubscriber = new TopicSubscriber("tcp://localhost:61616", "prediction.Flight", "FlightSubscriber");
        Subscriber weatherSubscriber = new TopicSubscriber("tcp://localhost:61616", "prediction.Weather", "WeatherSubscriber");

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            try {
                flightSubscriber.start();
            } catch (EventReceiverException e) {
                logger.log(Level.SEVERE, "Error starting FlightSubscriber", e);
            }
        });

        executorService.submit(() -> {
            try {
                weatherSubscriber.start();
            } catch (EventReceiverException e) {
                logger.log(Level.SEVERE, "Error starting WeatherSubscriber", e);
            }
        });

        executorService.shutdown();

        dataMartBuilder.buildDataMart();
    }
}
