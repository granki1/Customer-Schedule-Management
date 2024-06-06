package controller;

import DAO.AppointmentDAO;
import DAO.CustomerDAO;
import DAO.JDBC;
import DAO.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller class for managing appointments in the application.
 */
public class AppointmentController {
    @FXML
    private TextField appointmentIdField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField typeField;
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private ComboBox<Integer> customerIdComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField startTimeField;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeField;
    @FXML
    private RadioButton allViewRadio;
    @FXML
    private RadioButton monthViewRadio;
    @FXML
    private RadioButton weekViewRadio;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentLocationColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentContactColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerIdColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentUserIdColumn;

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    private ToggleGroup toggleGroup;

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
     * <p>
     * This method initializes the UI components, sets up event listeners, and loads initial data.
     * Lambda expressions are used to define event handlers for the radio buttons. Each radio button is associated
     * with the {@link #handleViewToggle(ActionEvent)} method using lambda expressions, providing a concise way
     * to implement event handling. When a radio button is selected, the corresponding event is triggered,
     * and the {@code handleViewToggle} method is invoked to handle the view toggle logic.
     * </p>
     */
    @FXML
    public void initialize() {

        // Set up columns with PropertyValueFactory
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Assign appointments to the table
        appointmentTable.setItems(appointments);

        // Populate ComboBoxes
        contactComboBox.setItems(AppointmentDAO.getAllContactNames());
        List<Customer> allCustomers = CustomerDAO.getAllCustomers();
        List<Integer> customerIds = allCustomers.stream().map(Customer::getCustomerId).collect(Collectors.toList());
        customerIdComboBox.setItems(FXCollections.observableArrayList(customerIds));

        // Setup Toggle Group and assign it to the radio buttons
        toggleGroup = new ToggleGroup();
        allViewRadio.setToggleGroup(toggleGroup);
        monthViewRadio.setToggleGroup(toggleGroup);
        weekViewRadio.setToggleGroup(toggleGroup);

        appointmentStartColumn.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            }
        });

        appointmentEndColumn.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            }
        });

        // Load appointments initially
        loadAppointments();

        // Set event handlers for view toggle
        allViewRadio.setOnAction(this::handleViewToggle);
        monthViewRadio.setOnAction(this::handleViewToggle);
        weekViewRadio.setOnAction(this::handleViewToggle);

        // Select 'All View' radio button by default
        allViewRadio.setSelected(true);
        handleViewToggle(new ActionEvent()); // Optionally, you can call the specific method to load all appointments
    }

    /**
     * Handles view toggle based on the selected radio button.
     *
     * @param event the action event
     */
    @FXML
    private void handleViewToggle(ActionEvent event) {
        if (allViewRadio.isSelected()) {
            loadAppointments();
        } else if (monthViewRadio.isSelected()) {
            handleMonthViewToggle();
        } else if (weekViewRadio.isSelected()) {
            handleWeekViewToggle();
        }
    }

    /**
     * Handles the month view toggle, filtering appointments for the current month.
     */
    @FXML
    private void handleMonthViewToggle() {

        List<Appointment> allAppointments = AppointmentDAO.getAllAppointments();

        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        List<Appointment> currentMonthAppointments = allAppointments.stream()
                .filter(appointment ->
                        appointment.getStart().getMonthValue() == currentMonth &&
                                appointment.getStart().getYear() == currentYear)
                .collect(Collectors.toList());

        convertAppointmentsToUserTimeZone(currentMonthAppointments);

        appointments.setAll(currentMonthAppointments);
    }

    /**
     * Handles the week view toggle, filtering appointments for the current week.
     */
    @FXML
    private void handleWeekViewToggle() {

        List<Appointment> allAppointments = AppointmentDAO.getAllAppointments();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        List<Appointment> currentWeekAppointments = allAppointments.stream()
                .filter(appointment ->
                        appointment.getStart().isAfter(startOfWeek) && appointment.getStart().isBefore(endOfWeek))
                .collect(Collectors.toList());

        convertAppointmentsToUserTimeZone(currentWeekAppointments);

        appointments.setAll(currentWeekAppointments);
    }

    /**
     * Handles the addition of a new appointment.
     * <p>
     * This method retrieves appointment data from the UI components, validates the input,
     * checks for overlapping appointments, and adds the new appointment to the database
     * if all conditions are met. If successful, it reloads the appointments and clears
     * the input fields. If any errors occur during the process, an alert dialog is displayed.
     * </p>
     */
    @FXML
    public void handleAddAppointment() {
        try {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String location = locationField.getText();
            String type = typeField.getText();
            String contact = contactComboBox.getValue();
            Integer customerId = customerIdComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();

            if (title == null || title.isEmpty() || description == null || description.isEmpty() ||
                    location == null || location.isEmpty() || type == null || type.isEmpty() ||
                    contact == null || customerId == null || startDate == null || endDate == null ||
                    startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty()) {
                showAlert("All fields must be filled out.");
                return;
            }

            String username = JDBC.getCurrentUser();
            int userId = UserDAO.getUserIdByUsername(username);

            LocalTime parsedStartTime;
            LocalTime parsedEndTime;
            try {
                parsedStartTime = LocalTime.parse(startTime, timeFormatter);
                parsedEndTime = LocalTime.parse(endTime, timeFormatter);
            } catch (DateTimeParseException e) {
                showAlert("Invalid time format.");
                return;
            }

            LocalDateTime startDateTime = LocalDateTime.of(startDate, parsedStartTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, parsedEndTime);

            if (!isWithinBusinessHours(startDateTime, endDateTime)) {
                showAlert("Appointments must be scheduled between 8:00 a.m. and 10:00 p.m. ET.");
                return;
            }

            List<Appointment> allAppointments = AppointmentDAO.getAllAppointments();
            if (hasOverlappingAppointments(allAppointments, startDateTime, endDateTime, 0)) {
                showAlert("Overlapping appointments are not allowed.");
                return;
            }

            Appointment newAppointment = new Appointment(
                    0, title, description, location, contact, type,
                    convertToUTC(startDateTime), convertToUTC(endDateTime),
                    customerId, userId
            );

            if (AppointmentDAO.addAppointment(userId, newAppointment)) {
                loadAppointments();
                clearFields();
            } else {
                showAlert("Failed to add appointment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while adding appointment.");
        }
    }

    /**
     * Handles the updating of an existing appointment.
     * <p>
     * This method updates the selected appointment with new data retrieved from the UI components.
     * It performs validation checks similar to the {@link #handleAddAppointment()} method,
     * including input validation, business hours validation, and checking for overlapping appointments.
     * If the update is successful, it reloads the appointments and clears the input fields.
     * Otherwise, it displays an error message using an alert dialog.
     * </p>
     */
    @FXML
    public void handleUpdateAppointment() {
        try {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment == null) {
                showAlert("No appointment selected.");
                return;
            }

            String title = titleField.getText().isEmpty() ? selectedAppointment.getTitle() : titleField.getText();
            String description = descriptionField.getText().isEmpty() ? selectedAppointment.getDescription() : descriptionField.getText();
            String location = locationField.getText().isEmpty() ? selectedAppointment.getLocation() : locationField.getText();
            String type = typeField.getText().isEmpty() ? selectedAppointment.getType() : typeField.getText();
            String contact = contactComboBox.getValue() != null ? contactComboBox.getValue() : selectedAppointment.getContact();
            Integer customerId;
            if (customerIdComboBox.getValue() != null) {
                customerId = customerIdComboBox.getValue();
            } else {
                customerId = selectedAppointment.getCustomerId();
            }
            if (customerId == 0) {
                showAlert("Customer ID cannot be null.");
                return;
            }

            int userId = UserDAO.getUserIdByUsername(JDBC.getCurrentUser());

            LocalDateTime newStartDateTime = selectedAppointment.getStart();
            LocalDateTime newEndDateTime = selectedAppointment.getEnd();

            if (!startTimeField.getText().isEmpty()) {
                String startTime = startTimeField.getText();
                try {
                    LocalTime parsedStartTime = LocalTime.parse(startTime, timeFormatter);
                    newStartDateTime = newStartDateTime.withHour(parsedStartTime.getHour()).withMinute(parsedStartTime.getMinute());
                } catch (DateTimeParseException e) {
                    showAlert("Invalid start time format.");
                    return;
                }
            }

            if (!endTimeField.getText().isEmpty()) {
                String endTime = endTimeField.getText();
                try {
                    LocalTime parsedEndTime = LocalTime.parse(endTime, timeFormatter);
                    newEndDateTime = newEndDateTime.withHour(parsedEndTime.getHour()).withMinute(parsedEndTime.getMinute());
                } catch (DateTimeParseException e) {
                    showAlert("Invalid end time format.");
                    return;
                }
            }

            if (startDatePicker.getValue() != null) {
                LocalDate newStartDate = startDatePicker.getValue();
                newStartDateTime = LocalDateTime.of(newStartDate, newStartDateTime.toLocalTime());
            }

            if (endDatePicker.getValue() != null) {
                LocalDate newEndDate = endDatePicker.getValue();
                newEndDateTime = LocalDateTime.of(newEndDate, newEndDateTime.toLocalTime());
            }

            if (!isWithinBusinessHours(newStartDateTime, newEndDateTime)) {
                showAlert("Appointments must be scheduled between 8:00 a.m. and 10:00 p.m. ET.");
                return;
            }

            List<Appointment> allAppointments = AppointmentDAO.getAllAppointments();
            if (hasOverlappingAppointments(allAppointments, newStartDateTime, newEndDateTime, selectedAppointment.getAppointmentId())) {
                showAlert("Overlapping appointments are not allowed.");
                return;
            }

            selectedAppointment.setTitle(title);
            selectedAppointment.setDescription(description);
            selectedAppointment.setLocation(location);
            selectedAppointment.setType(type);
            selectedAppointment.setContact(contact);
            selectedAppointment.setCustomerId(customerId);
            selectedAppointment.setUserId(userId);
            selectedAppointment.setStart(convertToUTC(newStartDateTime));
            selectedAppointment.setEnd(convertToUTC(newEndDateTime));

            if (AppointmentDAO.updateAppointment(userId, selectedAppointment)) {
                loadAppointments();
                clearFields();
            } else {
                showAlert("Failed to update appointment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while updating appointment.");
        }
    }

    /**
     * Handles the deletion of an existing appointment.
     */
    @FXML
    public void handleDeleteAppointment() {
        try {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment == null) {
                showAlert("No appointment selected.");
                return;
            }

            // Create confirmation dialog with expanded appointment details
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this appointment?");
            alert.setContentText("Details:\n" +
                    "Appointment ID: " + selectedAppointment.getAppointmentId() + "\n" +
                    "Title: " + selectedAppointment.getTitle() + "\n" +
                    "Description: " + selectedAppointment.getDescription() + "\n" +
                    "Location: " + selectedAppointment.getLocation() + "\n" +
                    "Contact: " + selectedAppointment.getContact() + "\n" +
                    "Type: " + selectedAppointment.getType() + "\n" +
                    "Start: " + selectedAppointment.getStart() + "\n" +
                    "End: " + selectedAppointment.getEnd() + "\n" +
                    "Customer ID: " + selectedAppointment.getCustomerId() + "\n" +
                    "User ID: " + selectedAppointment.getUserId());

            // Customizing the buttons
            ButtonType buttonTypeDelete = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeDelete) {
                if (AppointmentDAO.deleteAppointment(selectedAppointment.getAppointmentId())) {
                    loadAppointments();
                    clearFields();
                } else {
                    showAlert("Failed to delete appointment.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An error occurred while deleting appointment.");
        }
    }

    /**
     * Handles the navigation back to the customer management screen.
     *
     * @param event the action event
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerManagement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Customer Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load Customer Management screen.");
        }
    }

    /**
     * Loads appointments from the database and updates the UI.
     */
    private void loadAppointments() {
        try {
            appointments.clear();
            List<Appointment> allAppointments = AppointmentDAO.getAllAppointments();
            ZoneId userTimeZone = LoginController.userTimeZone;

            convertAppointmentsToUserTimeZone(allAppointments);

            appointments.addAll(allAppointments);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load appointments.");
        }
    }

    /**
     * Converts a list of appointments to the user's local time zone.
     *
     * @param appointments the list of appointments to convert
     */
    private void convertAppointmentsToUserTimeZone(List<Appointment> appointments) {
        ZoneId userTimeZone = LoginController.userTimeZone;
        for (Appointment appointment : appointments) {
            appointment.setStart(convertToUserTimeZone(appointment.getStart(), userTimeZone));
            appointment.setEnd(convertToUserTimeZone(appointment.getEnd(), userTimeZone));
        }
    }

    /**
     * Converts a LocalDateTime from UTC to the user's local time zone.
     *
     * @param utcDateTime  the LocalDateTime in UTC
     * @param userTimeZone the user's time zone
     * @return the LocalDateTime converted to the user's time zone
     */
    private LocalDateTime convertToUserTimeZone(LocalDateTime utcDateTime, ZoneId userTimeZone) {
        return utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(userTimeZone).toLocalDateTime();
    }

    /**
     * Checks if an appointment falls within business hours (8:00 a.m. to 10:00 p.m. ET).
     *
     * @param startDateTime the start date and time of the appointment
     * @param endDateTime   the end date and time of the appointment
     * @return true if the appointment falls within business hours, otherwise false
     */
    private boolean isWithinBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalTime businessStart = LocalTime.of(8, 0);
        LocalTime businessEnd = LocalTime.of(22, 0);

        ZonedDateTime startET = startDateTime.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime endET = endDateTime.atZone(ZoneId.of("America/New_York"));

        boolean startWithinBusinessHours = !startET.toLocalTime().isBefore(businessStart) && !startET.toLocalTime().isAfter(businessEnd);
        boolean endWithinBusinessHours = !endET.toLocalTime().isBefore(businessStart) && !endET.toLocalTime().isAfter(businessEnd);

        return startWithinBusinessHours && endWithinBusinessHours;
    }

    /**
     * Checks if the specified time frame overlaps with any existing appointments.
     *
     * @param appointments   the list of existing appointments
     * @param newStartDateTime the start date and time of the new appointment
     * @param newEndDateTime   the end date and time of the new appointment
     * @param appointmentId    the ID of the new appointment (0 for new appointments)
     * @return true if there is an overlap, otherwise false
     */
    private boolean hasOverlappingAppointments(List<Appointment> appointments, LocalDateTime newStartDateTime, LocalDateTime newEndDateTime, int appointmentId) {
        for (Appointment appointment : appointments) {

            if (appointment.getAppointmentId() == appointmentId) {
                continue;
            }

            if (!newStartDateTime.toLocalDate().isEqual(appointment.getStart().toLocalDate())) {
                continue;
            }

            if (newStartDateTime.isBefore(appointment.getEnd()) && newEndDateTime.isAfter(appointment.getStart())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        appointmentIdField.clear();
        titleField.clear();
        descriptionField.clear();
        locationField.clear();
        typeField.clear();
        contactComboBox.getSelectionModel().clearSelection();
        customerIdComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        startDatePicker.getEditor().clear();
        endDatePicker.setValue(null);
        endDatePicker.getEditor().clear();
        startTimeField.clear();
        endTimeField.clear();
    }

    /**
     * Displays an alert dialog with the specified message.
     *
     * @param message the message to display in the alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Converts a LocalDateTime from the system default time zone to UTC.
     *
     * @param localDateTime the LocalDateTime in the system default time zone
     * @return the LocalDateTime converted to UTC
     */
    private LocalDateTime convertToUTC(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}