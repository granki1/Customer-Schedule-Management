package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The CustomerSummary class represents a summary of customer information.
 * Since we have a customer class that is used to modify each field for the database table "customers", this
 * can be used for more basic information within our program, such as in our CustomerDAO and ReportsController.
 */
public class CustomerSummary {
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final StringProperty address;
    private final StringProperty postalCode;
    private final StringProperty phone;
    private final StringProperty divisionName;

    /**
     * Constructs a new CustomerSummary object with the specified attributes.
     *
     * @param customerId   The ID of the customer.
     * @param customerName The name of the customer.
     * @param address      The address of the customer.
     * @param postalCode   The postal code of the customer.
     * @param phone        The phone number of the customer.
     * @param divisionName The name of the division associated with the customer.
     */
    public CustomerSummary(int customerId, String customerName, String address, String postalCode, String phone, String divisionName) {
        this.customerId = new SimpleIntegerProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.address = new SimpleStringProperty(address);
        this.postalCode = new SimpleStringProperty(postalCode);
        this.phone = new SimpleStringProperty(phone);
        this.divisionName = new SimpleStringProperty(divisionName);
    }

    /**
     * The getters and setters.
     *
     */
    public int getCustomerId() {
        return customerId.get();
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getDivisionName() {
        return divisionName.get();
    }

    public StringProperty divisionNameProperty() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName.set(divisionName);
    }
}
