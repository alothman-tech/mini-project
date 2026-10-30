package nl.hu.library.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Verbinding met Postgres. Instellingen komen uit environment variables, zodat er
 * geen wachtwoorden in de code staan (zie handleiding: systemctl edit tomcat10).
 * De standaardwaarden zijn alleen bedoeld voor lokaal ontwikkelen.
 */
public final class Database {
    private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5432/library");
    private static final String USER = env("DB_USER", "postgres");
    private static final String PASSWORD = env("DB_PASSWORD", "postgres");

    static {
        try {
            Class.forName("org.postgresql.Driver"); // nodig binnen Tomcat
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
