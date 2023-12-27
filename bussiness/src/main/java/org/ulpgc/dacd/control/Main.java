package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.EventReceiverException;
import org.ulpgc.dacd.model.Modelo;
import org.ulpgc.dacd.view.CommandLineInterface;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        if (args.length < 2) {
            logger.severe("Insufficient arguments. Usage: Main <baseDirectory> <topic1> <topic2> ...");
            return;
        }

        String dataLakeDirectory = args[0];
        String neo4jPassword = args[1];

        String neo4jUri = "bolt://localhost:7687";
        String neo4jUser = "neo4j";

        DataLakeAccessor dataLakeAccessor = new DataLakeAccessor(dataLakeDirectory);
        Modelo modelo = new Modelo(neo4jUri, neo4jUser, neo4jPassword);
        DataMartBuilder dataMartBuilder = new DataMartBuilder(modelo, dataLakeAccessor);
        TopicSubscriber topicSubscriber = new TopicSubscriber(brokerUrl, List.of("prediction.Weather", "prediction.Hotel"), "clientId");
        HandlerFactory handlerFactory = new HandlerFactory(modelo);

        WeatherHandler weatherHandler = new WeatherHandler(modelo);
        HotelHandler hotelHandler = new HotelHandler(modelo);

        topicSubscriber.registerHandler("prediction.Weather", weatherHandler);
        topicSubscriber.registerHandler("prediction.Hotel", hotelHandler);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                topicSubscriber.start();
            } catch (EventReceiverException e) {
                logger.log(Level.SEVERE, "Error starting TopicSubscriber", e);
            }
        });

        // Agregar las siguientes líneas para probar la interfaz de usuario
        CommandLineInterface cli = new CommandLineInterface(new LocationRecommendationService(modelo));
        cli.iniciar();

        executorService.shutdown();
    }
}
