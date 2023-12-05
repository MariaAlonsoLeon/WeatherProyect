package org.ulpgc.dacd.control;
import org.ulpgc.dacd.control.exceptions.EventReceiverException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        // Crear una instancia de FileEventStoreBuilder
        String baseDirectory = args[0];
        FileEventStoreBuilder fileEventStoreBuilder = new FileEventStoreBuilder(baseDirectory);

        // Crear una instancia de TopicSubscriber
        TopicSubscriber topicSubscriber = new TopicSubscriber(fileEventStoreBuilder);

        // Configurar el ExecutorService para manejar la suscripción en un hilo separado
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Iniciar la suscripción de manera asíncrona
        executorService.submit(() -> {
            try {
                topicSubscriber.start();
            } catch (EventReceiverException e) {
                e.printStackTrace();
            }
        });

        // Puedes continuar con el resto de tu lógica aquí o simplemente esperar a que los mensajes lleguen en segundo plano.
    }

    /*public static void main(String[] args) throws WeatherReceiverException, JMSException {
        String brokerUrl = "tcp://localhost:61616";
        String topicName = "prediction.Weather";
        Listener listener = new Listener(brokerUrl, topicName);
        EventController controller = new EventController(listener, new FileEventStoreBuilder(args[0]));
        controller.execute();
    }*/
}