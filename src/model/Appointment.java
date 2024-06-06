package model;

import java.time.LocalDateTime;

/**
 * The Appointment class represents an appointment in the system. Here we gather all necessary and relevant
 * information for the use of our AppointmentManagement screen.
 */
public class Appointment {
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerId;
    private int userId;

    /**
     * Constructs a new Appointment object with the specified attributes.
     *
     * @param appointmentId The ID of the appointment.
     * @param title         The title of the appointment.
     * @param description   The description of the appointment.
     * @param location      The location of the appointment.
     * @param contact       The contact associated with the appointment.
     * @param type          The type of the appointment.
     * @param start         The start date and time of the appointment.
     * @param end           The end date and time of the appointment.
     * @param customerId    The ID of the customer associated with the appointment.
     * @param userId        The ID of the user who scheduled the appointment.
     */
    public Appointment(int appointmentId, String title, String description, String location, String contact, String type, LocalDateTime start, LocalDateTime end, int customerId, int userId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
        this.userId = userId;
    }

    /**
     * Gets the ID of the appointment.
     *
     * @return The appointment ID.
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * Sets the ID of the appointment.
     *
     * @param appointmentId The appointment ID to set.
     */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * Gets the title of the appointment.
     *
     * @return The appointment title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the appointment.
     *
     * @param title The appointment title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the appointment.
     *
     * @return The appointment description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the appointment.
     *
     * @param description The appointment description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the location of the appointment.
     *
     * @return The appointment location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the appointment.
     *
     * @param location The appointment location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the contact associated with the appointment.
     *
     * @return The appointment contact.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the contact associated with the appointment.
     *
     * @param contact The appointment contact to set.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Gets the type of the appointment.
     *
     * @return The appointment type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the appointment.
     *
     * @param type The appointment type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the start date and time of the appointment.
     *
     * @return The start date and time.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Sets the start date and time of the appointment.
     *
     * @param start The start date and time to set.
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Gets the end date and time of the appointment.
     *
     * @return The end date and time.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Sets the end date and time of the appointment.
     *
     * @param end The end date and time to set.
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * Gets the ID of the customer associated with the appointment.
     *
     * @return The customer ID.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the ID of the customer associated with the appointment.
     *
     * @param customerId The customer ID to set.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the ID of the user who scheduled the appointment.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who scheduled the appointment.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
