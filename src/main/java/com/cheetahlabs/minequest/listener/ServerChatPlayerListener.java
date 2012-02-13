package com.cheetahlabs.minequest.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.cheetahlabs.minequest.Minequest;

public class ServerChatPlayerListener implements Listener {
	public static Minequest plugin;
	
	public ServerChatPlayerListener(Minequest instance) {
		plugin = instance;
	}
	
	public void onPlayerChat(PlayerChatEvent chat) {
		Player p = chat.getPlayer();
		String message = chat.getMessage();
		String message_lower = message.toLowerCase();
		
		//Command Listeners
		if (message_lower.startsWith("/")){
			if (message_lower.startsWith("minequest", 1)){
				p.sendMessage("For help type /minequest help");
				
				//Command List
				if (message_lower.startsWith("minequest help" , 1 )) {
					p.sendMessage("Minequest commands page 1");
					p.sendMessage("/spawnnpc <Name>     Spawns NPC with the name at players location.");
					p.sendMessage("/removenpc <Name>    Deletes NPC with the given name.");
					p.sendMessage("/npcproperty <property> <value>");
				}
			if (message_lower.startsWith("spawnnpc", 1)){
				
				}	
			
			}
			
		}
	}
}
