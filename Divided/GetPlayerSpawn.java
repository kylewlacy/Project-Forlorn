package com.k25125.Divided;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;

public class GetPlayerSpawn {
	public static Location spawnLocation(Player player) {
		return new Location(player.getServer().getWorld("world"), 100f, 128f, 100f);
	}
}