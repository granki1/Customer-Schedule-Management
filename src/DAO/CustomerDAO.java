package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import model.CustomerSummary;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static DAO.JDBC.connection;

/**
 * The CustomerDAO class is a Data Access Object (DAO) responsible for managing customer data in the database.
 * It provides methods for retrieving, adding, updating, and deleting customers, as well as fetching customer data
 * based on different criteria such as country. This class interacts with the database using JDBC and provides
 * functionality to work with JavaFX ObservableLists for UI integration. It encapsulates database queries and
 * transactions related to customer management, ensuring a clean separation of concerns between the database
 * operations and the application logic.
 */
public class CustomerDAO {

    /**
     * Retrieves all customers from the database.
     *
     * @return A list of customers.
     */
    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, c.Division_ID, " +
                "c.Create_Date, c.Created_By, c.Last_Update, c.Last_Updated_By, d.Division " +
                "FROM client_schedule.customers c " +
                "JOIN client_schedule.first_level_divisions d ON c.Division_ID = d.Division_ID";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("Customer_ID");
                String name = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                int divisionId = resultSet.getInt("Division_ID");
                String divisionName = resultSet.getString("Division");
                LocalDateTime createDate = resultSet.getTimestamp("Create_Date") != null ? resultSet.getTimestamp("Create_Date").toLocalDateTime() : null;
                String createdBy = resultSet.getString("Created_By");
                LocalDateTime lastUpdate = resultSet.getTimestamp("Last_Update") != null ? resultSet.getTimestamp("Last_Update").toLocalDateTime() : null;
                String lastUpdatedBy = resultSet.getString("Last_Updated_By");

                Customer customer = new Customer(id, name, address, postalCode, phone, divisionId, divisionName, createDate, createdBy, lastUpdate, lastUpdatedBy);
                customers.add(customer);
            }
            System.out.println("Retrieved " + customers.size() + " customers.");

        } catch (SQLException e) {
            System.out.println("Error retrieving customers: " + e.getMessage());
        }

        return customers;
    }

    /**
     * Adds a new customer to the database.
     *
     * @param customer         The customer to add.
     * @param selectedDivision The selected division for the customer.
     */
    public static void addCustomer(Customer customer, String selectedDivision) {
        String getAllIdsQuery = "SELECT Customer_ID FROM customers ORDER BY Customer_ID";
        String insertCustomerQuery = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID, Create_Date, Created_By, Last_Update, Last_Updated_By) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Statement getAllIdsStatement = connection.createStatement();
             ResultSet resultSet = getAllIdsStatement.executeQuery(getAllIdsQuery)) {

            int newId = 1;
            while (resultSet.next()) {
                int currentId = resultSet.getInt("Customer_ID");
                if (currentId == newId) {
                    newId++;
                } else {
                    break;
                }
            }

            int divisionId = getDivisionIdFromName(selectedDivision);

            try (PreparedStatement insertCustomerStatement = connection.prepareStatement(insertCustomerQuery)) {
                insertCustomerStatement.setInt(1, newId);
                insertCustomerStatement.setString(2, customer.getCustomerName());
                insertCustomerStatement.setString(3, customer.getAddress());
                insertCustomerStatement.setString(4, customer.getPostalCode());
                insertCustomerStatement.setString(5, customer.getPhone());
                insertCustomerStatement.setInt(6, divisionId);
                insertCustomerStatement.setTimestamp(7, Timestamp.valueOf(customer.getCreateDate()));
                insertCustomerStatement.setString(8, customer.getCreatedBy());
                insertCustomerStatement.setTimestamp(9, Timestamp.valueOf(customer.getLastUpdate()));
                insertCustomerStatement.setString(10, customer.getLastUpdatedBy());

                insertCustomerStatement.executeUpdate();

                System.out.println("Customer added successfully with ID: " + newId);
            }

        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    /**
     * Retrieves the division ID based on the division name.
     *
     * @param divisionName The name of the division.
     * @return The division ID.
     */
    public static int getDivisionIdFromName(String divisionName) {
        String query = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, divisionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("Division_ID");
            } else {
                System.out.println("Division not found: " + divisionName);
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Retrieves the division name based on the division ID.
     *
     * @param divisionId The ID of the division.
     * @return The name of the division.
     */
    public static String getDivisionNameById(int divisionId) {
        String query = "SELECT Division FROM client_schedule.first_level_divisions WHERE Division_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, divisionId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Division");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the information of a customer in the database.
     *
     * @param customer The customer to update.
     */
    public static void updateCustomer(Customer customer) {
        String updateSQL = "UPDATE client_schedule.customers SET " +
                "Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                "Division_ID = ?, Last_Update = ?, Last_Updated_By = ? " +
                "WHERE Customer_ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, customer.getCustomerName());
            preparedStatement.setString(2, customer.getAddress());
            preparedStatement.setString(3, customer.getPostalCode());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setInt(5, customer.getDivisionId());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(customer.getLastUpdate()));
            preparedStatement.setString(7, customer.getLastUpdatedBy());
            preparedStatement.setInt(8, customer.getCustomerId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("No customer found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }
    }

    /**
     * Deletes a customer and related appointments from the database.
     *
     * @param customer The customer to delete.
     */
    public static void deleteCustomer(Customer customer) {
        String deleteAppointmentsQuery = "DELETE FROM appointments WHERE Customer_ID = ?";
        String deleteCustomerQuery = "DELETE FROM customers WHERE Customer_ID = ?";

        try (PreparedStatement deleteAppointmentsStatement = connection.prepareStatement(deleteAppointmentsQuery);
             PreparedStatement deleteCustomerStatement = connection.prepareStatement(deleteCustomerQuery)) {

            connection.setAutoCommit(false);

            deleteAppointmentsStatement.setInt(1, customer.getCustomerId());
            deleteAppointmentsStatement.executeUpdate();

            deleteCustomerStatement.setInt(1, customer.getCustomerId());
            deleteCustomerStatement.executeUpdate();

            connection.commit();
            System.out.println("Customer and related appointments deleted successfully.");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("Error rolling back transaction: " + rollbackException.getMessage());
            }
            System.out.println("Error deleting customer: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error restoring auto-commit: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves a list of all countries from the database.
     *
     * @return A list of country names.
     */
    public static List<String> getAllCountries() {
        List<String> countries = new ArrayList<>();
        String query = "SELECT DISTINCT c.Country " +
                "FROM countries c " +
                "JOIN first_level_divisions fld ON c.Country_ID = fld.Country_ID " +
                "JOIN customers cust ON fld.Division_ID = cust.Division_ID";

        try (Statement stmt = JDBC.connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                countries.add(rs.getString("Country"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return countries;
    }

    /**
     * Retrieves a list of customers filtered by country.
     *
     * @param country The country to filter by.
     * @return An observable list of customer summaries.
     */
    public static ObservableList<CustomerSummary> getCustomersByCountry(String country) {
        ObservableList<CustomerSummary> customers = FXCollections.observableArrayList();
        String query = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, " +
                "fld.Division AS Division_Name " +
                "FROM customers c " +
                "JOIN first_level_divisions fld ON c.Division_ID = fld.Division_ID " +
                "JOIN countries co ON fld.Country_ID = co.Country_ID " +
                "WHERE co.Country = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, country);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postalCode = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                String divisionName = rs.getString("Division_Name");

                CustomerSummary customer = new CustomerSummary(customerId, customerName, address, postalCode, phone, divisionName);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}