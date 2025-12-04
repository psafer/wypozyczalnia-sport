package pl.projekt.sprzet.api;

import com.google.gson.Gson;
import pl.projekt.sprzet.model.Equipment;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class EquipmentController {

    private static List<Equipment> equipmentList = new ArrayList<>();
    private static Gson gson = new Gson();

    public static void initRoutes() {

        // przykładowe dane
        equipmentList.add(new Equipment(1, "Rower górski", "Bike", true));
        equipmentList.add(new Equipment(2, "Kajak", "Water", false));
        equipmentList.add(new Equipment(3, "Narty", "Winter", true));

        // GET /sprzet
        get("/sprzet", (req, res) -> {
            res.type("application/json");
            return gson.toJson(equipmentList);
        });

        // GET /sprzet/:id
        get("/sprzet/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            return equipmentList.stream()
                    .filter(e -> e.getId() == id)
                    .findFirst()
                    .map(gson::toJson)
                    .orElse("{}");
        });
        // POST /sprzet
        post("/sprzet", (req, res) -> {
            Equipment eq = gson.fromJson(req.body(), Equipment.class);

            int newId = equipmentList.size() + 1;
            eq.setId(newId);
            equipmentList.add(eq);

            res.status(201);
            return gson.toJson(eq);
        });

    }
}
