package it.mauro.unrealfriends.api;

import net.md_5.bungee.api.ChatColor;

public class ChatAPI {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
