package com.k25125.Forlorn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerChatEvent;

//Our listener for player chats
//Will be used for nearby chat in the future
public class PlayerChatListener implements Listener {
	public Plugin plugin;
	
	public PlayerChatListener() {
		plugin = Bukkit.getPluginManager().getPlugin("Divided");
	}
	
	//Handle player chats
	@EventHandler
	public void onPlayerChat(PlayerChatEvent chat) {
		String message = chat.getMessage();
		
		//This was just an early event test. I like to keep it here for reference
		if(message.equals("Hello, World!")) {
			chat.getPlayer().sendMessage(ChatColor.YELLOW + "Hello!");
			//chat.setCancelled(true);
		}
	}
}