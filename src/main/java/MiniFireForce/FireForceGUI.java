package MiniFireForce;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FireForceGUI extends JFrame {
    private GenSituationClass situation;
    private JTable stationTable;
    private JTable fireTable;
    private DefaultTableModel stationModel;
    private DefaultTableModel fireModel;
    private JTextArea logArea;
    private JLabel clockLabel;
    private JButton pauseButton;
    private Timer refreshTimer;
    private Timer clockTimer;
    private JTabbedPane tabbedPane;
    private MapPanel mapPanel;
    
    public FireForceGUI() {
        // Create simulation and set the event logger so events appear in the log.
        situation = new GenSituationClass();
        situation.setEventLogger(message ->
            SwingUtilities.invokeLater(() -> appendLog(message))
        );
        
        initComponents();
        startClock();
        startRefresh();
        
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("Fire Force Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());
        
        // Top Panel: Pause/Resume button and live clock.
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener((ActionEvent e) -> togglePause());
        topPanel.add(pauseButton);
        
        clockLabel = new JLabel("Time: ");
        clockLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(clockLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Tabbed Pane with two tabs: Dashboard and Map.
        tabbedPane = new JTabbedPane();
        
        // Dashboard Tab: Contains tables and event log.
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        
        JPanel tablePanel = new JPanel(new GridLayout(1,2));
        stationModel = new DefaultTableModel(new Object[]{"ID","X","Y","Trucks"}, 0);
        stationTable = new JTable(stationModel);
        JScrollPane stationScroll = new JScrollPane(stationTable);
        stationScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Fire Stations", TitledBorder.CENTER, TitledBorder.TOP));
        tablePanel.add(stationScroll);
        
        fireModel = new DefaultTableModel(new Object[]{"ID","X","Y","Severity","Time"}, 0);
        fireTable = new JTable(fireModel);
        JScrollPane fireScroll = new JScrollPane(fireTable);
        fireScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Active Fires", TitledBorder.CENTER, TitledBorder.TOP));
        tablePanel.add(fireScroll);
        
        dashboardPanel.add(tablePanel, BorderLayout.CENTER);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Event Log", TitledBorder.CENTER, TitledBorder.TOP));
        logScroll.setPreferredSize(new Dimension(1000,150));
        dashboardPanel.add(logScroll, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Dashboard", dashboardPanel);
        
        // Map Tab: Contains a MapPanel.
        mapPanel = new MapPanel(situation);
        JPanel mapTab = new JPanel(new BorderLayout());
        mapTab.add(mapPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Map", mapTab);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void togglePause() {
        boolean paused = situation.isPaused();
        situation.setPaused(!paused);
        pauseButton.setText(paused ? "Pause" : "Resume");
    }
    
    private void appendLog(String message) {
        logArea.append(message + "\n");
    }
    
    private void startClock() {
        clockTimer = new Timer(1000, (ActionEvent e) -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            clockLabel.setText("Time: " + now.format(dtf));
        });
        clockTimer.start();
    }
    
    private void startRefresh() {
        refreshTimer = new Timer(5000, (ActionEvent e) -> refreshTables());
        refreshTimer.start();
    }
    
    private void refreshTables() {
        // Update fire station table.
        stationModel.setRowCount(0);
        for(FireStation station : situation.getFireStations().values()){
            stationModel.addRow(new Object[]{station.getID(), station.getX(), station.getY(), station.getTrucks()});
        }
        // Update fire table.
        fireModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(Fire fire : situation.getActiveFires().values()){
            fireModel.addRow(new Object[]{fire.getID(), fire.getX(), fire.getY(), fire.getSeverity(), fire.getTime().format(dtf)});
        }
        // Refresh map.
        mapPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FireForceGUI());
    }
}
