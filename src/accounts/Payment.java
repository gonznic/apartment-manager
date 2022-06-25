package accounts;

import java.time.LocalDate;

/**
 * A class that represents a payment.
 * To be used with the Charge.java.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Payment {
	private static int confirmNumberCount = 100100; // Base reference number
	private int confirmNumber;
	
	private LocalDate date;
	private double amount;
	private int paymentAccount;
	private String note;
	
	/**
	 * Creates a payment with the given date, amount, and credit card number.
	 * @param date
	 * @param amount
	 * @param paymentAccount
	 * @param note
	 */
	public Payment(LocalDate date, double amount, int paymentAccount, String note) {
		confirmNumber = confirmNumberCount++;
		this.date = date;
		this.amount = amount;
		this.paymentAccount = paymentAccount;
		this.setNote(note);
	}
	
	/**
	 * @return Confirmation number
	 */
	public int getConfirmNumber() {
		return confirmNumber;
	}
	
	/**
	 * Sets the date of the payment. (Demo purposes only)
	 * @param date
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * @return Date of payment
	 */
	public LocalDate getDate() {
		return date;
	}
	
	/**
	 * @return Payment amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @return Payment account number
	 */
	public int getPaymentAccount() {
		return paymentAccount;
	}

	/** 
	 * @return note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note
	 */
	public void setNote(String note) {
		this.note = note;
	}

}
