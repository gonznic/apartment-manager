package gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import accounts.Charge;
import accounts.Lease;
import accounts.Payment;
import unit.Building;
import unit.BuildingList;
import unit.Unit;

/**
 * A class that collects and calculates data from a Building or BuidlingList.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class DashboardData {
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
	 */
	public DashboardData(Building building) {

		this.title = building.getName();
		
		/*
		 * Get summary variables
		 */
		
		totalUnits = building.getAllUnits().size();
		occupied = 0;
		openComplaints = 0;
		rentableArea = 0;
		for (Unit u : building.getAllUnits()) {
			if (!u.isEmpty()) {
				occupied++;
				openComplaints += u.getResident().getOpenComplaintsNumber();
			}
			rentableArea += u.getFloorArea();
		}
		
		/*
		 * Get Average Rent/sqft
		 */
		
		LocalDate[] periods = generateMonthPeriods(periodNumber);
		
		// Each period
		for (LocalDate periodEnd : periods) {
			LocalDate periodStart = periodEnd.minus(periodEnd.lengthOfMonth() - 1, ChronoUnit.DAYS);
			
			// Average Rent variables
			int totalRentedArea = 0;
			int totalRentCharged = 0;
			
			// Vacancy Rate variables
			int totalOccupied = 0;
			int unitAmount = 0;
			
			// Rent Revenue
			double rentRevenue = 0;
			
			// Loop
			for (Unit u : building.getAllUnits()) {
				unitAmount++;
				for (Lease l : u.getLeaseHistory()) {
					
					if (l.getLeaseStart().isBefore(periodStart) && !l.getLeaseEnd().isBefore(periodStart)) {
						totalRentedArea += u.getFloorArea();
						totalRentCharged += l.getMonthlyRent();
						totalOccupied++;
					}
					for (Charge c : l.getMonthlyCharges()) {
						for (Payment p : c.getPayments()) {
							if (!p.getDate().isBefore(periodStart) && !p.getDate().isAfter(periodEnd)) {
								rentRevenue += p.getAmount();
							}
						}
					}
				}
			}
			// Average Rent Parse
			double averageRentPerSqft = ((double)totalRentCharged / (double)totalRentedArea);
			if ((double)totalRentedArea == 0) averageRentPerSqft = 0;
			
			Object[] averageRentPoint = {periodEnd, averageRentPerSqft};
			averageRentPerArea.add(averageRentPoint);
			
			// Vacancy Rate Parse
			Object[] vacancyRatePoint = {periodEnd, ((double)(unitAmount - totalOccupied)/ (double)unitAmount)};
			vacancyRate.add(vacancyRatePoint);
			
			//Rent Revenue
			Object[] grossRentsPoint = {periodEnd, rentRevenue};
			rentalRevenue.add(grossRentsPoint);	
		}
	}
	
	/**
	 * Creates a DashboardData object for the entered building list.
	 * @param buildingList
	 */
	public DashboardData(BuildingList buildingList) {
		this.title = "All Buildings";
		
		// Put individual building data into list
		ArrayList<DashboardData> buildingData = new ArrayList<DashboardData>();
		for (Building b : buildingList.getAllBuildings()) {
			buildingData.add(new DashboardData(b));
		}

		// Combine data
		totalUnits = 0;
		occupied = 0;
		openComplaints = 0;
		rentableArea = 0;
		
		//Base (first unit)
		averageRentPerArea = buildingData.get(0).getAverageRentPerArea();
		rentalRevenue = buildingData.get(0).getRents();
		vacancyRate = buildingData.get(0).getVacancyRate();
		
		// Loop (build on base)
		for (DashboardData dd : buildingData) {
			totalUnits += dd.getTotalUnits();
			occupied += dd.getOccupied();
			openComplaints += dd.getOpenComplaints();
			int previousRentableArea = rentableArea;
			rentableArea += dd.getRentableArea();
			
			// If not first building (base)
			if (dd != buildingData.get(0)) {
				
				// Loop
				for (int i = 0; i < periodNumber; i++) {
					
					// Average Rent/Area
					double newAverageRent = (((double)averageRentPerArea.get(i)[1]) * ((double)previousRentableArea / (double)rentableArea))
							+ (((double)dd.getAverageRentPerArea().get(i)[1]) * ((double)dd.getRentableArea() / (double)rentableArea));
					averageRentPerArea.get(i)[1] = newAverageRent;
				
					// Gross Rents
					double sum = (double)(rentalRevenue.get(i)[1]) + (double)(dd.getRents().get(i)[1]);
					rentalRevenue.get(i)[1] = sum;
					
					// Vacancy Rate
					double newVacancyRate = (((double)vacancyRate.get(i)[1]) * ((double)previousRentableArea / (double)rentableArea))
							+ (((double)dd.getVacancyRate().get(i)[1]) * ((double)dd.getRentableArea() / (double)rentableArea));
					vacancyRate.get(i)[1] = newVacancyRate;
				}		
			}	
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

