package org.ulpgc.dacd.control;


import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.Flight;
import org.ulpgc.dacd.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightController {
    private static final Logger logger = Logger.getLogger(FlightController.class.getName());
    private final List<Location> locations;
    private final FlightSupplier flightSupplier;
    private final FlightStore flightStore;

    public FlightController(FlightSupplier flightSupplier, FlightStore flightStore) {
        this.locations = loadLocations();
        this.flightSupplier = flightSupplier;
        this.flightStore = flightStore;
    }

    public void execute() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fetchAndStoreFlights();
                logger.info("Flight data update completed.");
            }
        };
        timer.schedule(task, 0, 6 * 60 * 60 * 1000);
    }

    private void fetchAndStoreFlights() {
        try {
            List<Location> locations = loadLocations();
            Location firstLocation = locations.get(0);

            for (Location location : locations) {
                if (!firstLocation.equals(location)) {
                    System.out.println(firstLocation + "-" + location);
                    for (Flight flight : flightSupplier.getFlights(firstLocation, location)) {
                        System.out.println(flight);
                        flightStore.save(flight);
                        //System.out.println("fin?");
                    }
                }
            }
        } catch (StoreException e) {
            logger.log(Level.SEVERE, "Error sending events: " + e.getMessage(), e);
        }
    }

    /*private void fetchAndStoreFlights() {
        try {
            List<Location> departureLocations = loadLocations();
            List<Location> arrivalLocations = loadLocations(); // Usar la misma lista para salidas y llegadas
            for (Location departure : departureLocations) {
                System.out.println(departure);
                for (Location arrival : arrivalLocations) {
                    if (!departure.equals(arrival)) {
                        System.out.println(departure + "-" + arrival);
                        for (Flight flight : flightSupplier.getFlights(departure, arrival)) {
                            System.out.println(flight);
                            flightStore.save(flight);
                            System.out.println("fin?");
                        }
                    }
                }
            }
        } catch (StoreException e) {
            logger.log(Level.SEVERE, "Error sending events: " + e.getMessage(), e);
        }
    }*/
    private static List<Location> loadLocations() {
        List<Location> locations = new ArrayList<>();

        // TODO pass this info to a tsv
        locations.add(new Location("LPA", 27.9799, -15.6174));  // Gran Canaria Airport
        locations.add(new Location("TFN", 28.4827, -16.3415));  // Tenerife North Airport
        //locations.add(new Location("GMZ", 28.0262, -17.2141));  // La Gomera Airport
        //locations.add(new Location("SPC", 28.6262, -17.7556));  // La Palma Airport
        //locations.add(new Location("VDE", 27.8148, -17.8870));  // El Hierro Airport
        //locations.add(new Location("FUE", 28.4524, -13.8638));  // Fuerteventura Airport
        //locations.add(new Location("ACE", 28.9455, -13.6059));  // Lanzarote Airport
        //locations.add(new Location("TEA", 29.4567, -13.5084));  // La Graciosa

        return locations;
    }
}
