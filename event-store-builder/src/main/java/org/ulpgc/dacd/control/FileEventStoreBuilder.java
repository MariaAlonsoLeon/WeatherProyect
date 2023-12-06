package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class FileEventStoreBuilder implements EventStoreBuilder {

    private static final String EVENTSTORE_DIRECTORY = "\\eventstore7\\prediction.Weather\\";
    private final String baseDirectory;

    public FileEventStoreBuilder(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void save(String message) {
        String ss = getJsonValue(message, "ss");
        String dateString = getDateString(message);
        createDirectoryIfNotExists(ss);
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
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private void createDirectoryIfNotExists(String ss) {
        String directoryPath = Paths.get(baseDirectory, EVENTSTORE_DIRECTORY, ss).toString();
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory created: " + directoryPath);
            } else {
                System.err.println("Failed to create directory: " + directoryPath);
            }
        }
    }

    private String getJsonValue(String message, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
            if (jsonObject.has(key)) {
                return jsonObject.get(key).getAsString();
            }
        } catch (Exception e) {
            handleJsonParseException(e);
        }
        return "";
    }

    private void handleJsonParseException(Exception e) {
        e.printStackTrace();
    }

    private void handleIOException(IOException e) {
        e.printStackTrace();
    }
}
