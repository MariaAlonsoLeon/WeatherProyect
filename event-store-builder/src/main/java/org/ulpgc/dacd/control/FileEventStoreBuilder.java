package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileEventStoreBuilder implements EventStoreBuilder {

    private static final String EVENTSTORE_DIRECTORY = "\\eventstore\\prediction.Weather\\";
    private final String baseDirectory;
    private static final Logger logger = Logger.getLogger(TopicSubscriber.class.getName());

    public FileEventStoreBuilder(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void save(String message) {
        String ss = getJsonValue(message, "ss");
        String dateString = getDateString(message);
        createDirectory(ss);
        String filePath = buildFilePath(ss, dateString);
        writeEventToFile(message, filePath);
    }

    private String getDateString(String message) {
        Instant ts = Instant.parse(getJsonValue(message, "ts"));
        return new SimpleDateFormat("yyyyMMdd").format(Date.from(ts));
    }

    private String buildFilePath(String ss, String dateString) {
        return Paths.get(baseDirectory, EVENTSTORE_DIRECTORY, ss, dateString + ".events").toString();
    }

    private void writeEventToFile(String message, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write event to file: " + filePath, e);
        }
    }

    private void createDirectory(String ss) {
        String directoryPath = Paths.get(baseDirectory, EVENTSTORE_DIRECTORY, ss).toString();
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info("Directory created: " + directoryPath);
            } else {
                logger.warning("Failed to create directory: " + directoryPath);
            }
        }
    }

    private String getJsonValue(String message, String key) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        return jsonObject.get(key).getAsString();
    }
}
