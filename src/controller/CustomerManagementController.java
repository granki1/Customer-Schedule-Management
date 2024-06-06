package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.application.Platform;
import model.Customer;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static DAO.CustomerDAO.*;
import static DAO.JDBC.connection;
import static DAO.JDBC.getCurrentUser;

/**
 * Controller class for managing customers.
 */
public class CustomerManagementController {

    @FXML
    private TextField customerIdField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private ComboBox<String> divisionComboBox;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> postalCodeColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML
    private TableColumn<Customer, Integer> divisionIdColumn;
    @FXML
    private TableColumn<Customer, String> divisionNameColumn;
    @FXML
    private TableColumn<Customer, LocalDateTime> createDateColumn;
    @FXML
    private TableColumn<Customer, String> createdByColumn;
    @FXML
    private TableColumn<Customer, LocalDateTime> lastUpdateColumn;
    @FXML
    private TableColumn<Customer, String> lastUpdatedByColumn;
    @FXML
    private Button viewReportsButton;

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        postalCodeColumn.setCellValueFactory(cellData -> cellData.getValue().postalCodeProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        divisionIdColumn.setCellValueFactory(cellData -> cellData.getValue().divisionIdProperty().asObject());
        divisionNameColumn.setCellValueFactory(cellData -> cellData.getValue().divisionNameProperty());
        createDateColumn.setCellValueFactory(cellData -> cellData.getValue().createDateProperty());
        createdByColumn.setCellValueFactory(cellData -> cellData.getValue().createdByProperty());
        lastUpdateColumn.setCellValueFactory(cellData -> cellData.getValue().lastUpdateProperty());
        lastUpdatedByColumn.setCellValueFactory(cellData -> cellData.getValue().lastUpdatedByProperty());

        loadCustomerData();

        customerTableView.setItems(customerList);

        initializeComboBoxes();
    }

    /**
     * Loads customer data from the database.
     */
    private void loadCustomerData() {
        customerList.setAll(getAllCustomers());
    }

    /**
     * Initializes the country and division combo boxes using lambda expressions.
     * Lambda expression used to set the items for the countryComboBox and to load divisions based on the selected country.
     */
    private void initializeComboBoxes() {
        countryComboBox.setItems(FXCollections.observableArrayList(getAllCountries()));

        countryComboBox.setOnAction(event -> loadDivisionsForCountry(countryComboBox.getValue()));
    }

