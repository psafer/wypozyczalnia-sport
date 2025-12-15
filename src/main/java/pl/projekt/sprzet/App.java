package pl.projekt.sprzet;

import static spark.Spark.*;

//import org.eclipse.jetty.io.ClientConnectionFactory;

import pl.projekt.sprzet.api.ClientController;
import pl.projekt.sprzet.api.EquipmentController;
import pl.projekt.sprzet.api.ReservationController;
import pl.projekt.sprzet.db.DatabaseManager;


public class App {
    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/public");

        DatabaseManager.initDatabase();

        EquipmentController.initRoutes();
        ReservationController.initRoutes();
        ClientController.initRoutes();

        System.out.println("Server running: http://localhost:8080");
    }
}
