package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.HotelTaxes;
import org.ulpgc.dacd.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelPricesController {
    private static final Logger logger = Logger.getLogger(HotelPricesController.class.getName());
    private final List<Location> locations;
    private final HotelSupplier hotelPricesSupplier;
    private final HotelStore hotelStore;

    public HotelPricesController(HotelSupplier hotelPricesSupplier, HotelStore hotelStore) {
        this.locations = loadLocations();
        this.hotelPricesSupplier = hotelPricesSupplier;
        this.hotelStore = hotelStore;
    }

    public void execute() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fetchAndStoreHotelPrices();
                logger.info("HotelPrices data update completed.");
            }
        };
        timer.schedule(task, 0, 6 * 60 * 60 * 1000);
    }

    private void fetchAndStoreHotelPrices() {
        try{
            for (Location location : locations) {
                for (HotelTaxes hotelTax : hotelPricesSupplier.getHotelPrices(location)) {
                    System.out.println(hotelTax);
                    hotelStore.save(hotelTax);
                }
            }
        } catch (StoreException e){
            logger.log(Level.SEVERE, "Error sending events: " + e.getMessage(), e);
        }
    }

    private static List<Location> loadLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location("Gran Canaria", "Hotel Riu Gran Canaria", 27.74044, -15.60505, "g230095-d530762"));
        locations.add(new Location("Venice", "Hotel Palladio", 37.83606, 15.27274, "g187870-d615183"));
        locations.add(new Location("Madrid", "Hotel Riu Plaza España", 40.42414, -3.71095, "g187514-d15235805"));
        locations.add(new Location("Oslo", "Hotel Continental", 59.91409, 10.73352, "g190479-d232475"));
        locations.add(new Location("Andorra", "Grau Roig Andorra Boutique Hotel & Spa", 42.53398972425904, 1.7007644831819493, "g1170716-d1219390"));
        locations.add(new Location("Praia de Boca Salina", "Hotel Riu Palace Boavista ", 16.142363, -22.903287, "g482843-d15087179"));
        locations.add(new Location("London",  "Shangri-La The Shard", 51.504501, -0.086500, "g186338-d6484754"));
        locations.add(new Location("Marrakech", "La Maison Arabe", 34.02306, -6.832003, "g293734-d303075"));
        return locations;
    }
}

