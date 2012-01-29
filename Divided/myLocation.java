package com.k25125.Forlorn;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class myLocation extends Location {	
	public myLocation(final World world, final double x, final double z) {
		this(world, x, 0, z);
		
		boolean isSet = false;
		
		for(int i = 127; i > 0; i--) {
			if(!super.getWorld().getBlockAt((int)super.getX(), i, (int)super.getZ()).isEmpty()) {
				super.setY(i+1.5);
				isSet = true;
				break;
			}
		}
		
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
	
	public boolean isValid() {
		if(super.getY() > 128) {
			return false;
		}
		
		Material spawnMat = Material.values()[super.getWorld().getBlockTypeIdAt((int)super.getX(), (int)(super.getY() - 1.5), (int)super.getZ())];
		if(spawnMat == Material.STATIONARY_WATER || spawnMat == Material.VINE || spawnMat == Material.WATER_LILY || spawnMat == Material.LEAVES || spawnMat == Material.STATIONARY_LAVA || spawnMat == Material.LAVA || spawnMat == Material.CACTUS) {
			return false;
		}
		
		String name = Bukkit.getServer().getPluginManager().getPlugin("Divided").getConfig().getString("general.worldName") + "_original";
		Chunk newChunk = super.getWorld().getChunkAt(this);
		Chunk originalChunk = Bukkit.getServer().getWorld(name).getChunkAt(this);
		newChunk.load();
		originalChunk.load();
		
		int diffCount = 0;
		
		//TODO: Add a config setting (general.diffRange?) to set this checked range
		for(int i = 4; i < 12; i++) {
			for(int j = 68; j > 60; j--) {
				for(int k = 4; k < 12; k++) {
					Material newMat = newChunk.getBlock(i, j, k).getType();
					Material originalMat = originalChunk.getBlock(i, j, k).getType();
					if(newMat != originalMat) {
						if(!(
							(originalMat == Material.AIR && 
								(newMat == Material.LEAVES || newMat == Material.VINE || newMat == Material.LONG_GRASS || newMat == Material.SNOW || newMat == Material.RED_ROSE || newMat == Material.YELLOW_FLOWER || newMat == Material.WATER_LILY)) || 
							(originalMat == Material.STONE && 
								(newMat == Material.COAL_ORE || newMat == Material.IRON_ORE || newMat == Material.GOLD_ORE || newMat == Material.DIAMOND_ORE || newMat == Material.GRAVEL || newMat == Material.REDSTONE_ORE || newMat == Material.LAPIS_ORE)) || 
							((originalMat == Material.DIRT || originalMat == Material.GRASS) && 
								(newMat == Material.GRASS || newMat == Material.DIRT || newMat == Material.SAND))	
						)) {
							diffCount++;
							//Divided.logger.info("(" + ((originalChunk.getX()*16)+i) + ", " + j + ", " + ((originalChunk.getZ()*16)+k) + ") is different (is " + newMat + ", was " + originalMat + ")");
						}
					}
				}
			}
		}
		
		//Divided.logger.info("There were " + diffCount + " differences");
		
		originalChunk.unload();
		
		//TODO: Add a config setting (general.diffThreshold?) to set this threshold
		if(diffCount > 30) {
			return false;
		}
		
		return true;
	}
}