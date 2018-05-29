package com.ryanair.connections.connections;

import java.util.List;

public class ConexionesVuelos {

    private int stops;
    private List<CaracteristicasVuelos> legs;

    public ConexionesVuelos(int stops, List<CaracteristicasVuelos> caracteristicasVuelos) {
        super();
        this.stops = stops;
        this.legs = caracteristicasVuelos;
    }

    public int getStops() {
        return stops;
    }
    public void setStops(int stops) {
        this.stops = stops;
    }
    public List<CaracteristicasVuelos> getCaracteristicasVuelos() {
        return legs;
    }
    public void setCaracteristicasVuelos(List<CaracteristicasVuelos> caracteristicasVuelos) {
        this.legs = caracteristicasVuelos;
    }

}
