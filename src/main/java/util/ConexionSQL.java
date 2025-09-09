package util;

import io.github.cdimascio.dotenv.Dotenv;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQL {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);

            // Configuraciones recomendadas
            config.setMaximumPoolSize(10); // conexiones máximas en el pool
            config.setMinimumIdle(2);      // conexiones mínimas en idle
            config.setIdleTimeout(30000);  // 30s antes de cerrar una conexión idle
            config.setMaxLifetime(1800000); // 30m de vida máxima por conexión

            dataSource = new HikariDataSource(config);
            System.out.println("Pool de conexiones inicializado correctamente.");
        } catch (Exception e) {
            System.err.println("Error inicializando pool de conexiones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
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

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPortafolio);
            stmt.execute(sqlInversion);
            stmt.execute(sqlInversionSimple);
            System.out.println("Tablas creadas correctamente");
        } catch (SQLException e) {
            System.out.println("Error creando tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Pool de conexiones cerrado.");
        }
    }
}
