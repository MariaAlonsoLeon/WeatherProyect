package org.ulpgc.dacd.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLakeAccessor {
    private static final Logger logger = Logger.getLogger(DataLakeAccessor.class.getName());
    private final String dataLakeDirectory;
    private static final int MAX_EVENTS = 40;

    public DataLakeAccessor(String dataLakeDirectory) {
        this.dataLakeDirectory = dataLakeDirectory;
    }

    public List<String> getWeatherData() {
        return getLatestEventData("prediction.Weather", "weather-provider", MAX_EVENTS);
    }

    public List<String> getHotelData() {
        return getAllLatestEventData("prediction.Hotel", "hotel-provider");
    }

    private List<String> getLatestEventData(String topic, String ss, int maxEvents) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String today = getLastestDate(topic, ss);
            System.out.println(today);
            String eventStoreDirectory = Paths.get(dataLakeDirectory, "eventstore", topic, ss, today).toString();
            return readLatestEventsFromFile(eventStoreDirectory, maxEvents);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading latest event data", e);
        }
        return Collections.emptyList();
    }

    private String getLastestDate(String topic, String ss) throws IOException {
        Path eventStorePath = Paths.get(dataLakeDirectory, "eventstore", topic, ss);

        try (Stream<Path> filesStream = Files.list(eventStorePath)) {
            List<String> files = filesStream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            return files.stream()
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }
    }

    private List<String> getAllLatestEventData(String topic, String ss) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String today = getLastestDate(topic, ss);

            String eventStoreDirectory = Paths.get(dataLakeDirectory, "eventstore", topic, ss, today).toString();

            return readAllLatestEventsFromFile(eventStoreDirectory);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading all latest event data", e);
        }
        return Collections.emptyList();
    }

    private List<String> readLatestEventsFromFile(String filePath, int maxEvents) throws IOException {
        List<String> allEvents = Files.readAllLines(Paths.get(filePath));

        // Obtener los últimos 'maxEvents' eventos (o todos si hay menos de 'maxEvents')
        int startIndex = Math.max(0, allEvents.size() - maxEvents);
        return allEvents.subList(startIndex, allEvents.size());
    }

    private List<String> readAllLatestEventsFromFile(String filePath) throws IOException {
        // Lee todos los eventos desde el archivo
        return Files.readAllLines(Paths.get(filePath));
    }
}
