package main.java.ui;

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
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.java.Page;
import main.java.Session;

/**
 * A class that creates an Resident Dashboard page consisting of 
 * three panels: Balance, Current Lease, and Charges.
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class ResidentDashboardPage extends JPanel implements Page{
	
	// Data
	private Session currentSession;
	Object[] userInfo = new Object[8];
	ArrayList<Object[]> userActivity = new ArrayList<Object[]>();
	
	//Setup
	private String title = "Dashboard";

	// Panels
	private JPanel balancePanel;
	private JPanel paymentsPanel;
	private JPanel unitInfoPanel;
	
	// Pop-up
	private PopupWindow submitPaymentPopup;
	
	// Refresh
	private JScrollPane paymentsScroll;
	
	// Refresh
	private JPanel balancePanelContent;
	private JPanel allResidentsPanelContent;
	
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
	 * Creates a Resident Dashboard page with information about 
	 * a user's lease and rent payments.
	 * @param session - Current session
	 */
	public ResidentDashboardPage(Session session) {
		
		// Data
		this.currentSession = session;

		// Setup
		this.setLayout(new BorderLayout());
		this.setBackground(backgroundColor);
		
		// Title
		JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 23));
		titleLabel.setForeground(textColorTitle);
		this.add(titleLabel, BorderLayout.NORTH);
		
		// Content container
		JPanel tabContent = new JPanel();
		tabContent.setLayout(new GridBagLayout());
		tabContent.setOpaque(false);
		this.add(tabContent, BorderLayout.CENTER);
		
		/*
		 * Panel Setup
		 */
		
        GridBagConstraints c = new GridBagConstraints();
        
        // Balance 
        balancePanel = buildBalancePanel(buildPanel("Balance"));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(PANE_MARGIN,0,0,0);
        tabContent.add(balancePanel, c);
        
        // Unit
        unitInfoPanel = buildUnitInfoPanel(buildPanel("Unit"));
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(PANE_MARGIN,PANE_MARGIN,0,0);
        tabContent.add(unitInfoPanel, c);
        
        // Payments
        paymentsPanel = buildPaymentsPanel(buildPanel("Activity"));
        c.weightx = 1.0;
        c.weighty = 0.5;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(PANE_MARGIN,0,0,0);
        tabContent.add(paymentsPanel, c);
	}
	
	/*
	 * Balance Panel
	 */
	
	/**
	 * Fills a panel that displays the tenant user's total outstanding charge balance.
	 * @param panel - Empty theme panel
	 * @return a panel with the user's balance
	 */
	private JPanel buildBalancePanel(JPanel panel) {
		// Container
		balancePanelContent = new JPanel();
		balancePanelContent.setLayout(new GridBagLayout());
		balancePanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		balancePanelContent.setOpaque(true);
		balancePanelContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
		
		/*
		 * Get user data
		 */
		
		// Prepare query
		String sqlQuery = """
				SELECT * FROM (
				SELECT `uid`,`user`,`start`,`recent_lease_end`,`unit`,`building`, ROUND(`sum_charges` - `sum_payments`, 2) AS balance
				FROM (SELECT * FROM `users` WHERE users.type = 'general' AND deleted = 0 """ + " AND uid = " + currentSession.getUserId() + ") AS u " +
				
				    """	
				    LEFT JOIN (SELECT `start`, MAX(`end`) AS 'recent_lease_end', `unitid`, `userid` FROM `leases` GROUP BY `userid`) AS l
				    ON u.uid = l.userid
				    
				    LEFT JOIN (SELECT `id`,`unit`,`building` FROM `units`) AS u2
				    ON l.unitid = u2.id
				    
				    LEFT JOIN (SELECT SUM(`charges`.`amount`) as sum_charges, charges.userid FROM charges WHERE charges.date <= CURRENT_DATE GROUP BY `userid`) as c
				    ON u.uid = c.userid
				    
				    LEFT JOIN (SELECT SUM(`payments`.`amount`) as sum_payments, payments.userid FROM payments GROUP BY `userid`) as p
				    ON u.uid = p.userid
				    
				) AS resident_manager 
				""";

		// Connect to database
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			Statement statement = connect.createStatement();
			
			// Query
			ResultSet result = statement.executeQuery(sqlQuery);
			while (result.next()) {
				Object[] currentUser = {
						result.getInt("uid"),
						result.getString("user"),
						(result.getDate("start")).toLocalDate(),
						(result.getDate("recent_lease_end")).toLocalDate(),
						result.getString("unit"),
						result.getString("building"),
						result.getDouble("balance")	
				};
				userInfo = currentUser;
			}

			// Close connection
			connect.close();
		} catch (SQLException err) {
			err.printStackTrace();
		}
		
		/*
		 * Frame
		 */
	
		double balanceHorPaddingWeight = 0.05;
		
		JPanel balanceLeftPadding = new JPanel();
		balanceLeftPadding.setOpaque(false);
		c.gridx = 0;
        c.gridy = 1;
        c.weightx = balanceHorPaddingWeight;
		c.weighty = 0;
		c.gridwidth = 1;
		balancePanelContent.add(balanceLeftPadding, c);
		
		JPanel balanceRightPadding = new JPanel();
		balanceRightPadding.setOpaque(false);
		c.gridx = 2;
		balancePanelContent.add(balanceRightPadding, c);
		
		/*
		 * Content
		 */

		// Balance amount
		JLabel balanceNumberDisplay = new JLabel(dollarFormat.format((double)userInfo[6]), SwingConstants.CENTER);
		balanceNumberDisplay.setFont(new Font("Arial", Font.PLAIN, 45));
		balanceNumberDisplay.setForeground(textColorTitle);
		balanceNumberDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 1;
        c.gridy = 1;
        balancePanelContent.add(balanceNumberDisplay, c);
		
        // Balance date
 		JLabel balanceDateNumberDisplay = new JLabel("Outstanding as of " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.CENTER);
 		balanceDateNumberDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
 		balanceDateNumberDisplay.setForeground(textColor);
 		balanceDateNumberDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
 		c.fill = GridBagConstraints.HORIZONTAL;
 		c.ipady = 0;
 		c.gridwidth = 1;
 		c.weightx = 0.0;
 		c.weighty = 0.0;
        c.gridx = 1;
        c.gridy = 2;
        balancePanelContent.add(balanceDateNumberDisplay, c);
        
        
        //Button
		CustomButton payChargeAction = new CustomButton("Pay", 
				Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
		payChargeAction.setPreferredSize(new Dimension(150, 50));
		payChargeAction.setMinimumSize(new Dimension(150, 50));
		payChargeAction.setFont(new Font("Arial", Font.BOLD, 30));
		payChargeAction.setBorderPainted(false);
		payChargeAction.setForeground(textColorTitle);
		payChargeAction.setFocusPainted(false);
		payChargeAction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				submitPaymentPopup = new PopupWindow(buildSubmitPayment(currentSession), "cancel");
				
			}
			
		});	
		c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(5,0,5,0);
		balancePanelContent.add(payChargeAction, c);
		
        /*
         * Add content to panel
         */
		
		panel.add(balancePanelContent);
		return panel;
	}
	
	/*
	 * Unit Info Panel
	 */

	/**
	 * Fills a panel with information about the user's most recent unit and lease.
	 * @param panel - Empty theme panel
	 * @return A panel with Unit info content
	 */
	private JPanel buildUnitInfoPanel(JPanel panel) {
		// Container
		JPanel panelContent = new JPanel();
		panelContent.setLayout(new GridBagLayout());
		panelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContent.setOpaque(true);
		panelContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
		
		/*
		 * Unit
		 */
		
		// Unit number
		JLabel unitNumberDisplay = new JLabel(((String)userInfo[4]), SwingConstants.CENTER);
        unitNumberDisplay.setFont(new Font("Arial", Font.PLAIN, 50));
		unitNumberDisplay.setForeground(textColorTitle);
		unitNumberDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		//c.insets = new Insets(fieldMargin,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 3;
		c.weightx = 0.0;
		c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 1;
		panelContent.add(unitNumberDisplay, c);
		
		// Building
		JLabel buildingNameDisplay = new JLabel(((String)userInfo[5]), SwingConstants.CENTER);
		buildingNameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		buildingNameDisplay.setForeground(textColor);
		buildingNameDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 3;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 2;
        panelContent.add(buildingNameDisplay, c);
        
		// Line Container
        JPanel unitLineContainer = new JPanel();
        unitLineContainer.setLayout(new GridBagLayout());
        unitLineContainer.setOpaque(false);
        
        // Line
		JPanel unitLine = new JPanel();
		unitLine.setBackground(textColor);
		unitLine.setPreferredSize(MIN_SIZE);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        unitLineContainer.add(unitLine, c);
		
        // Add container
        c.fill = GridBagConstraints.BOTH;
		c.ipady = 20;
		c.gridwidth = 3;
		c.weightx = 0.0;
		c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 3;
        panelContent.add(unitLineContainer, c);

        /*
		 * Lease
		 */
        
        // Lease Label
        JLabel leaseLabel = new JLabel("Lease", SwingConstants.LEFT);
        leaseLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		leaseLabel.setForeground(textColor);
		leaseLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 3;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 4;
        panelContent.add(leaseLabel, c);
		
		// Lease start date
		JLabel leaseStartDisplay = new JLabel(
				((LocalDate)userInfo[2]).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
				SwingConstants.LEFT);
		leaseStartDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
		leaseStartDisplay.setForeground(textColorTitle);
		leaseStartDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		panelContent.add(leaseStartDisplay, c);
		
		// Space (" > ")
		JLabel leaseSpaceDisplay = new JLabel(">",SwingConstants.LEFT);
		leaseSpaceDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
		leaseSpaceDisplay.setForeground(textColorTitle);
		leaseSpaceDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		panelContent.add(leaseSpaceDisplay, c);
		
		// Lease End date
		JLabel leaseEndDisplay = new JLabel(
				((LocalDate)userInfo[3]).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
				SwingConstants.LEFT);
		leaseEndDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
		leaseEndDisplay.setForeground(textColorTitle);
		leaseEndDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
//		if(searchUserLease.isFinished()) {
//			if(((Resident)user).getStatus().equalsIgnoreCase("ACTIVE")) {
//				leaseEndDisplay.setForeground(redColor);
//			}
//		}
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 5;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		panelContent.add(leaseEndDisplay, c);
		
		// Lease Status Container
		JPanel leaseStatusDisplay = new JPanel();
		leaseStatusDisplay.setLayout(new GridBagLayout());
		leaseStatusDisplay.setOpaque(false);
		
		// Status Flag
		JLabel statusFlag = new JLabel("INACTIVE", SwingConstants.LEFT);
		statusFlag.setFont(new Font("Arial", Font.BOLD, 11));
		statusFlag.setForeground(Theme.flagText);
		statusFlag.setBorder(new EmptyBorder(0, 4, 0, 4));
		statusFlag.setBackground(textColor);
		statusFlag.setOpaque(true);
    	c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		// If resident not on time
		if (!((LocalDate)userInfo[3]).isBefore(LocalDate.now())) {
			statusFlag.setText("ACTIVE");
			statusFlag.setBackground(greenColor);
		}
		leaseStatusDisplay.add(statusFlag);
		
		// Add container
		c.insets = new Insets(FIELD_MARGIN,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.0;
		c.weighty = 0.1;
		c.gridwidth = 3;
		panelContent.add(leaseStatusDisplay, c);

		/*
         * Add content to panel
         */
		
		panel.add(panelContent);
		return panel;
	}

	/*
	 * All Charges Panel
	 */
	
	/**
	 * Builds and fills a panel with a table.
	 * @param panel - Empty theme panel
	 * @return Panel with a Charge/Payment information table
	 */
	private JPanel buildPaymentsPanel(JPanel panel) {
		allResidentsPanelContent = new JPanel();
		allResidentsPanelContent.setLayout(new GridBagLayout());
		allResidentsPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		allResidentsPanelContent.setOpaque(true);
		
		buildChargesTable(allResidentsPanelContent);
		
		panel.add(allResidentsPanelContent);
		return panel;
	}
	
	/**
	 * Builds a Charges table on the input panel.
	 * Creates a scroll table with charge details and options.
	 * @param panel - Panel to build the Charges table on
	 */
	private void buildChargesTable(JPanel panel) {
		// Container
		JPanel chargesTable = new JPanel();
	    chargesTable.setLayout(new GridBagLayout());
	    chargesTable.setOpaque(true);
	    chargesTable.setBackground(paneColor);
	    chargesTable.setAlignmentX(Component.LEFT_ALIGNMENT);
	    GridBagConstraints c = new GridBagConstraints();
	    
	    /*
	     * Table header
	     */
	    
	    // Month
	    JLabel columnHeader0 = new JLabel("", SwingConstants.LEFT);
		columnHeader0.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader0.setForeground(textColor);
		columnHeader0.setBorder(new EmptyBorder(0, 4, 0, 6));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.10;
		c.weighty = 0.0;
		c.gridwidth = 1;
		chargesTable.add(columnHeader0, c);
		
		// Date
		JLabel columnHeader1 = new JLabel("Date", SwingConstants.LEFT);
		columnHeader1.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader1.setForeground(textColor);
		columnHeader1.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.14;
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 1;
        chargesTable.add(columnHeader1, c);
		
        // Name
		JLabel columnHeader2 = new JLabel("Charge/Payment", SwingConstants.LEFT);
		columnHeader2.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader2.setForeground(textColor);
		columnHeader2.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.14;
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        chargesTable.add(columnHeader2, c);
        
        // Charge
        JLabel columnHeader3 = new JLabel("Charge", SwingConstants.RIGHT);
		columnHeader3.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader3.setForeground(textColor);
		columnHeader3.setPreferredSize(new Dimension(65, TABLE_CELL_HEIGHT));
		columnHeader3.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.weightx = 0.14;
		c.gridwidth = 1;
        c.gridx = 5;
        c.gridy = 0;
        chargesTable.add(columnHeader3, c);
        
        // Payment
        JLabel columnHeader4 = new JLabel("Payment", SwingConstants.RIGHT);
		columnHeader4.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader4.setForeground(textColor);
		columnHeader4.setPreferredSize(new Dimension(65, TABLE_CELL_HEIGHT));
		columnHeader4.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.gridwidth = 1;
        c.weightx = 0.14;
        c.gridx = 6;
	    c.gridy = 0;
	    chargesTable.add(columnHeader4, c);
	    
	    // Balance
        JLabel columnHeader5 = new JLabel("Balance", SwingConstants.RIGHT);
		columnHeader5.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader5.setForeground(textColor);
		columnHeader5.setPreferredSize(new Dimension(65, TABLE_CELL_HEIGHT));
		columnHeader5.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.gridwidth = 1;
        c.weightx = 0.14;
        c.gridx = 7;
	    c.gridy = 0;
	    chargesTable.add(columnHeader5, c);
		
        // Header underline
        JPanel headerLine = new JPanel();
        headerLine.setBackground(textColor);
        headerLine.setPreferredSize(new Dimension(100, 1));
        c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 7;
        c.gridx = 1;
        c.gridy = 1;
        chargesTable.add(headerLine, c);
     
        /*
         * Table Rows
         */
        
        // Prepare query
        String sqlQuery = """
        		(SELECT userid, 'charge' AS type, date, name, amount AS charge, 0 AS payment, id
				FROM charges
				WHERE userid = ? AND date <= CURRENT_DATE())
				UNION
				(SELECT userid, 'payment' AS type, date, name, 0 AS charge, amount AS payment, id
				FROM payments
				WHERE userid = ? AND date <= CURRENT_DATE())
				ORDER BY date DESC, charge
        		""";

        // Connect to database
        try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
        	PreparedStatement statement = connect.prepareStatement(sqlQuery);

        	// Query
        	statement.setInt(1, currentSession.getUserId());
        	statement.setInt(2, currentSession.getUserId());
        	ResultSet result = statement.executeQuery();
        	userActivity = sqlToArray(result);
        	statement.close();

        	// Close connection
        	connect.close();
        } catch (SQLException err) {
        	err.printStackTrace();
        }
        
        // Loop
        int currentRow = 2;
        Color rowColor = paneColor;
        Object[] previous = new Object[7];
		double currentBalance = ((double)userInfo[6]);
        for (Object[] activity : userActivity) {
        	
        	// If current month or year different from previous
        	if (previous[0] != null) {
        		if (((Date)activity[2]).toLocalDate().getMonthValue() != ((Date)previous[2]).toLocalDate().getMonthValue() 
        			|| ((Date)activity[2]).toLocalDate().getYear() != ((Date)previous[2]).toLocalDate().getYear()) {
        		
	        		JPanel buildingLine = new JPanel();
		        	buildingLine.setBackground(textColor);
		        	buildingLine.setPreferredSize(new Dimension(100, 1));
		            c.fill = GridBagConstraints.HORIZONTAL;
		    		c.weightx = 0.0;
		    		c.gridwidth = 7;
		            c.gridx = 1;
		            c.gridy = currentRow;
		            chargesTable.add(buildingLine, c);
		            currentRow++;
		            currentRow++;
		            
		            // Month
					JLabel tableColumn0 = new JLabel(((Date)activity[2]).toLocalDate().format(
							DateTimeFormatter.ofPattern("MMMM")), SwingConstants.LEFT);
					tableColumn0.setFont(new Font("Arial", Font.PLAIN, 25));
			    	tableColumn0.setForeground(textColor);
			    	tableColumn0.setBorder(new EmptyBorder(0, 4, 0, 4));
			    	tableColumn0.setOpaque(true);
			    	tableColumn0.setBackground(rowColor);
					c.fill = GridBagConstraints.BOTH;
			        c.gridx = 1;
			        c.gridy = currentRow;
			        c.weightx = 0.0;
					c.weighty = 0.0;
					c.gridwidth = 1;
					c.gridheight = 1;
					chargesTable.add(tableColumn0, c);
        		}
        		else {
    				JPanel tableColumn0 = new JPanel();
    				tableColumn0.setOpaque(false);
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
    				chargesTable.add(tableColumn0, c);
    			}
        	}
        	else {
        		 // Month
				JLabel tableColumn0 = new JLabel(((Date)activity[2]).toLocalDate().format(
						DateTimeFormatter.ofPattern("MMMM")), SwingConstants.LEFT);
				tableColumn0.setFont(new Font("Arial", Font.PLAIN, 25));
		    	tableColumn0.setForeground(textColor);
		    	tableColumn0.setBorder(new EmptyBorder(0, 4, 0, 4));
		    	tableColumn0.setOpaque(true);
		    	tableColumn0.setBackground(rowColor);
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 1;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				c.gridheight = 1;
				chargesTable.add(tableColumn0, c);
        	}
        	
        	// Move temp
        	previous = activity;
        
			// Date
			JLabel tableColumn1 = new JLabel(((Date)activity[2]).toLocalDate().format(
					DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.LEFT);
			tableColumn1.setFont(new Font("Arial", Font.PLAIN, 14));
	    	tableColumn1.setForeground(textColorTitle);
	    	tableColumn1.setBorder(new EmptyBorder(7, 4, 7, 4));
	    	tableColumn1.setOpaque(true);
	    	tableColumn1.setBackground(rowColor);
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 2;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = 1;
			chargesTable.add(tableColumn1, c);
			

			// Name
			JLabel tableColumn2 = new JLabel(((String)activity[3]), SwingConstants.LEFT);
			tableColumn2.setFont(new Font("Arial", Font.PLAIN, 14));
			tableColumn2.setForeground(textColorTitle);
			tableColumn2.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn2.setOpaque(true);
	    	tableColumn2.setBackground(rowColor);
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 3;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(tableColumn2, c);
			
			// More info button
			if (((String)activity[1]).equals("payment")) {
				JPanel moreInfoContainer = new JPanel();
				moreInfoContainer.setLayout(new GridBagLayout());
				moreInfoContainer.setBackground(rowColor);
				
				CustomButton moreInfoButton = new CustomButton("...", 
						Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
				moreInfoButton.setFont(new Font("Arial", Font.BOLD, 10));
				moreInfoButton.setForeground(textColorTitle);
				moreInfoButton.setFocusPainted(false);
				moreInfoButton.setBorder(new EmptyBorder(0, 4, 0, 4));
				moreInfoButton.setPreferredSize(new Dimension(20,16));
				moreInfoButton.addActionListener(new ActionListener() {
	
					@Override
					public void actionPerformed(ActionEvent e) {
						new PopupWindow(buildPaymentDetails((int)activity[6]), "ok");
						
					}
					
				});
				moreInfoContainer.add(moreInfoButton, c);
				
				// Add container
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 4;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				c.gridheight = 1;
				chargesTable.add(moreInfoContainer, c);
			}
			else {
				JPanel moreInfoContainer = new JPanel();
				moreInfoContainer.setLayout(new GridBagLayout());
				moreInfoContainer.setBackground(rowColor);
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 4;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				c.gridheight = 1;
				chargesTable.add(moreInfoContainer, c);
			}
			
			// Charges
			JLabel tableColumn3 = new JLabel(dollarFormat.format((double)activity[4]), SwingConstants.RIGHT);
			tableColumn3.setFont(new Font("Arial", Font.PLAIN, 14));
			tableColumn3.setForeground(textColor);
			if (((String)activity[1]).equals("charge")) tableColumn3.setForeground(redColor);
			tableColumn3.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn3.setOpaque(true);
	    	tableColumn3.setBackground(rowColor);
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 5;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(tableColumn3, c);
			
			// Payments
			JLabel tableColumn4 = new JLabel(dollarFormat.format((double)activity[5]), SwingConstants.RIGHT);
			tableColumn4.setFont(new Font("Arial", Font.PLAIN, 14));
			tableColumn4.setForeground(textColor);
			if (((String)activity[1]).equals("payment")) tableColumn4.setForeground(greenColor);
			tableColumn4.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn4.setOpaque(true);
	    	tableColumn4.setBackground(rowColor);
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 6;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(tableColumn4, c);
			
			// Balance
			JLabel tableColumn5 = new JLabel(dollarFormat.format(currentBalance), SwingConstants.RIGHT);
			if (currentBalance <= 0.01) tableColumn5.setText(dollarFormat.format(0.0));
			tableColumn5.setFont(new Font("Arial", Font.PLAIN, 14));
			tableColumn5.setForeground(textColorTitle);
			tableColumn5.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn5.setOpaque(true);
	    	tableColumn5.setBackground(rowColor);
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 7;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(tableColumn5, c);
			
			currentBalance += (double)activity[5];
			currentBalance -= (double)activity[4];
				
			currentRow++;
			
			// Alternate row colors
			if (rowColor == paneLightColor) rowColor = paneColor;
        	else rowColor = paneLightColor;	
        }
        
        // Push charges table up
    	JPanel tableBottomSpacer = new JPanel();
    	tableBottomSpacer.setOpaque(false);
        c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = currentRow;
        chargesTable.add(tableBottomSpacer, c);
      
        // Charges table scroll
	    paymentsScroll = new JScrollPane(chargesTable);
	    paymentsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    paymentsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    paymentsScroll.setBorder(new EmptyBorder(0,0,0,0));
	    paymentsScroll.getVerticalScrollBar().setUnitIncrement(10);
	    paymentsScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
	    paymentsScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		panel.add(paymentsScroll, c);
	}
	
	/*
	 * Pop-up Content
	 */
	
	/**
	 * Builds a panel that displays the entered charge's information and a payment submission options.
	 * @param charge - Source of data to display
	 * @return A panel with information about the entered charge and an option to make a payment.
	 */
	private JPanel buildSubmitPayment(Session session) {
		// Container
		JPanel submitPaymentPanel = new JPanel();
		submitPaymentPanel.setLayout(new GridBagLayout());
		submitPaymentPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		int currentRow = 0;
		
		/*
		 * Charge
		 */
		
		// Charge title
		JLabel chargeTitle = new JLabel("Make Payment", SwingConstants.CENTER);
		chargeTitle.setFont(new Font("Arial", Font.PLAIN, 22));
		chargeTitle.setForeground(textColorTitle);
		chargeTitle.setBorder(new EmptyBorder(10, 4, 10, 4));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.5;
		c.gridwidth = 3;
		submitPaymentPanel.add(chargeTitle, c);
		
		// Total charge Label
		currentRow++;
		JLabel totalChargeLabel = new JLabel("Outstanding", SwingConstants.LEFT);
		totalChargeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		totalChargeLabel.setForeground(textColor);
		totalChargeLabel.setBorder(new EmptyBorder(0, 4, 10, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalChargeLabel, c);
		
		// Total Balance
		JLabel totalChargeNumber = new JLabel(dollarFormat.format(
				((double)userInfo[6])), SwingConstants.RIGHT);
		totalChargeNumber.setFont(new Font("Arial", Font.PLAIN, 30));
		totalChargeNumber.setForeground(textColorTitle);
		totalChargeNumber.setBorder(new EmptyBorder(0, 4, 10, 4));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalChargeNumber, c);
		
		/*
		 * Payment
		 */
		
		// Payment Account Label
		currentRow++;
		JLabel paymentAccountLabel = new JLabel("Payment Account", SwingConstants.LEFT);
        paymentAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentAccountLabel.setForeground(textColor);
		paymentAccountLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(FIELD_MARGIN,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = currentRow;
        submitPaymentPanel.add(paymentAccountLabel, c);
        
		// Payment Account field
//        int paymentAccountNumber = 0; //TODO Display payment account options
		JTextField accountNumberEntry = new JTextField();
		accountNumberEntry.setFont(new Font("Arial", Font.PLAIN, 14));
		accountNumberEntry.setBackground(fieldColor);
		accountNumberEntry.setForeground(fieldText);
		accountNumberEntry.setCaretColor(fieldText);
		accountNumberEntry.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(FIELD_MARGIN,FIELD_MARGIN,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = currentRow;
        c.gridwidth = 2;
        accountNumberEntry.setText("XXXX-0000 (Recent)"); // Place holder
        accountNumberEntry.setEditable(false); // Place holder
        submitPaymentPanel.add(accountNumberEntry, c);
    
		//Payment Amount label
        currentRow++;
		JLabel paymentAmountLabel = new JLabel("Payment Amount", SwingConstants.LEFT);
        paymentAmountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentAmountLabel.setForeground(textColor);
		paymentAmountLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.gridwidth = 1;
		c.insets = new Insets(0,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = currentRow;
        submitPaymentPanel.add(paymentAmountLabel, c);
		
        // Currency symbol
        JLabel currencyLabel = new JLabel("$", SwingConstants.RIGHT);
        currencyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		currencyLabel.setForeground(textColor);
		currencyLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = currentRow;
        submitPaymentPanel.add(currencyLabel, c);
        
		// Payment Amount field
		JTextField amountNumberEntry = new JTextField();
		amountNumberEntry.setFont(new Font("Arial", Font.PLAIN, 14));
		amountNumberEntry.setBackground(fieldColor);
		amountNumberEntry.setForeground(fieldText);
		amountNumberEntry.setCaretColor(fieldText);
		amountNumberEntry.setBorder(new EmptyBorder(0, 4, 0, 4));
		amountNumberEntry.setText((new DecimalFormat("0.00")).format(((double)userInfo[6])));
		c.insets = new Insets(0,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 2;
        c.gridy = currentRow;
        c.gridwidth = 1;
        submitPaymentPanel.add(amountNumberEntry, c);
	
        // Check box
		currentRow++;
 		JCheckBox boxTerms = new JCheckBox("I agree to the Terms and Conditions.");
 		boxTerms.setFocusable(false);
 		boxTerms.setOpaque(false);
 		boxTerms.setIcon(new checkBoxIcon(fieldColor));
 		boxTerms.setPressedIcon(new checkBoxIcon(pressedColor));
 		boxTerms.setSelectedIcon(new checkBoxIcon(fieldColor, "\u2713"));
 		boxTerms.setFont(new Font("Arial", Font.PLAIN, 14));
 		boxTerms.setForeground(textColor);
 		boxTerms.setBorderPainted(false);
 		boxTerms.setMargin(new Insets(0,-2,0,0));
 		c.insets = new Insets(0,FIELD_MARGIN,FIELD_MARGIN,FIELD_MARGIN);
 		c.ipady = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
     	c.gridwidth = 3;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = currentRow;
        submitPaymentPanel.add(boxTerms, c);
 		
        /*
         * Submit Payment
         */
        
 		// Submit Payment Button container
        currentRow++;
 		JPanel submitPaymentButtonContainer = new JPanel();
 		submitPaymentButtonContainer.setLayout(new GridBagLayout());
 		submitPaymentButtonContainer.setOpaque(false);

 		// Submit Payment Button
		CustomButton submitPaymentButton = new CustomButton("Submit Payment", Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor);
        submitPaymentButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitPaymentButton.setBorderPainted(false);
        submitPaymentButton.setForeground(textColorTitle);
        submitPaymentButton.setFocusPainted(false);
        submitPaymentButton.setMargin(new Insets(-2,4,-2,4));
        submitPaymentButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Validate
				double amountEntry = validateMoneyEntry(amountNumberEntry.getText());
				if (amountEntry > 0 && boxTerms.isSelected()) {
					
					
					// Prepare query
					String sqlQuery = "INSERT payments(`name`,`amount`,`account`,`userid`) VALUES "
							+ "(?,?,?,?)";
					
					String sqlGetPaymentId = "SELECT MAX(id) AS pid FROM `payments` WHERE userid = " + currentSession.getUserId();

					// Connect to database
					try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
						PreparedStatement statement = connect.prepareStatement(sqlQuery);
						
						// Query
						statement.setString(1, "Debit Card On-Line Payment");
						statement.setDouble(2, amountEntry);
						statement.setString(3, "0000");
						statement.setInt(4, currentSession.getUserId());
						statement.executeUpdate();
						statement.close();

						// Close submit payment pop up
						submitPaymentPopup.setVisible(false);
						submitPaymentPopup.dispose();
						
						// Refresh table
						refreshAll();
						
						// Get payment id
						int paymentId = 0;
						statement = connect.prepareStatement(sqlGetPaymentId);
						ResultSet result = statement.executeQuery();
						while (result.next()) paymentId = result.getInt("pid");
						
						// Close connection
						connect.close();

						// Pull up payment confirmation
						new PopupWindow(buildPaymentConfirmation(paymentId), "ok");
						
					} catch (SQLException err) {
						err.printStackTrace();
					}	
					
				}
			}
        	
        });
        c.insets = new Insets(20,0,00,0);
        c.fill = GridBagConstraints.NONE;
     	c.gridwidth = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        submitPaymentButtonContainer.add(submitPaymentButton, c);
		
        //Container
        c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
     	c.gridwidth = 3;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = currentRow;
        submitPaymentPanel.add(submitPaymentButtonContainer, c);

		return submitPaymentPanel;
	}
	
	/**
	 * Builds a panel that displays the entered charge's information and a payment method and 
	 * confirmation number of the latest payment.
	 * @param charge - Source of data to display
	 * @return A panel with information about the last submitted payment on a charge
	 */
	private JPanel buildPaymentConfirmation(int paymentId) {
		
		String paymentName = "";
		double paymentAmount = 0.0;
		LocalDate paymentDate = LocalDate.now();
		
		// Prepare query
		String sqlQuery = "SELECT * FROM payments WHERE id = " + paymentId;

		// Connect to database
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
			Statement statement = connect.createStatement();

			// Query
			ResultSet result = statement.executeQuery(sqlQuery);
			while (result.next()) {
				paymentName = result.getString("name");
				paymentAmount = result.getDouble("amount");
				paymentDate = result.getDate("date").toLocalDate();
			}
			statement.close();

			// Close connection
			connect.close();
		} catch (SQLException err) {
			err.printStackTrace();
		}
		
		// Container
		JPanel submitPaymentPanel = new JPanel();
		submitPaymentPanel.setLayout(new GridBagLayout());
		submitPaymentPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();

		// Payment Confirmed message
		JLabel tableColumn1 = new JLabel("Payment Confirmed", SwingConstants.CENTER);
		tableColumn1.setFont(new Font("Arial", Font.PLAIN, 22));
		tableColumn1.setForeground(textColorTitle);
		tableColumn1.setBorder(new EmptyBorder(10, 4, 10, 4));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.5;
		c.gridwidth = 2;
		submitPaymentPanel.add(tableColumn1, c);
		
		// Payment title
		JLabel chargeTitle = new JLabel(paymentName, SwingConstants.CENTER);
		chargeTitle.setFont(new Font("Arial", Font.PLAIN, 16));
		chargeTitle.setForeground(textColorTitle);
		chargeTitle.setBorder(new EmptyBorder(10, 4, 10, 4));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.5;
		c.gridwidth = 2;
		submitPaymentPanel.add(chargeTitle, c);
		
		// Date label
		JLabel dateLabel = new JLabel("Payment Date", SwingConstants.LEFT);
		dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		dateLabel.setForeground(textColor);
		dateLabel.setBorder(new EmptyBorder(0, 4, 0, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(dateLabel, c);
		
		// Date number
		JLabel dateNumber = new JLabel(paymentDate.format(
				DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.RIGHT);
		dateNumber.setFont(new Font("Arial", Font.PLAIN, 14));
		dateNumber.setForeground(textColorTitle);
		dateNumber.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(dateNumber, c);
		
		// Payment Account Label
		JLabel paymentAccountLabel = new JLabel("Payment Account", SwingConstants.LEFT);
		paymentAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentAccountLabel.setForeground(textColor);
		paymentAccountLabel.setBorder(new EmptyBorder(0, 4, 0, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(paymentAccountLabel, c);
		
		// Payment Account TODO Get real account number
		JLabel paymentAccount = new JLabel("XXXX-0000", SwingConstants.RIGHT); 
		paymentAccount.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentAccount.setForeground(textColorTitle);
		paymentAccount.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(paymentAccount, c);
		
		// Confirmation number label
		JLabel confirmNumberLabel = new JLabel("Confirmation Number", SwingConstants.LEFT);
		confirmNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		confirmNumberLabel.setForeground(textColor);
		confirmNumberLabel.setBorder(new EmptyBorder(0, 4, 0, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(confirmNumberLabel, c);
		
		// Confirmation number
		JLabel contfirmNumber = new JLabel(Integer.toString(1000 + paymentId), SwingConstants.RIGHT);
		contfirmNumber.setFont(new Font("Arial", Font.PLAIN, 16));
		contfirmNumber.setForeground(textColorTitle);
		contfirmNumber.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(contfirmNumber, c);
		c.insets = new Insets(0,0,0,0);
		
		// Amount Paid Label
		JLabel amountPaidLabel = new JLabel("Payment Amount", SwingConstants.LEFT);
		amountPaidLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		amountPaidLabel.setForeground(textColor);
		amountPaidLabel.setBorder(new EmptyBorder(10, 4, 0, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(amountPaidLabel, c);
		
		// Amount Paid
		JLabel amountPaid = new JLabel(dollarFormat.format(paymentAmount), SwingConstants.RIGHT);
		amountPaid.setFont(new Font("Arial", Font.PLAIN, 14));
		amountPaid.setForeground(textColorTitle);
		amountPaid.setBorder(new EmptyBorder(10, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(amountPaid, c);
		
		return submitPaymentPanel;
	}
	
	/**
	 * Builds a panel that displays the payment method and confirmation number of 
	 * the entered payment.
	 * @param payment - Source of data to display
	 * @return A panel with information about the entered payment
	 */
	private JPanel buildPaymentDetails(int paymentId) {
		JPanel paymentDetailsPanel = new JPanel();
		paymentDetailsPanel.setLayout(new GridBagLayout());
		paymentDetailsPanel.setOpaque(false);
		
		GridBagConstraints c = new GridBagConstraints();
		
		// Payment method label
		JLabel paymentAmountLabel = new JLabel("Payment Method", SwingConstants.LEFT);
		paymentAmountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentAmountLabel.setForeground(textColor);
		paymentAmountLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		paymentDetailsPanel.add(paymentAmountLabel, c);
		
		// TODO Payment method
		JLabel paymentAmountDisplay = new JLabel("XXXX-0000", SwingConstants.LEFT);
		paymentAmountDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentAmountDisplay.setForeground(textColorTitle);
		paymentAmountDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		paymentDetailsPanel.add(paymentAmountDisplay, c);
		
		// Confirmation number label
		JLabel paymentConfirmationLabel = new JLabel("Confirmation Number", SwingConstants.LEFT);
		paymentConfirmationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentConfirmationLabel.setForeground(textColor);
		paymentConfirmationLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		paymentDetailsPanel.add(paymentConfirmationLabel, c);
		
		// Confirmation number
		JLabel paymentConfirmationDisplay = new JLabel(String.valueOf(1000 + paymentId), SwingConstants.LEFT);
		paymentConfirmationDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentConfirmationDisplay.setForeground(textColorTitle);
		paymentConfirmationDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		paymentDetailsPanel.add(paymentConfirmationDisplay, c);
		
		return paymentDetailsPanel;
	}
	
	/*
	 * Refresh Page Methods
	 */
	
	@Override
	public void refreshAll() {
		refreshTable(allResidentsPanelContent, paymentsScroll);
		refreshBalance(balancePanel, balancePanelContent);
	}

	/**
	 * Removes the Charges panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param table - Content
	 */
	private void refreshTable(JPanel panel, Component table) {
		panel.remove(table);
		buildChargesTable(panel);
		panel.validate();
	}
	
	/**
	 * Removes the Balance panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param balance - Content
	 */
	private void refreshBalance(JPanel panel, Component balance) {
		panel.remove(balance);
		buildBalancePanel(panel);
		panel.revalidate();
	}
}

