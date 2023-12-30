package org.ulpgc.dacd.model;

public record Location(String name, String hotelName, double lat, double lon,  String hotelKey) {

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", apiHotelsToken='" + hotelKey + '\'' +
                '}';
    }
}
