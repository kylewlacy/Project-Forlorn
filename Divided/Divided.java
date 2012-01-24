package com.k25125.Divided;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Divided extends JavaPlugin {
	public static PluginDescriptionFile pdf;
	public static final Logger logger = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		pdf = getDescription();
		
		if(!getConfig().contains("general.spawnDistance")) {
			getConfig().set("general.spawnDistance", 100);
		}
		
		if(!getConfig().contains("general.worldName")) {
			getConfig().set("general.worldName", "world");
		}
		
		if(!getConfig().contains("players")) {
			getConfig().createSection("players");
		}
		
		saveConfig();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerJoinListener(this), this);
		pm.registerEvents(new PlayerRespawnListener(this), this);
		pm.registerEvents(new PlayerChatListener(this), this);
	}
	
	public void onDisable() {
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		boolean hasPermission = false;
		//Player Commands
		if(sender instanceof Player) {
			Player player = (Player)sender;
			
			hasPermission = (player.hasPermission(pdf.getName().toLowerCase() + "." + command.getName().toLowerCase()) || player.hasPermission(pdf.getName().toLowerCase() + ".*"));
			if(command.getName().equals("hello")) {
				getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " says hello!");
				return true;
			}
		}
		
		else {
			hasPermission = true;
		}
		
		//Console\Admin Commands
		if(hasPermission) {
			if(command.getName().equals("setindex")) {
				getConfig().set("players." + args[0].toLowerCase(), Integer.parseInt(args[1]));
				saveConfig();
				Location spawn = GetPlayerSpawn.spawnLocation(getServer().getPlayer(args[0]), this);
				//Location spawn = GetPlayerSpawn.locationFromIndex(getServer().getWorld(Divided.config.getString("general.worldName")), Integer.parseInt(args[1]));
				logger.info(args[0] + "'s spawn is now (" + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + ")");
				return true;
			}
			
			else if(command.getName().equals("removeindex")) {
				getConfig().set("players." + args[0].toLowerCase(), null);
				logger.info(args[0] + "'s spawn has been removed");
				saveConfig();
				return true;
			}
			
			else if(command.getName().equals("resetplayers")) {
				boolean deleted = true;
				File[] players = (new File(getConfig().getString("general.worldName") + File.separator + "players")).listFiles();
				
				for (File player: players) {
					if(!player.delete()) {
						deleted = false;
					}
				}
				
				for (String player : getConfig().getConfigurationSection("players").getKeys(false)) {
					getConfig().set("players." + player, null);
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
		
		return false;
	}
} 