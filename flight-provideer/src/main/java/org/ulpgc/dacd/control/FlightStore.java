package org.ulpgc.dacd.control;


import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.Flight;

public interface FlightStore {

    void save(Flight flight) throws StoreException;
}
