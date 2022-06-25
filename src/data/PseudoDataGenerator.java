package data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import accounts.Charge;
import unit.BuildingList;
import unit.Unit;
import user.Complaint;
import user.Resident;
import user.UserList;

/**
 * This class generates a building list and user account list with pseudo-random data to be used
 * for the application.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class PseudoDataGenerator {

	// Lists to Fill
	private BuildingList buildingList = new BuildingList();
	private UserList users = new UserList();
	
	public PseudoDataGenerator() {
		
		/*
		 * Add units
		 */
		
		Unit[] unitsToAdd = {
				new Unit("1A", "4792 Prospect Ave.", 1, 1500),
				new Unit("1B", "4792 Prospect Ave.", 1, 1500),
				new Unit("1C", "4792 Prospect Ave.", 1, 1500),
				new Unit("1D", "4792 Prospect Ave.", 1, 1500),
				new Unit("2A", "4792 Prospect Ave.", 2, 1500),
				new Unit("2B", "4792 Prospect Ave.", 2, 1500),
				new Unit("2C", "4792 Prospect Ave.", 2, 1500),
				new Unit("2D", "4792 Prospect Ave.", 2, 1500),
				new Unit("3A", "4792 Prospect Ave.", 3, 3000),
				new Unit("3B", "4792 Prospect Ave.", 3, 3000),
				new Unit("1A", "1822 Main St.", 1, 1000),
				new Unit("1B", "1822 Main St.", 1, 1000),
				new Unit("1C", "1822 Main St.", 1, 1000),
				new Unit("2A", "1822 Main St.", 2, 1000),
				new Unit("2B", "1822 Main St.", 2, 1000),
				new Unit("2C", "1822 Main St.", 2, 1000),
				new Unit("3A", "1822 Main St.", 3, 1000),
				new Unit("3B", "1822 Main St.", 3, 1000),
				new Unit("3C", "1822 Main St.", 3, 1000),
				new Unit("4A", "1822 Main St.", 4, 1000),
				new Unit("4B", "1822 Main St.", 4, 1000),
				new Unit("4C", "1822 Main St.", 4, 1000),
				new Unit("001", "Grand St.", 1, 3000),
				new Unit("002", "Grand St.", 1, 3000),
				new Unit("003", "Grand St.", 1, 3000),
				new Unit("004", "Grand St.", 1, 3000),
				new Unit("005", "Grand St.", 1, 3000)
				}; //27
		
		for (Unit u : unitsToAdd) {
			buildingList.addUnit(u);
		}
		
		/*
		 *  Create Resident Account Credentials
		 */
		
		// Latest Tenants
		String[] tenantNames = {
				"Travis Espinoza",
				"Eve Chen",
				"Anastazja Underwood",
				"Jacqueline Mathis",
				"Iris Parks",
				"Evie-Rose Oneal",
				"Donell Delgado",
				"Mikayla Barry",
				"Kavita Hall",
				"Julian Nelson",
				"Karis Lewis",
				"Giacomo Ramsay",
				"Ava-Mae Berg",
				"Brandon-Lee Brock",
				"Kiara Rawlings",
				"Hina Sparrow",
				"Ayesha Ashton",
				"Shayan Finch",
				"Hadi Barton",
				"Juliette Barrett",
				"Lorenzo Simmons",
				"Maariyah White",
				"Rafferty Mccullough",
				"Travis Mayo",
				"Mara Wilcox",
				"Mochi Shank",
				"Arjan Beck" //27
				};
		
		String[] tenantUsernames = {
				"carewhizz",
				"TapeMainsheet",
				"shadeHabitual",
				"ArrestKeyboard",
				"gildedIris",
				"evie_rose",
				"Delgado99",
				"QualifiedMik",
				"hallsat",
				"giraffelion",
				"Adspac",
				"AngelRosa",
				"Aurenewa",
				"Blinketon",
				"Bookke",
				"Cables",
				"ChatSablink",
				"Cookyak",
				"Coontso",
				"Doublentek",
				"Fliqzefly",
				"JollyGazette",
				"LaoInca",
				"Mafiasm",
				"ManiakUout",
				"Naivest",
				"Namenthe" //27
				};
		
		// Old tenants
		String[] oldTenantNames = {
				"Anabia Woodward",
				"Emily Rios",
				"Don Ponce",
				"Sumaiya Hahn",
				"Zane Fisher",
				"Ursula Estrada",
				"Chante Garrison",
				"Amelia-Rose Beaumont",
				"Matylda Knights",
				"Kendall Holland",
				"Teddie Magana",
				"Jeffrey Riley",
				"Jay Copeland",
				"Dwight Diaz",
				"Jackie Vega",
				"Desiree Richardson",
				"Krystal Harmon",
				"Horace Pierce",
				"Norman Warner",
				"Rochelle Clarke",
				"Terrance Perez",
				"Alfredo Ballard",
				"Wesley Powers",
				"Jenna James",
				"Andre Weber",
				"Gerardo Holland",
				"Pearl Garcia " //27
				};
		
		String[] oldTenantUsernames = {
				"Nemarget",
				"Nutsan",
				"Omerfide",
				"PierceWonder",
				"Shoppillel",
				"Showst",
				"Stalople",
				"SuperVengeance",
				"Talentuc",
				"Underat",
				"VibrantPark",
				"Airsi",
				"Akinair",
				"Badrist",
				"Bennost",
				"Cheertax",
				"Cozyme",
				"Dolante",
				"EliteSteve",
				"Eskiwi",
				"Ifallmu",
				"Indagalan",
				"Informanos",
				"Informel",
				"Interesee",
				"Logicor",
				"ReeAlli" //27
				};
		
		/*
		 * Create accounts (add only current residents to main user list)
		 */
		
		// Create lists
		Resident[] addedUsers = new Resident[tenantNames.length];
		Resident[] oldTenantPool = new Resident[oldTenantNames.length];
		int residentsToRegister = unitsToAdd.length;

		// Current Tenants
		for (int i = 0; i < residentsToRegister; i++) {
			String tenantName = tenantNames[i];
			String tenantEmail = "resident" + i + "@email.com";
			String tenantUsername = tenantUsernames[i];
			
			Resident newUser = new Resident(tenantName,
					tenantEmail,
					tenantUsername,
					"password",
					1234567890);
			addedUsers[i] = newUser;
			users.addUser(newUser); // Add to list
		}
		
		// Old tenants
		for (int i = 0; i < residentsToRegister; i++) {
			String tenantName = oldTenantNames[i];
			String tenantEmail = "resident" + i + "@email.com";
			String tenantUsername = oldTenantUsernames[i];
			
			Resident newUser = new Resident(tenantName,
					tenantEmail,
					tenantUsername,
					"password",
					1234567890);
			oldTenantPool[i] = newUser;
		}
	
		/*
		 * Create complaints pool
		 */
		
		String[][] complaintOptions= {
				{"Switch light bulb", "Light bulb went out in the kitchen"},
				{"light bulb", "Light bulb went out in the bathroom"},
				{"Light out", "Light bulb went out in the bedroom"},
				{"Door hinge broken", "Door hinge fell off the door"},
				{"No hot water", "Self explanatory"},
				{"Broken toilet seat", "Toilet seat has a crack"},
				{"Door knob getting stuck", "Door knob gets stuck"},
				{"Broken bulb", "Light bulb went out"},
				{"Sink is leaking", "the sink is leaking"},
				};

		/*
		 * Generate leases and complaints
		 */
		
		// Rent calibration
		double baseSqftRent = 3;
		double randomIncrements = 1;
		double rentGrowthFactor = -0.02; // Leases decrease in rent price depending on 
										 // how long ago they started
		// Loop
		for (int i = 0; i < residentsToRegister; i++) {
			
			/*
			 * Current lease
			 */
			
			LocalDate leaseStartDate = 
					LocalDate.now().minus((int)(Math.random() * 400), ChronoUnit.DAYS);
			LocalDate leaseEndDate = 
					(leaseStartDate.plus(1, ChronoUnit.YEARS)).minus(1, ChronoUnit.DAYS);
	
			double rentGrowth = 
					ChronoUnit.MONTHS.between(LocalDate.now().minus(24, ChronoUnit.MONTHS),
							leaseStartDate) * rentGrowthFactor;
			
			int residentRent = ((int)(unitsToAdd[i].getFloorArea() 
					* (baseSqftRent + (baseSqftRent * rentGrowth)) 
					+ ((int)(Math.random()*20) * randomIncrements)));

			/*
			 * Past Lease
			 */
			
			// Random chance of lease renewal (Make some tenants have a lease before their 
			// current one and some have previous tenants in their unit)
			int leaseLengthSwitch = 0;
			leaseLengthSwitch = (int) (Math.random() * 100);
			
			if (leaseLengthSwitch < 50) {
				// New lease one year prior (lease was renewed)
				LocalDate oldLaseEndDate = leaseStartDate.minus(1, ChronoUnit.DAYS);
				LocalDate oldLeaseStartDate = leaseStartDate.minus(1, ChronoUnit.YEARS);

				//Make old lease
				rentGrowth = ChronoUnit.MONTHS.between(LocalDate.now().minus(24, ChronoUnit.MONTHS),
						leaseStartDate) * rentGrowthFactor;
				
				residentRent = ((int)(unitsToAdd[i].getFloorArea() 
						* (baseSqftRent + (baseSqftRent * rentGrowth)) 
						+ ((int)(Math.random()*20) * randomIncrements)));
				
				// Add resident to unit (attaches lease to unit)
				unitsToAdd[i].setResident(addedUsers[i]);
				addedUsers[i].setUnit(unitsToAdd[i], oldLeaseStartDate, oldLaseEndDate, residentRent);
				
				//remove from unit (since no longer a tenant)
				unitsToAdd[i].removeResident();
				addedUsers[i].removeUnit();
			}
			
			/*
			 * Old tenants (if the current resident did not have a lease before their current one)
			 */
			
			else {
				// Add old tenant to the main user list
				users.addUser(oldTenantPool[i]);
				
				// Old lease terms (Up to 90 day gap between current lease start and 
				//old tenant lease end)
				LocalDate oldTenantLeaseStartDate = 
						(leaseStartDate.minus(1, ChronoUnit.YEARS)).minus((int)(Math.random() * 90),
								ChronoUnit.DAYS);
				LocalDate oldTenantLeaseEndDate = 
						(oldTenantLeaseStartDate.plus(1, ChronoUnit.YEARS)).minus(1, ChronoUnit.DAYS);

				//Make old leases
				rentGrowth = ChronoUnit.MONTHS.between(LocalDate.now().minus(24, ChronoUnit.MONTHS), 
						leaseStartDate) * rentGrowthFactor;
				residentRent = ((int)(unitsToAdd[i].getFloorArea() 
						* (baseSqftRent + (baseSqftRent * rentGrowth)) 
						+ ((int)(Math.random()*20) * randomIncrements)));
				
				// Add resident to unit (attaches lease to unit)
				unitsToAdd[i].setResident(oldTenantPool[i]);
				oldTenantPool[i].setUnit(unitsToAdd[i], oldTenantLeaseStartDate, 
						oldTenantLeaseEndDate, residentRent);
				
				// Complaints
				LocalDate oldComplaintDate = 
						oldTenantPool[i].getLatestLeaseHistory().getLeaseStart().plus(
								(int)(Math.random() * 300), ChronoUnit.DAYS);
				if (oldComplaintDate.isBefore(LocalDate.now())) {
					// Set subject from complaint pool, create new complaint
					int complaint = (int)(Math.random() * (complaintOptions.length - 1));
					Complaint oldComplaintToAdd = new Complaint(complaintOptions[complaint][0], 
							oldComplaintDate, complaintOptions[complaint][1]);
					oldComplaintToAdd.setLogDate(oldComplaintDate.plus((int)(Math.random() * 5), 
							ChronoUnit.DAYS));
					oldTenantPool[i].addComplaint(oldComplaintToAdd);
					
					// Close complaint if it already passed
					if (oldComplaintToAdd.getLogDate().isBefore(
							LocalDate.now().minus(30, ChronoUnit.DAYS))) {
						oldComplaintToAdd.setStatus("CLOSED");
					}
				}
				
				// Make payments on old lease charges
				for (Charge c : oldTenantPool[i].getCharges()) {
					
					// If the due date is before yesterday
					if (c.getDueDate().isBefore(LocalDate.now().plus(2, ChronoUnit.DAYS))) {
						c.makePayment(c.getRemainingBalance(), 0, 
								c.getPostingDate().plus(2, ChronoUnit.DAYS));
					}
				}
				
				//Remove from unit
				unitsToAdd[i].removeResident();
				oldTenantPool[i].removeUnit();
			}
			
			// Add resident to unit (attaches lease to unit)
			unitsToAdd[i].setResident(addedUsers[i]);
			addedUsers[i].setUnit(unitsToAdd[i], leaseStartDate, leaseEndDate, residentRent);
			
			/*
			 * Payments
			 */
			
			for (Charge c : addedUsers[i].getCharges()) {
				// If the due date in two days or before, pay the charge
				if (c.getDueDate().isBefore(LocalDate.now().plus(2, ChronoUnit.DAYS))) {
					c.makePayment(c.getRemainingBalance(), 0, c.getPostingDate().plus(
							2, ChronoUnit.DAYS));
				}
			}
			
			/*
			 * Complaints
			 */
			
			// One year before plus random
			LocalDate complaintDate = addedUsers[i].getLatestLeaseHistory().getLeaseStart().plus(
					(int)(Math.random() * 300), ChronoUnit.DAYS);
			
			// If complaint is before today
			if (complaintDate.isBefore(LocalDate.now())) {
				
				// Set subject from complaint pool, create new complaint
				int complaint = (int)(Math.random() * (complaintOptions.length - 1));
				Complaint complaintToAdd = new Complaint(complaintOptions[complaint][0], 
						complaintDate, complaintOptions[complaint][1]);
				complaintToAdd.setLogDate(complaintDate.plus((int)(Math.random() * 5), ChronoUnit.DAYS));
				addedUsers[i].addComplaint(complaintToAdd);
				
				// Close complaint if it already passed
				if (complaintToAdd.getLogDate().isBefore(LocalDate.now().minus(30, ChronoUnit.DAYS))) {
					complaintToAdd.setStatus("CLOSED");
				}
			}
		}
		
		/*
		 * Move out residents with expired leases
		 */
		
		ArrayList<Resident> leaseEndedResidents = new ArrayList<Resident>();
		
		// Find residents with expired leases
		for (int i = 0; i < residentsToRegister; i++) {
			if (addedUsers[i].isLeaseOver()) {
				leaseEndedResidents.add(addedUsers[i]);
			}
		}
		
		// Move out residents with expired leases except the last one (for display purposes)
		for (int i = 0; i < leaseEndedResidents.size(); i++) {
			if (i != leaseEndedResidents.size() - 1) {
				leaseEndedResidents.get(i).getUnit().removeResident();
				leaseEndedResidents.get(i).removeUnit();
			}
		}	
	}
	
	/**
	 * Used to access the generated building list.
	 * @return A building list generated by this class
	 */
	public BuildingList getBuildingList() {
		return buildingList;
	}

	/**
	 * Used to access the generated user list.
	 * @return A user list generated by this class
	 */
	public UserList getUsers() {
		return users;
	}
}

