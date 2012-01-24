package com.k25125.Divided;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

public class GetPlayerSpawn {	
	public static Location spawnLocation(Player player, Divided instance) {
		String name = player.getName().toLowerCase();
		if(!instance.getConfig().contains("players." + name)) {
			int index = getMaxIndex(instance.getConfig().getConfigurationSection("players")) + 1;
			instance.getConfig().set("players." + name, index);
			instance.logger.info(player.getName() + "'s index has been set to " + index);
			instance.saveConfig();
		}
		
		return locationFromIndex(player.getServer().getWorld(instance.getConfig().getString("general.worldName")), instance.getConfig().getInt("players." + name), instance.getConfig().getInt("general.spawnDistance"));
	}
	
	private static Location autoLocation(World world, double x, double z) {		
		for(int i = 127; i > 0; i--) {
			if(!(new Location(world, x, i, z).getBlock().isEmpty())) {
				return new Location(world, x, i + 1.5, z);
			}
		}
		
		return null;
	}
	
	public static Location locationFromIndex(World world, int spawnIndex, int scale) {
		Location spawn;
		
		boolean obstructed = false;
		
		for(int i = 0; i < 1000; i++) {
			spawn = getIndicies(world, new Location[] {autoLocation(world, 0f, 0f)}, scale, spawnIndex+i+1)[spawnIndex+i];
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
	
	public static Location[] getIndicies(World world, Location[] collection, int scale, int max) {
		if(collection.length >= max) {
			Divided.logger.info("Finished iterating!");
			return collection;
		}
		
		Location origin = collection[collection.length - 1];
		List<Location> next = new LinkedList<Location>(Arrays.asList(collection));
		
		Location[] neighbors = {
			autoLocation(world, origin.getX(), origin.getZ() + (2f*scale)),
			autoLocation(world, origin.getX() + (2f*scale), origin.getZ() + scale),
			autoLocation(world, origin.getX() + (2f*scale), origin.getZ() - scale),
			autoLocation(world, origin.getX(), origin.getZ() - (2f*scale)),
			autoLocation(world, origin.getX() - (2f*scale), origin.getZ() - scale),
			autoLocation(world, origin.getX() - (2f*scale), origin.getZ() + scale)
		};
		
		for(int i = 0; i < neighbors.length; i++) {
			int iBack = (i - 1 < 0) ? neighbors.length - 1 : i - 1;
			if(next.contains(neighbors[i]) && !next.contains(neighbors[iBack])) {
				Divided.logger.info("Iterating to (" + neighbors[iBack].getX() + ", " + neighbors[iBack].getZ() + ")");
				next.add(neighbors[iBack]);
				return getIndicies(world, next.toArray(new Location[0]), scale, max);
			}
		}
		
		Divided.logger.info("Defaulting to forward");
		next.add(neighbors[0]);
		return getIndicies(world, next.toArray(new Location[0]), scale, max);
	}
	
	public static int getMaxIndex(ConfigurationSection section) {
		int max = -1;
		for(String player : section.getKeys(false)) {
			if(section.getInt(player) > max) {
				max = section.getInt(player);
			}
		}
		
		Divided.logger.info("Max index is " + max);
		return max;
	}
}