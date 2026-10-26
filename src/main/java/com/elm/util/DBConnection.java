package com.elm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection utility managing JDBC connection lifecycle.
 */
public class DBConnection {

    // ========================================================================
    // DATABASE CONFIGURATION SETTINGS
    // ========================================================================
    // If your MySQL password is not 'root' or empty, update DB_PASSWORD below,
    // OR pass it via Environment Variable: MYSQL_PASSWORD
    // OR pass it via Java system property: -Ddb.password=yourpassword
    // ========================================================================
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "employee_leave_db";
    private static final String DEFAULT_USER = "root";
    
    // Default fallback password (change this to your actual MySQL root password)
    public static final String DEFAULT_PASSWORD = "root";

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    private DBConnection() {
        // Prevent instantiation
    }

    /**
     * Resolves the database connection URL.
     */
    public static String getDbUrl() {
        String host = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") : DEFAULT_HOST;
        String port = System.getenv("MYSQL_PORT") != null ? System.getenv("MYSQL_PORT") : DEFAULT_PORT;
        String db = System.getenv("MYSQL_DATABASE") != null ? System.getenv("MYSQL_DATABASE") : DEFAULT_DB;
        return "jdbc:mysql://" + host + ":" + port + "/" + db 
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    }

    /**
     * Resolves the database user.
     */
    public static String getDbUser() {
        if (System.getProperty("db.user") != null) {
            return System.getProperty("db.user");
        }
        if (System.getenv("MYSQL_USER") != null) {
            return System.getenv("MYSQL_USER");
        }
        return DEFAULT_USER;
    }

    /**
     * Resolves the database password.
     * Order of precedence:
     * 1. Java System property: -Ddb.password=...
     * 2. Environment variable: MYSQL_PASSWORD
     * 3. Configured DEFAULT_PASSWORD constant below
     */
    public static String getDbPassword() {
        if (System.getProperty("db.password") != null) {
            return System.getProperty("db.password");
        }
        if (System.getenv("MYSQL_PASSWORD") != null) {
            return System.getenv("MYSQL_PASSWORD");
        }
        return DEFAULT_PASSWORD;
    }

    /**
     * Obtains a new database connection.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(getDbUrl(), getDbUser(), getDbPassword());
        } catch (SQLException e) {
            // If authentication failed with configured password, attempt fallback to empty password
            if (e.getErrorCode() == 1045 && !getDbPassword().isEmpty()) {
                try {
                    return DriverManager.getConnection(getDbUrl(), getDbUser(), "");
                } catch (SQLException ignored) {}
            }
            System.err.println("[DBConnection] Failed to connect to MySQL database!");
            System.err.println("  URL: " + getDbUrl());
            System.err.println("  User: " + getDbUser());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Utility method to safely close JDBC resources.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}
