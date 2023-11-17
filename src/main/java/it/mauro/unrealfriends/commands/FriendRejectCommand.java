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
import java.sql.SQLException;
import java.util.UUID;

public class FriendRejectCommand extends Command {

    private final Main pl;

    private SQLiteManager dbManager;

    public FriendRejectCommand(Main pl, SQLiteManager dbManager) {
        super("friendreject");
        this.pl = pl;
        this.dbManager = dbManager;
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
                        denyFriendRequest(senderUUID, playerUUID);

                        player.sendMessage(ChatAPI.color("&fHai rifiutato la richiesta di &c%player%&f.").replace("%player%", senderPlayer.getName()));
                    } else {
                        player.sendMessage(ChatColor.RED+"Nessuna richiesta da " +ChatColor.DARK_RED+ senderPlayer.getName());
                    }
                } else {
                    player.sendMessage(ChatColor.RED+"Giocatore offline.");
                }
            } else {
                player.sendMessage(ChatColor.YELLOW+"Usa: /frienddeny <player>");
            }
        } else {
            sender.sendMessage(ChatColor.RED+"Only player.");
        }
    }

    private boolean isFriendRequestPending(UUID senderUUID, UUID receiverUUID) {
        try {
            Connection connection = dbManager.getConnection();
            String query = "SELECT * FROM friend_requests WHERE sender_uuid = ? AND receiver_uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, senderUUID.toString());
            preparedStatement.setString(2, receiverUUID.toString());

            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void denyFriendRequest(UUID senderUUID, UUID receiverUUID) {
        try {
            Connection connection = dbManager.getConnection();

            // Rimuovi la richiesta di amicizia
            String deleteQuery = "DELETE FROM friend_requests WHERE sender_uuid = ? AND receiver_uuid = ?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, senderUUID.toString());
            deleteStatement.setString(2, receiverUUID.toString());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



