package org.ulpgc.dacd.view;

import org.ulpgc.dacd.control.LocationRecommendationService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private final LocationRecommendationService locationRecommendationService;

    public CommandLineInterface(LocationRecommendationService locationRecommendationService) {
        this.locationRecommendationService = locationRecommendationService;
    }

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== Sistema de Recomendación de Hoteles ===");
            System.out.println("1. Elegir tipo de clima");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    mostrarRecomendacion();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        }
    }

    private void mostrarRecomendacion() {
        System.out.println("=== Elegir Tipo de Clima ===");
        // Aquí puedes mostrar los tipos de clima disponibles y permitir al usuario seleccionar uno
        // Supongamos que obtienes la selección del usuario y la almacenas en la variable tipoClimaElegido

        String tipoClimaElegido = obtenerTipoClimaSeleccionado();

        if (tipoClimaElegido == null) {
            System.out.println("Tipo de clima no válido. Volviendo al menú principal.");
            return;
        }

        System.out.println("=== Localizaciones con Clima " + tipoClimaElegido + " ===");
        // Obtener las localizaciones con el clima seleccionado
        List<String> listaDeLocalizaciones = locationRecommendationService.obtenerLocalizacionesPorTipoClima(tipoClimaElegido);

        if (listaDeLocalizaciones.isEmpty()) {
            System.out.println("No hay localizaciones con el tipo de clima seleccionado.");
            return;
        }

        // Mostrar las ofertas más baratas para cada localización
        System.out.println("=== Ofertas Más Baratas ===");
        for (String location : listaDeLocalizaciones) {
            double tarifaMasBarata = locationRecommendationService.obtenerTarifaMasBarata(location, "2023-12-29"); // Supongamos que la fecha es fija
            System.out.println("Localización: " + location + ", Tarifa Más Barata: " + tarifaMasBarata);
        }
    }

    private String obtenerTipoClimaSeleccionado() {
        Scanner scanner = new Scanner(System.in);

        // Tipos de clima válidos
        List<String> tiposDeClimaValidos = Arrays.asList("COLD", "RAINY", "WARN", "SNOWY", "CLEAR");

        while (true) {
            System.out.print("Seleccione el tipo de clima: ");
            String tipoClima = scanner.nextLine();

            // Validar el tipo de clima ingresado
            if (tiposDeClimaValidos.contains(tipoClima)) {
                return tipoClima;
            } else {
                System.out.println("Tipo de clima no válido. Inténtelo de nuevo.");
            }
        }
    }
}
