package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import user.Complaint;
import user.Resident;
import user.User;
import user.UserList;

/**
 * A class that creates a Complaints page consisting of a panel with a log of complaints and 
 * another panel for creating a New Complaint.
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class ComplaintsPage extends JPanel implements Page{

	// General
	private boolean isAdmin;
	private UserList users;
	private User user;
	private String title = "Complaints";

	// Panels
	private JPanel complaintLogPanel;
	private JPanel addComplaintPanel;
	
	// Table
	private JPanel complaintTable;
	private JPanel logPanelContent;
	private JScrollPane logScroll;
	
	// Add complaint
	private JTextField subject;
	private JTextField date;
	private JTextArea description;
	private JCheckBox boxTerms;
	private JCheckBox boxBeContacted;
	
	private JLabel invalidFormMessage; //BUGS: New Log submission messages
	private JLabel validFormMessage;
	
	//Minimize
	private JPanel addComplaintContent;
	
	// Colors
	private Color backgroundColor = Theme.baseColor;
	private Color paneColor = Theme.paneColor;
	private Color paneLightColor = Theme.paneLightColor;
	private Color textColor = Theme.textSecondaryColor;
	private Color textColorTitle = Theme.textPrimaryColor;
	private Color fieldColor = Theme.fieldColor; 
	private Color fieldText = Theme.fieldTextColor;
	private Color redColor = Theme.redColor;
	private Color greenColor = Theme.greenColor;
	private Color pressedColor = Theme.pressedColor;
	
	/**
	 * Creates a complaints page. The complaints displayed on the log will only be 
	 * the complaints made by the input user.
	 * @param user - User who's complaints to display.
	 */
	public ComplaintsPage(User user) {
		this(user, null);
	}
	
	/**
	 * Creates a complaints page. The complaints displayed on the log will only be 
	 * the complaints made by every user in the user list. (Meant for admin account only)
	 * @param user - Current user
	 * @param users - List of all user accounts
	 */
	public ComplaintsPage(User user, UserList users) {
		
		// Permissions
		isAdmin = (users == null) ? false : true;
		if (user instanceof Resident) {
			user = (Resident)user;
			isAdmin = false;
		}

		// Frame Setup
		if (isAdmin) this.users = users;
		this.user = user;
		this.setLayout(new BorderLayout());
		this.setBackground(backgroundColor);
		
		// Title
		JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
		titleLabel.setForeground(textColorTitle);
		this.add(titleLabel, BorderLayout.NORTH);
		
		// Content Container
		JPanel tabContent = new JPanel();
		tabContent.setLayout(new GridBagLayout());
		tabContent.setOpaque(false);
		this.add(tabContent, BorderLayout.CENTER);
		
		/*
		 * Panel Setup
		 */
        
        GridBagConstraints c = new GridBagConstraints();
        
        // Complaint Log
        complaintLogPanel = buildLogPanel(buildPanel("Complaint Log"));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(paneMargin,0,0,0);
        tabContent.add(complaintLogPanel, c);
        
        // Initialize New Complaint Content (to minimize)
        addComplaintContent = new JPanel();
        addComplaintContent.setVisible(false);
        
        // New Complaint (collapsible)
        addComplaintPanel = buildAddComplaint(buildPanel("New Complaint", true, addComplaintContent));
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(paneMargin,0,0,0);
        tabContent.add(addComplaintPanel, c);
	}
	
	/*
	 * Log Panel Methods
	 */
	
	/**
	 * Fills and returns a JPanel with a log table.
	 * @param panel - Blank theme panel
	 * @return A panel with a complaints log
	 */
	private JPanel buildLogPanel(JPanel panel) {
		logPanelContent = new JPanel();
		logPanelContent.setLayout(new GridBagLayout());
		logPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		logPanelContent.setOpaque(true);
		
	    buildComplaintTable(logPanelContent); // Add content

		panel.add(logPanelContent);
		return panel;
	}
	
	/**
	 * Fills the input panel with a complaint table.
	 * Creates a scroll table with complaint details.
	 * @param panel - Panel to build the complaint table on
	 */
	private void buildComplaintTable(JPanel panel) {
		panel.setBackground(paneColor);
		
		//Container
		complaintTable = new JPanel();
	    complaintTable.setLayout(new GridBagLayout());
	    complaintTable.setOpaque(true);
	    complaintTable.setBackground(paneColor);
	    complaintTable.setAlignmentX(Component.LEFT_ALIGNMENT);
	    GridBagConstraints c = new GridBagConstraints();
	    
	    // Left space
	    JPanel leftSpacer = new JPanel();
	    leftSpacer.setOpaque(false);
    	c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        complaintTable.add(leftSpacer, c);
        
        // Right Space
	    JPanel rightSpacer = new JPanel();
	    rightSpacer.setOpaque(false);
    	c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.0;
        c.gridx = 9;
        c.gridy = 0;
        complaintTable.add(rightSpacer, c);
	    
	    /*
	     * Table Header
	     */
	    
        // Log Date
	    JLabel columnSize0 = new JLabel("Log Date", SwingConstants.LEFT);
		columnSize0.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize0.setForeground(textColor);
		columnSize0.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		complaintTable.add(columnSize0, c);
		
		//EventDate
		JLabel columnSize1 = new JLabel("Event Date", SwingConstants.LEFT);
		columnSize1.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize1.setForeground(textColor);
		columnSize1.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
        c.gridx = 2;
        c.gridy = 0;
        complaintTable.add(columnSize1, c);
		
        // Status
		JLabel columnSize2 = new JLabel("Status", SwingConstants.LEFT);
		columnSize2.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize2.setForeground(textColor);
		columnSize2.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.2;
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 2;
        complaintTable.add(columnSize2, c);
		
        // Subject
		JLabel columnSize3 = new JLabel("Subject", SwingConstants.LEFT);
		columnSize3.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize3.setForeground(textColor);
		columnSize3.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.gridwidth = 2;
		c.weightx = 0.5;
        c.gridx = 5;
        c.gridy = 0;
        complaintTable.add(columnSize3, c);
		
        // Unit
		JLabel columnSize4 = new JLabel("Unit", SwingConstants.LEFT);
		columnSize4.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize4.setForeground(textColor);
		columnSize4.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.gridwidth = 2;
        c.gridx = 7;
        c.gridy = 0;
        c.weightx = 0.1;
        complaintTable.add(columnSize4, c);
        
        // Line
        JPanel headerLine = new JPanel();
        headerLine.setBackground(textColor);
        headerLine.setPreferredSize(new Dimension(100, 1));
        c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 8;
        c.gridx = 1;
        c.gridy = 1;
        complaintTable.add(headerLine, c);

        /*
         * Table Rows
         */

        // Load Data
	    ArrayList<Complaint> userComplaints = user.getComplaints();
	    if (isAdmin) userComplaints = users.getAllComplaints();
	    Complaint.sortByLogDate(userComplaints);
	    
	    // Loop
	    int currentRow = 2;
	    for (Complaint complaint : userComplaints) {
	    	
	    	// Log Date
	    	JLabel tableColumn0 = new JLabel(complaint.getLogDate().format(
	    			DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.LEFT);
	    	tableColumn0.setFont(new Font("Arial", Font.PLAIN, 14));
	    	tableColumn0.setForeground(textColorTitle);
	    	tableColumn0.setBorder(new EmptyBorder(0, 4, 0, 4));
	    	if (currentRow % 2 == 1) {
	    		tableColumn0.setOpaque(true);
	    		tableColumn0.setBackground(paneLightColor);
	    	}
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 1;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			complaintTable.add(tableColumn0, c);
	    	
			// Complaint Date
	    	JLabel tableColumn1 = new JLabel(complaint.getDate().format(
	    			DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.LEFT);
	    	tableColumn1.setFont(new Font("Arial", Font.PLAIN, 14));
			tableColumn1.setForeground(textColorTitle);
			tableColumn1.setBorder(new EmptyBorder(0, 4, 0, 4));
			if (currentRow % 2 == 1) {
				tableColumn1.setOpaque(true);
				tableColumn1.setBackground(paneLightColor);
	    	}
	        c.gridx = 2;
	        c.gridy = currentRow;
			complaintTable.add(tableColumn1, c);
			
			/*
        	 * Account Status
        	 */
        	
        	// Container
			JPanel tableColumn = new JPanel();
			tableColumn.setLayout(new GridBagLayout());
			tableColumn.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn.setOpaque(false);
			if (currentRow % 2 == 1) {
	    		tableColumn.setOpaque(true);
	    		tableColumn.setBackground(paneLightColor);
	    	}
			
			// Flag
			JLabel tableColumnFlag = new JLabel("CLOSED", SwingConstants.LEFT);
	    	tableColumnFlag.setFont(new Font("Arial", Font.BOLD, 11));
	    	tableColumnFlag.setForeground(Theme.flagText);
	    	tableColumnFlag.setBorder(new EmptyBorder(0, 4, 0, 4));
	    	tableColumnFlag.setBackground(greenColor);
	    	tableColumnFlag.setOpaque(true);
			if (complaint.getStatus().equals("CLOSED")) {
				tableColumnFlag.setText("CLOSED");
				tableColumnFlag.setBackground(textColor);
			}
			else if (complaint.getStatus().equals("OPEN")) {
				tableColumnFlag.setText("OPEN");
				tableColumnFlag.setBackground(greenColor);
			}
			c.fill = GridBagConstraints.NONE;
	        c.gridx = 0;
	        c.gridy = 0;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			tableColumn.add(tableColumnFlag);
			
			// Add container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 3;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			complaintTable.add(tableColumn, c);
			
			/*
			 * Close Button
			 */
			
			// Container
			JPanel closeButtonContainer = new JPanel();
			closeButtonContainer.setLayout(new GridBagLayout());
			closeButtonContainer.setOpaque(false);
			if (currentRow % 2 == 1) {
				closeButtonContainer.setOpaque(true);
				closeButtonContainer.setBackground(paneLightColor);
	    	}
			closeButtonContainer.setBorder(new EmptyBorder(0, 4, 0, 4));
			
			//Button
			CustomButton closeAction = new CustomButton("Close", Theme.buttonColor, 
					Theme.hoverColor, Theme.pressedColor);
			closeAction.setFont(new Font("Arial", Font.BOLD, 10));
			closeAction.setBorderPainted(false);
			closeAction.setForeground(textColorTitle);
			closeAction.setFocusPainted(false);
			closeAction.setBorder(new EmptyBorder(0, 0, 0, 0));
			closeAction.setPreferredSize(new Dimension(50,16));
			closeAction.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
						// Bring up pop up
						new PopupWindow(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								if (e.getActionCommand().equalsIgnoreCase("yes")) {
									complaint.setStatus("CLOSED"); // Close complaint
									refreshAll();
								}
								
							}
							
						},"Are you sure you want to close this complaint?", "yesno");
				}
	        });
			if (complaint.isOpen()) {
				closeButtonContainer.add(closeAction);
			}

			// Add container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 4;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			complaintTable.add(closeButtonContainer, c);
			
			/*
			 * Subject
			 */
		
			// Container
			JPanel tableColumn5Container = new JPanel();
			tableColumn5Container.setLayout(new GridBagLayout());
			tableColumn5Container.setAlignmentX(Component.LEFT_ALIGNMENT);			
			tableColumn5Container.setOpaque(false);
			if (currentRow % 2 == 1) {
				tableColumn5Container.setOpaque(true);
				tableColumn5Container.setBackground(paneLightColor);
	    	}
			
			// Subject
			JTextArea tableColumn5 = new JTextArea(1,1);
			tableColumn5.setText(complaint.getSubject());
			tableColumn5.setEditable(false);
			tableColumn5.setFont(new Font("Arial", Font.PLAIN, 14));
			tableColumn5.setForeground(textColorTitle);
			tableColumn5.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn5.setOpaque(true);
			tableColumn5.setBackground(paneColor);
			if (currentRow % 2 == 1) {
				tableColumn5.setOpaque(true);
				tableColumn5.setBackground(paneLightColor);
	    	}
			
			JScrollPane subjectDisplayScroll = new JScrollPane(tableColumn5);
			subjectDisplayScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			subjectDisplayScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			subjectDisplayScroll.setBorder(new EmptyBorder(0,0,0,0));
			subjectDisplayScroll.setOpaque(false);
			subjectDisplayScroll.setBackground(paneColor);
			if (currentRow % 2 == 1) {
				subjectDisplayScroll.setOpaque(true);
				subjectDisplayScroll.setBackground(paneLightColor);
	    	}
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 1;
	        c.gridy = 0;
	        c.weightx = 0.001;
			c.weighty = 0.0;
			c.gridwidth = 1;
			tableColumn5Container.add(subjectDisplayScroll, c);
			
			// Add container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 5;
	        c.gridy = currentRow;
	        c.weightx = 0.3;
			c.weighty = 0.0;
			c.gridwidth = 1;
			complaintTable.add(tableColumn5Container, c);
			
			/*
			 * Description Button
			 */
			
			// Button Container
			JPanel descriptionButtonContainer = new JPanel();
			descriptionButtonContainer.setLayout(new GridBagLayout());
			descriptionButtonContainer.setOpaque(false);
			if (currentRow % 2 == 1) {
				descriptionButtonContainer.setOpaque(true);
				descriptionButtonContainer.setBackground(paneLightColor);
	    	}
			descriptionButtonContainer.setBorder(new EmptyBorder(0, 4, 0, 4));
			
			// Description Panel
			JPanel descriptionPopupContainer = new JPanel();
			descriptionPopupContainer.setLayout(new GridBagLayout());
			descriptionPopupContainer.setPreferredSize(new Dimension(200,100));
			
			// Description Content
			JTextArea descriptionPopup = new JTextArea(2, 1);
			descriptionPopup.setFont(new Font("Arial", Font.PLAIN, 14));
			descriptionPopup.setBackground(fieldColor);
			descriptionPopup.setForeground(fieldText);
			descriptionPopup.setCaretColor(fieldText);
			descriptionPopup.setBorder(new EmptyBorder(4, 4, 4, 4));
			descriptionPopup.setMinimumSize(MIN_SIZE);
			descriptionPopup.setLineWrap(true);
			descriptionPopup.setText(complaint.getDescription());
			descriptionPopup.setEditable(false);
			
			JScrollPane descriptionEntryScroll = new JScrollPane(descriptionPopup);
			descriptionEntryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			descriptionEntryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			descriptionEntryScroll.setBorder(new EmptyBorder(0,0,0,0));
			descriptionEntryScroll.getVerticalScrollBar().setUnitIncrement(7);
			descriptionEntryScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
			descriptionEntryScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;
			c.weighty = 1;
	        c.gridx = 0;
	        c.gridy = 0;
	        descriptionPopupContainer.add(descriptionEntryScroll, c);
	
			//Button
			CustomButton descriptionAction = new CustomButton("Description", Theme.buttonColor, 
					Theme.hoverColor, Theme.pressedColor);
			descriptionAction.setFont(new Font("Arial", Font.BOLD, 10));
			descriptionAction.setBorderPainted(false);
			descriptionAction.setForeground(textColorTitle);
			descriptionAction.setFocusPainted(false);
			descriptionAction.setBorder(new EmptyBorder(0, 0, 0, 0));
			descriptionAction.setPreferredSize(new Dimension(70,16));
			descriptionAction.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
						// Bring up pop up
						new PopupWindow(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {

							}
							
						}, descriptionPopupContainer, "ok");
				}	
	        });
			descriptionButtonContainer.add(descriptionAction);
			
			// Add container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 6;
	        c.gridy = currentRow;
	        c.weightx = 0.01;
			c.weighty = 0.0;
			c.gridwidth = 1;
			complaintTable.add(descriptionButtonContainer, c);
			
			// Latest unit of complaint's user
			JPanel tableColumn7 = buildTwoNameDisplay("-", "-");
			tableColumn7.setBorder(new EmptyBorder(4, 4, 4, 4));
			if (complaint.getUser() instanceof Resident) {
				tableColumn7 = buildTwoNameDisplay(
						((Resident)complaint.getUser()).getLatestUnitHistoryString()[1],
						((Resident)complaint.getUser()).getLatestUnitHistoryString()[0]);
			}
	    	if (currentRow % 2 == 1) {
	    		tableColumn7.setOpaque(true);
	    		tableColumn7.setBackground(paneLightColor);
	    	}
			c.fill = GridBagConstraints.HORIZONTAL;
	        c.gridx = 7;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			complaintTable.add(tableColumn7, c);
				
			//Contact Button
			if (complaint.getUser() instanceof Resident) {
				// Container
				JPanel tableColumn8 = new JPanel();
				tableColumn8.setLayout(new GridBagLayout());
				tableColumn8.setOpaque(false);
				if (currentRow % 2 == 1) {
					tableColumn8.setOpaque(true);
					tableColumn8.setBackground(paneLightColor);
		    	}
				tableColumn8.setBorder(new EmptyBorder(0, 4, 0, 4));
				
				//Button
				CustomButton contactAction = new CustomButton("Contact", Theme.buttonColor, 
						Theme.hoverColor, Theme.pressedColor);
				contactAction.setFont(new Font("Arial", Font.BOLD, 10));
				contactAction.setBorderPainted(false);
				contactAction.setForeground(textColorTitle);
				contactAction.setFocusPainted(false);
				contactAction.setBorder(new EmptyBorder(0, 0, 0, 0));
				contactAction.setPreferredSize(new Dimension(50,16));
				contactAction.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
							// Bring up pop up
							new PopupWindow(buildAccountContactDisplay(complaint.getUser()), "ok");
					}
					
		        });
				tableColumn8.add(contactAction);
				
				// Add container
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 8;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				complaintTable.add(tableColumn8, c);
			}
		    currentRow++;
	    }
	    
	    // Push table to top
    	JPanel tableBottomSpacer = new JPanel();
    	tableBottomSpacer.setOpaque(false);
        c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = currentRow;
        complaintTable.add(tableBottomSpacer, c);
	    
	    // Table Scroll
	    logScroll = new JScrollPane(complaintTable);
	    logScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    logScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    logScroll.setBorder(new EmptyBorder(0,0,0,0));   
	    logScroll.getVerticalScrollBar().setUnitIncrement(10);
	    logScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
	    logScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
	    c.insets = new Insets(4,0,0,0);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		panel.add(logScroll, c);
	}
	
	/**
	 * Fills and returns a JPanel with the entered user information
	 * @param user - User who's contact  information will be displayed
	 * @return A JPanel with the entered user's contact information.
	 */
	private JPanel buildAccountContactDisplay(User user) {
		// Container
		JPanel accountContactDisplay = new JPanel();
		accountContactDisplay.setOpaque(false);
		accountContactDisplay.setLayout(new GridBagLayout());
		accountContactDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints c = new GridBagConstraints();
		
		// Name
		JLabel nameDisplay = new JLabel(user.getName(), SwingConstants.CENTER);
		nameDisplay.setFont(new Font("Arial", Font.PLAIN, 25));
		nameDisplay.setForeground(textColorTitle);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weightx = 0.1;
        c.anchor = GridBagConstraints.WEST;
        accountContactDisplay.add(nameDisplay, c);
		
		// User name
		JLabel usernameDisplay = new JLabel(user.getUsername(), SwingConstants.CENTER);
		usernameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		usernameDisplay.setForeground(textColor);
        c.gridy = 1;
        accountContactDisplay.add(usernameDisplay, c);
        
        // Space
        JPanel contactSpace = new JPanel();
        contactSpace.setOpaque(false);
        c.gridy = 2;
        c.weighty = 0.2;
        accountContactDisplay.add(contactSpace, c);
        
        // Email
        JLabel emailDisplay = new JLabel(user.getEmail(), SwingConstants.CENTER);
        emailDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
        emailDisplay.setForeground(textColorTitle);
        c.weighty = 0.0;
        c.gridy = 3;
        accountContactDisplay.add(emailDisplay, c);
        
        // Phone number
        JLabel phoneDisplay = new JLabel(user.getPhoneString(), SwingConstants.CENTER);
        phoneDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
        phoneDisplay.setForeground(textColorTitle);
        c.gridy = 4;
        accountContactDisplay.add(phoneDisplay, c);
        
		return accountContactDisplay;			
	}
	
	/*
	 * Add Complaint Panel
	 */
	
	/**
	 * Adds components to add a new complaint including text fields and a submit button.
	 * @param panel - Theme panel to build the New Complaint panel content
	 * @return A JPanel with content to create a new complaint
	 */
	private JPanel buildAddComplaint(JPanel panel) {
		addComplaintContent.setLayout(new GridBagLayout());
		addComplaintContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		addComplaintContent.setOpaque(true);
		addComplaintContent.setBackground(paneColor);
		
		GridBagConstraints c = new GridBagConstraints();
		Dimension textFieldSize = new Dimension(300, 1);
	
		// Subject label
		JLabel subjectLabel = new JLabel("Subject", SwingConstants.LEFT);
        subjectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		subjectLabel.setForeground(textColor);
		subjectLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(fieldMargin,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
		addComplaintContent.add(subjectLabel, c);
		
		//Subject text field
		subject = new JTextField();
		subject.setFont(new Font("Arial", Font.PLAIN, 14));
		subject.setBackground(fieldColor);
		subject.setForeground(fieldText);
		subject.setCaretColor(fieldText);
		subject.setBorder(new EmptyBorder(0, 4, 0, 4));
		subject.setMinimumSize(textFieldSize);	// Sets column size
		subject.setPreferredSize(textFieldSize);
		c.insets = new Insets(fieldMargin,fieldMargin,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 1;
		addComplaintContent.add(subject, c);
		
		// Date Label
		JLabel dateLabel = new JLabel("Event Date", SwingConstants.LEFT);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		dateLabel.setForeground(textColor);
		dateLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 2;
		addComplaintContent.add(dateLabel, c);
		
		// Date text field
		date = new JTextField(defaultDateText);
		date.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		date.setFont(new Font("Arial", Font.PLAIN, 14));
		date.setBackground(fieldColor);
		date.setForeground(textColor);
		date.setText(defaultDateText);
		date.setBorder(new EmptyBorder(0, 4, 0, 4));
		date.setMinimumSize(new Dimension(85, 1));
		date.setCaretColor(fieldText);
		date.addFocusListener(new FocusListener() {
		    public void focusGained(FocusEvent e) {
		    	if (date.getText().equals(defaultDateText)) {
		    		date.setText("");
		    		date.setForeground(fieldText);
		    	}   
		    }
		    public void focusLost(FocusEvent e) {
		        if (date.getText().isEmpty()) {
		        	date.setText(defaultDateText);
		        	date.setForeground(textColor);
		        }  
		    }
		});
		c.insets = new Insets(0,fieldMargin,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.WEST;
		addComplaintContent.add(date, c);
		c.anchor = GridBagConstraints.CENTER;
		
		// Description Label
		JLabel descriptionLabel = new JLabel("Description", SwingConstants.LEFT);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		descriptionLabel.setForeground(textColor);
		descriptionLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 3;
		addComplaintContent.add(descriptionLabel, c);
			
		//Description text box
		description = new JTextArea(2, 1);
		description.setFont(new Font("Arial", Font.PLAIN, 14));
		description.setBackground(fieldColor);
		description.setForeground(fieldText);
		description.setCaretColor(fieldText);
		description.setBorder(new EmptyBorder(4, 4, 4, 4));
		description.setLineWrap(true);
		
		JScrollPane descriptionEntryScroll = new JScrollPane(description);
		descriptionEntryScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		descriptionEntryScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		descriptionEntryScroll.setBorder(new EmptyBorder(0,0,0,0));
		descriptionEntryScroll.getVerticalScrollBar().setUnitIncrement(7);
		descriptionEntryScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
		descriptionEntryScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		c.insets = new Insets(0,fieldMargin,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 3;
		addComplaintContent.add(descriptionEntryScroll, c);
		
 		/*
 		 * Check Boxes
 		 */
		
		// Container
 		JPanel complaintCheckBoxes = new JPanel();
 		complaintCheckBoxes.setLayout(new GridBagLayout());
 		complaintCheckBoxes.setOpaque(false);
 		
 		// Be Contacted
 		boxBeContacted = new JCheckBox("I acknowledge that I may be contacted regarding this submission");
 		boxBeContacted.setOpaque(false);
 		boxBeContacted.setIcon(new checkBoxIcon(fieldColor));
 		boxBeContacted.setPressedIcon(new checkBoxIcon(pressedColor));
 		boxBeContacted.setSelectedIcon(new checkBoxIcon(fieldColor, "\u2713"));
 		boxBeContacted.setFont(new Font("Arial", Font.PLAIN, 14));
 		boxBeContacted.setForeground(textColor);
 		boxBeContacted.setBorderPainted(false);
 		boxBeContacted.setMargin(new Insets(0,-2,0,0));
 		c.ipady = 0;
 		c.insets = new Insets(0,0,0,0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
     	c.gridwidth = 1;
        c.weightx = 0.01;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        complaintCheckBoxes.add(boxBeContacted, c);
        
        // Privacy Policy
        boxTerms = new JCheckBox("I certify that I have read and accept the Privacy Policy");
        boxTerms.setOpaque(false);
        boxTerms.setIcon(new checkBoxIcon(fieldColor));
 		boxTerms.setPressedIcon(new checkBoxIcon(pressedColor));
 		boxTerms.setSelectedIcon(new checkBoxIcon(fieldColor, "\u2713"));
 		boxTerms.setFont(new Font("Arial", Font.PLAIN, 14));
 		boxTerms.setForeground(textColor);
 		boxTerms.setMargin(new Insets(0,-2,0,0));
 		c.insets = new Insets(0,0,0,0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
     	c.gridwidth = 1;
        c.weightx = 0.01;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        complaintCheckBoxes.add(boxTerms, c);
 		
 		// Add Check Boxes container
        c.ipady = 4;
        c.insets = new Insets(0,fieldMargin,fieldMargin,fieldMargin);
 		c.fill = GridBagConstraints.HORIZONTAL;
     	c.gridwidth = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 1;
        c.gridy = 4;
		addComplaintContent.add(complaintCheckBoxes, c);
 		
 		/*
 		 * Submit Complaint
 		 */
		
 		// Submit Complaint Container
 		JPanel submitComplaint = new JPanel();
 		submitComplaint.setLayout(new GridBagLayout());
 		submitComplaint.setOpaque(false);
 		
 		// Submit Complaint Button
		CustomButton submitComplaintButton = new CustomButton("Submit Complaint", Theme.buttonColor, 
				Theme.hoverColor, Theme.pressedColor);
        submitComplaintButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitComplaintButton.setBorderPainted(false);
        submitComplaintButton.setForeground(textColorTitle);
        submitComplaintButton.setFocusPainted(false);
        submitComplaintButton.setMargin(new Insets(-2,4,-2,4));
        submitComplaintButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Submit Complaint")) {
					if (validateComplaintEntry()) {
						LocalDate dateEntry = validateDateEntry(date.getText());
						if (dateEntry != null) {
							
							// Fields validated
							new PopupWindow(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									if (e.getActionCommand().equals("Yes")) {
						    				user.addComplaint(new Complaint(subject.getText(), 
						    						dateEntry, description.getText()));
						    				clearComplaintEntry();
						    				refreshAll();
						    			}
						        	if (e.getActionCommand().equals("No")) {
						        		validFormMessage.setVisible(false);
						        	} 
								}
								
							}, "Are you sure you want to submit this complaint?", "yesno");
						}
					}
					else invalidFormMessage.setVisible(true);		
				}
				
			}
		});
        c.insets = new Insets(0,0,0,0);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
     	c.gridwidth = 1;
        c.weightx = 0.01;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        submitComplaint.add(submitComplaintButton, c);
        
        // Invalid message
        invalidFormMessage = new JLabel("Invalid submission. All fields must be filled.", SwingConstants.LEFT);
        invalidFormMessage.setFont(new Font("Arial", Font.PLAIN, 14));
		invalidFormMessage.setForeground(redColor);
		invalidFormMessage.setBorder(new EmptyBorder(0, 6, 0, 0));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 0;
        submitComplaint.add(invalidFormMessage, c);
        invalidFormMessage.setVisible(false);
        
        // Valid form message //TODO remove
        validFormMessage = new JLabel("Complaint Submitted", SwingConstants.LEFT);
        validFormMessage.setFont(new Font("Arial", Font.PLAIN, 14));
		validFormMessage.setForeground(greenColor);
		validFormMessage.setBorder(new EmptyBorder(0, 6, 0, 0));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 0;
        submitComplaint.add(validFormMessage, c);
        validFormMessage.setVisible(false);
		
        //Add Container
        c.ipady = 4;
        c.insets = new Insets(0,fieldMargin,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
     	c.gridwidth = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 1;
        c.gridy = 5;
		addComplaintContent.add(submitComplaint, c);
			
		panel.add(addComplaintContent);
		return panel;
	}
	
	/**
	 * Checks that the new complaint entry fields are filled, that the date is filled correctly, 
	 * and that the check boxes are selected.
	 * @return true if the new complaint fields are filled correctly.
	 */
	private boolean validateComplaintEntry() {
		if (!(subject.getText().isBlank() || description.getText().isBlank()) 
				&& date.getText().length() == 10 && (validateDateEntry(date.getText()) != null)
				&& boxBeContacted.isSelected() && boxTerms.isSelected()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Sets all New Complaint text fields to their blank default
	 */
	private void clearComplaintEntry() {
		subject.setText("");
		date.setForeground(textColor);
		date.setText(defaultDateText);
		description.setText("");
	
		boxTerms.setSelected(false);
		boxBeContacted.setSelected(false);
		
		invalidFormMessage.setVisible(false);
		validFormMessage.setVisible(true);
	}
	
	/*
	 * Refresh Methods
	 */
	
	@Override
	public void refreshAll() {
		refreshTable(logPanelContent, logScroll);
	}
	
	/**
	 * Removes the complaint table from the container and rebuilds it.
	 * @param panel - Content container
	 * @param table - Complaint table
	 */
	private void refreshTable(JPanel panel, Component table) {
		panel.remove(table);
		buildComplaintTable(panel);
		panel.validate();
	}	
}

