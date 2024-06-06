package controller;

import DAO.AppointmentDAO;
import DAO.JDBC;
import DAO.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.Appointment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.List;
import java.sql.SQLException;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for the login functionality.
 */
public class LoginController {

    public static ZoneId userTimeZone;

    @FXML
    private Label locationLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private ResourceBundle resources;
    private UserDAO userDAO;

    /**
     * Sets the UserDAO for the controller.
     *
     * @param userDAO the UserDAO to set
     */
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Initializes the login controller.
     */
    @FXML
    public void initialize() {
        Locale locale = Locale.getDefault();
        resources = ResourceBundle.getBundle("bundles.Login", locale);

        ZoneId zoneId = ZoneId.systemDefault();
        String localizedZoneName = zoneId.getDisplayName(TextStyle.FULL, locale);
        String locationString = zoneId.getId() + " (" + localizedZoneName + ")";

        locationLabel.setText(resources.getString("location") + ": " + locationString);
        usernameLabel.setText(resources.getString("username"));
        passwordLabel.setText(resources.getString("password"));
        loginButton.setText(resources.getString("login"));

        passwordField.setOnKeyPressed(this::handleEnterKeyPress);
        usernameField.setOnKeyPressed(this::handleEnterKeyPress);

        JDBC.openConnection();
        this.userDAO = new UserDAO(JDBC.connection);
    }

    /**
     * Handles the login action.
     */
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText(resources.getString("error.invalid.credentials"));
            return;
        }

        if (authenticate(username, password)) {
            logLoginActivity(username, true);

            JDBC.setCurrentUsername(username);

            userTimeZone = ZoneId.systemDefault();

            try {
                checkUpcomingAppointments(username);
            } catch (SQLException e) {
                errorLabel.setText("Error checking upcoming appointments.");
                e.printStackTrace();
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerManagement.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Customer Management");
                stage.show();
            } catch (IOException e) {
                errorLabel.setText(resources.getString("error.loading.page"));
                e.printStackTrace();
            }
        } else {
            logLoginActivity(username, false);
            errorLabel.setText(resources.getString("error.invalid.credentials"));
        }
    }

    /**
     * Checks for upcoming appointments and displays an alert if found.
     *
     * @param username the username
     * @throws SQLException if a SQL error occurs
     */
    private void checkUpcomingAppointments(String username) throws SQLException {
        int userId = UserDAO.getUserIdByUsername(username);

        LocalDateTime nowUTC = LocalDateTime.now(ZoneOffset.UTC);

        LocalDateTime windowEndUTC = nowUTC.plusMinutes(15);

        List<Appointment> upcomingAppointments = AppointmentDAO.getAppointmentsWithinTimeWindow(userId, nowUTC, windowEndUTC);

        if (!upcomingAppointments.isEmpty()) {
            ZoneId userZoneId = ZoneId.systemDefault();

            StringBuilder message = new StringBuilder();
            message.append("You have upcoming appointments within 15 minutes:\n");
            for (Appointment appointment : upcomingAppointments) {
                ZonedDateTime startLocal = appointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(userZoneId);

                message.append("Appointment ID: ").append(appointment.getAppointmentId())
                        .append(", Date: ").append(startLocal.toLocalDate())
                        .append(", Time: ").append(startLocal.toLocalTime()).append("\n");
            }
            showAlert(message.toString());
        } else {
            showAlert("There are no upcoming appointments within 15 minutes.");
        }
    }

    /**
     * Displays an alert dialog with the specified message.
     *
     * @param message the message to display in the alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming Appointments");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Authenticates a user.
     *
     * @param username The username.
     * @param password The password.
     * @return True if authentication is successful, otherwise false.
     */
    private boolean authenticate(String username, String password) {
        try {
            return userDAO.authenticateUser(username, password);
        } catch (SQLException e) {
            errorLabel.setText("Database error occurred. Please try again.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Logs login activity.
     *
     * @param username The username.
     * @param success  True if login was successful, otherwise false.
     */
    private void logLoginActivity(String username, boolean success) {
        String filename = "login_activity.txt";
        try (FileWriter fileWriter = new FileWriter(filename, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String timestamp = dtf.format(now);
            String status = success ? "Success" : "Failed";
            printWriter.println(timestamp + " - User: " + username + ", Status: " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the Enter key press event.
     *
     * @param event The KeyEvent.
     */
    private void handleEnterKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) handleLogin();
    }
}
