package accounts;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import unit.Unit;

/**
 * 
 * A class representing a residential apartment lease.
 * The lease is linked to a unit and has start and end dates.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Lease {
	private Unit unit;
	
	private LocalDate leaseStart;
	private LocalDate leaseEnd;
	
	private int monthlyRent;
	private ArrayList<Charge> rent;
	
	/**
	 * Creates a lease with the given parameters. 
	 * Creates charges for each month. Creates partial charges for partial months.
	 * @param leaseStart - Start date of lease
	 * @param leaseEnd - End date of lease
	 * @param monthlyRent - Rent cost
	 * @param unit - Unit associated with the lease
	 */
	public Lease(LocalDate leaseStart, LocalDate leaseEnd, int monthlyRent, Unit unit) {
		this.unit = unit;
		this.leaseStart = leaseStart;
		this.leaseEnd = leaseEnd;
		this.monthlyRent = monthlyRent;
		rent = new ArrayList<Charge>();
		
		LocalDate termEndCount = leaseEnd;
		
		// Loop while the term end count is ahead of the lease start
		while ((termEndCount.isEqual(leaseStart)) || (termEndCount.isAfter(leaseStart))) {
			
			// Sets start of term to first of the month (based on term end)
			LocalDate termStartCount = 
					LocalDate.of(termEndCount.getYear(), termEndCount.getMonthValue() , 1);
			
			// If the lease starts after the count
			if (termStartCount.isBefore(leaseStart)) {
				termStartCount = leaseStart;
			}
			
			// Create Monthly Charge
			Charge monthlyCharge = 
					new Charge((termStartCount.format(DateTimeFormatter.ofPattern("MMMM")) + " Charge"), 
							termStartCount, termStartCount.plus(7, ChronoUnit.DAYS));
			rent.add(monthlyCharge);
		
			// Add rent charge
			int termLengthDays = termEndCount.getDayOfMonth() - (termStartCount.getDayOfMonth() - 1);
			
			int rentAmount = (int)(((double)termLengthDays 
					/ (double)termEndCount.lengthOfMonth()) 
					* (double)monthlyRent);
			
			// Partial month charges
			if (termLengthDays != termEndCount.lengthOfMonth()) {
				monthlyCharge.append(new Charge("Rent (" + termLengthDays + "/" 
						+ termEndCount.lengthOfMonth() + " days)", rentAmount));
			}
			else {
				monthlyCharge.append(new Charge("Rent", rentAmount));
			}
			
			// Service fee sub charge
			monthlyCharge.append(new Charge("Service Fee", 3.99));
		
			termEndCount = termEndCount.minus(1, ChronoUnit.MONTHS);
			termEndCount = LocalDate.of(termEndCount.getYear(), termEndCount.getMonthValue(), 
					termEndCount.lengthOfMonth());
		}
	}
	
	/**
	 * @return Unit associated with this lease
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @return Lease start date
	 */
	public LocalDate getLeaseStart() {
		return leaseStart;
	}
	
	/**
	 * @param leaseStart
	 */
	public void setLeaseStart(LocalDate leaseStart) {
		this.leaseStart = leaseStart;
	}
	
	/**
	 * @return Lease end date
	 */
	public LocalDate getLeaseEnd() {
		return leaseEnd;
	}
	
	/**
	 * @param leaseEnd
	 */
	public void setLeaseEnd(LocalDate leaseEnd) {
		this.leaseEnd = leaseEnd;
	}
	
	/**
	 * @return Monthly rent amount
	 */
	public int getMonthlyRent() {
		return monthlyRent;
	}
	
	/**
	 * @param monthlyRent
	 */
	public void setMonthlyRent(int monthlyRent) {
		this.monthlyRent = monthlyRent;
	}
	
	/**
	 * @return A list of all rent charges.
	 */
	public ArrayList<Charge> getMonthlyCharges() {
		return rent;
	}
	
	/**
	 * @return true if the lease end date passed
	 */
	public boolean isFinished() {
		if (this.leaseEnd.isBefore(LocalDate.now())) return true;
		return false;
	}
	
	/**
	 * @return true if all of the lease's charges are paid
	 */
	public boolean isPaid() {
		for (Charge c : rent) {
			if (!c.isPaid()) return false;
		}
		return true;
	}
}
