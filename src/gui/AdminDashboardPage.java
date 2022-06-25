package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import unit.Building;
import unit.BuildingList;

/**
 * A class that creates an Admin Dashboard page consisting of 
 * four panels: View (building selection), Rents, Revenue, and Vacancy.
 * These panels display data based on the selected view.
 * 
 * @author Nicolas Gonzalez
 *
 */
@SuppressWarnings("serial")
public class AdminDashboardPage extends JPanel implements Page {
	
	// Data
	private BuildingList buildingList;
	private DashboardData displayData;
	
	// Setup
	private String title = "Dashboard";
	
	// Panels
	private JPanel viewPanel;
	private JPanel rentInfoPanel;
	private JPanel revenuePanel;
	private JPanel vacancyRatePanel;
	
	// Panel Content (refresh)
	private JPanel viewContent;
	private JPanel rentInfoContent;
	private JPanel revenueContent;
	private JPanel vacancyContent;
	
	// Colors
	private Color backgroundColor = Theme.baseColor;
	private Color paneColor = Theme.paneColor;
	private Color textColor = Theme.textSecondaryColor;
	private Color textColorTitle = Theme.textPrimaryColor;
	private Color fieldColor = Theme.fieldColor; 
	private Color textThirdColor = Theme.textThirdColor;
	
	/**
	 * Creates an Admin Dashboard page. 
	 * Creates a DashboardData object to access statistical information 
	 * regarding the list of buildings.
	 * @param buildingList - List of buildings to display data for
	 */
	public AdminDashboardPage(BuildingList buildingList) {
		
		// Data
		this.buildingList = buildingList;
		if (displayData == null) displayData = new DashboardData(buildingList);
	
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
        
        // Top row container
        JPanel firstRowPanels = new JPanel();
        firstRowPanels.setLayout(new GridBagLayout());
        firstRowPanels.setOpaque(false);
        
        // View
        viewPanel = buildViewPanel(buildPanel("View"));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(paneMargin,0,0,paneMargin);
        firstRowPanels.add(viewPanel, c);

        // Rents
        rentInfoPanel = buildRentPanel(buildPanel("Rents"));
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 0;
        //c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(paneMargin,0,0,0);
        firstRowPanels.add(rentInfoPanel, c);
        
        // Add top row container
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.1;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0,0,0,0);
        tabContent.add(firstRowPanels, c);
        
        // Second row container
        JPanel secondRowPanels = new JPanel();
        secondRowPanels.setLayout(new GridBagLayout());
        secondRowPanels.setOpaque(false);
        
        // Revenue
        revenuePanel = buildRevenuePanel(buildPanel("Revenue"));
        c.weightx = 0.5;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        //c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(paneMargin,0,0,paneMargin);
        secondRowPanels.add(revenuePanel, c);
        
        // Vacancy
        vacancyRatePanel = buildVacancyRatePanel(buildPanel("Vacancy"));
        c.weightx = 0.4;
        c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        //c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(paneMargin,0,0,0);
        secondRowPanels.add(vacancyRatePanel, c);
        
