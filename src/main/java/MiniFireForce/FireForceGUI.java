package MiniFireForce;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FireForceGUI provides a graphical user interface for the FireForce simulation.
 * The GUI includes a dashboard displaying active fires and fire stations, a map
 * panel for visualization, and an event log for tracking simulation updates.
 */
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
    private Timer animationTimer;
    private JTabbedPane tabbedPane;
    private MapPanel mapPanel;

    /**
     * Constructs the FireForce GUI, initializes components, and starts timers.
     */
    public FireForceGUI() {
        situation = new GenSituationClass();
        situation.setEventLogger(msg -> SwingUtilities.invokeLater(() -> appendLog(msg)));

        initComponents();
        startClock();
        startRefresh();
        startAnimation();

        setVisible(true);
    }

    /**
     * Initializes the graphical components of the GUI.
     */
    private void initComponents() {
        setTitle("Fire Force Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // Top Panel: Pause/Resume + Clock
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener((ActionEvent e) -> togglePause());
        topPanel.add(pauseButton);

        clockLabel = new JLabel("Time: ");
        clockLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(clockLabel);

        add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();

        // Dashboard tab
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        JPanel tablePanel = new JPanel(new GridLayout(1, 2));

        // Fire station table
        stationModel = new DefaultTableModel(new Object[]{"ID", "X", "Y", "Trucks"}, 0);
        stationTable = new JTable(stationModel);
        JScrollPane stationScroll = new JScrollPane(stationTable);
        stationScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Fire Stations", TitledBorder.CENTER, TitledBorder.TOP));
        tablePanel.add(stationScroll);

        // Active fires table
        fireModel = new DefaultTableModel(new Object[]{"ID", "X", "Y", "Severity", "Time"}, 0);
        fireTable = new JTable(fireModel);
        JScrollPane fireScroll = new JScrollPane(fireTable);
        fireScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Active Fires", TitledBorder.CENTER, TitledBorder.TOP));
        tablePanel.add(fireScroll);

        dashboardPanel.add(tablePanel, BorderLayout.CENTER);

        // Event log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Event Log", TitledBorder.CENTER, TitledBorder.TOP));
        logScroll.setPreferredSize(new Dimension(1000, 150));
        dashboardPanel.add(logScroll, BorderLayout.SOUTH);

        tabbedPane.addTab("Dashboard", dashboardPanel);

        // Map tab
        mapPanel = new MapPanel(situation);
        JPanel mapTab = new JPanel(new BorderLayout());
        mapTab.add(mapPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Map", mapTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Toggles the pause state of the simulation.
     */
    private void togglePause() {
        boolean p = situation.isPaused();
        situation.setPaused(!p);
        pauseButton.setText(p ? "Pause" : "Resume");
    }

    /**
     * Appends a message to the event log.
     *
     * @param msg the message to log
     */
    private void appendLog(String msg) {
        logArea.append(msg + "\n");
    }

    /**
     * Starts the clock timer that updates every second.
     */
    private void startClock() {
        clockTimer = new Timer(1000, (ActionEvent e) -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            clockLabel.setText("Time: " + now.format(dtf));
        });
        clockTimer.start();
    }

    /**
     * Starts the refresh timer for updating the fire station and fire tables.
     */
    private void startRefresh() {
        refreshTimer = new Timer(2000, (ActionEvent e) -> refreshTables());
        refreshTimer.start();
    }

    /**
     * Starts the animation timer for updating the map visualization.
     */
    private void startAnimation() {
        animationTimer = new Timer(50, e -> mapPanel.repaint());
        animationTimer.start();
    }

    /**
     * Refreshes the tables displaying fire stations and active fires.
     */
    private void refreshTables() {
        // Refresh station table
        stationModel.setRowCount(0);
        for (FireStation st : situation.getFireStations().values()) {
            stationModel.addRow(new Object[]{
                    st.getID(), st.getX(), st.getY(), st.getTrucks()
            });
        }

        // Refresh fire table
        fireModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Fire f : situation.getActiveFires().values()) {
            fireModel.addRow(new Object[]{
                    f.getID(), f.getX(), f.getY(), f.getSeverity(), f.getTime().format(dtf)
            });
        }
    }

    /**
     * Main method that starts the GUI application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FireForceGUI::new);
    }
}
