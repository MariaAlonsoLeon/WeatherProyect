package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.HotelTax;

public interface HotelTaxStore {
    void save(HotelTax weather) throws StoreException;
}
