package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Appointment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) class for managing appointments in the database.
 * This class provides methods to interact with the database to retrieve, add, update, and delete appointments.
 * It also includes methods for retrieving appointment counts by type and month, and appointments associated with specific contacts.
 * Additionally, it contains methods for handling appointment creation, updates, and deletions, along with helper methods
 * for managing contacts and user existence checks. This class encapsulates database interactions related to appointments
 * and facilitates efficient management of appointment data within the application.
 */
public class AppointmentDAO {

    /**
     * Retrieves all appointments from the database.
     *
     * @return An ObservableList containing all appointments.
     */
    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        try {
            JDBC.ensureConnection();
            String sql = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, c.Contact_Name AS Contact, a.Type, a.Start, a.End, a.Customer_ID, a.User_ID " +
                    "FROM appointments a " +
                    "JOIN contacts c ON a.Contact_ID = c.Contact_ID";
            try (Statement stmt = JDBC.connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                            rs.getInt("Appointment_ID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Location"),
                            rs.getString("Contact"),
                            rs.getString("Type"),
                            rs.getTimestamp("Start").toLocalDateTime(),
                            rs.getTimestamp("End").toLocalDateTime(),
                            rs.getInt("Customer_ID"),
                            rs.getInt("User_ID")
                    );
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Retrieves the names of all contacts from the database.
     *
     * @return An ObservableList containing the names of all contacts.
     */
    public static ObservableList<String> getAllContactNames() {
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        try {
            JDBC.ensureConnection();
            String sql = "SELECT Contact_Name FROM contacts";
            try (Statement stmt = JDBC.connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    contactNames.add(rs.getString("Contact_Name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactNames;
    }

    /**
     * Retrieves the ID of a contact by its name.
     *
     * @param contactName The name of the contact.
     * @return The ID of the contact, or -1 if not found.
     */
    private static int getContactIdByName(String contactName) {
        JDBC.ensureConnection();
        String query = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        try (PreparedStatement preparedStatement = JDBC.connection.prepareStatement(query)) {
            preparedStatement.setString(1, contactName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("Contact_ID");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting contact ID: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Retrieves a report of appointment counts by type and month from the database.
     *
     * @return A list containing a map for each row of the report.
     * @throws SQLException If a database error occurs.
     */
    public static List<Map<String, Object>> getAppointmentCountsByTypeAndMonth() throws SQLException {
        String query = "SELECT Type, MONTHNAME(Start) as Month, COUNT(*) as Count FROM appointments GROUP BY Type, Month ORDER BY Month";
        List<Map<String, Object>> result = new ArrayList<>();

        try (Statement stmt = JDBC.connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("Type", rs.getString("Type"));
                row.put("Month", rs.getString("Month"));
                row.put("Count", rs.getInt("Count"));
                result.add(row);
            }
        }
        return result;
    }

    /**
     * Retrieves appointments associated with a specific contact from the database.
     *
     * @param contactId The ID of the contact.
     * @return A list of appointments associated with the contact.
     * @throws SQLException If a database error occurs.
     */
    public static List<Appointment> getAppointmentsByContact(int contactId) throws SQLException {
        String query = "SELECT a.Appointment_ID, a.Title, a.Description, a.Location, a.Type, c.Contact_Name, a.Start, a.End, a.Customer_ID, a.User_ID FROM appointments a JOIN contacts c ON a.Contact_ID = c.Contact_ID WHERE a.Contact_ID = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (PreparedStatement pstmt = JDBC.connection.prepareStatement(query)) {
            pstmt.setInt(1, contactId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int appointmentId = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                String contact = rs.getString("Contact_Name");
                LocalDateTime start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("End").toLocalDateTime();
                int customerId = rs.getInt("Customer_ID");
                int userId = rs.getInt("User_ID");

                Appointment appointment = new Appointment(
                        appointmentId,
                        title,
                        description,
                        location,
                        type,
                        contact,
                        start,
                        end,
                        customerId,
                        userId
                );
                appointments.add(appointment);
            }
        }
        return appointments;
    }

    /**
     * Adds a new appointment to the database.
     *
     * @param userId      The ID of the user creating the appointment.
     * @param appointment The appointment to add.
     * @return True if the appointment was added successfully, false otherwise.
     */
    public static boolean addAppointment(int userId, Appointment appointment) {
        JDBC.ensureConnection();

        if (!doesUserExist(userId)) {
            System.out.println("User with ID " + userId + " does not exist.");
            return false;
        }

        int contactId = getContactIdByName(appointment.getContact());
        if (contactId == -1) {
            System.out.println("Contact with name " + appointment.getContact() + " does not exist.");
            return false;
        }

        String getAllIdsQuery = "SELECT Appointment_ID FROM appointments ORDER BY Appointment_ID";
        String insertAppointmentQuery = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID, User_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Statement getAllIdsStatement = JDBC.connection.createStatement();
             ResultSet resultSet = getAllIdsStatement.executeQuery(getAllIdsQuery)) {

            int newId = 1;
            while (resultSet.next()) {
                int currentId = resultSet.getInt("Appointment_ID");
                if (currentId == newId) {
                    newId++;
                } else {
                    break;
                }
            }

            try (PreparedStatement insertAppointmentStatement = JDBC.connection.prepareStatement(insertAppointmentQuery)) {
                insertAppointmentStatement.setInt(1, newId);
                insertAppointmentStatement.setString(2, appointment.getTitle());
                insertAppointmentStatement.setString(3, appointment.getDescription());
                insertAppointmentStatement.setString(4, appointment.getLocation());
                insertAppointmentStatement.setInt(5, contactId);
                insertAppointmentStatement.setString(6, appointment.getType());
                insertAppointmentStatement.setTimestamp(7, Timestamp.valueOf(appointment.getStart()));
                insertAppointmentStatement.setTimestamp(8, Timestamp.valueOf(appointment.getEnd()));
                insertAppointmentStatement.setInt(9, appointment.getCustomerId());
                insertAppointmentStatement.setInt(10, userId);

                insertAppointmentStatement.executeUpdate();

                System.out.println("Appointment added successfully with ID: " + newId);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error adding appointment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a user exists in the database.
     *
     * @param userId The ID of the user.
     * @return True if the user exists, false otherwise.
     */
    private static boolean doesUserExist(int userId) {
        JDBC.ensureConnection();
        String query = "SELECT User_ID FROM users WHERE User_ID = ?";
        try (PreparedStatement preparedStatement = JDBC.connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing appointment in the database.
     *
     * @param userId      The ID of the user updating the appointment.
     * @param appointment The updated appointment object.
     * @return True if the appointment was updated successfully, false otherwise.
     */
    public static boolean updateAppointment(int userId, Appointment appointment) {
        JDBC.ensureConnection();

        StringBuilder sqlBuilder = new StringBuilder("UPDATE appointments SET ");
        List<Object> values = new ArrayList<>();

        if (appointment.getTitle() != null) {
            sqlBuilder.append("Title = ?, ");
            values.add(appointment.getTitle());
        }

        if (appointment.getDescription() != null) {
            sqlBuilder.append("Description = ?, ");
            values.add(appointment.getDescription());
        }

        if (appointment.getLocation() != null) {
            sqlBuilder.append("Location = ?, ");
            values.add(appointment.getLocation());
        }

        if (appointment.getContact() != null) {
            int contactId = getContactIdByName(appointment.getContact());
            if (contactId == -1) {
                System.out.println("Contact with name " + appointment.getContact() + " does not exist.");
                return false;
            }
            sqlBuilder.append("Contact_ID = ?, ");
            values.add(contactId);
        }

        if (appointment.getType() != null) {
            sqlBuilder.append("Type = ?, ");
            values.add(appointment.getType());
        }

        if (appointment.getStart() != null) {
            sqlBuilder.append("Start = ?, ");
            values.add(Timestamp.valueOf(appointment.getStart()));
        }

        if (appointment.getEnd() != null) {
            sqlBuilder.append("End = ?, ");
            values.add(Timestamp.valueOf(appointment.getEnd()));
        }

        if (appointment.getCustomerId() != 0) {
            sqlBuilder.append("Customer_ID = ?, ");
            values.add(appointment.getCustomerId());
        }

        if (userId != 0) {
            sqlBuilder.append("User_ID = ?, ");
            values.add(userId);
        }

        sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());

        sqlBuilder.append(" WHERE Appointment_ID = ?");
        values.add(appointment.getAppointmentId());

        String sql = sqlBuilder.toString();

        try (PreparedStatement ps = JDBC.connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an appointment from the database.
     *
     * @param appointmentId The ID of the appointment to delete.
     * @return True if the appointment was deleted successfully, false otherwise.
     */
    public static boolean deleteAppointment(int appointmentId) {
        JDBC.ensureConnection();

        String selectQuery = "SELECT Type FROM appointments WHERE Appointment_ID=?";
        String deleteQuery = "DELETE FROM appointments WHERE Appointment_ID=?";
        try (PreparedStatement selectStmt = JDBC.connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, appointmentId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("Type");
                    try (PreparedStatement deleteStmt = JDBC.connection.prepareStatement(deleteQuery)) {
                        deleteStmt.setInt(1, appointmentId);
                        int affectedRows = deleteStmt.executeUpdate();
                        if (affectedRows > 0) {
                            showAlert("Deletion Successful");
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    System.out.println("No appointment found with ID: " + appointmentId);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves appointments within a specified time window for a user.
     *
     * @param userId      The ID of the user.
     * @param windowStart The start of the time window.
     * @param windowEnd   The end of the time window.
     * @return A list of appointments within the specified time window.
     * @throws SQLException If a database error occurs.
     */
    public static List<Appointment> getAppointmentsWithinTimeWindow(int userId, LocalDateTime windowStart, LocalDateTime windowEnd) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();

        String query = "SELECT appointment_id, start FROM appointments WHERE user_id = ? AND start BETWEEN ? AND ?";

        JDBC.ensureConnection();

        try (PreparedStatement statement = JDBC.openConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setTimestamp(2, Timestamp.valueOf(windowStart));
            statement.setTimestamp(3, Timestamp.valueOf(windowEnd));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                LocalDateTime start = resultSet.getTimestamp("start").toLocalDateTime();

                // Create an Appointment object with only the appointment ID and start time
                Appointment appointment = new Appointment(appointmentId, null, null, null, null, null, start, null, -1, -1);
                appointments.add(appointment);
            }
        }

        return appointments;
    }

    /**
     * Displays a notification alert.
     *
     * @param message The message to display in the alert.
     */
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
