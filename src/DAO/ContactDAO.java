package DAO;

import model.Contact;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing contacts in the database.
 */
public class ContactDAO {

    /**
     * Retrieves all contacts from the database.
     *
     * @return A list of Contact objects representing all contacts.
     */
    public static List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT Contact_ID, Contact_Name FROM contacts";

        try (Statement stmt = JDBC.connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int contactId = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");

                contacts.add(new Contact(contactId, contactName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contacts;
    }
}
