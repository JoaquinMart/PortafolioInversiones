package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Portafolio {
    private int id;
    private String nombre;
    private LocalDate fechaCreacion;
    private List<Inversion> inversiones;

    public Portafolio() {
        this.inversiones = new ArrayList<>();
    }
    public Portafolio(String nombre) {
        this.nombre = nombre;
        this.inversiones = new ArrayList<>();
    }
    public Portafolio(int id, String nombre) { // SOLO DE PRUEBA
        this.id = id;
        this.nombre = nombre;
        this.inversiones = new ArrayList<>();
    }

    public void setID(int id) { this.id = id; }
    public int getId() { return this.id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }

    public List<Inversion> getInversiones() { return inversiones; }
    public void setInversiones(List<Inversion> inversiones) { this.inversiones = inversiones; }
    public void addInversion(Inversion inversion) { this.inversiones.add(inversion); }

    @Override
    public String toString() { return this.nombre; }

}
