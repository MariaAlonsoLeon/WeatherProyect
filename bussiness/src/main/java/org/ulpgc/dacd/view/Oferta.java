package org.ulpgc.dacd.view;

public class Oferta {
    private final String localizacion;
    private final String fechaReserva;
    private final double tarifa;

    public Oferta(String localizacion, String fechaReserva, double tarifa) {
        this.localizacion = localizacion;
        this.fechaReserva = fechaReserva;
        this.tarifa = tarifa;
    }
}
