package ru.zoomov.rpname;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin implements Listener {
    private Map<String, String[]> playerNames = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        loadPlayerNames();
    }

    @Override
    public void onDisable() {
        savePlayerNames();
    }

    private void loadPlayerNames() {
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("players");
        if (section == null) {
            return;
        }

        for (String player : section.getKeys(false)) {
            String[] names = section.getString(player).split(" ");
            playerNames.put(player.toLowerCase(), names);
        }
    }

    private void savePlayerNames() {
        File file = new File(getDataFolder(), "data.yml");
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, String[]> entry : playerNames.entrySet()) {
            String name = entry.getValue()[0];
            String sname = entry.getValue()[1];
            if (name == null && sname == null) {
                continue;
            }
            if (name == null) {
                name = "";
            }
            if (sname == null) {
                sname = "";
            }
            config.set("players." + entry.getKey(), name + " " + sname);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String[] names = playerNames.get(player.getName().toLowerCase());

        if (names != null && names.length >= 2) {
            String prefix = ChatColor.GRAY + "[" + names[0] + " " + names[1] + "] ";
            player.setDisplayName(prefix + player.getName());
            player.setPlayerListName(prefix + player.getName());
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String[] names = playerNames.get(player.getName().toLowerCase());

        if (names != null && names.length >= 2) {
            String prefix = ChatColor.GRAY + "[" + names[0] + " " + names[1] + "] ";
            event.setFormat(prefix + "<%1$s> %2$s");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName().toLowerCase();

        if (command.getName().equalsIgnoreCase("name")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Используйте: /" + label + " Имя Фамилия");
                return true;
            }

            String name = args[0];
            String sname = (args.length > 1) ? args[1] : "";

            playerNames.put(playerName, new String[] { name, sname });
            sender.sendMessage(ChatColor.GREEN + "Вы установили себе " + name + " " + sname);
            return true;
        }

        if (command.getName().equalsIgnoreCase("setname")) {
            if (!player.hasPermission("rpname.admin")) {
                sender.sendMessage(ChatColor.RED + "У вас не хватает прав!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Используйте: /" + label + " [Ник] [Имя]");
                return true;
            }

            String targetName = args[0].toLowerCase();
            String name = args[1];

            String[] names = playerNames.get(targetName);
            if (names == null) {
                sender.sendMessage(ChatColor.RED + "Игрок " + targetName + " не найден!");
                return true;
            }

            playerNames.put(targetName, new String[] { name, names[1] });
            sender.sendMessage(ChatColor.GREEN + "Игроку " + targetName + " было установлено имя " + name);
            return true;
        }

        if (command.getName().equalsIgnoreCase("setsname")) {
            if (!player.hasPermission("rpname.admin")) {
                sender.sendMessage(ChatColor.RED + "У вас не хватает прав!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Используйт: /" + label + " [Ник] [Фамилия]");
                return true;
            }

            String targetName = args[0].toLowerCase();
            String sname = args[1];

            String[] names = playerNames.get(targetName);
            if (names == null) {
                sender.sendMessage(ChatColor.RED + "Игрок " + targetName + " не найден!");
                return true;
            }

            playerNames.put(targetName, new String[] { names[0], sname });
            sender.sendMessage(ChatColor.GREEN + "Игроку " + targetName + " установлена фамилия " + sname);
            return true;
        }

        return false;
    }
}