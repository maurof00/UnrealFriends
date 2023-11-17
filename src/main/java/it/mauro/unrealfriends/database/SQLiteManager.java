package it.mauro.unrealfriends.database;

import java.sql.*;
import java.util.UUID;

public class SQLiteManager {

    private Connection connection;
    private String dbFile;

    public SQLiteManager(String dbFile) {
        this.dbFile = dbFile;

        try {
            // Carica esplicitamente il driver JDBC per SQLite
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // Verifica se il file del database esiste, altrimenti crea il file
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);

            // Crea la tabella 'friends' se non esiste
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        if (connection != null) {
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables() {
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS friend_requests (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "sender_uuid TEXT NOT NULL, " +
                    "receiver_uuid TEXT NOT NULL)");

            statement.execute("CREATE TABLE IF NOT EXISTS friends (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "player_uuid TEXT NOT NULL, " +
                    "friend_uuid TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addFriend(UUID playerUUID, UUID friendUUID) throws SQLException {
        if (connection != null) {
            try {
                String query = "INSERT INTO friends (player_uuid, friend_uuid) VALUES (?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, playerUUID.toString());
                statement.setString(2, friendUUID.toString());
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFriend(UUID playerUUID, UUID friendUUID) throws SQLException {
        if (connection != null) {
            try {
                String query = "DELETE FROM friends WHERE player_uuid = ? AND friend_uuid = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, playerUUID.toString());
                statement.setString(2, friendUUID.toString());
                statement.execute();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean areFriends(UUID playerUUID, UUID friendUUID) throws SQLException {
        if (connection != null) {
            try {
                String query = "SELECT * FROM friends WHERE player_uuid = ? AND friend_uuid = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, playerUUID.toString());
                statement.setString(2, friendUUID.toString());
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}




