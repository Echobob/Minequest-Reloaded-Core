package com.cheetahlabs.minequest.sqlcore;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.bukkit.Server;

import com.cheetahlabs.minequest.ConfigHandler;
import com.cheetahlabs.minequest.Minequest;

/*
 * 
 */

public class SqlMain {

	public static ConfigHandler configHandler;
	public static Minequest minequest = new Minequest();
	public static Server server;

//Checks if the column in the sql exists.
	public static boolean column_exists(String db, String column) throws SQLException {
		ResultSet results = configHandler.sql_server.query("SELECT * FROM " + db);
		if (results == null) return false;
		ResultSetMetaData meta = results.getMetaData();
		
		int i;
		for (i = 0; i < meta.getColumnCount(); i++) {
			if (meta.getColumnName(i + 1).equals(column)) {
				return true;
			}
		}
		return false;
	}
	
//  Adds Column to SQL File.
	private static void addColumns(String db, String cols[], String types[]) {
		int i;
		for (i = 0; i < cols.length; i++) {
			try {
				if (!column_exists(db, cols[i])) {
					configHandler.sql_server.update("ALTER TABLE " + db + " ADD COLUMN " + cols[i] + " " + types[i], false);
				}
			} catch (SQLException e) {
			}
		}
	}
//	Runs the upgrade method to create sql.
	public static void createDB() throws Exception {
		Minequest.log("Your DB is too old to determine version");
		Minequest.log("Upgrading DB to 0.50");
		
		upgradeDB(0, minequest.version());
	}
	
//Creates SQL
	public static void upgradeDB(double oldVersion, double newVersion) throws Exception {
		String cols[] = null;
		String types[] = null;
		if (oldVersion == 0) {
			cols = new String[] {
					"world",
					"x",
					"y",
					"z",
					"mode",
					"pitch",
					"yaw"
			};
			types = new String[] {
					"varchar(30) DEFAULT 'world'",
					"double DEFAULT '0'",
					"double DEFAULT '0'",
					"double DEFAULT '0'",
					"varchar(30) DEFAULT 'Quester'",
					"double DEFAULT '0'",
					"double DEFAULT '0'"
			};
			
			addColumns("questers", cols, types);
			
			cols = new String[] {
					"merc_x",
					"merc_y",
					"merc_z"
			};

			types = new String[] {
					"double DEFAULT '0'",
					"double DEFAULT '0'",
					"double DEFAULT '0'"
			};

			addColumns("towns", cols, types);
		}
		if (oldVersion < 6) {
			cols = new String[] {
					"mana",
					"max_mana"
			};
			types = new String[] {
					"int DEFAULT '10'",
					"int DEFAULT '10'"
			};
			addColumns("questers", cols, types);
			
			cols = new String[] {
					"world",
			};
			types = new String[] {
					"VARCHAR(30) DEFAULT '" + server.getWorlds().get(0).getName() + "'"
			};
			addColumns("towns", cols, types);
		}
		if (oldVersion < minequest.version()) {
			ResultSet results = configHandler.sql_server.query("SELECT * FROM questers");
			List<String> questers = new ArrayList<String>();
			List<Boolean> npc_flag = new ArrayList<Boolean>();
			
			try {
				while (results.next()) {
					questers.add(results.getString("name"));
					if (results.getString("mode").equals("Quester")) {
						npc_flag.add(false);
					} else {
						npc_flag.add(true);
					}
				}
			} catch (SQLException e) {
				Minequest.log("DB Upgrading failed - Aborting!!");
				
//TODO: Add a MQ disable here.
				throw new Exception();
			}
			
			int index = 0;
			for (String name : questers) {
				try {
					results = configHandler.sql_server.query("SELECT * FROM " + name);
					List<String> abil = new ArrayList<String>();
					List<Integer> bind = new ArrayList<Integer>();
					List<Integer> bind_2 = new ArrayList<Integer>();
					
					while (results.next()) {
						abil.add(results.getString("abil"));
						bind.add(results.getInt("bind"));
						bind_2.add(results.getInt("bind_2"));
					}
					int i;
					for (i = 0; i < abil.size(); i++) {
						configHandler.sql_server
								.update("INSERT INTO binds (name, abil, bind, bind_2) VALUES('"
										+ name
										+ "', '"
										+ abil.get(i)
										+ "', '"
										+ bind.get(i)
										+ "', '"
										+ bind_2.get(i)
										+ "')");
					}
					configHandler.sql_server.update("DROP TABLE " + name);
				} catch (Exception e) {
				}
				
				try {
					results = configHandler.sql_server.query("SELECT * FROM " + name + "_chests");
					List<String> town = new ArrayList<String>();
					List<Integer> x = new ArrayList<Integer>();
					List<Integer> y = new ArrayList<Integer>();
					List<Integer> z = new ArrayList<Integer>();
					
					while (results.next()) {
						town.add(results.getString("town"));
						x.add(results.getInt("x"));
						y.add(results.getInt("y"));
						z.add(results.getInt("z"));
					}
					int i;
					for (i = 0; i < town.size(); i++) {
						configHandler.sql_server
								.update("INSERT INTO chests (name, town, x, y, z) VALUES('"
										+ name
										+ "', '"
										+ town.get(i)
										+ "', '"
										+ x.get(i)
										+ "', '"
										+ y.get(i)
										+ "', '"
										+ z.get(i)
										+ "')");
					}
					configHandler.sql_server.update("DROP TABLE " + name + "_chests");
				} catch (Exception e) {
				}
				
				try {
					results = configHandler.sql_server.query("SELECT * FROM " + name + "_kills");
					List<String> type = new ArrayList<String>();
					List<Integer> count = new ArrayList<Integer>();
					
					while (results.next()) {
						type.add(results.getString("name"));
						count.add(results.getInt("count"));
					}
					int i;
					for (i = 0; i < type.size(); i++) {
						configHandler.sql_server
								.update("INSERT INTO kills (name, type, count) VALUES('"
										+ name
										+ "', '"
										+ type.get(i)
										+ "', '"
										+ count.get(i)
										+ "')");
					}
					configHandler.sql_server.update("DROP TABLE " + name + "_kills");
				} catch (Exception e) {
				}
				
				try {
					results = configHandler.sql_server.query("SELECT * FROM " + name + "_quests");
					List<String> type = new ArrayList<String>();
					List<String> file = new ArrayList<String>();
					
					while (results.next()) {
						type.add(results.getString("type"));
						file.add(results.getString("file"));
					}
					int i;
					for (i = 0; i < type.size(); i++) {
						configHandler.sql_server
								.update("INSERT INTO quests (name, type, file) VALUES('"
										+ name
										+ "', '"
										+ type.get(i)
										+ "', '"
										+ file.get(i)
										+ "')");
					}
					configHandler.sql_server.update("DROP TABLE " + name + "_quests");
				} catch (Exception e) {
				}
				
				if (npc_flag.get(index++)) {
					try {
						results = configHandler.sql_server.query("SELECT * FROM " + name + "_npc");
						List<String> property = new ArrayList<String>();
						List<String> value = new ArrayList<String>();
						
						while (results.next()) {
							property.add(results.getString("property"));
							value.add(results.getString("value"));
						}
						int i;
						for (i = 0; i < property.size(); i++) {
							configHandler.sql_server
									.update("INSERT INTO npc (name, property, value) VALUES('"
											+ name
											+ "', '"
											+ property.get(i)
											+ "', '"
											+ value.get(i)
											+ "')");
						}
						configHandler.sql_server.update("DROP TABLE " + name + "_npc");
					} catch (Exception e) {
					}
				}
			}
		}
		
		configHandler.sql_server.update("CREATE TABLE IF NOT EXISTS version (version VARCHAR(30))");
		configHandler.sql_server.update("DELETE FROM version");
		configHandler.sql_server.update("INSERT INTO version (version) VALUES('" + minequest.version() + "')");
		
	}

	public static void upgradeDB(String string) throws Exception {
		int oldVersion = 0;
		try {
			oldVersion = (int)(Double.parseDouble(string) * 10);
		} catch (Exception e) {
			Minequest.log("Could not detect version - Previously running dev?");
		}

		upgradeDB(oldVersion, 5);
	}
}
