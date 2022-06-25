package unit;

import java.util.ArrayList;

import user.Resident;

/**
 * A class that represents a building.
 * To be used with Unit.java.
 * 
 * Contains Floor nested class.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Building {
	private String name;
	private ArrayList<Floor> allFloors = new ArrayList<Floor>();
	
	/**
	 * Creates a building with the given name.
	 * @param name
	 */
	public Building(String name) {
		this.name = name;
	}
	
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return all floors in this building
	 */
	public ArrayList<Floor> getAllFloors() {
		// TODO sort
		return allFloors;
	}
	
	/**
	 * @param number - Floor number
	 * @return true if a floor is added, false if it already exists
	 */
	public boolean addFloor(int number) {
		
		// Check if floor already exists
		for (Floor f : allFloors) {
			if (f.getNumber() == number) {
				return false;
			}
		}
		
		// Add new floor
		Floor newFloor = new Floor(number);
		allFloors.add(newFloor);		
		return true;
	}
	
	/**
	 * Returns a floor with the given number.
	 * @param floorNumber
	 * @return Floor object or null
	 */
	public Floor findFloor(int floorNumber) {
		for (Floor f : allFloors) {
			if (f.getNumber() == floorNumber) {
				return f;
			}
		}
		return null;
	}
	
	/**
	 * @return List of all units in this building
	 */
	public ArrayList<Unit> getAllUnits() {
		//TODO Sort
		ArrayList<Unit> allUnits = new ArrayList<Unit>();
		for (Floor f : allFloors) {
			for (Unit u : f.getAllUnits()) {
				allUnits.add(u);
			}
		}
		return allUnits;
	}
	
	/**
	 * @param unit - Unit to be added
	 * @return true if the unit is added false if it already exists
	 */
	public boolean addUnit(Unit unit) {
		
		// If floor exists
		for (Floor f : allFloors) {
			if (f.getNumber() == unit.getFloorInt()) {
				
				// If unit does not already exist
				if (f.addUnit(unit)) return true;
				else return false;
			}
		}
		
		// If floor does not exist, add new floor
		Floor newFloor = new Floor(unit.getFloorInt());
		
		// Link floor to unit
		newFloor.addUnit(unit);
		
		// Add floor to building
		allFloors.add(newFloor);		
		return true;
	}
	
	/**
	 * @param unit - Unit to be removed
	 * @return true if the unit is found and removed
	 */
	public boolean removeUnit(Unit unit) {
		for (Floor f : allFloors) {
			if (unit.getFloor() == f) {
				f.removeUnit(unit);
				unit.setBuilding(null);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return List of all residents
	 */
	public ArrayList<Resident> getAllResidents(){
		ArrayList<Resident> allUsers = new ArrayList<Resident>();
		
		for (Floor f : allFloors) {
			for (Unit u : f.getAllUnits()) {
				if (!u.isEmpty()) {
					allUsers.add(u.getResident());
				}
			}
		}
		return allUsers;
	}
	
	/**
	 * A class that represents a floor. A floor contains a list of units.
	 */
	public class Floor {
		int number;
		ArrayList<Unit> allUnits = new ArrayList<Unit>();
		
		/**
		 * Creates a floor with the given number.
		 * @param number
		 */
		Floor(int number){
			this.number = number;
		}
		
		/**
		 * Returns floor number.
		 * @return number
		 */
		public int getNumber() {
			return number;
		}

		/**
		 * @param unit
		 * @return true if the unit is added or false if it already exists
		 */
		public boolean addUnit(Unit unit) {
			// Check if unit already exists
			for (Unit u : allUnits) {
				if (u.getUnitNumber().equalsIgnoreCase(unit.getUnitNumber())) {
					return false;
				}
			}
			
			unit.setFloor(this);
			allUnits.add(unit);
			return true;
		}
		
		/** 
		 * @param unit
		 * @return true if the unit is removed
		 */
		public boolean removeUnit(Unit unit) {
			if(allUnits.remove(unit)) {
				unit.setFloor(null);
				return true;
			}
			return false;
		}
		
		/**
		 * @return List of all units
		 */
		public ArrayList<Unit> getAllUnits(){
			//TODO Sort
			return allUnits;
		}	
	}
}

