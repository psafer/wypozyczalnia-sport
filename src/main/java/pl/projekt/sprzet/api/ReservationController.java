package pl.projekt.sprzet.api;

import com.google.gson.Gson;
import pl.projekt.sprzet.db.DatabaseManager;
import pl.projekt.sprzet.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class ReservationController {

    private static final Gson gson = new Gson();

    public static void initRoutes() {

        // Globalny handler błędów
        exception(Exception.class, (e, req, res) -> {
            e.printStackTrace();
            res.status(500);
            res.body("{\"error\":\"" + e.getMessage() + "\"}");
        });

        /*
         * =======================
         * GET /rezerwacje
         * =======================
         */
        get("/rezerwacje", (req, res) -> {
            res.type("application/json");
            List<Reservation> list = new ArrayList<>();
            String sql = "SELECT r.*, e.name AS equipmentName, k.firstName || ' ' || k.lastName AS clientName " +
                    "FROM rezerwacje r LEFT JOIN sprzet e ON r.equipmentId = e.id " +
                    "LEFT JOIN klienci k ON r.clientId = k.id";

            try (Connection conn = DatabaseManager.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Reservation r = new Reservation(
                            rs.getInt("id"), rs.getInt("equipmentId"), rs.getInt("clientId"),
                            rs.getString("clientName"), rs.getString("dateFrom"), rs.getString("dateTo"),
                            rs.getInt("amount"), rs.getString("status"));
                    r.setEquipmentName(rs.getString("equipmentName"));
                    r.setTotalCost(rs.getDouble("totalCost")); // Pobranie kosztu
                    list.add(r);
                }
            }
            return gson.toJson(list);
        });

        /*
         * =======================
         * POST /rezerwacje
         * =======================
         */
        post("/rezerwacje", (req, res) -> {
            res.type("application/json");
            Reservation r = gson.fromJson(req.body(), Reservation.class);

            try (Connection conn = DatabaseManager.getConnection()) {
                conn.setAutoCommit(false);

                // 1. Pobierz cenę za dobę i dostępność
                PreparedStatement eqStmt = conn
                        .prepareStatement("SELECT quantity, pricePerDay FROM sprzet WHERE id = ?");
                eqStmt.setInt(1, r.getEquipmentId());
                ResultSet rsEq = eqStmt.executeQuery();

                if (!rsEq.next())
                    return "{\"error\":\"Sprzęt nie istnieje\"}";

                int available = rsEq.getInt("quantity");
                double pricePerDay = rsEq.getDouble("pricePerDay");

                if (available < r.getAmount()) {
                    res.status(400);
                    return "{\"error\":\"Brak wystarczającej ilości sprzętu\"}";
                }

                // 2. OBLICZENIE KOSZTU
                LocalDate start = LocalDate.parse(r.getDateFrom());
                LocalDate end = LocalDate.parse(r.getDateTo());
                long days = ChronoUnit.DAYS.between(start, end);
                if (days <= 0)
                    days = 1; // Minimalnie 1 dzień

                double totalCost = days * pricePerDay * r.getAmount();
                r.setTotalCost(totalCost);

                // 3. Aktualizacja stanu i Insert rezerwacji
                PreparedStatement updateEq = conn
                        .prepareStatement("UPDATE sprzet SET quantity = quantity - ? WHERE id = ?");
                updateEq.setInt(1, r.getAmount());
                updateEq.setInt(2, r.getEquipmentId());
                updateEq.executeUpdate();

                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount, totalCost, status) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')",
                        Statement.RETURN_GENERATED_KEYS);
                insert.setInt(1, r.getEquipmentId());
                insert.setInt(2, r.getClientId());
                insert.setString(3, r.getDateFrom());
                insert.setString(4, r.getDateTo());
                insert.setInt(5, r.getAmount());
                insert.setDouble(6, totalCost);
                insert.executeUpdate();

                ResultSet keys = insert.getGeneratedKeys();
                if (keys.next())
                    r.setId(keys.getInt(1));

                conn.commit();
                res.status(201);
                return gson.toJson(r);
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        /*
         * =======================
         * DELETE /rezerwacje/:id
         * =======================
         */
        delete("/rezerwacje/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM rezerwacje WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            res.status(204);
            return "";
        });
        /*
         * =======================
         * POST /rezerwacje/:id/zwrot
         * =======================
         */
        post("/rezerwacje/:id/zwrot", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));

            try (Connection conn = DatabaseManager.getConnection()) {
                conn.setAutoCommit(false);

                // 1. Pobierz rezerwację
                PreparedStatement ps = conn.prepareStatement("""
                            SELECT equipmentId, amount, status
                            FROM rezerwacje
                            WHERE id = ?
                        """);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    res.status(404);
                    return "{\"error\":\"Rezerwacja nie istnieje\"}";
                }

                if (!"ACTIVE".equals(rs.getString("status"))) {
                    res.status(400);
                    return "{\"error\":\"Rezerwacja już została zwrócona\"}";
                }

                int equipmentId = rs.getInt("equipmentId");
                int amount = rs.getInt("amount");

                // 2. Oddaj sprzęt do puli
                PreparedStatement updateEq = conn.prepareStatement("""
                            UPDATE sprzet
                            SET quantity = quantity + ?
                            WHERE id = ?
                        """);
                updateEq.setInt(1, amount);
                updateEq.setInt(2, equipmentId);
                updateEq.executeUpdate();

                // 3. Zmień status rezerwacji
                PreparedStatement updateRez = conn.prepareStatement("""
                            UPDATE rezerwacje
                            SET status = 'RETURNED'
                            WHERE id = ?
                        """);
                updateRez.setInt(1, id);
                updateRez.executeUpdate();

                conn.commit();
                return "{\"status\":\"returned\"}";

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"Błąd zwrotu\"}";
            }
        });

    }
}
