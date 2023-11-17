package it.mauro.unrealfriends.commands;

import it.mauro.unrealfriends.Main;
import it.mauro.unrealfriends.database.SQLiteManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class FriendRemoveCommand extends Command {

    private final Main pl;

    private SQLiteManager dbManager;

    public FriendRemoveCommand(Main pl, SQLiteManager dbManager) {
        super("friendremove");
        this.pl = pl;
        this.dbManager = dbManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            UUID playerUUID = player.getUniqueId();

            if (args.length == 1) {
                ProxiedPlayer friend = pl.getProxy().getPlayer(args[0]);

                if (friend != null) {
                    UUID friendUUID = friend.getUniqueId();

                    if (areFriends(playerUUID, friendUUID)) {
                        removeFriendship(playerUUID, friendUUID);

                        player.sendMessage(ChatColor.WHITE+"Hai rimosso " +ChatColor.RED+ friend.getName() +ChatColor.WHITE+ " dalla tua lista amici.");
                    } else {
                        player.sendMessage(ChatColor.RED+"Questo giocatore non è nella tua lista amici.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED+"Il giocatore specificato non è online.");
                }
            } else {
                player.sendMessage(ChatColor.YELLOW+"Utilizzo: /friendremove <player>");
            }
        } else {
            sender.sendMessage(ChatColor.RED+"Comando per giocatori.");
        }
    }

    private boolean areFriends(UUID playerUUID, UUID friendUUID) {
        try {
            Connection connection = dbManager.getConnection();
            String query = "SELECT * FROM friends WHERE (player_uuid = ? AND friend_uuid = ?) OR (player_uuid = ? AND friend_uuid = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setString(2, friendUUID.toString());
            preparedStatement.setString(3, friendUUID.toString());
            preparedStatement.setString(4, playerUUID.toString());

            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void removeFriendship(UUID playerUUID, UUID friendUUID) {
        try {
            Connection connection = dbManager.getConnection();

            String deleteQuery = "DELETE FROM friends WHERE (player_uuid = ? AND friend_uuid = ?) OR (player_uuid = ? AND friend_uuid = ?)";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, playerUUID.toString());
            deleteStatement.setString(2, friendUUID.toString());
            deleteStatement.setString(3, friendUUID.toString());
            deleteStatement.setString(4, playerUUID.toString());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


