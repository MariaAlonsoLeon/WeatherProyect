package org.ulpgc.dacd.view;

import org.ulpgc.dacd.control.LocationRecommendationService;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
        System.out.println("COLD-WARM-RANINY-SNOWY-CLEAR");
        // Aquí puedes mostrar los tipos de clima disponibles y permitir al usuario seleccionar uno
        // Supongamos que obtienes la selección del usuario y la almacenas en la variable tipoClimaElegido

        String tipoClimaElegido = obtenerTipoClimaSeleccionado();

        if (tipoClimaElegido == null) {
            System.out.println("Tipo de clima no válido. Volviendo al menú principal.");
            return;
        }

        System.out.println("=== Localizaciones con Clima " + tipoClimaElegido + " ===");
        // Obtener las localizaciones con el clima seleccionado
        Set<String> listaDeLocalizaciones = locationRecommendationService.obtenerLocalizacionesPorTipoClima(tipoClimaElegido);

        if (listaDeLocalizaciones.isEmpty()) {
            System.out.println("No hay localizaciones con el tipo de clima seleccionado.");
            return;
        } else {
            System.out.println("Localizaciones disponibles:");
            for (String location : listaDeLocalizaciones) {
                System.out.println(location);
            }
        }

        // Solicitar al usuario que elija una localización
        String localizacionElegida = obtenerLocalizacionElegida(listaDeLocalizaciones);

        if (localizacionElegida == null) {
            System.out.println("Localización no válida. Volviendo al menú principal.");
            return;
        }

        // Mostrar la oferta más barata para la localización elegida
        System.out.println("=== Oferta Más Barata para " + localizacionElegida + " ===");
        double tarifaMasBarata = locationRecommendationService.obtenerTarifaMasBarata(localizacionElegida, "2023-12-30"); // Supongamos que la fecha es fija
        System.out.println("Localización: " + localizacionElegida + ", Tarifa Más Barata: " + tarifaMasBarata);
    }

    private String obtenerTipoClimaSeleccionado() {
        Scanner scanner = new Scanner(System.in);

        // Tipos de clima válidos
        List<String> tiposDeClimaValidos = Arrays.asList("COLD", "RAINY", "WARM", "SNOWY", "CLEAR");

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

    private String obtenerLocalizacionElegida(Set<String> localizacionesDisponibles) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Elija una localización: ");
            String localizacionElegida = scanner.nextLine();

            // Validar la localización ingresada
            if (localizacionesDisponibles.contains(localizacionElegida)) {
                return localizacionElegida;
            } else {
                System.out.println("Localización no válida. Inténtelo de nuevo.");
            }
        }
    }
}
