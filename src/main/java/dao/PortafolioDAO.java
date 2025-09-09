package dao;

import model.Portafolio;
import util.ConexionSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortafolioDAO {

    // METODO DE CREACION DEL PORTAFOLIO
    public void crearPortafolio(Portafolio p) {
        String sql = "INSERT INTO portafolio (nombre, fecha_creacion) VALUES (?, ?)";

        try (Connection conn = ConexionSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombre());
            ps.setDate(2, Date.valueOf(p.getFechaCreacion()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setID(rs.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // METODO PARA RECUPERAR TODOS LOS PORTAFOLIOS
    public List<Portafolio> getPortafolios() {
        List<Portafolio> portafolios = new ArrayList<>();
        String sql = "SELECT * FROM portafolio";

        try (Connection conn = ConexionSQL.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Portafolio p = new Portafolio();
                p.setID(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
                portafolios.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return portafolios;
    }

    // METODO PARA RECUPERAR UN PORTAFOLIO POR ID
    public Portafolio getPortafolioPorId(int id) {
        String sql = "SELECT * FROM portafolio WHERE id = ?";
        try (Connection conn = ConexionSQL.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Portafolio p = new Portafolio();
                    p.setID(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return null;
    }

    // METODO PARA ELIMINAR UN PORTAFOLIO POR ID
    public void eliminarPortafolio(int id) {
        String sql = "DELETE FROM portafolio WHERE id = ?";
        try (Connection conn = ConexionSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
