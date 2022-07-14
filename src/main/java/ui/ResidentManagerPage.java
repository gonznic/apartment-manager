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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.java.Page;
import main.java.Session;

/**
 * A class that creates a Resident Manager page consisting of three panels:
 * Filter, Residents, and New Resident
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class ResidentManagerPage extends JPanel implements Page {
	
	//Setup
	private Session currentSession;
	private String title = "Resident Manager";
	
	// Panels
	private JPanel filterPanel;
	private JPanel residentAccountsPanel;
	private JPanel addResidentPanel;
	
	// Search Fields
	private JTextField nameUsername;
	private JTextField buildingSearch;
	private JTextField unitSearch;
	
	// Query
	private String searchedName = "";
	private String searchedBuilding = "";
	private String searchedUnit = "";
	
	// Search Filters
	private CustomButton activeButton;
	private CustomButton inactiveButton;
	private CustomButton newButton;
	private boolean activeSelected = false;
	private boolean inactiveSelected = false;
	private boolean newSelected = false;
	
	// New Resident Panel
	private JTextField newResidentName;
	private JTextField newResidentEmail;
	private JTextField newResidentPhone;
	private JTextField newResidentUsername;
	private JTextField newResidentPassword;
	
	// Minimize
	private JPanel newResidentContent;
	
	// Refresh
	private JPanel searchPanelContent;
	private JPanel allResidentsPanelContent;
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
	 * Creates a Resident manager page with a search panel, a panel where residents 
	 * are displayed, and a panel to add a new resident.
	 * @param users - List of user accounts
	 * @param buildingList - List of buildings
	 */
	public ResidentManagerPage(Session session) {
		
		// Setup
		this.setLayout(new BorderLayout());
		this.setBackground(backgroundColor);
		this.currentSession = session;
		
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
        
        // Filter
        filterPanel = buildSearchPanel(buildPanel("Filter"));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(PANE_MARGIN,0,0,0);
        tabContent.add(filterPanel, c);

        // Residents
        residentAccountsPanel = buildAllResidents(buildPanel("Residents"));
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(PANE_MARGIN,0,0,0);
        tabContent.add(residentAccountsPanel, c);
        
        // Initialize New Resident Content (to minimize)
        newResidentContent = new JPanel();
        newResidentContent.setVisible(false);
        
        // New Resident (collapsible)
        addResidentPanel = buildNewResidentForm(buildPanel("New Resident", true, newResidentContent));
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(PANE_MARGIN,0,0,0);
        tabContent.add(addResidentPanel, c);
	}

	/*
	 * Search Panel Methods
	 */
	
	/**
	 * Fills a panel with a search bar.
	 * The search bar has a name/username fields, building field, 
	 * unit field, and account status selectors.
	 * @param panel - Empty theme panel
	 * @return A panel with a search bar
	 */
	private JPanel buildSearchPanel(JPanel panel) {
		// Container
		searchPanelContent = new JPanel();
		searchPanelContent.setLayout(new GridBagLayout());
		searchPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		searchPanelContent.setOpaque(true);
		searchPanelContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
		
		/*
		 * Frame
		 */
	
		double searchHorPaddingWeight = 0.05;
		
		JPanel searchLeftPadding = new JPanel();
		searchLeftPadding.setOpaque(false);
		c.gridx = 0;
        c.gridy = 1;
        c.weightx = searchHorPaddingWeight;
		c.weighty = 0;
		c.gridwidth = 1;
		searchPanelContent.add(searchLeftPadding, c);
		
		JPanel searchRightPadding = new JPanel();
		searchRightPadding.setOpaque(false);
		c.gridx = 4;
		searchPanelContent.add(searchRightPadding, c);
		
		/*
		 * Content
		 */
		
		// Search Bar Container
		JPanel searchBarPanel = new JPanel();
		searchBarPanel.setLayout(new GridBagLayout());
		searchBarPanel.setOpaque(false);
		
		// Username Label
		JLabel nameUsernameLabel = new JLabel("Name/Username", SwingConstants.LEFT);
		nameUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		nameUsernameLabel.setForeground(textColor);
		nameUsernameLabel.setBorder(new EmptyBorder(4, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 3;
		searchBarPanel.add(nameUsernameLabel, c);
		
		// Building Label
		JLabel buildingLabel = new JLabel("Building", SwingConstants.LEFT);
		buildingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		buildingLabel.setForeground(textColor);
		buildingLabel.setBorder(new EmptyBorder(4, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		searchBarPanel.add(buildingLabel, c);
		
		// Unit label
		JLabel unitLabel = new JLabel("Unit", SwingConstants.LEFT);
		unitLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		unitLabel.setForeground(textColor);
		unitLabel.setBorder(new EmptyBorder(4, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 4;
        c.gridy = 0;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 1;
		searchBarPanel.add(unitLabel, c);
		
		/*
		 * Name/User Name Search
		 */
		// Search text size
		int searchBarSpacing = 5;
		int searchBarHeight = 30;
		int searchTextSize = searchBarHeight - 6;
		
		// Name text field
		Dimension textFieldSize = new Dimension(200, searchBarHeight);
		nameUsername = new JTextField();
		nameUsername.setBackground(fieldColor);
		nameUsername.setCaretColor(fieldText);
		nameUsername.setMinimumSize(textFieldSize);	// Sets column size
		nameUsername.setPreferredSize(textFieldSize);
		nameUsername.setBorder(new EmptyBorder(0, 4, 0, 4));
		nameUsername.setText("Type a name or username...");
		nameUsername.setForeground(textColor);
		nameUsername.setFont(new Font("Arial", Font.PLAIN, searchBarHeight/2));
		
		if (!searchedName.equals("")) {
			nameUsername.setText(searchedName);
			nameUsername.setForeground(textColorTitle);
			nameUsername.setFont(new Font("Arial", Font.PLAIN, searchTextSize));
		}
		
		nameUsername.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!nameUsername.getText().equals("Type a name or username...")) {
					searchedName = nameUsername.getText();
				}
				else searchedName = "";
				
				refreshTable();
			}
			
		});
		nameUsername.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (nameUsername.getText().equals("Type a name or username...")) {
					nameUsername.setText("");
					nameUsername.setForeground(textColorTitle);
					nameUsername.setFont(new Font("Arial", Font.PLAIN, searchTextSize));
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				
				if (nameUsername.getText().isBlank()) {
					nameUsername.setText("Type a name or username...");
					nameUsername.setForeground(textColor);
					nameUsername.setFont(new Font("Arial", Font.PLAIN, searchBarHeight/2));
		        }	
			}
			
		});
		c.insets = new Insets(0,0,0,searchBarSpacing);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        searchBarPanel.add(nameUsername, c);
		
		/*
		 * Building Search
		 */
        
        //Building Search Bar Container
        JPanel buildingSearchContainer = new JPanel();
        buildingSearchContainer.setLayout(new GridBagLayout());
        buildingSearchContainer.setBackground(fieldColor);
        buildingSearchContainer.setPreferredSize(new Dimension(10,searchBarHeight));
        
        // Building text field
        buildingSearch = new JTextField();
        buildingSearch.setFont(new Font("Arial", Font.PLAIN, searchBarHeight/2));
        buildingSearch.setBackground(fieldColor);
        buildingSearch.setForeground(textColor);
        buildingSearch.setCaretColor(fieldText);
        buildingSearch.setBorder(new EmptyBorder(0, 4, 0, 4));
        buildingSearch.setText("Search buildings...");
        
        if (!searchedBuilding.equals("")) {
        	buildingSearch.setText(searchedBuilding);
        	buildingSearch.setForeground(textColorTitle);
        	buildingSearch.setFont(new Font("Arial", Font.PLAIN, searchTextSize));
		}
        
        buildingSearch.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!buildingSearch.getText().equals("Search buildings...")) {
					searchedBuilding = buildingSearch.getText();
				}
				else searchedBuilding = "";
				
				refreshTable();
			}
			
		});
        buildingSearch.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (buildingSearch.getText().equals("Search buildings...")) {
					buildingSearch.setText("");
					buildingSearch.setForeground(textColorTitle);
					buildingSearch.setFont(new Font("Arial", Font.PLAIN, searchTextSize));
				}
				refreshTable();
			}

			@Override
			public void focusLost(FocusEvent e) {
				
				if (buildingSearch.getText().isBlank()) {
					buildingSearch.setText("Search buildings...");
					buildingSearch.setForeground(textColor);
					buildingSearch.setFont(new Font("Arial", Font.PLAIN, searchBarHeight/2));
		        }	
				refreshTable();
			}
			
		});
        c.fill = GridBagConstraints.BOTH;
 		c.insets = new Insets(0,0,0,0);
 		c.weightx = 1.0;
 		c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        buildingSearchContainer.add(buildingSearch, c);
        
        // Add Building Search Bar Container
 		c.fill = GridBagConstraints.BOTH;
 		c.insets = new Insets(0,0,0,searchBarSpacing);
 		c.weightx = 0.0;
 		c.weighty = 0;
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        searchBarPanel.add(buildingSearchContainer, c);
//        searchBarPanel.add(buildingSearch, c);
		
		/*
		 * Unit Search Bar
		 */
        
        // Unit text field
        unitSearch = new JTextField();
        unitSearch.setBackground(fieldColor);
        unitSearch.setCaretColor(fieldText);
        unitSearch.setBorder(new EmptyBorder(0, 4, 0, 4));
    	unitSearch.setText(searchedUnit);
    	unitSearch.setForeground(textColorTitle);
    	unitSearch.setFont(new Font("Arial", Font.PLAIN, searchTextSize));
        unitSearch.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!unitSearch.getText().equals("")) {
					searchedUnit = unitSearch.getText();
				}
				else searchedUnit = "";
				
				refreshTable();
			}
			
		});
        c.insets = new Insets(0,0,0,0);
 		c.fill = GridBagConstraints.BOTH;
 		c.weightx = 0.00;
 		c.weighty = 0;
        c.gridx = 4;
        c.gridy = 1;
        c.gridwidth = 1;
        searchBarPanel.add(unitSearch, c);

		/*
		 * Clear Button
		 */
		
		// Clear Button Container
		JPanel clearButtonCell = new JPanel();
		clearButtonCell.setLayout(new GridBagLayout());
		clearButtonCell.setOpaque(true);
		clearButtonCell.setBackground(fieldColor);
		
		//Clear Button
		CustomButton clearButton = new CustomButton("Clear", 
				Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
		clearButton.setFont(new Font("Arial", Font.BOLD, 14));
		clearButton.setBorderPainted(false);
		clearButton.setForeground(textColorTitle);
		clearButton.setFocusPainted(false);
		clearButton.setBorder(new EmptyBorder(0, 4, 0, 4));
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchedName = "";
				searchedBuilding = "";
				searchedUnit = "";
				activeSelected = false;
				inactiveSelected = false;
				newSelected = false;
				nameUsername.setText("");
				buildingSearch.setText("");
				unitSearch.setText("");
				refreshAll();	
			}
			
        });
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		clearButtonCell.add(clearButton, c);
		
		// Add Clear Button container
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 5;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.insets = new Insets(0,searchBarSpacing,0,0);
		searchBarPanel.add(clearButtonCell, c);
		c.insets = new Insets(0,0,0,0);
		
		/*
		 * Status Filters
		 */
		
		// Status Filter buttons container
		JPanel statusSelectorsContainer = new JPanel();
		statusSelectorsContainer.setLayout(new GridBagLayout());
		statusSelectorsContainer.setOpaque(false);
		Dimension statusSelectorSize = new Dimension(70,30);
		
		// Active Status Container
		JPanel activeButtonCell = new JPanel();
		activeButtonCell.setLayout(new GridBagLayout());
		activeButtonCell.setOpaque(false);
		activeButtonCell.setBorder(new EmptyBorder(0, 4, 0, 4));
		
		// Active StatusButton
		activeButton = new CustomButton("Active", 
				Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
		activeButton.setFont(new Font("Arial", Font.BOLD, 16));
		activeButton.setBorderPainted(false);
		activeButton.setForeground(textColorTitle);
		activeButton.setFocusPainted(false);
		activeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		activeButton.setPreferredSize(statusSelectorSize);
		activeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (activeSelected) {
					// Deselect
					activeButton.setBackground(Theme.buttonColor);
					activeButton.setForeground(textColorTitle);
					activeButton.setHoverBackgroundColor(Theme.hoverColor);
					activeSelected = false;
				}
				else {
					//Light up
					activeButton.setBackground(Theme.buttonHold);
					activeButton.setForeground(Theme.buttonHoldText);
					activeButton.setHoverBackgroundColor(Theme.pressedHoverColor);
					activeSelected = true;
					
					if (newSelected && inactiveSelected) {
						activeButton.setBackground(Theme.buttonColor);
						activeButton.setForeground(textColorTitle);
						activeButton.setHoverBackgroundColor(Theme.hoverColor);
						
						inactiveButton.setBackground(Theme.buttonColor);
						inactiveButton.setForeground(textColorTitle);
						inactiveButton.setHoverBackgroundColor(Theme.hoverColor);
						
						newButton.setBackground(Theme.buttonColor);
						newButton.setForeground(textColorTitle);
						newButton.setHoverBackgroundColor(Theme.hoverColor);
						
						activeSelected = false;
						inactiveSelected = false;
						newSelected = false;
					}
				}
				refreshTable();		
			}
			
        });
		activeButtonCell.add(activeButton);
		
		// Add Active Status container
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		statusSelectorsContainer.add(activeButtonCell, c);
		
		// Inactive Status Container
		JPanel inactiveButtonCell = new JPanel();
		inactiveButtonCell.setLayout(new GridBagLayout());
		inactiveButtonCell.setOpaque(false);
		inactiveButtonCell.setBorder(new EmptyBorder(0, 4, 0, 4));
		
		// Inactive Status Button
		inactiveButton = new CustomButton("Inactive", Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
		inactiveButton.setFont(new Font("Arial", Font.BOLD, 16));
		inactiveButton.setBorderPainted(false);
		inactiveButton.setForeground(textColorTitle);
		inactiveButton.setFocusPainted(false);
		inactiveButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		inactiveButton.setPreferredSize(statusSelectorSize);
		inactiveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inactiveSelected) {
					// Deselect
					inactiveButton.setBackground(Theme.buttonColor);
					inactiveButton.setForeground(textColorTitle);
					inactiveButton.setHoverBackgroundColor(Theme.hoverColor);
					inactiveSelected = false;
				}
				else {
					//Light up
					inactiveButton.setBackground(Theme.buttonHold);
					inactiveButton.setForeground(Theme.buttonHoldText);
					inactiveButton.setHoverBackgroundColor(Theme.pressedHoverColor);
					inactiveSelected = true;
					
					if (activeSelected && newSelected) {
						activeButton.setBackground(Theme.buttonColor);
						activeButton.setForeground(textColorTitle);
						activeButton.setHoverBackgroundColor(Theme.hoverColor);
						
						inactiveButton.setBackground(Theme.buttonColor);
						inactiveButton.setForeground(textColorTitle);
						inactiveButton.setHoverBackgroundColor(Theme.hoverColor);
						
						newButton.setBackground(Theme.buttonColor);
						newButton.setForeground(textColorTitle);
						newButton.setHoverBackgroundColor(Theme.hoverColor);
						
						activeSelected = false;
						inactiveSelected = false;
						newSelected = false;
					}	
				}
				refreshTable();
			}
			
        });
		inactiveButtonCell.add(inactiveButton);
		
		// Add Inactive Status ontainer
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		statusSelectorsContainer.add(inactiveButtonCell, c);
		
		// New Status Container
		JPanel newButtonCell = new JPanel();
		newButtonCell.setLayout(new GridBagLayout());
		newButtonCell.setOpaque(false);
		newButtonCell.setBorder(new EmptyBorder(0, 4, 0, 4));
		
		// New Status Button
		newButton = new CustomButton("New", Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
		newButton.setFont(new Font("Arial", Font.BOLD, 16));
		newButton.setBorderPainted(false);
		newButton.setForeground(textColorTitle);
		newButton.setFocusPainted(false);
		newButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		newButton.setPreferredSize(statusSelectorSize);
		newButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newSelected) {
					// Deselect
					newButton.setBackground(Theme.buttonColor);
					newButton.setForeground(textColorTitle);
					newButton.setHoverBackgroundColor(Theme.hoverColor);
					newSelected = false;
				}
				else {
					
					//Light up
					newButton.setBackground(Theme.buttonHold);
					newButton.setForeground(Theme.buttonHoldText);
					newButton.setHoverBackgroundColor(Theme.pressedHoverColor);
					newSelected = true;
					
					if (activeSelected && inactiveSelected) {
						activeButton.setBackground(Theme.buttonColor);
						activeButton.setForeground(textColorTitle);
						activeButton.setHoverBackgroundColor(Theme.hoverColor);
						
						inactiveButton.setBackground(Theme.buttonColor);
						inactiveButton.setForeground(textColorTitle);
						inactiveButton.setHoverBackgroundColor(Theme.hoverColor);
						
						newButton.setBackground(Theme.buttonColor);
						newButton.setForeground(textColorTitle);
						newButton.setHoverBackgroundColor(Theme.hoverColor);
						
						
						activeSelected = false;
						inactiveSelected = false;
						newSelected = false;
					}	
				}
				refreshTable();
			}
			
        });
		newButtonCell.add(newButton);
		
		// Add New Status container
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		statusSelectorsContainer.add(newButtonCell, c);
		
		// Add Status Selectors container
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 3;
		searchBarPanel.add(statusSelectorsContainer, c);
		
		/*
		 * Building Selector
		 */
		
		ArrayList<String> buildingList = new ArrayList<String>();
		
		String sqlBuildingQuery = "SELECT `building` FROM units WHERE deleted = 0 GROUP BY `building`";

		// Load Data
        try (Connection connect = DriverManager.getConnection(dbUrl, currentSession.getUser(), currentSession.getAdminPass())) {
        	Statement statement = connect.createStatement();

			// Query
			ResultSet results = statement.executeQuery(sqlBuildingQuery);
			while (results.next()) buildingList.add(results.getString("building"));

			// Close Connection
			connect.close();
		} catch (SQLException err) {
			err.printStackTrace();
		}

		Dimension selectorContainerSize = new Dimension(1, 60);
		
		JPanel buildingSelectorContainer = new JPanel();
		buildingSelectorContainer.setMinimumSize(selectorContainerSize);
		buildingSelectorContainer.setPreferredSize(selectorContainerSize); //sets height
		buildingSelectorContainer.setLayout(new GridBagLayout());
		buildingSelectorContainer.setOpaque(false);
		
		// Building Selection List
		JPanel buildingSelection = new JPanel();
		buildingSelection.setOpaque(true);
		buildingSelection.setBackground(paneColor);
		buildingSelection.setLayout(new GridBagLayout());

		// Loop
		int buildingRow = 0;
		for (String b : buildingList) {

			// Building button
			CustomButton buildingButton = new CustomButton(b, Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
			buildingButton.setFont(new Font("Arial", Font.PLAIN, 14));
			buildingButton.setBorderPainted(false);
			buildingButton.setForeground(textColorTitle);
			buildingButton.setFocusPainted(false);
			buildingButton.setBorder(new EmptyBorder(2, 4, 2, 4));
			buildingButton.setHorizontalAlignment(SwingConstants.LEFT);
			buildingButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// Select building
					searchedBuilding = b;
					buildingSearch.setText(b);
	
					// Clear Unit
					searchedUnit = "";
					unitSearch.setText("");
					
					buildingSearch.setForeground(textColorTitle);
					buildingSearch.setFont(new Font("Arial", Font.PLAIN, searchTextSize));
					
					refreshTable();
				}
				
	        });
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0,0,1,0);
	        c.gridx = 0;
	        c.gridy = buildingRow;
	        c.weightx = 1.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			buildingSelection.add(buildingButton, c);
			
			buildingRow++;
		}
		
		// Push list up
		JPanel buildingSelectionSpace = new JPanel();
		buildingSelectionSpace.setBackground(paneColor);
		buildingSelectionSpace.setMinimumSize(MIN_SIZE);
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = buildingRow + 1;
        c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		buildingSelection.add(buildingSelectionSpace, c);
		
		// Building Selection Scroll
		JScrollPane buildingSelectorScroll = new JScrollPane(buildingSelection);
		buildingSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		buildingSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		buildingSelectorScroll.setBorder(new EmptyBorder(0,0,0,0)); 
		buildingSelectorScroll.setOpaque(false);
		buildingSelectorScroll.getVerticalScrollBar().setUnitIncrement(7);
		buildingSelectorScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
		buildingSelectorScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		c.insets = new Insets(0,0,0,searchBarSpacing);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		buildingSelectorContainer.add(buildingSelectorScroll, c);
		
		// Add Building Selection container
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 2;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		searchBarPanel.add(buildingSelectorContainer, c);
					
		/*
		 * Unit Selector (Older version)
		 */
		
