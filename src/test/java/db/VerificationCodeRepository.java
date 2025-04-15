package db;

import java.sql.*;

import static io.opentelemetry.semconv.SemanticAttributes.DB_USER;

public class VerificationCodeRepository {
    public String getLatestVerificationCode() {
        String code = null;
        String url = "jdbc:postgresql://178.154.240.188:5432/cb-crm-dev"; // Update with your database URL
        String user = "cb-crm-dev"; // Update with your database username
        String password = "Lsjdfnbi3sdfewo4uHIO#UH@iubhf34iub"; // Update with your database password

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT code FROM public.asp_codes ORDER BY created_at DESC LIMIT 1")) {

            if (resultSet.next()) {
                code = resultSet.getString("code");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return code;
    }
}
