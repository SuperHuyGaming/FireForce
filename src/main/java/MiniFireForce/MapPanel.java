package MiniFireForce;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MapPanel extends JPanel {
    private GenSituationClass situation;
    
    // Unicode icons for visualization.
    private final String fireIcon = "🔥";
    private final String truckIcon = "🚒";
    private final String stationIcon = "🏢";
    
    public MapPanel(GenSituationClass situation) {
        this.situation = situation;
        setPreferredSize(new Dimension(500, 500));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        
        // Draw a new gradient background (dark navy to mid-blue)
        Color topColor = new Color(10, 10, 50);
        Color bottomColor = new Color(30, 30, 80);
        GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);
        
        // Map simulation coordinates (assumed range -1000 to 1000) to panel coordinates.
        int offset = 10;
        int drawWidth = width - 2 * offset;
        int drawHeight = height - 2 * offset;
        double scaleX = (double) drawWidth / 2000.0;
        double scaleY = (double) drawHeight / 2000.0;
        int centerX = offset + drawWidth / 2;
        int centerY = offset + drawHeight / 2;
        
        // Draw Fire Stations with a light blue icon.
        g2d.setColor(new Color(135, 206, 250)); // light sky blue
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        for (Map.Entry<Integer, FireStation> entry : situation.getFireStations().entrySet()) {
            FireStation st = entry.getValue();
            int x = centerX + (int)(st.getX() * scaleX);
            int y = centerY - (int)(st.getY() * scaleY);
            g2d.drawString(stationIcon, x - 10, y + 10);
        }
        
        // Draw Fires with an orange color.
        g2d.setColor(Color.ORANGE);
        for (Map.Entry<Integer, Fire> entry : situation.getActiveFires().entrySet()) {
            Fire f = entry.getValue();
            int x = centerX + (int)(f.getX() * scaleX);
            int y = centerY - (int)(f.getY() * scaleY);
            g2d.drawString(fireIcon, x - 10, y + 10);
        }
        
        // Draw Moving Trucks with green.
        g2d.setColor(Color.GREEN.darker());
        g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
        // Assume situation.getMovingTrucks() returns a List<MovingTruck>
        for (MovingTruck mt : situation.getMovingTrucks()) {
            int truckX = centerX + (int)(mt.getCurrentX() * scaleX);
            int truckY = centerY - (int)(mt.getCurrentY() * scaleY);
            g2d.drawString(truckIcon, truckX - 10, truckY + 10);
            if (mt.getState() == MovingTruck.State.EXTINGUISHING) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString("Extinguishing", truckX + 15, truckY);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
                g2d.setColor(Color.GREEN.darker());
            }
        }
    }
}
