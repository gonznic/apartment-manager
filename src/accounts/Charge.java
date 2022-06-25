package accounts;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A class representing a charge. A charge can be composed of multiple charges.
 * Each charge has a name, an amount, a balance, and post and due dates.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Charge {
	// General
	private String name;
	private double amount;
	private double balance;
	
	// Dates
	private LocalDate postingDate;
	private LocalDate dueDate;
	
	// Sub Charges and Payments
	private ArrayList<Payment> payments = new ArrayList<Payment>();
	private ArrayList<Charge> includedCharges = new ArrayList<Charge>();
	
	/**
	 * Creates an empty charge with the given name.
	 * @param name
	 */
	public Charge(String name) {
		this.name = name;	
	}
	
	/**
	 * Creates a charge with the given name and amount.
	 * Used for a charge without dates.
	 * @param name
	 * @param amount
	 */
	public Charge(String name, double amount) {
		this(name);
		this.amount = amount;
		this.balance = amount;
	}
	
	/**
	 * Creates a charge with the given amount and dates.
	 * Used for a charge group (no amount).
	 * @param name
	 * @param postingDate
	 * @param dueDate
	 */
	public Charge(String name, LocalDate postingDate, LocalDate dueDate) {
		this(name);
		this.postingDate = postingDate;
		this.dueDate = dueDate;
	}
	
	/**
	 * Creates a charge with the given variables.
	 * Used for stand-alone charges and sub-charges.
	 * @param name
	 * @param amount
	 * @param postingDate
	 * @param dueDate
	 */
	public Charge(String name, double amount, LocalDate postingDate, LocalDate dueDate) {
		this(name);
		this.amount = amount;
		this.balance = amount;
		this.postingDate = postingDate;
		this.dueDate = dueDate;
	}

	/**
	 * @return Charge name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name - Name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Initial charge amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param Initial charge amount
	 */
	public void setAmount(double amount) {
		if (amount < balance) this.amount = amount;
	}

	/**
	 * @return Posting date
	 */
	public LocalDate getPostingDate() {
		return postingDate;
	}

	/**
	 * @param postingDate
	 */
	public void setPostingDate(LocalDate postingDate) {
		this.postingDate = postingDate;
	}

	/**
	 * @return Due date
	 */
	public LocalDate getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate
	 */
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	
	/**
	 * @return List of payments
	 */
	public ArrayList<Payment> getPayments() {
		return payments;
	}
	
	/**
	 * Creates a payment and adds it to the charge. 
	 * The payment cannot be more than the remaining balance.
	 * @param paymentAmount - Amount being paid
	 * @param paymentAccount - A credit card number
	 * @param date - The date the payment is made
	 * @return true of the payment was successfully made
	 */
	public boolean makePayment(double paymentAmount, int paymentAccount, LocalDate date) {
		// Check amount (can't pay more than balance)
		if (paymentAmount > balance) return false;
		boolean paymentMade = false;
		
		// Check credit card
		if (validatePaymentAccount(Integer.toString(paymentAccount))) {
			
			/*
			 * This is where the payment would be processed
			 */
			
			if (!payments.add(new Payment(date, paymentAmount, paymentAccount, this.name))) {
				return false;
			}
			balance -= paymentAmount;
			paymentMade = true;
		}
		return paymentMade;
	}
	
	/**
	 * Returns the last payment made.
	 * @return Last payment on payment list or null if empty
	 */
	public Payment getLastPayment() {
		if (payments.isEmpty()) return null;
		return payments.get(payments.size() - 1);
	}
	
	/**
	 * Returns a list of sub charges.
	 * @return List of included charges
	 */
	public ArrayList<Charge> getIncludedCharges() {
		return includedCharges;
	}

	/**
	 * Adds a sub charge to this charge
	 * @param newCharge - charge to include
	 * @return true if the included charge is added
	 */
	public boolean append(Charge newCharge) {
		if (includedCharges.add(newCharge)) {
			this.amount += newCharge.getAmount();
			this.balance += newCharge.getAmount();
		}
		return false;
	}

	/**
	 * Returns the string representation of the charge status.
	 * @return String PAID, ON TIME, or OVERDUE
	 */
	
	public String getStatus() {
		if (isPaid()) return "PAID";
		else {
			if (dueDate.isAfter(LocalDate.now())) {
				return "ON TIME";
			}
			else {
				return "OVERDUE";
			}
		}
	}
	
	/**
	 * @return true if charge is paid
	 */
	public boolean isPaid() {
		if (balance <= 0) return true;
		return false;
	}
	
	/**
	 * @return true if remaining balance is less than initial amount
	 */
	public boolean isPartiallyPaid() {
		if (balance < amount) return true;
		return false;
	}
	
	/**
	 * @return true if the charge is due and not fully paid
	 */
	public boolean isLate() {
		if (isDue() && !isPaid()) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return true if the charge due date has passed
	 */
	public boolean isDue() {
		return dueDate.isBefore(LocalDate.now());
	}
	
	/**
	 * Returns the remaining unpaid balance.
	 * @return Initial amount minus payments
	 */
	public double getRemainingBalance() {
		return balance;
	}
	
	/**
	 * Returns total amount paid so far.
	 * @return Initial amount minus remaining balance
	 */
	public double getAmountPaid() {
		return amount - balance;
	}
	
	/**
	 * @return true if the posting date passed
	 */
	public boolean isPosted() {
		if (!postingDate.isAfter(LocalDate.now())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Luhn algorithm to check credit card number
	 * @param n - card number
	 * @return true if number is a valid credit card number
	 */
	public boolean validatePaymentAccount(String n){
        if (n == null || n.isEmpty()) return false;
        boolean x = true;
        int sum = 0;
        int temp = 0;
        for (int i = n.length() - 1; i >= 0; i--) {
            temp = n.charAt(i) - '0';
            sum += (x = !x) ? temp > 4 ? temp * 2 - 9 : temp * 2 : temp;
        }
        return sum % 10 == 0;
    }
}

