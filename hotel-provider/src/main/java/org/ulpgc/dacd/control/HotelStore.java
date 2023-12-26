package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.HotelTaxes;

public interface HotelStore {
    void save(HotelTaxes weather) throws StoreException;
}
