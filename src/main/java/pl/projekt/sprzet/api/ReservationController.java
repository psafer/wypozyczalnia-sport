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
        // Obsługa nieprzewidzianych błędów
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.body("Wystąpił błąd serwera: " + e.getMessage());
        });

        // GET /rezerwacje — lista wszystkich rezerwacji
        get("/rezerwacje", (req, res) -> {
            res.type("application/json");

            List<Reservation> list = new ArrayList<>();

            // ZŁĄCZENIE TRZECH TABEL: Rezerwacje + Sprzęt + Klienci
            String sql = """
                        SELECT r.id, r.equipmentId, r.clientId, r.dateFrom, r.dateTo, r.amount,
                               e.name AS equipmentName,
                               k.firstName || ' ' || k.lastName AS clientName
                        FROM rezerwacje r
                        LEFT JOIN sprzet e ON r.equipmentId = e.id
                        LEFT JOIN klienci k ON r.clientId = k.id
                    """;

            try (Connection conn = DatabaseManager.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Reservation r = new Reservation(
                            rs.getInt("id"),
                            rs.getInt("equipmentId"),
                            rs.getInt("clientId"),
                            rs.getString("clientName"),
                            rs.getString("dateFrom"),
                            rs.getString("dateTo"),
                            rs.getInt("amount"));

                    r.setEquipmentName(rs.getString("equipmentName"));

                    list.add(r);
                }
            }

            return gson.toJson(list);
        });

        // POST /rezerwacje — dodawanie nowej rezerwacji
        post("/rezerwacje", (req, res) -> {
            res.type("application/json");

            Reservation r = gson.fromJson(req.body(), Reservation.class);
            Connection conn = DatabaseManager.getConnection();

            try {
                // 1. Sprawdzenie czy sprzęt istnieje i czy jest go dość
                PreparedStatement check = conn.prepareStatement(
                        "SELECT quantity FROM sprzet WHERE id = ?");

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

                // 2. Zmniejszamy ilość sprzętu w magazynie
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE sprzet SET quantity = quantity - ? WHERE id = ?");
                update.setInt(1, r.getAmount());
                update.setInt(2, r.getEquipmentId());
                update.executeUpdate();

                // 3. Tworzymy rezerwację (z clientId jako int)
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);

                insert.setInt(1, r.getEquipmentId());
                insert.setInt(2, r.getClientId());
                insert.setString(3, r.getDateFrom());
                insert.setString(4, r.getDateTo());
                insert.setInt(5, r.getAmount());
                insert.executeUpdate();

                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next()) {
                    r.setId(keys.getInt(1));
                }

                r.setClientName("");

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"Błąd bazy danych\"}";
            } finally {

                if (conn != null)
                    conn.close();
            }

            res.status(201);
            return gson.toJson(r);
        });

        // DELETE /rezerwacje/:id
        delete("/rezerwacje/:id", (req, res) -> {
            String idParam = req.params(":id");
            int id;

            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                res.status(400);
                return "Nieprawidłowe ID";
            }

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