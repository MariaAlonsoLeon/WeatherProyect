package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.HotelTax;
import org.ulpgc.dacd.model.Location;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelTaxController {
    private static final Logger logger = Logger.getLogger(HotelTaxController.class.getName());
    private final List<Location> locations;
    private final HotelTaxSupplier hotelPricesSupplier;
    private final HotelTaxStore hotelStore;

    public HotelTaxController(HotelTaxSupplier hotelPricesSupplier, HotelTaxStore hotelStore) {
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
        List<String> dates = generateDateList();
        try{
            for (Location location : locations) {
                for (HotelTax hotelTax : hotelPricesSupplier.getHotelTaxes(location, dates)) {
                    System.out.println(hotelTax);
                    hotelStore.save(hotelTax);
                }
            }
        } catch (StoreException e){
            logger.log(Level.SEVERE, "Error sending events: " + e.getMessage(), e);
        }
    }

    private List<String> generateDateList() {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate tomorrow = LocalDate.now().plusDays(2);

        for (int i = 0; i < 5; i++) {
            dateList.add(tomorrow.plusDays(i).format(formatter));
        }

        return dateList;
    }
    private static List<Location> loadLocations() {
        List<Location> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("locations.tsv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 5) {
                    String name = parts[0];
                    String hotelName = parts[3];
                    double latitude = Double.parseDouble(parts[1]);
                    double longitude = Double.parseDouble(parts[2]);
                    String hotelKey = parts[4];
                    locations.add(new Location(name, hotelName, latitude, longitude, hotelKey));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return locations;
    }
}

