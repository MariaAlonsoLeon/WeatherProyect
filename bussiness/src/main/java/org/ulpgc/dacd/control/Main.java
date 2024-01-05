package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.EventReceiverException;
import org.ulpgc.dacd.view.HotelRecommendationAPI;

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

        DataLakeAccessor dataLakeAccessor = new DataLakeAccessor(dataLakeDirectory);
        DataMartStore dataMartStore = new SqLiteDataMartStore("C:\\Users\\Maria\\Desktop\\HotelDB.db");
        HandlerFactory handlerFactory = new HandlerFactory(dataMartStore);
        Handler weatherHandler = handlerFactory.create("prediction.Weather");
        Handler hotelOfferHandler = handlerFactory.create("prediction.Hotel");

        TopicSubscriber topicSubscriber = new TopicSubscriber(brokerUrl, List.of("prediction.Weather", "prediction.Hotel"), "clientId");

        DataMartBuilder dataMartBuilder = new DataMartBuilder(dataLakeAccessor, hotelOfferHandler, weatherHandler);
        // Llamada a buildDataMart si es necesario
        dataMartBuilder.buildDataMart();

        topicSubscriber.registerHandler("prediction.Weather", weatherHandler);
        topicSubscriber.registerHandler("prediction.Hotel", hotelOfferHandler);


        DataMartConsultant dataMartConsultant = new DataMartConsultant("C:\\Users\\Maria\\Desktop\\HotelDB.db");
        HotelRecommendationAPI hotelAPI = new HotelRecommendationAPI(dataMartConsultant);
        hotelAPI.init();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                topicSubscriber.start();
            } catch (EventReceiverException e) {
                logger.log(Level.SEVERE, "Error starting TopicSubscriber", e);
            }
        });

        // Agregar las siguientes líneas para probar la interfaz de usuario
        //CommandLineInterface cli = new CommandLineInterface(new LocationRecommendationService(modelo));
        //cli.iniciar();

        executorService.shutdown();
        //modelo.limpiarGrafo();
    }
}
