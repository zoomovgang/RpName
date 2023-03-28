package ru.zoomov.rpname;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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

public class Main extends JavaPlugin implements Listener {
   private Map<String, String[]> playerNames = new HashMap();

   public void onEnable() {
      this.getServer().getPluginManager().registerEvents(this, this);
      this.loadPlayerNames();
   }

   public void onDisable() {
      this.savePlayerNames();
   }

   private void loadPlayerNames() {
      File file = new File(this.getDataFolder(), "data.yml");
      if (file.exists()) {
         YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
         ConfigurationSection section = config.getConfigurationSection("players");
         if (section != null) {
            Iterator var5 = section.getKeys(false).iterator();

            while(var5.hasNext()) {
               String player = (String)var5.next();
               String[] names = section.getString(player).split(" ");
               this.playerNames.put(player.toLowerCase(), names);
            }

         }
      }
   }

   private void savePlayerNames() {
      File file = new File(this.getDataFolder(), "data.yml");
      YamlConfiguration config = new YamlConfiguration();
      Iterator var4 = this.playerNames.entrySet().iterator();

      while(true) {
         Entry entry;
         String name;
         String sname;
         do {
            if (!var4.hasNext()) {
               try {
                  config.save(file);
               } catch (IOException var7) {
                  var7.printStackTrace();
               }

               return;
            }

            entry = (Entry)var4.next();
            name = ((String[])entry.getValue())[0];
            sname = ((String[])entry.getValue())[1];
         } while(name == null && sname == null);

         if (name == null) {
            name = "";
         }

         if (sname == null) {
            sname = "";
         }

         config.set("players." + (String)entry.getKey(), name + " " + sname);
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      String[] names = (String[])this.playerNames.get(player.getName().toLowerCase());
      if (names != null && names.length >= 2) {
         String prefix = ChatColor.GRAY + "[" + names[0] + " " + names[1] + "] ";
         player.setDisplayName(prefix + player.getName());
         player.setPlayerListName(prefix + player.getName());
      }

   }

   @EventHandler
   public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      String[] names = (String[])this.playerNames.get(player.getName().toLowerCase());
      if (names != null && names.length >= 2) {
         String prefix = ChatColor.GRAY + "[" + names[0] + " " + names[1] + "] ";
         event.setFormat(prefix + "<%1$s> %2$s");
      }

   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("Только игроки могут использовать эту команду!");
         return true;
      } else {
         Player player = (Player)sender;
         String playerName = player.getName().toLowerCase();
         String targetName;
         String sname;
         if (command.getName().equalsIgnoreCase("name")) {
            if (args.length == 0) {
               sender.sendMessage(ChatColor.RED + "Используйте: /" + label + " Имя Фамилия");
               return true;
            } else {
               targetName = args[0];
               sname = args.length > 1 ? args[1] : "";
               this.playerNames.put(playerName, new String[]{targetName, sname});
               sender.sendMessage(ChatColor.GREEN + "Вы установили себе " + targetName + " " + sname);
               return true;
            }
         } else {
            String[] names;
            if (command.getName().equalsIgnoreCase("setname")) {
               if (!player.hasPermission("rpname.admin")) {
                  sender.sendMessage(ChatColor.RED + "У вас не хватает прав!");
                  return true;
               } else if (args.length < 2) {
                  sender.sendMessage(ChatColor.RED + "Используйте: /" + label + " [Ник] [Имя]");
                  return true;
               } else {
                  targetName = args[0].toLowerCase();
                  sname = args[1];
                  names = (String[])this.playerNames.get(targetName);
                  if (names == null) {
                     sender.sendMessage(ChatColor.RED + "Игрок " + targetName + " не найден!");
                     return true;
                  } else {
                     this.playerNames.put(targetName, new String[]{sname, names[1]});
                     sender.sendMessage(ChatColor.GREEN + "Игроку " + targetName + " было установлено имя " + sname);
                     return true;
                  }
               }
            } else if (command.getName().equalsIgnoreCase("setsname")) {
               if (!player.hasPermission("rpname.admin")) {
                  sender.sendMessage(ChatColor.RED + "У вас не хватает прав!");
                  return true;
               } else if (args.length < 2) {
                  sender.sendMessage(ChatColor.RED + "Используйт: /" + label + " [Ник] [Фамилия]");
                  return true;
               } else {
                  targetName = args[0].toLowerCase();
                  sname = args[1];
                  names = (String[])this.playerNames.get(targetName);
                  if (names == null) {
                     sender.sendMessage(ChatColor.RED + "Игрок " + targetName + " не найден!");
                     return true;
                  } else {
                     this.playerNames.put(targetName, new String[]{names[0], sname});
                     sender.sendMessage(ChatColor.GREEN + "Игроку " + targetName + " установлена фамилия " + sname);
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }
   }
}