        // Add second row container
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0,0,0,0);
        tabContent.add(secondRowPanels, c); 
	}
	
	/*
	 * View Panel
	 */

	/**
	 * Fills a panel with a building selector with basic 
	 * information about the selection
	 * @param panel - Empty theme panel to use as a view selector
	 * @return a panel with View panel content
	 */
	private JPanel buildViewPanel(JPanel panel) {
		
		// Container
		viewContent = new JPanel();
		viewContent.setLayout(new GridBagLayout());
		viewContent.setOpaque(true);
		viewContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
        
		/*
		 * Building Selector
		 */
		
		// Container
		JPanel buildingSelectorContainer = new JPanel();
		buildingSelectorContainer.setLayout(new GridBagLayout());
		buildingSelectorContainer.setOpaque(false);
		
		// List of buildings
		JPanel buildingSelection = new JPanel();
		buildingSelection.setOpaque(true);
		buildingSelection.setBackground(paneColor);
		buildingSelection.setLayout(new GridBagLayout());
		
		// All Buildings Button
		CustomButton allBuildingButton = new CustomButton("ALL", Theme.buttonColor, 
				Theme.hoverColor, Theme.pressedColor);
		allBuildingButton.setFont(new Font("Arial", Font.PLAIN, 14));
		allBuildingButton.setBorderPainted(false);
		allBuildingButton.setForeground(textColorTitle);
		allBuildingButton.setFocusPainted(false);
		allBuildingButton.setBorder(new EmptyBorder(2, 4, 2, 4));
		allBuildingButton.setHorizontalAlignment(SwingConstants.LEFT);
		allBuildingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				displayData = new DashboardData(buildingList); // Get DashboardData on all buildings
				refreshAll();
			}
			
        });
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,1,0);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		buildingSelection.add(allBuildingButton, c);
		
		// Loop (every building)
		int buildingRow = 1;
		for (Building b : buildingList.getAllBuildings()) {
			
			// Build button
			CustomButton buildingButton = new CustomButton(b.getName(), Theme.buttonColor, 
					Theme.hoverColor, Theme.pressedColor);
			buildingButton.setFont(new Font("Arial", Font.PLAIN, 14));
			buildingButton.setBorderPainted(false);
			buildingButton.setForeground(textColorTitle);
			buildingButton.setFocusPainted(false);
			buildingButton.setBorder(new EmptyBorder(2, 4, 2, 4));
			buildingButton.setHorizontalAlignment(SwingConstants.LEFT);
			buildingButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					displayData = new DashboardData(b);
					refreshAll();
					
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
		
		// Push up
		JPanel buildingSelectionSpace = new JPanel();
		buildingSelectionSpace.setBackground(paneColor);
		buildingSelectionSpace.setMinimumSize(MIN_SIZE);
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = buildingRow + 1;
        c.weightx = 0.0;
		c.weighty = 0.01;
		c.gridwidth = 1;
		buildingSelection.add(buildingSelectionSpace, c);
		
		//Scroll
		JScrollPane buildingSelectorScroll = new JScrollPane(buildingSelection);
		buildingSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		buildingSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		buildingSelectorScroll.setBorder(new EmptyBorder(0,0,0,0)); 
		buildingSelectorScroll.setOpaque(false);
		buildingSelectorScroll.getVerticalScrollBar().setUnitIncrement(7);
		buildingSelectorScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI(fieldColor));
		buildingSelectorScroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		c.insets = new Insets(0,0,0,0);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		buildingSelectorContainer.add(buildingSelectorScroll, c);
		
		// Add container
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 3;
		viewContent.add(buildingSelectorContainer, c);
		
		/*
		 * Dividing Line
		 */
		
		JPanel midLineSpace = new JPanel();
		midLineSpace.setLayout(new GridBagLayout());
		midLineSpace.setOpaque(false);
		
		JPanel midLine = new JPanel();
		midLine.setBackground(textColor);
		midLine.setPreferredSize(MIN_SIZE);
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 1;
		midLineSpace.add(midLine, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.05;
		c.weighty = 1;
		c.gridwidth = 1;
		c.gridheight = 3;
		viewContent.add(midLineSpace, c);
		c.gridheight = 1;
		
		/*
		 * View Name
		 */
		
		// View Name Container
		JPanel viewNameLabelContainer = new JPanel();
		viewNameLabelContainer.setLayout(new GridBagLayout());
		viewNameLabelContainer.setBackground(fieldColor);

		//View Name
		JLabel viewNameLabel = new JLabel(displayData.getTitle(), SwingConstants.LEFT);
		viewNameLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		viewNameLabel.setForeground(textColorTitle);
		viewNameLabel.setMinimumSize(MIN_SIZE);
		viewNameLabel.setBorder(new EmptyBorder(new Insets(0, 4, 0, 4)));
		viewNameLabel.setPreferredSize(new Dimension(200, 35));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		viewNameLabelContainer.add(viewNameLabel, c);
		
		//Container
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 2;
		viewContent.add(viewNameLabelContainer, c);
		
		/*
		 * Info
		 */
		
		// Total Units
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 2;
        c.weightx = 0.1;
		c.weighty = 1.0;
		c.gridwidth = 1;
		viewContent.add(buildLabelValueDisplay("Total Units", Integer.toString(displayData.getTotalUnits())), c);
		
		// Occupied
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 3;
        c.gridy = 3;
        c.weightx = 0.1;
		c.weighty = 1.0;
		c.gridwidth = 1;
		viewContent.add(buildLabelValueDisplay("Occupied", Integer.toString(displayData.getOccupied())), c);

		// Area
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 4;
        c.gridy = 2;
        c.weightx = 0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		viewContent.add(buildLabelValueDisplay("Rentable Area", (NumberFormat.getNumberInstance(Locale.US).format(displayData.getRentableArea())), "sqft"), c);
		
		// Complaints
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 4;
        c.gridy = 3;
        c.weightx = 0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		viewContent.add(buildLabelValueDisplay("Open Complaints", Integer.toString(displayData.getOpenComplaints())), c);
		
		/*
         * Add all content container to panel
         */
		
		panel.add(viewContent);
		return panel;
	}
		
	/*
	 * Rents Panel
	 */

	/**
	 * Fills a panel with a value display and a graph.
	 * Displays information regarding rents.
	 * @param panel - Empty theme panel
	 * @return A panel with Rents content
	 */
	private JPanel buildRentPanel(JPanel panel) {
		// Container
		rentInfoContent = new JPanel();
		rentInfoContent.setLayout(new GridBagLayout());
		rentInfoContent.setOpaque(true);
		rentInfoContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
        
		/*
		 * Frame
		 */
	
		double horPaddingWeight = 0.01;

		JPanel leftPadding = new JPanel();
		leftPadding.setOpaque(false);
		c.gridx = 0;
        c.gridy = 1;
        c.weightx = horPaddingWeight;
		c.weighty = 0;
		c.gridwidth = 1;
		rentInfoContent.add(leftPadding, c);
		
		JPanel rightPadding = new JPanel();
		rightPadding.setOpaque(false);
		c.gridx = 6;
		rentInfoContent.add(rightPadding, c);
		
		/*
		 * Content
		 */
		
		// Average Rent/Area label
		double value = (double) displayData.getAverageRentPerArea().get(displayData.getAverageRentPerArea().size() - 1)[1];
		String valueString = dollarFormat.format(value);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 5;
        c.gridy = 1;
        c.weightx = 0.0;
		c.weighty = 0.2;
		c.gridwidth = 1;
		rentInfoContent.add(buildLabelValueDisplay("Average Rent/sqft", valueString), c);
		
		// Graph
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 5;
        c.gridy = 2;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 2;
		rentInfoContent.add(buildAverageRentGraph(displayData.getAverageRentPerArea()), c);
		c.gridheight = 1;
		
		/*
         * Add content container to panel 
         */
		
		panel.add(rentInfoContent);
		return panel;
	}
	
	/**
	 * Builds a graph with average rents over time.
	 * Used by buildRentPanel.
	 * @param data - Rent data
	 * @return A graph displaying the entered data
	 */
	private JPanel buildAverageRentGraph(ArrayList<Object[]> data) {
		// Container
		JPanel rentPerAreaGraph = new JPanel();
		rentPerAreaGraph.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Graph
		JPanel graph = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				/*
				 * Setup
				 */
				
				// Data
				ArrayList<Object[]> points = data;
				int periods = 13; // Number of columns
				
				// Graph
				int graphWidth = this.getWidth() - 1;
				int graphHeight = this.getHeight() - 1;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

				//Background
				Rectangle2D background = new Rectangle2D.Float(0, 0, this.getWidth(), this.getHeight());
				g2d.setColor(paneColor);
				g2d.fill(background);

				/*
				 * Plane
				 */
				
				int planeWidth = graphWidth - 30;
				int planeHeight = graphHeight - 45;
				
				int planeX = graphWidth - planeWidth;
				int planeY = 5;
				
				int xAxis = planeHeight + planeY;
				int yAxis = planeX;
				
				double scale = 0.01;
				
				// Empty Plane
				Rectangle2D plane = new Rectangle2D.Float(planeX, planeY, planeWidth, planeHeight);
				g2d.setColor(paneColor);
				g2d.fill(plane);
					
				/*
				 * Y-Axis
				 */
				
				// Calculate max and min
				double maxPoint = 0;
				double minPoint = 0;
				for (int i = points.size() - periods ; i < points.size(); i++) {
					double point = (double)points.get(i)[1];
							
					if (point > maxPoint) maxPoint = point;
					if (minPoint == 0) minPoint = point;
					if (point < minPoint) minPoint = point;
				}

				// Pad bounds
				double paddedMaxPoint = maxPoint + scale;
				double paddedMinPoint = minPoint - scale;
				double paddedPointRange = paddedMaxPoint - paddedMinPoint;
				
				// Calculate scaling
				int numberOfUnitsOnAxis = (int) Math.ceil(paddedPointRange / scale);
				double heightOfOneScaleUnit = planeHeight / (numberOfUnitsOnAxis);
				
				// Y Axis labels and lines
				int yCount = xAxis + 5;
				int spacing = 1;

				for (int i = 0; i < numberOfUnitsOnAxis + 1; i++) {
					
					double point = paddedMinPoint + (scale * i);
					g2d.setColor(textColorTitle);
					g2d.setFont(new Font("Arial", Font.PLAIN, 13));
					
					// Evens
					if ((point/scale) % 2 == 0) g2d.drawString((new DecimalFormat("0.00")).format(point), 0, yCount);
					
					// Thick line on multiples of 10 units
					if ((point/scale) % 10 == 0) {
						g2d.setColor(textColor);
						Rectangle2D tensLine = new Rectangle2D.Double(yAxis, yCount - 5, planeWidth, 0.7);
						g2d.draw(tensLine);
						g2d.setColor(textColorTitle);
					}
					
					//Lines
					g2d.setColor(textThirdColor);
					Rectangle2D planeLine = new Rectangle2D.Double(planeX, xAxis - (heightOfOneScaleUnit * i), planeWidth, 0.05);
					g2d.draw(planeLine);
					g2d.setColor(textColorTitle);
					
					yCount -= (heightOfOneScaleUnit * spacing);
				}
				
				/*
				 * X-Axis
				 */
				
				// Axis Setup
				int xAxisLabelY = xAxis + 10;
				int xAxisUnitWidth = planeWidth / periods;
				int xCount = planeX - (xAxisUnitWidth / 2) + 5;
	
				// Unit Labels (periods)
				for (int i = points.size() - periods ; i < points.size(); i++) {
					g2d.setFont(new Font("Arial", Font.PLAIN, 11));
					FontMetrics fontMetrics = g2d.getFontMetrics();
					AffineTransform old = g2d.getTransform();
			        g2d.rotate(Math.toRadians(-45), (xCount + xAxisUnitWidth), xAxisLabelY);
					g2d.drawString(
							((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("MMM")) + " ' " +
							((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("yy")), 
							(xCount + xAxisUnitWidth) - fontMetrics.stringWidth(
									((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("MMM")) + " ' " +
									((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("yy")))
							, xAxisLabelY);
					g2d.setTransform(old);
					
					xCount += xAxisUnitWidth;
				}
				
				/*
				 * Plot
				 */
				
				// Line Plot Variables
				int[] previous = {0, 0};
				int xChord = planeX;
				
				for (int i = points.size() - periods ; i < points.size(); i++) {
					
					// Position
					int yChord = (int)(xAxis - 
							((heightOfOneScaleUnit) * (int) (((((double)(points.get(i)[1])) - paddedMinPoint)/scale))));

					// Draw Line Plot
					g2d.setColor(textColorTitle);
					g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
					if (previous[0] != 0) {
						g2d.drawLine(xChord, previous[1], xChord, yChord);
					}
					g2d.drawLine(xChord, yChord, xChord + xAxisUnitWidth, yChord);

					previous[0] = xChord;
					previous[1] = yChord;
					xChord += xAxisUnitWidth;
				}	
			}	
			
		};
		c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		rentPerAreaGraph.add(graph, c);
		
		return rentPerAreaGraph;
	}
	
	/*
	 * Revenue Panel
	 */

	/**
	 * Fills a panel with a graph.
	 * Displays information regarding rental revenues.
	 * @param panel - Empty theme panel
	 * @return A panel with a revenue graph
	 */
	private JPanel buildRevenuePanel(JPanel panel) {
		// Container
		revenueContent = new JPanel();
		revenueContent.setLayout(new GridBagLayout());
		revenueContent.setOpaque(true);
		revenueContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
        
		/*
		 * Frame
		 */
	
		double horPaddingWeight = 0.01;

		JPanel leftPadding = new JPanel();
		leftPadding.setOpaque(false);
		c.gridx = 0;
        c.gridy = 1;
        c.weightx = horPaddingWeight;
		c.weighty = 0;
		c.gridwidth = 1;
		revenueContent.add(leftPadding, c);
		
		JPanel rightPadding = new JPanel();
		rightPadding.setOpaque(false);
		c.gridx = 2;
		revenueContent.add(rightPadding, c);
		
		JPanel topPadding = new JPanel();
		topPadding.setOpaque(false);
		topPadding.setMinimumSize(MIN_SIZE);
		c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
		c.weighty = 0.01;
		c.gridwidth = 1;
		revenueContent.add(topPadding, c);
		
		/*
		 * Content
		 */
		
		// Rental Revenue label
		JLabel label = new JLabel("Rental Revenue ($)", SwingConstants.LEFT);
		label.setFont(new Font("Arial", Font.PLAIN, 14));
		label.setForeground(textColor);
		label.setBorder(new EmptyBorder(new Insets(0, 4, 0, 4)));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		revenueContent.add(label, c);
		
		// Graph
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		revenueContent.add(buildRevenueGraph(displayData.getRents()), c);

		/*
         * Add content container to panel 
         */
		
		panel.add(revenueContent);
		return panel;
	}

	/**
	 * Builds a graph with rental revenue over time.
	 * Used by buildRevenuePanel.
	 * @param data - Revenue data
	 * @return A bar graph graph displaying the entered data
	 */
	private JPanel buildRevenueGraph(ArrayList<Object[]> data) {
		// Container
		JPanel revenueGraph = new JPanel();
		revenueGraph.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel graph = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				/*
				 * Setup
				 */
				
				ArrayList<Object[]> points = data;
				int periods = 13; // Number of columns
				
				// Graph
				int graphWidth = this.getWidth() - 1;
				int graphHeight = this.getHeight() - 1;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

				//Background
				Rectangle2D background = new Rectangle2D.Float(0, 0, this.getWidth(), this.getHeight());
				g2d.setColor(paneColor);
				g2d.fill(background);
				
				/*
				 * Plane
				 */
				int planeWidth = graphWidth - 30;
				int planeHeight = graphHeight - 45;
				
				int planeX = graphWidth - planeWidth;
				int planeY = 5;
				
				int xAxis = planeHeight + planeY;
				int yAxis = planeX;
				
				double scale = 1000;
				
				// Empty Plane
				Rectangle2D plane = new Rectangle2D.Float(planeX, planeY, planeWidth, planeHeight);
				g2d.setColor(paneColor);
				g2d.fill(plane);
				
				/*
				 * Y-Axis
				 */
				
				// Calculate max and min
				double maxPoint = 0;
				double minPoint = 0;
				for (int i = points.size() - periods ; i < points.size(); i++) {
					double point = (double)points.get(i)[1];
							
					if (point > maxPoint) maxPoint = point;
					if (minPoint == 0) minPoint = point;
					if (point < minPoint) minPoint = point;
				}

				// Pad bounds
				double paddedMaxPoint = Math.round(maxPoint) + scale;
				double paddedMinPoint = Math.round(minPoint) - (5 * scale);
				double paddedPointRange = paddedMaxPoint - paddedMinPoint;
				
				// Calculate scaling
				int numberOfUnitsOnAxis = (int) Math.ceil(paddedPointRange / scale);
				double heightOfOneScaleUnit = planeHeight / (numberOfUnitsOnAxis);
				
				//Y Axis labels and lines
				int yCount = xAxis + 5;
				int spacing = 1;
				
				for (int i = 0; i < numberOfUnitsOnAxis + 1; i++) {
					
					double point = paddedMinPoint + (scale * i);
					g2d.setColor(textColorTitle);
					g2d.setFont(new Font("Arial", Font.PLAIN, 13));

					// Multiples of 5 units
					if (Math.round(point/scale) % 5 == 0) {
						// Label
						g2d.drawString(Long.toString(Math.round(point/scale)) + "k", 
								0, yCount);
						g2d.setColor(textThirdColor);
						// Line
						Rectangle2D tensLine = new Rectangle2D.Double(yAxis, yCount - 5, 
								planeWidth, 0.05);
						g2d.draw(tensLine);
						g2d.setColor(textColorTitle);
						
					}
					
					// Multiples of 10 units
					if (Math.round(point/scale) % 10 == 0) {
						// Line
						g2d.setColor(textColor);
						Rectangle2D tensLine = new Rectangle2D.Double(yAxis, yCount - 5, 
								planeWidth, 0.7);
						g2d.draw(tensLine);
						g2d.setColor(textColorTitle);
					}
					yCount -= (heightOfOneScaleUnit * spacing);
				}
				
				/*
				 * X-Axis
				 */
				
				// Axis Setup
				int xAxisLabelY = xAxis + 10;
				int xAxisUnitWidth = planeWidth / periods;
				int xCount = planeX - (xAxisUnitWidth / 2) + 5;
				
				// Unit Labels (periods)
				for (int i = points.size() - periods ; i < points.size(); i++) {
					g2d.setColor(textColorTitle);
					g2d.setFont(new Font("Arial", Font.PLAIN, 11));
					FontMetrics fontMetrics = g2d.getFontMetrics();
					AffineTransform old = g2d.getTransform();
			        g2d.rotate(Math.toRadians(-45), (xCount + xAxisUnitWidth), xAxisLabelY);
					g2d.drawString(
							((LocalDate)points.get(i)[0]).format(
									DateTimeFormatter.ofPattern("MMM")) + " ' " +
							((LocalDate)points.get(i)[0]).format(
									DateTimeFormatter.ofPattern("yy")), 
							(xCount + xAxisUnitWidth) - fontMetrics.stringWidth(
									((LocalDate)points.get(i)[0]).format(
											DateTimeFormatter.ofPattern("MMM")) + " ' " +
									((LocalDate)points.get(i)[0]).format(
											DateTimeFormatter.ofPattern("yy")))
							, xAxisLabelY);
					g2d.setTransform(old);
					
					xCount += xAxisUnitWidth;
				}
				
				/*
				 * Plot
				 */
				
				// Bar variables
				int barGap = xAxisUnitWidth/10;
				int barThickness = xAxisUnitWidth - (barGap * 2);
				int xChord = planeX;
				
				// Bar Label variables
				int labelFontSize = (int) (barThickness / 3.5);
				int labelFontHeight = labelFontSize;
				
				// Loop
				for (int i = points.size() - periods ; i < points.size(); i++) {
					
					// Bar size and position
					int yChord = (int)(xAxis - 
							((heightOfOneScaleUnit) * (((((double)(points.get(i)[1])) 
									- paddedMinPoint)/scale))));
					int barHeight = (int) ((heightOfOneScaleUnit) * (((((double)(points.get(i)[1])) 
							- paddedMinPoint)/scale)));

					// Bar
					g2d.setColor(textColorTitle);
					if (i == points.size() - 1) g2d.setColor(textColor);// Current period
					g2d.fillRect(xChord + barGap, yChord, barThickness, barHeight);
					
					// Bar Label
					if (barThickness >= 30) {
						FontMetrics fontMetrics = g2d.getFontMetrics();
						g2d.setColor(paneColor);
						g2d.setFont(new Font("Arial", Font.BOLD, labelFontSize));
						String barLabel = (new DecimalFormat("0.0")).format((double)points.get(i)[1] / scale) + "k";
						g2d.drawString(barLabel,
								((xChord + barGap) + (barThickness/2) - (fontMetrics.stringWidth(barLabel) / 2)) ,
								yChord + labelFontHeight);
					}
					
					xChord += xAxisUnitWidth;
				}
			}	
		};
		c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		revenueGraph.add(graph, c);
		
		return revenueGraph;
	}
	
	/*
	 * Vacancy Rate Panel
	 */

	/**
	 * Fills a panel with information and a graph.
	 * Displays information regarding vacancy rate.
	 * @param panel - Empty theme panel
	 * @return A panel with a vacancy info and a vacancy graph
	 */
	private JPanel buildVacancyRatePanel(JPanel panel) {
		// Container
		vacancyContent = new JPanel();
		vacancyContent.setLayout(new GridBagLayout());
		vacancyContent.setOpaque(true);
		vacancyContent.setBackground(paneColor);
		GridBagConstraints c = new GridBagConstraints();
        
		/*
		 * Frame
		 */
	
		double horPaddingWeight = 0.01;

		JPanel leftPadding = new JPanel();
		leftPadding.setOpaque(false);
		c.gridx = 0;
        c.gridy = 1;
        c.weightx = horPaddingWeight;
		c.weighty = 0;
		c.gridwidth = 1;
		vacancyContent.add(leftPadding, c);
		
		JPanel rightPadding = new JPanel();
		rightPadding.setOpaque(false);
		c.gridx = 3;
		vacancyContent.add(rightPadding, c);
		
		/*
		 * Content
		 */
		
		// Currently Vacant Units
		int vacantUnitsNumber = displayData.getTotalUnits() - displayData.getOccupied();
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 0.2;
		c.gridwidth = 1;
		vacancyContent.add(buildLabelValueDisplay("Vacant Units", Integer.toString(vacantUnitsNumber)), c);
		
		// Average 12 month vacancy rate
		double total = 0;
		for (int i = displayData.getVacancyRate().size() - 13; i < displayData.getVacancyRate().size() - 1; i++) {
			total += (double)(displayData.getVacancyRate().get(i)[1]);
		}
		double aveVacancyRateNumber = 100 * (total / 12);
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 1;
		vacancyContent.add(buildLabelValueDisplay("Avg. Vacancy Rate (12 mos)", (new DecimalFormat("0.00")).format(aveVacancyRateNumber), "%"), c);
		
		// Vacancy Rate Graph Label
		JLabel label = new JLabel("Vacancy Rate (%)", SwingConstants.LEFT);
		label.setFont(new Font("Arial", Font.PLAIN, 14));
		label.setForeground(textColor);
		label.setBorder(new EmptyBorder(new Insets(0, 4, 0, 4)));
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = 2;
		vacancyContent.add(label, c);
		
		// Graph
		c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 2;
		c.gridheight = 1;
		vacancyContent.add(buildVacancyGraph(displayData.getVacancyRate()), c);
		
		/*
         * Add content container to panel 
         */
		
		panel.add(vacancyContent);
		return panel;
	}
		
	/**
	 * Builds a graph with vacancy rate over time.
	 * Used by buildVacancyPanel.
	 * @param data - Vacancy data
	 * @return A bar graph graph displaying the entered data
	 */
	private JPanel buildVacancyGraph(ArrayList<Object[]> data) {
		// Container
		JPanel vacancyGraph = new JPanel();
		vacancyGraph.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel graph = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				/*
				 * Setup
				 */
				
				ArrayList<Object[]> points = data;
				int periods = 13; // Number of columns
				
				// Graph
				int graphWidth = this.getWidth() - 1;
				int graphHeight = this.getHeight() - 1;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

				//Background
				Rectangle2D background = new Rectangle2D.Float(0, 0, this.getWidth(), this.getHeight());
				g2d.setColor(paneColor);
				g2d.fill(background);
				
				/*
				 * Plane
				 */
				int planeWidth = graphWidth - 30;
				int planeHeight = graphHeight - 45;
				
				int planeX = graphWidth - planeWidth;
				int planeY = 5;
				
				int xAxis = planeHeight + planeY;
				int yAxis = planeX;
				
				double scale = 0.01;
				
				//Plane
				Rectangle2D plane = new Rectangle2D.Float(planeX, planeY, planeWidth, planeHeight);
				g2d.setColor(paneColor);
				g2d.fill(plane);
				
				/*
				 * Y-Axis
				 */
				
				// Calculate max (min = 0)
				double maxPoint = 0;
				for (int i = points.size() - periods ; i < points.size(); i++) {
					double point = (double)points.get(i)[1];
					
					if (point > maxPoint) maxPoint = point;
				}

				// Pad bounds
				double paddedMaxPoint = maxPoint + scale;
				double paddedMinPoint = 0;
				double paddedPointRange = paddedMaxPoint - paddedMinPoint;
				
				// Calculate Scaling
				int numberOfUnitsOnAxis = (int) Math.ceil(paddedPointRange / scale);
				double heightOfOneScaleUnit = planeHeight / (numberOfUnitsOnAxis);
				
				//Y-Axis labels and lines
				int yCount = xAxis + 5;
				int spacing = 1;
				
				for (int i = 0; i < numberOfUnitsOnAxis + 1; i++) {
					
					double point = paddedMinPoint + (scale * i);
					g2d.setColor(textColorTitle);
					g2d.setFont(new Font("Arial", Font.PLAIN, 13));

					// Multiples of 1 unit
					if (Math.round(point/scale) % 1 == 0) {
						g2d.setColor(textThirdColor);
						Rectangle2D tensLine = new Rectangle2D.Double(yAxis, yCount - 5, planeWidth, 0.05);
						g2d.draw(tensLine);
						g2d.setColor(textColorTitle);
					}
					
					// Multiples of 5 units (Prevents crowding)
					if (Math.round(point/scale) % 5 == 0 && Math.round(point/scale) % 10 != 0 
							&& (planeHeight > 5 * (paddedPointRange/scale))) {
						// Label
						g2d.drawString(Integer.toString((int)(point/scale)), 0, yCount);
						g2d.setColor(textColorTitle);
					}
					
					// Multiples of 10 units
					if (Math.round(point/scale) % 10 == 0) {
						g2d.drawString(Integer.toString((int)(point/scale)), 0, yCount);
						g2d.setColor(textColor);
						Rectangle2D tensLine = new Rectangle2D.Double(yAxis, yCount - 5, planeWidth, 0.7);
						g2d.draw(tensLine);
						g2d.setColor(textColorTitle);
					}
					yCount -= (heightOfOneScaleUnit * spacing);
				}
				
				/*
				 * X-Axis
				 */
				
				// Axis Setup
				int xAxisLabelY = xAxis + 10;
				int xAxisUnitWidth = planeWidth / periods;
				int xCount = planeX - (xAxisUnitWidth / 2) + 5;
				
				// Unit Labels (periods)
				for (int i = points.size() - periods ; i < points.size(); i++) {
					g2d.setColor(textColorTitle);
					g2d.setFont(new Font("Arial", Font.PLAIN, 11));
					FontMetrics fontMetrics = g2d.getFontMetrics();
					AffineTransform old = g2d.getTransform();
			        g2d.rotate(Math.toRadians(-45), (xCount + xAxisUnitWidth), xAxisLabelY);
					g2d.drawString(
							((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("MMM")) + " ' " +
							((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("yy")), 
							(xCount + xAxisUnitWidth) - fontMetrics.stringWidth(
									((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("MMM")) + " ' " +
									((LocalDate)points.get(i)[0]).format(DateTimeFormatter.ofPattern("yy")))
							, xAxisLabelY);
					g2d.setTransform(old);
					
					xCount += xAxisUnitWidth;
				}
				
				/*
				 * Plot
				 */
				
				// Position variables
				int[] previous = {0, 0};
				int xChord = planeX + (xAxisUnitWidth / 2);
				
				// Label variables
				int labelFontSize = (int) (xAxisUnitWidth / 5);
				int labelFontHeight = labelFontSize;
				g2d.setFont(new Font("Arial", Font.PLAIN, labelFontSize));

				// Loop
				for (int i = points.size() - periods ; i < points.size(); i++) {
					
					// Plot position
					int yChord = (int)(xAxis - 
							((heightOfOneScaleUnit) * (((((double)(points.get(i)[1])) - paddedMinPoint)/scale))));
		
					// Point (circle)
					g2d.setColor(textColorTitle);
					g2d.fillOval(xChord - 3, yChord - 3, 6, 6);
		
					// Label (prevents crowding)
					if (xAxisUnitWidth >= 30) {
						// Label
						FontMetrics fontMetrics = g2d.getFontMetrics(); // For centering
						String barLabel = (new DecimalFormat("0.0")).format((double)points.get(i)[1] / scale) + "%";
						g2d.setColor(textColor);
						g2d.drawString(barLabel,
								xChord - (fontMetrics.stringWidth(barLabel) / 2) ,
								yChord - labelFontHeight);
					}
					
					//Line (not drawn for first point)
					g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
					g2d.setColor(textColorTitle);
					if (i != points.size() - periods) {
						g2d.drawLine(previous[0], previous[1], xChord, yChord);
					}
					
					previous[0] = xChord;
					previous[1] = yChord;
					xChord += xAxisUnitWidth;
				}
			}	
		};
		c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		vacancyGraph.add(graph, c);
	
		return vacancyGraph;
	}
	
	/*
	 * Refresh Methods
	 */
	
	@Override
	public void refreshAll() {
		refreshView(viewPanel, viewContent);
		refreshRent(rentInfoPanel, rentInfoContent);
		refreshRevenue(revenuePanel, revenueContent);
		refreshVecancyRate(vacancyRatePanel, vacancyContent);
	}
	
	/**
	 * Removes the View panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param view - Content
	 */
	private void refreshView(JPanel panel, Component view) {
		panel.remove(view);
		buildViewPanel(panel);
		panel.revalidate();
	}
	
	/**
	 * Removes the Rents panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param view - Content
	 */
	private void refreshRent(JPanel panel, Component view) {
		panel.remove(view);
		buildRentPanel(panel);
		panel.revalidate();
	}
	
	/**
	 * Removes the Revenue panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param view - Content
	 */
	private void refreshRevenue(JPanel panel, Component view) {
		panel.remove(view);
		buildRevenuePanel(panel);
		panel.revalidate();
	}
	
	/**
	 * Removes the Vacancy Rate panel content from its container and rebuilds it.
	 * @param panel - Container
	 * @param view - Content
	 */
	private void refreshVecancyRate(JPanel panel, Component view) {
		panel.remove(view);
		buildVacancyRatePanel(panel);
		panel.revalidate();
	}
}

