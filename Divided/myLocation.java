package com.k25125.Forlorn;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;

//We need some stuff Location provide
//TODO: Rename this spawnLoaction [maybe]
public class myLocation extends Location {
	//With only an X and Z value, we set the location to the first air block
	public myLocation(final World world, final double x, final double z) {
		//Start with 0
		this(world, x, 0, z);
		//So far, we haven't changed anything
		boolean isSet = false;
		
		//Start from the heighest point and work down
		for(int i = 127; i > 0; i--) {
			//If the height of i isn't empty,
			if(!super.getWorld().getBlockAt((int)super.getX(), i, (int)super.getZ()).isEmpty()) {
				//Set Y to the block directly above that
				super.setY(i+1.5);
				//We changed the value
				isSet = true;
				//We're done
				break;
			}
		}
		
		//If there are no valid heights, we must assume it's the only one we didn't check
		//This also serves the purpose of returning isValid() false
		if(!isSet) {
			super.setY(128.5);
		}
	}
	
	public myLocation(final World world, final double x, final double y, final double z) {
		this(world, x, y, z, 0, 0);
	}
	
	public myLocation(final World world, final double x, final double y, final double z, final float yaw, final float pitch) {
		super(world, x, y, z, yaw, pitch);
	}
	
	//It's about to get messy...
	public boolean isValid() {
		//If our height is >128, we're invalid. Whew! That wasn't too bad so far, right?
		if(super.getY() > 128) {
			return false;
		}
		
		//Get the material 1.5 below (remember that number from the (world, x, z) constructor?)
		Material spawnMat = Material.values()[super.getWorld().getBlockTypeIdAt((int)super.getX(), (int)(super.getY() - 1.5), (int)super.getZ())];
		//All of these materials are dangerous or result in some silly spawn. We don't want that!
		if(spawnMat == Material.STATIONARY_WATER || spawnMat == Material.VINE || spawnMat == Material.WATER_LILY || spawnMat == Material.LEAVES || spawnMat == Material.STATIONARY_LAVA || spawnMat == Material.LAVA || spawnMat == Material.CACTUS) {
			return false;
		}
		
		//Remember how we made a copy of the world? We need it now
		String name = Bukkit.getServer().getPluginManager().getPlugin("Divided").getConfig().getString("general.worldName") + "_original";
		//Get both the current chunk and the untouched, original chunk
		//TODO: Rename 'new*' to 'current*'
		Chunk newChunk = super.getWorld().getChunkAt(this);
		Chunk originalChunk = Bukkit.getServer().getWorld(name).getChunkAt(this);
		newChunk.load();
		originalChunk.load();
		
		//We can only tolerate so many changes to the world
		int diffCount = 0;
		
		//We sample a small area around the spawn to save resources. It takes far too long otherwise (16*16*128 iterations vs 8*8*8)
		//TODO: Add a config setting (general.diffRange?) to set this checked range
		for(int i = 4; i < 12; i++) {
			for(int j = 68; j > 60; j--) {
				for(int k = 4; k < 12; k++) {
					//Sample this block from the iteration compared to the original
					Material newMat = newChunk.getBlock(i, j, k).getType();
					Material originalMat = originalChunk.getBlock(i, j, k).getType();
					//If the two are different, use the blacklist below to determine if the world
					//Logs aren't on the list because they can be a common choice for player-built structures; there never seem to be enough to cause error, though
					//TODO: Make this list configurable
					//TOOD: Add logs, but check for columns to only allow trees
					if(newMat != originalMat) {
						if(!(
							(originalMat == Material.AIR && 
								(newMat == Material.LEAVES || newMat == Material.VINE || newMat == Material.LONG_GRASS || newMat == Material.SNOW || newMat == Material.RED_ROSE || newMat == Material.YELLOW_FLOWER || newMat == Material.WATER_LILY)) || 
							(originalMat == Material.STONE && 
								(newMat == Material.COAL_ORE || newMat == Material.IRON_ORE || newMat == Material.GOLD_ORE || newMat == Material.DIAMOND_ORE || newMat == Material.GRAVEL || newMat == Material.REDSTONE_ORE || newMat == Material.LAPIS_ORE)) || 
							((originalMat == Material.DIRT || originalMat == Material.GRASS) && 
								(newMat == Material.GRASS || newMat == Material.DIRT || newMat == Material.SAND))	
						)) {
							//Uh-oh, theres a difference!
							diffCount++;
						}
					}
				}
			}
		}
		
		//We're done with the original chunk
		originalChunk.unload();
		
		//Check to see if we have more differences than our threshold allows
		//I haven't seen more than 30 for natural worlds, while a small house seems to take up ~50, at the least
		//TODO: Balance this with the diffRange for defaults
		//TODO: Add a config setting (general.diffThreshold?) to set this threshold
		if(diffCount > 30) {
			return false;
		}
		
		return true;
	}
}