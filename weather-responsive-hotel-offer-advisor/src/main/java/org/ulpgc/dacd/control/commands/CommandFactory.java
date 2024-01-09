package org.ulpgc.dacd.control.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private final Map<String, CommandFactory.Constructor<Command>> commands = new HashMap<>();

    public CommandFactory(String dbPath) {
        commands.put("getCheapestOffersByWeatherAndDate", () -> new GetCheapestOffersByWeatherAndDateCommand(dbPath));
        commands.put("getLocationsByWeatherAndDate", () -> new GetLocationsByWeatherAndDateCommand(dbPath));
        commands.put("getCheapestOfferByLocationAndDate", () -> new GetCheapestOfferByLocationAndDateCommand(dbPath));
    }

    public Command create(String type) {
        return commands.get(type).create();
    }

    public interface Constructor<T> {
        T create();
    }
}
