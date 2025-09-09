package model;

import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Inversion {
    private int id;
    private String nombre;
    private int portafolioId;

    private boolean esDolar;
    private boolean esVenta;

    // API IOL
    private SimpleObjectProperty<BigDecimal> precioAccion = new SimpleObjectProperty<>(BigDecimal.ZERO);

    // COMPRA
    private BigDecimal precioCompra;
    private LocalDate fechaCompra;
    private int cantidad;
    private double precioDolar;
    private double precioAccionEnDolar;
    private double comisionCompra;

    // RESUMEN
    private int cantidadTotal;
    private double precioHoy;
    private BigDecimal precioTotalPesos;
    private BigDecimal precioTotalDolares;
    private double porcentajeTotal;

    public Inversion() {}

    /// ID
    public void setID(int id) { this.id = id; } // OK
    public int getId() { return this.id; }
    public int getPortafolioId() { return portafolioId; }
    public void setPortafolioId(int portafolioId) { this.portafolioId = portafolioId; } // OK

    /// FECHA COMPRA
    public LocalDate getFechaCompra() { return fechaCompra; } // OK
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; } // OK

    /// NOMBRE
    public String getNombre() { return nombre; } // OK
    public void setNombre(String nombre) { this.nombre = nombre; } // OK

    /// PRECIO COMPRA
    public BigDecimal getPrecioCompra() { return precioCompra; } // OK
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; } // OK

    /// PRECIO ACCION
    public BigDecimal getPrecioAccion() { return precioAccion.get(); } // OK
    public void setPrecioAccion(BigDecimal valor) { precioAccion.set(valor); } // OK
    public SimpleObjectProperty<BigDecimal> precioAccionProperty() { return precioAccion; }

    /// PRECIO ACCION HOY
    public void setPrecioAccionHoy(double precio) { this.precioHoy = precio;}

    /// PRECIO ACCION EN DOLARES
    public double getPrecioAccionEnDolar() { return precioAccionEnDolar; } // OK
    public void setPrecioAccionEnDolar(double precioAccionEnDolar) { this.precioAccionEnDolar = precioAccionEnDolar; }

    /// PRECIO DOLAR
    public double getPrecioDolar() { return precioDolar; } // OK
    public void setPrecioDolar(double precioDolar) { this.precioDolar = precioDolar; } // OK

    /// BOOLEAN DOLARES
    public void setEnDolares(boolean estado) { this.esDolar = estado; } // OK
    public boolean isEsDolares() { return this.esDolar; };

    /// BOOLEAN VENTA
    public boolean isVenta() { return esVenta; }
    public void setEsVenta(boolean esVenta) { this.esVenta = esVenta; }

    /// CANTIDAD
    public int getCantidad() { return cantidad;} // OK
    public void setCantidad(int cantidad) { this.cantidad = cantidad; } // OK

    /// TOTAL PESOS
    public BigDecimal getTotalPesos() {
        return (precioAccion.get() != null ? precioAccion.get() : BigDecimal.ZERO)
                .multiply(new BigDecimal(cantidad));
    } // OK

    /// RENDIMIENTO PESOS
    public double getRendimientoPesos() { return getTotalPesos().doubleValue() / precioCompra.doubleValue(); }

    /// TOTAL DOLARES
    public double getTotalEnDolares() { return precioAccionEnDolar * cantidad; } // OK

    /// RENDIMIENTO EN DOLARES
    public double getRendimientoDolar() { return getTotalEnDolares() / precioAccionEnDolar; }

    /// COMISIONES
    public double getComisionCompra() { return comisionCompra; } // OK
    public void setComisionCompra(double comisionCompra) { this.comisionCompra = comisionCompra; } // OK
}
