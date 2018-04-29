package com.projectkorra.projectkorra.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;

public class DBCooldownManager {

	public DBCooldownManager() {
		setupCooldowns();
	}

	public void setupCooldowns() {
		// Create pk_cooldown_ids table
		if (!DBConnection.sql.tableExists("pk_cooldown_ids")) {
			ProjectKorra.log.info("Creating pk_cooldown_ids table");
			String query = "CREATE TABLE `pk_cooldown_ids` (id INTEGER PRIMARY KEY AUTOINCREMENT, cooldown_name TEXT(256) NOT NULL);";
			if (DBConnection.sql instanceof MySQL) {
				query = "CREATE TABLE `pk_cooldown_ids` (id INTEGER PRIMARY KEY AUTO_INCREMENT, cooldown_name VARCHAR(256) NOT NULL);";
			}
			DBConnection.sql.modifyQuery(query, false);
		}
		// Create pk_cooldowns table
		if (!DBConnection.sql.tableExists("pk_cooldowns")) {
			ProjectKorra.log.info("Creating pk_cooldowns table");
			String query = "CREATE TABLE `pk_cooldowns` (uuid TEXT(36) PRIMARY KEY, cooldown_id INTEGER NOT NULL, value BIGINT);";
			if (DBConnection.sql instanceof MySQL) {
				query = "CREATE TABLE `pk_cooldowns` (uuid VARCHAR(36) PRIMARY KEY, cooldown_id INTEGER NOT NULL, value BIGINT);";
			}
			DBConnection.sql.modifyQuery(query, false);
		}
	}

	public int getCooldownId(String cooldown, boolean async) {
		try (ResultSet rs = DBConnection.sql.readQuery("SELECT id FROM pk_cooldown_ids WHERE cooldown_name = '" + cooldown + "'")) {
			if (rs.next()) {
				return rs.getInt("id");
			} else {
				DBConnection.sql.modifyQuery("INSERT INTO pk_cooldown_ids (cooldown_name) VALUES ('" + cooldown + "')", async);
				return getCooldownId(cooldown, async);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public String getCooldownName(int id) {
		try (ResultSet rs = DBConnection.sql.readQuery("SELECT cooldown_name FROM pk_cooldown_ids WHERE id = " + id)) {
			if (rs.next()) {
				return rs.getString("cooldown_name");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

}