package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import unit.BuildingList;
import user.User;
import user.UserList;

/**
 * This class uses Swing GUI Components to build the main platform for this application. 
 * It creates the application window, side bar, and container for tab content.
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class Dashboard extends JFrame {
	// Data
	private User user;
	private UserList users;
	private BuildingList buildingList;
	
	// Panel Sections
	private JPanel sidebarPanel;
	private JPanel contentPanel;
	private JPanel mainPanel;
	
	// Tabs
	private Tab[] tabs = new Tab[5];
	private Tab currentTab;
	
	// Pages
	private AccountPage account;
	private ComplaintsPage adminComplaints;
	private ComplaintsPage residentComplaints;
	private UnitManagerPage unitManager;
	private ResidentManagerPage residentManager;
	private ResidentDashboardPage residentDashboard;
	private AdminDashboardPage adminDashboard;
	
	// Colors
	private Color sidebarColor;
	private Color contentBackground;
	private Color textColor;
	private Color textPrimaryColor;
	
	/**
	 * Opens a JFrame window with a GUI (side bar on left and content in the center).
	 * Side bar tabs determine the content displayed.
	 * @param user - Current user
	 * @param users - List of users if Admin, null if Resident
	 * @param buildingList - List of buildings if Admin, null if Resident
	 */
	public Dashboard(User user, UserList users, BuildingList buildingList) {		
		
		// Data
		this.buildingList = buildingList;
		this.user = user;
		this.users = users;
		
		// Theme (Default is Dark)
		if (!user.isDarkTheme()) Theme.setLight();
		else Theme.setDark();
		
		sidebarColor = Theme.paneColor;
		contentBackground = Theme.baseColor;
		textColor = Theme.textSecondaryColor;
		textPrimaryColor = Theme.textPrimaryColor;
		
		// Main Container
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		/*
		 * Content Panel Setup
		 */

		contentPanel = new JPanel();
		contentPanel.setPreferredSize(new Dimension(800,300));
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setBackground(contentBackground);
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		
		/*
		 * Tab Setup
		 */
		
		// Settings
		account = new AccountPage(user, this); 
		account.setVisible(false);
		
		if (user.getType().equals("Admin")) {
			
			// Administrator Dashboard
			adminDashboard = new AdminDashboardPage(buildingList);
			adminDashboard.setVisible(false);
			
			// Unit Manager
			unitManager = new UnitManagerPage(users, buildingList);
			unitManager.setVisible(false);
			
			// Resident Manager
			residentManager = new ResidentManagerPage(users, buildingList);
			residentManager.setVisible(false);
			
			// Administrator Complaints
			adminComplaints = new ComplaintsPage(user, users);
			adminComplaints.setVisible(false);
			
			// Tabs
        	tabs = new Tab[6];
        	tabs[0] = new Tab("Dashboard", adminDashboard);
        	tabs[1] = new Tab("Unit Manager", unitManager);
        	tabs[2] = new Tab("Resident Manager", residentManager);
        	tabs[3] = new Tab("Complaints", adminComplaints);
        	tabs[4] = new Tab("Settings", account);
        	tabs[5] = new Tab("Log Out", new JPanel());        	
        }
		
        else if (user.getType().equals("Resident")) {
        	
        	// Resident Complaints
        	residentComplaints = new ComplaintsPage(user);
    		residentComplaints.setVisible(false);
    		
    		// Resident Dashboard
    		residentDashboard = new ResidentDashboardPage(user);
    		residentDashboard.setVisible(false);
    			
    		// Tabs
         	tabs[0] = new Tab("Dashboard", residentDashboard);
        	tabs[1] = new Tab("Complaints", residentComplaints);
        	tabs[2] = new Tab("Settings", account);
        	tabs[3] = new Tab("Log Out", new JPanel());
        }
		
		// Open Default Tab
		currentTab.getPage().setVisible(true);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
		contentPanel.add(currentTab.getPage(), c);

		/*
		 * Side Bar Panel //TODO Make side bar collapsible
		 */
        sidebarPanel = new JPanel();
		buildSidebar(sidebarPanel);
		mainPanel.add(sidebarPanel, BorderLayout.WEST);
				
		/*
		 * JFrame Setup
		 */
        
        this.setTitle("Apartment Manager");
        this.getContentPane().add(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(contentBackground);
        this.setMinimumSize(new Dimension(900, 600));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * Fills the input JPanel with the side bar content.
	 * @param sidebar - Empty JPanel to use as side bar
	 */
	public void buildSidebar(JPanel sidebar) {
		sidebar.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Content
		JPanel sidebarContent = new JPanel();
		buildSidebarContent(sidebarContent);
		c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0,0,0,0);
        sidebar.add(sidebarContent, c);
		
        //Border (If in Light mode)
        if (!Theme.isDark()) {
        	JPanel sidebarBorder = new JPanel();
	        sidebarBorder.setPreferredSize(new Dimension(1,1));
	        sidebarBorder.setMinimumSize(new Dimension(1,1));
	        sidebarBorder.setBackground(Theme.borderColor);
			c.fill = GridBagConstraints.VERTICAL;
	        c.weightx = 0.0;
	        c.weighty = 1.0;
	        c.gridx = 1;
	        c.gridy = 0;
	        c.gridwidth = 1;
	        c.insets = new Insets(0,0,0,0);
	        sidebar.add(sidebarBorder, c);
        }
	}
	
	/**
	 * Creates content to be added to an empty side bar. This includes name displays, 
	 * a profile image, tab buttons, and a log out button.
	 * @param sidebar - A JPanel styled as a side bar with no content.
	 */
	public void buildSidebarContent(JPanel sidebar) {
		sidebar.setBackground(sidebarColor);
		sidebar.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// User Name
		JLabel userName = new JLabel(user.getUsername(), SwingConstants.CENTER);
		userName.setFont(new Font("Arial", Font.PLAIN, 26));
		userName.setForeground(textPrimaryColor);
		userName.setPreferredSize(new Dimension(180, 35));
		c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.15;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0,10,0,10);
        sidebar.add(userName, c);

		// Profile Picture Size
		int profileIconWidth = 60;
		int profileIconHeight = 60;
		Dimension profileIconDimension = new Dimension(profileIconWidth, profileIconHeight);
		
		// Profile Picture
		JLabel profilePicture = new JLabel(new Icon() {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
				
		        Ellipse2D.Double baseCircle = new Ellipse2D.Double(0, 0, 
		        		profileIconWidth, profileIconHeight);
		        g2.setColor(Theme.profilePictureBackground);
		        g2.fill(baseCircle);
		        
		        Ellipse2D.Double headCircle = new Ellipse2D.Double(profileIconWidth/4, 
		        		profileIconHeight/10, profileIconWidth/2, profileIconHeight/2);
		        g2.setColor(Theme.profilePictureColor);
		        g2.fill(headCircle);
		        
		        Ellipse2D.Double bodyCircle = new Ellipse2D.Double(0, 13 * (profileIconHeight/20),
		        		profileIconWidth, profileIconHeight);
		        g2.setColor(Theme.profilePictureColor);
		        g2.fill(bodyCircle);
		        
		        Ellipse2D.Double outsideCircleSubtract = new Ellipse2D.Double(0, 0, 
		        		profileIconWidth, profileIconHeight);
		        Area outsideCircle = new Area(new Rectangle2D.Double(0,0,
		        		profileIconWidth, profileIconHeight));
		        outsideCircle.subtract(new Area(outsideCircleSubtract));
		        g2.setColor(sidebarColor);
		        g2.fill(outsideCircle);
			}

			@Override
			public int getIconWidth() {
				return profileIconHeight;
			}

			@Override
			public int getIconHeight() {
				return profileIconWidth;
			}
			
		});
        profilePicture.setPreferredSize(profileIconDimension);
        profilePicture.setMaximumSize(profileIconDimension);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        c.weighty = 0.05;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0,10,0,10);
        c.anchor = GridBagConstraints.CENTER;
        sidebar.add(profilePicture, c);
        
        // Real Name
		JLabel realName = new JLabel(user.getName(), SwingConstants.CENTER);
		realName.setFont(new Font("Arial", Font.PLAIN, 16));
		realName.setForeground(textPrimaryColor);
		realName.setPreferredSize(new Dimension(180, 35));
		c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.05;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0,10,0,10);
        sidebar.add(realName, c);
        
        //Filler
        JPanel profileSpace = new JPanel();
        profileSpace.setOpaque(false);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.insets = new Insets(0,0,0,0);
        sidebar.add(profileSpace, c);
        
        // Tab Buttons
        int tabRow = 4;
        for (Tab t : tabs) {
        	if (t == null) return;
        	
        	// Space above the Settings button
        	if (t.getTabButton().getText().equalsIgnoreCase("Settings")) {
        		JPanel tabSpace = new JPanel();
        		tabSpace.setOpaque(false);
                c.fill = GridBagConstraints.BOTH;
                c.weighty = 1.0;
                c.gridy = tabRow;
                c.insets = new Insets(0,0,0,0);
                sidebar.add(tabSpace, c);
                c.weighty = 0.0;
                tabRow++;
        	}
        	
        	// Make Log Out button text red
        	if (t.getTabButton().getText().equalsIgnoreCase("Log Out")) {
        		t.getTabButton().setForeground(Theme.redColor);
        	}
        	
        	// Add button
        	c.fill = GridBagConstraints.BOTH;
            c.weightx = 0.0;
            c.weighty = 0.0;
            c.gridx = 0;
            c.gridy = tabRow;
            c.gridwidth = 1;
            c.insets = new Insets(0,0,0,0);
        	sidebar.add(t.getTabButton(), c);
        	tabRow++;
        }
	}
	
	/**
	 * A class representing a tab. Each tab contains a button and a page.
	 */
	public class Tab {
		CustomButton tabButton;
		JPanel page;
	
		public Tab (String name, JPanel page) {
			this(new CustomButton(), page);
			
			if (currentTab == null) {
				currentTab = this;
			}
			
			this.tabButton = buildTabButton(name, this);
		}
		
		public Tab(CustomButton button, JPanel page) {
			this.tabButton = button;
			this.page = page;
		}
		public CustomButton getTabButton() {
			return tabButton;
		}
		public void setTabButton(CustomButton tabButton) {
			this.tabButton = tabButton;
		}
		public JPanel getPage() {
			return page;
		}
		public void setPage(JPanel page) {
			this.page = page;
		}	
	}
	
	/**
	 * JButton theme class for tab buttons. Styles the tab buttons.
	 * @param string - Button text
	 * @param tab - Tab object associated with his button
	 * @return A tab button
	 */
	public CustomButton buildTabButton(String string, Tab tab) {
		CustomButton button = new CustomButton(string);
		button.setFont(new Font("Arial", Font.PLAIN, 15));
		button.setForeground(textColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(sidebarColor);
        button.setHoverBackgroundColor(sidebarColor);
        button.setPressedBackgroundColor(sidebarColor);
        button.setPreferredSize(new Dimension(180, 35));
        
        // Make selected tab button text bigger and change the color
 		if (tab == currentTab) {
 			button.setFont(new Font("Arial", Font.PLAIN, 17));
 			button.setForeground(textPrimaryColor);
 		}
        
     	// Enlarges text when a button is hovered over
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            	if (currentTab.getTabButton() != button) {
            		button.setFont(new Font("Arial", Font.PLAIN, 17));
            	}
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
            	if (currentTab.getTabButton() != button) {
            		button.setFont(new Font("Arial", Font.PLAIN, 15));
            	}	
            }
        });
        
        // Action
        button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (button.getText().equalsIgnoreCase("Log Out")) logOut();
				
				// If click is on current tab
				else if (button == currentTab.getTabButton()) {
					currentTab.getTabButton().setForeground(textPrimaryColor);
					currentTab.getTabButton().setFont(new Font("Arial", Font.PLAIN, 17));
					return;
				}
				
				// If click is not current tab
				else {
					// Close old tab
					currentTab.getPage().setVisible(false);
					contentPanel.remove(currentTab.getPage());
					
					// Set tab button back to neutral
					currentTab.getTabButton().setForeground(textColor);
					currentTab.getTabButton().setFont(new Font("Arial", Font.PLAIN, 15));
					
					// Set new tab
					currentTab = tab;
					
					// Open new tab
					((Page) currentTab.getPage()).refreshAll();
					currentTab.getPage().setVisible(true);
					GridBagConstraints c = new GridBagConstraints();
					c.fill = GridBagConstraints.BOTH;
					c.gridx = 0;
			        c.gridy = 0;
			        c.weightx = 1.0;
			        c.weighty = 1.0;
			        contentPanel.add(currentTab.getPage(), c);
					
					// Set selected button state
					currentTab.getTabButton().setForeground(textPrimaryColor);
					currentTab.getTabButton().setFont(new Font("Arial", Font.PLAIN, 17));
					
					// Refresh tab
					contentPanel.revalidate();
					currentTab.getPage().revalidate();
					
				}
			}
        	
        });
     
        return button;
	}
	
	/**
	 * JButton with custom hover and press colors.
	 */
	class CustomButton extends JButton {

        private Color hoverBackgroundColor;
        private Color pressedBackgroundColor;

        public CustomButton() {
            this(null);
        }

        public CustomButton(String text) {
            super(text);
            super.setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getModel().isPressed()) g.setColor(pressedBackgroundColor);
            else if (getModel().isRollover()) g.setColor(hoverBackgroundColor);
            else g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }

        @Override
        public void setContentAreaFilled(boolean b) {
        }

        public Color getHoverBackgroundColor() {
            return hoverBackgroundColor;
        }

        public void setHoverBackgroundColor(Color hoverBackgroundColor) {
            this.hoverBackgroundColor = hoverBackgroundColor;
        }

        public Color getPressedBackgroundColor() {
            return pressedBackgroundColor;
        }

        public void setPressedBackgroundColor(Color pressedBackgroundColor) {
            this.pressedBackgroundColor = pressedBackgroundColor;
        }
    }

	/**
	 * Creates a new instance of this JFrame with the same data and closes the current window.
	 */
	public void relaunch() {
		new Dashboard(user, users, buildingList);
		this.dispose();
	}
	
	/**
	 * Closes the program
	 */
	public void logOut() {
		setVisible(false);
		dispose();
	}
}

