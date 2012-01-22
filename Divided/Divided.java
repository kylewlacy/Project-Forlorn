package com.k25125.Divided;

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

public class Divided extends JavaPlugin {
	public static Divided plugin;
	public static PluginDescriptionFile pdf;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable() {
		pdf = this.getDescription();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerChatListener(), this);
		pm.registerEvents(new PlayerRespawnListener(), this);
		
		//this.logger.info(this.pdf.getName() + " is now enabled.");
	}
	
	@Override
	public void onDisable() {
		//this.logger.info(this.pdf.getName() + " is now disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Player Commands
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(command.getName().equals("hello")) {
				getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " says hello!");
			}
			
			else if(command.getName().equals("setspawn")) {
				Location spawn = new Location(getServer().getWorld("world"), Double.parseDouble(args[0]), 64d, Double.parseDouble(args[1]));
				player.sendMessage("Your spawn is now (" + args[0] + "," + args[1] + ")");
			}
		}
		
		//Console commands
		else {
			
		}
		
		return false;
	}
} 