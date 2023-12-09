package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.time.Instant;
import java.util.List;

public interface WeatherSupplier {
    List<Weather> getWeathers(Location location);
}
