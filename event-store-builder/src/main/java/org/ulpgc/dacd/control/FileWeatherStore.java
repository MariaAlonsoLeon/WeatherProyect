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
import java.util.List;

public class FileWeatherStore implements WeatherStore {

    private static final String EVENTSTORE_DIRECTORY = "\\eventstore6\\prediction.Weather\\";
    private final String baseDirectory;

    public FileWeatherStore(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void save(List<String> weatherJsonList) {
        String ss = getJsonValue(weatherJsonList.get(0), "ss");
        String dateString = getDateString(weatherJsonList.get(0));
        createDirectoryIfNotExists(ss);
        String filePath = buildFilePath(ss, dateString);
        writeWeatherEventsToFile(weatherJsonList, filePath);
    }

    private String getDateString(String weatherJson) {
        Instant ts = Instant.parse(getJsonValue(weatherJson, "ts"));
        return new SimpleDateFormat("yyyyMMdd").format(Date.from(ts));
    }

    private String buildFilePath(String ss, String dateString) {
        return Paths.get(baseDirectory, EVENTSTORE_DIRECTORY, ss, dateString + ".events").toString();
    }

    private void writeWeatherEventsToFile(List<String> weatherJsonList, String filePath) {
        try {
            for (String weatherEvent : weatherJsonList) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(weatherEvent + "\n");
                }
            }
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

    private String getJsonValue(String json, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
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
