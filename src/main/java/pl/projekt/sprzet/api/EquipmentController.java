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

        // GET /sprzet (lista)
        get("/sprzet", (req, res) -> {
            res.type("application/json");

            List<Equipment> list = new ArrayList<>();

            try (Connection conn = DatabaseManager.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM sprzet")) {

                while (rs.next()) {
                    list.add(new Equipment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getInt("available") == 1,
                            rs.getInt("quantity")));
                }
            }

            return gson.toJson(list);
        });

        // GET /sprzet/:id (pojedynczy rekord)
        get("/sprzet/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params("id"));

            Equipment eq = null;

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM sprzet WHERE id = ?")) {

                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    eq = new Equipment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getInt("available") == 1,
                            rs.getInt("quantity"));
                }
            }

            return eq != null ? gson.toJson(eq) : "{}";
        });

        // POST /sprzet (dodawanie)
        post("/sprzet", (req, res) -> {
            res.type("application/json");

            Equipment eq = gson.fromJson(req.body(), Equipment.class);

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO sprzet (name, type, available) VALUES (?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, eq.getName());
                ps.setString(2, eq.getType());
                ps.setInt(3, eq.isAvailable() ? 1 : 0);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    eq.setId(keys.getInt(1));
                }
            }

            res.status(201);
            return gson.toJson(eq);
        });

        // PUT /sprzet/:id (edycja)

        put("/sprzet/:id", (req, res) -> {
            res.type("application/json");

            int id = Integer.parseInt(req.params("id"));
            Equipment eq = gson.fromJson(req.body(), Equipment.class);

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE sprzet SET name = ?, type = ?, available = ? WHERE id = ?")) {

                ps.setString(1, eq.getName());
                ps.setString(2, eq.getType());
                ps.setInt(3, eq.isAvailable() ? 1 : 0);
                ps.setInt(4, id);

                ps.executeUpdate();
            }

            return "{\"status\":\"updated\"}";
        });

        // DELETE /sprzet/:id (usuwanie)
        delete("/sprzet/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM sprzet WHERE id = ?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            res.status(204);
            return "";
        });
    }
}
