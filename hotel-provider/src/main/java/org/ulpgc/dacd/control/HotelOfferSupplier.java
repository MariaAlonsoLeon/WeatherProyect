package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.HotelOffer;
import org.ulpgc.dacd.model.Location;

import java.util.List;

public interface HotelOfferSupplier {
    List<HotelOffer> getHotelTaxes(Location location, List<String> dates);
}
