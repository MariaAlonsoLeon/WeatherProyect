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
        FileEventStoreBuilder fileEventStoreBuilder = new FileEventStoreBuilder(baseDirectory);
        TopicSubscriber topicSubscriber = new TopicSubscriber(fileEventStoreBuilder);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                topicSubscriber.start();
            } catch (EventReceiverException e) {
                logger.log(Level.SEVERE, "Error starting TopicSubscriber", e);
            }
        });
        executorService.shutdown();
    }
}
