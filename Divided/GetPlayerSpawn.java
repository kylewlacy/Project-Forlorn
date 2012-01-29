package com.k25125.Forlorn;

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
		
		return locationFromIndex(player, instance, instance.getConfig().getInt("players." + name), instance.getConfig().getInt("general.spawnDistance"));
	}
	
	public static Location locationFromIndex(Player player, Divided instance, int spawnIndex, int scale) {
		World world = player.getServer().getWorld(instance.getConfig().getString("general.worldName"));
		myLocation spawn;
		myLocation[] set = new myLocation[]{new myLocation(world, 0f, 0f)};
		
		boolean obstructed = false;
		
		for(int i = 0; i < 1000; i++) {
			set = getIndicies(world, set, scale, spawnIndex+i+1);
			spawn = set[set.length-1];
			if(spawn.isValid()) {
				if(obstructed) {
					if(i == 0) {
						Divided.logger.info("Spawn index " + spawnIndex + " is obstructed");
					}
					
					else {
						Divided.logger.info("Spawn indicies " + spawnIndex + "-" + (spawnIndex+i) + " are obstructed");
					}
					
					instance.getConfig().set("players." + player.getName().toLowerCase(), spawnIndex+i+1);
					instance.saveConfig();
				}
				
				return (Location)spawn;
			}
			
			obstructed = true;
		}
		
		Divided.logger.warning("All possible indicies within range are obstructed");
		
		spawn = new myLocation(world, 0, 0);
		if(spawn.isValid()) {
			return (Location)spawn;
		}
		
		return new Location(world, 0f, 128f, 0f);
	}
	
	public static myLocation[] getIndicies(World world, myLocation[] collection, int scale, int max) {
		if(collection.length >= max) {
			Divided.logger.info("Finished iterating!");
			return collection;
		}
		
		myLocation origin = collection[collection.length - 1];
		List<myLocation> next = new LinkedList<myLocation>(Arrays.asList(collection));
		
		myLocation[] neighbors = {
			new myLocation(world, origin.getX(), origin.getZ() + (2f*scale)),
			new myLocation(world, origin.getX() + (2f*scale), origin.getZ() + scale),
			new myLocation(world, origin.getX() + (2f*scale), origin.getZ() - scale),
			new myLocation(world, origin.getX(), origin.getZ() - (2f*scale)),
			new myLocation(world, origin.getX() - (2f*scale), origin.getZ() - scale),
			new myLocation(world, origin.getX() - (2f*scale), origin.getZ() + scale)
		};
		
		for(int i = 0; i < neighbors.length; i++) {
			int iBack = (i - 1 < 0) ? neighbors.length - 1 : i - 1;
			if(next.contains(neighbors[i]) && !next.contains(neighbors[iBack])) {
				Divided.logger.info("Iterating to (" + neighbors[iBack].getX() + ", " + neighbors[iBack].getZ() + ")");
				next.add(neighbors[iBack]);
				return getIndicies(world, next.toArray(new myLocation[0]), scale, max);
			}
		}
		
		Divided.logger.info("Defaulting to forward");
		next.add(neighbors[0]);
		return getIndicies(world, next.toArray(new myLocation[0]), scale, max);
	}
	
	public static int getMaxIndex(ConfigurationSection section) {
		int max = -1;
		for(String player : section.getKeys(false)) {
			if(section.getInt(player) > max) {
				max = section.getInt(player) + 1;
			}
		}
		
		Divided.logger.info("Max index is " + max);
		return max;
	}
}