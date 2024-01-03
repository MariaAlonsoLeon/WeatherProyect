package org.ulpgc.dacd.view;

import java.util.Optional;

public class Offer {
    private final String localizacion;
    private final String fechaReserva;
    private final Optional<Double> tarifa;

    public Offer(String localizacion, String fechaReserva, Optional<Double> tarifa) {
        this.localizacion = localizacion;
        this.fechaReserva = fechaReserva;
        this.tarifa = tarifa;
    }
}
