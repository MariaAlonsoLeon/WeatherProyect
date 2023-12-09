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
        EventStoreBuilder eventStoreBuilder = new FileEventStoreBuilder(baseDirectory);
        Subscriber subscriber = new TopicSubscriber("tcp://localhost:61616", "prediction.Weather", "EventStoreBuilder",  eventStoreBuilder);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                subscriber.start();
            } catch (EventReceiverException e) {
                logger.log(Level.SEVERE, "Error starting TopicSubscriber", e);
            }
        });
        executorService.shutdown();
    }
}
