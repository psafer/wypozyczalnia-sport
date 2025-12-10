package pl.projekt.sprzet.model;

public class Client {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String documentId; // Np. PESEL lub nr dowodu

    public Client(int id, String firstName, String lastName, String phone, String documentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.documentId = documentId;
    }

    // Gettery
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDocumentId() {
        return documentId;
    }

    // Settery
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}