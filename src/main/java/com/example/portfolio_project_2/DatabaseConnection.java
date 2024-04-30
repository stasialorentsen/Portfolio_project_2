package com.example.portfolio_project_2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private DatabaseConnection() {
        // Empty private constructor to prevent instantiating this class
    }

    public static Connection getConnection() {
        try {
            String url = "jdbc:sqlite:identifier.sqlite";
            // Establish a new connection each time this method is called, and return it
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
}