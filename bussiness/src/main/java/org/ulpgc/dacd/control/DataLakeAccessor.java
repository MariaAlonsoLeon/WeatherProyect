package org.ulpgc.dacd.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Path;

public class DataLakeAccessor {
    private static final Logger logger = Logger.getLogger(DataLakeAccessor.class.getName());
    private final String dataLakeDirectory;

    public DataLakeAccessor(String dataLakeDirectory) {
        this.dataLakeDirectory = dataLakeDirectory;
    }

    public List<String> getWeatherData() {
        return getLatestEventData("prediction.Weather");
    }

    public List<String> getFlightData() {
        return getLatestEventData("prediction.Flight");
    }

    private List<String> getLatestEventData(String topic) {
        try {
            String eventStoreDirectory = Paths.get(dataLakeDirectory, "eventstore", topic).toString();
            List<String> eventFiles = findEventFiles(eventStoreDirectory);
            if (!eventFiles.isEmpty()) {
                String latestEventFile = Collections.max(eventFiles);
                return readEventsFromFile(Paths.get(eventStoreDirectory, latestEventFile).toString());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading latest event data", e);
        }
        return Collections.emptyList();
    }

    private List<String> findEventFiles(String eventStoreDirectory) {
        List<String> eventFiles = new ArrayList<>();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(eventStoreDirectory), "*.events")) {
            for (Path path : directoryStream) {
                eventFiles.add(path.getFileName().toString());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading event files", e);
        }

        return eventFiles;
    }

    private List<String> readEventsFromFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }
}