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

//Vault imports. Used to find and hook into permissions and economy. 
import net.milkbowl.vault.economy.*;
import net.milkbowl.vault.permission.*;

public class Minequest extends JavaPlugin {
	
	private String version;
	private static Server server;
	
	
	//Starts Minequest
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
        version = pdfFile.getVersion();		
		server = getServer();
		
		
	}
	

}
