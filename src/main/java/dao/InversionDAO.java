package dao;

import model.Inversion;
import util.ConexionSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InversionDAO {

    /// METODO PARA CREAR UNA INVERSION
    public void crearInversion(Inversion i) {
        String sql = """
            INSERT INTO inversion 
            (portafolio_id, nombre, precio_compra, fecha_compra, cantidad, precio_dolar, precio_accion_en_dolar, comision_compra, es_dolar, es_venta)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConexionSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, i.getPortafolioId());
            ps.setString(2, i.getNombre());
            ps.setBigDecimal(3, i.getPrecioCompra());
            ps.setDate(4, Date.valueOf(i.getFechaCompra()));
            ps.setInt(5, i.getCantidad());
            ps.setDouble(6, i.getPrecioDolar());
            ps.setDouble(7, i.getPrecioAccionEnDolar());
            ps.setDouble(8, i.getComisionCompra());
            ps.setBoolean(9, i.isEsDolares());
            ps.setBoolean(10, i.isVenta());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    i.setID(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// METODO PARA TRAER TODAS LAS INVERSIONES DE UN PORTAFOLIO
    public List<Inversion> getInversionesPorPortafolio(int id) {
        List<Inversion> inversiones = new ArrayList<>();
        String sql = "SELECT * FROM inversion WHERE portafolio_id = ?";

        try (Connection conn = ConexionSQL.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Inversion i = new Inversion();
                    i.setID(rs.getInt("id"));
                    i.setPortafolioId(rs.getInt("portafolio_id"));
                    i.setNombre(rs.getString("nombre"));
                    i.setPrecioCompra(rs.getBigDecimal("precio_compra"));
                    i.setFechaCompra(rs.getDate("fecha_compra").toLocalDate());
                    i.setCantidad(rs.getInt("cantidad"));
                    i.setPrecioDolar(rs.getDouble("precio_dolar"));
                    i.setPrecioAccionEnDolar(rs.getDouble("precio_accion_en_dolar"));
                    i.setComisionCompra(rs.getDouble("comision_compra"));
                    i.setEnDolares(rs.getBoolean("es_dolar"));
                    i.setEsVenta(rs.getBoolean("es_venta"));

                    inversiones.add(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inversiones;
    }

    /// METODO PARA ELIMINAR TODAS LAS INVERSIONES DE UN PORTAFOLIO
    public void eliminarInversionesPorPortafolio(int portafolioId) throws SQLException {
        String sql = "DELETE FROM inversion WHERE portafolio_id = ?";
        try (Connection conn = ConexionSQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, portafolioId);
            stmt.executeUpdate();
        }
    }

    ///  METODO PARA TRAER LOS MOVIMIENTOS DE X ACCION
    public List<Inversion> getInversionesPorNombre(String nombre) {
        List<Inversion> inversiones = new ArrayList<>();
        String sql = "SELECT * FROM inversion WHERE nombre = ?";
        try (Connection conn = ConexionSQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Inversion i = new Inversion();
//                i.setID(rs.getInt("id"));
//                i.setPortafolioId(rs.getInt("portafolio_id"));
                i.setNombre(rs.getString("nombre"));
                i.setPrecioCompra(rs.getBigDecimal("precio_compra"));
                i.setFechaCompra(rs.getDate("fecha_compra").toLocalDate());
                i.setCantidad(rs.getInt("cantidad"));
                i.setPrecioDolar(rs.getDouble("precio_dolar"));
                i.setPrecioAccionEnDolar(rs.getDouble("precio_accion_en_dolar"));
                i.setComisionCompra(rs.getDouble("comision_compra"));
                i.setEnDolares(rs.getBoolean("es_dolar"));
                i.setEsVenta(rs.getBoolean("es_venta"));

                inversiones.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inversiones;
    }
}
