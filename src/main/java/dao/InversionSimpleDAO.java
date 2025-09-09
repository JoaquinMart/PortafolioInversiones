package dao;

import model.InversionSimple;
import util.ConexionSQL;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InversionSimpleDAO {

    public InversionSimple getInversionPorNombre(String nombre) throws SQLException {
        String sql = "SELECT nombre, ultimoValor FROM inversionsimple WHERE nombre = ?";
        try (Connection conn = ConexionSQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    InversionSimple inv = new InversionSimple();
                    inv.setNombre(rs.getString("nombre"));
                    inv.setUltimoValor(rs.getBigDecimal("ultimoValor"));
                    return inv;
                }
            }
        }
        return null;
    }

    public Map<String, BigDecimal> getUltimosValores() throws SQLException {
        Map<String, BigDecimal> map = new HashMap<>();
        String sql = "SELECT nombre, ultimoValor FROM InversionSimple";
        try (Connection conn = ConexionSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("nombre"), rs.getBigDecimal("ultimoValor"));
            }
        }
        return map;
    }

    public List<InversionSimple> getInversiones() throws SQLException {
        List<InversionSimple> lista = new ArrayList<>();
        String sql = "SELECT nombre, ultimoValor FROM InversionSimple";

        try (Connection conn = ConexionSQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                InversionSimple inv = new InversionSimple();
                inv.setNombre(rs.getString("nombre"));
                inv.setUltimoValor(rs.getBigDecimal("ultimoValor"));
                lista.add(inv);
            }
        }
        return lista;
    }

    public void actualizarValor(String nombre, java.math.BigDecimal valor) throws SQLException {
        String sql = "INSERT INTO inversionSimple(nombre, ultimoValor) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE ultimoValor = ?";
        try (Connection conn = ConexionSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setBigDecimal(2, valor);
            ps.setBigDecimal(3, valor);
            ps.executeUpdate();
        }
    }
}
