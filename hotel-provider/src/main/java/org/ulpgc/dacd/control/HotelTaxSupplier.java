package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.HotelTax;
import org.ulpgc.dacd.model.Location;

import java.util.List;

public interface HotelTaxSupplier {
    List<HotelTax> getHotelTaxes(Location location, List<String> dates);
}
