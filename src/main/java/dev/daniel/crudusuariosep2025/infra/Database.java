package dev.daniel.crudusuariosep2025.infra;

import java.sql.*;

public class Database {
    private static final String URL  = "jdbc:postgresql://localhost:5432/crud_usuarios";
    private static final String USER = "devuser";
    private static final String PASS = "devpass";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}