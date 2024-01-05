package org.ulpgc.dacd.view.model;

import org.ulpgc.dacd.model.HotelOfferNode;
import org.ulpgc.dacd.model.WeatherNode;

import java.util.List;
import java.util.Optional;

public record Offer(String dt, String weatherType, List<LocationOfferByWeather> locationOfferByWeathers) {
}
