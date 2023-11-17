package it.mauro.unrealfriends.commands;

import it.mauro.unrealfriends.Main;
import it.mauro.unrealfriends.api.ChatAPI;
import it.mauro.unrealfriends.database.SQLiteManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class FriendAcceptCommand extends Command {

    private SQLiteManager dbManager;

    private final Main pl;

    public FriendAcceptCommand(SQLiteManager dbManager, Main pl) {
        super("friendaccept");
        this.dbManager = dbManager;
        this.pl = pl;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            UUID playerUUID = player.getUniqueId();

            if (args.length == 1) {
                ProxiedPlayer senderPlayer = pl.getProxy().getPlayer(args[0]);

                if (senderPlayer != null) {
                    UUID senderUUID = senderPlayer.getUniqueId();

                    if (isFriendRequestPending(senderUUID, playerUUID)) {
                        addFriendship(playerUUID, senderUUID);
                        updatePlayersTable(senderPlayer);
                        senderPlayer.sendMessage(ChatAPI.color("&fTu e &c%player%&f siete ora amici.").replace("%player%", player.getName()));
                        player.sendMessage(ChatAPI.color("&fTu e &c%player%&f siete ora amici.").replace("%player%", senderPlayer.getName()));
                    } else {
                        player.sendMessage(ChatColor.RED+"Nessuna richiesta da " +ChatColor.DARK_RED+ senderPlayer.getName());
                    }
                } else {
                    player.sendMessage(ChatColor.RED+"Giocatore offline.");
                }
            } else {
                player.sendMessage(ChatColor.YELLOW+"Usa: /friendaccept <player>");
            }
        } else {
            sender.sendMessage("Comando per giocatori.");
        }
    }

    private boolean isFriendRequestPending(UUID senderUUID, UUID receiverUUID) {
        try {
            Connection connection = dbManager.getConnection();
            String query = "SELECT * FROM friend_requests WHERE sender_uuid = ? AND receiver_uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, senderUUID.toString());
            preparedStatement.setString(2, receiverUUID.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addFriendship(UUID playerUUID, UUID friendUUID) {
        try {
            Connection connection = dbManager.getConnection();

            String insertQuery = "INSERT INTO friends (player_uuid, friend_uuid) VALUES (?, ?), (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, playerUUID.toString());
            insertStatement.setString(2, friendUUID.toString());
            insertStatement.setString(3, friendUUID.toString());
            insertStatement.setString(4, playerUUID.toString());
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayersTable(ProxiedPlayer player) {
        try {
            Connection connection = dbManager.getConnection();

            String query = "INSERT OR IGNORE INTO players (uuid, username) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




