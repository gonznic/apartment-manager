package user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A class representing a complaint made by a resident about their unit.
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Complaint {
	// General
	private User user;
	private LocalDate logDate;

	private String subject;
	private LocalDate date;
	private String description;
	
	// Status
	private String status;
	private boolean isOpen;
	
	/**
	 * Creates a Complaint object with the given parameters.
	 * The Complaint's log date will be today's date.
	 * The initial status is OPEN.
	 * @param subject
	 * @param date
	 * @param description
	 */
	public Complaint(String subject, LocalDate date, String description) {
		this.user = null;
		this.setLogDate(LocalDate.now());
		
		this.setSubject(subject);
		this.setDate(date);
		this.setDescription(description);
		
		this.setStatus("OPEN");
		this.isOpen = true;
	}
	
	/**
	 * @param user - User to link to this complaint
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return user who made the complaint
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Returns the date when the complaint was created.
	 * @return Log date
	 */
	public LocalDate getLogDate() {
		return logDate;
	}

	/**
	 * Sets the log date (To be used by the data generator).
	 * @param logDate - Date to set as the log date
	 */
	public void setLogDate(LocalDate logDate) {
		this.logDate = logDate;
	}
	
	/**
	 * @return Subject of the complaint
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * @param subject - Complaint's new subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Returns date of incident.
	 * @return date - The date associated with the complaint.
	 */
	public LocalDate getDate() {
		return date;
	}
	
	/**
	 * Sets the complaint event date.
	 * @param date - Date the complaint even happened
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description - Complaint's new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	 /**
	  * Returns status of the complaint.
	  * @return status - OPEN or CLOSED
	  */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status of the complaint.
	 * @param status - The status to set: OPEN or CLOSED
	 */
	public void setStatus(String status) {
		if (status.equalsIgnoreCase("CLOSED")) {
			isOpen = false;
		}
		if (status.equalsIgnoreCase("OPEN")) {
			isOpen = true;
		}
		this.status = status;
	}
	
	/** 
	 * @return true if the complaint is open
	 */
	public boolean isOpen() {
		return isOpen;
	}
	
	/**
	 * Sorts an ArrayList of complaints by log date.
	 * @param complaints - The complaint list to sort
	 */
	public static void sortByLogDate(ArrayList<Complaint> complaints) {
		complaints.sort(new Comparator<Complaint>() {
			@Override
			public int compare(Complaint o1, Complaint o2) {
				if (o1.getLogDate().isBefore(o2.getLogDate())) return 1;
				if (o1.getLogDate().isAfter(o2.getLogDate())) return -1;
				return 0;
			}
		});
	}
}

