package pl.projekt.sprzet;

import static spark.Spark.*;

import pl.projekt.sprzet.api.EquipmentController;

public class App {
    public static void main(String[] args) {

        port(8080);

        EquipmentController.initRoutes();

        System.out.println("Server running: http://localhost:8080");
    }
}
