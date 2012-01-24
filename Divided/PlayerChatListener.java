package com.k25125.Divided;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class PlayerChatListener implements Listener {
	public Divided plugin;
	
	public PlayerChatListener(Divided instance) {
		plugin = instance;
	}
	
	@EventHandler(event = PlayerChatEvent.class, priority = EventPriority.NORMAL)
	public void onPlayerChat(PlayerChatEvent chat) {
		String message = chat.getMessage();
		
		if(message.equals("Hello, World!")) {
			chat.getPlayer().sendMessage(ChatColor.YELLOW + "Hello!");
			//chat.setCancelled(true);
		}
	}
}