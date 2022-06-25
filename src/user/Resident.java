package user;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import accounts.Charge;
import accounts.Lease;
import unit.Unit;

/**
 * A class representing a User that is a Resident.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Resident extends User {
	private boolean newAccount;
	
	// Unit
	private Unit currentUnit;
	private ArrayList<Unit> unitHistory = new ArrayList<Unit>();
	private ArrayList<String[]> unitHistoryString = new ArrayList<String[]>(); //building name, unit number
	
	// Lease
	private Lease latestLease;
	private ArrayList<Lease> leaseHistory = new ArrayList<Lease>();
	
	// Charges
	private ArrayList<Charge> charges = new ArrayList<Charge>();
	
	/**
	 * Creates a Resident with no units or leases.
	 * @param name
	 * @param email
	 * @param username
	 * @param password
	 * @param phone
	 */
	public Resident(String name, String email, String username, String password, int phone) {
		this.setName(name);
		this.setEmail(email);
		this.setType("Resident");
		this.setUsername(username);
		this.setPassword(password);
		this.setPhone(phone);
		this.currentUnit = null;
		this.latestLease = null;
		newAccount = true;
	}
	
	/**
	 * Returns NEW if the Resident has no previous lease, otherwise, ACTIVE or INACTIVE depending 
	 * on if they are currently assigned to a unit.
	 * @return NEW, ACTIVE, or INACTIVE
	 */
	public String getStatus() {
		if (newAccount) {
			return "NEW";
		}
		else {
			if (this.currentUnit == null) return "INACTIVE";
			else return "ACTIVE";
		}
	}
	
	/**
	 * @return true if this is a new account (no previous leases)
	 */
	public boolean isNewAccount() {
		return newAccount;
	}
	
	/*
	 * Unit
	 */

	/**
	 * @return the resident's current unit
	 */
	public Unit getUnit() {
		return currentUnit;
	}
	
	/**
	 * Sets the resident's unit with lease information.
	 * A lease is created in the process.
	 * @param unit
	 * @param leaseStart
	 * @param leaseEnd
	 * @param monthlyRent
	 */
	public void setUnit(Unit unit, LocalDate leaseStart, LocalDate leaseEnd, int monthlyRent) {
		newAccount = false;
		
		// Create and set resident lease
		this.setLease(new Lease(leaseStart, leaseEnd, monthlyRent, unit));
		leaseHistory.add(latestLease);
		
		// Set Resident Unit
		this.currentUnit = unit;
		unitHistory.add(unit);
		String[] unitNames = {unit.getBuildingName(), unit.getUnitNumber()};
		unitHistoryString.add(unitNames);
		
		// Add lease to unit
		unit.addLeaseHistory(latestLease);	
	}
	
	/**
	 * Removes the current unit assigned to this resident.
	 */
	public void removeUnit() {
		this.currentUnit = null;
	}
	
	/**
	 * @return Latest lease associated with this account
	 */
	public Unit getLatestUnitHistory() {
		if (newAccount) return null;
		return unitHistory.get(unitHistory.size() - 1);
	}
	
	/**
	 * @return building name, unit number
	 */
	public String[] getLatestUnitHistoryString() {
		if (newAccount) return null;
		return unitHistoryString.get(unitHistoryString.size() - 1);
	}
	
	/*
	 * Lease
	 */

	/**
	 * Sets this account's current lease
	 * @param lease
	 */
	public void setLease(Lease lease) {
		newAccount = false;
		this.latestLease = lease;
		charges.addAll(lease.getMonthlyCharges());
	}
	
	/**
	 * @return Latest lease associated with this account
	 */
	public Lease getLatestLeaseHistory() {
		if (newAccount) return null;
		return leaseHistory.get(leaseHistory.size() - 1);
	}
	
	/**
	 * @return true if the lease expired.
	 */
	public boolean isLeaseOver() {
		if (!this.isNewAccount()) {
			if(this.latestLease.getLeaseEnd().isBefore(LocalDate.now())) {
				return true;
			}
		}
		return false;	
	}

	/**
	 * @return Last lease start date
	 */
	public LocalDate getLeaseStart() {
		return latestLease.getLeaseStart();
	}
	
	/**
	 * @return Last lease end date
	 */
	public LocalDate getLeaseEnd() {
		return latestLease.getLeaseEnd();
	}
	
	/**
	 * @return Last lease monthly rent amount
	 */
	public int getLeaseAmount() {
		return latestLease.getMonthlyRent();
	}
	
	/**
	 * Checks if the latest lease ends in the next entered amount of months.
	 * @param months - number of months ahead
	 * @return true if the latest lease ends in the next amount of months
	 */
	public boolean leaseEndingMonths(int months) {
		if (!newAccount) {
			// If two months from now is after lease end
			if((LocalDate.now().plus(months, ChronoUnit.MONTHS)).isAfter(latestLease.getLeaseEnd())) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Charges
	 */
	
	/**
	 * @return All charges made to this account
	 */
	public ArrayList<Charge> getCharges() {
		return charges;
	}
	
	/**
	 * @return Sum of all posted charges' outstanding balance
	 */
	public double getBalance() {
		double balance = 0;
		
		for (Charge c : charges) {
			if (!c.getPostingDate().isAfter(LocalDate.now())) {
				balance += c.getRemainingBalance();
			}
		}
		return balance;
	}
	
	/**
	 * @return true if this resident is late on any charge
	 */
	public boolean isLate() {
		for (Charge c : charges) {
			if (c.isLate()) return true;
		}
		return false;
	}
	
	/**
	 * @return true if all of this resident's charges are fully paid
	 */
	public boolean isFullyPaid() {
		for (Charge c : charges) {
			if (!c.isPaid()) return false;
		}
		return true;
	}
}

