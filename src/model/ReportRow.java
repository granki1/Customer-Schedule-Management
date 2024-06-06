package model;

/**
 * The ReportRow class represents the three reports for the ReportsView screen.
 * It models how the table will look for each report.
 */
public class ReportRow {
    private int appointmentId;
    private String title;
    private String type;
    private String description;
    private String start;
    private String end;
    private int customerId;
    private String month;
    private int count;

    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String divisionName;

    /**
     * Constructs a new ReportRow object with the first report parameters.
     *
     * @param type  The type of the appointment.
     * @param month The month associated with the summary.
     * @param count The count of appointments for the summary.
     */
    public ReportRow(String type, String month, int count) {
        this.type = type;
        this.month = month;
        this.count = count;
    }

    /**
     * Constructs a new ReportRow object with the first report parameters.
     *
     * @param appointmentId The ID of the appointment.
     * @param title         The title of the appointment.
     * @param type          The type of the appointment.
     * @param description   The description of the appointment.
     * @param start         The start time of the appointment.
     * @param end           The end time of the appointment.
     * @param customerId    The ID of the customer associated with the appointment.
     */
    public ReportRow(int appointmentId, String title, String type, String description, String start, String end, int customerId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
        this.customerId = customerId;
    }

    /**
     * Constructs a new ReportRow object with last report parameters.
     *
     * @param customerId    The ID of the customer.
     * @param customerName  The name of the customer.
     * @param address       The address of the customer.
     * @param postalCode    The postal code of the customer.
     * @param phone         The phone number of the customer.
     * @param divisionName  The name of the division associated with the customer.
     * @param countryName   The name of the country associated with the customer.
     */
    public ReportRow(int customerId, String customerName, String address, String postalCode, String phone, String divisionName, String countryName) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionName = divisionName;
    }

    /**
     * The following are all the getters and setter.
     *
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }
}
