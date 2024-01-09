package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.handlers.Handler;
import java.util.List;
import java.util.Map;

public class DataMartBuilder {
    private final DataLakeAccessor dataLakeAccessor;
    private final Map<String, Handler> handlers;

    public DataMartBuilder(DataLakeAccessor dataLakeAccessor, Map<String, Handler> handlers) {
        this.dataLakeAccessor = dataLakeAccessor;
        this.handlers = handlers;

    }

    public void buildDataMart() {
        List<String> weatherData = dataLakeAccessor.getWeathers();
        List<String> hotelData = dataLakeAccessor.getHotelOffers();
        processWeathers(weatherData);
        processHotelOffers(hotelData);
    }

    private void processWeathers(List<String> weathers) {
        for (String weather : weathers) {
            handlers.get("prediction.Weather").handleEvent(weather);
        }
    }

    private void processHotelOffers(List<String> hotelOffers) {
        for (String hotelOffer : hotelOffers) {
            handlers.get("prediction.Hotel").handleEvent(hotelOffer);
        }
    }

}
