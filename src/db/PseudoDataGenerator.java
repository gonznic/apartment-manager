package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Connects to a SQL database and performs a series of operations to fill the tables:
 * units, users, leases, charges, complaints, and payments.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class PseudoDataGenerator {
	public static void main(String[] args) {
		new PseudoDataGenerator();
	}
	
	// Database
	private static String dbUrl = "jdbc:mysql://localhost:3306/apartment_manager"; // TODO Change to your database url
	private static String dbUsername = "apartment-manager-generator"; // Database access account with all privileges
	private static String dbPassword = "data-generator"; // Database access account with all privileges
	
	// Rent calibration
	private double baseSqftRent = 3;
	private double randomIncrements = 5; // Factor of pseudo-random rent amount
	private double rentGrowthFactor = 0.005; // The older the lease, the lower the rent
	
	/**
	 * Fills the database by calling the methods: clearDatabase(), insertCurrentUsers(), 
	 * insertPastUsers(), insertComplaints(), and insertPayments().
	 */
	public PseudoDataGenerator() {
		
		System.out.print("""
				=========================================================
				                    APARTMENT MANAGER
				---------------------------------------------------------
				               PSEUDORANDOM DATA GENERATOR
				=========================================================
				""");
		String consoleSpace = "=========================================================\n";
		
		/*
		 * Database Operations
		 */
		
		clearDatabase(); // Preserves administrator accounts
		
		System.out.print(consoleSpace);
		
		insertUnits();
		
		System.out.print(consoleSpace);
		
		insertCurrentUsers();
		
		System.out.print(consoleSpace);
		
		insertPastUsers();
		
		System.out.print(consoleSpace);
		
		insertComplaints();
		
		System.out.print(consoleSpace);
	
		insertPayments();
		
		System.out.print("""
				=========================================================
				                           END
				=========================================================
				""");
	}
	
	/**
	 * Connects to database and inserts a list of units into `units` table.
	 */
	private void insertUnits() {
		// Initialize
		System.out.println("[Units] Initializing...");
		
		Object[][] unitsToAdd = {
				{"1A", "4792 Prospect Ave.", 1, 1500},
				{"1B", "4792 Prospect Ave.", 1, 1500},
				{"1C", "4792 Prospect Ave.", 1, 1500},
				{"1D", "4792 Prospect Ave.", 1, 1500},
				{"2A", "4792 Prospect Ave.", 2, 1500},
				{"2B", "4792 Prospect Ave.", 2, 1500},
				{"2C", "4792 Prospect Ave.", 2, 1500},
				{"2D", "4792 Prospect Ave.", 2, 1500},
				{"3A", "4792 Prospect Ave.", 3, 3000},
				{"3B", "4792 Prospect Ave.", 3, 3000},
				{"1A", "1822 Main St.", 1, 1000},
				{"1B", "1822 Main St.", 1, 1000},
				{"1C", "1822 Main St.", 1, 1000},
				{"2A", "1822 Main St.", 2, 1000},
				{"2B", "1822 Main St.", 2, 1000},
				{"2C", "1822 Main St.", 2, 1000},
				{"3A", "1822 Main St.", 3, 1000},
				{"3B", "1822 Main St.", 3, 1000},
				{"3C", "1822 Main St.", 3, 1000},
				{"4A", "1822 Main St.", 4, 1000},
				{"4B", "1822 Main St.", 4, 1000},
				{"4C", "1822 Main St.", 4, 1000},
				{"001", "Grand St.", 1, 3000},
				{"002", "Grand St.", 1, 3000},
				{"003", "Grand St.", 1, 3000},
				{"004", "Grand St.", 1, 3000},
				{"005", "Grand St.", 1, 3000}
				}; //27
		
		System.out.println("[Units] --> Units initialized");
		
		// Connect to database
		System.out.println("[Database] Connecting...");
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			System.out.println("[Database] --> Connected to database");
			Statement statement = connect.createStatement();

			// Prepare query
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("INSERT INTO units(`unit`,`building`,`floor`,`sqft`,`created`) VALUES ");
			for (Object[] unit : unitsToAdd) {
				sqlQuery.append("(");
				sqlQuery.append("'" + unit[0] + "',");
				sqlQuery.append("'" + unit[1] + "',");
				sqlQuery.append("'" + unit[2] + "',");
				sqlQuery.append("'" + unit[3] + "', '2000-01-01'");
				sqlQuery.append("),");
			}
			sqlQuery.deleteCharAt(sqlQuery.toString().length() - 1);
			
			// Query
			System.out.println("[Units] Inserting units...");
			try {
				statement.executeUpdate(sqlQuery.toString());
				System.out.println("[Units] --> SUCCESS: INSERT INTO `units`");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Close connection
			System.out.println("[Database] Disconnecting from database...");
			connect.close();
			System.out.println("[Database] --> Disconnected");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects to database and inserts a list of users with current leases into `users` table.
	 * Leases and rent charges are generated and inserted into `leases` and `charges` table.
	 */
	private void insertCurrentUsers() {
		// Initialize
		System.out.println("[Current Users] Initializing...");
		
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
				"Mochi Shanx",
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
		
		System.out.println("[Current Users] --> Accounts initialized");

		// Connect to database
		System.out.println("[Database] Connecting...");
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			System.out.println("[Database] --> Connected to database");
			Statement statement = connect.createStatement();
			
			/*
			 * Insert current residents
			 */
			
			System.out.println("[Current Users] Inserting current residents...");
			
			// Prepare query
			StringBuilder sqlInsertCurrentResidents = new StringBuilder();
			sqlInsertCurrentResidents.append(
					"INSERT INTO users (`user`,`pass`,`email`,`phone`,`first_name`,`last_name`)"
					+ "VALUES");
			
			// Loop through current residents
			for (int i = 0; i < tenantUsernames.length; i++) {
				String tenantFirst = tenantNames[i].split(" ")[0];
				String tenantLast = tenantNames[i].split(" ")[1];
				String tenantUsername = tenantUsernames[i].toLowerCase();
				sqlInsertCurrentResidents.append("('"+ tenantUsername + "','password','"
						+ tenantUsername.toLowerCase() + "@email.com', '123456789', '" 
						+ tenantFirst + "','" + tenantLast + "'),");
			}
			sqlInsertCurrentResidents.deleteCharAt(sqlInsertCurrentResidents.toString().length() - 1);
			
			// Query
			try {
				statement.executeUpdate(sqlInsertCurrentResidents.toString());
				System.out.println("[Current Users] --> SUCCESS: INSERT INTO `user`");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			/*
			 * Insert current leases
			 */
			
			System.out.println("[Current Users] Generating leases...");
			
			// Select units
			String sqlSelectUnits = "SELECT id, sqft FROM units";
			ArrayList<Integer[]> currentLeaseUnits = new ArrayList<Integer[]>();

			// Select residents
			String sqlSelectCurrentResidents = "SELECT uid FROM users WHERE type = 'general'";
			ArrayList<Integer> currentLeaseUsers = new ArrayList<Integer>();
			
			// Query
			try {
				ResultSet unitQuery = statement.executeQuery(sqlSelectUnits);
				System.out.println("[Current Users] --> SUCCESS: SELECT FROM `units'");
				
				// Close statement ResultSet for next query
				while (unitQuery.next()) {
					int unitId = unitQuery.getInt("id");
					int sqft = unitQuery.getInt("sqft");
					Integer[] unit= {unitId, sqft};
					currentLeaseUnits.add(unit);
				}

				ResultSet currentResidentsQuery = statement.executeQuery(sqlSelectCurrentResidents);
				System.out.println("[Current Users] --> SUCCESS: SELECT FROM `users'");
				while (currentResidentsQuery.next()) currentLeaseUsers.add(currentResidentsQuery.getInt(1));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Generate leases
			System.out.println("[Current Users] --> Generating lease terms... ");
			
			StringBuilder sqlInsertCurrentLeases = new StringBuilder(""
					+ "INSERT INTO leases(`start`,`end`,`rent`,`unitid`,`userid`) "
					+ "VALUES");
		
			StringBuilder sqlInsertCurrentPastLeases = new StringBuilder(""
					+ "INSERT INTO leases(`start`,`end`,`rent`,`unitid`,`userid`) "
					+ "VALUES");
			
			StringBuilder sqlInsertMonthlyCharges = new StringBuilder(
					"INSERT INTO charges(`name`,`date`,`amount`,`userid`) VALUES ");
			
			int currentPastLeasecount = 0;
			
			// Loop
			Iterator<Integer> currentLeaseUsersIterator = currentLeaseUsers.iterator();
			for (Integer[] unit : currentLeaseUnits) {
				if (currentLeaseUsersIterator.hasNext()) {
					int uid = currentLeaseUsersIterator.next();
					int unitId = unit[0];
					int sqft = unit[1];
					
					/*
					 * Current leases
					 */
					
					// Generate dates
					LocalDate leaseStartDate = 
							LocalDate.now().minus((int)(Math.random() * 400), ChronoUnit.DAYS);
					
					LocalDate leaseEndDate = 
							(leaseStartDate.plus(1, ChronoUnit.YEARS)).minus(1, ChronoUnit.DAYS);
					
					// Generate rent
					double rentGrowth = 
							ChronoUnit.MONTHS.between(LocalDate.now().minus(24, ChronoUnit.MONTHS),
									leaseStartDate) * rentGrowthFactor;
					int residentRent = ((int)(sqft * (baseSqftRent + (baseSqftRent * rentGrowth)) 
							+ ((int)(Math.random()*20) * randomIncrements)));
					
					// Insert into current lease query
					sqlInsertCurrentLeases.append("(");
					sqlInsertCurrentLeases.append("'" + leaseStartDate + "',");
					sqlInsertCurrentLeases.append("'" + leaseEndDate + "',");
					sqlInsertCurrentLeases.append("" + residentRent + ",");
					sqlInsertCurrentLeases.append("" + unitId + ",");
					sqlInsertCurrentLeases.append("" + uid + "");
					sqlInsertCurrentLeases.append("),");
					
					// Create and insert monthly charges based on the lease
					sqlInsertMonthlyCharges.append(
							sqlInsertRentCharges(leaseStartDate, leaseEndDate, residentRent, uid));
					
					/*
					 * Current resident past leases
					 */
					
					// Random change of having a past lease
					int leaseLengthSwitch = (int) (Math.random() * 100); 
					if (leaseLengthSwitch < 50) {
						currentPastLeasecount++;
						
						// New lease one year prior (lease was renewed)
						LocalDate oldLeaseEndDate = leaseStartDate.minus(1, ChronoUnit.DAYS);
						LocalDate oldLeaseStartDate = leaseStartDate.minus(1, ChronoUnit.YEARS);
		
						// Old lease rent
						rentGrowth = ChronoUnit.MONTHS.between(LocalDate.now().minus(24, ChronoUnit.MONTHS),
								leaseStartDate) * rentGrowthFactor;
						
						residentRent = ((int)(sqft * (baseSqftRent + (baseSqftRent * rentGrowth)) 
								+ ((int)(Math.random()*20) * randomIncrements)));
						
						// Insert into current past lease statement
						sqlInsertCurrentPastLeases.append("(");
						sqlInsertCurrentPastLeases.append("'" + oldLeaseStartDate + "',");
						sqlInsertCurrentPastLeases.append("'" + oldLeaseEndDate + "',");
						sqlInsertCurrentPastLeases.append("" + residentRent + ",");
						sqlInsertCurrentPastLeases.append("" + unitId + ",");
						sqlInsertCurrentPastLeases.append("" + uid + "");
						sqlInsertCurrentPastLeases.append("),");
						
						// Create and insert monthly charges based on the lease
						sqlInsertMonthlyCharges.append(
								sqlInsertRentCharges(oldLeaseStartDate, oldLeaseEndDate, residentRent, uid));
					}

				}	
			}
			
			// Clean queries
			sqlInsertCurrentLeases.deleteCharAt(sqlInsertCurrentLeases.toString().length() - 1);
			System.out.println("[Current Users] ----> Current leases generated");
			
			sqlInsertCurrentPastLeases.deleteCharAt(sqlInsertCurrentPastLeases.toString().length() - 1);
			System.out.println("[Current Users] ----> Past leases generated (" + currentPastLeasecount + ")");
			
			sqlInsertMonthlyCharges.deleteCharAt(sqlInsertMonthlyCharges.toString().length() - 1);
			System.out.println("[Current Users] ----> Lease charges generated (" + currentPastLeasecount + ")");

			// Query
			System.out.println("[Current Users] Inserting leases...");	
			try {
				statement.executeUpdate(sqlInsertCurrentLeases.toString());
				statement.executeUpdate(sqlInsertCurrentPastLeases.toString());
				System.out.println("[Current Users] --> SUCCESS: INSERT INTO `leases'");
				
				statement.executeUpdate(sqlInsertMonthlyCharges.toString());
				System.out.println("[Current Users] --> SUCCESS: INSERT INTO `charges'");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// Close connection
			System.out.println("[Database] Disconnecting from database...");
			connect.close();
			System.out.println("[Database] --> Disconnected");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Connects to database and inserts a list of users with past leases into `users` table.
	 * Leases and rent charges are generated and inserted into `leases` and `charges` table.
	 * Leases will take place before the current resident leases.
	 */
	private void insertPastUsers() {
		// Initialize
		System.out.println("[Past Users] Initializing...");
		
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
		
		System.out.println("[Past Users] --> Accounts initialized");
		
		// Connect to database
		System.out.println("[Database] Connecting...");
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			System.out.println("[Database] --> Connected to database");
			Statement statement = connect.createStatement();
		
			/*
			 * Insert past residents
			 */
			
			System.out.println("[Past Users] Inserting past residents...");
			
			// Prepare query
			StringBuilder sqlInsertCurrentResidents = new StringBuilder();
			sqlInsertCurrentResidents.append(
					"INSERT INTO users (`user`,`pass`,`email`,`phone`,`first_name`,`last_name`)"
					+ "VALUES");
			
			// Loop through old residents
			for (int i = 0; i < oldTenantUsernames.length; i++) {
				String tenantFirst = oldTenantNames[i].split(" ")[0];
				String tenantLast = oldTenantNames[i].split(" ")[1];
				String tenantUsername = oldTenantUsernames[i].toLowerCase();
				sqlInsertCurrentResidents.append("('"+ tenantUsername + "','password','"
						+ tenantUsername.toLowerCase() + "@email.com', '123456789', '" 
						+ tenantFirst + "','" + tenantLast + "'),");
			}
			sqlInsertCurrentResidents.deleteCharAt(sqlInsertCurrentResidents.toString().length() - 1);
			
			// Query
			try {
				statement.executeUpdate(sqlInsertCurrentResidents.toString());
				System.out.println("[Past Users] --> SUCCESS: INSERT INTO `user`");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
			/*
			 * Insert past leases
			 */
			
			System.out.println("[Past Users] Generating leases...");
			
			// Select residents with no leases
			String sqlSelectNoLeaseResidents = """
					SELECT `uid` 
					FROM users
					WHERE `uid`
					NOT IN (SELECT `userid` FROM leases)
					AND `type` = 'general'
					""";
			ArrayList<Integer> noLeaseUsers = new ArrayList<Integer>();
		
			// Select lease of units with only one lease
			String sqlOldResidentSuccessorLease = """
					SELECT `start`, `unitid`, `sqft` 
					FROM leases 
					JOIN units 
					ON leases.`unitid` = units.`id`
					GROUP BY `userid` HAVING COUNT(*) = 1
					""";
			ArrayList<Object[]> uniqueUnitLeases = new ArrayList<Object[]>();
			
			// Query
			try {
				ResultSet pastResidentsQuery = statement.executeQuery(sqlSelectNoLeaseResidents);
				System.out.println("[Past Users] --> SUCCESS: SELECT FROM `users'");
				// Close statement ResultSet for next query
				while (pastResidentsQuery.next()) noLeaseUsers.add(pastResidentsQuery.getInt(1));
				
				ResultSet unitQuery = statement.executeQuery(sqlOldResidentSuccessorLease);
				System.out.println("[Past Users] --> SUCCESS: SELECT FROM `leases'");
				
				// Close statement ResultSet for next query
				while (unitQuery.next()) {
					LocalDate start = unitQuery.getDate("start").toLocalDate();
					int unitId = unitQuery.getInt("unitid");
					int sqft = unitQuery.getInt("sqft");
					Object[] lease= {start, unitId, sqft};
					uniqueUnitLeases.add(lease);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Generate Leases
			System.out.println("[Past Users] --> Generating lease terms... ");
			
			StringBuilder sqlInsertPastLeases = new StringBuilder(""
					+ "INSERT INTO leases(`start`,`end`,`rent`,`unitid`,`userid`) "
					+ "VALUES");
			
			StringBuilder sqlInsertMonthlyCharges = new StringBuilder(
					"INSERT INTO charges(`name`,`date`,`amount`,`userid`) VALUES ");
			
			int oldResidentCount = 0;
			
			// Loop
			Iterator<Integer> noLeaseUsersIterator = noLeaseUsers.iterator();
			for (Object[] lease : uniqueUnitLeases) {
				if (noLeaseUsersIterator.hasNext()) {
					oldResidentCount++;
					
					int uid = noLeaseUsersIterator.next();
					int unitId = (int) lease[1];
					int sqft = (int) lease[2];
					LocalDate successorStart = (LocalDate) lease[0];
					
					/*
					 * Current leases
					 */
					
					// Generate dates
					LocalDate leaseStartDate = 
							(successorStart.minus(1, ChronoUnit.YEARS)).minus((int)(Math.random() * 90),
									ChronoUnit.DAYS);
					
					LocalDate leaseEndDate = 
							(leaseStartDate.plus(1, ChronoUnit.YEARS)).minus(1, ChronoUnit.DAYS);
					
					// Generate rent
					double rentGrowth = 
							ChronoUnit.MONTHS.between(LocalDate.now().minus(24, ChronoUnit.MONTHS),
									leaseStartDate) * rentGrowthFactor;
					int residentRent = ((int)(sqft * (baseSqftRent + (baseSqftRent * rentGrowth)) 
							+ ((int)(Math.random()*20) * randomIncrements)));
					
					// Insert into current lease statement
					sqlInsertPastLeases.append("(");
					sqlInsertPastLeases.append("'" + leaseStartDate + "',");
					sqlInsertPastLeases.append("'" + leaseEndDate + "',");
					sqlInsertPastLeases.append("" + residentRent + ",");
					sqlInsertPastLeases.append("" + unitId + ",");
					sqlInsertPastLeases.append("" + uid + "");
					sqlInsertPastLeases.append("),");
					
					// Create and insert monthly charges based on the lease
					sqlInsertMonthlyCharges.append(
							sqlInsertRentCharges(leaseStartDate, leaseEndDate, residentRent, uid));
				}
			}
			
			// Clean queries
			sqlInsertPastLeases.deleteCharAt(sqlInsertPastLeases.toString().length() - 1);
			System.out.println("[Past Users] ----> Leases generated (" + oldResidentCount + ")");
			
			sqlInsertMonthlyCharges.deleteCharAt(sqlInsertMonthlyCharges.toString().length() - 1);
			System.out.println("[Past Users] ----> Lease charges generated");
			
			// Query
			System.out.println("[Past Users] Inserting leases...");
			try {
				statement.executeUpdate(sqlInsertPastLeases.toString());
				System.out.println("[Past Users] --> SUCCESS: INSERT INTO `leases'");
				
				statement.executeUpdate(sqlInsertMonthlyCharges.toString());
				System.out.println("[Past Users] --> SUCCESS: INSERT INTO `charges'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
			// Remove users with no leases
			System.out.println("[Past Users] Removing unused users...");
			
			// Get users with no existing leases
			String removeUnusedUsers = """
					DELETE FROM users
					WHERE `uid`
					NOT IN (SELECT `userid` FROM leases)
					AND `type` = 'general'
					""";
			
			// Query
			try {
				statement.executeUpdate(removeUnusedUsers.toString());
				System.out.println("[Past Users] --> SUCCESS: DELETE FROM `users'");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Close connection
			System.out.println("[Database] Disconnecting from database...");
			connect.close();
			System.out.println("[Database] --> Disconnected");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Connects to database and inserts a two complaints per lease into `complaints` table.
	 */
	private void insertComplaints() {
		// Initialize
		System.out.println("[Complaints] Initializing...");
		
		String[][] complaintPool= {
				{"Switch light bulb", "Light bulb went out in the kitchen"},
				{"light bulb", "Light bulb went out in the bathroom"},
				{"Light out", "Light bulb went out in the bedroom"},
				{"Door hinge broken", "Door hinge fell off the door"},
				{"No hot water", "Self explanatory"},
				{"Broken toilet seat", "Toilet seat has a crack"},
				{"Door knob getting stuck", "Door knob gets stuck"},
				{"Broken bulb", "Light bulb went out"},
				{"Sink is leaking", "The sink is leaking"},
				{"Bugs in kitchen", "There are roaches in the kitchen"},
				{"Floor tile broken", "One of the bathroom tiles is broken and sharp"},
				{"Broken window", "The living room window was broken by a golf ball"},
				{"Hole in the wall", "I fell and made a hole in the wall"},
				{"Refrigerator leaking", "The fridge is leaking water"},
				{"Ceiling leak", "Water is leaking from the ceiling in the living room"},
				{"There is no hot water", "Only freezing water is coming out of the tap"},
				{"Repaint", "The walls of the unit need repainting"},
				{"Dirty walls", "Repaint the walls please"},
				{"Moldy carpet", "The master bedroom carpet is growing mold"},
				{"Washer making loud noises", "The washer has started making loud banging noises"}
				};
		
		System.out.println("[Complaints] --> Complaints initialized");

		// Connect to database
		System.out.println("[Database] Connecting...");
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			System.out.println("[Database] --> Connected to database");
			Statement statement = connect.createStatement();
		
			/*
			 * Insert complaints
			 */
			
			System.out.println("[Complaints] Inserting complaints...");
			
			// Get Leases
			String sqlComplaintLeases = "SELECT `start`, `userid`, `unitid` FROM leases";
			ArrayList<Object[]> complaintLeases = new ArrayList<Object[]>();
			
			// Query
			try {
				ResultSet selectComplaintLeases = statement.executeQuery(sqlComplaintLeases);
				System.out.println("[Complaints] --> SUCCESS: SELECT FROM `lease'");
				
				// Close statement ResultSet for next query
				while (selectComplaintLeases.next()) {
					LocalDate leaseStart = selectComplaintLeases.getDate("start").toLocalDate();
					int userId = selectComplaintLeases.getInt("userid");
					int unitId = selectComplaintLeases.getInt("unitid");
					Object[] lease= {leaseStart, userId, unitId};
					complaintLeases.add(lease);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Generate complaints
			StringBuilder sqlInsertComplaints = new StringBuilder(""
					+ "INSERT INTO complaints(`log_date`,`event_date`,`subject`,`description`,"
					+ "`status`,`userid`,`unitid`) VALUES");
			
			System.out.println("[Complaints] --> Generating complaints");
			
			int insertComplaintCount = 0;
			for (Object[] lease : complaintLeases) {

				/*
				 * First complaint
				 */
				
				LocalDate complaintDate = ((LocalDate) lease[0]).plus(
						(int)(Math.random() * 300), ChronoUnit.DAYS);
				
				LocalDate complaintLogDate = complaintDate.plus(
						(int)(Math.random() * 3), ChronoUnit.DAYS);
				
				String complaintStatus = "open";
				if (complaintLogDate.isBefore(LocalDate.now().minus(7, ChronoUnit.DAYS))) {
					complaintStatus = "closed";
				}
				
				// If complaint is before today
				if (complaintLogDate.isBefore(LocalDate.now())) {
					
					// Set subject from complaint pool, create new complaint
					int complaint = (int)(Math.random() * (complaintPool.length - 1));
					
					// Insert into complaint statement
					sqlInsertComplaints.append("(");
					sqlInsertComplaints.append("'" + complaintLogDate + "',");
					sqlInsertComplaints.append("'" + complaintDate + "',");
					sqlInsertComplaints.append("'" + complaintPool[complaint][0] + "',");
					sqlInsertComplaints.append("'" + complaintPool[complaint][1] + "',");
					sqlInsertComplaints.append("'" + complaintStatus + "',");
					sqlInsertComplaints.append("" + lease[1] + ",");
					sqlInsertComplaints.append("" + lease[2] + "");
					sqlInsertComplaints.append("),");
					insertComplaintCount++;
				}
				
				/*
				 * Second complaint
				 */
				
				LocalDate complaintDate2 = ((LocalDate) lease[0]).plus(
						(int)(Math.random() * 300), ChronoUnit.DAYS);
				
				LocalDate complaintLogDate2 = complaintDate2.plus(
						(int)(Math.random() * 3), ChronoUnit.DAYS);
				
				String complaintStatus2 = "open";
				if (complaintLogDate2.isBefore(LocalDate.now().minus(7, ChronoUnit.DAYS))) {
					complaintStatus2 = "closed";
				}
				
				// If complaint is before today
				if (complaintLogDate2.isBefore(LocalDate.now())) {
					
					// Set subject from complaint pool, create new complaint
					int complaint = (int)(Math.random() * (complaintPool.length - 1));
					
					// Insert into complaint statement
					sqlInsertComplaints.append("(");
					sqlInsertComplaints.append("'" + complaintLogDate2 + "',");
					sqlInsertComplaints.append("'" + complaintDate2 + "',");
					sqlInsertComplaints.append("'" + complaintPool[complaint][0] + "',");
					sqlInsertComplaints.append("'" + complaintPool[complaint][1] + "',");
					sqlInsertComplaints.append("'" + complaintStatus2 + "',");
					sqlInsertComplaints.append("" + lease[1] + ",");
					sqlInsertComplaints.append("" + lease[2] + "");
					sqlInsertComplaints.append("),");
					insertComplaintCount++;
				}
			}
			
			// Clean query
			sqlInsertComplaints.deleteCharAt(sqlInsertComplaints.toString().length() - 1);
			System.out.println("[Complaints] ----> Complaints generated (" + insertComplaintCount + ")");
			
			// Query
			try {
				statement.executeUpdate(sqlInsertComplaints.toString());
				System.out.println("[Complaints] --> SUCCESS: INSERT INTO `complaints`");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// Close connection
			System.out.println("[Database] Disconnecting from database...");
			connect.close();
			System.out.println("[Database] --> Disconnected");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects to database and inserts a payment for every rent charge
	 */
	private void insertPayments() {
		
		// Connect to database
		System.out.println("[Database] Connecting...");
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			System.out.println("[Database] --> Connected to database");
			Statement statement = connect.createStatement();
		
			System.out.println("[Payments] Generating payments...");
			
			// Select users with charges
			String sqlSelectUsersWithCharges = ""
					+ "SELECT `date`,`amount`,`userid` FROM charges ORDER BY `userid`,`date` DESC";
			
			// Insert payments
			StringBuilder sqlInsertPayments = new StringBuilder(
					"INSERT payments(`name`,`date`,`amount`,`account`,`userid`) VALUES ");
			
			// Query
			try {
				ResultSet chargesQuery = statement.executeQuery(sqlSelectUsersWithCharges);
				System.out.println("[Payments] --> SUCCESS: SELECT FROM `charges'");
				
				// Add payments for each queried charge
				Object[] previousCharge = null;
				double tmpPayment = 0;
				boolean loopCharges = true;
				
				chargesQuery.next();
				while (loopCharges) {
					
					// Current charge
					LocalDate date = chargesQuery.getDate("date").toLocalDate();
					double amount = chargesQuery.getDouble("amount");
					int userId = chargesQuery.getInt("userid");
					
					// If last charge
					if (!chargesQuery.next()) loopCharges = false;
					
					// if no preceding charge
					if (previousCharge == null) {
						tmpPayment += amount;
						Object[] charge= {date, amount, userId};
						previousCharge = charge;
						continue;
					}
					
					// If same user and same month of same year as previous charge
					if (userId == (int)previousCharge[2] 
							&& date.getYear() == ((LocalDate)previousCharge[0]).getYear() 
							&& date.getMonthValue() == ((LocalDate)previousCharge[0]).getMonthValue()
							&& loopCharges) {
						
						// Add to payment amount
						tmpPayment += amount;
					}
					
					// If different user or month
					else {
						if (!loopCharges) tmpPayment += amount;
						
						String paymentAccount = "0000";
						String paymentName = "Debit Card On-Line Payment";
						LocalDate paymentDate = ((LocalDate)previousCharge[0]).plus(
								(int)(Math.random() * 5), ChronoUnit.DAYS);
						
						// If the payment date is today or before insert into payments query
						if (!paymentDate.isAfter(LocalDate.now())) {
							boolean makePayment = true;
							
							// if payment in last 30 days
							if (paymentDate.isAfter(LocalDate.now().minus(30, ChronoUnit.DAYS))) {
								
								// 1/30 chance of no payment
								if ((int)(Math.random() * 30) == 1) makePayment = false;
							}
							
							if (makePayment) {
								sqlInsertPayments.append("(");
								sqlInsertPayments.append("'" + paymentName + "',");
								sqlInsertPayments.append("'" + paymentDate + "',");
								sqlInsertPayments.append("" + (new DecimalFormat("0.00")).format(tmpPayment) + ",");
								sqlInsertPayments.append("'" + paymentAccount + "',");
								sqlInsertPayments.append("" + (int)previousCharge[2] + "");
								sqlInsertPayments.append("),");
							}
							
						}
						
						// Reset payment amount
						tmpPayment = amount;
					}
					
					// Set current charge as previous charge
					Object[] charge= {date, amount, userId};
					previousCharge = charge;
					
					
				}
				
				// Clean query
				sqlInsertPayments.deleteCharAt(sqlInsertPayments.toString().length() - 1);
				System.out.println("[Payments] ----> Payments generated");
				
				// Insert Payments
				System.out.println("[Payments] Inserting Payments...");
				statement.executeUpdate(sqlInsertPayments.toString());
				System.out.println("[Payments] --> SUCCESS: INSERT INTO `payments'");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// Close connection
			System.out.println("[Database] Disconnecting from database...");
			connect.close();
			System.out.println("[Database] --> Disconnected");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Helper method used by insertCurrentUsers() and insertPastUsers() to create rent charges.
	 * Creates monthly charges for the input lease terms.
	 * @param leaseStart
	 * @param leaseEnd
	 * @param monthlyRent
	 * @param uid
	 * @return sql query insert value details in the format "(...),..."
	 */
	private String sqlInsertRentCharges(LocalDate leaseStart, LocalDate leaseEnd, int monthlyRent, int uid) {
		StringBuilder insertQuery = new StringBuilder();

		// Loop while the term end count is ahead of the lease start
		LocalDate termEndCount = leaseEnd;
		while ((termEndCount.isEqual(leaseStart)) || (termEndCount.isAfter(leaseStart))) {
			
			// Sets start of term to first of the month (based on term end)
			LocalDate termStartCount = 
					LocalDate.of(termEndCount.getYear(), termEndCount.getMonthValue() , 1);
			
			// If the lease starts after the count
			if (termStartCount.isBefore(leaseStart)) {
				termStartCount = leaseStart;
			}
			
			/**
			 * Rent Charges
			 */
			
			// Calculate rent amounts and periods
			int termLengthDays = termEndCount.getDayOfMonth() - (termStartCount.getDayOfMonth() - 1);
			int rentAmount = (int)(((double)termLengthDays 
					/ (double)termEndCount.lengthOfMonth()) 
					* (double)monthlyRent);
			
			// Charge Name
			String chargeName = "Rent (" 
					+ termStartCount.format(DateTimeFormatter.ofPattern("MM/yyyy")) + ")";
			if (termLengthDays != termEndCount.lengthOfMonth()) {
				chargeName += " - (" + termLengthDays + "/" + termEndCount.lengthOfMonth() + " days)";
			}
			
			// Dates
			LocalDate postingDate = termStartCount;

			// Insert into charges query
			insertQuery.append("(");
			insertQuery.append("'" + chargeName + "',");
			insertQuery.append("'" + postingDate + "',");
			insertQuery.append("" + rentAmount + ",");
			insertQuery.append("" + uid + "");
			insertQuery.append("),");
			
			/*
			 * Service Fee
			 */
			
			String serviceFeeName = "Service Fee (" 
					+ termStartCount.format(DateTimeFormatter.ofPattern("MM/yyyy")) + ")";
			double serviceFee = 3.99;
			
			// Insert into charges query
			insertQuery.append("(");
			insertQuery.append("'" + serviceFeeName + "',");
			insertQuery.append("'" + postingDate + "',");
			insertQuery.append("" + serviceFee + ",");
			insertQuery.append("" + uid + "");
			insertQuery.append("),");
			
			// Move counters
			termEndCount = termEndCount.minus(1, ChronoUnit.MONTHS);
			termEndCount = LocalDate.of(termEndCount.getYear(), termEndCount.getMonthValue(), 
					termEndCount.lengthOfMonth());
		}
		return insertQuery.toString();
	}
	
	/**
	 * Truncates all database tables. Preserves rows in `users` with type 'admin'. 
	 */
	private static void clearDatabase() {
		
		// Connect to database
		System.out.println("[Database] Connecting...");
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			System.out.println("[Database] --> Connected to Database");
			Statement statement = connect.createStatement();

			// Create temporary table with same rows as `user`
			String sqlTemp =
					"""
					CREATE TEMPORARY TABLE admin_users_tmp( 
						user VARCHAR(255),
					    pass VARCHAR(255),
					    email VARCHAR(255),
					    first_name VARCHAR(255),
					    last_name VARCHAR(255),
					    type VARCHAR(255));
					""";

			// Copy admin accounts into temporary table
			String sqlTempFill =
					"""
					INSERT INTO admin_users_tmp(`user`,`pass`,`email`,`first_name`,`last_name`,`type`)
					SELECT `user`, `pass`, `email`, `first_name`, `last_name`, `type` 
					FROM users 
					WHERE type = 'admin'; 
					""";
			
			// Remove constraints
			String removeConstraint0 = "ALTER TABLE complaints DROP FOREIGN KEY `complaint_user`;";
			String removeConstraint1 = "ALTER TABLE complaints DROP FOREIGN KEY `complaint_unit`;";
			String removeConstraint2 = "ALTER TABLE leases DROP FOREIGN KEY `lease_unit`;";
			String removeConstraint3 = "ALTER TABLE leases DROP FOREIGN KEY `lease_user`;";
			String removeConstraint4 = "ALTER TABLE charges DROP FOREIGN KEY `charge_user`;";
			String removeConstraint5 = "ALTER TABLE payments DROP FOREIGN KEY `payment_user`;";
					
			// Truncate tables
			String leasesTruncate = "TRUNCATE leases;";
			String usersTruncate = "TRUNCATE users;";
			String unitsTruncate = "TRUNCATE units;";
			String complaintsTruncate = "TRUNCATE complaints;";
			String chargesTruncate = "TRUNCATE charges;";
			String paymentsTruncate = "TRUNCATE payments;";

			// Add constraints
			String addConstraint0 = "ALTER TABLE complaints ADD CONSTRAINT `complaint_user` FOREIGN KEY (`userid`) REFERENCES `users` (`uid`);";
			String addConstraint1 = "ALTER TABLE complaints ADD CONSTRAINT `complaint_unit` FOREIGN KEY (`unitid`) REFERENCES `units` (`id`);";
			String addConstraint2 = "ALTER TABLE leases ADD CONSTRAINT `lease_unit` FOREIGN KEY (`unitid`) REFERENCES `units` (`id`);";
			String addConstraint3 = "ALTER TABLE leases ADD CONSTRAINT `lease_user` FOREIGN KEY (`userid`) REFERENCES `users` (`uid`);";
			String addConstraint4 = "ALTER TABLE charges ADD CONSTRAINT `charge_user` FOREIGN KEY (`userid`) REFERENCES `users` (`uid`);";
			String addConstraint5 = "ALTER TABLE payments ADD CONSTRAINT `payment_user` FOREIGN KEY (`userid`) REFERENCES `users` (`uid`);";
			
			// Insert administrator accounts from temporary table
			String sqlTempCopy =
					"""
					INSERT INTO users(`user`, `pass`, `email`, `first_name`, `last_name`, `type`)
					SELECT `user`,`pass`,`email`,`first_name`,`last_name`,`type`
					FROM admin_users_tmp
					WHERE type = 'admin';
					""";
	
			//Query
			try {
				statement.executeUpdate(sqlTemp);
				System.out.println("[Clear Database] Saving admin users...");
				
				statement.executeUpdate(sqlTempFill);
				System.out.println("[Clear Database] --> Admin users saved");
				
				System.out.println("[Clear Database] Clearing tables...");
				
				// Remove constraints
				statement.executeUpdate(removeConstraint0);
				statement.executeUpdate(removeConstraint1);
				statement.executeUpdate(removeConstraint2);
				statement.executeUpdate(removeConstraint3);
				statement.executeUpdate(removeConstraint4);
				statement.executeUpdate(removeConstraint5);
				System.out.println("[Clear Database] --> Table constraints removed");
				
				/*
				 * Truncate
				 */
				
				statement.executeUpdate(leasesTruncate);
				System.out.println("[Clear Database] ----> SUCCESS: TRUNCATE `leases`");
				
				statement.executeUpdate(usersTruncate);
				System.out.println("[Clear Database] ----> SUCCESS: TRUNCATE `users`");
				
				statement.executeUpdate(unitsTruncate);
				System.out.println("[Clear Database] ----> SUCCESS: TRUNCATE `units`");
				
				statement.executeUpdate(complaintsTruncate);
				System.out.println("[Clear Database] ----> SUCCESS: TRUNCATE `complaints`");
				
				statement.executeUpdate(chargesTruncate);
				System.out.println("[Clear Database] ----> SUCCESS: TRUNCATE `charges`");
				
				statement.executeUpdate(paymentsTruncate);
				System.out.println("[Clear Database] ----> SUCCESS: TRUNCATE `payments`");
				
				System.out.println("[Clear Database] Configuring...");
				
				// Add constraints
				statement.executeUpdate(addConstraint0);
				statement.executeUpdate(addConstraint1);
				statement.executeUpdate(addConstraint2);
				statement.executeUpdate(addConstraint3);
				statement.executeUpdate(addConstraint4);
				statement.executeUpdate(addConstraint5);
				System.out.println("[Clear Database] --> Table constraints added");
				
				// Add administrator accounts
				statement.executeUpdate(sqlTempCopy);
				System.out.println("[Clear Database] --> Admin users added");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Close connection
			System.out.println("[Database] Disconnecting from Database...");
			connect.close();
			System.out.println("[Database] --> Disconnected");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

