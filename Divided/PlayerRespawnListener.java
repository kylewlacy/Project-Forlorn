 package com.k25125.Forlorn;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
	public Divided plugin;
	
	public PlayerRespawnListener(Divided instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent respawn) {
		if(!respawn.isBedSpawn()) {
			respawn.setRespawnLocation(GetPlayerSpawn.spawnLocation(respawn.getPlayer(), plugin));
			plugin.saveConfig();
		}
	}
}