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

//This is our primary class for the plugin
public class Divided extends JavaPlugin {
	//These are some basic variables we need throughout the course of the plugin
	public static final Logger logger = Logger.getLogger("Minecraft");
	public PluginDescriptionFile pdf;
	public PluginManager pm;
	
	//This is our entry point
	public void onEnable() {
		//Setup our variables from above
		pdf = getDescription();
		pm = getServer().getPluginManager();
		
		//The following if statements setup the config files
		//TODO: Use addDefaults() instead of regular sets()
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
		
		//We need a copy of our world for checking if a chunk has been tampered with. We define it here so we don't need to regenerate it for every check
		//TODO: Add a config option for checking chunk tampering for valid spawns (which would avoid these shenanigans
		WorldCreator worldCreator = new WorldCreator(getConfig().getString("general.worldName") + "_original");
		worldCreator.copy(getServer().getWorld(getConfig().getString("general.worldName"))).createWorld();
		
		//Register all of our Listener classes to their corresponding events
		pm.registerEvents(new PlayerJoinListener(), this);
		pm.registerEvents(new PlayerRespawnListener(), this);
		pm.registerEvents(new PlayerChatListener(), this);
	}
	
	//This is our exit point
	public void onDisable() {
		//All we need to do now is save
		saveConfig();
	}
	
	//This is where we handle the commands
	//You can open the plugin.yml file for some info on these commands
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//We need these to determine who is trying to run what command
		Player player = null;
		boolean isPlayer = false;
		boolean hasPermission = false;
		
		//If a player is running a command,
		if(sender instanceof Player) {
			//We have a player
			player = (Player)sender;
			isPlayer = true;
			//We need to know if the player has permission
			hasPermission = (player.hasPermission(pdf.getName().toLowerCase() + "." + command.getName().toLowerCase()) || player.hasPermission(pdf.getName().toLowerCase() + ".*"));
		}
		
		//If the console is running a command,
		else {
			//We give them permission right now
			hasPermission = true;
		}
		
		//TODO: Ignore command cases (getName().toLowerCase().equals())
		//The commander has permission, so handle the command
		if(hasPermission) {
			// /hello - Used for debugging some code
			if(command.getName().equals("hello") && isPlayer && args.length == 0) {
				getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " says hello!");
				new myLocation(getServer().getWorld("world"), 101.5, 32.1);
				return true;
			}
			// /setindex <player> <index> - change the index to that specified
			if(command.getName().equals("setindex") && args.length == 2) {
				getConfig().set("players." + args[0].toLowerCase(), Integer.parseInt(args[1]));
				saveConfig();
				Location spawn = GetPlayerSpawn.spawnLocation(args[0]);
				logger.info(args[0] + "'s spawn is now (" + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + ")");
				return true;
			}
			// /removeindex <player> - Remove that player's index
			else if(command.getName().equals("removeindex") && args.length == 1) {
				getConfig().set("players." + args[0].toLowerCase(), null);
				saveConfig();
				logger.info(args[0] + "'s spawn has been removed");
				return true;
			}
			// /resetplayers - Reset all player data (used for debugging)
			else if(command.getName().equals("resetplayers") && args.length == 0) {
				//We need this to track if the deed was actually done
				boolean deleted = true;
				//Get all player.dat files
				File[] players = (new File(getConfig().getString("general.worldName") + File.separator + "players")).listFiles();
				
				//Kick all online players first; we'll probably get IO errors otherwise
				for (Player onlinePlayer : getServer().getOnlinePlayers()) {
					onlinePlayer.kickPlayer("Player data is being reset!");
				}
				
				//Delete each player file
				for (File playerData : players) {
					if(!playerData.delete()) {
						deleted = false;
					}
				}
				
				//Delete each player stored in config.yml
				for (String playerConfig : getConfig().getConfigurationSection("players").getKeys(false)) {
					getConfig().set("players." + playerConfig, null);
				}
				
				//Output whether or not all the player data was actually reset
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
		
		//TODO: Output an error if the command was wrong, rather than just spit the command back out
		return false;
	}
} 