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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import accounts.Charge;
import accounts.Lease;
import accounts.Payment;
import user.Resident;
import user.User;

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
	private User user;
	
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
	 * @param user - Account of user
	 */
	public ResidentDashboardPage(User user) {
		
		// Data
		this.user = user;
		
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
        c.insets = new Insets(paneMargin,0,0,0);
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
        c.insets = new Insets(paneMargin,paneMargin,0,0);
        tabContent.add(unitInfoPanel, c);
        
        // Payments
        paymentsPanel = buildPaymentsPanel(buildPanel("Payments"));
        c.weightx = 1.0;
        c.weighty = 0.5;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(paneMargin,0,0,0);
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
		JLabel balanceNumberDisplay = new JLabel(dollarFormat.format(((Resident)user).getBalance()), SwingConstants.CENTER);
		balanceNumberDisplay.setFont(new Font("Arial", Font.PLAIN, 35));
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
 		JLabel balanceDateNumberDisplay = new JLabel("As of " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.CENTER);
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
	private JPanel buildUnitInfoPanel(JPanel panel) { //TODO Handle new accounts (no lease/unit)
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
		JLabel unitNumberDisplay = new JLabel(((Resident)user).getLatestUnitHistoryString()[1], SwingConstants.CENTER);
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
		JLabel buildingNameDisplay = new JLabel(((Resident)user).getLatestUnitHistoryString()[0], SwingConstants.CENTER);
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
		
        Lease searchUserLease = ((Resident)user).getLatestLeaseHistory();
		
		// Lease start date
		JLabel leaseStartDisplay = new JLabel(
				searchUserLease.getLeaseStart().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
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
				searchUserLease.getLeaseEnd().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
				SwingConstants.LEFT);
		leaseEndDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
		leaseEndDisplay.setForeground(textColorTitle);
		leaseEndDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
		if(searchUserLease.isFinished()) {
			if(((Resident)user).getStatus().equalsIgnoreCase("ACTIVE")) {
				leaseEndDisplay.setForeground(redColor);
			}
		}
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
		if (!searchUserLease.isFinished()) {
			statusFlag.setText("ACTIVE");
			statusFlag.setBackground(greenColor);
		}
		leaseStatusDisplay.add(statusFlag);
		
		// Add container
		c.insets = new Insets(fieldMargin,0,fieldMargin,0);
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
	    
	    // Post Date
	    JLabel columnHeader0 = new JLabel("Post Date", SwingConstants.LEFT);
		columnHeader0.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader0.setForeground(textColor);
		columnHeader0.setBorder(new EmptyBorder(4, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.15;
		c.weighty = 0.0;
		c.gridwidth = 1;
		chargesTable.add(columnHeader0, c);
		
		// Charge
		JLabel columnHeader1 = new JLabel("Charge", SwingConstants.LEFT);
		columnHeader1.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader1.setForeground(textColor);
		columnHeader1.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 3;
        chargesTable.add(columnHeader1, c);
		
        // Remaining
		JLabel columnHeader2 = new JLabel("Remaining", SwingConstants.LEFT);
		columnHeader2.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader2.setForeground(textColor);
		columnHeader2.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.15;
        c.gridx = 5;
        c.gridy = 0;
        c.gridwidth = 1;
        chargesTable.add(columnHeader2, c);
        
        // Due Date
        JLabel columnHeader3 = new JLabel("Due Date", SwingConstants.LEFT);
		columnHeader3.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader3.setForeground(textColor);
		columnHeader3.setPreferredSize(new Dimension(65, tableCellHeight));
		columnHeader3.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.weightx = 0.15;
		c.gridwidth = 1;
        c.gridx = 6;
        c.gridy = 0;
        chargesTable.add(columnHeader3, c);
        
        // Payment
        JLabel columnHeader4 = new JLabel("Payment", SwingConstants.LEFT);
		columnHeader4.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader4.setForeground(textColor);
		columnHeader4.setPreferredSize(new Dimension(65, tableCellHeight));
		columnHeader4.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.gridwidth = 1;
        c.weightx = 0.15;
        c.gridx = 7;
	    c.gridy = 0;
	    chargesTable.add(columnHeader4, c);
		
        // Header underline
        JPanel headerLine = new JPanel();
        headerLine.setBackground(textColor);
        headerLine.setPreferredSize(new Dimension(100, 1));
        c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 8;
        c.gridx = 1;
        c.gridy = 1;
        chargesTable.add(headerLine, c);
     
        /*
         * Table Rows
         */

        // Load Data
        CopyOnWriteArrayList<Charge> rowChargeList = 
        		new CopyOnWriteArrayList<Charge>(((Resident)user).getCharges());
        
        // Remove charges posted in longer than a month
        for (Charge rowChargeFilter : rowChargeList) {
        	if (rowChargeFilter.getPostingDate().isAfter(LocalDate.now().plus(1, ChronoUnit.MONTHS))) {
        		rowChargeList.remove(rowChargeFilter);
        	}
        }
        
        // Sort by post date
        rowChargeList.sort(new Comparator<Charge>() {

			@Override
			public int compare(Charge o1, Charge o2) {
				if (o1.getDueDate().isBefore(o2.getDueDate())) return 1;
				if (o1.getDueDate().isAfter(o2.getDueDate())) return -1;
				return 0;
			}
        	
        });
        
        // Loop
        int currentRow = 2;
        Color rowColor = paneColor;
        for (Charge rowCharge : rowChargeList) {

        	int rowHeight = rowCharge.getIncludedCharges().size() + 3;
        	
        	// Post date
			JLabel tableColumn0 = new JLabel(rowCharge.getPostingDate().format(
					DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.CENTER);
			tableColumn0.setFont(new Font("Arial", Font.PLAIN, 14));
	    	tableColumn0.setForeground(textColorTitle);
	    	tableColumn0.setBorder(new EmptyBorder(0, 4, 0, 4));
	    	tableColumn0.setOpaque(true);
	    	tableColumn0.setBackground(rowColor);
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 1;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = rowHeight;
			chargesTable.add(tableColumn0, c);
			c.gridheight = 1;

			// Charge name
			JLabel tableColumn1 = new JLabel(rowCharge.getName(), SwingConstants.LEFT);
			tableColumn1.setFont(new Font("Arial", Font.PLAIN, 18));
			tableColumn1.setForeground(textColorTitle);
			tableColumn1.setBorder(new EmptyBorder(10, 4, 5, 4));
			tableColumn1.setOpaque(true);
	    	tableColumn1.setBackground(rowColor);
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 2;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 3;
			chargesTable.add(tableColumn1, c);
			
			// Included Charges
			int includedChargeRow = 0;
			for (Charge subCharge : rowCharge.getIncludedCharges()) {
				currentRow++;
				
				// Included Charge Name
				JLabel includedChargeName = new JLabel(subCharge.getName(), SwingConstants.LEFT);
				includedChargeName.setFont(new Font("Arial", Font.PLAIN, 12));
				includedChargeName.setForeground(textColor);
				includedChargeName.setBorder(new EmptyBorder(0, 4, 0, 4));
				includedChargeName.setOpaque(true);
				includedChargeName.setBackground(rowColor);
		    	c.fill = GridBagConstraints.BOTH;
		        c.gridx = 2;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				chargesTable.add(includedChargeName, c);
				
				// Included Charge Amount
				JLabel includedChargeAmount = new JLabel(dollarFormat.format(
						subCharge.getAmount()), SwingConstants.RIGHT);
				includedChargeAmount.setFont(new Font("Arial", Font.PLAIN, 12));
				includedChargeAmount.setForeground(textColor);
				includedChargeAmount.setBorder(new EmptyBorder(0, 4, 0, 4));
				includedChargeAmount.setOpaque(true);
				includedChargeAmount.setBackground(rowColor);
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 3;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				chargesTable.add(includedChargeAmount, c);
				
				includedChargeRow++;
			}
			
			// Total Line
			currentRow++;
			JPanel totalLine = new JPanel();
			totalLine.setPreferredSize(MIN_SIZE);
			totalLine.setBackground(textColor);
			c.fill = GridBagConstraints.HORIZONTAL;
	        c.gridx = 3;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(totalLine, c);
			
			// Other side of line
			JPanel totalLineCounter = new JPanel();
			totalLineCounter.setPreferredSize(MIN_SIZE);
			totalLineCounter.setBackground(rowColor);
			c.fill = GridBagConstraints.HORIZONTAL;
	        c.gridx = 2;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(totalLineCounter, c);
			includedChargeRow++;
			
			// Total Charge Label
			currentRow++;
			JLabel totalChargeLabel = new JLabel("Total", SwingConstants.LEFT);
			totalChargeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			totalChargeLabel.setForeground(textColor);
			totalChargeLabel.setBorder(new EmptyBorder(0, 4, 10, 4));
			totalChargeLabel.setOpaque(true);
			totalChargeLabel.setBackground(rowColor);
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 2;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(totalChargeLabel, c);
			
			//Total Charge
			JLabel totalChargeNumber = new JLabel(dollarFormat.format(
					rowCharge.getAmount()), SwingConstants.RIGHT);
			totalChargeNumber.setFont(new Font("Arial", Font.PLAIN, 12));
			totalChargeNumber.setForeground(textColorTitle);
			totalChargeNumber.setBorder(new EmptyBorder(0, 4, 10, 4));
			totalChargeNumber.setOpaque(true);
	    	totalChargeNumber.setBackground(rowColor);
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 3;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			chargesTable.add(totalChargeNumber, c);
			
			includedChargeRow++;
			currentRow -= includedChargeRow;

			// Space
			JPanel chargeSpace = new JPanel();
			chargeSpace.setPreferredSize(MIN_SIZE);
			chargeSpace.setBackground(rowColor);
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 4;
	        c.gridy = currentRow + 1;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = rowHeight - 1;
			chargesTable.add(chargeSpace, c);
			c.gridheight = 0;
		
			// Balance Remaining
			JLabel tableColumn3 = new JLabel(dollarFormat.format(
					rowCharge.getRemainingBalance()), SwingConstants.CENTER);
			tableColumn3.setFont(new Font("Arial", Font.PLAIN, 14));
			tableColumn3.setForeground(textColorTitle);
			tableColumn3.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn3.setOpaque(true);
			tableColumn3.setBackground(rowColor);
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 5;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = rowHeight;
			chargesTable.add(tableColumn3, c);
			
			// Due Date container
			JPanel dueContainer = new JPanel();
			dueContainer.setLayout(new GridBagLayout());
			dueContainer.setBackground(rowColor);
			
			//Due date
			JLabel tableColumn4 = new JLabel(rowCharge.getDueDate().format(
					DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.LEFT);
			tableColumn4.setFont(new Font("Arial", Font.PLAIN, 14));			
			tableColumn4.setForeground(textColorTitle);
			if (rowCharge.getDueDate().isBefore(LocalDate.now()) && !rowCharge.isPaid()) {
				tableColumn4.setForeground(redColor);
			}
			tableColumn4.setBorder(new EmptyBorder(0, 4, 0, 4));
			tableColumn4.setOpaque(true);
			tableColumn4.setBackground(rowColor);
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 0;
	        c.gridy = 0;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = 1;
			dueContainer.add(tableColumn4, c);
			
			
			// Payment Status Container
			JPanel tableColumn5 = new JPanel();
			tableColumn5.setLayout(new GridBagLayout());
			tableColumn5.setOpaque(false);
			tableColumn5.setOpaque(true);
			tableColumn5.setBackground(rowColor);
			
			// Status Flag
			JLabel tableColumn5Flag = new JLabel("ON TIME", SwingConstants.LEFT);
	    	tableColumn5Flag.setFont(new Font("Arial", Font.BOLD, 11));
	    	tableColumn5Flag.setForeground(Theme.flagText);
	    	tableColumn5Flag.setBorder(new EmptyBorder(0, 4, 0, 4));
	    	tableColumn5Flag.setBackground(greenColor);
	    	tableColumn5Flag.setOpaque(true);
			if (rowCharge.isLate()) {
				tableColumn5Flag.setText("LATE");
				tableColumn5Flag.setBackground(redColor);
			}
			if (rowCharge.isPaid()) {
				tableColumn5Flag.setText("PAID");
				tableColumn5Flag.setBackground(textColor);
			}
	    	c.fill = GridBagConstraints.NONE;
	        c.gridx = 0;
	        c.gridy = 0;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			tableColumn5.add(tableColumn5Flag);
			
			// Add Payment Status Container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 0;
	        c.gridy = 1;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = 1;
			dueContainer.add(tableColumn5, c); 
			
			// Add Due container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 6;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = rowHeight;
			chargesTable.add(dueContainer, c);  
		
			// Pay Button and payment history container
			JPanel tableColumn6 = new JPanel();
			tableColumn6.setLayout(new GridBagLayout());
			tableColumn6.setOpaque(true);
			tableColumn6.setBackground(rowColor);
			tableColumn6.setBorder(new EmptyBorder(0, 4, 0, 4));
			
			// Pay button if charge is posted and not paid
			if (rowCharge.isPosted() && !rowCharge.isPaid()) {
				//Button
				CustomButton payChargeAction = new CustomButton("Pay", 
						Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
				payChargeAction.setFont(new Font("Arial", Font.BOLD, 16));
				payChargeAction.setBorderPainted(false);
				payChargeAction.setForeground(textColorTitle);
				payChargeAction.setFocusPainted(false);
				payChargeAction.addActionListener(new ActionListener() {
		
					@Override
					public void actionPerformed(ActionEvent e) {
						submitPaymentPopup = new PopupWindow(buildSubmitPayment(rowCharge), "cancel");
						
					}
					
				});	
				c.fill = GridBagConstraints.NONE;
		        c.gridx = 0;
		        c.gridy = 0;
		        c.weightx = 1.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.insets = new Insets(5,0,5,0);
				tableColumn6.add(payChargeAction, c);
			}
			
			// If a charge is not posted, but the user can still view it.
			if (!rowCharge.isPosted()) {
				JLabel upcomingLabel = new JLabel("Upcoming...", SwingConstants.LEFT);
				upcomingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
				upcomingLabel.setForeground(textColor);
				upcomingLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
				c.fill = GridBagConstraints.NONE;
		        c.gridx = 0;
		        c.gridy = 0;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.insets = new Insets(0,0,0,0);
				tableColumn6.add(upcomingLabel, c);
			}
			
			// Show button and partial payment details if charge is partially paid
			if (rowCharge.isPartiallyPaid()) {
				
				// Payments
				JPanel paymentsTable = new JPanel();
				paymentsTable.setLayout(new GridBagLayout());
				paymentsTable.setBackground(rowColor);
				
				// Loop
				int currentPaymentRow = 0;
				for (Payment p : rowCharge.getPayments()) {
					
					// Payment date
					JLabel paymentDateDisplay = new JLabel(p.getDate().format(
							DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.LEFT);
					paymentDateDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
					paymentDateDisplay.setForeground(textColorTitle);
					paymentDateDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
					c.fill = GridBagConstraints.BOTH;
			        c.gridx = 0;
			        c.gridy = currentPaymentRow;
			        c.weightx = 0.0;
					c.weighty = 0.0;
					c.gridwidth = 1;
					c.gridheight = 1;
					paymentsTable.add(paymentDateDisplay, c);
					
					// Payment amount
					JLabel paymentAmountDisplay = new JLabel(dollarFormat.format(
							p.getAmount()), SwingConstants.LEFT);
					paymentAmountDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
					paymentAmountDisplay.setForeground(textColorTitle);
					paymentAmountDisplay.setBorder(new EmptyBorder(0, 4, 0, 4));
					c.fill = GridBagConstraints.BOTH;
			        c.gridx = 1;
			        c.gridy = currentPaymentRow;
			        c.weightx = 0.0;
					c.weighty = 0.0;
					c.gridwidth = 1;
					c.gridheight = 1;
					paymentsTable.add(paymentAmountDisplay, c);
					
					// More info button
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
							new PopupWindow(buildPaymentDetails(p), "ok");
							
						}
						
					});
					c.fill = GridBagConstraints.BOTH;
			        c.gridx = 2;
			        c.gridy = currentPaymentRow;
			        c.weightx = 0.0;
					c.weighty = 0.0;
					c.gridwidth = 1;
					c.gridheight = 1;
					paymentsTable.add(moreInfoButton, c);

					currentPaymentRow++;
				}

				// Scroll through payments
				paymentsScroll = new JScrollPane(paymentsTable);
			    paymentsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			    paymentsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			    paymentsScroll.setBorder(new EmptyBorder(0,0,0,0));    
			    paymentsScroll.getVerticalScrollBar().setUnitIncrement(7);
			    paymentsScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
			    paymentsScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		        c.fill = GridBagConstraints.BOTH;
		        c.gridx = 0;
		        c.gridy = 1;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				c.insets = new Insets(5,0,5,0);
				tableColumn6.add(paymentsScroll, c);
			}
			
			// Add Pay/Payments container
			c.insets = new Insets(0,0,0,0);
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 7;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			c.gridheight = rowHeight;
			chargesTable.add(tableColumn6, c);
			c.gridheight = 1;
			
			currentRow += includedChargeRow + 1;
			
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
	private JPanel buildSubmitPayment(Charge charge) {
		// Container
		JPanel submitPaymentPanel = new JPanel();
		submitPaymentPanel.setLayout(new GridBagLayout());
		submitPaymentPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		
		/*
		 * Charge
		 */
		
		// Charge title
		JLabel chargeTitle = new JLabel(charge.getName(), SwingConstants.CENTER);
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
		
		// Sub-Charges
		int currentRow = 0;
		for (Charge subCharge : charge.getIncludedCharges()) {
			currentRow++;
			
			// Included Charge Name
			JLabel includedChargeName = new JLabel(subCharge.getName(), SwingConstants.LEFT);
			includedChargeName.setFont(new Font("Arial", Font.PLAIN, 14));
			includedChargeName.setForeground(textColor);
			includedChargeName.setBorder(new EmptyBorder(0, 4, 0, 10));
	    	c.fill = GridBagConstraints.BOTH;
	        c.gridx = 0;
	        c.gridy = currentRow;
	        c.weightx = 0.1;
			c.weighty = 0.0;
			c.gridwidth = 1;
			submitPaymentPanel.add(includedChargeName, c);
			
			// Included Charge Amount
			JLabel includedChargeAmount = new JLabel(dollarFormat.format(
					subCharge.getAmount()), SwingConstants.RIGHT);
			includedChargeAmount.setFont(new Font("Arial", Font.PLAIN, 14));
			includedChargeAmount.setForeground(textColor);
			includedChargeAmount.setBorder(new EmptyBorder(0, 4, 0, 4));
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 2;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			submitPaymentPanel.add(includedChargeAmount, c);
		}
		
		//Total Line
		currentRow++;
		JPanel totalLine = new JPanel();
		totalLine.setPreferredSize(MIN_SIZE);
		totalLine.setBackground(textColor);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalLine, c);
		
		// Total charge Label
		currentRow++;
		JLabel totalChargeLabel = new JLabel("Total", SwingConstants.LEFT);
		totalChargeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		totalChargeLabel.setForeground(textColorTitle);
		totalChargeLabel.setBorder(new EmptyBorder(0, 4, 10, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalChargeLabel, c);
		
		// Total Charge
		JLabel totalChargeNumber = new JLabel(dollarFormat.format(
				charge.getAmount()), SwingConstants.RIGHT);
		totalChargeNumber.setFont(new Font("Arial", Font.PLAIN, 16));
		totalChargeNumber.setForeground(textColorTitle);
		totalChargeNumber.setBorder(new EmptyBorder(0, 4, 10, 4));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalChargeNumber, c);
		
		// Total paid Label
		currentRow++;
		JLabel totalPaidLabel = new JLabel("Total Paid", SwingConstants.LEFT);
		totalPaidLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		totalPaidLabel.setForeground(textColor);
		totalPaidLabel.setBorder(new EmptyBorder(0, 4, 0, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalPaidLabel, c);
		
		// Total Paid
		JLabel totalPaidNumber = new JLabel(dollarFormat.format(charge.getAmountPaid()), SwingConstants.RIGHT);
		totalPaidNumber.setFont(new Font("Arial", Font.PLAIN, 14));
		totalPaidNumber.setForeground(textColor);
		totalPaidNumber.setBorder(new EmptyBorder(0, 4, 0, 4));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalPaidNumber, c);
		
		// Total Remaining Label
		currentRow++;
		JLabel totalRemainderLabel = new JLabel("Total Due", SwingConstants.LEFT);
		totalRemainderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		totalRemainderLabel.setForeground(textColorTitle);
		totalRemainderLabel.setBorder(new EmptyBorder(0, 4, 10, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalRemainderLabel, c);
		
		// Total Remaining
		JLabel totalRemainderNumber = new JLabel(dollarFormat.format(charge.getRemainingBalance()), SwingConstants.RIGHT);
		totalRemainderNumber.setFont(new Font("Arial", Font.PLAIN, 16));
		totalRemainderNumber.setForeground(textColorTitle);
		totalRemainderNumber.setBorder(new EmptyBorder(0, 4, 10, 4));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = currentRow;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(totalRemainderNumber, c);
		
		/*
		 * Payment
		 */
		
		// Payment Account Label
		currentRow++;
		JLabel paymentAccountLabel = new JLabel("Payment Account", SwingConstants.LEFT);
        paymentAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		paymentAccountLabel.setForeground(textColor);
		paymentAccountLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(fieldMargin,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = currentRow;
        submitPaymentPanel.add(paymentAccountLabel, c);
        
		// Payment Account field
        int paymentAccountNumber = 0; //TODO Display payment account options
		JTextField accountNumberEntry = new JTextField();
		accountNumberEntry.setFont(new Font("Arial", Font.PLAIN, 14));
		accountNumberEntry.setBackground(fieldColor);
		accountNumberEntry.setForeground(fieldText);
		accountNumberEntry.setCaretColor(fieldText);
		accountNumberEntry.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(fieldMargin,fieldMargin,fieldMargin,0);
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
		c.insets = new Insets(0,0,fieldMargin,0);
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
		c.insets = new Insets(0,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = currentRow;
        submitPaymentPanel.add(currencyLabel, c);
        
		// Payment Amount field //TODO Make field editable
		JTextField amountNumberEntry = new JTextField();
		amountNumberEntry.setFont(new Font("Arial", Font.PLAIN, 14));
		amountNumberEntry.setBackground(fieldColor);
		amountNumberEntry.setForeground(fieldText);
		amountNumberEntry.setCaretColor(fieldText);
		amountNumberEntry.setBorder(new EmptyBorder(0, 4, 0, 4));
		amountNumberEntry.setText(String.valueOf(charge.getRemainingBalance()));
		c.insets = new Insets(0,0,fieldMargin,0);
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
 		boxTerms.setOpaque(false);
 		boxTerms.setIcon(new checkBoxIcon(fieldColor));
 		boxTerms.setPressedIcon(new checkBoxIcon(pressedColor));
 		boxTerms.setSelectedIcon(new checkBoxIcon(fieldColor, "\u2713"));
 		boxTerms.setFont(new Font("Arial", Font.PLAIN, 14));
 		boxTerms.setForeground(textColor);
 		boxTerms.setBorderPainted(false);
 		boxTerms.setMargin(new Insets(0,-2,0,0));
 		c.insets = new Insets(0,fieldMargin,fieldMargin,fieldMargin);
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
					if (charge.makePayment(amountEntry, paymentAccountNumber, LocalDate.now())) {
						// Close submit payment pop up
						submitPaymentPopup.setVisible(false);
						submitPaymentPopup.dispose();
						
						// Refresh table
						refreshAll();
						
						// Pull up payment confirmation
						new PopupWindow(buildPaymentConfirmation(charge), "ok");
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
	 * confirmation number of the latest payment on the charge.
	 * @param charge - Source of data to display
	 * @return A panel with information about the last submitted payment on a charge
	 */
	private JPanel buildPaymentConfirmation(Charge charge) {
		// Container
		JPanel submitPaymentPanel = new JPanel();
		submitPaymentPanel.setLayout(new GridBagLayout());
		submitPaymentPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		
		//Get latest payment on this charge
		Payment payment = charge.getLastPayment();

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
		
		// Charge title
		JLabel chargeTitle = new JLabel(charge.getName(), SwingConstants.CENTER);
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
		JLabel dateNumber = new JLabel(payment.getDate().format(
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
		JLabel contfirmNumber = new JLabel(Integer.toString(payment.getConfirmNumber()), SwingConstants.RIGHT);
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
		JLabel amountPaid = new JLabel(dollarFormat.format(charge.getLastPayment().getAmount()), SwingConstants.RIGHT);
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
		
		// Remaining label
		JLabel amountRemainingLabel = new JLabel("Charge Remaining", SwingConstants.LEFT);
		amountRemainingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		amountRemainingLabel.setForeground(textColor);
		amountRemainingLabel.setBorder(new EmptyBorder(0, 4, 0, 10));
    	c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(amountRemainingLabel, c);

		// Remaining
		JLabel amountRemaining = new JLabel(dollarFormat.format(charge.getRemainingBalance()), SwingConstants.RIGHT);
		amountRemaining.setFont(new Font("Arial", Font.PLAIN, 14));
		amountRemaining.setForeground(textColorTitle);
		amountRemaining.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 6;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		submitPaymentPanel.add(amountRemaining, c);
		
		return submitPaymentPanel;
	}
	
	/**
	 * Builds a panel that displays the payment method and confirmation number of 
	 * the entered payment.
	 * @param payment - Source of data to display
	 * @return A panel with information about the entered payment
	 */
	private JPanel buildPaymentDetails(Payment payment) {
		JPanel paymentDetailsPanel = new JPanel();
		paymentDetailsPanel.setLayout(new GridBagLayout());
		paymentDetailsPanel.setOpaque(false);
		
		GridBagConstraints c = new GridBagConstraints();
		
		// Payment amount label
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
		
		// Payment amount
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
		JLabel paymentConfirmationDisplay = new JLabel(String.valueOf(payment.getConfirmNumber()), SwingConstants.LEFT);
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

