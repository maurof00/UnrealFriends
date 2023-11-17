package it.mauro.unrealfriends.commands;

import it.mauro.unrealfriends.Main;
import it.mauro.unrealfriends.api.ChatAPI;
import it.mauro.unrealfriends.api.CooldownAPI;
import it.mauro.unrealfriends.database.SQLiteManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class FriendAddCommand extends Command {

    private SQLiteManager dbManager;

    private CooldownAPI cooldownManager;

    private final Main pl;

    public FriendAddCommand(SQLiteManager dbManager, Main pl, CooldownAPI cooldownManager) {
        super("friendadd");
        this.dbManager = dbManager;
        this.cooldownManager = cooldownManager;
        this.pl = pl;
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
                        player.sendMessage(ChatColor.YELLOW + "Questo giocatore è già nella tua lista amici.");
                    } else if (cooldownManager.hasCooldown(playerUUID)) {
                        player.sendMessage(ChatColor.YELLOW + "Devi aspettare prima di poter inviare un'altra richiesta.");
                    } else if (friendRequestSent(playerUUID, friendUUID)) {
                        player.sendMessage(ChatColor.YELLOW + "Hai già inviato una richiesta di amicizia a questo giocatore.");
                    } else {
                        cooldownManager.setCooldown(playerUUID);
                        sendFriendRequest(playerUUID, friendUUID);
                        updatePlayersTable(player);
                        updatePlayersTable(friend);
                        TextComponent message = new TextComponent(ChatColor.DARK_AQUA+player.getName() +ChatColor.AQUA+ " vuole essere tuo amico! ");
                        TextComponent accept = new TextComponent(ChatColor.GREEN+"ACCETTA");
                        TextComponent deny = new TextComponent(ChatColor.RED+" RIFIUTA");

                        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friendaccept " + player.getName()));
                        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friendreject " + player.getName()));

                        message.addExtra(accept);
                        message.addExtra(deny);

                        friend.sendMessage(message);
                        player.sendMessage(ChatAPI.color("&fRichiesta inviata a &c%player%").replace("%player%", friend.getName()));
                    }
                } else {
                    player.sendMessage(ChatColor.RED+"Giocatore offline.");
                }
            } else {
                player.sendMessage(ChatColor.RED+"Usa: /friendadd <player>");
            }
        } else {
            sender.sendMessage("Solo per giocatori.");
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

    private boolean friendRequestSent(UUID senderUUID, UUID receiverUUID) {
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

    private void sendFriendRequest(UUID senderUUID, UUID receiverUUID) {
        try {
            Connection connection = dbManager.getConnection();

            String query = "INSERT INTO friend_requests (sender_uuid, receiver_uuid) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, senderUUID.toString());
            preparedStatement.setString(2, receiverUUID.toString());
            preparedStatement.executeUpdate();
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





