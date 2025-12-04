package pl.projekt.sprzet.model;

public class Reservation {
    private int id;
    private int equipmentId;
    private String userName;
    private String dateFrom;
    private String dateTo;

    public Reservation(int id, int equipmentId, String userName, String dateFrom, String dateTo) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.userName = userName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public int getId() { return id; }
    public int getEquipmentId() { return equipmentId; }
    public String getUserName() { return userName; }
    public String getDateFrom() { return dateFrom; }
    public String getDateTo() { return dateTo; }

    public void setId(int id) { this.id = id; }
}
