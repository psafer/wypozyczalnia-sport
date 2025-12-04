package pl.projekt.sprzet.model;

public class Reservation {
    private int id;
    private int equipmentId;
    private String userName;
    private String dateFrom;
    private String dateTo;
    private int amount;
    private String equipmentName;

    public Reservation(int id, int equipmentId, String userName, String dateFrom, String dateTo, int amount) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.userName = userName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.amount = amount;
    }

    public int getId() { return id; }
    public int getEquipmentId() { return equipmentId; }
    public String getUserName() { return userName; }
    public String getDateFrom() { return dateFrom; }
    public String getDateTo() { return dateTo; }
    public int getAmount() {return amount;}
    public String getEquipmentName() { return equipmentName; }

    public void setId(int id) { this.id = id; }
    public void setEquipmentName(String name) { this.equipmentName = name; }
}
