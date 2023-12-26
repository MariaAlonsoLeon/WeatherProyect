package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Flight;
import org.ulpgc.dacd.model.Location;

import java.util.List;

public interface FlightSupplier {
    List<Flight> getFlights(Location departure, Location arrival);
}
