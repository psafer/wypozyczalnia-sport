package pl.projekt.sprzet.model;

public class Equipment {
    private int id;
    private String name;
    private String type;
    private boolean available;

    public Equipment(int id, String name, String type, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.available = available;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
