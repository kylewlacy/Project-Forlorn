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
	public static FileConfiguration config;
	public static File configFile;
	
	public static final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable() {
		pdf = getDescription();
		config = getConfig();
		
		if(!config.contains("general.spawnDistance")) {
			config.set("general.spawnDistance", 100);
		}
		
		if(!config.contains("general.worldName")) {
			config.set("general.worldName", "world");
		}
		
		if(!config.contains("maxIndex")) {
			config.set("maxIndex", 0);
		}
		
		saveConfig();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerJoinListener(), this);
		pm.registerEvents(new PlayerRespawnListener(), this);
		pm.registerEvents(new PlayerChatListener(), this);
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				logger.info("Saving config file");
			}
		}, 600L, 1200L);
		
		//this.logger.info(this.pdf.getName() + " is now enabled.");
	}
	
	@Override
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
				config.set("players." + args[0], args[1]);
				Location spawn = GetPlayerSpawn.locationFromIndex(getServer().getWorld(Divided.config.getString("general.worldName")), Integer.parseInt(args[1]));
				logger.info(args[0] + "'s spawn is now (" + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + ")");
				return true;
			}
			
			else if(command.getName().equals("removeindex")) {
				config.set("players." + args[0], null);
				logger.info(args[0] + "'s spawn has been removed");
				return true;
			}
			
			else if(command.getName().equals("resetplayers")) {
				boolean deleted = true;
				File[] players = (new File(Divided.config.getString("general.worldName") + File.separator + "players")).listFiles();
				
				for (File player: players) {
					config.set("players." + player.getName().substring(0, player.getName().lastIndexOf(".")), null);
					config.set("maxIndex", 0);
					if(!player.delete()) {
						deleted = false;
					}
				}
				
				if(deleted) {
					logger.info("All player data has been reset!");
				}
				
				else {
					logger.warning("Unable to delete all player.dat files!");
				}
				
				saveConfig();
				
				return true;
			}
		}
		
		return false;
	}
} 