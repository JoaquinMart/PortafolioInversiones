package model;

import javafx.beans.property.SimpleObjectProperty;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class InversionAgrupada {
    private String nombre;
    private int cantidadTotal;
    private double totalPesos;
    private double totalDolares;
    private double precioPromedio;
    private SimpleObjectProperty<BigDecimal> precioAccion = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private static double dolarMepActual = 0.0;
    private double porcentajePromedio;
    private boolean enDolares;
    private double totalPesosCompras = 0.0;   // suma solo compras en pesos
    private double totalDolaresCompras = 0.0; // suma solo compras en dólares
    private int cantidadComprada = 0;

    public InversionAgrupada(String nombre) { this.nombre = nombre; }

    public static void setDolarMepActual(double valor) { dolarMepActual = valor; } // OK
    public static double getPrecioDolar() { return dolarMepActual; } // OK

    public String getNombre() { return nombre; } // OK
    public BigDecimal getPrecioCompra() { return BigDecimal.valueOf(precioPromedio); }
    public BigDecimal getPrecioCompraPromedio() {
        return BigDecimal.valueOf(precioPromedio).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPrecioAccion() { return precioAccion.get(); }
    public SimpleObjectProperty<BigDecimal> precioAccionProperty() { return precioAccion; }
    public void setPrecioAccion(BigDecimal valor) { precioAccion.set(valor); }
    public double getPrecioAccionHoy() { return getPrecioAccion() != null ? getPrecioAccion().doubleValue() : 0.0; } // OK

    // Deberia traer el precio de hoy del dolar bolsa / mep
    public double getTotalEnDolares() { return totalDolares; }
    public int getCantidad() { return cantidadTotal; } // OK
    public double getTotalInicial() { return enDolares ? totalDolaresCompras : totalPesosCompras; }// OK
    public double getTotalPesos() { return totalPesos; }

    public double getRendimiento() {
        if (getTotalInicial() == 0 || getPrecioAccion() == null) return 0.0;

        BigDecimal valorActualTotal = getPrecioAccion().multiply(BigDecimal.valueOf(cantidadTotal));
        BigDecimal totalInvertido = BigDecimal.valueOf(getTotalInicial());

        return valorActualTotal
                .divide(totalInvertido, 6, RoundingMode.HALF_UP)
                .subtract(BigDecimal.ONE)
                .doubleValue() * 100;
    }

//    public double getRendimientoPesos() {
//        if (totalPesos == 0 || getPrecioAccion() == null) return 0.0;
//
//        BigDecimal valorActualTotal = getPrecioAccion().multiply(BigDecimal.valueOf(cantidadTotal));
//        BigDecimal totalInvertido = BigDecimal.valueOf(totalPesos);
//
//        return valorActualTotal.divide(totalInvertido, 6, RoundingMode.HALF_UP)
//                .subtract(BigDecimal.ONE)
//                .doubleValue()
//                 * 100;
//    } // OK
//
//    public double getRendimientoDolar() {
//        // Si no hay precio promedio o no hay precio actual, no hay rendimiento
//        if (precioPromedio == 0.0) {
//            return 0.0;
//        }
//
//        BigDecimal precioActual = BigDecimal.valueOf(getPrecioAccionHoy());
//        BigDecimal promedio = BigDecimal.valueOf(precioPromedio);
//
//        return precioActual
//                .divide(promedio, 6, RoundingMode.HALF_UP)
//                .subtract(BigDecimal.ONE)
//                .doubleValue() * 100;
//    }

    public double getTotalEnDolaresInicial() { return enDolares ? totalDolaresCompras : 0.0; }
    public double getTotalDolares() { return totalDolares; }
    public double getRendimientoEnDolares() { return totalDolares / precioPromedio; }

    public double getPorcentajePromedio() { return porcentajePromedio; }

    public void acumular(Inversion i) {
        cantidadTotal += i.isVenta() ? -Math.abs(i.getCantidad()) : i.getCantidad();
        if (i.isEsDolares()) {
            enDolares = true;
            double precioCompra = i.getPrecioAccionEnDolar();
            if (!i.isVenta()) {
                cantidadComprada += i.getCantidad();
                totalDolaresCompras += i.getCantidad() * precioCompra;
                precioPromedio = totalDolaresCompras / cantidadComprada;
            } else {
                int cantVenta = Math.abs(i.getCantidad());
                cantidadComprada -= cantVenta;
                totalDolaresCompras -= cantVenta * precioPromedio;
                precioPromedio = cantidadComprada > 0 ? totalDolaresCompras / cantidadComprada : 0;
            }
        } else {
            enDolares = false;
            double precioCompra = i.getPrecioCompra().doubleValue();

            if (!i.isVenta()) {
                // Compra
                cantidadComprada += i.getCantidad();
                totalPesosCompras += i.getCantidad() * precioCompra;
                precioPromedio = totalPesosCompras / cantidadComprada;
            } else {
                // Venta: reducimos subtotal según precio promedio
                int cantVenta = Math.abs(i.getCantidad());
                cantidadComprada -= cantVenta;
                totalPesosCompras -= cantVenta * precioPromedio; // siempre con precio promedio actual
                precioPromedio = cantidadComprada > 0 ? totalPesosCompras / cantidadComprada : 0;
            }
        }

        // Esto NO se toca, así seguimos mostrando el precio actual de la API
        if (i.getPrecioAccion() != null) {
            setPrecioAccion(i.getPrecioAccion());
        }
    }

    public boolean isEnDolares() {
        return enDolares;
    }
}
