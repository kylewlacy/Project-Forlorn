 package com.k25125.Forlorn;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

//Handle the player respawning (only triggers on death, unfortunately)
public class PlayerRespawnListener implements Listener {
	public Plugin plugin;
	
	public PlayerRespawnListener() {
		plugin = Bukkit.getPluginManager().getPlugin("Divided");
	}
	
	//Handle the event
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent respawn) {
		//If the player doesn't spawn in a bed,
		if(!respawn.isBedSpawn()) {
			//Set their respawn location to their spawn point
			//Unfortunately, there doesn't seem to be any way to set this pre-mortem. So we're stuck with some teleport shenanigans
			respawn.setRespawnLocation(GetPlayerSpawn.spawnLocation(respawn.getPlayer().getName()));
			//Save the config (on the off chance their index may have changed; better safe than sorry, I guess)
			plugin.saveConfig();
		}
	}
}