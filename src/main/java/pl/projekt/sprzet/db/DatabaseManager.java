package pl.projekt.sprzet.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:wypozyczalnia.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

   public static void initDatabase() {
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {

        // tabela SPRZĘT
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS sprzet (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                available INTEGER NOT NULL,
                quantity INTEGER NOT NULL DEFAULT 1
            );
        """);

        // tabela REZERWACJE
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS rezerwacje (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                equipmentId INTEGER NOT NULL,
                userName TEXT NOT NULL,
                dateFrom TEXT NOT NULL,
                dateTo TEXT NOT NULL,
                amount INTEGER NOT NULL DEFAULT 1
            );
        """);

        // Dodanie przykładowych danych, jeśli tabela sprzet jest pusta
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM sprzet");
        int count = rs.getInt("count");

        if (count == 0) {
            stmt.execute("INSERT INTO sprzet (name, type, available, quantity) VALUES ('Rower górski', 'Bike', 1, 5)");
            stmt.execute("INSERT INTO sprzet (name, type, available, quantity) VALUES ('Kajak jednoosobowy', 'Water', 1, 2)");
            stmt.execute("INSERT INTO sprzet (name, type, available, quantity) VALUES ('Narty carvingowe', 'Winter', 0, 10)");
            stmt.execute("INSERT INTO sprzet (name, type, available, quantity) VALUES ('Deskorolka', 'Urban', 1, 7)");
            stmt.execute("INSERT INTO sprzet (name, type, available, quantity) VALUES ('Kijki trekkingowe', 'Outdoor', 1, 12)");


            System.out.println("Dodano przykładowy sprzęt do bazy.");
        }

        System.out.println("Baza danych gotowa.");

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

}
