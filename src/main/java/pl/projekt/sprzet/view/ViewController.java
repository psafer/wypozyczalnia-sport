package pl.projekt.sprzet.view;

import static spark.Spark.*;

public class ViewController {

    public static void initViews() {

        // Strona główna
        get("/", (req, res) -> {
            res.type("text/html");
            return "<h1>Wypożyczalnia Sprzętu Sportowego</h1>" +
                   "<ul>" +
                   "<li><a href='/view/sprzet'>Lista sprzętu</a></li>" +
                   "<li><a href='/view/rezerwacje'>Rezerwacje</a></li>" +
                   "<li><a href='/view/add'>Dodaj sprzęt</a></li>" +
                   "</ul>";
        });

        // Widok listy sprzętu (HTML)
        get("/view/sprzet", (req, res) -> {
            res.type("text/html");
            return "<h1>Lista sprzętu</h1>" +
                   "<p>Tu pojawi się tabela sprzętu.</p>" +
                   "<a href='/'>Powrót</a>";
        });

        // Widok formularza dodawania sprzętu
        get("/view/add", (req, res) -> {
            res.type("text/html");
            return "<h1>Dodaj sprzęt</h1>" +
                   "<form method='post' action='/view/add'>" +
                   "Nazwa: <input name='name'><br>" +
                   "Typ: <input name='type'><br>" +
                   "Dostępny: <input type='checkbox' name='available'><br>" +
                   "<button type='submit'>Dodaj</button>" +
                   "</form>";
        });

        // Obsługa POST formularza
        post("/view/add", (req, res) -> {
            String name = req.queryParams("name");
            String type = req.queryParams("type");
            boolean available = req.queryParams("available") != null;

            // TODO: wywołać EquipmentController / service

            return "<h2>Dodano sprzęt!</h2><a href='/'>Powrót</a>";
        });
    }
}
