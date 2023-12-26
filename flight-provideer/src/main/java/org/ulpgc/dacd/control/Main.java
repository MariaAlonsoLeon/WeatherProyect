package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Flight;

import java.util.ArrayList;
import java.util.List;


import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Configuración para el proveedor de vuelos
        String rapidApiKey = "37bd0729efmsh1608581c42629c7p17b46fjsn8dba722d7357"; // Reemplazar con tu clave de API de RapidAPI
        String apiUrl = "https://timetable-lookup.p.rapidapi.com/TimeTable/"; // Reemplazar con la URL de tu API
        FlightSupplier flightSupplier = new TimeTableLookupFlightSupplier(rapidApiKey, apiUrl);

        // Configuración para el almacenamiento de vuelos
        String brokerUrl = "tcp://localhost:61616"; // Reemplazar con la URL de tu broker ActiveMQ
        String topicName = "prediction.Flight"; // Reemplazar con el nombre del tópico de destino en tu broker
        String clientId = "PredictionProvider";
        FlightStore flightStore = new JMSFlightStore(brokerUrl, topicName, clientId);

        // Controlador de vuelos
        FlightController flightController = new FlightController(flightSupplier, flightStore);

        // Ejecutar la obtención y almacenamiento de vuelos
        flightController.execute();

        // Puedes imprimir o procesar los vuelos obtenidos según tus necesidades

    }
}

/*public class Main {
    public static void main(String[] args) {
        // Configuración para el proveedor de vuelos
        String aviationStackApiKey = "e075be05ad7555e5a5fea39bb8568c8e"; // Reemplazar con tu clave de API de AviationStack
        String aviationStackUrl = "http://api.aviationstack.com/v1/flights";
        FlightSupplier flightSupplier = new AviationStackFlightSupplier(aviationStackApiKey, aviationStackUrl);

        // Configuración para el almacenamiento de vuelos
        String brokerUrl = "tcp://localhost:61616"; // Reemplazar con la URL de tu broker ActiveMQ
        String topicName = "prediction.Flight"; // Reemplazar con el nombre del tópico de destino en tu broker
        String clientId = "PredictionProvider";
        FlightStore flightStore = new JMSFlightStore(brokerUrl, topicName, clientId);

        // Controlador de vuelos
        FlightController flightController = new FlightController(flightSupplier, flightStore);

        // Ejecutar la obtención y almacenamiento de vuelos
        flightController.execute();
    }
}*/
