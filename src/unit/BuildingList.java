package unit;

import java.util.ArrayList;

/**
 * A class representing a list of buildings.
 * To be used with Building.java.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class BuildingList {
	private ArrayList<Building> allBuildings;
	private int unitCount = 0;
	
	/**
	 * Creates an empty building list.
	 */
	public BuildingList() {
		allBuildings = new ArrayList<Building>();
	}

	/**
	 * @return A list of all buildings
	 */
	public ArrayList<Building> getAllBuildings() {
		//TODO Sort
		return allBuildings;
	}

	/**
	 * Searches for existing Building objects with the given name.
	 * Returns the Building if it is found.
	 * Creates and returns the building if one with the searched name is not in the list.
	 * @param buildingName - To add or search for
	 * @return Building added or found
	 */
	public Building addFindBuilding(String buildingName) {
		// Check if building exists
		for (Building b : allBuildings) {
			if (b.getName().equals(buildingName)) {
				return b;
			}
		}
		// If building does not exist
		Building building = new Building(buildingName);
		allBuildings.add(building);
		return building;
	}
	
	/**
	 * @return total count of units in this building list
	 */
	public int getUnitCount() {
		return this.unitCount;
	}
	
	/**
	 * @param unit
	 * @return true if the building is added
	 */
	public boolean addUnit(Unit unit) {
		
		//Get or add the building
		Building building = addFindBuilding(unit.getBuildingName());
		
		// Set unit's building
		unit.setBuilding(building);
		
		// Add unit to building;
		this.unitCount++;
		return building.addUnit(unit);
	}
	
	/**
	 * @param unit - The unit to be removed
	 * @return true if the unit is found and removed
	 */
	public boolean removeUnit(Unit unit) {
		Building removeFrom = unit.getBuilding();
		if (removeFrom.removeUnit(unit)) {
			this.unitCount--;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the vacancy rate of the building list.
	 * That is, the percentage of units that are unoccupied (empty).
	 * @return the vacancy rate
	 */
	public double getVacancyRate() {
		double vacant = 0;
		double total = 0;
		
		for (Building b : allBuildings) {
			for (Unit u: b.getAllUnits()) {
				if (u.isEmpty()) vacant++;
				total++;
			}
		}
		return (1 - ((total - vacant) / total));
	}
	
	/**
	 * Returns an array with the count of occupied units at index 0 and the count of 
	 * vacant units at index 1.
	 * @return occupiedCount, vacantCount
	 */
	public int[] getVacancyStats() {
		int vacantCount = 0;
		int occupiedCount = 0;
		
		for (Building b : allBuildings) {
			for (Unit u: b.getAllUnits()) {
				if (u.isEmpty()) vacantCount++;
				else occupiedCount++;
			}
		}
		return new int[] {occupiedCount, vacantCount};
	}
	
	/**
	 * Returns an array with the count of residents on time on their payments at index 0 and 
	 * the count of residents late on their payments at index 1.
	 * @return onTimeCount, overdueCount
	 */
	public int[] getPaymentStats() {
		int onTimeCount = 0;
		int overdueCount = 0;
		
		for (Building b : allBuildings) {
			for (Unit u: b.getAllUnits()) {
				if (!u.isEmpty()) {
					// If on time
					if (!u.getResident().isLate()) onTimeCount++;
					// If late
					else overdueCount++;
				}
			}
		}
		return new int[] {onTimeCount, overdueCount};
	}
	
	/**
	 * Returns the count of residents with leases ending in the range between now and given 
	 * amount of months.
	 * @param month - The range of months
	 * @return The count of leases ending in the input range
	 */
	public int getLeaseEndMonths(int month) {
		int leasesEnding = 0;
		
		for (Building b : allBuildings) {
			for (Unit u: b.getAllUnits()) {
				// If unit is occupied
				if (!u.isEmpty()) {
					// If lease ending in months
					if (u.getResident().leaseEndingMonths(month)) leasesEnding++;
				}
			}
		}
		return leasesEnding;
	}
}

