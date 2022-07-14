package main.java.ui;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import main.java.Session;

/**
 * A class that collects, calculates, and parses data from the database.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class DashboardData {
	private Session currentSession;
	
	private static int periodNumber = 24;
	
	private String title;
	private int totalUnits, occupied, openComplaints, rentableArea;
	
	// Average Rent / Square Feet
	private ArrayList<Object[]> averageRentPerArea = new ArrayList<Object[]>();
	
	// Gross Rents
	private ArrayList<Object[]> rentalRevenue = new ArrayList<Object[]>();
	
	// Vacancy Rate
	private ArrayList<Object[]> vacancyRate = new ArrayList<Object[]>();
	
	/**
	 * Creates a DashboardData object for the entered building.
	 * @param building
	 * @param session
	 */
	public DashboardData(String building, Session session) {

		this.currentSession = session;
		this.title = building;
		
		
		String dbUrl = session.getDbUrl();
		String dbUsername = currentSession.getUser();
		String dbPassword = currentSession.getAdminPass();

		/*
		 * Get summary variables
		 */
		
		totalUnits = 0;
		occupied = 0;
		openComplaints = 0;
		rentableArea = 0;

		// Prepare query
		String sqlGetTotalUnits = """
				SELECT 
					COUNT(unit) AS unit_count,
					COUNT(IF(current_lease_end < CURRENT_DATE(), 1, NULL)) AS vacant,
                	SUM(sqft) AS area
				
				FROM 
                	(
                    SELECT building, unit, current_lease_end, rent, sqft
					FROM
						(SELECT `unitid`, MAX(`end`) AS 'current_lease_end', `rent`, `userid` FROM `leases` GROUP BY `unitid`) as l
									
						RIGHT JOIN (SELECT id, building, floor, unit, sqft FROM units WHERE deleted = 0) as units 
						ON l.unitid = units.id
					) AS all_units 
				"""
				+ "WHERE building LIKE '%" 
				+ ((building.equals("")) ? "" : (building)) 
				+ "%'";
		
		String sqlGetOpenComplaints = """
				SELECT COUNT(IF(complaint_count.status = 'open', 1, NULL)) as open_complaints
				FROM (
				    SELECT `status`, unitid, building 
				    FROM `complaints`
				    JOIN units 
				    ON complaints.unitid = units.id
				"""
				+ "WHERE building LIKE '%" 
				+ ((building.equals("")) ? "" : (building)) 
				+ "%') AS complaint_count";

		// Connect to database
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			Statement statement = connect.createStatement();

			// Query
			
			ResultSet result = statement.executeQuery(sqlGetTotalUnits);
			while (result.next()) {
				totalUnits = result.getInt("unit_count");
				occupied = totalUnits - result.getInt("vacant");
				rentableArea = result.getInt("area");
			}
			
			result = statement.executeQuery(sqlGetOpenComplaints);
			while (result.next()) openComplaints = result.getInt("open_complaints");
			

			// Close connection
			connect.close();
		} catch (SQLException err) {
			err.printStackTrace();
		}
		
		/*
		 * Get Average Rent/sqft
		 */
		
		LocalDate[] periods = generateMonthPeriods(periodNumber);
		
		// Each period
		for (LocalDate periodEnd : periods) {
			LocalDate periodStart = periodEnd.minus(periodEnd.lengthOfMonth() - 1, ChronoUnit.DAYS);
			
			// Rent Revenue
			double paymentRevenue = 0;
			
			// Average Rent variables
			int totalRentedArea = 0;
			int totalRentCharged = 0;
			
			// Vacancy Rate variables
			int totalOccupied = 0;
			int unitAmount = 0;
			
			
			// Prepare query
			String sqlGetRevenue = """
					SELECT ROUND(SUM(amount), 2) AS revenue 
					FROM  payments
					JOIN (
					    SELECT uid, unitid, building
					    FROM users
					    LEFT JOIN (
					        SELECT *
					        FROM (
					            SELECT MAX(`end`) AS `recent_end`, unitid, userid
					            FROM leases """ 
					        + " WHERE leases.start < '" + periodEnd + "' "
					        + "AND leases.end > '" + periodStart + "' "
					        + """
					            GROUP BY userid
					        ) AS recent_leases
					        JOIN units ON recent_leases.unitid = units.id
					    ) AS l
					    ON users.uid = l.userid
					) AS u
					ON payments.userid = u.uid 
					"""
					+ "WHERE payments.date >= '" + periodStart + "' "
					+ "AND payments.date <= '" + periodEnd + "' "
					+ "AND building LIKE '%" + building + "%'";
			
			String sqlGetRentArea = """
					SELECT SUM(rent) AS rent_charged, SUM(sqft) as area_rented FROM(
					    SELECT unitid, unit, building, `start`, `end`, rent, sqft, deleted, created
					    FROM (SELECT unitid, `start`, `end`, rent FROM leases) as l
					    JOIN units
					    ON l.unitid = units.id
					"""
					+ "WHERE l.end > '" + periodStart + "' " 
					+ "AND l.start < '" + periodEnd + "' "
					+ "AND building LIKE '%" + building + "%' "
					+ "AND created < '" + periodEnd + "' "
					+ "AND (l.end >= CURRENT_DATE() OR l.end > '" + periodEnd + "') "
					+ """ 
					    GROUP BY unitid
					) AS leases_in_count
					""";
			
			String sqlGetOccupied = """
					SELECT COUNT(unitid) AS occupied FROM(
					    SELECT unitid, unit, building, `start`, `end`, deleted, created
					    FROM (SELECT unitid, `start`, `end` FROM leases) as l
					    JOIN units
					    ON l.unitid = units.id
					"""
					+ "WHERE l.end > '" + periodStart + "' " 
					+ "AND l.start < '" + periodEnd + "' "
					+ "AND building LIKE '%" + building + "%' "
					+ "AND created < '" + periodEnd + "' "
					+ "AND (l.end >= CURRENT_DATE() OR l.end > '" + periodEnd + "') "
					+ """ 
					    GROUP BY unitid
					) AS leases_in_count
					""";
			
			String sqlGetUnitAmount = "SELECT COUNT(IF(units.created <= '" 
					+ periodEnd + "',1,NULL)) AS unit_count "
					+ "FROM `units` "
					+ "WHERE building LIKE '%" + building + "%'";

			// Connect to database
			try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
				Statement statement = connect.createStatement();

				// Query
				ResultSet result = statement.executeQuery(sqlGetRevenue);
				while (result.next()) {
					paymentRevenue = result.getDouble("revenue"); // TODO Change label to "payment revenue"
				}
				
				result = statement.executeQuery(sqlGetRentArea);
				while (result.next()) {
					totalRentCharged = result.getInt("rent_charged");
					totalRentedArea = result.getInt("area_rented");
				}
				
				result = statement.executeQuery(sqlGetOccupied);
				while (result.next()) {
					totalOccupied = result.getInt("occupied");
				}
				
				result = statement.executeQuery(sqlGetUnitAmount);
				while (result.next()) {
					unitAmount = result.getInt("unit_count");
				}

				// Close connection
				connect.close();
			} catch (SQLException err) {
				err.printStackTrace();
			}

			// Average Rent Parse
			double averageRentPerSqft = ((double)totalRentCharged / (double)totalRentedArea);
			if ((double)totalRentedArea == 0) averageRentPerSqft = 0;
			
			Object[] averageRentPoint = {periodEnd, averageRentPerSqft};
			averageRentPerArea.add(averageRentPoint);
			
			// Vacancy Rate Parse
			Object[] vacancyRatePoint = {periodEnd, ((double)(unitAmount - totalOccupied)/ (double)unitAmount)};
			vacancyRate.add(vacancyRatePoint);
			
			// Rent Revenue
			Object[] grossRentsPoint = {periodEnd, paymentRevenue};
			rentalRevenue.add(grossRentsPoint);	
		}
	}
	
	/**
	 * @return title of this data set
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return The total number of units in this data set
	 */
	public int getTotalUnits() {
		return totalUnits;
	}

	/**
	 * @return Total number of occupied units in this data set
	 */
	public int getOccupied() {
		return occupied;
	}

	/**
	 * @return Total number of open complaints
	 */
	public int getOpenComplaints() {
		return openComplaints;
	}

	/**
	 * @return The total sum of unit areas
	 */
	public int getRentableArea() {
		return rentableArea;
	}

	/**
	 * @return List of periods and total rental revenue in each
	 */
	public ArrayList<Object[]> getRents() {
		return rentalRevenue;
	}

	/**
	 * @return List of periods and vacancy rate in each
	 */
	public ArrayList<Object[]> getVacancyRate() {
		return vacancyRate;
	}
	
	/**
	 * Returns an array of arrays in the 
	 * format [LocalDate endOfPeriod, double averageRentPerArea]
	 * rounds the raw numbers.
	 * @return List of periods and average rent per area in each
	 */
	public ArrayList<Object[]> getAverageRentPerArea() {
		ArrayList<Object[]> roundedList = new ArrayList<Object[]>();
		//Round
		for (Object[] point : averageRentPerArea) {
			DecimalFormat df = new DecimalFormat("0.00");
			df.setRoundingMode(RoundingMode.HALF_UP);
			Object[] roundedPoint = {point[0], Double.parseDouble((df).format(point[1]))};
			roundedList.add(roundedPoint);
		}
		return roundedList;
	}

	/**
	 * Creates an array with the amount of months specified before the current month.
	 * @param amount - Length of month periods to make the array
	 * @return an array of LocalDate objects
	 */
	public LocalDate[] generateMonthPeriods(int amount) {
		LocalDate[] months = new LocalDate[amount];
		for (int i = months.length - 1; i >= 0; i--) {
			// Each period ends at the end of the month
			if (i == months.length - 1) {
				months[i] = LocalDate.now().minus(LocalDate.now().getDayOfMonth(), ChronoUnit.DAYS).plus(LocalDate.now().lengthOfMonth(), ChronoUnit.DAYS);
				continue;
			}
			months[i] = months[i + 1].minus(months[i + 1].lengthOfMonth(), ChronoUnit.DAYS);
		}
		return months;
	}
	
	/*
	 * Diagnostic
	 */
	
	public void averageRentPerAreaPrint() {
		System.out.println("AVERAGE RENT/SQFT");
		double max = 0;
		double min = 0;
		
		for (Object[] point : averageRentPerArea) {
			if ((double)point[1] > max) max = (double)point[1];
			if (min == 0) min = (double)point[1];
			else if ((double)point[1] < min) min = (double)point[1];
		}
		
		double percentPadding = 0.1;
		double range = max - min;
		double padding = range * percentPadding;
		max += padding/2;
		min -= padding/2;
		
		for (Object[] point : averageRentPerArea) {
			StringBuilder bar = new StringBuilder();
			for (int i = 0; i < (((double)point[1] - (min)) * 100); i++) {
				bar.append("=");
			}
			System.out.println("Period ending " + point[0] + " : " 
			+ (new DecimalFormat("0.00")).format(point[1]) 
			+ " " + bar.toString());
		}
	}
	
	public void vacancyRatePrint() {
		System.out.println("VACANCY RATE");
		double max = 0;
		double min = 0;
		
		for (Object[] point : averageRentPerArea) {
			if ((double)point[1] > max) max = (double)point[1];
		}
		
		double percentPadding = 0.1;
		double range = max - min;
		double padding = range * percentPadding;
		max += padding/2;
		
		for (Object[] point : vacancyRate) {
			StringBuilder bar = new StringBuilder();
			for (int i = 0; i < (((double)point[1]) * 100); i++) {
				bar.append("=");
			}
			System.out.println("Period ending " + point[0] + " : " 
			+ (new DecimalFormat("0.00")).format(point[1]) 
			+ " " + bar.toString());
		}
	}
	
	public void grossRentsPrint() {
		System.out.println("GROSS RENTS");
		double max = 0;
		double min = 0;
		
		for (Object[] point : rentalRevenue) {
			if ((double)point[1] > max) max = (double)point[1];
			if (min == 0) min = (double)point[1];
			else if ((double)point[1] < min) min = (double)point[1];
		}
		
		double percentPadding = 0.1;
		double range = max - min;
		double padding = range * percentPadding;
		max += padding/2;
		min -= padding/2;
		
		for (Object[] point : rentalRevenue) {
			StringBuilder bar = new StringBuilder();
			for (int i = 0; i < (((double)point[1]) /1000.00); i++) {
				bar.append("=");
			}
			System.out.println("Period ending " + point[0] + " : " 
			+ (new DecimalFormat("0.00")).format(point[1])
			+ " " + bar.toString());
		}
	}
	
	public void toPrint() {
		System.out.println("Building: " + title);
		System.out.println("Total Units: " + totalUnits);
		System.out.println("Occupied: " + occupied);
		System.out.println("Open Complaints: " + openComplaints);
		System.out.println("Rentable Area: " + rentableArea);
		
		System.out.println();
		averageRentPerAreaPrint();
		System.out.println();
		vacancyRatePrint();
		System.out.println();
		grossRentsPrint();
	}
}

