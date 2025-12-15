package pl.projekt.sprzet.model;

public class Reservation {
    private int id;
    private int equipmentId;
    private int clientId;
    private String clientName;
    private String dateFrom;
    private String dateTo;
    private int amount;
    private String equipmentName;
    private String status;


    // Konstruktor
    public Reservation(int id, int equipmentId, int clientId, String clientName, String dateFrom, String dateTo,int amount, String status) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.amount = amount;
        this.status = status;
    }

    // Gettery i Settery
    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public int getClientId() {
        return clientId;
    } // Getter do ID

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    } // Getter do nazwy

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public int getAmount() {
        return amount;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String name) {
        this.equipmentName = name;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}