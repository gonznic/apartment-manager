import data.PseudoDataGenerator;
import gui.Dashboard;
import gui.LoginWindow;
import unit.BuildingList;
import user.Admin;
import user.UserList;

/**
 * Launches Apartment Manager. This class contains a main method that generates user and building
 * data using data.PseudoDataGenerator and then either creates a log-in window or launches an
 * account dash board using the settings in the "Log In" section.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Launch {
	
	public static void main(String[] args) {
		
		/*
		 * Data (empty)
		 */
		BuildingList buildingList; // = new BuildingList();
		UserList users; // = new UserList();
		
		
		/*
		 * Generate Data (Demo Purposes)
		 */
		
		PseudoDataGenerator generatedData = new PseudoDataGenerator();
		buildingList = generatedData.getBuildingList();
		users = generatedData.getUsers();

		
		/*
		 * Administrator Account
		 */
		
		Admin adminAccount = new Admin("Admin Account",
				"admin@email.com",
				"Operator",
				"password",
				1234567890);
		users.addUser(adminAccount);
		

		/*
		 * Log In
		 */
		
		boolean skipLogIn = false;
		boolean isAdmin = true; // Only matters if skipLogIn = true
		
		if (!skipLogIn) {
			new LoginWindow(users, buildingList);
		}
		
		else {
			if (isAdmin) {
				new Dashboard(adminAccount, users, buildingList); // Administrator
			}
			else {
				new Dashboard(users.getAllResidents().get(0), null, null); // Resident
			}
		}
	}

}

