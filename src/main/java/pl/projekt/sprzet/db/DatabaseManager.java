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

            /*
             * =======================
             * 1. TWORZENIE TABEL
             * =======================
             */

            // Tabela Sprzęt
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS sprzet (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            type TEXT NOT NULL,
                            available INTEGER NOT NULL,
                            quantity INTEGER NOT NULL DEFAULT 1,
                            totalQuantity INTEGER NOT NULL DEFAULT 1,
                            pricePerDay REAL NOT NULL DEFAULT 10.0
                        );
                    """);

            // Tabela Klienci
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS klienci (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            firstName TEXT NOT NULL,
                            lastName TEXT NOT NULL,
                            phone TEXT,
                            documentId TEXT NOT NULL UNIQUE
                        );
                    """);

            // Tabela Rezerwacje
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS rezerwacje (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            equipmentId INTEGER NOT NULL,
                            clientId INTEGER NOT NULL,
                            dateFrom TEXT NOT NULL,
                            dateTo TEXT NOT NULL,
                            amount INTEGER NOT NULL,
                            totalCost REAL DEFAULT 0.0,
                            status TEXT NOT NULL DEFAULT 'ACTIVE',
                            FOREIGN KEY (equipmentId) REFERENCES sprzet(id),
                            FOREIGN KEY (clientId) REFERENCES klienci(id)
                        );
                    """);

            /*
             * =======================
             * MIGRACJE
             * =======================
             */
            try {
                stmt.execute("ALTER TABLE sprzet ADD COLUMN totalQuantity INTEGER NOT NULL DEFAULT quantity");
            } catch (SQLException ignored) {
            }

            try {
                stmt.execute("ALTER TABLE rezerwacje ADD COLUMN status TEXT NOT NULL DEFAULT 'ACTIVE'");
            } catch (SQLException ignored) {
            }

            try {
                stmt.execute("ALTER TABLE sprzet ADD COLUMN pricePerDay REAL NOT NULL DEFAULT 10.0");
            } catch (SQLException ignored) {
            }

            try {
                stmt.execute("ALTER TABLE rezerwacje ADD COLUMN totalCost REAL DEFAULT 0.0");
            } catch (SQLException ignored) {
            }
            try {
                stmt.execute("ALTER TABLE rezerwacje ADD COLUMN penalty REAL DEFAULT 0.0");
            } catch (SQLException ignored) {
            }

            /*
             * =======================
             * DANE STARTOWE – SPRZĘT
             * =======================
             */
            ResultSet rsSprzet = stmt.executeQuery("SELECT COUNT(*) AS count FROM sprzet");
            if (rsSprzet.next() && rsSprzet.getInt("count") == 0) {
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity, totalQuantity, pricePerDay) VALUES ('Rower górski', 'Bike', 1, 5, 5, 45.0)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity, totalQuantity, pricePerDay) VALUES ('Kajak jednoosobowy', 'Water', 1, 2, 2, 35.0)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity, totalQuantity, pricePerDay) VALUES ('Narty carvingowe', 'Winter', 1, 10, 10, 50.0)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity, totalQuantity, pricePerDay) VALUES ('Deskorolka', 'Urban', 1, 7, 7, 15.0)");
                stmt.execute(
                        "INSERT INTO sprzet (name, type, available, quantity, totalQuantity, pricePerDay) VALUES ('Kijki trekkingowe', 'Outdoor', 1, 12, 12, 10.0)");
                System.out.println("Dodano przykładowy sprzęt z cenami.");
            }

            /*
             * =======================
             * DANE STARTOWE – KLIENCI
             * =======================
             */
            ResultSet rsKlienci = stmt.executeQuery("SELECT COUNT(*) AS count FROM klienci");
            if (rsKlienci.next() && rsKlienci.getInt("count") == 0) {
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
                System.out.println("Dodano przykładowych klientów.");
            }

            /*
             * =======================
             * 5. DANE STARTOWE – REZERWACJE
             * =======================
             */
            ResultSet rsRezerwacje = stmt.executeQuery("SELECT COUNT(*) AS count FROM rezerwacje");
            if (rsRezerwacje.next() && rsRezerwacje.getInt("count") == 0) {
                stmt.execute("""
                            INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount, totalCost, status)
                            VALUES (1, 1, '2025-12-01', '2025-12-03', 1, 90.0, 'ACTIVE')
                        """);

                stmt.execute("""
                            INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount, totalCost, status)
                            VALUES (2, 2, '2025-12-05', '2025-12-06', 1, 35.0, 'RETURNED')
                        """);

                stmt.execute("""
                            INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount, totalCost, status)
                            VALUES (3, 3, '2025-12-10', '2025-12-13', 2, 300.0, 'ACTIVE')
                        """);

                stmt.execute("""
                            INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount, totalCost, status)
                            VALUES (4, 4, '2025-12-15', '2025-12-16', 1, 15.0, 'ACTIVE')
                        """);

                stmt.execute("""
                            INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount, totalCost, status)
                            VALUES (5, 5, '2025-12-01', '2025-12-06', 2, 100.0, 'RETURNED')
                        """);
                stmt.execute("""
                        INSERT INTO rezerwacje (equipmentId, clientId, dateFrom, dateTo, amount, totalCost, status)
                        VALUES (1, 1, '2023-01-01', '2023-01-05', 1, 100.0, 'ACTIVE')
                    """);

                System.out.println("Dodano przykładowe rezerwacje.");
            }

            System.out.println("Baza danych gotowa.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}