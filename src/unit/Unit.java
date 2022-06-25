package unit;

import java.util.ArrayList;

import accounts.Lease;
import unit.Building.Floor;
import user.Resident;

/**
 * A class representing a real estate unit.
 * A new unit is composed of a unit number, the name of its building, the floor it is on, and 
 * the floor area in square feet.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Unit {
	// General
	private String unitNumber;
	private String buildingName;
	private Building building;
	private int floorInt;
	private Floor floor;
	private int floorArea;
	
	// Leases
	private ArrayList<Lease> leaseHistory = new ArrayList<Lease>();
	private Resident resident;
	private boolean isEmpty;
	
	/**
	 * Creates a new empty unit.
	 * @param unitNumber
	 * @param building
	 * @param floorNumber
	 * @param floorArea
	 */
	public Unit(String unitNumber,String building, int floorNumber, int floorArea) {
		this.unitNumber = unitNumber;
		this.buildingName = building;
		this.floorInt = floorNumber;
		this.floorArea = floorArea;
		isEmpty = true;
	}
	
	/**
	 * @return Unit number
	 */
	public String getUnitNumber() {
		return unitNumber;
	}
	
	/**
	 * @param unitNumber
	 */
	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	/**
	 * @return Building name
	 */
	public String getBuildingName() {
		return this.buildingName;
	}
	
	/**
	 * Returns the Building object associated with this unit.
	 * @return building
	 */
	public Building getBuilding() {
		return building;
	}
	
	/**
	 * Sets this unit's building.
	 * @param building
	 */
	public void setBuilding(Building building) {
		this.buildingName = building.getName();
		this.building = building;
	}
	
	/**
	 * Returns the integer of the floor this unit is on.
	 * @return floor number
	 */
	public int getFloorInt() {
		return floorInt;
	}
	
	/**
	 * @return floor - Floor object this unit in on.
	 */
	public Floor getFloor() {
		return this.floor;
	}
	
	/**
	 * Sets the Floor the unit is on/
	 * Updates floorInt.
	 * @param floor
	 */
	public void setFloor(Floor floor) {
		this.floor = floor;
		this.floorInt = floor.getNumber();
	}

	/**
	 * @return floor area in square feet
	 */
	public int getFloorArea() {
		return floorArea;
	}
	
	/**
	 * @param floorArea
	 */
	public void setFloorArea(int floorArea) {
		this.floorArea = floorArea;
	}
	
	/**
	 * @return true if the unit is empty or false if occupied
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
	
	/**
	 * @return Resident assigned to unit or null
	 */
	public Resident getResident() {
		if (isEmpty) return null;
		return resident;
	}
	
	/**
	 * Sets the resident in this unit.
	 * @param resident
	 */
	public void setResident(Resident resident) {
		if (isEmpty) {
			this.resident = resident;
			isEmpty = false;
		}
	}
	
	/**
	 * Removes the resident in this unit.
	 */
	public void removeResident() {
		isEmpty = true;
		resident = null;
	}
	
	/**
	 * Returns a list of all past leases.
	 * @return Lease history
	 */
	public ArrayList<Lease> getLeaseHistory() {
		return leaseHistory;
	}
	
	/**
	 * Adds a lease to this unit's lease history.
	 * @param lease
	 */
	public void addLeaseHistory(Lease lease) {
		leaseHistory.add(lease);
	}
}

