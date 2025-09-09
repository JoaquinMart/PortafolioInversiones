package model;

import java.math.BigDecimal;

public class InversionSimple {
    private String nombre;
    private BigDecimal ultimoValor;

    public InversionSimple() {}
    public InversionSimple(String nombre, BigDecimal ultimoValor) {
        this.nombre = nombre;
        this.ultimoValor = ultimoValor;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getUltimoValor() { return ultimoValor; }
    public void setUltimoValor(BigDecimal ultimoValor) { this.ultimoValor = ultimoValor; }
}
