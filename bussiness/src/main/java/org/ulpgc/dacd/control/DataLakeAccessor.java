package org.ulpgc.dacd.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLakeAccessor {
    private static final Logger logger = Logger.getLogger(DataLakeAccessor.class.getName());
    private final String dataLakeDirectory;;

    public DataLakeAccessor(String dataLakeDirectory) {
        this.dataLakeDirectory = dataLakeDirectory;
    }

    public List<String> getWeathers() {
        return getAllLatestEvent("prediction.Weather", "weather-provider");
    }

    public List<String> getHotelOffers() {
        return getAllLatestEvent("prediction.Hotel", "hotel-provider");
    }

    private String getLastestDate(String topic, String ss) throws IOException {
        Path eventStorePath = Paths.get(dataLakeDirectory, "eventstore", topic, ss);
        try (Stream<Path> filesStream = Files.list(eventStorePath)) {
            List<String> files = filesStream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            return files.stream().max(Comparator.naturalOrder()).orElse(null);
        }
    }

    private List<String> getAllLatestEvent(String topic, String ss) {
        try {
            String eventStoreDirectory = getEventStoreDirectory(topic, ss);
            return readAllLatestEventsFromFile(eventStoreDirectory);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading all latest event data", e);
        }
        return Collections.emptyList();
    }

    private String getEventStoreDirectory(String topic, String ss) throws IOException {
        String date = getLastestDate(topic, ss);
        String eventStoreDirectory = Paths.get(dataLakeDirectory, "eventstore", topic, ss, date).toString();
        return eventStoreDirectory;
    }

    private List<String> readAllLatestEventsFromFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }
}
