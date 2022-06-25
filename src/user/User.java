package user;

import java.util.ArrayList;

/**
 * A class that represents a user. Contains user credentials and account data.
 * 
 * @author Nicolas Gonzalez
 *
 */
public abstract class User {
	
	private String name, email, type, username, password;
	private int phone;
	private ArrayList<Complaint> complaints = new ArrayList<Complaint>();
	private boolean darkTheme = true;
	
	/**
	 * @return Name of the user
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name - Name of the user
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return Email of the user
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * @param email - Email of the user
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * @return User's type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type of user. (Only used by Admin.java constructor)
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return User's user name
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @param username - User name to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return User's password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @param password - New password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return phone number
	 */
	public int getPhone() {
		return phone;
	}
	
	/**
	 * Returns a string representation of the user's phone number.
	 * @return Phone number string in the format (000) 000-0000
	 */
	public String getPhoneString() {
		// Convert number to string
		String rawPhone = Integer.toString(this.phone);
		// TODO Handle different number lengths
		
		StringBuilder sb = new StringBuilder();
		if (rawPhone.length() == 10) {
			sb.append("(");
			sb.append(rawPhone.substring(0,3));
			sb.append(") ");
			sb.append(rawPhone.substring(3,6));
			sb.append("-");
			sb.append(rawPhone.substring(6,10));
		}
		else sb.append(rawPhone);
		return sb.toString();
	}
	
	/**
	 * Sets the user's phone number
	 * @param phone number
	 */
	public void setPhone(int phone) {
		this.phone = phone;
	}
	
	/**
	 * Returns a list of this user's complaints.
	 * @return List of complaints
	 */
	public ArrayList<Complaint> getComplaints() {
		return complaints;
	}
	
	/**
	 * Returns the number of open complaints in the user's list of complaints.
	 * @return count of open complaints
	 */
	public int getOpenComplaintsNumber() {
		int openComplaints = 0;
		for (Complaint c : complaints) {
			if (c.isOpen()) openComplaints++;
		}
		return openComplaints;
	}
	
	/**
	 * Adds a complaint to the user's list of complaints.
	 * @param complaint
	 */
	public void addComplaint(Complaint complaint) {
		complaint.setUser(this);
		this.complaints.add(complaint);
	}
	
	/**
	 * Checks if the user's theme is dark mode.
	 * @return true if dark theme is active
	 */
	public boolean isDarkTheme() {
		return darkTheme;
	}
	
	/**
	 * Sets user's theme to dark.
	 * @param true to set the theme to dark
	 */
	public void setDarkTheme(boolean darkTheme) {
		this.darkTheme = darkTheme;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[" + name + ", " + email + ", " + type + ", " + username + "]");
		return sb.toString();
	}	
}

