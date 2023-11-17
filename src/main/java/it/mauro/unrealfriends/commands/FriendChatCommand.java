package it.mauro.unrealfriends.commands;

import it.mauro.unrealfriends.Main;
import it.mauro.unrealfriends.database.SQLiteManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FriendChatCommand extends Command {

    private final Main pl;

    private final SQLiteManager dbmanager;

    private final Set<UUID> friendChatEnabledPlayers = new HashSet<>();

    public FriendChatCommand(Main pl, SQLiteManager dbmanager) {
        super("friendchat", "", "fc");
        this.pl = pl;
        this.dbmanager = dbmanager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            UUID playerUUID = player.getUniqueId();

            if (friendChatEnabledPlayers.contains(playerUUID)) {
                String message = String.join(" ", args);
                sendMessageToFriends(playerUUID, player.getName() + ": " + message);
            } else {
                toggleFriendChat(playerUUID);
            }
        } else {
            sender.sendMessage("Questo comando può essere eseguito solo da un giocatore.");
        }
    }

    private void toggleFriendChat(UUID playerUUID) {
        if (friendChatEnabledPlayers.contains(playerUUID)) {
            friendChatEnabledPlayers.remove(playerUUID);
            pl.getProxy().getPlayer(playerUUID).sendMessage(ChatColor.DARK_AQUA + "Hai disabilitato la chat degli amici.");
        } else {
            friendChatEnabledPlayers.add(playerUUID);
            pl.getProxy().getPlayer(playerUUID).sendMessage(ChatColor.AQUA + "Hai abilitato la chat degli amici. Ora i tuoi messaggi saranno visibili solo agli amici.");
        }
    }

    public boolean isFriendChatEnabled(UUID playerUUID) {
        return friendChatEnabledPlayers.contains(playerUUID);
    }

    public void sendMessageToFriends(UUID senderUUID, String message) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (isFriendChatEnabled(player.getUniqueId()) && areFriends(senderUUID, player.getUniqueId())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l[&3Friends&b&l] &f{msg}").replace("{msg}", message));
            }
        }
    }

    private boolean areFriends(UUID playerUUID, UUID friendUUID) {
        try {
            Connection connection = dbmanager.getConnection();
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
}


