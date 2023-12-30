package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.StoreException;
import org.ulpgc.dacd.model.HotelOffer;

public interface HotelOfferStore {
    void save(HotelOffer hotelOffer) throws StoreException;
}
