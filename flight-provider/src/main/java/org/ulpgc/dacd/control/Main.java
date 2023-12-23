package org.ulpgc.dacd.control;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Configuración para el proveedor de vuelos
        String aviationStackApiKey = "1d28ebd6eaf7e6bda770ca7c385f4500"; // Reemplazar con tu clave de API de AviationStack
        String aviationStackUrl = "http://api.aviationstack.com/v1/flights";
        FlightSupplier flightSupplier = new AviationStackFlightSupplier(aviationStackApiKey, aviationStackUrl);

        // Configuración para el almacenamiento de vuelos
        String brokerUrl = "tcp://localhost:61616"; // Reemplazar con la URL de tu broker ActiveMQ
        String topicName = "flight_data"; // Reemplazar con el nombre del tópico de destino en tu broker
        String clientId = "flight_controller";
        FlightStore flightStore = new JMSFlightStore(brokerUrl, topicName, clientId);

        // Controlador de vuelos
        FlightController flightController = new FlightController(flightSupplier, flightStore);

        // Ejecutar la obtención y almacenamiento de vuelos
        flightController.execute();

        List<Integer> primeros = new ArrayList<>();
        primeros.add(1);
        primeros.add(3);
        primeros.add(5);

        List<Integer> segundas = new ArrayList<>();
        segundas.add(2);
        segundas.add(4);
        segundas.add(6);


    }
}
