package org.ulpgc.dacd.model;

import java.util.List;

public record Location(String name, String hotelName, double lat, double lon,  String apiHotelsToken) {

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", apiHotelsToken='" + apiHotelsToken + '\'' +
                '}';
    }
}
