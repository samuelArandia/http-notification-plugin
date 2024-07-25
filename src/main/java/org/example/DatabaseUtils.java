package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtils {

    public static void logRequest(String method, String url, String body, String contentType, int statusCode, String responseBody) {
        String sql = "INSERT INTO http_requests (method, url, body, content_type, status_code, response_body) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, method);
            statement.setString(2, url);
            statement.setString(3, body);
            statement.setString(4, contentType);
            statement.setInt(5, statusCode);
            statement.setString(6, responseBody);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
        }
    }
}