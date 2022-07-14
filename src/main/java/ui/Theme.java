package main.java.ui;

import java.awt.Color;

/**
 * A class representing the color theme of this application. 
 * 
 * @author Nicolas Gonzalez
 *
 */
public class Theme {
	// Layout
	public static Color baseColor;
	public static Color paneColor;
	public static Color paneLightColor;
	public static Color borderColor;
	
	// Text
	public static Color textPrimaryColor;
	public static Color textSecondaryColor;
	public static Color textThirdColor;
	
	// Colors
	public static Color redColor;
	public static Color greenColor;
	public static Color darkGreenColor;
	public static Color yellowColor;
	
	// Fields
	public static Color fieldColor;
	public static Color fieldTextColor;
	
	// Buttons
	public static Color buttonColor;
	public static Color pressedColor;
	public static Color hoverColor;
	
	public static Color pressedHoverColor;
	public static Color buttonHold;
	public static Color buttonHoldText;
	
	public static Color buttonOutline;
	
	// Scroll Bar
	public static Color scrollBar;
	public static Color scrollBarDragging;
	public static Color scrollBarHover;
	
	// Flags
	public static Color flagText;
	public static Color newStatus;
	
	// Profile Picture
	public static Color profilePictureBackground;
	public static Color profilePictureColor;
	
	// POPUP
	public static Color popupColor;
	
	// POPUP BUTTONS
	public static Color popupButtonColor;
	public static Color popupPressedColor;
	public static Color popupHoverColor;
	
	// POPUP FIELDS
	public static Color popupFieldColor;
	
	public static boolean isDark = true;

	/**
	 * Sets the theme to Dark Mode
	 */
	public static void setDark() {
		// Layout
		baseColor = Color.decode("#121212");
		paneColor = Color.decode("#212121");
		paneLightColor = Color.decode("#242424");
		borderColor = Color.decode("#CCCCCC"); // Light mode only
		
		// Text
		textPrimaryColor = Color.decode("#FFFFFF");
		textSecondaryColor = Color.decode("#999999");
		textThirdColor = Color.decode("#333333");
		
		// Colors
		redColor = Color.decode("#AE6B6B");
		greenColor = Color.decode("#52b788");
		darkGreenColor = Color.decode("#0D1C15");
		yellowColor = Color.decode("#FFE69A");
		
		// Fields
		fieldColor = Color.decode("#1C1C1C");
		fieldTextColor = Color.white;
		
		// Buttons
		buttonColor = Color.decode("#2B2B2B");
		pressedColor = Color.decode("#262626");
		hoverColor = Color.decode("#292929");
		
		buttonHoldText = Color.black;
		pressedHoverColor = Color.decode("#FCFCFC");
		buttonHold = Color.white;
		
		buttonOutline = Color.black; // Light mode only
		
		// Scroll Bar
		scrollBar = textSecondaryColor;
		scrollBarDragging = Color.decode("#FFFFFF");
		scrollBarHover = Color.decode("#FFFFFF");
		
		// Flags
		flagText = Color.black;
		newStatus = Color.white;
		
		// Profile Picture
		profilePictureBackground = baseColor;
		profilePictureColor = Color.decode("#FFFFFF");

		// Pop-Up
		popupColor = Color.decode("#262626");
		
		// Pop-Up Buttons
		popupButtonColor = Color.decode("#303030");
		popupPressedColor = Color.decode("#2B2B2B");
		popupHoverColor = Color.decode("#2E2E2E");
		
		// Pop-Up Fields
		popupFieldColor = Color.decode("#212121");
		
		isDark = true;
	}
	
	/**
	 * Sets the theme to Light Mode
	 */
	public static void setLight() {
		// Layout
		baseColor = Color.decode("#E3E3E3");
		paneColor = Color.decode("#FFFFFF");
		paneLightColor = Color.decode("#F2F2F2");
		borderColor = Color.decode("#CCCCCC");
		
		// Text
		textPrimaryColor = Color.black;
		textSecondaryColor = Color.decode("#4D4D4D");
		textThirdColor = Color.decode("#E6E6E6");
		
		// Colors
		redColor = Color.decode("#B00020");
		greenColor = Color.decode("#1F9D00");
		darkGreenColor = Color.decode("#0D1C15");
		yellowColor = Color.decode("#E09D00");
		
		// Fields
		fieldColor = Color.decode("#E6E6E6");
		fieldTextColor = Color.black;
		
		// Buttons
		buttonColor = Color.white;
		pressedColor = Color.decode("#A3A3A3");
		hoverColor = Color.decode("#CCCCCC");
		
		buttonHoldText = Color.black;
		pressedHoverColor = pressedColor;
		buttonHold = hoverColor;
		
		buttonOutline = Color.black;
		
		// Scroll Bar
		scrollBar = textSecondaryColor;
		scrollBarDragging = Color.decode("#FFFFFF");
		scrollBarHover = Color.decode("#FFFFFF");
		
		// Flags
		flagText = Color.white;
		newStatus = Color.decode("#0797f7");
		
		// Profile Picture
		profilePictureBackground = Color.decode("#000000");
		profilePictureColor = Color.decode("#FFFFFF");
		
		// Pop-Up
		popupColor = Color.decode("#FFFFFF");
		
		// Pop-Up Buttons
		popupButtonColor = buttonColor;
		popupPressedColor = pressedColor;
		popupHoverColor = hoverColor;
		
		// Pop-Up Fields
		popupFieldColor = fieldColor;
		
		isDark = false;
	}
	
	/**
	 * @return true if theme is in Dark Mode
	 */
	public static boolean isDark() {
		return isDark;
	}
}

