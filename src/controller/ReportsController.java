package controller;

import DAO.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import DAO.AppointmentDAO;
import DAO.ContactDAO;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller class for generating reports.
 */
public class ReportsController implements Initializable {

    @FXML
    private TableView<ReportRow> reportTableView;

    @FXML
    private ComboBox<Contact> contactComboBox;

    @FXML
    private VBox rootVBox;

    @FXML
    private ComboBox<String> countryComboBox;

    /**
     * Initializes the controller by setting up event handlers for ComboBox selections.
     * <p>
     * This method is called when the controller is initialized, typically after its
     * root element has been processed by the FXMLLoader. It sets up event handlers
     * for the selection actions of the contactComboBox and countryComboBox. When an
     * item is selected from the contactComboBox, it triggers the
     * generateScheduleForSelectedContactReport method to generate a report specific
     * to the selected contact. Similarly, when an item is selected from the
     * countryComboBox, it triggers the generateCustomerByCountryReport method to
     * generate a report specific to the selected country. Lambda expressions are
     * used here to provide concise and inline definitions for these event handlers.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootVBox.setPadding(new Insets(20));
        populateContactComboBox();
        populateCountryComboBox();

        contactComboBox.setOnAction(event -> {
            clearCountrySelection();
            generateScheduleForSelectedContactReport();
        });

        countryComboBox.setOnAction(event -> {
            clearContactSelection();
            generateCustomerByCountryReport();
        });
    }

    /**
     * Populates the contact ComboBox with available contacts.
     */
    private void populateContactComboBox() {
        try {
            List<Contact> contacts = ContactDAO.getAllContacts();
            contactComboBox.setItems(FXCollections.observableArrayList(contacts));
        } catch (Exception e) {
            showAlert("Error loading contacts: " + e.getMessage());
        }
    }

    /**
     * Generates the customer appointment frequency report.
     */
    @FXML
    public void generateCustomerAppointmentFrequencyReport() {
        try {
            List<Map<String, Object>> reportData = AppointmentDAO.getAppointmentCountsByTypeAndMonth();
            ObservableList<ReportRow> reportRows = FXCollections.observableArrayList();

            for (Map<String, Object> row : reportData) {
                String type = (String) row.get("Type");
                String month = (String) row.get("Month");
                int count = (int) row.get("Count");
                reportRows.add(new ReportRow(type, month, count));
            }

            reportTableView.setItems(reportRows);
            setupCustomerAppointmentFrequencyColumns();
        } catch (Exception e) {
            showAlert("Error generating report: " + e.getMessage());
        }
    }

    private void setupCustomerAppointmentFrequencyColumns() {
        reportTableView.getColumns().clear();
        TableColumn<ReportRow, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<ReportRow, String> monthColumn = new TableColumn<>("Month");
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        TableColumn<ReportRow, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        reportTableView.getColumns().addAll(typeColumn, monthColumn, countColumn);
    }

