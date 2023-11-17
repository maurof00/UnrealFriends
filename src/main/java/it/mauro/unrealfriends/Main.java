package it.mauro.unrealfriends;

import it.mauro.unrealfriends.api.CooldownAPI;
import it.mauro.unrealfriends.commands.*;
import it.mauro.unrealfriends.database.SQLiteManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Main extends Plugin {

    public Map<UUID, List<UUID>> friends = new HashMap<>();

    private SQLiteManager dbManager;

    private CooldownAPI cooldownAPI;

    @Override
    public void onEnable() {
        dbManager = new SQLiteManager("friends.db");
        cooldownAPI = new CooldownAPI(5);
        dbManager.connect();
        createTables();

        System.out.println("________                                     ___________      .__                   .___      ");
        System.out.println("\\______ \\____________     ____   ____   ____ \\_   _____/______|__| ____   ____    __| _/______");
        System.out.println(" |    |  \\_  __ \\__  \\   / ___\\ /  _ \\ /    \\ |    __) \\_  __ \\  |/ __ \\ /    \\  / __ |/  ___/\n" +
                " |    `   \\  | \\// __ \\_/ /_/  >  <_> )   |  \\|     \\   |  | \\/  \\  ___/|   |  \\/ /_/ |\\___ \\ \n" +
                "/_______  /__|  (____  /\\___  / \\____/|___|  /\\___  /   |__|  |__|\\___  >___|  /\\____ /____  >\n" +
                "        \\/           \\//_____/             \\/     \\/                  \\/     \\/      \\/    \\/ ");

        getProxy().getPluginManager().registerListener(this, new Events(this, dbManager));
        getProxy().getPluginManager().registerCommand(this, new FriendListCommand(dbManager));
        getProxy().getPluginManager().registerCommand(this, new FriendAddCommand(dbManager, this,cooldownAPI));
        getProxy().getPluginManager().registerCommand(this, new FriendAcceptCommand(dbManager, this));
        getProxy().getPluginManager().registerCommand(this, new FriendRejectCommand(this, dbManager));
        getProxy().getPluginManager().registerCommand(this, new FriendRemoveCommand(this, dbManager));
        getProxy().getPluginManager().registerCommand(this, new FriendChatCommand(this, dbManager));
        getProxy().getPluginManager().registerCommand(this, new FriendHelpCommand("friend"));
    }

    @Override
    public void onDisable() {
        dbManager.disconnect();
    }

    private void createTables() {
        Connection connection = dbManager.getConnection();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                        "    uuid TEXT PRIMARY KEY,\n" +
                        "    username TEXT NOT NULL\n" +
                        ");" +
                        "" +
                        "CREATE TABLE IF NOT EXISTS friends (" +
                        "    player_uuid TEXT NOT NULL," +
                        "    friend_uuid TEXT NOT NULL," +
                        "    FOREIGN KEY (player_uuid) REFERENCES players(uuid),\n" +
                        "    FOREIGN KEY (friend_uuid) REFERENCES players(uuid),\n" +
                        "    PRIMARY KEY (player_uuid, friend_uuid)" +
                        ");" +
                        "" +
                        "CREATE TABLE IF NOT EXISTS friend_requests (" +
                        "    sender_uuid TEXT NOT NULL," +
                        "    receiver_uuid TEXT NOT NULL," +
                        "    FOREIGN KEY (sender_uuid) REFERENCES players(uuid)," +
                        "    FOREIGN KEY (receiver_uuid) REFERENCES players(uuid)," +
                        "    PRIMARY KEY (sender_uuid, receiver_uuid)" +
                        ");" +
                        "" +
                        "CREATE TABLE IF NOT EXISTS blocked_players (" +
                        "    uuid TEXT PRIMARY KEY,\n" +
                        "    username TEXT NOT NULL\n" +
                        ");");
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getLogger().severe("La connessione al database non è stata stabilita correttamente.");
        }
    }
}
