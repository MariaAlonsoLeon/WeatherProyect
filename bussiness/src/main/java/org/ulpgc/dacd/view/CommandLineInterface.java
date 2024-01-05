package org.ulpgc.dacd.view;

public class CommandLineInterface {
    //private final LocationRecommendationService locationRecommendationService;

    /*public CommandLineInterface(LocationRecommendationService locationRecommendationService) {
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
        String tipoClimaElegido = obtenerTipoClimaSeleccionado();

        if (tipoClimaElegido == null) {
            System.out.println("Tipo de clima no válido. Volviendo al menú principal.");
            return;
        }

        System.out.println("=== Localizaciones con Clima " + tipoClimaElegido + " ===");
        Set<String> listaDeLocalizaciones = locationRecommendationService.getLocationsByWeatherType(tipoClimaElegido, "2023-12-31");

        if (listaDeLocalizaciones.isEmpty()) {
            System.out.println("No hay localizaciones con el tipo de clima seleccionado.");
            return;
        } else {
            System.out.println("Localizaciones disponibles:");
            for (String location : listaDeLocalizaciones) {
                System.out.println(location);
            }
        }

        String localizacionElegida = obtenerLocalizacionElegida(listaDeLocalizaciones);

        if (localizacionElegida == null) {
            System.out.println("Localización no válida. Volviendo al menú principal.");
            return;
        }

        String fechaReserva = obtenerFechaReserva();

        if (fechaReserva == null) {
            System.out.println("Fecha de reserva no válida. Volviendo al menú principal.");
            return;
        }

        System.out.println("=== Oferta Más Barata para " + localizacionElegida + " ===");
        double tarifaMasBarata = locationRecommendationService.getCheapestRate(localizacionElegida, fechaReserva);
        System.out.println("Localización: " + localizacionElegida + ", Fecha de Reserva: " + fechaReserva + ", Tarifa Más Barata: " + tarifaMasBarata);
    }

    private String obtenerTipoClimaSeleccionado() {
        Scanner scanner = new Scanner(System.in);
        List<String> tiposDeClimaValidos = Arrays.asList("COLD", "RAINY", "WARM", "SNOWY", "CLEAR");

        while (true) {
            System.out.print("Seleccione el tipo de clima: ");
            String tipoClima = scanner.nextLine();

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

            if (localizacionesDisponibles.contains(localizacionElegida)) {
                return localizacionElegida;
            } else {
                System.out.println("Localización no válida. Inténtelo de nuevo.");
            }
        }
    }

    private String obtenerFechaReserva() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Ingrese la fecha de reserva (YYYY-MM-DD): ");
            String fechaReserva = scanner.nextLine();

            if (validarFormatoFecha(fechaReserva)) {
                return fechaReserva;
            } else {
                System.out.println("Formato de fecha no válido. Inténtelo de nuevo.");
            }
        }
    }

    private boolean validarFormatoFecha(String fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(fecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }*/
}

