package main.java.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.java.Page;
import main.java.Session;

/**
 * A class that creates a JPanel with a settings page.
 * Contains an Account panel and a Settings panel.
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class AccountPage extends JPanel implements Page { //TODO Password reset
	// Setup
	private Session currentSession;
	private String title = "Settings";
	private JFrame frame; // Access parent to re-launch application when theme is changed

	// Panels
	private JPanel userInfo;
	private JPanel settings;

	// Colors
	private Color backgroundColor = Theme.baseColor;
	private Color paneColor = Theme.paneColor;
	private Color textColor = Theme.textSecondaryColor;
	private Color textColorTitle = Theme.textPrimaryColor;
	private Color fieldColor = Theme.fieldColor; 

	/**
	 * Creates a page in a JPanel composing of a header and a panel titled 
	 * "Account" and a panel titled "Settings".
	 * @param user - User account
	 * @param frame - Application JFrame
	 */
	public AccountPage(Session session, JFrame frame) {
		//Setup
		this.currentSession = session;
		this.frame = frame;
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
		 * Panels
		 */
        
        GridBagConstraints c = new GridBagConstraints();
        
        userInfo = buildUserInfo(buildPanel("Account"));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(PANE_MARGIN,0,0,0);
        tabContent.add(userInfo, c);
  
        settings = buildSettings(buildPanel("Settings"));
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(PANE_MARGIN,0,0,0);
        tabContent.add(settings, c);
	}
	
	/**
	 * Adds Account panel content to the input JPanel.
	 * Includes labels with user info.
	 * @param panel - Account panel
	 * @return Account panel with content
	 */
	private JPanel buildUserInfo(JPanel panel) {
		JPanel panelContent = new JPanel();
		panelContent.setLayout(new GridBagLayout());
		panelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContent.setBackground(paneColor);
		
		GridBagConstraints c = new GridBagConstraints();

		// Big name label
		JLabel nameLabel = new JLabel(currentSession.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		nameLabel.setForeground(textColorTitle);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 4;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
		panelContent.add(nameLabel, c);
		
		// User name label
		JLabel usernameLabel = new JLabel("Username", SwingConstants.LEFT);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		usernameLabel.setForeground(textColor);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,FIELD_MARGIN,FIELD_MARGIN);
		c.weighty = 0;
		c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
		panelContent.add(usernameLabel, c);
		
		// User name text
		JLabel usernameDisplay = new JLabel(currentSession.getUser(), SwingConstants.LEFT);
        usernameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		usernameDisplay.setForeground(textColorTitle);
		c.insets = new Insets(0,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
		panelContent.add(usernameDisplay, c);
		
		// Phone Label
		JLabel phoneLabel = new JLabel("Phone Number", SwingConstants.LEFT);
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		phoneLabel.setForeground(textColor);
		c.insets = new Insets(0,0,FIELD_MARGIN,FIELD_MARGIN);
        c.gridx = 1;
        c.gridy = 2;
		panelContent.add(phoneLabel, c);
		
		// Phone number and email text
		String phone = "";
		String number = phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
		String email = "";
		
		// Connect to database
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

			// Prepare query
			String sqlQuery = "SELECT `email`,`phone` FROM users WHERE uid = " 
			+ currentSession.getUserId();
			Statement statement = connect.createStatement();
			
			// Query
			try {
				ResultSet result = statement.executeQuery(sqlQuery);
				while (result.next()) {
					phone = result.getString("phone");
					email = result.getString("email");
				}

			} catch (SQLException err) {
				err.printStackTrace();
			}
			
			// Close connection
			connect.close();
		} catch (SQLException err) {
			err.printStackTrace();
		}
				
		// Phone label
		JLabel phoneDisplay = new JLabel(number);
        phoneDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		phoneDisplay.setForeground(textColorTitle);
		c.insets = new Insets(0,0,FIELD_MARGIN,0);
		c.gridx = 2;
        c.gridy = 2;
		panelContent.add(phoneDisplay, c);

		// Email Label
		JLabel emailLabel = new JLabel("E-Mail", SwingConstants.LEFT);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		emailLabel.setForeground(textColor);
		//emailLabel.setOpaque(true);
		c.insets = new Insets(0,0,FIELD_MARGIN,FIELD_MARGIN);
        c.gridx = 1;
        c.gridy = 3;
		panelContent.add(emailLabel, c);
		
		//Email Text
		JLabel emailDisplay = new JLabel(email);
        emailDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		emailDisplay.setForeground(textColorTitle);
		c.insets = new Insets(0,0,FIELD_MARGIN,0);
		c.gridx = 2;
        c.gridy = 3;
		panelContent.add(emailDisplay, c);
		
		// left filler
		JLabel profileFiller = new JLabel();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 4;
		panelContent.add(profileFiller, c);
		
		// right filler
		JLabel profileFiller1 = new JLabel();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridx = 3;
        c.gridy = 4;
		panelContent.add(profileFiller1, c);
		
		// Add container to panel
		panel.add(panelContent);
		return panel;
	}
	
	/**
	 * Adds Settings panel content to input JPanel.
	 * @param panel - Settings panel
	 * @return Settings panel with content
	 */
	private JPanel buildSettings(JPanel panel) {
		// Container
		JPanel panelContent = new JPanel();
		panelContent.setLayout(new GridBagLayout());
		panelContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
		
		// Name label
		JLabel darkModeLabel = new JLabel("Dark Mode", SwingConstants.LEFT);
		darkModeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		darkModeLabel.setForeground(textColor);
		darkModeLabel.setBorder(new EmptyBorder(0, 4, 0, 4));
		c.insets = new Insets(FIELD_MARGIN,0,FIELD_MARGIN,FIELD_MARGIN);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
		panelContent.add(darkModeLabel, c);
		
		/*
		 * Dark Mode Switch
		 */
		
		int switchHeight = 20;
		
		JCheckBox darkModeSwitch = new JCheckBox();
		darkModeSwitch.setOpaque(false);
		darkModeSwitch.setBorderPainted(false);
		darkModeSwitch.setMargin(new Insets(0,0,0,0));
		
		// Set current selection
		if (Theme.isDark()) darkModeSwitch.setSelected(true);
		else darkModeSwitch.setSelected(false);
		
		// Set icons
		darkModeSwitch.setIcon(new Icon() {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g;
				
		        Ellipse2D.Double baseCircle = new Ellipse2D.Double(0, 0, 
		        		switchHeight, switchHeight);
		        g2.setColor(fieldColor);
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
		        g2.fill(baseCircle);
		        
		        Ellipse2D.Double baseCircle0 = new Ellipse2D.Double(switchHeight, 0, 
		        		switchHeight, switchHeight);
		        g2.setColor(fieldColor);
		        g2.fill(baseCircle0);
		        
		        Rectangle2D.Double baseRect = new Rectangle2D.Double(switchHeight/2, 0,
		        		switchHeight, switchHeight);
		        g2.setColor(fieldColor);
		        g2.fill(baseRect);
		        
		        // Switch
		        Ellipse2D.Double switchCircle = new Ellipse2D.Double(
		        		switchHeight/20, 
		        		switchHeight/20, 
		        		switchHeight - (switchHeight/10), 
		        		switchHeight - (switchHeight/10));
		        g2.setColor(textColorTitle);
		        g2.fill(switchCircle);
				
			}

			@Override
			public int getIconWidth() {
				return switchHeight * 2;
			}

			@Override
			public int getIconHeight() {
				return switchHeight;
			}
			
		});
		darkModeSwitch.setSelectedIcon(new Icon() {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g;
				
		        Ellipse2D.Double baseCircle = new Ellipse2D.Double(0, 0, 
		        		switchHeight, switchHeight);
		        g2.setColor(Theme.greenColor);
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
		        g2.fill(baseCircle);
		        
		        Ellipse2D.Double baseCircle0 = new Ellipse2D.Double(switchHeight, 0, 
		        		switchHeight, switchHeight);
		        g2.setColor(Theme.greenColor);
		        g2.fill(baseCircle0);
		        
		        Rectangle2D.Double baseRect = new Rectangle2D.Double(switchHeight/2, 0,
		        		switchHeight, switchHeight);
		        g2.setColor(Theme.greenColor);
		        g2.fill(baseRect);
		        
		        // Switch
		        Ellipse2D.Double switchCircle = new Ellipse2D.Double(
		        		(switchHeight) + (switchHeight/20), 
		        		switchHeight/20, 
		        		switchHeight - (switchHeight/10), 
		        		switchHeight - (switchHeight/10));
		        g2.setColor(textColorTitle);
		        g2.fill(switchCircle);
				
			}

			@Override
			public int getIconWidth() {
				return switchHeight * 2;
			}

			@Override
			public int getIconHeight() {
				return switchHeight;
			}
			
		});
 		
		// Action listener (Asks if user really wants to switch the theme)
		darkModeSwitch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				new PopupWindow(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (e.getActionCommand().equalsIgnoreCase("ok")) { 
							//Switch theme
							
							// Connect to database
							try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
								Statement statement = connect.createStatement();
								
								// Prepare query
								String sqlQuery = "UPDATE users SET theme = '";
								if (Theme.isDark()) {
									Theme.setLight();
									sqlQuery += "light";
								}
								else {
									Theme.setDark();
									sqlQuery += "dark";
								}
								sqlQuery += "' WHERE uid =" + currentSession.getUserId();
								
								// Query
								statement.executeUpdate(sqlQuery);
								
								// Close connection
								connect.close();
							} catch (SQLException err) {
								err.printStackTrace();
							}

							// Re-launch
							((Dashboard)frame).relaunch();
						}
						if (e.getActionCommand().equalsIgnoreCase("cancel")) {
							if (darkModeSwitch.isSelected()) darkModeSwitch.setSelected(false);
							else darkModeSwitch.setSelected(true);
						}
					}
					
				}, "This will require the program to restart.", "cancelok");
			}
			
		});
		
		// Add Switch
		c.insets = new Insets(FIELD_MARGIN,0,FIELD_MARGIN,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
        c.gridx = 1;
        c.gridy = 0;
		panelContent.add(darkModeSwitch, c);
		
		panel.add(panelContent);
		return panel;
	}	
}

