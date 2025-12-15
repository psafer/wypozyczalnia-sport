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

            // 1. Tabela SPRZĘT
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS sprzet (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        available INTEGER NOT NULL,
                        quantity INTEGER NOT NULL DEFAULT 1
                    );
                    """);

            // 2. Tabela REZERWACJE
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS rezerwacje (
                               id INTEGER PRIMARY KEY AUTOINCREMENT,
                                equipmentId INTEGER NOT NULL,
                                clientId INTEGER NOT NULL,
                                dateFrom TEXT NOT NULL,
                                dateTo TEXT NOT NULL,
                                amount INTEGER NOT NULL,
                                FOREIGN KEY (equipmentId) REFERENCES sprzet(id),
                                FOREIGN KEY (clientId) REFERENCES klienci(id)
                        );
                    """);

            // 3. Tabela KLIENCI
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS klienci (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        phone TEXT,
                        documentId TEXT NOT NULL UNIQUE
                    );
                    """);

            // nicjalizacja danych sprzęt
            // Sprawdzamy czy tabela jest pusta
            ResultSet rsSprzet = stmt.executeQuery("SELECT COUNT(*) AS count FROM sprzet");
            int countSprzet = 0;
            if (rsSprzet.next()) {
                countSprzet = rsSprzet.getInt("count");
            }
            rsSprzet.close(); // Zamykamy ResultSet przed kolejnym zapytaniem

            if (countSprzet == 0) {
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity) VALUES ('Rower górski', 'Bike', 1, 5)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity) VALUES ('Kajak jednoosobowy', 'Water', 1, 2)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity) VALUES ('Narty carvingowe', 'Winter', 0, 10)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity) VALUES ('Deskorolka', 'Urban', 1, 7)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity) VALUES ('Kijki trekkingowe', 'Outdoor', 1, 12)");

                System.out.println("Dodano przykładowy sprzęt do bazy.");
            }

            // inicjalizacja danych klienci
            // Sprawdzamy czy tabela klientów jest pusta
            ResultSet rsKlienci = stmt.executeQuery("SELECT COUNT(*) AS count FROM klienci");
            int countKlienci = 0;
            if (rsKlienci.next()) {
                countKlienci = rsKlienci.getInt("count");
            }
            rsKlienci.close();

            if (countKlienci == 0) {
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Jan', 'Kowalski', '111-222-333', 'ABC123456')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Anna', 'Nowak', '222-333-444', 'DEF234567')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Piotr', 'Wiśniewski', '333-444-555', 'GHI345678')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Maria', 'Wójcik', '444-555-666', 'JKL456789')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Krzysztof', 'Kowalczyk', '555-666-777', 'MNO567890')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Agnieszka', 'Kamińska', '666-777-888', 'PQR678901')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Tomasz', 'Lewandowski', '777-888-999', 'STU789012')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Ewa', 'Zielińska', '888-999-000', 'VWX890123')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Michał', 'Szymański', '999-000-111', 'YZA901234')");
                stmt.execute(
                        "INSERT INTO klienci (firstName, lastName, phone, documentId) VALUES ('Karolina', 'Woźniak', '000-111-222', 'BCD012345')");

                System.out.println("Dodano 10 przykładowych klientów do bazy.");
            }
            try {
                stmt.execute("ALTER TABLE rezerwacje ADD COLUMN status TEXT DEFAULT 'ACTIVE'");
                System.out.println("Dodano status rezerwacji do bazy danych.");
            } catch (SQLException ignored) {
                // kolumna już istnieje
            }
            System.out.println("Baza danych gotowa.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}