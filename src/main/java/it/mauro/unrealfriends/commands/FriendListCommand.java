package it.mauro.unrealfriends.commands;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendListCommand extends Command {

    private SQLiteManager dbManager;

    public FriendListCommand(SQLiteManager dbManager) {
        super("friendlist", "", "amici", "friends");
        this.dbManager = dbManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            UUID playerUUID = player.getUniqueId();

            List<String> friendList = getFriendList(playerUUID);

            if (!friendList.isEmpty()) {
                player.sendMessage(ChatAPI.color("              "));
                player.sendMessage(ChatAPI.color("&7&l-----&b&l[&3Amici - Lista&b&l]&7&l-----"));
                player.sendMessage(ChatAPI.color("              "));

                for (String friendName : friendList) {
                    player.sendMessage(ChatColor.GRAY + " * " +ChatColor.AQUA + friendName);
                }
                player.sendMessage(ChatAPI.color("                          "));
            } else {
                player.sendMessage(ChatAPI.color("              "));
                player.sendMessage(ChatColor.RED + "La tua lista amici è vuota.");
                player.sendMessage(ChatAPI.color("              "));
            }
        } else {
            sender.sendMessage(ChatColor.RED +"Questo comando può essere eseguito solo da un giocatore.");
        }
    }

    private List<String> getFriendList(UUID playerUUID) {
        List<String> friendList = new ArrayList<>();

        try {
            Connection connection = dbManager.getConnection();
            String query = "SELECT DISTINCT username FROM friends JOIN players ON friends.friend_uuid = players.uuid " +
                    "WHERE player_uuid = ? OR friend_uuid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.setString(2, playerUUID.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                friendList.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendList;
    }
}




