package org.ulpgc.dacd.control;

import org.example.control.exceptions.StoreException;
import org.example.model.Flight;

public interface FlightStore {

    void save(Flight flight) throws StoreException;
}
