package model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * Represents a customer entity, of which all fields can be found in the "customers" table in the database.
 */
public class Customer {
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final StringProperty address;
    private final StringProperty postalCode;
    private final StringProperty phone;
    private final IntegerProperty divisionId;
    private final StringProperty divisionName;
    private final ObjectProperty<LocalDateTime> createDate;
    private final StringProperty createdBy;
    private final ObjectProperty<LocalDateTime> lastUpdate;
    private final StringProperty lastUpdatedBy;

    /**
     * Constructs a Customer object with the specified details.
     *
     * @param customerId    The ID of the customer.
     * @param customerName  The name of the customer.
     * @param address       The address of the customer.
     * @param postalCode    The postal code of the customer.
     * @param phone         The phone number of the customer.
     * @param divisionId    The ID of the division to which the customer belongs.
     * @param divisionName  The name of the division to which the customer belongs.
     * @param createDate    The date and time when the customer was created.
     * @param createdBy     The user who created the customer.
     * @param lastUpdate    The date and time when the customer was last updated.
     * @param lastUpdatedBy The user who last updated the customer.
     */
    public Customer(int customerId, String customerName, String address, String postalCode, String phone, int divisionId, String divisionName, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdatedBy) {
        this.customerId = new SimpleIntegerProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.address = new SimpleStringProperty(address);
        this.postalCode = new SimpleStringProperty(postalCode);
        this.phone = new SimpleStringProperty(phone);
        this.divisionId = new SimpleIntegerProperty(divisionId);
        this.divisionName = new SimpleStringProperty(divisionName);
        this.createDate = new SimpleObjectProperty<>(createDate);
        this.createdBy = new SimpleStringProperty(createdBy);
        this.lastUpdate = new SimpleObjectProperty<>(lastUpdate);
        this.lastUpdatedBy = new SimpleStringProperty(lastUpdatedBy);
    }

    /**
     * Gets the customer ID.
     *
     * @return The customer ID.
     */
    public int getCustomerId() {
        return customerId.get();
    }

    /**
     * Gets the customer ID property.
     *
     * @return The customer ID property.
     */
    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    /**
     * Gets the customer name.
     *
     * @return The customer name.
     */
    public String getCustomerName() {
        return customerName.get();
    }

    /**
     * Gets the customer name property.
     *
     * @return The customer name property.
     */
    public StringProperty customerNameProperty() {
        return customerName;
    }

    /**
     * Sets the customer name.
     *
     * @param customerName The customer name to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    /**
     * Gets the address of the customer.
     *
     * @return The address of the customer.
     */
    public String getAddress() {
        return address.get();
    }

    /**
     * Gets the address property of the customer.
     *
     * @return The address property of the customer.
     */
    public StringProperty addressProperty() {
        return address;
    }

    /**
     * Sets the address of the customer.
     *
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address.set(address);
    }

    /**
     * Gets the postal code of the customer.
     *
     * @return The postal code of the customer.
     */
    public String getPostalCode() {
        return postalCode.get();
    }

    /**
     * Gets the postal code property of the customer.
     *
     * @return The postal code property of the customer.
     */
    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    /**
     * Sets the postal code of the customer.
     *
     * @param postalCode The postal code to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    /**
     * Gets the phone number of the customer.
     *
     * @return The phone number of the customer.
     */
    public String getPhone() {
        return phone.get();
    }

    /**
     * Gets the phone number property of the customer.
     *
     * @return The phone number property of the customer.
     */
    public StringProperty phoneProperty() {
        return phone;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param phone The phone number to set.
     */
    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    /**
     * Gets the ID of the division to which the customer belongs.
     *
     * @return The ID of the division.
     */
    public int getDivisionId() {
        return divisionId.get();
    }

/**
 * Gets the division ID property.
 *
 * @return The division ID property.
 */
public IntegerProperty divisionIdProperty() {
    return divisionId;
}

    /**
     * Sets the ID of the division to which the customer belongs.
     *
     * @param divisionId The division ID to set.
     */
    public void setDivisionId(int divisionId) {
        this.divisionId.set(divisionId);
    }

    /**
     * Gets the name of the division to which the customer belongs.
     *
     * @return The division name.
     */
    public StringProperty divisionNameProperty() {
        return divisionName;
    }

    /**
     * Sets the name of the division to which the customer belongs.
     *
     * @param divisionName The division name to set.
     */
    public void setDivisionName(String divisionName) {
        this.divisionName.set(divisionName);
    }

    /**
     * Gets the date and time when the customer was created.
     *
     * @return The creation date and time.
     */
    public LocalDateTime getCreateDate() {
        return createDate.get();
    }

    /**
     * Gets the creation date property.
     *
     * @return The creation date property.
     */
    public ObjectProperty<LocalDateTime> createDateProperty() {
        return createDate;
    }

    /**
     * Gets the user who created the customer.
     *
     * @return The user who created the customer.
     */
    public String getCreatedBy() {
        return createdBy.get();
    }

    /**
     * Gets the created by property.
     *
     * @return The created by property.
     */
    public StringProperty createdByProperty() {
        return createdBy;
    }

    /**
     * Gets the date and time when the customer was last updated.
     *
     * @return The last update date and time.
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate.get();
    }

    /**
     * Gets the last update property.
     *
     * @return The last update property.
     */
    public ObjectProperty<LocalDateTime> lastUpdateProperty() {
        return lastUpdate;
    }

    /**
     * Sets the date and time when the customer was last updated.
     *
     * @param lastUpdate The last update date and time to set.
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate.set(lastUpdate);
    }

    /**
     * Gets the user who last updated the customer.
     *
     * @return The user who last updated the customer.
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy.get();
    }

    /**
     * Gets the last updated by property.
     *
     * @return The last updated by property.
     */
    public StringProperty lastUpdatedByProperty() {
        return lastUpdatedBy;
    }

    /**
     * Sets the user who last updated the customer.
     *
     * @param lastUpdatedBy The user who last updated the customer.
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy.set(lastUpdatedBy);
    }
}
