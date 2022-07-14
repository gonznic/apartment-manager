package main.java.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.java.Page;
import main.java.Session;

/**
 * 
 * A class that implements JFrame and creates a window with user name and password fields.
 * If the entered credentials are in the database, a "Dashboard" window will open.
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class LoginWindow extends JFrame implements ActionListener, Page {
	
	// Panel
	private JPanel panel;
	private JLayeredPane layeredPane;
	
	// Fields
	private JTextField userText;
	private JPasswordField passwordText;
	private JLabel invalidCred;
	private boolean validLogin = true;
	
	/**
	 * Creates a log in window.
	 */
	public LoginWindow() {
		
		//Theme
		Theme.setDark();
		
		/*
		 * Frame Setup
		 */
		
		//General Setup
		Dimension windowSize = new Dimension(700, 700);
		this.setResizable(false);
		this.setTitle("Apartment Manager");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Layered pane
		layeredPane = getLayeredPane();
		layeredPane.setLayout(null);
		
		/*
		 * Content
		 */
		
		// Panel
		panel = new JPanel();
		panel.setLayout(null);
		panel.setBounds(150, 100, 400, 400);
		Dimension panelSize = new Dimension(400, 400);
		panel.setPreferredSize(panelSize);
		panel.setMinimumSize(panelSize);
		panel.setMaximumSize(panelSize);
		panel.setBackground(Theme.paneColor);
		setComponents(panel);
		
		/*
		 * Invalid Log in
		 */
		
		if (validLogin) {
			// Invalid log in label
			invalidCred = new JLabel("Invalid Credentials", SwingConstants.CENTER);
			invalidCred.setFont(new Font("Arial", Font.PLAIN, 16));
			invalidCred.setForeground(Color.RED);
			invalidCred.setBounds(0,150,400,50);
			invalidCred.setVisible(false);
	        panel.add(invalidCred);
		}
		layeredPane.add(panel, 3);
	
		/*
		 * Background Image
		 */
	
		// Load image
		BufferedImage loadImage = null;
		try {
			loadImage = ImageIO.read(new File("src/main/resources/loginBg.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set image
		JImageComponent image = new JImageComponent(loadImage);
		image.setSize(windowSize);
		layeredPane.add(image, 1);
		
		/*
		 * Finish frame setup
		 */
		
		this.setPreferredSize(windowSize);
		this.setMinimumSize(windowSize);
		this.setMaximumSize(windowSize);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.setVisible(true);
	}
	
	/**
	 * Adds a title, a version, a user name field, a password field, and a log in button to the
	 * parameter "panel".
	 * @param panel - Panel to set log in screen components
	 */
	public void setComponents(JPanel panel) {
		
		// Title
		JLabel title = new JLabel("Apartment Manager", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.PLAIN, 30));
		title.setForeground(Theme.textPrimaryColor);
		title.setBounds(0,70,400,50);
        panel.add(title);
		
        // User name label
		JLabel userLabel = new JLabel("Username");
		userLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		userLabel.setForeground(Theme.textPrimaryColor);
		userLabel.setBounds(60,200,100,25);
        panel.add(userLabel);
		
        // User name text field
		userText = new JTextField(20);
		userText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		userText.setFont(new Font("Arial", Font.PLAIN, 20));
        userText.setBounds(170,200,170,25);
        panel.add(userText);
		
        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		passwordLabel.setForeground(Theme.textPrimaryColor);
		passwordLabel.setBounds(60,240,100,25);
        panel.add(passwordLabel);
        
        // Password text field
        passwordText = new JPasswordField(20);
        passwordText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        passwordText.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordText.setBounds(170,240,170,25);
        panel.add(passwordText);
        
        // Log in button
        CustomButton loginButton = new CustomButton("Log In", Theme.buttonColor, 
        		Theme.hoverColor, Theme.pressedColor);
        loginButton.setForeground(Theme.textPrimaryColor);
        loginButton.setFont(new Font("Arial", Font.PLAIN, 20));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setBounds(125, 300, 150, 50);
        loginButton.addActionListener(this); 
        panel.add(loginButton);
	}

	/**
	 * Is triggered when log in button is pressed. If the credentials match an account in the 
	 * database, open a "Dashboard" page with that account.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Get user entry
		String username = userText.getText();
		char[] password = passwordText.getPassword();
		String passwordString = new String(password);
		
		try (Connection connect = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

			// Prepare query
			Statement statement = connect.createStatement();
			String sqlQuery = "SELECT * FROM users WHERE user LIKE '"
			+ username + "' AND pass LIKE '" + passwordString + "'";
			
			// Try query
			try {
				ResultSet results = statement.executeQuery(sqlQuery);

				// If there is a match
				if (results.next()) {
					invalidCred.setVisible(false);

					try {
						// Bring up dash board
						new Dashboard(new Session(results, dbUrl));
						
						// Close log in window
						this.setVisible(false);
						this.dispose();
						
					} catch(Exception err) {
						System.err.println("This is a new user. Log-in will "
								+ "be available once a lease is assigned.");
					}
				}
				
				// If no match
				else {
					invalidCred.setVisible(true);
					passwordText.setText("");
				}
			} catch (SQLException err) {
				err.printStackTrace();
			}
			
			connect.close();
		} catch (SQLException err) {
			err.printStackTrace();
		}
	}
}

