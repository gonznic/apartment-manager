package user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import unit.Building;
import unit.Unit;
import unit.Building.Floor;

/**
 * A class representing a list of users.
 * To be used with User.java.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class UserList {
	private ArrayList<User> listOfUsers;
	
	/**
	 * Creates a list of users
	 */
	public UserList() {
		listOfUsers = new ArrayList<>();
	}
	
	/**
	 * Adds a user list to this user list.
	 * @param users - Array of users
	 */
	public void addUserList(User[] users) {
		for (User u : users) {
			if (!addUser(u)) System.out.println("Unable to add user");
		}
	}
	
	/**
	 * @param user - User to be added
	 * @return true if user name was unique and the user was added
	 */
	public boolean addUser(User user) {
		// Check unique user name
		for (User u : listOfUsers) {
			if ((u.getUsername()).equals(user.getUsername())) return false;
		}
		listOfUsers.add(user);
		
		//Sort alphabetically by name
		listOfUsers.sort(new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return (o1.getName()).compareToIgnoreCase(o2.getName());
			}
			
		});
		return true;
	}
	
	/**
	 * @param user
	 * @return true if the user was removed
	 */
	public boolean removeUser(User user) {
		if (user instanceof Resident) {
			Unit unit = ((Resident) user).getUnit();
			
			// Move residents out
			if (unit != null) {
				unit.getResident().removeUnit();
				unit.removeResident();
			}
			
			// Remove them from list
			listOfUsers.remove(user);
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (User u : listOfUsers) sb.append(u.toString() + "\n");
		return sb.toString();
	}
	
	/**
	 * Searches for a user based on matching user name and password keys.
	 * @param username
	 * @param password
	 * @return A User with matching user name and password
	 */
	public User find(String username, String password) {
		if (username == null || password == null) return null;
		for (User u : listOfUsers) {
			if ((u.getUsername()).equals(username)) {
				if ((u.getPassword()).equals(password)) {
					return u;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return List of users not currently assigned to a unit.
	 */
	public ArrayList<Resident> getAllUnassigned(){
		ArrayList<Resident> listUnassignedUsers = new ArrayList<Resident>();
		for (User u : listOfUsers) {
			if (u instanceof Resident) {
				if (((Resident)u).getUnit() == null) {
					listUnassignedUsers.add((Resident)u);
				}
			}
		}
		return listUnassignedUsers;
	}
	
	
	/**
	 * @return List of Users who are Residents
	 */
	public ArrayList<Resident> getAllResidents(){
		ArrayList<Resident> allUsers = new ArrayList<Resident>();
		for (User u : listOfUsers) {
			if (u instanceof Resident) {
				allUsers.add((Resident)u);
			}
		}
		return allUsers;
	}
	
	/**
	 * @return List of all complaints made by every user
	 */
	public ArrayList<Complaint> getAllComplaints(){
		ArrayList<Complaint> listComplaints = new ArrayList<Complaint>();
		for (User u : listOfUsers) {
			listComplaints.addAll(u.getComplaints());
		}
		return listComplaints;
	}
	
	/*
	 * Search Methods
	 */
	
	/**
	 * Removes all Residents with an ACTIVE status from the parameter list.
	 * @param query - List of Resident objects
	 */
	public void queryRemoveActive(List<Resident> query){
		for(Resident r : query) {
			if (r.getStatus().equalsIgnoreCase("Active")) {
				query.remove(r);
			}
		}
	}
	
	/**
	 * Removes all Residents with an INACTIVE status from the parameter list.
	 * @param query - List of Resident objects
	 */
	public void queryRemoveInactive(List<Resident> query){
		for(Resident r : query) {
			if (r.getStatus().equalsIgnoreCase("Inactive")) {
				query.remove(r);
			}
		}
	}
	
	/**
	 * Removes all Residents with a NEW status from the parameter list.
	 * @param query - List of Resident objects
	 */
	public void queryRemoveNew(List<Resident> query){
		for(Resident r : query) {
			if (r.getStatus().equalsIgnoreCase("New")) {
				query.remove(r);
			}

		}

	}
	
	/**
	 * Clear every resident who's latest unit was not in the parameter building.
	 * This leaves only residents that were/are in the selected building.
	 * @param query - List of Resident objects
	 * @param building - Building to get residents from
	 */
	public void filterBuilding(List<Resident> query, Building building) {
		for(Resident r : query) {
			if (r.isNewAccount()) query.remove(r);
			else if (r.getLatestUnitHistory().getBuilding() != building) {
				query.remove(r);	
			}
		}
	}
	
	/**
	 * Clear every resident who's latest unit was not on the parameter Floor.
	 * This leaves only residents that were/are in the selected Floor.
	 * @param query - List of Resident objects
	 * @param floor - floor to get residents from
	 */
	public void filterFloor(List<Resident> query, Floor floor) {
		for(Resident r : query) {
			if (r.isNewAccount()) query.remove(r);
			else if (r.getLatestUnitHistory().getFloor() != floor) {
				query.remove(r);	
			}
		}
	}
	
	/**
	 * Searches User account names and user names by substring.
	 * @param query - List of Resident objects
	 * @param substring
	 */
	public void filterNameUsername(List<Resident> query, String substring) {
		for(Resident r : query) {
			// Remove if name does not contain substring
			if (!(
					(r.getName().toLowerCase()).contains(substring.toLowerCase()) || 
					(r.getUsername().toLowerCase()).contains(substring.toLowerCase()))) {
				query.remove(r);
			}
		}
	}
	
	/**
	 * Sorts a list of Residents alphabetically by Resident.name.
	 * @param query
	 */
	public void sortAZ(List<Resident> query) {
		query.sort(new Comparator<Resident>() {

			@Override
			public int compare(Resident o1, Resident o2) {
				return (o1.getName()).compareToIgnoreCase(o2.getName());
			}

		});
	}
}

