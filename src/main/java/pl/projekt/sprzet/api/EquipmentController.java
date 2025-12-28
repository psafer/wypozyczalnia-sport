package pl.projekt.sprzet.api;

import com.google.gson.Gson;
import pl.projekt.sprzet.db.DatabaseManager;
import pl.projekt.sprzet.model.Equipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class EquipmentController {

    private static final Gson gson = new Gson();

    public static void initRoutes() {

        /*
         * =======================
         * GET /sprzet
         * =======================
         */
        get("/sprzet", (req, res) -> {
            res.type("application/json");
            List<Equipment> list = new ArrayList<>();
            try (Connection conn = DatabaseManager.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM sprzet")) {
                while (rs.next()) {
                    list.add(new Equipment(
                            rs.getInt("id"), rs.getString("name"), rs.getString("type"),
                            rs.getInt("quantity") > 0, rs.getInt("quantity"),
                            rs.getInt("totalQuantity"), rs.getDouble("pricePerDay")));
                }
            }
            return gson.toJson(list);
        });

        /*
         * =======================
         * POST /sprzet
         * =======================
         */
        post("/sprzet", (req, res) -> {
            res.type("application/json");
            Equipment eq = gson.fromJson(req.body(), Equipment.class);
            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO sprzet (name, type, available, quantity, totalQuantity, pricePerDay) VALUES (?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, eq.getName());
                ps.setString(2, eq.getType());
                ps.setInt(3, eq.isAvailable() ? 1 : 0);
                ps.setInt(4, eq.getQuantity());
                ps.setInt(5, eq.getQuantity());
                ps.setDouble(6, eq.getPricePerDay());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    eq.setId(keys.getInt(1));
            }
            res.status(201);
            return gson.toJson(eq);
        });

        /*
         * =======================
         * POST /sprzet/:id/add-stock
         * =======================
         */
        post("/sprzet/:id/add-stock", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));

            // body: { "amount": 5 }
            int amount = gson.fromJson(req.body(), AddStockRequest.class).amount;

            if (amount <= 0) {
                res.status(400);
                return "{\"error\":\"Ilość musi być > 0\"}";
            }

            String sql = """
                        UPDATE sprzet
                        SET quantity = quantity + ?,
                            totalQuantity = totalQuantity + ?
                        WHERE id = ?
                    """;

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, amount);
                ps.setInt(2, amount);
                ps.setInt(3, id);

                int updated = ps.executeUpdate();
                if (updated == 0) {
                    res.status(404);
                    return "{\"error\":\"Sprzęt nie istnieje\"}";
                }
            }

            return "{\"status\":\"stock updated\"}";
        });

        /*
         * =======================
         * DELETE /sprzet/:id
         * =======================
         */
        delete("/sprzet/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM sprzet WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            res.status(204);
            return "";
        });
    }

    /*
     * =======================
     * DTO pomocnicze
     * =======================
     */
    private static class AddStockRequest {
        int amount;
    }
}
