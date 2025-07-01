package com.example.database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/emailscanningsystem";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Surendr@1612";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            String createLogsTable = "CREATE TABLE IF NOT EXISTS logs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "log_entry TEXT NOT NULL, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            statement.execute(createLogsTable);

            String createRulesTable = "CREATE TABLE IF NOT EXISTS rules (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "rule TEXT NOT NULL)";
            statement.execute(createRulesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Methods to manage rules
    public static void saveRule(String rule) throws SQLException {
        String insertRuleSQL = "INSERT INTO rules (rule) VALUES (?)";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(insertRuleSQL)) {
            ps.setString(1, rule);
            ps.executeUpdate();
        }
    }

    public static void deleteRule(String rule) throws SQLException {
        String deleteRuleSQL = "DELETE FROM rules WHERE rule = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteRuleSQL)) {
            ps.setString(1, rule);
            ps.executeUpdate();
        }
    }

    public static ResultSet getRules() throws SQLException {
        String selectRulesSQL = "SELECT rule FROM rules";
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(selectRulesSQL);
    }
}
