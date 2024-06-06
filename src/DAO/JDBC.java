package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC class manages the database connection, authentication, and user-related functionalities.
 * It provides methods to open and close the database connection, authenticate users, and set/get the current username.
 * <p>
 * This class abstracts the interaction with the underlying database using JDBC (Java Database Connectivity) API.
 * It encapsulates common database operations such as opening/closing connections, executing queries, and handling exceptions.
 * <p>
 * The main functionalities provided by this class include:
 * - Opening and closing a connection to the database.
 * - Authenticating users by verifying their credentials against the database records.
 * - Setting and retrieving the current username for the session.
 * <p>
 * It also ensures that the database connection is maintained throughout the session and reopens it if necessary.
 * <p>
 * This class serves as a foundation for database interactions within the application, providing a central point for
 * managing database-related operations and encapsulating the details of JDBC implementation.
 */

public abstract class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static final String password = "Passw0rd!";
    public static Connection connection;
    private static String currentUsername;

    /**
     * Opens a connection to the database.
     *
     * @return The database connection.
     */
    public static Connection openConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to open database connection: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to close database connection: " + e.getMessage());
        }
    }

    /**
     * Checks if the connection is still valid, and reopens it if it is not.
     */
    public static void ensureConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                openConnection();
            }
        } catch (SQLException e) {
            System.out.println("Error checking connection validity: " + e.getMessage());
        }
    }

    /**
     * Authenticates a user with the provided username and password.
     *
     * @param username The username to authenticate.
     * @param password The password associated with the username.
     * @return True if authentication is successful, false otherwise.
     */
    public static boolean authenticateUser(String username, String password) {
        ensureConnection();
        if (connection == null) {
            System.out.println("Database connection is not established.");
            return false;
        }

        String query = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    setCurrentUsername(username);
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
        }

        return false;
    }

    /**
     * Sets the current username.
     *
     * @param username The username to set as the current username.
     */
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    /**
     * Retrieves the current username.
     *
     * @return The current username.
     */
    public static String getCurrentUser() {
        return currentUsername;
    }
}
