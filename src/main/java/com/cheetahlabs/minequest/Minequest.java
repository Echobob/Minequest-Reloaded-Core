/* MineQuest Reloaded the rpg plugin.
 * This is the new version of the original plugin by Jmonk.
 * 
 * 
 * @Author: Echobob, Jmonk
 * @Authors: Xu_R, Wafflegod
 * 
 */

package com.cheetahlabs.minequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
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

import com.cheetahlabs.minequest.listener.ServerChatPlayerListener;
import com.cheetahlabs.minequest.sqlcore.SqlMain;

//Vault imports. Used to find and hook into permissions and economy. 
import net.milkbowl.vault.economy.*;
import net.milkbowl.vault.permission.*;

public class Minequest extends JavaPlugin {
	
	private String versionString;
	public double version;
	private static Server server;
	public final Logger mqLogger = Logger.getLogger("Minecraft");
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
    public static MobHandler mobHandler = new MobHandler();
	public static ConfigHandler configHandler;
    public static QuesterHandler questerHandler = new QuesterHandler();
    public static TownHandler townHandler = new TownHandler();
    public static Minequest Minequest; 
    public SqlMain sqlMain = new SqlMain();
    
	public static void log(String string) {
		//log.info("[MineQuest] " + string);
		System.out.println("[MineQuest] " + string);
	}
	
	public double version(){
		PluginDescriptionFile pdfFile = this.getDescription();
        versionString = pdfFile.getVersion();
        version = Double.parseDouble(versionString);
		return version;
	}
	
	//Starts MineQuest
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);		
		version = version();
		this.mqLogger.info("Minequest" + "version" + version + " is Enabled");

		
		if ((!(new File("plugins/Minequest-Skills.jar")).exists())) {
//TODO: Add Hooks into Skills after core is complete.
			configHandler = new ConfigHandler();
        	mobHandler.checkAllMobs();
			this.mqLogger.info("Loaded Minequest-Skills. Hooked into skills.");
        
        try {
			
			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " npc (name VARCHAR(30), property VARCHAR(30), value VARCHAR(300))");

			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " binds (name VARCHAR(30), abil VARCHAR(30), bind INT, bind_2 INT)");

			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " chests (name VARCHAR(30), town VARCHAR(30), x INT, y INT, z INT)");

			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS"
					+ " kills (name VARCHAR(30), type VARCHAR(30), count INT)");

			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " quests (name VARCHAR(30), type VARCHAR(1), file VARCHAR(30))");

			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " reps (name VARCHAR(30), type VARCHAR(30), amount INT)");

			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS"
							+ " questers (name VARCHAR(30), health INT, "
							+ "max_health INT, cubes DOUBLE, exp INT, "
							+ "last_town VARCHAR(30), level INT, "
							+ "enabled INT, selected_chest VARCHAR(33), "
							+ "classes VARCHAR(150), mode VARCHAR(30) DEFAULT 'Quester', "
							+ "world VARCHAR(30) DEFAULT 'world', x DOUBLE DEFAULT '0', "
							+ "y DOUBLE DEFAULT '0', z DOUBLE DEFAULT '0', "
							+ "pitch DOUBLE DEFAULT '0', yaw DOUBLE DEFAULT '0')");
			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS classes (name VARCHAR(30), "
							+ "class VARCHAR(30), exp INT, level INT, abil_list_id INT)");
			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS abilities (abil_list_id INT, "
							+ "abil0 VARCHAR(30) DEFAULT '0', abil1 VARCHAR(30) DEFAULT '0', "
							+ "abil2 VARCHAR(30) DEFAULT '0',"
							+ "abil3 VARCHAR(30) DEFAULT '0', abil4 VARCHAR(30) DEFAULT '0', "
							+ "abil5 VARCHAR(30) DEFAULT '0', abil6 VARCHAR(30) DEFAULT '0', "
							+ "abil7 VARCHAR(30) DEFAULT '0', abil8 VARCHAR(30) DEFAULT '0', "
							+ "abil9 VARCHAR(30) DEFAULT '0')");
			
			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS idle (name VARCHAR(30), file VARCHAR(30), type INT, event_id INT, target VARCHAR(180))");

			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS towns (name VARCHAR(30), x INT, z INT, max_x INT, max_z INT, spawn_x INT, spawn_y INT, spawn_z INT, " +
					"owner VARCHAR(30), height INT, y INT, merc_x DOUBLE, merc_y DOUBLE, merc_z DOUBLE, world VARCHAR(30))");
			
			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS claims (name VARCHAR(30), x INT, z INT, max_x INT, max_z INT, " +
					"owner VARCHAR(30), height INT, y INT, world VARCHAR(30))");
			
			configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS villages (name VARCHAR(30), x INT, z INT, max_x INT, max_z INT, " +
					"owner VARCHAR(30), height INT, y INT, world VARCHAR(30))");

		} catch (Exception e) {
			this.mqLogger.info("Unable to initialize configuration");
        	this.mqLogger.info("Check configuration in MineQuest directory");
        	e.printStackTrace();
        	setEnabled(false);
        	return;
        }
        
		ResultSet results = configHandler.sql_server.query("SELECT * FROM version");
		
		try {
			if ((results == null) || (!results.next())) {
				SqlMain.createDB();
			} else {
				if (!results.getString("version").equals(version)) {
					SqlMain.upgradeDB(results.getString("version"));
				}
				results = configHandler.sql_server.query("SELECT * FROM version");
				results.next();
				log("DB Version: " + results.getString("version"));
			}
		} catch (SQLException e) {
			try {
				SqlMain.createDB();
			} catch (Exception e1) {
				log("Unable to upgrade DB1! - Disabling MineQuest");
//				e.printStackTrace();
//				e1.printStackTrace();
				onDisable();
				return;
			}
		} catch (Exception e) {
			log("Unable to upgrade DB - Disabling MineQuest");
			e.printStackTrace();
			onDisable();
			return;
		}

		List<String> names = new ArrayList<String>();
		results = configHandler.sql_server.query("SELECT * FROM towns");
		List<String> worlds = new ArrayList<String>();
		
		try {
			while (results.next()) {
				names.add(results.getString("name"));
				worlds.add(results.getString("world"));
			}
		} catch (SQLException e) {
			log("Unable to get list of towns");
		}
		
		int i = 0;
		for (String name : names) {
			townHandler.towns.add(new Town(name, getServer().getWorld(worlds.get(i++))));
		}

		blockL = new MineQuestBlockListener();
		entitityL = new MineQuestEntityListener();
		playerL = new MineQuestPlayerListener();
		worldL = new MineQuestWorldListener();
	
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		
		if (new File("MineQuest/main.script").exists()) {
			Minequest.addQuest(new Quest("MineQuest/main.script", new FullParty()));
		}
	}		
	}
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.mqLogger.info(pdfFile.getName() + " is now disabled.");
		
	}

}
