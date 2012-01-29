package com.k25125.Forlorn;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Divided extends JavaPlugin {
	public static final Logger logger = Logger.getLogger("Minecraft");
	public PluginDescriptionFile pdf;
	public PluginManager pm;
	
	public void onEnable() {
		pdf = getDescription();
		pm = getServer().getPluginManager();
		
		if(!getConfig().contains("general.spawnDistance")) {
			getConfig().set("general.spawnDistance", 64);
		}
		
		//TODO: Fill this field programatically
		if(!getConfig().contains("general.worldName")) {
			getConfig().set("general.worldName", "world");
		}
		
		if(!getConfig().contains("players")) {
			getConfig().createSection("players");
		}
		
		saveConfig();
		
		//TODO: Add a config option for checking chunk tampering for valid spawns (which would avoid these shenanigans
		WorldCreator worldCreator = new WorldCreator(getConfig().getString("general.worldName") + "_original");
		worldCreator.copy(getServer().getWorld(getConfig().getString("general.worldName"))).createWorld();
		
		pm.registerEvents(new PlayerJoinListener(this), this);
		pm.registerEvents(new PlayerRespawnListener(this), this);
		pm.registerEvents(new PlayerChatListener(this), this);
	}
	
	public void onDisable() {
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		boolean isPlayer = false;
		boolean hasPermission = false;
		
		if(sender instanceof Player) {
			player = (Player)sender;
			isPlayer = true;
			hasPermission = (player.hasPermission(pdf.getName().toLowerCase() + "." + command.getName().toLowerCase()) || player.hasPermission(pdf.getName().toLowerCase() + ".*"));
		}
		
		else {
			hasPermission = true;
		}
		
		//TODO: Ignore the case
		if(hasPermission) {
			if(command.getName().equals("hello") && isPlayer && args.length == 0) {
				getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " says hello!");
				new myLocation(getServer().getWorld("world"), 101.5, 32.1);
				return true;
			}
			
			if(command.getName().equals("setindex") && args.length == 2) {
				getConfig().set("players." + args[0].toLowerCase(), Integer.parseInt(args[1]));
				saveConfig();
				Location spawn = GetPlayerSpawn.spawnLocation(getServer().getPlayer(args[0]), this);
				logger.info(args[0] + "'s spawn is now (" + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + ")");
				return true;
			}
			
			else if(command.getName().equals("removeindex") && args.length == 1) {
				getConfig().set("players." + args[0].toLowerCase(), null);
				saveConfig();
				logger.info(args[0] + "'s spawn has been removed");
				return true;
			}
			
			else if(command.getName().equals("resetplayers") && args.length == 0) {
				boolean deleted = true;
				File[] players = (new File(getConfig().getString("general.worldName") + File.separator + "players")).listFiles();
				
				for (Player onlinePlayer : getServer().getOnlinePlayers()) {
					onlinePlayer.kickPlayer("Player data is being reset!");
				}
				
				for (File playerData : players) {
					if(!playerData.delete()) {
						deleted = false;
					}
				}
				
				for (String playerConfig : getConfig().getConfigurationSection("players").getKeys(false)) {
					getConfig().set("players." + playerConfig, null);
				}
				
				if(deleted) {
					logger.info("All player data has been reset!");
				}
				
				else {
					logger.warning("Unable to reset player data!");
				}
				
				saveConfig();
				
				return true;
			}
		}
		
		//TODO: Output an error if the command was wrong
		return false;
	}
} 