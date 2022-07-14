package main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import main.java.ui.Theme;

/**
 * An interface containing standardized methods for page creation and database connection.
 * 
 * @author Nicolas Gonzalez
 *
 */
public interface Page {
	
	// Database Connection
	String dbUrl = "jdbc:mysql://localhost:3306/apartment_manager"; // TODO Change to your database url
	String dbUsername = "apartment-manager-session"; // Permission to select, insert, and update
	String dbPassword = "password";
	
	// Constants
	public static final Dimension MIN_SIZE = new Dimension(1,1);
	int PANE_MARGIN = 10;
	int TABLE_CELL_HEIGHT = 20;
	int FIELD_MARGIN = 10;
	String DEFAULT_DATE_TEXT = "mm/dd/yyyy";
	NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

	/*
	 * Panel Setup
	 */
	
	/**
	 * @return Empty panel with round corners (Theme)
	 */
	@SuppressWarnings("serial")
	default public JPanel buildPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(Theme.baseColor);
		panel.setLayout(new BorderLayout());
		panel.setBorder(new LineBorder(Theme.paneColor, 10, true) {
			
			/**
			 * Custom border with round corners, different for dark/light modes.
			 */
			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		        if ((this.thickness > 0) && (g instanceof Graphics2D)) {
		            Graphics2D g2d = (Graphics2D) g;
		            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                        RenderingHints.VALUE_ANTIALIAS_ON);

		            // Rounded corners border
		            g2d.setColor(this.lineColor);
		            Shape outer;
		            Shape inner;
		            int offs = this.thickness;
		            int margins = offs + offs;
		            if (this.roundedCorners) {
		                float arc = .2f * offs;
		                outer = new RoundRectangle2D.Float(x, y, width, height, offs, offs);
		                inner = new RoundRectangle2D.Float(x + offs, y + offs, width - margins, height - margins, arc, arc);
		            }
		            else {
		                outer = new Rectangle2D.Float(x, y, width, height);
		                inner = new Rectangle2D.Float(x + offs, y + offs, width - margins, height - margins);
		            }
		            Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
		            path.append(outer, false);
		            path.append(inner, false);
		            g2d.fill(path);
		            
		            // Extra border on the outside perimeter of main border if in light mode. (thin)
		            if (!Theme.isDark()) {
		            	RoundRectangle2D lineBorder = new RoundRectangle2D.Float(x, y, width - 1, height - 1, offs, offs);
		            	g2d.setColor(Theme.borderColor);
		            	g2d.draw(lineBorder);
		            }
		        }
		    }
			
		});
		return panel;
	}
	
	/**
	 * Builds and returns a panel with a header. (Theme)
	 * @param title - Title to display at the header of the panel
	 * @return A panel with a header and an empty BorderLayout.CENTER
	 */
	default public JPanel buildPanel(String title) {
		JPanel panel = buildPanel();
		
		// Title label
		JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		titleLabel.setForeground(Theme.textPrimaryColor);
		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		titleLabel.setOpaque(true);
		titleLabel.setBackground(Theme.paneColor);
		panel.add(titleLabel, BorderLayout.NORTH);
		
		return panel;
	}
	
	/**
	 * Builds and returns a panel with a header (Option to have a minimize button)
	 * @param title - Title to display at the header of the panel
	 * @param collapsible - Whether the panel is can be collapsed and expanded
	 * @param content - Reference to panel's inside content (to alter visibility)
	 * @return A panel that can be collapsed with a title and a minimize button at the header
	 */
	default public JPanel buildPanel(String title, boolean collapsible, JPanel content) {
		if (collapsible) {
			JPanel panel = buildPanel();
			
			// Header container
			JPanel headerContainer = new JPanel();
			headerContainer.setOpaque(false);
			headerContainer.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			// Title
			JLabel titleLabel = new JLabel(title);
	        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
			titleLabel.setForeground(Theme.textPrimaryColor);
			titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			titleLabel.setOpaque(true);
			titleLabel.setBackground(Theme.paneColor);
			c.fill = GridBagConstraints.BOTH;
	        c.weightx = 1.0;
	        c.gridx = 0;
	        headerContainer.add(titleLabel, c);
	        
	        // Collapse Button
	        CustomButton collapseSwitch = new CustomButton("+", Theme.buttonColor, Theme.hoverColor, Theme.pressedColor, 
	        		true, Theme.paneColor);
	        collapseSwitch.setFont(new Font("Arial", Font.BOLD, 30));
	        collapseSwitch.setBorderPainted(false);
	        collapseSwitch.setForeground(Theme.textPrimaryColor);
	        collapseSwitch.setFocusPainted(false);
	        collapseSwitch.setBorder(new EmptyBorder(0, 0, 0, 0));
	        collapseSwitch.setMinimumSize(MIN_SIZE);
	        collapseSwitch.setPreferredSize(new Dimension(22, 22));
	        collapseSwitch.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (content.isVisible()) content.setVisible(false);
					else content.setVisible(true);
					
					if (content.isVisible()) collapseSwitch.setText("-");
					else collapseSwitch.setText("+");
				}
	        	
	        });
	        c.fill = GridBagConstraints.NONE;
	        c.weightx = 0.0;
	        c.gridx = 1;
	        headerContainer.add(collapseSwitch, c);
			
			// Add header container to panel
			panel.add(headerContainer, BorderLayout.NORTH);
			return panel;	
		}
		else return buildPanel(title); // if collapsible = false
	}

	/**
	 * To override
	 */
	default public void refreshAll() {
		return;
	}
	
	/*
	 * Utilities
	 */
	
	/**
	 * Builds a panel with a big name on top of a small one underneath.
	 * @param big
	 * @param small
	 * @return A JPanel with a two stacked labels
	 */
	default public JPanel buildTwoNameDisplay(String big, String small) {
		// Container
		JPanel twoNameDisplay = new JPanel();
		twoNameDisplay.setOpaque(false);
		twoNameDisplay.setLayout(new GridBagLayout());
		twoNameDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints c = new GridBagConstraints();
		
		// Big name
		JLabel firstNameDisplay = new JLabel(big, SwingConstants.LEFT);
		firstNameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		firstNameDisplay.setForeground(Theme.textPrimaryColor);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weightx = 0.1;
        c.anchor = GridBagConstraints.WEST;
        twoNameDisplay.add(firstNameDisplay, c);
		
		// Small name
		JLabel secondNameDisplay = new JLabel(small, SwingConstants.LEFT);
		secondNameDisplay.setFont(new Font("Arial", Font.PLAIN, 9));
		secondNameDisplay.setForeground(Theme.textSecondaryColor);
        c.gridy = 1;
        twoNameDisplay.add(secondNameDisplay, c);
		
		return twoNameDisplay;			
	}
	
	/**
	 * Builds a panel with a small label on top and a big label at the bottom.
	 * Used to display a label with a big figure underneath.
	 * @param labelText - Small label on top
	 * @param valueText - Large value
	 * @return A JPanel with two stacked labels
	 */
	default public JPanel buildLabelValueDisplay(String labelText, String valueText) {
		JPanel container = new JPanel();
		container.setOpaque(false);
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel label = new JLabel(labelText, SwingConstants.LEFT);
		label.setFont(new Font("Arial", Font.PLAIN, 14));
		label.setForeground(Theme.textSecondaryColor);
		label.setBorder(new EmptyBorder(new Insets(0, 4, 0, 4)));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		container.add(label, c);
		
		JLabel value = new JLabel(valueText, SwingConstants.LEFT);
		value.setFont(new Font("Arial", Font.PLAIN, 30));
		value.setForeground(Theme.textPrimaryColor);
		value.setBorder(new EmptyBorder(new Insets(-2, 4, 0, 0)));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		container.add(value, c);
		
		return container;
	}
	
	/**
	 * Builds a panel with a small label on top and a big label and a small label at the bottom.
	 * Used to display a label with a big figure underneath and a unit.
	 * @param labelText - Small label on top
	 * @param valueText - Large value
	 * @param unit - Small label next to valueText
	 * @return A JPanel with three labels
	 */
	default public JPanel buildLabelValueDisplay(String labelText, String valueText, String unit) {
		JPanel container = buildLabelValueDisplay(labelText, valueText);
		GridBagConstraints c = new GridBagConstraints();

		JLabel unitLabel = new JLabel(unit, SwingConstants.LEFT);
		unitLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		unitLabel.setForeground(Theme.textPrimaryColor);
		unitLabel.setBorder(new EmptyBorder(new Insets(-2, 0, 0, 4)));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		container.add(unitLabel, c);
		
		return container;
	}
	
	/**
	 * Builds a percentage bar of variable size.
	 * @param value - percentage in decimal format
	 * @return A panel with a plain percentage bar
	 */
	default public JPanel buildPercentageBar(double value) {
		// Container
		JPanel percentageBar = new JPanel();
		percentageBar.setOpaque(false);
		percentageBar.setLayout(new GridBagLayout());
		
		// Calculate weights
		double positiveWeight = value;
		double negativeWeight = (1 - value);
		
		// Build Percentage bar
		GridBagConstraints c = new GridBagConstraints();
		
		// Left side of bar
		JPanel positivePercentage = new JPanel();
		positivePercentage.setBackground(Theme.textPrimaryColor);
		positivePercentage.setPreferredSize(new Dimension(1, 15));
		positivePercentage.setMinimumSize(new Dimension(1, 15));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = positiveWeight;
		c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        percentageBar.add(positivePercentage, c);
		
        // Right side of bar
		JPanel negativePercentage = new JPanel();
		negativePercentage.setBackground(Theme.fieldColor);
		negativePercentage.setPreferredSize(new Dimension(1, 15));
		negativePercentage.setMinimumSize(new Dimension(1, 15));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = negativeWeight;
		c.weighty = 0.0;
        c.gridx = 1;
        c.gridy = 0;
        percentageBar.add(negativePercentage, c);
        
        return percentageBar;
	}
	
	/**
	 * Builds and returns panel with input user's contact information
	 * @param name
	 * @param user
	 * @param email
	 * @param phone
	 * @return panel with input user's contact information
	 */
	default public JPanel buildAccountContactDisplay(String name, String user, String email, String phone) {
		// Container
		JPanel accountContactDisplay = new JPanel();
		accountContactDisplay.setOpaque(false);
		accountContactDisplay.setLayout(new GridBagLayout());
		accountContactDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints c = new GridBagConstraints();
		
		// Name
		JLabel nameDisplay = new JLabel(name, SwingConstants.CENTER);
		nameDisplay.setFont(new Font("Arial", Font.PLAIN, 25));
		nameDisplay.setForeground(Theme.textPrimaryColor);
		//nameDisplay.setOpaque(true);
		c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weightx = 0.1;
        c.anchor = GridBagConstraints.WEST;
        accountContactDisplay.add(nameDisplay, c);
		
		// User name
		JLabel usernameDisplay = new JLabel(user, SwingConstants.CENTER);
		usernameDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
		usernameDisplay.setForeground(Theme.textSecondaryColor);
        c.gridy = 1;
        accountContactDisplay.add(usernameDisplay, c);
        
        // Space
        JPanel contactSpace = new JPanel();
        contactSpace.setOpaque(false);
        c.gridy = 2;
        c.weighty = 0.2;
        accountContactDisplay.add(contactSpace, c);
        
        // Email
        JLabel emailDisplay = new JLabel(email, SwingConstants.CENTER);
        emailDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
        emailDisplay.setForeground(Theme.textPrimaryColor);
        c.weighty = 0.0;
        c.gridy = 3;
        accountContactDisplay.add(emailDisplay, c);
        
        // Phone number
        JLabel phoneDisplay = new JLabel(phone, SwingConstants.CENTER);
        phoneDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
        phoneDisplay.setForeground(Theme.textPrimaryColor);
        c.gridy = 4;
        accountContactDisplay.add(phoneDisplay, c);
        
		return accountContactDisplay;			
	}
	
	/**
	 * Concatenate two names
	 * @param firstName
	 * @param lastName
	 * @return "firstName lastName"
	 */
	default public String combineNames(String firstName, String lastName) {
		if (firstName == null && lastName == null) return "";
		else if (firstName == null) return lastName;
		else if (lastName == null) return firstName;
		return firstName + " " + lastName;
	}
	
	/**
	 * @param color
	 * @param radius
	 * @return a circular JPanel
	 */
	default public JPanel getStatusCircle(Color color, int radius) {
		@SuppressWarnings("serial")
		JPanel circle = new JPanel() {
			
			@Override
			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(color);
				g2d.fillOval(0,0, radius * 2, radius * 2);

			}
		};
		circle.setPreferredSize(new Dimension(radius * 2,radius * 2));
		circle.setMinimumSize(new Dimension(radius * 2,radius * 2));
		return circle;
	}
	
	
	/*
	 * Validation
	 */
	
	/**
	 * Checks if an entered date fits the default date format.
	 * @param date - String entered
	 * @return The entered string parsed into a LocalDate object
	 */
	default public LocalDate validateDateEntry(String date) {
		if (date == null) return null;
		LocalDate dateObject = null;
		
		// Check length and slashes
		if (date.length() == 10 
				&& date.substring(2,3).equals("/")
				&& date.substring(5,6).equals("/")) {
			
			// Check numbers
			String monthEntry = date.substring(0, 2);
			String dayEntry = date.substring(3, 5);
			String yearEntry = date.substring(6, 10);
			if (monthEntry.matches("[0-9]+") 
					&& dayEntry.matches("[0-9]+") 
					&& yearEntry.matches("[0-9]+")) {
				// Create date object with entry
				dateObject = LocalDate.of(Integer.valueOf(yearEntry), 
						Integer.valueOf(monthEntry), Integer.valueOf(dayEntry));
			}
		}
		return dateObject;
	}
	
	/**
	 * Checks that an entered monetary amount fits the default format.
	 * @param entry - String entered
	 * @return The entered string parsed into a double
	 */
	default public double validateMoneyEntry(String entry) {
		String amount = entry.replace(",", "");
		if (amount.contains(".")){
			return Double.parseDouble(amount);	
		}
		else {
			if (entry.matches("[0-9]+")) return Double.parseDouble(entry);
		}
		return 0;
	}
	
	/*
	 * SQL Methods
	 */
	
	/**
	 * Returns a ResultSet in ArrayList<Object[]> format.
	 * @param results
	 * @return An ArrayList of rows
	 */
	default public ArrayList<Object[]> sqlToArray(ResultSet results){
		ArrayList<Object[]> table = new ArrayList<Object[]>();
		try {
			int columnCount = results.getMetaData().getColumnCount();
			// Fill Array
			while (results.next()) {
				Object[] row = new Object[columnCount];
				// Fill row
				for (int i = 1; i <= columnCount; i++) {
					//if(row[i - 1] != null) 
					row[i - 1] = results.getObject(i);
				}	
				table.add(row);
			}
		} 
		
		catch (SQLException e) {
			e.printStackTrace();
		}

		return table;
	}
	
	/*
	 * Custom Classes
	 */
	
	/**
	 * Custom JButton
	 */
	@SuppressWarnings("serial")
	class CustomButton extends JButton {
        private Color hoverBackgroundColor;
        private Color pressedBackgroundColor;
        private Color baseBackground; // For round corners
        private boolean isRound;

        /**
         * Creates button with no text and default colors.
         */
        public CustomButton() {
            this(null);
            isRound = false;
        }

        /**
         * Creates a button with the entered text.
         * @param text
         */
        public CustomButton(String text) {
            super(text);
            super.setContentAreaFilled(false);
            isRound = false;
        }
        
        /**
         * Creates a button with the entered text and colors.
         * @param text
         * @param background - Button background color
         * @param hover - Button hover color
         * @param pressed - Button pressed color
         */
        public CustomButton(String text, Color background, Color hover, Color pressed) {
            super(text);
            super.setContentAreaFilled(false);
            this.hoverBackgroundColor = hover;
            this.pressedBackgroundColor = pressed;
            this.setBackground(background);
            isRound = false;
        }
        
        /**
         * Creates a button with the entered text and colors with the option of rounded corners.
         * @param text
         * @param background - Button background color
         * @param hover - Button hover color
         * @param pressed - Button pressed color
         * @param round - Whether the button has rounded corners
         * @param baseBackground - color behind the button
         */
        public CustomButton(String text, Color background, Color hover, Color pressed, 
        		boolean round, Color baseBackground) {
        	this(text, background, hover, pressed);
        	isRound = true;
        	this.baseBackground = baseBackground;
        }

        /**
         * Paints the button.
         */
        @Override
        protected void paintComponent(Graphics g) {
        	// Colors
        	Color currentColor;
            if (getModel().isPressed()) currentColor = pressedBackgroundColor;
            else if (getModel().isRollover()) currentColor = hoverBackgroundColor;
            else currentColor = getBackground();
            
            //Draw the button
            if (isRound) {
            	g.setColor(baseBackground);
            	g.fillRect(0, 0, getWidth(), getHeight());
            	
            	g.setColor(currentColor);
            	g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!Theme.isDark()) {
                	g.setColor(Theme.buttonOutline);
            		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            	}
            }
            else {
            	g.setColor(currentColor);
            	g.fillRect(0, 0, getWidth(), getHeight());
	            if (!Theme.isDark()) {
	            	g.setColor(Theme.buttonOutline);
	        		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	        	}
            }
            super.paintComponent(g);
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
	 * A check box icon.
	 */
	public class checkBoxIcon implements Icon {
		Color color;
		String string;
		boolean withChar = false;
		
		public checkBoxIcon(Color color) {
			this.color = color;
			withChar = false;
		}
		
		public checkBoxIcon(Color color, String string) {
			this.color = color;
			this.string = string;
			withChar = true;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Color oldColor = g.getColor();
		    g.setColor(color);
		    g.fillRect(x, y, getIconWidth(), getIconHeight());
		    g.setColor(Theme.fieldTextColor);
		    if (withChar) g.drawString(this.string, x + 2, y + getIconHeight() - 3);
		    g.setColor(oldColor);
		}

		@Override
		public int getIconWidth() {
			return 15;
		}

		@Override
		public int getIconHeight() {
			return 15;
		}
 	}
	
	/**
	 * A custom scroll bar.
	 */
	class CustomScrollBarUI extends BasicScrollBarUI{
		private final Dimension d = new Dimension();
		Color backgroundColor;
		
		public CustomScrollBarUI(Color barBackground) {
			super();
			this.backgroundColor = barBackground;
		}
		
		@SuppressWarnings("serial")
		@Override
		protected JButton createDecreaseButton(int orientation) {
			return new JButton() {
			    @Override
			    public Dimension getPreferredSize() {
			    return d;
			    }
			};
		}

		@SuppressWarnings("serial")
		@Override
		protected JButton createIncreaseButton(int orientation) {
			return new JButton() {
			    @Override
			    public Dimension getPreferredSize() {
			    return d;
			    }
			};
		}

		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
			Graphics2D g2d = (Graphics2D) g.create();
		    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    g2d.setPaint(backgroundColor);
		    g2d.fillRoundRect(r.x, r.y, r.width, r.height, 0, 0);
		    g2d.dispose();
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
			Graphics2D g2d = (Graphics2D) g.create();
		    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		    
		    Color color = null;
		    JScrollBar sb = (JScrollBar) c;
		    
		    if (!sb.isEnabled() || r.width > r.height) return;
		    else if (isDragging) color = Theme.scrollBarDragging;
		    else if (isThumbRollover()) color = Theme.scrollBarHover;
		    else color = Theme.scrollBar;
		    
		    g2d.setPaint(color);
		    g2d.fillRoundRect(r.x, r.y, r.width, r.height, 0, 0);
		    g2d.dispose();
		}

		@Override
		protected void setThumbBounds(int x, int y, int width, int height) {
			super.setThumbBounds(x, y, width, height);
		}
	}
	
	/**
	 * Creates a JImageComponent with the parameter image.
	 */
	@SuppressWarnings("serial")
	class JImageComponent extends JComponent {
		Image image;
		
		public JImageComponent(Image image) {
	        this.image = image;
	    }
	    
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, 0, 0, this);
	    }
	}

}
