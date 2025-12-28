package pl.projekt.sprzet.model;

public class Equipment {
    private int id;
    private String name;
    private String type;
    private boolean available;
    private int quantity;
    private int totalQuantity;
    private double pricePerDay;

    public Equipment(int id, String name, String type, boolean available, int quantity, int totalQuantity,
            double pricePerDay) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.available = available;
        this.quantity = quantity;
        this.totalQuantity = totalQuantity;
        this.pricePerDay = pricePerDay;
    }

    // Gettery i Settery
    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}