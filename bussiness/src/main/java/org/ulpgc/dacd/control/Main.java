package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.EventReceiverException;
import org.ulpgc.dacd.control.exceptions.DataMartStoreException;
import org.ulpgc.dacd.view.HotelRecommendationAPI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Main mainInstance = new Main();
        mainInstance.run(args);
    }

    private void run(String[] args) {
        clearTables(args);
        String brokerUrl = "tcp://localhost:61616";
        if (args.length < 4) {
            logger.severe("Insufficient arguments. Usage: Main <baseDirectory> <topic1> <topic2> ...");
        }

        try {
            //Map<String, String> topicNames = handlersOf(Arrays.asList(args[2], args[3]));
            List<String> topicNames = Arrays.asList(args[2], args[3]);
            DataLakeAccessor dataLakeAccessor = new DataLakeAccessor(args[0]);
            DataMartStore dataMartStore = new SqLiteDataMartStore(args[1]);
            Map<String, Handler> handlerMap = initializeHandlers(dataMartStore, topicNames);
            TopicSubscriber topicSubscriber = new TopicSubscriber(brokerUrl, topicNames, "clientId", handlerMap);
            DataMartBuilder dataMartBuilder = new DataMartBuilder(dataLakeAccessor, handlerMap.get(topicNames.get(0)), handlerMap.get(topicNames.get(1)));
            dataMartBuilder.buildDataMart();
            DataMartConsultant dataMartConsultant = new DataMartConsultant(args[1]);
            HotelRecommendationAPI hotelAPI = new HotelRecommendationAPI(dataMartConsultant);
            hotelAPI.init();
            startTopicSubscriber(topicSubscriber);
        } finally {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Cerrando la aplicación. Limpiando datos de las tablas...");
                logger.info("Datos de las tablas limpiados.");
            }));
        }
    }

    private Map<String, Handler> initializeHandlers(DataMartStore dataMartStore, List<String> topicsName) {
        HandlerFactory handlerFactory = new HandlerFactory(dataMartStore);
        Map<String, Handler> handlerMap = new HashMap<>();
        for (String topicName : topicsName) {
            handlerMap.put(topicName, handlerFactory.create(topicName));
        }
        return handlerMap;
    }

    /*private Map<String, Handler> initializeHandlers(DataMartStore dataMartStore, Map<String, String> handlers) {
        HandlerFactory handlerFactory = new HandlerFactory(dataMartStore);
        Map<String, Handler> handlerMap = new HashMap<>();
        for (String topicName : handlers.keySet()) {
            handlerMap.put(handlers.get(topicName), handlerFactory.create(topicName));
        }
        return handlerMap;
    }*/

    private void startTopicSubscriber(TopicSubscriber topicSubscriber) {
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

    private void clearTables(String[] args) { //Eliminar la base de datos
        try {
            String dataMartDirectory = args[1];
            DataMartStore dataMartStore = new SqLiteDataMartStore(dataMartDirectory);
            ((SqLiteDataMartStore) dataMartStore).clearTables();
        } catch (DataMartStoreException e) {
            logger.log(Level.SEVERE, "Error clearing tables", e);
        }
    }
}
