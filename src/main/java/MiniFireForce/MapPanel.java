package MiniFireForce;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * The {@code MapPanel} class is responsible for rendering the fire simulation.
 * It visually represents fire stations, active fires, and moving fire trucks.
 * The panel dynamically updates based on the state of the simulation.
 */
public class MapPanel extends JPanel {
    private GenSituationClass situation;

    // Unicode icons for map representation
    private final String fireIcon = "🔥";
    private final String truckIcon = "🚒";
    private final String stationIcon = "🏢";

    /**
     * Constructs a new {@code MapPanel} with a reference to the simulation.
     *
     * @param situation the simulation instance containing fire stations, fires, and trucks
     */
    public MapPanel(GenSituationClass situation) {
        this.situation = situation;
        setPreferredSize(new Dimension(500, 500));
    }

    /**
     * Paints the graphical elements representing the fire stations, fires, and fire trucks.
     * This method maps the simulation coordinates to the panel and updates the visuals.
     *
     * @param g the {@code Graphics} object used for rendering
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        // Background gradient
        Color topColor = new Color(10, 10, 50);
        Color bottomColor = new Color(30, 30, 80);
        GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);

        // Coordinate mapping from simulation space [-1000, 1000] to panel space
        int offset = 10;
        int drawWidth = width - 2 * offset;
        int drawHeight = height - 2 * offset;
        double scaleX = (double) drawWidth / 2000.0;
        double scaleY = (double) drawHeight / 2000.0;
        int centerX = offset + drawWidth / 2;
        int centerY = offset + drawHeight / 2;

        // Draw fire stations
        g.setColor(new Color(135, 206, 250)); // Light blue for fire stations
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        for (Map.Entry<Integer, FireStation> e : situation.getFireStations().entrySet()) {
            FireStation st = e.getValue();
            int x = centerX + (int) (st.getX() * scaleX);
            int y = centerY - (int) (st.getY() * scaleY);
            g.drawString(stationIcon, x - 10, y + 10);
        }

        // Draw active fires
        g.setColor(Color.ORANGE);
        for (Map.Entry<Integer, Fire> e : situation.getActiveFires().entrySet()) {
            Fire f = e.getValue();
            int x = centerX + (int) (f.getX() * scaleX);
            int y = centerY - (int) (f.getY() * scaleY);
            g.drawString(fireIcon, x - 10, y + 10);
        }

        // Draw moving fire trucks
        g.setColor(Color.GREEN.darker());
        for (MovingTruck mt : situation.getMovingTrucks()) {
            int tx = centerX + (int) (mt.getCurrentX() * scaleX);
            int ty = centerY - (int) (mt.getCurrentY() * scaleY);
            g.drawString(truckIcon, tx - 10, ty + 10);

            // Label extinguishing trucks
            if (mt.getState() == MovingTruck.State.EXTINGUISHING) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString("Extinguishing", tx + 15, ty);
                g.setColor(Color.GREEN.darker());
                g.setFont(new Font("SansSerif", Font.BOLD, 22));
            }
        }
    }
}