    /**
     * Generates the schedule for selected contact report.
     */
    @FXML
    public void generateScheduleForSelectedContactReport() {
        Contact selectedContact = contactComboBox.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            try {
                List<Appointment> appointments = AppointmentDAO.getAppointmentsByContact(selectedContact.getContactId());
                ObservableList<ReportRow> reportRows = FXCollections.observableArrayList();

                for (Appointment appointment : appointments) {
                    reportRows.add(new ReportRow(
                            appointment.getAppointmentId(),
                            appointment.getTitle(),
                            appointment.getType(),
                            appointment.getDescription(),
                            appointment.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            appointment.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            appointment.getCustomerId()
                    ));
                }

                reportTableView.setItems(reportRows);
                setupScheduleForContactsColumns();
            } catch (Exception e) {
                showAlert("Error generating report: " + e.getMessage());
            }
        }
    }

    /**
     * Sets up the columns for displaying appointment schedule for contacts in the reportTableView.
     * <p>
     * This method configures the columns of the reportTableView to display appointment schedule
     * information for contacts. It creates TableColumn instances for various properties of the
     * appointments such as ID, title, type, description, start time, end time, and customer ID.
     * PropertyValueFactory is used to map these TableColumn instances to properties of the
     * ReportRow objects. Finally, the configured columns are added to the reportTableView.
     */
    private void setupScheduleForContactsColumns() {
        reportTableView.getColumns().clear();
        TableColumn<ReportRow, Integer> idColumn = new TableColumn<>("Appointment ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<ReportRow, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<ReportRow, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<ReportRow, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<ReportRow, String> startColumn = new TableColumn<>("Start");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        TableColumn<ReportRow, String> endColumn = new TableColumn<>("End");
        endColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        TableColumn<ReportRow, Integer> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        reportTableView.getColumns().addAll(idColumn, titleColumn, typeColumn, descriptionColumn, startColumn, endColumn, customerIdColumn);
    }

    /**
     * Populates the country ComboBox with available countries.
     */
    private void populateCountryComboBox() {
        try {
            List<String> countries = CustomerDAO.getAllCountries();
            countryComboBox.setItems(FXCollections.observableArrayList(countries));
        } catch (Exception e) {
            showAlert("Error loading countries: " + e.getMessage());
        }
    }

    /**
     * Generates the customer by country report.
     */
    @FXML
    public void generateCustomerByCountryReport() {
        String selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCountry != null) {
            try {
                ObservableList<CustomerSummary> customers = CustomerDAO.getCustomersByCountry(selectedCountry);
                ObservableList<ReportRow> reportRows = FXCollections.observableArrayList();

                for (CustomerSummary customer : customers) {
                    reportRows.add(new ReportRow(
                            customer.getCustomerId(),
                            customer.getCustomerName(),
                            customer.getAddress(),
                            customer.getPostalCode(),
                            customer.getPhone(),
                            customer.getDivisionName(),
                            selectedCountry
                    ));
                }
                reportTableView.setItems(reportRows);
                setupCustomerByCountryColumns();
            } catch (Exception e) {
                showAlert("Error generating report: " + e.getMessage());
            }
        }
    }

    /**
     * Sets up the columns for displaying customer information by country in the reportTableView.
     * <p>
     * This method configures the columns of the reportTableView to display customer information
     * categorized by country. It creates TableColumn instances for various properties of the
     * customers such as ID, name, address, postal code, phone number, and division name.
     * PropertyValueFactory is used to map these TableColumn instances to properties of the
     * ReportRow objects. Finally, the configured columns are added to the reportTableView.
     */
    private void setupCustomerByCountryColumns() {
        reportTableView.getColumns().clear();

        TableColumn<ReportRow, Integer> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        TableColumn<ReportRow, String> customerNameColumn = new TableColumn<>("Customer Name");
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        TableColumn<ReportRow, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        TableColumn<ReportRow, String> postalCodeColumn = new TableColumn<>("Postal Code");
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        TableColumn<ReportRow, String> phoneColumn = new TableColumn<>(" Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<ReportRow, String> divisionNameColumn = new TableColumn<>("Division");
        divisionNameColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

        reportTableView.getColumns().addAll(customerIdColumn, customerNameColumn, addressColumn, postalCodeColumn, phoneColumn, divisionNameColumn);
    }

    /**
     * Handles navigation back to the Customer Management view.
     */
    @FXML
    public void handleBackToCustomerManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerManagement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert with the given message.
     *
     * @param message The message to display in the alert.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears the selection in the country ComboBox.
     */
    private void clearCountrySelection() {
        if (contactComboBox.getSelectionModel().getSelectedItem() != null) {
            countryComboBox.getSelectionModel().clearSelection();
        }
    }

    /**
     * Clears the selection in the contact ComboBox.
     */
    private void clearContactSelection() {
        if (countryComboBox.getSelectionModel().getSelectedItem() != null) {
            contactComboBox.getSelectionModel().clearSelection();
        }
    }
}
