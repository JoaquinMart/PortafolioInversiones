package util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQL {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private static Connection conexion = null;

    public static Connection getConnection() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a la base de datos");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    }

    public static void crearTablas() throws SQLException {
        String sqlPortafolio = """
            CREATE TABLE IF NOT EXISTS Portafolio (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL,
                fecha_creacion DATE NOT NULL
            );
        """;

        String sqlInversion = """
            CREATE TABLE IF NOT EXISTS Inversion (
                id INT AUTO_INCREMENT PRIMARY KEY,
                portafolio_id INT NOT NULL,
                nombre VARCHAR(100) NOT NULL,
                
                precio_compra DECIMAL(15,2),
                fecha_compra DATE,
                cantidad INT,
                precio_dolar DOUBLE,
                precio_accion_en_dolar DOUBLE,
                comision_compra DOUBLE,
                
                es_dolar BOOLEAN NOT NULL DEFAULT FALSE,
                es_venta BOOLEAN NOT NULL DEFAULT FALSE,
                
                FOREIGN KEY (portafolio_id) REFERENCES Portafolio(id) ON DELETE CASCADE
            );
        """;

        String sqlInversionSimple = """
        CREATE TABLE IF NOT EXISTS InversionSimple (
            nombre VARCHAR(100) PRIMARY KEY,
            ultimoValor DECIMAL(20,2)
        );
        """;

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sqlPortafolio);
            stmt.execute(sqlInversion);
            stmt.execute(sqlInversionSimple);
            System.out.println("Tablas creadas correctamente");
        } catch (SQLException e) {
            System.out.println("Error creando tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexion cerrada");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
