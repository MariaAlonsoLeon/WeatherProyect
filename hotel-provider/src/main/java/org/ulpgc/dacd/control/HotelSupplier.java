package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.HotelTaxes;
import org.ulpgc.dacd.model.Location;

import java.util.List;

public interface HotelSupplier {
    List<HotelTaxes> getHotelPrices(Location location);
}
