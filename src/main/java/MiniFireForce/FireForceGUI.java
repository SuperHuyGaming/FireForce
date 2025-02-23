package MiniFireForce;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class FireForceGUI extends JFrame {
    private GenSituationClass situation;
    private JTable fireStationTable;
    private JTable fireTable;
    private DefaultTableModel fireStationModel;
    private DefaultTableModel fireModel;
    private JTextArea messageArea;
    private Timer refreshTimer;
    private Timer animationTimer;
    private JLabel clockLabel;
    private JButton pauseButton;
    private MapPanel mapPanel;
    private JTable mapDataTable;
    private DefaultTableModel mapDataModel;
    
    public FireForceGUI() {
        situation = new GenSituationClass();
        situation.setEventLogger(message ->
            SwingUtilities.invokeLater(() -> logMessage(message))
        );
        setupGUI();
        startClock();
        startAutoRefresh();
        startAnimationTimer();
    }
    
    private void setupGUI() {
        setTitle("Mini Fire Force Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());
        
        // Top Bar: Pause Button and Clock.
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> togglePause());
        topBar.add(pauseButton);
        clockLabel = new JLabel("Time: ", SwingConstants.CENTER);
        clockLabel.setFont(clockLabel.getFont().deriveFont(Font.BOLD, 16));
        topBar.add(clockLabel);
        add(topBar, BorderLayout.NORTH);
        
        // Tabbed Pane with Dashboard and Map.
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Dashboard Tab (existing tables and log)
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2));
        fireStationModel = new DefaultTableModel(new Object[]{"ID", "X", "Y", "Trucks"}, 0);
        fireStationTable = new JTable(fireStationModel);
        JScrollPane stationScrollPane = new JScrollPane(fireStationTable);
        stationScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Fire Station List", TitledBorder.CENTER, TitledBorder.TOP));
        tablesPanel.add(stationScrollPane);
        fireModel = new DefaultTableModel(new Object[]{"ID", "X", "Y", "Severity", "Time"}, 0);
        fireTable = new JTable(fireModel);
        JScrollPane fireScrollPane = new JScrollPane(fireTable);
        fireScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Active Fires List", TitledBorder.CENTER, TitledBorder.TOP));
        tablesPanel.add(fireScrollPane);
        dashboardPanel.add(tablesPanel, BorderLayout.CENTER);
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Status Log", TitledBorder.CENTER, TitledBorder.TOP));
        messageScrollPane.setPreferredSize(new Dimension(1000, 150));
        dashboardPanel.add(messageScrollPane, BorderLayout.SOUTH);
        tabbedPane.addTab("Dashboard", dashboardPanel);
        
        // Map Tab: MapPanel plus a scrollable data table at the bottom.
        JPanel mapTab = new JPanel(new BorderLayout());
        mapPanel = new MapPanel(situation);
        mapTab.add(mapPanel, BorderLayout.CENTER);
        // Create a small table that shows summary data.
        mapDataModel = new DefaultTableModel(new Object[]{"ID", "Type", "X", "Y"}, 0);
        mapDataTable = new JTable(mapDataModel);
        JScrollPane mapTableScroll = new JScrollPane(mapDataTable);
        mapTableScroll.setPreferredSize(new Dimension(1000, 120));
        mapTab.add(mapTableScroll, BorderLayout.SOUTH);
        tabbedPane.addTab("Map", mapTab);
        
        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }
    
    private void startClock() {
        Timer clockTimer = new Timer(1000, (ActionEvent e) -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            clockLabel.setText("Time: " + now.format(formatter));
        });
        clockTimer.start();
    }
    
    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, (ActionEvent e) -> refreshData());
        refreshTimer.start();
    }
    
    // Animation timer for smooth map updates.
    private void startAnimationTimer() {
        animationTimer = new Timer(100, e -> mapPanel.repaint());
        animationTimer.start();
    }
    
    private void refreshData() {
        // Refresh Dashboard tables.
        fireStationModel.setRowCount(0);
        for (FireStation station : situation.getFireStations().values()) {
            fireStationModel.addRow(new Object[]{
                station.getID(), station.getX(), station.getY(), station.getTrucks()
            });
        }
        fireModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Fire fire : situation.getActiveFires().values()) {
            fireModel.addRow(new Object[]{
                fire.getID(), fire.getX(), fire.getY(), fire.getSeverity(), fire.getTime().format(formatter)
            });
        }
        // Refresh Map Data Table (combined summary).
        mapDataModel.setRowCount(0);
        for (FireStation station : situation.getFireStations().values()) {
            mapDataModel.addRow(new Object[]{
                station.getID(), "Station", station.getX(), station.getY()
            });
        }
        for (Fire fire : situation.getActiveFires().values()) {
            mapDataModel.addRow(new Object[]{
                fire.getID(), "Fire", fire.getX(), fire.getY()
            });
        }
    }
    
    private void logMessage(String message) {
        messageArea.append(message + "\n");
    }
    
    private void togglePause() {
        boolean currentlyPaused = situation.isPaused();
        situation.setPaused(!currentlyPaused);
        pauseButton.setText(currentlyPaused ? "Pause" : "Resume");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FireForceGUI::new);
    }
}