//		ArrayList<String> unitList = new ArrayList<String>();
//		
//		String sqlUnitQuery = "SELECT `unit` FROM units WHERE `building` LIKE '%" + searchedBuilding+ "%'";
//
//		// Load Data
//        try (Connection connect = DriverManager.getConnection(dbUrl, currentSession.getUser(), currentSession.getAdminPass())) {
//        	Statement statement = connect.createStatement();
//
//			// Query
//			ResultSet results = statement.executeQuery(sqlUnitQuery);
//			while (results.next()) {
//				unitList.add(results.getString("unit"));
//			}
//
//			// Close Connection
//			connect.close();
//		} catch (SQLException err) {
//			err.printStackTrace();
//		}
//		
//		// Floor Selection Container
//		JPanel floorSelectorContainer = new JPanel();
//		floorSelectorContainer.setMinimumSize(selectorContainerSize);
//		floorSelectorContainer.setPreferredSize(selectorContainerSize); //sets height
//		floorSelectorContainer.setLayout(new GridBagLayout());
//		floorSelectorContainer.setOpaque(true);
//		floorSelectorContainer.setBackground(Color.RED);
//		
//		// Floor Selection Table
//		JPanel floorSelection = new JPanel();
//		floorSelection.setOpaque(true);
//		floorSelection.setLayout(new GridBagLayout());
//		floorSelection.setBackground(paneColor);
//		
//		int unitRow = 0;
//		for (String u : unitList) {
//			
//			// Build button
//			CustomButton floorButton = new CustomButton(u, Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
//			floorButton.setFont(new Font("Arial", Font.PLAIN, 14));
//			floorButton.setBorderPainted(false);
//			floorButton.setForeground(textColorTitle);
//			floorButton.setFocusPainted(false);
//			floorButton.setBorder(new EmptyBorder(2, 4, 2, 4));
//			floorButton.setHorizontalAlignment(SwingConstants.LEFT);
//			floorButton.addActionListener(new ActionListener() {
//				
//				@Override
//				public void actionPerformed(ActionEvent e) {
//						searchedUnit = u;
//						unitSearch.setText(u);
//
//						refreshTable();	
//				}
//				
//	        });
//			c.fill = GridBagConstraints.HORIZONTAL;
//			c.insets = new Insets(0,0,1,0);
//	        c.gridx = 0;
//	        c.gridy = unitRow;
//	        c.weightx = 1.0;
//			c.weighty = 0.0;
//			c.gridwidth = 1;
//			floorSelection.add(floorButton, c);
//			
//			unitRow++;
//		}
//		
//		// Push list up
//		JPanel floorSelectionSpace = new JPanel();
//		floorSelectionSpace.setBackground(paneColor);
//		floorSelectionSpace.setMinimumSize(MIN_SIZE);
//		c.insets = new Insets(0,0,0,0);
//		c.fill = GridBagConstraints.BOTH;
//        c.gridx = 0;
//        c.gridy = unitRow + 1;
//        c.weightx = 0.0;
//		c.weighty = 1.0;
//		c.gridwidth = 1;
//		floorSelection.add(floorSelectionSpace, c);
//		
//		// Floor Selection scroll
//		JScrollPane floorSelectorScroll = new JScrollPane(floorSelection);
//		floorSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		floorSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		floorSelectorScroll.setBorder(new EmptyBorder(0,0,0,0)); 
//		floorSelectorScroll.setOpaque(false);
//		floorSelectorScroll.getVerticalScrollBar().setUnitIncrement(7);
//		floorSelectorScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
//		floorSelectorScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
//		c.insets = new Insets(0,0,0,0);
//        c.fill = GridBagConstraints.BOTH;
//        c.gridx = 0;
//        c.gridy = 0;
//        c.weightx = 1.0;
//		c.weighty = 1.0;
//		c.gridwidth = 1;
//		floorSelectorContainer.add(floorSelectorScroll, c);
//		
//		// Add Floor Selection Container
//		c.insets = new Insets(0,0,0,0);
//		c.fill = GridBagConstraints.BOTH;
//        c.gridx = 4;
//        c.gridy = 2;
//        c.weightx = 0.0;
//		c.weighty = 0.0;
//		c.gridwidth = 1;
//		searchBarPanel.add(floorSelectorContainer, c);
		
		/*
		 * Add Search Content Container
		 */
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		searchPanelContent.add(searchBarPanel, c);
		
        /*
         * Add content to panel
         */
		
		panel.add(searchPanelContent);
		return panel;
	}
	
	/*
	 * All Residents Panel
	 */
	
	/**
	 * Fills and returns a panel with a residents table.
	 * @param panel - Empty theme panel
	 * @return A panel with a table of residents
	 */
	private JPanel buildAllResidents(JPanel panel) {
		allResidentsPanelContent = new JPanel();
		allResidentsPanelContent.setLayout(new GridBagLayout());
		allResidentsPanelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		allResidentsPanelContent.setOpaque(true);
		
		buildResidentTable(allResidentsPanelContent);
		
		panel.add(allResidentsPanelContent);
		return panel;
	}
	
	/**
	 * Fills the input panel with a Residents table.
	 * Creates a scroll table with Resident information
	 * @param panel - Panel to build the Resident table on
	 */
	private void buildResidentTable(JPanel panel) {
		// Container
		JPanel residentTable = new JPanel();
	    residentTable.setLayout(new GridBagLayout());
	    residentTable.setOpaque(true);
	    residentTable.setBackground(paneColor);
	    residentTable.setAlignmentX(Component.LEFT_ALIGNMENT);
	    GridBagConstraints c = new GridBagConstraints();
	    
	    // Left space
	    JPanel leftSpacer = new JPanel();
	    leftSpacer.setOpaque(false);
    	c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        residentTable.add(leftSpacer, c);
        
        // Right space
	    JPanel rightSpacer = new JPanel();
	    rightSpacer.setOpaque(false);
    	c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.1;
		c.weighty = 0.0;
        c.gridx = 10;
        c.gridy = 0;
        residentTable.add(rightSpacer, c);
	    
	    /*
	     * Table header
	     */
	    
        // Account
	    JLabel columnHeader0 = new JLabel("Account", SwingConstants.LEFT);
		columnHeader0.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader0.setForeground(textColor);
		columnHeader0.setBorder(new EmptyBorder(4, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.1;
		c.weighty = 0.0;
		c.gridwidth = 3;
		residentTable.add(columnHeader0, c);
		
		// Unit
		JLabel columnHeader2 = new JLabel("Unit", SwingConstants.LEFT);
		columnHeader2.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader2.setForeground(textColor);
		columnHeader2.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 0.1;
        c.gridx = 4;
        c.gridy = 0;
        residentTable.add(columnHeader2, c);
        
        // Lease
        JLabel columnHeader3 = new JLabel("Lease", SwingConstants.LEFT);
		columnHeader3.setFont(new Font("Arial", Font.PLAIN, 14));
		columnHeader3.setForeground(textColor);
		columnHeader3.setPreferredSize(new Dimension(65, TABLE_CELL_HEIGHT));
		columnHeader3.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.weightx = 0.1;
		c.gridwidth = 3;
        c.gridx = 5;
        c.gridy = 0;
        residentTable.add(columnHeader3, c);
   
        // Payment
        JLabel columnSize6 = new JLabel("Payment", SwingConstants.LEFT);
		columnSize6.setFont(new Font("Arial", Font.PLAIN, 14));
		columnSize6.setForeground(textColor);
		columnSize6.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.gridwidth = 1;
        c.gridx = 8;
        c.gridy = 0;
        residentTable.add(columnSize6, c);
        
        
        // Header underline
        JPanel headerLine = new JPanel();
        headerLine.setBackground(textColor);
        headerLine.setPreferredSize(new Dimension(100, 1));
        c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.gridwidth = 9;
        c.gridx = 1;
        c.gridy = 1;
        residentTable.add(headerLine, c);
        
        /*
         * Search
         */
        
        ArrayList<Object[]> users = new ArrayList<Object[]>();
        
        // Prepare query
		String sqlQuery = """
				SELECT * FROM (
				SELECT `uid`,`user`,`email`,`phone`,`first_name`,`last_name`,`start`,`recent_lease_end`,`unit`,`building`,`sum_charges`,`sum_payments`
				FROM (SELECT * FROM `users` WHERE users.type = 'general' AND deleted = 0) AS u 
				
				    LEFT JOIN (SELECT `start`, MAX(`end`) AS 'recent_lease_end', `unitid`, `userid` FROM `leases` GROUP BY `userid`) AS l
				    ON u.uid = l.userid
				    
				    LEFT JOIN (SELECT `id`,`unit`,`building` FROM `units`) AS u2
				    ON l.unitid = u2.id
				    
				    LEFT JOIN (SELECT SUM(`charges`.`amount`) as sum_charges, charges.userid FROM charges WHERE charges.date <= CURRENT_DATE GROUP BY `userid`) as c
				    ON u.uid = c.userid
				    
				    LEFT JOIN (SELECT SUM(`payments`.`amount`) as sum_payments, payments.userid FROM payments GROUP BY `userid`) as p
				    ON u.uid = p.userid
				    
				) AS resident_manager 
				"""
				+ "WHERE "
				
				// Name Search
				+ "(`first_name` LIKE '%" + searchedName + "%' OR `last_name` LIKE '%" + searchedName + "%' OR `user` LIKE '%" + searchedName + "%') "
				
				// Filter
				+ ((activeSelected || inactiveSelected || newSelected) ? (
				
						((!activeSelected) ? "AND (`recent_lease_end` < CURRENT_DATE() OR `recent_lease_end` IS NULL) ":"")
						+ ((!inactiveSelected) ? "AND (`recent_lease_end` >= CURRENT_DATE() OR `recent_lease_end` IS NULL) ":"")
						
					) : "")
				
				// Unit Search
				+ ((searchedUnit.equals("")) ? "AND (unit LIKE '%%' OR unit IS NULL) " : ("AND unit LIKE '%" + searchedUnit + "%' "))
				
				// Building Search
				+ ((searchedBuilding.equals("")) ? "AND (building LIKE '%%' OR unit IS NULL) " : ("AND building LIKE '%" + searchedBuilding + "%' "))
				
				
				+ ((activeSelected || inactiveSelected || newSelected) ? (
					((!newSelected) ? "AND `unit` IS NOT NULL ":"")) : "")
		
		
				+ "ORDER BY `resident_manager`.`user` ASC";
		
        // Load Data
        try (Connection connect = DriverManager.getConnection(dbUrl, currentSession.getUser(), currentSession.getAdminPass())) {
        	Statement statement = connect.createStatement();

			// Query
			ResultSet results = statement.executeQuery(sqlQuery);
			users = sqlToArray(results);

			// Close Connection
			connect.close();
		} catch (SQLException err) {
			err.printStackTrace();
		}

        /*
         * Table Rows
         */
        
        // Loop
        int currentRow = 2;
        for (Object[] user : users) {
        	
        	// Calculate
        	boolean isNewUser = (user[8] == null);
        	boolean isActive = false;
        	
        	double balance = 0;
        	if (!isNewUser) {
        		if(user[10] != null && user[11] != null) 
        			balance = ((double) user[10]) - ((double) user[11]);
        		isActive = ((Date)user[7]).toLocalDate().isBefore(LocalDate.now()) ? false : true;
        	}
        	boolean isPaid = (balance <= 1);
        	
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
			int circleRadius = 6;
			JPanel tableColumnFlag = new JPanel();

			if (isNewUser) {
				tableColumnFlag = getStatusCircle(Theme.newStatus, circleRadius);
			}
			else if (isActive) {
				tableColumnFlag = getStatusCircle(greenColor, circleRadius);

			}
			else {
				tableColumnFlag = getStatusCircle(Theme.textThirdColor, circleRadius);
			}
			tableColumn.add(tableColumnFlag);
			
			// Add Container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 1;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			residentTable.add(tableColumn, c);
			
			/*
			 * Account Name
			 */
			
			JPanel tableColumn0 = buildTwoNameDisplay(combineNames((String)user[4], (String)user[5]), (String)user[1]);
	    	tableColumn0.setBorder(new EmptyBorder(4, 4, 4, 4));
	    	if (currentRow % 2 == 1) {
	    		tableColumn0.setOpaque(true);
	    		tableColumn0.setBackground(paneLightColor);
	    	}
			c.fill = GridBagConstraints.HORIZONTAL;
	        c.gridx = 2;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			residentTable.add(tableColumn0, c);
			
			/*
			 * Contact Button
			 */
			
			// Container
			JPanel tableColumn1 = new JPanel();
			tableColumn1.setLayout(new GridBagLayout());
			tableColumn1.setOpaque(false);
			if (currentRow % 2 == 1) {
	    		tableColumn1.setOpaque(true);
	    		tableColumn1.setBackground(paneLightColor);
	    	}
			tableColumn1.setBorder(new EmptyBorder(0, 4, 0, 4));
			
			//Button
			CustomButton contactAction = new CustomButton("Contact", 
					Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
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
						new PopupWindow(buildAccountContactDisplay(
								combineNames((String)user[4], (String)user[5]), 
								(String)user[1], (String)user[2], (String)user[3]), "ok");
				}
				
	        });
			tableColumn1.add(contactAction);
			
			// Add container
			c.fill = GridBagConstraints.BOTH;
	        c.gridx = 3;
	        c.gridy = currentRow;
	        c.weightx = 0.0;
			c.weighty = 0.0;
			c.gridwidth = 1;
			residentTable.add(tableColumn1, c);
			
			// If already had a lease
			if (!isNewUser) {
				
				/*
				 * Unit
				 */
				
				JPanel tableColumn2 = new JPanel();
				tableColumn2 = buildTwoNameDisplay((String)user[8], (String)user[9]);
				tableColumn2.setOpaque(false);
				tableColumn2.setBorder(new EmptyBorder(4, 4, 4, 4));
		    	if (currentRow % 2 == 1) {
		    		tableColumn2.setOpaque(true);
		    		tableColumn2.setBackground(paneLightColor);
		    	}
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 4;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				residentTable.add(tableColumn2, c);
				
				/*
				 * Lease
				 */
				
				// Lease Start
				JLabel tableColumn3 = new JLabel(
						((Date)user[6]).toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
						SwingConstants.RIGHT);
		    	tableColumn3.setFont(new Font("Arial", Font.PLAIN, 14));
		    	tableColumn3.setForeground(textColorTitle);
		    	tableColumn3.setBorder(new EmptyBorder(0, 4, 0, 4));
		    	
		    	//If the lease is finished
		    	if (!isActive) {
		    		tableColumn3.setForeground(textColor);
		    	}
		    	if (currentRow % 2 == 1) {
		    		tableColumn3.setOpaque(true);
		    		tableColumn3.setBackground(paneLightColor);
		    	}
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 5;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				residentTable.add(tableColumn3, c);
				
				// Space ">"
				JLabel tableColumn3Space = new JLabel(">",SwingConstants.LEFT);
				tableColumn3Space.setFont(new Font("Arial", Font.PLAIN, 14));
				tableColumn3Space.setForeground(textColorTitle);
				tableColumn3Space.setBorder(new EmptyBorder(0, 4, 0, 4));
				if (!isActive) {
		    		tableColumn3Space.setForeground(textColor);
		    	}
		    	if (currentRow % 2 == 1) {
		    		tableColumn3Space.setOpaque(true);
		    		tableColumn3Space.setBackground(paneLightColor);
		    	}
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 6;
		        c.gridy = currentRow;
		        c.weightx = 0.0;
				c.weighty = 0.0;
				c.gridwidth = 1;
				residentTable.add(tableColumn3Space, c);
				
				// Lease End
				JLabel tableColumn4 = new JLabel(
						((Date)user[7]).toLocalDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
						SwingConstants.LEFT);
				tableColumn4.setFont(new Font("Arial", Font.PLAIN, 14));
				tableColumn4.setForeground(textColorTitle);
				tableColumn4.setBorder(new EmptyBorder(0, 4, 0, 4));
				if(!isActive) {
					tableColumn4.setForeground(textColor);
				}
		    	if (currentRow % 2 == 1) {
		    		tableColumn4.setOpaque(true);
		    		tableColumn4.setBackground(paneLightColor);
		    	}
				c.fill = GridBagConstraints.BOTH;
		        c.gridx = 7;
		        c.gridy = currentRow;
		        c.weightx = 0.01;
				c.weighty = 0.0;
				c.gridwidth = 1;
				residentTable.add(tableColumn4, c);
				
				/*
				 * Payment Status
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
    			JLabel tableColumn7Flag = new JLabel("CHARGED", SwingConstants.LEFT);
    	    	tableColumn7Flag.setFont(new Font("Arial", Font.BOLD, 11));
    	    	tableColumn7Flag.setForeground(Theme.flagText);
    	    	tableColumn7Flag.setBorder(new EmptyBorder(0, 4, 0, 4));
    	    	tableColumn7Flag.setBackground(textColor);
    	    	tableColumn7Flag.setOpaque(true);
    	    	if (!isActive && !isPaid) {
    				tableColumn7Flag.setText("CHARGED");
    				tableColumn7Flag.setBackground(redColor);
    			}
    			if (isPaid) {
    				tableColumn7Flag.setText("PAID");
    				tableColumn7Flag.setBackground(greenColor);
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
    			residentTable.add(tableColumn7, c);
			}
			
			else {
				//Fill with background
				JPanel tableColumn2Fill = new JPanel();
				tableColumn2Fill.setOpaque(false);
				if (currentRow % 2 == 1) {
					tableColumn2Fill.setOpaque(true);
					tableColumn2Fill.setBackground(paneLightColor);
    	    	}
				c.fill = GridBagConstraints.BOTH;
    	        c.gridx = 4;
    	        c.gridy = currentRow;
    	        c.weightx = 0.0;
    			c.weighty = 0.0;
    			c.gridwidth = 5;
    			residentTable.add(tableColumn2Fill, c);
			}
			
			/*
			 * Delete unit
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
			CustomButton deleteAction = new CustomButton("Delete", Theme.buttonColor, Theme.hoverColor, Theme.pressedColor);
			deleteAction.setFont(new Font("Arial", Font.BOLD, 10));
			deleteAction.setBorderPainted(false);
			deleteAction.setForeground(textColorTitle);
			deleteAction.setFocusPainted(false);
			deleteAction.setBorder(new EmptyBorder(0, 0, 0, 0));
			deleteAction.setPreferredSize(new Dimension(40,16));
			deleteAction.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
						// Bring up pop up
						new PopupWindow(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// If user answers yes
								if (e.getActionCommand().equals("Yes")) {
									
									// Prepare query
									String sqlQuery = "UPDATE users SET deleted = 1 WHERE uid =" + user[0];
									
									// Connect to database
									try (Connection connect = DriverManager.getConnection(dbUrl, currentSession.getUser(), currentSession.getAdminPass())) {
										Statement statement = connect.createStatement();
										
										// Query
										statement.executeUpdate(sqlQuery);
										
										// Close connection
										connect.close();
									} catch (SQLException err) {
										err.printStackTrace();
									}

									refreshAll();
								}
							}
							
						}, "Are you sure you want to permanently delete this account?", "yesno");
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
			residentTable.add(tableColumn8, c);

			/*
			 * Increase row
			 */
			
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
        residentTable.add(tableBottomSpacer, c);
        	
        /*
         * Make table scroll
         */
        
	    unitScroll = new JScrollPane(residentTable);
	    unitScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    unitScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    unitScroll.setBorder(new EmptyBorder(0,0,0,0));
	    unitScroll.getVerticalScrollBar().setUnitIncrement(10);
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
		
	/*
	 * New Resident Panel Methods
	 */

	/**
	 * Fills a panel with a form to add a new resident to the resident list.
	 * @param panel - empty theme panel
	 * @return A panel with a new resident form and submit button.
	 */
	private JPanel buildNewResidentForm(JPanel panel) {
		// Container
		newResidentContent.setLayout(new GridBagLayout());
		newResidentContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		newResidentContent.setOpaque(true);
		newResidentContent.setBackground(paneColor);

		GridBagConstraints c = new GridBagConstraints();
		Dimension textFieldSize = new Dimension(200, 1);
        
		// Name label
		JLabel newResidentNameLabel = new JLabel("Name", SwingConstants.LEFT);
        newResidentNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentNameLabel.setForeground(textColor);
		newResidentNameLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(FIELD_MARGIN,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
		newResidentContent.add(newResidentNameLabel, c);
		
		// Name text field
		newResidentName = new JTextField();
		newResidentName.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentName.setBackground(fieldColor);
		newResidentName.setForeground(fieldText);
		newResidentName.setCaretColor(fieldText);
		newResidentName.setMinimumSize(textFieldSize);	// Sets column size
		newResidentName.setPreferredSize(textFieldSize);
		newResidentName.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(FIELD_MARGIN,FIELD_MARGIN,FIELD_MARGIN,FIELD_MARGIN);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 1;
		newResidentContent.add(newResidentName, c);
		
		// Email label
		JLabel newResidentEmailLabel = new JLabel("E-Mail", SwingConstants.LEFT);
		newResidentEmailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentEmailLabel.setForeground(textColor);
		newResidentEmailLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 2;
		newResidentContent.add(newResidentEmailLabel, c);
		
		// Email text field
		newResidentEmail = new JTextField();
		newResidentEmail.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentEmail.setBackground(fieldColor);
		newResidentEmail.setForeground(fieldText);
		newResidentEmail.setCaretColor(fieldText);
		newResidentEmail.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,FIELD_MARGIN,FIELD_MARGIN,FIELD_MARGIN);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 2;
		newResidentContent.add(newResidentEmail, c);
		
		// Phone label
		JLabel newResidentPhoneLabel = new JLabel("Phone", SwingConstants.LEFT);
		newResidentPhoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentPhoneLabel.setForeground(textColor);
		newResidentPhoneLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,0,FIELD_MARGIN/2,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 3;
		newResidentContent.add(newResidentPhoneLabel, c);
		
		// Phone text field
		newResidentPhone = new JTextField();
		newResidentPhone.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentPhone.setBackground(fieldColor);
		newResidentPhone.setForeground(fieldText);
		newResidentPhone.setCaretColor(fieldText);
		newResidentPhone.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,FIELD_MARGIN,FIELD_MARGIN/2,FIELD_MARGIN);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 1;
        c.gridy = 3;
		newResidentContent.add(newResidentPhone, c);
		
		// User name label
		JLabel newResidentUsernameLabel = new JLabel("Username", SwingConstants.LEFT);
		newResidentUsernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentUsernameLabel.setForeground(textColor);
		newResidentUsernameLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(FIELD_MARGIN,FIELD_MARGIN,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 2;
        c.gridy = 1;
		newResidentContent.add(newResidentUsernameLabel, c);
		
		// User name text field
		newResidentUsername = new JTextField();
		newResidentUsername.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentUsername.setBackground(fieldColor);
		newResidentUsername.setForeground(fieldText);
		newResidentUsername.setCaretColor(fieldText);
		newResidentUsername.setMinimumSize(textFieldSize);	// Sets column size
		newResidentUsername.setPreferredSize(textFieldSize);
		newResidentUsername.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(FIELD_MARGIN,FIELD_MARGIN,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 3;
        c.gridy = 1;
		newResidentContent.add(newResidentUsername, c);
		
		// Password label
		JLabel newResidentPasswordLabel = new JLabel("Password", SwingConstants.LEFT);
		newResidentPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentPasswordLabel.setForeground(textColor);
		newResidentPasswordLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,FIELD_MARGIN,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 4;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 2;
        c.gridy = 2;
		newResidentContent.add(newResidentPasswordLabel, c);
		
		// Password text field
		newResidentPassword = new JTextField(1);
		newResidentPassword.setFont(new Font("Arial", Font.PLAIN, 14));
		newResidentPassword.setBackground(fieldColor);
		newResidentPassword.setForeground(fieldText);
		newResidentPassword.setCaretColor(fieldText);
		newResidentPassword.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(0,FIELD_MARGIN,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0;
        c.gridx = 3;
        c.gridy = 2;
		newResidentContent.add(newResidentPassword, c);

		// Add Resident Container
 		JPanel addResidentSubmit = new JPanel();
 		addResidentSubmit.setLayout(new GridBagLayout());
 		addResidentSubmit.setOpaque(false);
 		
 		// Add Resident Button
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
				if (validateNewResidentEntry()) {
					new PopupWindow(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// If user answers yes
							if (e.getActionCommand().equals("Yes")) {
								
								// Prepare query
								String[] insertName = newResidentName.getText().trim().split("\s", 2);
								String sqlQuery = "INSERT INTO users(`user`,`first_name`,`last_name`,`pass`,`email`,`phone`)"
										+ "VALUES('" 
										+ newResidentUsername.getText() + "','"
										+ insertName[0] + "',"
										+ ((insertName.length > 1) ? ("'" + insertName[1] + "','") : ("NULL ,'"))
										+ newResidentPassword.getText() + "','"
										+ newResidentEmail.getText() + "','"
										+ newResidentPhone.getText() + "')";
								
								
								// Connect to database
								try (Connection connect = DriverManager.getConnection(dbUrl, currentSession.getUser(), currentSession.getAdminPass())) {
									Statement statement = connect.createStatement();
									
									// Query
									statement.executeUpdate(sqlQuery);
								
									// Close connection
									connect.close();
								} catch (SQLException err) {
									err.printStackTrace();
								}
								
								// Clear fields
								clearNewEntry();
								refreshAll();
							}
						}
						
					}, "Are you sure you want to add this account?", "yesno");
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
        addResidentSubmit.add(submitUnitButton, c);
		
        // Add Add Resident Button container
		c.fill = GridBagConstraints.HORIZONTAL;
     	c.gridwidth = 2;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 2;
        c.gridheight = 1;
		newResidentContent.add(addResidentSubmit, c);
		
		/*
         * Add content to panel
         */
		
		panel.add(newResidentContent);
		return panel;
	}
		
	/**
	 * Checks that all forms in the add resident form are filled and that the
	 * phone number entered is only made up of integers. 
	 * @return true if entry is valid
	 */
	private boolean validateNewResidentEntry() { //TODO Add length limits to fields
		// Check if all fields are filled
		if (!(newResidentName.getText().isBlank() 
				|| newResidentEmail.getText().isBlank()
				|| newResidentPhone.getText().isBlank()
				|| newResidentUsername.getText().isBlank()
				|| newResidentPassword.getText().isBlank())
				&& newResidentPhone.getText().matches("[0-9]+")) {
			return true;	
		}	
		return false;
	}
	
	/**
	 * Clears entry text fields for New Resident
	 */
	private void clearNewEntry() {
		newResidentName.setText("");
		newResidentEmail.setText("");
		newResidentPhone.setText("");
		newResidentUsername.setText("");
		newResidentPassword.setText("");
	}

	/*
	 * Refresh Page Methods
	 */
	
	@Override
	public void refreshAll() {
		refreshTable(allResidentsPanelContent, unitScroll);
		refreshSearch(filterPanel, searchPanelContent);
		
	}
	
	/**
	 * Quick version of refreshTable.
	 */
	private void refreshTable() {
		refreshTable(allResidentsPanelContent, unitScroll);
	}
	
	/**
	 * Removes the Search panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param summary - Content
	 */
	private void refreshSearch(JPanel panel, Component summary) {
		panel.remove(summary);
		buildSearchPanel(panel);
		panel.revalidate();
		
	}	
	
	/**
	 * Removes the Table panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param table - Content
	 */
	private void refreshTable(JPanel panel, Component table) {
		panel.remove(table);
		buildResidentTable(panel);
		panel.validate();
	}
}

