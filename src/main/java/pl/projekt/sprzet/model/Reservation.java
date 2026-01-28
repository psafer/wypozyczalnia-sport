package pl.projekt.sprzet.model;

public class Reservation {
    private int id;
    private int equipmentId;
    private int clientId;
    private String clientName;
    private String equipmentName;
    private String dateFrom;
    private String dateTo;
    private int amount;
    private String status;
    private double totalCost;

    // Konstruktor
    public Reservation(int id, int equipmentId, int clientId, String clientName, String dateFrom, String dateTo,
            int amount, String status) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.amount = amount;
        this.status = status;
    }

    // GETTERY I SETTERY
    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public int getClientId() {
        return clientId;
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

    public String getStatus() {
        return status;
    }
     public String getClientName() {
        return clientName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    private double penalty;

    public double getPenalty(){
        return penalty;
    }
    public void setPenalty(double penalty){
        this.penalty = penalty;
    }
}