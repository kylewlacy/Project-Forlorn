package com.k25125.Forlorn;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

//This class calculates and returns info on player spawn points
public class GetPlayerSpawn {
	//Return a player's spawn location from a player
	public static Location spawnLocation(String player) {
		//Get our plugin by name
		Plugin plugin = Bukkit.getPluginManager().getPlugin("Divided");
		//If we haven't set up our player in the config,
		if(!plugin.getConfig().contains("players." + player.toLowerCase())) {
			//Get the next index (after the last player who joined)
			int index = getMaxIndex(plugin.getConfig().getConfigurationSection("players")) + 1;
			//Set this as our player's index and save
			plugin.getConfig().set("players." + player.toLowerCase(), index);
			Divided.logger.info(player + "'s index has been set to " + index);
			plugin.saveConfig();
		}
		
		//Return the location from our player's index
		return locationFromIndex(plugin.getConfig().getInt("players." + player.toLowerCase()), plugin.getConfig().getInt("general.spawnDistance"), player);
	}
	
	//Calculate a location from an index
	public static Location locationFromIndex(int spawnIndex, int scale, String player) {
		//Get our plugin and our world by name
		Plugin plugin = Bukkit.getPluginManager().getPlugin("Divided");
		World world = Bukkit.getWorld(plugin.getConfig().getString("general.worldName"));
		//Set up our variable to store our spawn and our array to handle iteration
		myLocation spawn;
		//TODO: Set the first of the set to the world spawn
		//TODO: Save the set so we don't have to recurse every time [maybe]
		myLocation[] set = new myLocation[]{new myLocation(world, 0f, 0f)};
		
		//By default, we are not obstructed
		boolean obstructed = false;
		
		//We don't want the server to grind to a halt on iteration, so we add a cap of 1000 iterations
		for(int i = 0; i < 1000; i++) {
			//Iterate set to our spawnIndex, plus i (to compensate for error
			set = getIndicies(world, set, scale, spawnIndex+i+1);
			//The spawn we return would be the last element of the arra
			spawn = set[set.length-1];
			//Calculate if our spawn is valid
			if(spawn.isValid()) {
				//If it is, log which indicies are blocked to the console (if any)
				if(obstructed) {
					if(i == 0) {
						Divided.logger.info("Spawn index " + spawnIndex + " is obstructed");
					}
					
					else {
						Divided.logger.info("Spawn indicies " + spawnIndex + "-" + (spawnIndex+i) + " are obstructed");
					}
					
					//Because there were blocked indicies, we need to skip them in the config
					plugin.getConfig().set("players." + player.toLowerCase(), spawnIndex+i+1);
					plugin.saveConfig();
				}
				
				//If the spawn is valid, then we're done
				return (Location)spawn;
			}
			
			//If the spawn isn't valid, we set obstructed to true so we can log the invalid indicies (see above)
			obstructed = true;
		}
		
		//Oh crap; all our indicies are blocked!
		Divided.logger.warning("All possible indicies within range are obstructed");
		
		//We need to use backup measures!
		//TODO: Just set this to the world spawn and leave it be
		spawn = new myLocation(world, 0, 0);
		if(spawn.isValid()) {
			return (Location)spawn;
		}
		
		return new Location(world, 0f, 128f, 0f);
	}
	
	//Get a collection of indicies, from 0 to max
	public static myLocation[] getIndicies(World world, myLocation[] collection, int scale, int max) {
		//If the collection is already at the max, we are done; don't waste our memoriez
		if(collection.length >= max) {
			Divided.logger.info("Finished iterating!");
			return collection;
		}
		
		//Our 'origin' is the last element of the collection
		myLocation origin = collection[collection.length - 1];
		//This is where we calculate our collection for the next iteration
		List<myLocation> next = new LinkedList<myLocation>(Arrays.asList(collection));
		
		//Neighbors! Theese represent the center points of neighboring 'hexagons'
		//See this PDF of my notes to see how I came to these magic numbers:
		//http://dl.dropbox.com/u/9774870/MagicNumbers.pdf
		myLocation[] neighbors = {
			new myLocation(world, origin.getX(), origin.getZ() + (2f*scale)),
			new myLocation(world, origin.getX() + (2f*scale), origin.getZ() + scale),
			new myLocation(world, origin.getX() + (2f*scale), origin.getZ() - scale),
			new myLocation(world, origin.getX(), origin.getZ() - (2f*scale)),
			new myLocation(world, origin.getX() - (2f*scale), origin.getZ() - scale),
			new myLocation(world, origin.getX() - (2f*scale), origin.getZ() + scale)
		};
		
		//Iterate through each neighbor
		for(int i = 0; i < neighbors.length; i++) {
			//neighbors[iBack] is one CCW rotation from the center over neighbors[i] (the next hexagon hugging the last, in essence; if that makes any sense)
			int iBack = (i - 1 < 0) ? neighbors.length - 1 : i - 1;
			//If our collection contains neighbors[i] but neighbors[iBack] is still free, add neighbors[iBack]
			if(next.contains(neighbors[i]) && !next.contains(neighbors[iBack])) {
				Divided.logger.info("Iterating to (" + neighbors[iBack].getX() + ", " + neighbors[iBack].getZ() + ")");
				next.add(neighbors[iBack]);
				//Go from the top
				return getIndicies(world, next.toArray(new myLocation[0]), scale, max);
			}
		}
		
		//If no such neighbors[i]\neighbors[iBack] case was found, then the show must go on!
		Divided.logger.info("Defaulting to forward");
		//We just default to the first neighbor (forward)
		next.add(neighbors[0]);
		//Take it from the top
		return getIndicies(world, next.toArray(new myLocation[0]), scale, max);
	}
	
	//Calculate the largest index from the configuration
	public static int getMaxIndex(ConfigurationSection section) {
		//We default to -1 so the first index is 0 (since we add one to the max index in implementation)
		int max = -1;
		//Iterate through each player to find the largest one
		for(String player : section.getKeys(false)) {
			if(section.getInt(player) > max) {
				//TODO: Check to see if this is the proper behavior, or if we need to add onet to this
				max = section.getInt(player);
			}
		}
		
		//Log it for teh lulz (or debugging)
		Divided.logger.info("Max index is " + max);
		return max;
	}
}