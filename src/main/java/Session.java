package main.java;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class for the current log-in session.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Session {
	
	private int userId;
	private String user;
	private String name;
	
	private boolean isAdmin;
	private String adminPass;
	
	private String dbUrl;
	
	/**
	 * Creates a new session with the database query result ResultSet
	 * @param login - ResultSet
	 * @param dbUrl
	 */
	public Session(ResultSet login, String dbUrl) {

		try {
			// User
			this.userId = Integer.parseInt(login.getString("uid"));
			this.user = login.getString("user");
			this.dbUrl = dbUrl;
			
			// Admin
			this.isAdmin = false;
			if (login.getString("type").equals("admin")) {
				this.isAdmin = true;
				this.adminPass = login.getString("pass"); // Needed for permissions
			}
			
			// Name
			String firstName = login.getString("first_name");
			String lastName = login.getString("last_name");
			
			if (firstName == null && lastName == null) this.name = "";
			else if (firstName == null) this.name = lastName;
			else if (lastName == null) this.name = firstName;
			else this.name = firstName + " " + lastName;
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @return the adminPass (Needed for database permission)
	 */
	public String getAdminPass() {
		return adminPass;
	}

	/**
	 * @return the dbUrl
	 */
	public String getDbUrl() {
		return dbUrl;
	}
	
}
