package pl.projekt.sprzet.api;

import com.google.gson.Gson;
import pl.projekt.sprzet.model.Reservation;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class ReservationController {

    private static List<Reservation> reservations = new ArrayList<>();
    private static Gson gson = new Gson();

    public static void initRoutes() {

        // GET /rezerwacje
        get("/rezerwacje", (req, res) -> {
            res.type("application/json");
            return gson.toJson(reservations);
        });

        // GET /rezerwacje/:id
        get("/rezerwacje/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            return reservations.stream()
                    .filter(r -> r.getId() == id)
                    .findFirst()
                    .map(gson::toJson)
                    .orElse("{}");
        });

        // POST /rezerwacje
        post("/rezerwacje", (req, res) -> {
            Reservation r = gson.fromJson(req.body(), Reservation.class);

            int newId = reservations.size() + 1;
            r.setId(newId);

            reservations.add(r);
            res.status(201);
            return gson.toJson(r);
        });

        // DELETE /rezerwacje/:id
        delete("/rezerwacje/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            reservations.removeIf(r -> r.getId() == id);
            res.status(204);
            return "";
        });
    }
}