    /**
     * Retrieves all countries from the database.
     *
     * @return A list of country names.
     */
    private List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        String query = "SELECT DISTINCT Country FROM client_schedule.countries";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                countries.add(resultSet.getString("Country"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }

    /**
     * Loads divisions for the selected country using a lambda expression.
     *
     * @param country The selected country.
     */
    private void loadDivisionsForCountry(String country) {
        Platform.runLater(() -> {
            List<String> divisions = new ArrayList<>();
            String query = "SELECT Division FROM client_schedule.first_level_divisions " +
                    "WHERE COUNTRY_ID = (SELECT Country_ID FROM client_schedule.countries WHERE Country = ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, country);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    divisions.add(resultSet.getString("Division"));
                }
                divisionComboBox.setItems(FXCollections.observableArrayList(divisions));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handles the event when adding a new customer.
     *
     * This method is responsible for processing the addition of a new customer
     * to the system. It retrieves input data from various UI components, validates
     * the input, creates a new customer object, and adds it to the database. If any
     * essential field is empty, an error message is displayed to the user. Upon
     * successful addition, the customer list is refreshed, and all input fields are cleared.
     *
     * @param event The action event triggering the addition of a new customer.
     */
    @FXML
    private void handleAddCustomer(ActionEvent event) {
        String customerName = customerNameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phone = phoneField.getText();
        String division = divisionComboBox.getValue();
        String currentUser = getCurrentUser();

        if (customerName.isEmpty() || address.isEmpty() || postalCode.isEmpty() || phone.isEmpty() || division.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
        } else {
            Customer newCustomer = new Customer(0, customerName, address, postalCode, phone, 0, division, LocalDateTime.now(), currentUser, LocalDateTime.now(), currentUser);
            addCustomer(newCustomer, division);
            loadCustomerData();
            clearTextFields();
        }
    }

    /**
     * Handles the event when updating an existing customer.
     *
     * This method is responsible for updating an existing customer's information
     * in the system. It retrieves the selected customer from the table view,
     * gathers updated information from UI components, validates the input,
     * and updates the customer record in the database. If no customer is selected
     * or any essential field is empty, an error message is displayed to the user.
     * Upon successful update, the customer list is refreshed, and all input fields are cleared.
     *
     * @param event The action event triggering the update of an existing customer.
     */
    @FXML
    private void handleUpdateCustomer(ActionEvent event) {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer to update.");
            alert.showAndWait();
            return;
        }

        String customerName = customerNameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phone = phoneField.getText();

        int divisionId;
        String selectedDivision = divisionComboBox.getValue();
        String divisionName;
        if (selectedDivision == null) {
            divisionId = selectedCustomer.getDivisionId();
        } else {
            divisionId = getDivisionIdFromName(selectedDivision);
        }
        divisionName = getDivisionNameById(divisionId);

        if (customerName.isEmpty()) {
            customerName = selectedCustomer.getCustomerName();
        }
        if (address.isEmpty()) {
            address = selectedCustomer.getAddress();
        }
        if (postalCode.isEmpty()) {
            postalCode = selectedCustomer.getPostalCode();
        }
        if (phone.isEmpty()) {
            phone = selectedCustomer.getPhone();
        }

        selectedCustomer.setCustomerName(customerName);
        selectedCustomer.setAddress(address);
        selectedCustomer.setPostalCode(postalCode);
        selectedCustomer.setPhone(phone);
        selectedCustomer.setDivisionId(divisionId);
        selectedCustomer.setDivisionName(divisionName);
        selectedCustomer.setLastUpdate(LocalDateTime.now());
        selectedCustomer.setLastUpdatedBy(getCurrentUser());

        updateCustomer(selectedCustomer);
        loadCustomerData();
        clearTextFields();
    }

    /**
     * Handles the event when deleting a customer.
     *
     * @param event The action event.
     */
    @FXML
    private void handleDeleteCustomer(ActionEvent event) {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmDeleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeleteAlert.setTitle("Confirm Deletion");
        confirmDeleteAlert.setHeaderText(null);
        confirmDeleteAlert.setContentText("Are you sure you want to delete the selected customer?");

        Optional<ButtonType> result = confirmDeleteAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteCustomer(selectedCustomer);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Customer Deleted");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The customer has been successfully deleted.");
            successAlert.showAndWait();

            loadCustomerData();
        }
    }

    /**
     * Handles the event when selecting a row in the table view.
     *
     * @param event The mouse event.
     */
    @FXML
    private void handleRowSelect(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                customerIdField.setText(String.valueOf(selectedCustomer.getCustomerId()));
                customerNameField.setText(selectedCustomer.getCustomerName());
                addressField.setText(selectedCustomer.getAddress());
                postalCodeField.setText(selectedCustomer.getPostalCode());
                phoneField.setText(selectedCustomer.getPhone());
                divisionComboBox.setValue(getDivisionNameById(selectedCustomer.getDivisionId()));
            }
        }
    }

    /**
     * Handles the event for viewing reports.
     *
     * @param event The action event.
     */
    @FXML
    private void handleViewReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReportsView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reports");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
 * Handles the event for managing appointments.
 *
 * @param event The action event.
 */
    @FXML
    private void handleManageAppointments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AppointmentManagement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Appointment Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearTextFields() {
        customerIdField.clear();
        customerNameField.clear();
        addressField.clear();
        postalCodeField.clear();
        phoneField.clear();
        divisionComboBox.getSelectionModel().clearSelection();
    }
}
