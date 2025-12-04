package pl.projekt.sprzet.model;

public class Equipment {
    private int id;
    private String name;
    private String type;
    private boolean available;
    private int quantity;

    public Equipment(int id, String name, String type, boolean available, int quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.available = available;
        this.quantity = quantity;
    }

    // GETTERY
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isAvailable() { return available; }
    public int getQuantity() {return quantity;}

    // SETTERY
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setQuantity(int quantity) {this.quantity = quantity;}
}
