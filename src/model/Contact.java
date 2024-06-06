package model;

/**
 * The Contact class represents a contact entity, which can be found in our database in the "contacts" table.
 */
public class Contact {
    private int contactId;
    private String contactName;

    /**
     * Constructs a new Contact object with the specified attributes.
     *
     * @param contactId   The ID of the contact.
     * @param contactName The name of the contact.
     */
    public Contact(int contactId, String contactName) {
        this.contactId = contactId;
        this.contactName = contactName;
    }

    /**
     * Gets the ID of the contact.
     *
     * @return The contact ID.
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Sets the ID of the contact.
     *
     * @param contactId The contact ID to set.
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Gets the name of the contact.
     *
     * @return The contact name.
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets the name of the contact.
     *
     * @param contactName The contact name to set.
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Returns the string representation of the contact (its name).
     *
     * @return The contact name.
     */
    @Override
    public String toString() {
        return contactName;
    }
}
