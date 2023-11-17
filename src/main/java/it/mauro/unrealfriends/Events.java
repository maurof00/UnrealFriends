package it.mauro.unrealfriends;

import it.mauro.unrealfriends.database.SQLiteManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Events implements Listener {

    private final Main pl;
    private final SQLiteManager manager;
    public Events(Main pl, SQLiteManager manager) {
        this.pl = pl;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        List<ProxiedPlayer> onlineFriends = getOnlineFriends(playerUUID);

        for (ProxiedPlayer friend : onlineFriends) {
            friend.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&4❤&8] &c{player}&f è entrato nel server!").replace("{player}", player.getName()));
        }
    }

    private List<ProxiedPlayer> getOnlineFriends(UUID playerUUID) {
        List<ProxiedPlayer> onlineFriends = new ArrayList<>();

        try {
            Connection connection = manager.getConnection();
            String query = "SELECT DISTINCT uuid FROM friends JOIN players ON friends.friend_uuid = players.uuid " +
                    "WHERE (player_uuid = ? OR friend_uuid = ?) AND players.uuid != ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setString(2, playerUUID.toString());
            preparedStatement.setString(3, playerUUID.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ProxiedPlayer friend = pl.getProxy().getPlayer(UUID.fromString(resultSet.getString("uuid")));
                if (friend != null && friend.isConnected()) {
                    onlineFriends.add(friend);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return onlineFriends;
    }
}
