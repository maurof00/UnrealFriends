package it.mauro.unrealfriends.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FriendHelpCommand extends Command {
    public FriendHelpCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (strings.length==0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7DragonFriends &8[%version%]&7 made with &4❤&7 by @maurof00").replaceAll("%version%", "1.0"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&ePer aiuto fai /friend help"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        } else if (strings.length == 1 && strings[0].equalsIgnoreCase("help")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&l-----&b&l[&3Amici - Aiuto&b&l]&7&l-----"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7* &b/friendlist: &eVedi la tua lista amici. &7(/friends, /amici)"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7* &b/friendadd: &eManda la richiesta ad un giocatore."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7* &b/friendremove: &eRimuovi un giocatore dalla lista amici."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7* &b/friendaccept: &eAccetta la richiesta di un giocatore."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " &7* &b/friendreject: &eRifiuta la richiesta di un giocatore."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
        }

    }
}
