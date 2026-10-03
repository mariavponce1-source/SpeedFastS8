package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BD = "speedfast_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // <-- Vacía, sin espacios entre las comillas

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BD
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver no encontrado: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}