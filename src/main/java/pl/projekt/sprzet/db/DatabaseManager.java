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

            /* =======================
               SPRZĘT
               ======================= */
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS sprzet (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    available INTEGER NOT NULL,
                    quantity INTEGER NOT NULL DEFAULT 1,
                    totalQuantity INTEGER NOT NULL DEFAULT 1
                );
            """);

            /* =======================
               KLIENCI
               ======================= */
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS klienci (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    firstName TEXT NOT NULL,
                    lastName TEXT NOT NULL,
                    phone TEXT,
                    documentId TEXT NOT NULL UNIQUE
                );
            """);

            /* =======================
               REZERWACJE
               ======================= */
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rezerwacje (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    equipmentId INTEGER NOT NULL,
                    clientId INTEGER NOT NULL,
                    dateFrom TEXT NOT NULL,
                    dateTo TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    status TEXT NOT NULL DEFAULT 'ACTIVE',
                    FOREIGN KEY (equipmentId) REFERENCES sprzet(id),
                    FOREIGN KEY (clientId) REFERENCES klienci(id)
                );
            """);

            /* =======================
               MIGRACJE (dla starych baz)
               ======================= */
            try {
                stmt.execute("ALTER TABLE sprzet ADD COLUMN totalQuantity INTEGER NOT NULL DEFAULT quantity");
            } catch (SQLException ignored) {}

            try {
                stmt.execute("ALTER TABLE rezerwacje ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'");
            } catch (SQLException ignored) {}

            /* =======================
               DANE STARTOWE – SPRZĘT
               ======================= */
            ResultSet rsSprzet = stmt.executeQuery("SELECT COUNT(*) AS count FROM sprzet");
            if (rsSprzet.next() && rsSprzet.getInt("count") == 0) {

                stmt.execute("""
                    INSERT INTO sprzet (name, type, available, quantity, totalQuantity)
                    VALUES ('Rower górski', 'Bike', 1, 5, 5)
                """);

                stmt.execute("""
                    INSERT INTO sprzet (name, type, available, quantity, totalQuantity)
                    VALUES ('Kajak jednoosobowy', 'Water', 1, 2, 2)
                """);

                stmt.execute("""
                    INSERT INTO sprzet (name, type, available, quantity, totalQuantity)
                    VALUES ('Narty carvingowe', 'Winter', 1, 10, 10)
                """);

                stmt.execute("""
                    INSERT INTO sprzet (name, type, available, quantity, totalQuantity)
                    VALUES ('Deskorolka', 'Urban', 1, 7, 7)
                """);

                stmt.execute("""
                    INSERT INTO sprzet (name, type, available, quantity, totalQuantity)
                    VALUES ('Kijki trekkingowe', 'Outdoor', 1, 12, 12)
                """);

                System.out.println("Dodano przykładowy sprzęt.");
            }

            /* =======================
               DANE STARTOWE – KLIENCI
               ======================= */
            ResultSet rsKlienci = stmt.executeQuery("SELECT COUNT(*) AS count FROM klienci");
            if (rsKlienci.next() && rsKlienci.getInt("count") == 0) {

                stmt.execute("""
                    INSERT INTO klienci (firstName, lastName, phone, documentId)
                    VALUES ('Jan', 'Kowalski', '111-222-333', 'ABC123456')
                """);

                stmt.execute("""
                    INSERT INTO klienci (firstName, lastName, phone, documentId)
                    VALUES ('Anna', 'Nowak', '222-333-444', 'DEF234567')
                """);

                stmt.execute("""
                    INSERT INTO klienci (firstName, lastName, phone, documentId)
                    VALUES ('Piotr', 'Wiśniewski', '333-444-555', 'GHI345678')
                """);

                stmt.execute("""
                    INSERT INTO klienci (firstName, lastName, phone, documentId)
                    VALUES ('Maria', 'Wójcik', '444-555-666', 'JKL456789')
                """);

                stmt.execute("""
                    INSERT INTO klienci (firstName, lastName, phone, documentId)
                    VALUES ('Krzysztof', 'Kowalczyk', '555-666-777', 'MNO567890')
                """);

                System.out.println("Dodano przykładowych klientów.");
            }

            System.out.println("Baza danych gotowa.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
