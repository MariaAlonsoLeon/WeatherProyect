package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import org.ulpgc.dacd.model.Weather;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class FileWeatherStore implements WeatherStore {

    private static final String EVENTSTORE_DIRECTORY = "\\eventstoreDos\\prediction.Weather\\";
    private final String baseDirectory;

    public FileWeatherStore(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void save(String weatherJson) {
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

    private Weather jsonToWeather(String jsonWeather) {
        Gson gson = prepareGson();
        try {
            return gson.fromJson(jsonWeather, Weather.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Gson prepareGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
    }

    private static class InstantTypeAdapter extends TypeAdapter<Instant> {
        @Override
        public void write(com.google.gson.stream.JsonWriter out, Instant value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public Instant read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String value = in.nextString();
            return Instant.parse(value);
        }
    }
}
