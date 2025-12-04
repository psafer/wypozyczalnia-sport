package pl.projekt.sprzet.api;

import com.google.gson.Gson;
import pl.projekt.sprzet.db.DatabaseManager;
import pl.projekt.sprzet.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class ReservationController {

    private static final Gson gson = new Gson();

    public static void initRoutes() {
        exception(Exception.class, (e, req, res) -> {
    e.printStackTrace();
});


        // GET /rezerwacje — lista
       get("/rezerwacje", (req, res) -> {
    res.type("application/json");

    List<Reservation> list = new ArrayList<>();

    String sql =
        "SELECT r.id, r.equipmentId, r.userName, r.dateFrom, r.dateTo, r.amount, " +
        "e.name AS equipmentName " +
        "FROM rezerwacje r " +
        "JOIN sprzet e ON r.equipmentId = e.id";

    try (Connection conn = DatabaseManager.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Reservation r = new Reservation(
                rs.getInt("id"),
                rs.getInt("equipmentId"),
                rs.getString("userName"),
                rs.getString("dateFrom"),
                rs.getString("dateTo"),
                rs.getInt("amount")
            );

            // dodatkowe pole
            r.setEquipmentName(rs.getString("equipmentName"));

            list.add(r);
        }
    }

    return gson.toJson(list);
});


        // GET /rezerwacje/:id
        get("/rezerwacje/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params("id"));

            Reservation r = null;

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM rezerwacje WHERE id = ?")) {

                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    r = new Reservation(
                            rs.getInt("id"),
                            rs.getInt("equipmentId"),
                            rs.getString("userName"),
                            rs.getString("dateFrom"),
                            rs.getString("dateTo"),
                            rs.getInt("amount")
                    );
                }
            }

            return r != null ? gson.toJson(r) : "{}";
        });

        // POST /rezerwacje — dodawanie
        post("/rezerwacje", (req, res) -> {
    res.type("application/json");

    Reservation r = gson.fromJson(req.body(), Reservation.class);

    Connection conn = DatabaseManager.getConnection();

    try {
        // 1. Sprawdzenie dostępnej ilości sprzętu
        PreparedStatement check = conn.prepareStatement(
            "SELECT quantity FROM sprzet WHERE id = ?"
        );

        check.setInt(1, r.getEquipmentId());
        ResultSet rs = check.executeQuery();

        if (!rs.next()) {
            res.status(400);
            return "{\"error\":\"Sprzęt nie istnieje\"}";
        }

        int available = rs.getInt("quantity");

        if (available < r.getAmount()) {
            res.status(400);
            return "{\"error\":\"Niewystarczająca ilość sprzętu\"}";
        }

        // 2. Zmniejszamy ilość sprzętu
        PreparedStatement update = conn.prepareStatement(
            "UPDATE sprzet SET quantity = quantity - ? WHERE id = ?"
        );
        update.setInt(1, r.getAmount());
        update.setInt(2, r.getEquipmentId());
        update.executeUpdate();

        // 3. Tworzymy rezerwację
        PreparedStatement insert = conn.prepareStatement(
            "INSERT INTO rezerwacje (equipmentId, userName, dateFrom, dateTo, amount) VALUES (?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );

        insert.setInt(1, r.getEquipmentId());
        insert.setString(2, r.getUserName());
        insert.setString(3, r.getDateFrom());
        insert.setString(4, r.getDateTo());
        insert.setInt(5, r.getAmount());
        insert.executeUpdate();

        ResultSet keys = insert.getGeneratedKeys();
        if (keys.next()) {
            r.setId(keys.getInt(1));
        }

    } finally {
        conn.close();
    }

    res.status(201);
    return gson.toJson(r);
});


        // DELETE /rezerwacje/:id
        delete("/rezerwacje/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM rezerwacje WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            res.status(204);
            return "";
        });
    }
}
