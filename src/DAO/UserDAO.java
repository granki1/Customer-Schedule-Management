package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The UserDAO class provides methods for accessing and authenticating user data in the database.
 */
public class UserDAO {

    private static Connection connection = null;

    /**
     * Constructs a new UserDAO instance.
     *
     * @param connection The database connection.
     */
    public UserDAO(Connection connection) {
        UserDAO.connection = connection;
    }

    /**
     * Retrieves the user ID based on the username.
     *
     * @param username The username of the user.
     * @return The user ID corresponding to the username.
     */
    public static int getUserIdByUsername(String username) throws SQLException {
        String query = "SELECT User_ID FROM users WHERE User_Name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            } else {
                return -1;
            }
        }
    }

    /**
     * Authenticates the user with the provided username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return True if the user is authenticated, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean authenticateUser(String username, String password) throws SQLException {
        return JDBC.authenticateUser(username, password);
    }
}
