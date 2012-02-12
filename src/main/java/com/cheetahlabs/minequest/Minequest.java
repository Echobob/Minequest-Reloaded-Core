package com.cheetahlabs.minequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.cheetahlabs.minequest.Listeners.ServerChatPlayerListener;

//Vault imports. Used to find and hook into permissions and economy. 
import net.milkbowl.vault.economy.*;
import net.milkbowl.vault.permission.*;

public class Minequest extends JavaPlugin {
	
	private String version;
	private static Server server;
	public static Minequest plugin;
	public final Logger mqLogger = Logger.getLogger("Minecraft");
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
	
	
	//Starts MineQuest
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
        version = pdfFile.getVersion();		
		this.mqLogger.info(pdfFile.getName() + "version" + version + " is Enabled");
				
	}
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.mqLogger.info(pdfFile.getName() + " is now disabled.");
		
	}

}
