package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.HotelOffer;
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

public class HotelOfferController {
    private static final Logger logger = Logger.getLogger(HotelOfferController.class.getName());
    private final List<Location> locations;
    private final HotelOfferSupplier hotelOfferSupplier;
    private final HotelOfferStore hotelStore;
    private final String locationsFilePath;

    public HotelOfferController(HotelOfferSupplier hotelOfferSupplier, HotelOfferStore hotelStore, String locationsFilePath) {
        this.locationsFilePath = locationsFilePath;
        this.locations = loadLocations();
        this.hotelOfferSupplier = hotelOfferSupplier;
        this.hotelStore = hotelStore;
    }

    public void execute() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                fetchAndStoreHotelPrices();
                logger.info("Hotel offers update completed.");
            }
        };
        timer.schedule(task, 0, 24 * 60 * 60 * 1000);
    }

    private void fetchAndStoreHotelPrices() {
        List<String> dates = generateDateList();
        try{
            for (Location location : locations) {
                for (HotelOffer hotelOffer : hotelOfferSupplier.getHotelOffers(location, dates)) {
                    hotelStore.save(hotelOffer);
                }
            }
        } catch (StoreException e){
            logger.log(Level.SEVERE, "Error sending events: " + e.getMessage(), e);
        }
    }

    private List<String> generateDateList() {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now().plusDays(2);

        for (int i = 0; i < 5; i++) {
            dateList.add(currentDate.plusDays(i).format(formatter));
        }
        System.out.println(dateList);
        return dateList;
    }
    private List<Location> loadLocations() {
        List<String> lines = readLinesFromFile(this.locationsFilePath);
        return parseLinesToLocations(lines);
    }

    private static List<String> readLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static List<Location> parseLinesToLocations(List<String> lines) {
        List<Location> locations = new ArrayList<>();
        for (String line : lines) {
            Location location = parseLineToLocation(line);
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }

    private static Location parseLineToLocation(String line) {
        String[] parts = line.split("\t");
        if (parts.length >= 3) {
            String name = parts[0];
            String hotelName = parts[3];
            String hotelKey = parts[4];
            return new Location(name, hotelName, hotelKey);
        }
        return null;
    }
}

