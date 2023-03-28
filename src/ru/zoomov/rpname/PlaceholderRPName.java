package ru.zoomov.rpname;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderRPName extends PlaceholderExpansion {

    private Main plugin;

    public PlaceholderRPName(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "zoomov";
    }

    @Override
    public String getIdentifier() {
        return "rpname";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        String[] names = plugin.getPlayerNames().get(player.getName().toLowerCase());
        if (names == null || names.length < 2) {
            return "";
        }

        if (identifier.equalsIgnoreCase("name")) {
            return names[0];
        }

        if (identifier.equalsIgnoreCase("sname")) {
            return names[1];
        }

        if (identifier.equalsIgnoreCase("full")) {
            return names[0] + " " + names[1];
        }

        return null;
    }
}
