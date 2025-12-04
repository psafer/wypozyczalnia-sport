package pl.projekt.sprzet;

import static spark.Spark.*;

import pl.projekt.sprzet.api.EquipmentController;
import pl.projekt.sprzet.api.ReservationController;
import pl.projekt.sprzet.view.ViewController;

public class App {
    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/public");


        EquipmentController.initRoutes();
        ViewController.initViews();
        ReservationController.initRoutes();


        System.out.println("Server running: http://localhost:8080");
    }
}
