package org.ulpgc.dacd.control;

import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class FileWeatherStore implements WeatherStore {

    private static final String EVENTSTORE_DIRECTORY = "\\eventstore5\\prediction.Weather\\";
    private final String baseDirectory;

    public FileWeatherStore(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void save(List<String> weatherJsonList) {
        System.out.println("Prueba");
        String weatherJson = weatherJsonList.get(0);
        String ss = getJsonValue(weatherJson, "ss");
        Instant ts = Instant.parse(getJsonValue(weatherJson, "ts"));
        String dateString = new SimpleDateFormat("yyyyMMdd").format(Date.from(ts)); // Convierte la fecha a YYYYMMDD
        createDirectoryIfNotExists(ss);
        String filePath = Paths.get(baseDirectory, EVENTSTORE_DIRECTORY, ss, dateString + ".events").toString();
        try {
            for (String weatherEvent : weatherJsonList) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(weatherEvent + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getJsonValue(String json, String key) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            if (jsonObject.has(key)) {
                return jsonObject.get(key).getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /*@Override
    public void save(String weatherJson) {
        System.out.println("Prueba");
        try {
            Weather weather = jsonToWeather(weatherJson);

            String ss = weather.getSs();
            String dateString = new SimpleDateFormat("yyyyMMdd").format(Date.from(weather.getTs())); // Convierte la fecha a YYYYMMDD
            createDirectoryIfNotExists(ss);
            String filePath = Paths.get(baseDirectory, EVENTSTORE_DIRECTORY, ss, dateString + ".events").toString();
            try (FileWriter writer = new FileWriter(filePath, true)) {
                writer.write(weatherJson + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

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

}
