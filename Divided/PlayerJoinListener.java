package com.k25125.Divided;

import java.io.File;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerJoinListener implements Listener {
	@EventHandler(event = PlayerJoinEvent.class, priority = EventPriority.HIGH)	
	public void onPlayerJoin(PlayerJoinEvent join) {
		Location spawn = GetPlayerSpawn.spawnLocation(join.getPlayer());
		if(!new File(Divided.config.getString("general.worldName") + File.separator + "players" + File.separator + join.getPlayer().getName() + ".dat").exists()) {
			join.getPlayer().getServer().broadcastMessage(join.getPlayer().getName() + " is spawning at (" + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + ")");
			join.getPlayer().teleport(spawn);
		}
	}
}