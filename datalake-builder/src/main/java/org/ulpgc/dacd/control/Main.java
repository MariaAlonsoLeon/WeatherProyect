package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.EventReceiverException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length < 2) {
            logger.severe("Insufficient arguments. Usage: Main <baseDirectory> <topic1> <topic2> ...");
            return;
        }
        String baseDirectory = args[0];
        List<String> topicNames = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
        EventStoreBuilder eventStoreBuilder = new FileEventStoreBuilder(baseDirectory);
        Subscriber subscriber = new TopicSubscriber("tcp://localhost:61616", topicNames, "DataLakeStoreBuilder", eventStoreBuilder);
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
