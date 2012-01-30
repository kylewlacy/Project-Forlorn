package com.k25125.Forlorn;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;

//Wait for a player to join
public class PlayerJoinListener implements Listener {
	private Plugin plugin;
	
	public PlayerJoinListener() {
		plugin = Bukkit.getPluginManager().getPlugin("Divided");
	}
	
	//Handle the event
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent join) {
		Location spawn;
		
		//If the player has never joined before,
		//TODO: Debug this (just did a quick fix; still need to test)
		if(!new File(plugin.getConfig().getString("general.worldName") + File.separator + "players" + File.separator + join.getPlayer().getName() + ".dat").exists() || !plugin.getConfig().contains("players." + join.getPlayer().getName().toLowerCase())) {
			//Get the player's spawn
			spawn = GetPlayerSpawn.spawnLocation(join.getPlayer().getName());
			//Broadcast that the player has joined (for debugging)
			join.getPlayer().getServer().broadcastMessage(join.getPlayer().getName() + " is spawning at (" + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + ")");
			//Teleport the player to their spawn
			//TODO: Fix 'player hacking' bug
			join.getPlayer().teleport(spawn);
			//Save the player's spawn
			plugin.saveConfig();
		}
	}
}