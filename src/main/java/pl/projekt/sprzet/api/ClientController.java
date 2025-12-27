package pl.projekt.sprzet.api;

import com.google.gson.Gson;
import pl.projekt.sprzet.db.DatabaseManager;
import pl.projekt.sprzet.model.Client;
import pl.projekt.sprzet.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class ClientController {

    private static final Gson gson = new Gson();

    public static void initRoutes() {

        // GET /klienci - Pobierz listę wszystkich klientów
        get("/klienci", (req, res) -> {
            res.type("application/json");
            List<Client> clients = new ArrayList<>();

            try (Connection conn = DatabaseManager.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM klienci")) {

                while (rs.next()) {
                    clients.add(new Client(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("phone"),
                            rs.getString("documentId")));
                }
            }
            return gson.toJson(clients);
        });

        // POST /klienci - Dodaj nowego klienta
        post("/klienci", (req, res) -> {
            res.type("application/json");
            Client newClient = gson.fromJson(req.body(), Client.class);

            String sql = "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, newClient.getFirstName());
                ps.setString(2, newClient.getLastName());
                ps.setString(3, newClient.getPhone());
                ps.setString(4, newClient.getDocumentId());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    newClient.setId(keys.getInt(1));
                }
            }

            res.status(201);
            return gson.toJson(newClient);
        });

        // PUT /klienci/:id - Edycja danych klienta
        put("/klienci/:id", (req, res) -> {
            res.type("application/json");

            // Pobranie id z adresu url np. klienci/5
            int id = Integer.parseInt(req.params("id"));

            // pobieramy nowe dane z JSONA wysłanego przez formularz
            Client client = gson.fromJson(req.body(), Client.class);

            String sql = "UPDATE klienci SET firstName = ?, lastName = ?, phone = ?, documentId = ? WHERE id = ?";

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, client.getFirstName());
                ps.setString(2, client.getLastName());
                ps.setString(3, client.getPhone());
                ps.setString(4, client.getDocumentId());
                ps.setInt(5, id);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    res.status(404);// jesli nie ma takiego id
                    return "{\"error\":\"Client not found\"}";
                }
            }
            return "{\"status\":\"updated\"}";
        });

        // delete klienci/id
        delete("/klienci/:id", (req, res) -> {
            // Musisz pobrać parametr z requestu (req.params)
            String idParam = req.params("id");
            int id = Integer.parseInt(idParam);
            String sql = "DELETE FROM klienci WHERE id = ?";

            try (Connection conn = DatabaseManager.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // status 204 - sukces bez tresci
            res.status(204);
            return "";
        });

        // get /klienci/:id/history - pobranie historii wypożyczeń klienta
        get("/klienci/:id/history", (req, res) -> {
            res.type("application/json");
            int clientId = Integer.parseInt(req.params("id"));
            List<Reservation> history = new ArrayList<>();

            String sql = """
                SELECT r.id, r.equipmentId, r.clientId, r.dateFrom, r.dateTo, r.amount, r.status,
                       s.name AS equipmentName,
                       k.firstName || ' ' || k.lastName AS clientName
                FROM rezerwacje r
                JOIN sprzet s ON r.equipmentId = s.id
                JOIN klienci k ON r.clientId = k.id
                WHERE r.clientId = ?
                ORDER BY r.dateFrom DESC
            """;

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, clientId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Reservation rez = new Reservation(
                        rs.getInt("id"),
                        rs.getInt("equipmentId"),
                        rs.getInt("clientId"),
                        rs.getString("clientName"),
                        rs.getString("dateFrom"),
                        rs.getString("dateTo"),
                        rs.getInt("amount"),
                        rs.getString("status")
                    );
                    rez.setEquipmentName(rs.getString("equipmentName"));
                    
                    history.add(rez);
                }
            }
            return gson.toJson(history);
        });
    }
}