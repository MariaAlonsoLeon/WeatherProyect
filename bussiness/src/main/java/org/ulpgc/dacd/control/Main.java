package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.*;
import org.ulpgc.dacd.control.exceptions.EventReceiverException;
import org.ulpgc.dacd.model.Modelo;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        // Configuración de la aplicación
        String brokerUrl = "tcp://localhost:61616";
        String dataLakeDirectory = "/ruta/a/tu/data/lake";
        String neo4jUri = "bolt://localhost:7687";
        String neo4jUser = "tu_usuario";
        String neo4jPassword = "tu_contraseña";  

        // Crear instancias de las clases necesarias
        DataLakeAccessor dataLakeAccessor = new DataLakeAccessor(dataLakeDirectory);
        Modelo modelo = new Modelo(neo4jUri, neo4jUser, neo4jPassword);
        TopicSubscriber topicSubscriber = new TopicSubscriber(brokerUrl, List.of("prediction.Weather", "prediction.Hotel"), "clientId");
        HandlerFactory handlerFactory = new HandlerFactory(modelo);

        // Configurar los handlers para el TopicSubscriber
        WeatherHandler weatherHandler = new WeatherHandler(modelo);
        HotelHandler hotelHandler = new HotelHandler(modelo);

        topicSubscriber.registerHandler("prediction.Weather", weatherHandler);
        topicSubscriber.registerHandler("prediction.Hotel", hotelHandler);

        // Iniciar el TopicSubscriber
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
