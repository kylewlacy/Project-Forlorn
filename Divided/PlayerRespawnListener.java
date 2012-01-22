package com.k25125.Divided;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
	@EventHandler(event = PlayerRespawnEvent.class, priority = EventPriority.HIGH)
	
	public void onPlayerRespawn(PlayerRespawnEvent respawn) {
		if(!respawn.isBedSpawn()) {
			respawn.setRespawnLocation(GetPlayerSpawn.spawnLocation(respawn.getPlayer()));
		}
	}
}