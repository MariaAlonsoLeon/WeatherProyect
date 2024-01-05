package org.ulpgc.dacd.view.model;

public record HotelOffer(String hotelName, String companyName, double cost) {
    @Override
    public String toString() {
        return "HotelOffer{" +
                "hotelName='" + hotelName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", cost=" + cost +
                '}';
    }
}
