package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.commands.Command;
import org.ulpgc.dacd.control.commands.CommandFactory;
import org.ulpgc.dacd.control.exceptions.EventReceiverException;
import org.ulpgc.dacd.control.exceptions.DataMartStoreException;
import org.ulpgc.dacd.control.handlers.Handler;
import org.ulpgc.dacd.control.handlers.HandlerFactory;
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
        String brokerUrl = "tcp://localhost:61616";
        if (args.length < 2) {
            logger.severe("Insufficient arguments. Usage: Main <baseDataLakeDirectory> <baseDataMartDirectory> ...");
        }
        dropTables(args[1]);
        List<String> topicNames = Arrays.asList("prediction.Weather", "prediction.Hotel");
        DataMartStore dataMartStore = new SqLiteDataMartStore(args[1]);
        DataLakeAccessor dataLakeAccessor = new DataLakeAccessor(args[0]);
        Map<String, Handler> handlerMap = initializeHandlers(dataMartStore, topicNames);
        Subscriber topicSubscriber = new TopicSubscriber(brokerUrl, topicNames, "WeatherResponsiveHotelOfferAdvisor", handlerMap);
        DataMartBuilder dataMartBuilder = new DataMartBuilder(dataLakeAccessor, handlerMap);
        dataMartBuilder.buildDataMart();
        CommandFactory commandFactory = new CommandFactory(args[1]);
        Map<String, Command> commands = initializeCommands(commandFactory);
        HotelRecommendationAPI hotelAPI = new HotelRecommendationAPI(commands);
        hotelAPI.init();
        startTopicSubscriber(topicSubscriber);
    }

    private Map<String, Handler> initializeHandlers(DataMartStore dataMartStore, List<String> topicsName) {
        HandlerFactory handlerFactory = new HandlerFactory(dataMartStore);
        Map<String, Handler> handlerMap = new HashMap<>();
        for (String topicName : topicsName) {
            handlerMap.put(topicName, handlerFactory.create(topicName));
        }
        return handlerMap;
    }

    private Map<String, Command> initializeCommands(CommandFactory commandFactory) {
        Map<String, Command> commands = new HashMap<>();
        commands.put("getCheapestOffersByWeatherAndDate", commandFactory.create("getCheapestOffersByWeatherAndDate"));
        commands.put("getLocationsByWeatherAndDate", commandFactory.create("getLocationsByWeatherAndDate"));
        commands.put("getCheapestOfferByLocationAndDate", commandFactory.create("getCheapestOfferByLocationAndDate"));
        return commands;
    }

    private void startTopicSubscriber(Subscriber subscriber) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                subscriber.start();
            } catch (EventReceiverException e) {
                logger.log(Level.SEVERE, "Error starting TopicSubscriber", e);
            }
        });
    }

    private void dropTables(String dataMartDirectory) {
        try {
            DataMartStore dataMartStore = new SqLiteDataMartStore(dataMartDirectory);
            ((SqLiteDataMartStore) dataMartStore).dropDatabase();
        } catch (DataMartStoreException e) {
            logger.log(Level.SEVERE, "Error clearing tables", e);
        }
    }
}
