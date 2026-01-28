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
        post("/rezerwacje/:id/zwrot", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));

            try (Connection conn = DatabaseManager.getConnection()) {
                conn.setAutoCommit(false);

                // 1. Pobierz rezerwację i cenę sprzętu
                String selectSql = """
            SELECT r.equipmentId, r.amount, r.status, r.dateTo, s.pricePerDay
            FROM rezerwacje r
            JOIN sprzet s ON r.equipmentId = s.id
            WHERE r.id = ?
        """;

                PreparedStatement ps = conn.prepareStatement(selectSql);
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
                String dateToStr = rs.getString("dateTo");
                double pricePerDay = rs.getDouble("pricePerDay");

                //logika kar
                LocalDate dateTo = LocalDate.parse(dateToStr);
                LocalDate returnDate = LocalDate.now();
                double penalty = 0.0;

                // Jeśli zwracamy PO dacie 'dateTo'
                if (returnDate.isAfter(dateTo)) {
                    long overdueDays = ChronoUnit.DAYS.between(dateTo, returnDate);
                    // Wzór: spóźnione dni * cena * ilość sztuk
                    penalty = overdueDays * pricePerDay * amount;

                }

                // 2. Oddaj sprzęt do puli
                PreparedStatement updateEq = conn.prepareStatement("""
            UPDATE sprzet
            SET quantity = quantity + ?
            WHERE id = ?
        """);
                updateEq.setInt(1, amount);
                updateEq.setInt(2, equipmentId);
                updateEq.executeUpdate();

                // 3. Zmień status rezerwacji I zapisz karę
                PreparedStatement updateRez = conn.prepareStatement("""
            UPDATE rezerwacje
            SET status = 'RETURNED', penalty = ?
            WHERE id = ?
        """);
                updateRez.setDouble(1, penalty);
                updateRez.setInt(2, id);
                updateRez.executeUpdate();

                conn.commit();

                // Zwracamy JSON z informacją o karze
                return "{\"status\":\"returned\", \"penalty\": " + penalty + "}";

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"Błąd zwrotu: " + e.getMessage() + "\"}";
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
