package com.k25125.Divided;

import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

public class GetPlayerSpawn {	
	public static Location spawnLocation(Player player) {
		int index;
		if(!Divided.config.contains("players." + player.getName())) {
			//TODO: Calculate this programmatically, rather than relying on a property
			index = Divided.config.getInt("maxIndex");
			Divided.config.set("players." + player.getName(), index);
			Divided.logger.info(player.getName() + "'s index is " + index);
			Divided.config.set("maxIndex", index+1);
		}
		
		else {
			index = Divided.config.getInt("players." + player.getName());
		}
		
		return locationFromIndex(player.getServer().getWorld(Divided.config.getString("general.worldName")), index);
	}
	
	private static Location autoLocation(World world, int x, int z) {		
		for(int i = 127; i > 0; i--) {
			if(!(new Location(world, (double)x, (double)i, (double)z).getBlock().isEmpty())) {
				return new Location(world, (double)x, (double)i + 1.5, (double)z);
			}
		}
		
		return null;
	}
	
	public static Location locationFromIndex(World world, int spawnIndex) {
		Location spawn;
		
		boolean obstructed = false;
		
		for(int i = 0; i < 1000; i++) {
			spawn = autoLocation(world, (spawnIndex+i)*100, (spawnIndex+i)*100);
			if(spawn != null) {
				if(obstructed) {
					if(i == 0) {
						Divided.logger.info("Spawn index " + spawnIndex + " is obstructed");
					}
					
					else {
						Divided.logger.info("Spawn indicies " + spawnIndex + "-" + (spawnIndex+i) + " are obstructed");
					}
				}
				
				return spawn;
			}
			
			obstructed = true;
		}
		
		Divided.logger.warning("All possible indicies within range are obstructed");
		
		spawn = autoLocation(world, 0, 0);
		if(spawn != null) {
			return spawn;
		}
		
		return new Location(world, 0f, 128f, 0f);
	}
}