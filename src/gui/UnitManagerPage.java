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
import java.time.temporal.ChronoUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import unit.Building;
import unit.BuildingList;
import unit.Unit;
import unit.Building.Floor;
import user.Resident;
import user.User;
import user.UserList;

/**
 * A class that creates a Unit Manager Page consisting of three panels:
 * Summary, Units, and New Unit
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class UnitManagerPage extends JPanel implements Page {
	
	// Data
	private UserList users;
	private BuildingList buildingList;
	
	//Setup
	private String title = "Unit Manager";
	
	// Panels
	private JPanel summaryPanel;
	private JPanel allUnitsPanel;
	private JPanel newUnitPanel;
	
	// Pop-up
	private PopupWindow unassignedUserPanel;
	private PopupWindow unassignedUserDate;
	
	// New Lease (Add resident to unit)
	private JTextField leaseStartEntry;
	private JTextField leaseEndEntry;
	private JTextField rentAmountEntry;
	
	// New Unit Panel
	private JTextField unitNumber;
	private JTextField building;
	private JTextField floor;
	private JTextField floorArea;
	
	// Minimize
	private JPanel newUnitContent;
	
	//Refresh
	private JPanel summaryPanelContent;
	private JPanel allUnitsPanelContent;
	private JScrollPane unitScroll;
	
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

	/**
	 * Creates a Unit Manager page with a summary panel, a panel where units 
	 * are displayed, and a panel to add a new unit.
	 * @param users - List of user accounts
	 * @param buildingList - List of buildings
	 */
	public UnitManagerPage(UserList users, BuildingList buildingList) {
		
		// Data
		this.buildingList = buildingList;
		this.users = users;
		
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
        
        // Summary
        summaryPanel = buildSummaryPanel(buildPanel("Summary"));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(paneMargin,0,0,0);
        tabContent.add(summaryPanel, c);
        
        // Units
        allUnitsPanel = buildAllUnits(buildPanel("Units"));
        c.weightx = 1.0;
        c.weighty = 2.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(paneMargin,0,0,0);
        tabContent.add(allUnitsPanel, c);
        
        // Initialize New Unit Content (to minimize)
        newUnitContent = new JPanel();
        newUnitContent.setVisible(false);
        
        // New Unit (collapsible)
        newUnitPanel = buildNewUnit(buildPanel("New Unit", true, newUnitContent));
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(paneMargin,0,0,0);
        tabContent.add(newUnitPanel, c);
	}
	
	/*
	 * Summary Panel
	 */
	
	/**
	 * Fills a panel with with unit statistics.
	 * @param panel - Empty theme panel
	 * @return A panel with unit statistics.
	 */
	private JPanel buildSummaryPanel(JPanel panel) {
		// Container
		summaryPanelContent = new JPanel();
		summaryPanelContent.setLayout(new GridBagLayout());
		summaryPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		summaryPanelContent.setOpaque(true);
		summaryPanelContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
		
		/*
		 * Frame
		 */
	
		double summaryVertPaddingWeight = 0.1;
		double summaryHorPaddingWeight = 0.01;
		
		JPanel summaryTopPadding = new JPanel();
		summaryTopPadding.setOpaque(false);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
		c.weighty = summaryVertPaddingWeight;
		c.gridwidth = 3;
		summaryPanelContent.add(summaryTopPadding, c);
		
		JPanel summaryBottomPadding = new JPanel();
		summaryBottomPadding.setOpaque(false);
		c.gridy = 2;
		summaryPanelContent.add(summaryBottomPadding, c);
		
		JPanel summaryLeftPadding = new JPanel();
		summaryLeftPadding.setOpaque(false);
		c.gridx = 0;
        c.gridy = 1;
        c.weightx = summaryHorPaddingWeight;
		c.weighty = 0;
		c.gridwidth = 1;
		summaryPanelContent.add(summaryLeftPadding, c);
		
		JPanel summaryRightPadding = new JPanel();
		summaryRightPadding.setOpaque(false);
		c.gridx = 4;
		summaryPanelContent.add(summaryRightPadding, c);
		
		// Dividing line
		JPanel summaryMidLineSpace = new JPanel();
		summaryMidLineSpace.setLayout(new GridBagLayout());
		summaryMidLineSpace.setOpaque(false);
		
		JPanel summaryMidLine = new JPanel();
		summaryMidLine.setBackground(textColor);
		summaryMidLine.setPreferredSize(MIN_SIZE);
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 1;
		summaryMidLineSpace.add(summaryMidLine, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.1;
		c.weighty = 1;
		c.gridwidth = 1;
		summaryPanelContent.add(summaryMidLineSpace, c);
		
		/*
		 * Summary section: Total Units
		 */
		
		// Section 1 Container
		JPanel summarySection1 = new JPanel();
		summarySection1.setOpaque(false);
		summarySection1.setLayout(new GridBagLayout());
		
		// Total Units label
		JLabel summaryTotalUnitsLabel = new JLabel("Total Units", SwingConstants.LEFT);
		summaryTotalUnitsLabel.setFont(new Font("Arial", Font.BOLD, 16));
		summaryTotalUnitsLabel.setForeground(textColorTitle);
		summaryTotalUnitsLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		summarySection1.add(summaryTotalUnitsLabel, c);

		// Space
		JPanel summarySection1Padding1 = new JPanel();
		summarySection1Padding1.setOpaque(false);
		c.gridy = 1;
		c.weighty = 1.0;
		summarySection1.add(summarySection1Padding1, c);
		
		// Occupied and Vacant container
		JPanel summaryOccVacDisplay = new JPanel();
		summaryOccVacDisplay.setOpaque(false);
		summaryOccVacDisplay.setLayout(new GridBagLayout());
		
		// Occupied label
		JLabel summaryOccupiedLabel = new JLabel("Occupied", SwingConstants.LEFT);
		summaryOccupiedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		summaryOccupiedLabel.setForeground(textColor);
		summaryOccupiedLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.NORTH;
		summaryOccVacDisplay.add(summaryOccupiedLabel, c);
		
		// Occupied number
		JLabel summaryOccupiedNumber = new JLabel(Integer.toString((buildingList.getVacancyStats())[0]), SwingConstants.LEFT);
		summaryOccupiedNumber.setFont(new Font("Arial", Font.PLAIN, 25));
		summaryOccupiedNumber.setForeground(textColorTitle);
		summaryOccupiedNumber.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		summaryOccVacDisplay.add(summaryOccupiedNumber, c);
		
		// Occupied percentage bar
		JPanel summaryOccupiedPercentBar = buildPercentageBar(1.0 - buildingList.getVacancyRate());
		c.insets = new Insets(0,10,0,10);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summaryOccVacDisplay.add(summaryOccupiedPercentBar, c);
		
		// Vacant label
		JLabel summaryVacantLabel = new JLabel("Vacant", SwingConstants.LEFT);
		summaryVacantLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		summaryVacantLabel.setForeground(textColor);
		summaryVacantLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.NORTH;
		summaryOccVacDisplay.add(summaryVacantLabel, c);
		
		// Vacant number
		JLabel summaryVacantNumber = new JLabel(Integer.toString((buildingList.getVacancyStats())[1]), SwingConstants.LEFT);
		summaryVacantNumber.setFont(new Font("Arial", Font.PLAIN, 25));
		summaryVacantNumber.setForeground(textColorTitle);
		summaryVacantNumber.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		summaryOccVacDisplay.add(summaryVacantNumber, c);
		
		// Vacant percentage bar
		JPanel summaryVacantPercentBar = buildPercentageBar(buildingList.getVacancyRate());
		c.insets = new Insets(0,10,0,10);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summaryOccVacDisplay.add(summaryVacantPercentBar, c);
		
		// Add Occupied and Vacant container
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		summarySection1.add(summaryOccVacDisplay, c);
		
		// Space
		JPanel summarySection1Padding2 = new JPanel();
		summarySection1Padding2.setOpaque(false);
		c.gridy = 3;
		c.weighty = 1.0;
		summarySection1.add(summarySection1Padding2, c);
		
		// Vacancy Rate label
		JLabel summaryVacRateLabel = new JLabel("Vacancy Rate", SwingConstants.LEFT);
		summaryVacRateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		summaryVacRateLabel.setForeground(textColor);
		summaryVacRateLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection1.add(summaryVacRateLabel, c);
		
		// Vacancy Rate number
		JLabel summaryVacRateNumLabel = new JLabel(Integer.toString((int)(buildingList.getVacancyRate() * 100)) + "%", SwingConstants.LEFT);
		summaryVacRateNumLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		summaryVacRateNumLabel.setForeground(textColorTitle);
		summaryVacRateNumLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection1.add(summaryVacRateNumLabel, c);
		
		// Leases ending label
		JLabel summaryLeasesLabel = new JLabel("Leases Ending (60d)", SwingConstants.LEFT);
		summaryLeasesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		summaryLeasesLabel.setForeground(textColor);
		summaryLeasesLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection1.add(summaryLeasesLabel, c);
		
		// Leases ending number
		JLabel summaryLeaseNumLabel = new JLabel(Integer.toString(buildingList.getLeaseEndMonths(2)), SwingConstants.LEFT);
		summaryLeaseNumLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		summaryLeaseNumLabel.setForeground(textColorTitle);
		summaryLeaseNumLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection1.add(summaryLeaseNumLabel, c);
		
		// Add Section 1 Container
		c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		summaryPanelContent.add(summarySection1, c);
		
		/*
		 * Summary section: Payments
		 */
		
		// Section 2 Container
		JPanel summarySection2 = new JPanel();
		summarySection2.setOpaque(false);
		summarySection2.setLayout(new GridBagLayout());
		
		// Payments label
		JLabel summaryPaymentsLabel = new JLabel("Payments", SwingConstants.LEFT);
		summaryPaymentsLabel.setFont(new Font("Arial", Font.BOLD, 16));
		summaryPaymentsLabel.setForeground(textColorTitle);
		summaryPaymentsLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection2.add(summaryPaymentsLabel, c);
		
		// Space
		JPanel summarySection2Padding1 = new JPanel();
		summarySection2Padding1.setOpaque(false);
		c.gridy = 1;
		c.weighty = 1.0;
		summarySection2.add(summarySection2Padding1, c);
		
		// On time label
		JLabel summaryOnTimeLabel = new JLabel("On Time", SwingConstants.LEFT);
		summaryOnTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		summaryOnTimeLabel.setForeground(textColor);
		summaryOnTimeLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection2.add(summaryOnTimeLabel, c);
		
		// On time number
		JLabel summaryOnTimeNumLabel = new JLabel(Integer.toString((buildingList.getPaymentStats())[0]), SwingConstants.LEFT);
		summaryOnTimeNumLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		summaryOnTimeNumLabel.setForeground(textColorTitle);
		summaryOnTimeNumLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection2.add(summaryOnTimeNumLabel, c);
		
		// Space
		JPanel summarySection2Padding2 = new JPanel();
		summarySection2Padding2.setOpaque(false);
		c.gridy = 5;
		c.weighty = 1.0;
		summarySection2.add(summarySection2Padding2, c);
		
		// Overdue label
		JLabel summaryOverdueLabel = new JLabel("Overdue", SwingConstants.LEFT);
		summaryOverdueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		summaryOverdueLabel.setForeground(textColor);
		summaryOverdueLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection2.add(summaryOverdueLabel, c);
		
		// Overdue number
		JLabel summaryOverdueNumLabel = new JLabel(Integer.toString((buildingList.getPaymentStats())[1]), SwingConstants.LEFT);
		summaryOverdueNumLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		summaryOverdueNumLabel.setForeground(textColorTitle);
		summaryOverdueNumLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 7;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		summarySection2.add(summaryOverdueNumLabel, c);
		
		//Add Section 1 Container
		c.gridx = 3;
        c.gridy = 1;
        c.weightx = 0.25;
		c.weighty = 1.0;
		c.gridwidth = 1;
		summaryPanelContent.add(summarySection2, c);

        /*
         * Add content to panel
         */
		
		panel.add(summaryPanelContent);
		return panel;
	}
	
	/*
	 * All Units Panel
	 */
	
	/**
	 * Fills and returns a panel with a table of units and their information.
	 * @param panel - Empty theme panel
	 * @return A panel with a unit table
	 */
	private JPanel buildAllUnits(JPanel panel) {
		allUnitsPanelContent = new JPanel();
		allUnitsPanelContent.setLayout(new GridBagLayout());
		allUnitsPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		allUnitsPanelContent.setOpaque(true);
		
		buildUnitTable(allUnitsPanelContent);
		
		panel.add(allUnitsPanelContent);
		return panel;
	}
	
	/**
	 * Fills the input panel with a Unit table.
	 * Creates a scroll table with Unit information
	 * @param panel - Panel to build the Unit table on
	 */
	private void buildUnitTable(JPanel panel) {
		// Container
		JPanel unitTable = new JPanel();
	    unitTable.setLayout(new GridBagLayout());
	    unitTable.setOpaque(true);
	    unitTable.setBackground(paneColor);
	    unitTable.setAlignmentX(Component.LEFT_ALIGNMENT);
	    GridBagConstraints c = new GridBagConstraints();
	    
	    // Left space
	    JPanel leftSpacer = new JPanel();
	    leftSpacer.setOpaque(false);
    	c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        unitTable.add(leftSpacer, c);
        
        // Right space
	    JPanel rightSpacer = new JPanel();
	    rightSpacer.setOpaque(false);
    	c.fill = GridBagConstraints.BOTH;
		//c.weightx = 0.1;
		c.weighty = 0.0;
        c.gridx = 10;
        c.gridy = 0;
        unitTable.add(rightSpacer, c);
	    
	    /*
	     * Table header
	     */
	    
        // Building
	    JLabel columnHeader0 = new JLabel("Building", SwingConstants.LEFT);
		columnHeader0.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader0.setForeground(textColor);
		columnHeader0.setBorder(new EmptyBorder(4, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		unitTable.add(columnHeader0, c);
		
		// Floor
		JLabel columnHeader1 = new JLabel("Floor", SwingConstants.LEFT);
		columnHeader1.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader1.setForeground(textColor);
		columnHeader1.setPreferredSize(new Dimension(45, tableCellHeight));
		columnHeader1.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
        c.gridx = 2;
        c.gridy = 0;
        unitTable.add(columnHeader1, c);
		
        // Unit
		JLabel columnHeader2 = new JLabel("Unit", SwingConstants.LEFT);
		columnHeader2.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader2.setForeground(textColor);
		columnHeader2.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
        c.gridx = 3;
        c.gridy = 0;
        unitTable.add(columnHeader2, c);
        
        //Resident
        JLabel columnHeader3 = new JLabel("Resident", SwingConstants.LEFT);
		columnHeader3.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader3.setForeground(textColor);
		columnHeader3.setPreferredSize(new Dimension(65, tableCellHeight));
		columnHeader3.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.weightx = 0.1;
        c.gridx = 4;
        c.gridy = 0;
        unitTable.add(columnHeader3, c);
      
		// Lease End
		JLabel columnSize5 = new JLabel("Lease End", SwingConstants.LEFT);
		columnSize5.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize5.setForeground(textColor);
		columnSize5.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.weightx = 0.1;
        c.gridx = 6;
        c.gridy = 0;
        unitTable.add(columnSize5, c);
        
        // Rent
        JLabel columnSize6 = new JLabel("Rent", SwingConstants.LEFT);
		columnSize6.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize6.setForeground(textColor);
		columnSize6.setBorder(new EmptyBorder(0, 4, 0, 4));
        c.gridx = 7;
        c.gridy = 0;
        unitTable.add(columnSize6, c);
        
        // Payment
        JLabel columnSize7 = new JLabel("Payment", SwingConstants.LEFT);
		columnSize7.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize7.setForeground(textColor);
        c.gridx = 8;
        c.gridy = 0;
        unitTable.add(columnSize7, c);
        
        // Delete
        JLabel columnSize8 = new JLabel("Delete", SwingConstants.LEFT);
		columnSize8.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize8.setForeground(textColor);
		c.weightx = 0.0;
        c.gridx = 9;
        c.gridy = 0;
        
        // Header underline
        JPanel headerLine = new JPanel();
        headerLine.setBackground(textColor);
        headerLine.setPreferredSize(new Dimension(100, 1));
        c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridwidth = 9;
        c.gridx = 1;
        c.gridy = 1;
        unitTable.add(headerLine, c);
        
        /*
         * Table Rows
         */
        
        // Loop
        int currentRow = 2;
    	for (Building building : buildingList.getAllBuildings()) {
    		
    		boolean firstRow = true; // To display building name only once
        	for (Floor floor : building.getAllFloors()) {
        		
        		boolean firstFloorRow = true; // To display floor number only once
        		for (Unit unit : floor.getAllUnits()) {
        			
        			/*
        			 * Building
        			 */
        			
        			String buildingName = firstRow ? building.getName() : " ";
        			
        			JLabel tableColumn0 = new JLabel(buildingName, SwingConstants.LEFT);
        	    	tableColumn0.setFont(new Font("Arial", Font.BOLD, 14));
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
        			unitTable.add(tableColumn0, c);
        			
        			/*
        			 * Floor
        			 */
        			
        			String floorName = firstFloorRow ? Integer.toString(unit.getFloorInt()) : " ";
 
        			JLabel tableColumn1 = new JLabel(floorName, SwingConstants.LEFT);
        	    	tableColumn1.setFont(new Font("Arial", Font.PLAIN, 14));
        	    	tableColumn1.setForeground(textColorTitle);
        	    	tableColumn1.setBorder(new EmptyBorder(0, 4, 0, 4));
        	    	tableColumn1.setPreferredSize(new Dimension(1, tableCellHeight)); // Sets row height
        	    	if (currentRow % 2 == 1) {
        	    		tableColumn1.setOpaque(true);
        	    		tableColumn1.setBackground(paneLightColor);
        	    	}
        			c.fill = GridBagConstraints.BOTH;
        	        c.gridx = 2;
        	        c.gridy = currentRow;
        	        c.weightx = 0.0;
        			c.weighty = 0.0;
        			c.gridwidth = 1;
        			unitTable.add(tableColumn1, c);
        			
        			firstRow = false;
        			firstFloorRow = false;
        			
        			/*
        			 * Unit
        			 */
        			
        			JLabel tableColumn2 = new JLabel(unit.getUnitNumber(), SwingConstants.LEFT);
        	    	tableColumn2.setFont(new Font("Arial", Font.PLAIN, 14));
        	    	tableColumn2.setForeground(textColorTitle);
        	    	tableColumn2.setBorder(new EmptyBorder(0, 4, 0, 4));
        	    	if (currentRow % 2 == 1) {
        	    		tableColumn2.setOpaque(true);
        	    		tableColumn2.setBackground(paneLightColor);
        	    	}
        			c.fill = GridBagConstraints.BOTH;
        	        c.gridx = 3;
        	        c.gridy = currentRow;
        	        c.weightx = 0.0;
        			c.weighty = 0.0;
        			c.gridwidth = 1;
        			unitTable.add(tableColumn2, c);
        			
        			/*
        			 * Resident
        			 */

        			JLabel tableColumn3 = new JLabel();
        	    	
        	    	// Pull resident object
        			Resident tableResident = null;
        			if (!unit.isEmpty()) tableResident = unit.getResident();
        			
        			// If resident in unit
        			if (tableResident != null) {
        				// Set cell to name
        				tableColumn3.setText(tableResident.getName());
        				tableColumn3.setFont(new Font("Arial", Font.PLAIN, 14));
        				tableColumn3.setForeground(textColorTitle);
        				tableColumn3.setOpaque(false);
        				if (currentRow % 2 == 1) {
            	    		tableColumn3.setOpaque(true);
            	    		tableColumn3.setBackground(paneLightColor);
            	    	}
        				tableColumn3.setBorder(new EmptyBorder(0, 4, 0, 4));
        				tableColumn3.setPreferredSize(null);
        				c.fill = GridBagConstraints.BOTH;
            	        c.gridx = 4;
            	        c.gridy = currentRow;
            	        c.weightx = 0.0;
            			c.weighty = 0.0;
            			c.gridwidth = 1;
        				unitTable.add(tableColumn3, c);
        			}
        			
        			// If vacant set cell to VACANT flag
        			else {
        				// Container
            			JPanel tableColumn3Vacant = new JPanel();
            			tableColumn3Vacant.setLayout(new GridBagLayout());
            			tableColumn3Vacant.setOpaque(false);
            			if (currentRow % 2 == 1) {
            	    		tableColumn3Vacant.setOpaque(true);
            	    		tableColumn3Vacant.setBackground(paneLightColor);
            	    	}

            			// Add label to container
        				tableColumn3.setText("VACANT");
            	    	tableColumn3.setFont(new Font("Arial", Font.BOLD, 11));
            	    	tableColumn3.setForeground(Theme.flagText);
            	    	tableColumn3.setBorder(new EmptyBorder(0, 4, 0, 4));
            	    	tableColumn3.setBackground(redColor);
            	    	tableColumn3.setOpaque(true);
            			c.fill = GridBagConstraints.NONE;
            	        c.gridx = 0;
            	        c.gridy = 0;
            	        c.weightx = 0.1;
            			c.weighty = 0.0;
            			c.gridwidth = 1;
            			tableColumn3Vacant.add(tableColumn3, c);
        				
            			// Add container to cell
        				c.fill = GridBagConstraints.BOTH;
            	        c.gridx = 4;
            	        c.gridy = currentRow;
            	        c.weightx = 0.0;
            			c.weighty = 0.0;
            			c.gridwidth = 1;
        				unitTable.add(tableColumn3Vacant, c);
        			}
        			
        			/*
        			 * Add or remove resident buttons
        			 */
        			
        			// Button Container
        			JPanel tableColumn4 = new JPanel();
        			tableColumn4.setLayout(new GridBagLayout());
        			tableColumn4.setOpaque(false);
        			if (currentRow % 2 == 1) {
        	    		tableColumn4.setOpaque(true);
        	    		tableColumn4.setBackground(paneLightColor);
        	    	}
        			tableColumn4.setBorder(new EmptyBorder(0, 4, 0, 4));
        			
        			// If unit is occupied (Show remove button)
        			if (!unit.isEmpty()) {
        				// Remove button
        				CustomButton residentAction = new CustomButton("-", 
        						Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
            			residentAction.setFont(new Font("Arial", Font.BOLD, 14));
            			residentAction.setBorderPainted(false);
            			residentAction.setForeground(textColorTitle);
            			residentAction.setFocusPainted(false);
            			residentAction.setBorder(new EmptyBorder(0, 2, 0, 3));
            			residentAction.setPreferredSize(new Dimension(16,16));
            	        residentAction.addActionListener(new ActionListener() {
            	        	
            				@Override
            				public void actionPerformed(ActionEvent e) {
            						// Bring up pop up
            						new PopupWindow(new ActionListener() {

            							@Override
            							public void actionPerformed(ActionEvent e) {
            								// If user answers yes
            								if (e.getActionCommand().equals("Yes")) {
            									unit.getResident().removeUnit();
            									unit.removeResident();
            									refreshAll();
            								}
            								// If user answers no
            								if (e.getActionCommand().equals("No")) {}
            							}
            							
            						}, "Are you sure you want to remove this resident?", "yesno");
            				}
            	        
            	        });
            			tableColumn4.add(residentAction);
        			}
        			
        			// If unit is empty (Show add button)
        			else {
        				// Add button
        				CustomButton residentAction = new CustomButton("+", 
        						Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
            			residentAction.setFont(new Font("Arial", Font.BOLD, 14));
            			residentAction.setBorderPainted(false);
            			residentAction.setForeground(textColorTitle);
            			residentAction.setFocusPainted(false);
            			residentAction.setBorder(new EmptyBorder(0, 4, 0, 4));
            			residentAction.setPreferredSize(new Dimension(16,16));
            			residentAction.addActionListener(new ActionListener() {
            				
            				@Override
            				public void actionPerformed(ActionEvent e) {
            					unassignedUserPanel = new PopupWindow(new ActionListener() {

            							@Override
            							public void actionPerformed(ActionEvent e) {
            								// If user answers Cancel
            								if (e.getActionCommand().equalsIgnoreCase("Cancel")) {

            								}
            								else {
            								}

            							}
            							
            						}, buildUnassignedUserPanel(unit), "cancel");
            				}
            				
            	        });
            			tableColumn4.add(residentAction);
        			}
        			
        			// Add container to cell
        			c.fill = GridBagConstraints.BOTH;
        	        c.gridx = 5;
        	        c.gridy = currentRow;
        	        c.weightx = 0.0;
        			c.weighty = 0.0;
        			c.gridwidth = 1;
        			unitTable.add(tableColumn4, c);
        			
        			/*
        			 * If unit is full (Rest of cells will be blank if the unit is empty))
        			 */
        			
        			if (!unit.isEmpty()) {
        				
        				/*
        				 * Lease end 
        				 */
        				
            			JLabel tableColumn5 = 
            					new JLabel(unit.getResident().getLeaseEnd().format(
            							DateTimeFormatter.ofPattern("MM/dd/yyyy")), SwingConstants.LEFT);
            	    	tableColumn5.setFont(new Font("Arial", Font.PLAIN, 14));
            	    	tableColumn5.setForeground(textColorTitle);
            	    	tableColumn5.setBorder(new EmptyBorder(0, 4, 0, 4));
            	    	if (currentRow % 2 == 1) {
            	    		tableColumn5.setOpaque(true);
            	    		tableColumn5.setBackground(paneLightColor);
            	    	}
            	    	
            	    	// If the lease is done, grey
            	    	if(unit.getResident().getLatestLeaseHistory().isFinished()) {
            	    		tableColumn5.setForeground(textColor);
        					// but they are still active, red
        					if(unit.getResident().getStatus().equalsIgnoreCase("ACTIVE")) {
        						tableColumn5.setForeground(redColor); // They should move out
        					}	
        				}
            	    	
            	    	else { 
            	    		// If the lease is ending in he next two months, yellow
            	    		if (unit.getResident().getLatestLeaseHistory().getLeaseEnd().isBefore(
            	    				LocalDate.now().plus(2, ChronoUnit.MONTHS))) {
            	    			tableColumn5.setForeground(Theme.yellowColor);
            	    		}
            	    	}

            			c.fill = GridBagConstraints.BOTH;
            	        c.gridx = 6;
            	        c.gridy = currentRow;
            	        c.weightx = 0.0;
            			c.weighty = 0.0;
            			c.gridwidth = 1;
            			unitTable.add(tableColumn5, c);
            			
            			//TODO Add Lease Renew button
            			
            			/*
            			 * Rent amount
            			 */
            			
            			int rentAmount = unit.getResident().getLeaseAmount();
            			String rentAmountString = String.format("%,d", rentAmount);
            			
            			JLabel tableColumn6 = new JLabel(("$ " + rentAmountString), SwingConstants.LEFT);
            	    	tableColumn6.setFont(new Font("Arial", Font.PLAIN, 14));
            	    	tableColumn6.setForeground(textColorTitle);
            	    	tableColumn6.setBorder(new EmptyBorder(0, 4, 0, 4));
            	    	if (currentRow % 2 == 1) {
            	    		tableColumn6.setOpaque(true);
            	    		tableColumn6.setBackground(paneLightColor);
            	    	}
            			c.fill = GridBagConstraints.BOTH;
            	        c.gridx = 7;
            	        c.gridy = currentRow;
            	        c.weightx = 0.0;
            			c.weighty = 0.0;
            			c.gridwidth = 1;
            			unitTable.add(tableColumn6, c);
            			
            			/*
            			 * Payment status
            			 */
            			
            			// Container
            			JPanel tableColumn7 = new JPanel();
            			tableColumn7.setLayout(new GridBagLayout());
            			tableColumn7.setOpaque(false);
        				if (currentRow % 2 == 1) {
            	    		tableColumn7.setOpaque(true);
            	    		tableColumn7.setBackground(paneLightColor);
            	    	}
            			
        				// Flag
            			JLabel tableColumn7Flag = new JLabel("ON TIME", SwingConstants.LEFT);
            	    	tableColumn7Flag.setFont(new Font("Arial", Font.BOLD, 11));
            	    	tableColumn7Flag.setForeground(Theme.flagText);
            	    	tableColumn7Flag.setBorder(new EmptyBorder(0, 4, 0, 4));
            	    	tableColumn7Flag.setBackground(greenColor);
            	    	tableColumn7Flag.setOpaque(true);
            	    	
            	    	// If resident not on time
            			if (unit.getResident().isLate()) {
            				tableColumn7Flag.setText("LATE");
            				tableColumn7Flag.setBackground(redColor);
            			}
            			if (unit.getResident().isFullyPaid()) {
            				tableColumn7Flag.setText("PAID");
            				tableColumn7Flag.setBackground(textColor);
            			}
            			
            	    	c.fill = GridBagConstraints.NONE;
            	        c.gridx = 0;
            	        c.gridy = 0;
            	        c.weightx = 0.0;
            			c.weighty = 0.0;
            			c.gridwidth = 1;
            			tableColumn7.add(tableColumn7Flag);
            			
            			// Container
            			c.fill = GridBagConstraints.BOTH;
            	        c.gridx = 8;
            	        c.gridy = currentRow;
            	        c.weightx = 0.0;
            			c.weighty = 0.0;
            			c.gridwidth = 1;
            			unitTable.add(tableColumn7, c);    			
        			}
        			
        			/*
        			 * If unit is empty (Fill empty space with the right color)
        			 */
        			
        			else {
        				JPanel tableColumn5 = new JPanel();
        				tableColumn5.setOpaque(false);
        				if (currentRow % 2 == 1) {
            	    		tableColumn5.setOpaque(true);
            	    		tableColumn5.setBackground(paneLightColor);
            	    	}
        				c.fill = GridBagConstraints.BOTH;
            	        c.gridx = 6;
            	        c.gridy = currentRow;
            	        c.weightx = 0.0;
            			c.weighty = 0.0;
            			c.gridwidth = 3;
            			unitTable.add(tableColumn5, c);
        			}
        			
        			/*
        			 * Delete Unit Button
        			 */
        			
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
    				CustomButton deleteAction = new CustomButton("Delete Unit", 
    						Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
    				deleteAction.setFont(new Font("Arial", Font.BOLD, 10));
    				deleteAction.setForeground(textColorTitle);
    				deleteAction.setFocusPainted(false);
    				deleteAction.setBorder(new EmptyBorder(0, 0, 0, 0));
    				deleteAction.setPreferredSize(new Dimension(65,16));
    				deleteAction.addActionListener(new ActionListener() {
    					
        				@Override
        				public void actionPerformed(ActionEvent e) {
        						// Bring up pop up
        						new PopupWindow(new ActionListener() {

        							@Override
        							public void actionPerformed(ActionEvent e) {
        								// If user answers yes
        								if (e.getActionCommand().equals("Yes")) {
        									// Remove resident from unit
        									if (!unit.isEmpty()) {
        										unit.getResident().removeUnit();
            									unit.removeResident();
        									}
        									//delete unit
        									buildingList.removeUnit(unit);
        									refreshAll();
        								}
        							}
        							
        						}, "Are you sure you want to remove this unit?", "yesno");
        				}
        				
        	        });
        			tableColumn8.add(deleteAction);
        			
        			// Add container
        			c.fill = GridBagConstraints.BOTH;
        	        c.gridx = 9;
        	        c.gridy = currentRow;
        	        c.weightx = 0.0;
        			c.weighty = 0.0;
        			c.gridwidth = 1;
        			unitTable.add(tableColumn8, c);

        			/*
        			 * Increase row
        			 */
        			
        			currentRow++;
        		}
        		
        		/*
        		 * Lines Between Floors
        		 */
        		
        		int lastFloor = 
        				building.getAllFloors().get(building.getAllFloors().size() - 1).getNumber();
        		
        		// Don't draw line under the last floor of a building (only between)
        		if (floor.getNumber() != lastFloor) {
	        			JPanel floorLine = new JPanel();
	        		floorLine.setBackground(Theme.textThirdColor);
	        		floorLine.setPreferredSize(new Dimension(100, 1));
	                c.fill = GridBagConstraints.HORIZONTAL;
	        		c.weightx = 0.0;
	        		c.gridwidth = 9;
	                c.gridx = 1;
	                c.gridy = currentRow;
	                unitTable.add(floorLine, c);
	                currentRow++;
	                currentRow++;
        		}
        	}
        	
        	/*
        	 * Lines Between Buildings
        	 */
        	JPanel buildingLine = new JPanel();
        	buildingLine.setBackground(textColor);
        	buildingLine.setPreferredSize(new Dimension(100, 1));
            c.fill = GridBagConstraints.HORIZONTAL;
    		c.weightx = 0.0;
    		c.gridwidth = 9;
            c.gridx = 1;
            c.gridy = currentRow;
            unitTable.add(buildingLine, c);
            currentRow++;
            currentRow++;
        }
    	
    	/*
    	 * Push table to top
    	 */
    	
    	JPanel tableBottomSpacer = new JPanel();
    	tableBottomSpacer.setOpaque(false);
        c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = currentRow;
        unitTable.add(tableBottomSpacer, c);
        	
        /*
         * Make table scroll
         */
        
	    unitScroll = new JScrollPane(unitTable);
	    unitScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    unitScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    unitScroll.setBorder(new EmptyBorder(0,0,0,0));    
	    unitScroll.getVerticalScrollBar().setUnitIncrement(7);
	    unitScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
	    unitScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		panel.add(unitScroll, c);
	}
	
	/**
	 * Builds and returns a list of users not currently assigned to a unit.
	 * Used to add a resident to an empty unit.
	 * To be used as pop-up content.
	 * @param unit - Unit to add resident to
	 * @return A panel with a selected unit and a user selection
	 */
	private JPanel buildUnassignedUserPanel(Unit unit) {
		// Container
		JPanel popupPanel = new JPanel();
		popupPanel.setLayout(new GridBagLayout());
		popupPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		
		// Top text
		JLabel popupText = new JLabel("Select an account.", SwingConstants.CENTER);
		popupText.setFont(new Font("Arial", Font.PLAIN, 14));
		popupText.setForeground(textColor);
		popupText.setOpaque(false);
		c.insets = new Insets(0,0,10,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupText, c);
		
		/*
		 * Table
		 */
		
		// Table Container
		JPanel unassignedUserTable = new JPanel();
		unassignedUserTable.setBackground(Theme.popupColor);
		unassignedUserTable.setOpaque(true);
		unassignedUserTable.setLayout(new GridBagLayout());
		
		/*
		 * Table header
		 */
		
		// User name
		JLabel unassignedUserHeader0 = new JLabel("Username", SwingConstants.LEFT);
		unassignedUserHeader0.setFont(new Font("Arial", Font.BOLD, 14));
		unassignedUserHeader0.setForeground(textColor);
		unassignedUserHeader0.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		unassignedUserTable.add(unassignedUserHeader0, c);
		
		// Name
		JLabel unassignedUserHeader1 = new JLabel("Name", SwingConstants.LEFT);
		unassignedUserHeader1.setFont(new Font("Arial", Font.BOLD, 14));
		unassignedUserHeader1.setForeground(textColor);
		unassignedUserHeader1.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		unassignedUserTable.add(unassignedUserHeader1, c);
		
		// Header underline
		JPanel headerLine = new JPanel();
        headerLine.setBackground(textColor);
        headerLine.setPreferredSize(new Dimension(1, 1));
        c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        unassignedUserTable.add(headerLine, c);
		
        /*
         * Table rows
         */
        
        // Loop
		int unassignedUserCurrentRow = 2;
		for (Resident r : users.getAllUnassigned()) {
			String name = r.getName();
			String username = r.getUsername();
			
			/*
			 * Username Selection
			 */

	        CustomButton residentSelection = new CustomButton(username, 
	        		Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor);
			residentSelection.setFont(new Font("Arial", Font.BOLD, 14));
			residentSelection.setBorderPainted(false);
			residentSelection.setForeground(Color.WHITE);
			residentSelection.setFocusPainted(false);
			residentSelection.setBorder(new EmptyBorder(0, 4, 0, 4));
			residentSelection.setHorizontalAlignment(SwingConstants.LEFT);
			residentSelection.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// Hide previous pop-up
					unassignedUserPanel.setVisible(false);
					
					// Bring up pop-up for lease
					unassignedUserDate = new PopupWindow(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								
								// If user answers yes
								if (e.getActionCommand().equals("OK")) {
									
									// Check if date and rent entries are all numbers
									LocalDate validStartDateEntry = 
											validateDateEntry(leaseStartEntry.getText());
									LocalDate validEndDateEntry = 
											validateDateEntry(leaseEndEntry.getText());
									
									// If the entry is valid
									if (!(validStartDateEntry == null || validEndDateEntry == null)
											|| rentAmountEntry.getText().matches("[0-9]+") ) {
										int validRentAmountEntry = Integer.valueOf(rentAmountEntry.getText());
										
										// Link user to unit
										r.setUnit(unit, validStartDateEntry, validEndDateEntry, validRentAmountEntry);
										unit.setResident(r);
										refreshAll();
										
										//Close user selection
										unassignedUserPanel.dispose();
										unassignedUserPanel = null;
										
										// Close lease window
										unassignedUserDate.setVisible(false);
										unassignedUserDate.dispose();
										unassignedUserDate = null;
									}
								}
								
								// If user answers no
								if (e.getActionCommand().equals("Cancel")) {
									unassignedUserPanel.setVisible(true);
								}
							}
							
						}, buildUnassignedDatePanel(r, unit),"cancelok") {
						
						// Don't close when OK is clicked
						@Override
						public void actionPerformed(ActionEvent e) {
							if (e.getActionCommand().equalsIgnoreCase("ok")) {}
							if (e.getActionCommand().equalsIgnoreCase("cancel")) {
								setVisible(false);
								dispose();
							}
						}
						
					};
				}
	        	
	        });
			c.insets = new Insets(0,0,0,0);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			c.gridwidth = 1;
	        c.gridx = 0;
	        c.gridy = unassignedUserCurrentRow;
	        unassignedUserTable.add(residentSelection, c);
	        
	        /*
	         * Account name
	         */
	        
	        JLabel unassignedName = new JLabel(name, SwingConstants.LEFT);
			unassignedName.setFont(new Font("Arial", Font.PLAIN, 14));
			unassignedName.setForeground(textColor);
			unassignedName.setBorder(new EmptyBorder(0, 4, 0, 4));
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			c.gridwidth = 1;
	        c.gridx = 1;
	        c.gridy = unassignedUserCurrentRow;
	        unassignedUserTable.add(unassignedName, c);
	        
	        // Set height
	        JPanel rowHeightFiller = new JPanel();
	        rowHeightFiller.setOpaque(false);
	        rowHeightFiller.setPreferredSize(new Dimension(1, tableCellHeight));
	        c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			c.gridwidth = 1;
	        c.gridx = 0;
	        c.gridy = unassignedUserCurrentRow;
	        unassignedUserTable.add(rowHeightFiller, c);
			
	        /*
	         * Increase row
	         */
	        
	        unassignedUserCurrentRow++;
		}
		
		/*
		 * Table scroll
		 */
		
		JScrollPane unassignedUserScroll = new JScrollPane(unassignedUserTable);
		unassignedUserScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		unassignedUserScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    unassignedUserScroll.setBorder(new EmptyBorder(0,0,0,0));
	    unassignedUserScroll.setBackground(Theme.popupColor);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(unassignedUserScroll, c);
		
		return popupPanel;
	}
	
	/**
	 * Builds and returns a panel with lease creation fields.
	 * Used to add a resident to an empty unit.
	 * To be used as pop-up content.
	 * @param resident - Resident selected in previous window.
	 * @param unit - Unit to add resident to
	 * @return A panel with lease information fields
	 */
	private JPanel buildUnassignedDatePanel(User resident, Unit unit) {
		// Container
		JPanel popupPanel = new JPanel();
		popupPanel.setLayout(new GridBagLayout());
		popupPanel.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		
		/*
		 * Labels
		 */
		
		// Account text
		JLabel popupTextAccountLabel = new JLabel("Account", SwingConstants.LEFT);
		popupTextAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		popupTextAccountLabel.setForeground(textColor);
		popupTextAccountLabel.setOpaque(false);
		c.insets = new Insets(0,0,0,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupTextAccountLabel, c);
		
		// Building label
		JLabel popupTextBuildingLabel = new JLabel("Building", SwingConstants.LEFT);
		popupTextBuildingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		popupTextBuildingLabel.setForeground(textColor);
		popupTextBuildingLabel.setOpaque(false);
		c.insets = new Insets(0,0,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupTextBuildingLabel, c);
		
		// Unit label
		JLabel popupTextUnitLabel = new JLabel("Unit", SwingConstants.LEFT);
		popupTextUnitLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		popupTextUnitLabel.setForeground(textColor);
		popupTextUnitLabel.setOpaque(false);
		c.insets = new Insets(0,0,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupTextUnitLabel, c);
		
		/*
		 * Info display
		 */
		
		// Account person name
		JLabel popupTextName = new JLabel(resident.getName(), SwingConstants.LEFT);
		popupTextName.setFont(new Font("Arial", Font.PLAIN, 16));
		popupTextName.setForeground(textColorTitle);
		popupTextName.setOpaque(false);
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupTextName, c);
		
		// Account user name
		JLabel popupTextUsername = new JLabel(resident.getUsername(), SwingConstants.LEFT);
		popupTextUsername.setFont(new Font("Arial", Font.PLAIN, 10));
		popupTextUsername.setForeground(textColorTitle);
		popupTextUsername.setOpaque(false);
		c.insets = new Insets(0,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupTextUsername, c);
				
		// Building name
		JLabel popupTextBuilding = new JLabel(unit.getBuildingName(), SwingConstants.LEFT);
		popupTextBuilding.setFont(new Font("Arial", Font.PLAIN, 14));
		popupTextBuilding.setForeground(textColorTitle);
		popupTextBuilding.setOpaque(false);
		c.insets = new Insets(0,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupTextBuilding, c);
		
		// Unit
		JLabel popupTextUnit = new JLabel(unit.getUnitNumber(), SwingConstants.LEFT);
		popupTextUnit.setFont(new Font("Arial", Font.PLAIN, 16));
		popupTextUnit.setForeground(textColorTitle);
		popupTextUnit.setOpaque(false);
		c.insets = new Insets(0,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(popupTextUnit, c);
		
		/*
		 * Entry Labels
		 */
		
		// Lease Start
		JLabel leaseStartLabel = new JLabel("Lease Start", SwingConstants.LEFT);
		leaseStartLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		leaseStartLabel.setForeground(textColor);
		leaseStartLabel.setOpaque(false);
		c.insets = new Insets(0,0,10,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(leaseStartLabel, c);
		
		// Lease End
		JLabel leaseEndLabel = new JLabel("Lease End", SwingConstants.LEFT);
		leaseEndLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		leaseEndLabel.setForeground(textColor);
		leaseEndLabel.setOpaque(false);
		c.insets = new Insets(0,0,10,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(leaseEndLabel, c);
		
		// Rent Amount
		JLabel rentLabel = new JLabel("Rent Amount", SwingConstants.LEFT);
		rentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		rentLabel.setForeground(textColor);
		rentLabel.setOpaque(false);
		c.insets = new Insets(0,0,10,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(rentLabel, c);
		
		/*
		 * Entry Boxes
		 */
		
		// Lease start
		leaseStartEntry = new JTextField(8);
		leaseStartEntry.setFont(new Font("Arial", Font.PLAIN, 14));
		leaseStartEntry.setForeground(textColor);
		leaseStartEntry.setBackground(Theme.popupFieldColor);
		leaseStartEntry.setBorder(new EmptyBorder(0,4,0,4));
		leaseStartEntry.setMargin(new Insets(0,4,0,4));
		leaseStartEntry.setCaretColor(fieldText);
		leaseStartEntry.setText(defaultDateText);
		leaseStartEntry.addFocusListener(new FocusListener() {
		    public void focusGained(FocusEvent e) {
		    	
		    	if (leaseStartEntry.getText().equals(defaultDateText)) {
		    		leaseStartEntry.setText("");
		    		leaseStartEntry.setForeground(fieldText);
		    	}   
		    	
		    }
		    public void focusLost(FocusEvent e) {
		        if (leaseStartEntry.getText().isEmpty()) {
		        	leaseStartEntry.setText(defaultDateText);
		        	leaseStartEntry.setForeground(textColor);
		        }  
		    }
		});
		c.insets = new Insets(0,0,10,0);
		c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		popupPanel.add(leaseStartEntry, c);
		
		// Lease end
		leaseEndEntry = new JTextField(8);
		leaseEndEntry.setFont(new Font("Arial", Font.PLAIN, 14));
		leaseEndEntry.setForeground(textColor);
		leaseEndEntry.setBackground(Theme.popupFieldColor);
		leaseEndEntry.setBorder(new EmptyBorder(0,4,0,4));
		leaseEndEntry.setMargin(new Insets(0,4,0,4));
		leaseEndEntry.setCaretColor(fieldText);
		leaseEndEntry.setText(defaultDateText);
		leaseEndEntry.addFocusListener(new FocusListener() {
		    public void focusGained(FocusEvent e) {
		    	if (leaseEndEntry.getText().equals(defaultDateText)) {
		    		leaseEndEntry.setText("");
		    		leaseEndEntry.setForeground(fieldText);
		    	}   
		    }
		    public void focusLost(FocusEvent e) {
		        if (leaseEndEntry.getText().isEmpty()) {
		        	leaseEndEntry.setText(defaultDateText);
		        	leaseEndEntry.setForeground(textColor);
		        }  
		    }
		});
		c.insets = new Insets(0,0,10,0);
		c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(leaseEndEntry, c);
		
		// Rent amount
		rentAmountEntry = new JTextField(8);
		rentAmountEntry.setFont(new Font("Arial", Font.PLAIN, 14));
		rentAmountEntry.setForeground(fieldText);
		rentAmountEntry.setBackground(Theme.popupFieldColor);
		rentAmountEntry.setBorder(new EmptyBorder(0,4,0,4));
		rentAmountEntry.setMargin(new Insets(0,4,0,4));
		rentAmountEntry.setCaretColor(fieldText);
		c.insets = new Insets(0,0,10,0);
		c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 6;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		popupPanel.add(rentAmountEntry, c);
		
		return popupPanel;
	}
	
	/*
	 * New Unit Panel
	 */
	
	/**
	 * Fills a panel with a form to add a new unit to the unit list.
	 * @param panel - empty theme panel
	 * @return A panel with a new unit form and submit button.
	 */
	private JPanel buildNewUnit(JPanel panel) {
		// Container
		newUnitContent.setLayout(new GridBagLayout());
		newUnitContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		newUnitContent.setOpaque(true);
		newUnitContent.setBackground(paneColor);
		
		GridBagConstraints c = new GridBagConstraints();
		Dimension textFieldSize = new Dimension(200, 1);
        
		// Name label
		JLabel newResidentNameLabel = new JLabel("Unit Number", SwingConstants.LEFT);
        newResidentNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentNameLabel.setForeground(textColor);
		newResidentNameLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(fieldMargin,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
		newUnitContent.add(newResidentNameLabel, c);
		
		// Name text field
		unitNumber = new JTextField();
		unitNumber.setFont(new Font("Arial", Font.PLAIN, 14));
		unitNumber.setBackground(fieldColor);
		unitNumber.setForeground(fieldText);
		unitNumber.setCaretColor(fieldText);
		unitNumber.setMinimumSize(textFieldSize);
		unitNumber.setPreferredSize(textFieldSize);
		unitNumber.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(fieldMargin,fieldMargin,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 1;
		newUnitContent.add(unitNumber, c);
		
		// Building label
		JLabel buildingEntryLabel = new JLabel("Building", SwingConstants.LEFT);
		buildingEntryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		buildingEntryLabel.setForeground(textColor);
		buildingEntryLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 2;
		newUnitContent.add(buildingEntryLabel, c);
		
		// Building text field
		building = new JTextField();
		building.setFont(new Font("Arial", Font.PLAIN, 14));
		building.setBackground(fieldColor);
		building.setForeground(fieldText);
		building.setCaretColor(fieldText);
		building.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,fieldMargin,fieldMargin,fieldMargin);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 2;
		newUnitContent.add(building, c);
		
		// Floor label
		JLabel newResidentUsernameLabel = new JLabel("Floor (int)", SwingConstants.LEFT);
		newResidentUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentUsernameLabel.setForeground(textColor);
		newResidentUsernameLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(fieldMargin,fieldMargin,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 2;
        c.gridy = 1;
		newUnitContent.add(newResidentUsernameLabel, c);
		
		// Floor Text Field
		floor = new JTextField();
		floor.setFont(new Font("Arial", Font.PLAIN, 14));
		floor.setBackground(fieldColor);
		floor.setForeground(fieldText);
		floor.setCaretColor(fieldText);
		floor.setMinimumSize(textFieldSize);
		floor.setPreferredSize(textFieldSize);
		floor.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(fieldMargin,fieldMargin,fieldMargin,0);
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 3;
        c.gridy = 1;
		newUnitContent.add(floor, c);
		
		// Floor Area
		JLabel floorAreaEntryLabel = new JLabel("Floor Area (sqft)", SwingConstants.LEFT);
		floorAreaEntryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		floorAreaEntryLabel.setForeground(textColor);
		floorAreaEntryLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,fieldMargin,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 2;
        c.gridy = 2;
		newUnitContent.add(floorAreaEntryLabel, c);
		
		// Floor Area text field
		floorArea = new JTextField(1);
		floorArea.setFont(new Font("Arial", Font.PLAIN, 14));
		floorArea.setBackground(fieldColor);
		floorArea.setForeground(fieldText);
		floorArea.setCaretColor(fieldText);
		floorArea.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,fieldMargin,fieldMargin,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 3;
        c.gridy = 2;
		newUnitContent.add(floorArea, c);
		
 		/*
 		 * Add unit button
 		 */
 		
		// Container
 		JPanel addUnitSubmit = new JPanel();
 		addUnitSubmit.setLayout(new GridBagLayout());
 		addUnitSubmit.setOpaque(false);
 		
 		// Submit Button
		CustomButton submitUnitButton = new CustomButton("Add Resident", Theme.buttonColor, Theme.hoverColor, Theme.pressedColor); 
        submitUnitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitUnitButton.setBorderPainted(false);
        submitUnitButton.setForeground(textColorTitle);
        submitUnitButton.setFocusPainted(false);
        submitUnitButton.setMargin(new Insets(-2,4,-2,4));
        submitUnitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// If all fields filled
				if (validateNewUnitEntry()) {
					new PopupWindow(new ActionListener() {
	
						@Override
						public void actionPerformed(ActionEvent e) {
							
							// If user answers yes
							if (e.getActionCommand().equals("Yes")) {
								
								// Create new unit
								Unit newUnit = new Unit(unitNumber.getText(), 
										building.getText(), 
										Integer.valueOf(floor.getText()), 
										Integer.valueOf(floorArea.getText())
										);
								
								// If unit successfully added
								if (buildingList.addUnit(newUnit)) {
									refreshAll();
								}
								
								// Clear fields
								clearNewEntry();
							}
						}
						
					}, "Are you sure you want to add this Unit?", "yesno");
				}
			}
			
	    });
        c.insets = new Insets(0,0,0,0);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
     	c.gridwidth = 1;
        c.weightx = 0.00;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        addUnitSubmit.add(submitUnitButton, c);
		
        // Add button container to panel
		c.fill = GridBagConstraints.HORIZONTAL;
     	c.gridwidth = 4;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 4;
        c.gridheight = 1;
		newUnitContent.add(addUnitSubmit, c);
		
		/*
         * Add content to panel
         */
		
		panel.add(newUnitContent);
		return panel;
	}
	
	/**
	 * Checks that all forms in the add unit form are filled and that the
	 * floor and floor area entered are only made up of integers.
	 * @return true if entry is valid
	 */
	private boolean validateNewUnitEntry() { //TODO Add length limits to fields
		// Check if all fields are filled
		if (!(unitNumber.getText().isBlank() 
				|| building.getText().isBlank()
				|| floor.getText().isBlank()
				|| floorArea.getText().isBlank())
				&& floor.getText().matches("[0-9]+")
				&& floorArea.getText().matches("[0-9]+")) {
			return true;	
		}	
		return false;
	}
	
	/**
	 * Clears entry text fields for New Unit
	 */
	private void clearNewEntry() {
		unitNumber.setText("");
		building.setText("");
		floor.setText("");
		floorArea.setText("");
	}

	/*
	 * Refresh Page Methods
	 */
	
	@Override
	public void refreshAll() {
		refreshSummary(summaryPanel, summaryPanelContent);
		refreshTable(allUnitsPanelContent, unitScroll);
	}
	
	/**
	 * Removes the Summary panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param summary - Content
	 */
	private void refreshSummary(JPanel panel, Component summary) {
		panel.remove(summary);
		buildSummaryPanel(panel);
		panel.revalidate();
	}
	
	/**
	 * Removes the Unit table panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param table - Content
	 */
	private void refreshTable(JPanel panel, Component table) {
		panel.remove(table);
		buildUnitTable(panel);
		panel.validate();
	}
}

