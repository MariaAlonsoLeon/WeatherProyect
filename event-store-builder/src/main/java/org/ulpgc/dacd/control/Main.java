package org.ulpgc.dacd.control;
import org.ulpgc.dacd.control.exceptions.EventReceiverException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        String baseDirectory = args[0];
        FileEventStoreBuilder fileEventStoreBuilder = new FileEventStoreBuilder(baseDirectory);
        TopicSubscriber topicSubscriber = new TopicSubscriber(fileEventStoreBuilder);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                topicSubscriber.start();
            } catch (EventReceiverException e) {
                e.printStackTrace();
            }
        });
    }
}