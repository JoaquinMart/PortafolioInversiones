package service;

import model.Portafolio;

public class PortafolioService {
    private int nextId = 1;

    public Portafolio crearPortafolio(String nombre) {
        return new Portafolio(nextId++, nombre);
    }
}
