package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 * A class that creates a pop-up JFrame window.
 * Has four types that determine the button layout: "yesno", "cancelok", "cancel", and "ok".
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class PopupWindow extends JFrame implements ActionListener, Page {

	private JPanel messagePanel; // Content
	private ActionListener listener; // External ActionListener
	private String type; 
	
	private Dimension buttonSize = new Dimension(90, 30);
	private Color paneColor = Theme.popupColor;
	
	/**
	 * Creates a pop up window with the entered text displayed.
	 * Window contains buttons depending on the type: "yesno", "cancelok", "cancel", or "ok".
	 * @param listener - External listener
	 * @param text - Text to display on the pop up
	 * @param type - "yesno", "cancelok", "cancel", or "ok"
	 */
	public PopupWindow(ActionListener listener, String text, String type) {
		this.listener = listener;
		this.messagePanel = stringToPanel(text);
		this.type = type;
		buildFrame();	
	}
	
	/*
	 * Helper method that takes in a string and returns a JPanel with a JLabel inside.
	 */
	private JPanel stringToPanel(String text) {
		// Label
		JLabel popupText = new JLabel(text, SwingConstants.CENTER);
		popupText.setFont(new Font("Arial", Font.PLAIN, 14));
		popupText.setForeground(Theme.textSecondaryColor);
		
		//Panel
		JPanel stringPanel = new JPanel();
		stringPanel.setLayout(new GridBagLayout());
		stringPanel.add(popupText);
		stringPanel.setOpaque(false);
		return stringPanel;
	}
	
	/**
	 * Creates a pop up window with the entered JPanel displayed.
	 * Window contains buttons depending on the type: "yesno", "cancelok", "cancel", or "ok".
	 * @param listener - External listener
	 * @param panel - Panel to display on the pop up
	 * @param type - "yesno", "cancelok", "cancel", or "ok"
	 */
	public PopupWindow(ActionListener listener, JPanel panel, String type) {
		this.listener = listener;
		this.messagePanel = panel;
		this.type = type;
		buildFrame();
	}
	
	/**
	 * Creates a pop up window with the entered JPanel displayed.
	 * Window contains buttons depending on the type: "yesno", "cancelok", "cancel", or "ok".
	 * Does not incorporate external action listener (pop up will close when any pop up 
	 * button is clicked).
	 * @param panel - Panel to display on the pop up
	 * @param type - "yesno", "cancelok", "cancel", or "ok"
	 */
	public PopupWindow(JPanel panel, String type) {
		this.listener = null;
		this.messagePanel = panel;
		this.type = type;
		buildFrame();
	}
	
	/**
	 * Sets up the JFrame and fills it with content depending on type.
	 */
	public void buildFrame() {
		this.setUndecorated(true);
		this.setTitle("");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.toFront();
		
		// Add content based on type
		JPanel content = new JPanel();
		if (!Theme.isDark()) {
			content.setBorder(new LineBorder(Theme.textPrimaryColor, 1));
		}
		if (type.equalsIgnoreCase("yesno")) buildYesNo(content);
		if (type.equalsIgnoreCase("cancelok")) buildCancelOk(content);
		if (type.equalsIgnoreCase("cancel")) buildCancel(content);
		if (type.equalsIgnoreCase("ok")) buildOk(content);
		this.add(content);
		
		//Set position
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		
		//TODO Make window close if user clicks out
		
		this.setVisible(true);
		this.pack();
	}
	
	/**
	 * Adds the content and yes and no buttons to the entered panel.
	 * @param panel - Main pop up window panel
	 */
	public void buildYesNo(JPanel panel) {
		panel.setBackground(paneColor);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Content
		c.insets = new Insets(20,40,20,40);
		c.fill = GridBagConstraints.BOTH;
   		c.gridwidth = 2;
   		c.weightx = 0.00;
      	c.weighty = 0.1;
      	c.gridx = 0;
      	c.gridy = 0;
      	panel.add(messagePanel, c);

		// Yes Button
		CustomButton yesButton = new CustomButton("Yes", 
				Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor); 
        yesButton.setFont(new Font("Arial", Font.BOLD, 14));
        yesButton.setBorderPainted(false);
        yesButton.setForeground(Theme.textPrimaryColor);
        yesButton.setFocusPainted(false);
        yesButton.addActionListener(listener);
        yesButton.addActionListener(this);
        yesButton.setPreferredSize(buttonSize);
        c.insets = new Insets(10,10,10,5);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
     	c.gridwidth = 1;
        c.weightx = 1.00;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(yesButton, c);
		
		// No Button
        CustomButton noButton = new CustomButton("No", 
        		Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor); 
        noButton.setFont(new Font("Arial", Font.BOLD, 14));
        noButton.setBorderPainted(false);
        noButton.setForeground(Theme.textPrimaryColor);
        noButton.setFocusPainted(false);
        noButton.addActionListener(listener);
        noButton.addActionListener(this);
        noButton.setPreferredSize(buttonSize);
        c.insets = new Insets(10,5,10,10);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
     	c.gridwidth = 1;
        c.weightx = 1.00;
        c.weighty = 0.0;
        c.gridx = 1;
        c.gridy = 1;
        panel.add(noButton, c);
	}
	
	/**
	 * Adds the content and Cancel and OK buttons to the entered panel.
	 * @param panel - Main pop up window panel
	 */
	public void buildCancelOk(JPanel panel) {
		panel.setBackground(paneColor);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Content
		c.insets = new Insets(20,40,20,40);
		c.fill = GridBagConstraints.BOTH;
   		c.gridwidth = 2;
   		c.weightx = 0.00;
      	c.weighty = 0.1;
      	c.gridx = 0;
      	c.gridy = 0;
      	panel.add(messagePanel, c);
      	
      	// Cancel Button
      	CustomButton cancelButton = new CustomButton("Cancel", 
      			Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor); 
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBorderPainted(false);
        cancelButton.setForeground(Theme.textPrimaryColor);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(listener);
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(buttonSize);
        c.insets = new Insets(10,10,10,5);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.LINE_END;
        c.gridwidth = 1;
        c.weightx = 1.00;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(cancelButton, c);
	
        // OK Button
        CustomButton okButton = new CustomButton("OK", 
        		Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor); 
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBorderPainted(false);
    	okButton.setForeground(Theme.textPrimaryColor);
    	okButton.setFocusPainted(false);
    	okButton.addActionListener(listener);
    	okButton.addActionListener(this);
    	okButton.setPreferredSize(buttonSize);
    	c.insets = new Insets(10,5,10,10);
    	c.fill = GridBagConstraints.NONE;
    	c.anchor = GridBagConstraints.LINE_START;
    	c.gridwidth = 1;
    	c.weightx = 1.00;
    	c.weighty = 0.0;
    	c.gridx = 1;
    	c.gridy = 1;
    	panel.add(okButton, c);
	
	}
	
	/**
	 * Adds the content and an Cancel button to the entered panel.
	 * @param panel - Main pop up window panel
	 */
	public void buildCancel(JPanel panel) {
		panel.setBackground(paneColor);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Content
		c.insets = new Insets(20,40,20,40);
		c.fill = GridBagConstraints.BOTH;
   		c.gridwidth = 2;
   		c.weightx = 0.00;
      	c.weighty = 0.1;
      	c.gridx = 0;
      	c.gridy = 0;
      	panel.add(messagePanel, c);
      	
      	// Cancel Button
      	CustomButton cancelButton = new CustomButton("Cancel", 
      			Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor); 
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBorderPainted(false);
        cancelButton.setForeground(Theme.textPrimaryColor);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(listener);
        cancelButton.addActionListener(this);
        cancelButton.setPreferredSize(buttonSize);
        c.insets = new Insets(10,10,10,5);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;
        c.weightx = 1.00;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(cancelButton, c);
	}
	
	/**
	 * Adds the content and an OK button to the entered panel.
	 * @param panel - Main pop up window panel
	 */
	public void buildOk(JPanel panel) {
		panel.setBackground(paneColor);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Content
		c.insets = new Insets(20,40,20,40);
		c.fill = GridBagConstraints.BOTH;
   		c.gridwidth = 2;
   		c.weightx = 0.00;
      	c.weighty = 0.1;
      	c.gridx = 0;
      	c.gridy = 0;
      	panel.add(messagePanel, c);
      	
      	// OK Button
      	CustomButton okButton = new CustomButton("OK", Theme.popupButtonColor, Theme.popupHoverColor, Theme.popupPressedColor); 
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBorderPainted(false);
        okButton.setForeground(Theme.textPrimaryColor);
        okButton.setFocusPainted(false);
        okButton.addActionListener(listener);
        okButton.addActionListener(this);
        okButton.setPreferredSize(buttonSize);
        c.insets = new Insets(10,10,10,5);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 2;
        c.weightx = 1.00;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 1;
        panel.add(okButton, c);
	}
	
	/**
	 * Close the pop up when any button on the pop up is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	}
}

