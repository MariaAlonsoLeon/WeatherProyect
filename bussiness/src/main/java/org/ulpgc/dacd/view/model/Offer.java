package org.ulpgc.dacd.view.model;

import java.util.List;
public record Offer(String dt, String weatherType, List<LocationOfferByWeather> locationOfferByWeathers) implements Output{
}
