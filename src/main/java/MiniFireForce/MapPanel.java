package MiniFireForce;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MapPanel extends JPanel {
    private GenSituationClass situation;

    // Use Unicode symbols for overlay icons.
    private final String fireIcon = "🔥";     
    private final String truckIcon = "🚒";    
    private final String stationIcon = "🏢";  

    // The map image (background).
    private Image mapImage;

    public MapPanel(GenSituationClass situation) {
        this.situation = situation;
        setPreferredSize(new Dimension(500, 500));

        // Load the map image from resources.
        // Adjust the path/name if needed.
        mapImage = new ImageIcon(getClass().getResource("losangeles.png")).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1) Draw the map background, scaled to fit the entire panel.
        int width = getWidth();
        int height = getHeight();
        g.drawImage(mapImage, 0, 0, width, height, this);

        // 2) Overlay your icons. 
        //    If you previously used coordinate scaling (e.g., -1000..1000), 
        //    you might want to define a method to map simulation coordinates to panel coords.
        
        // Example: just center everything as if your simulation coords are in [-1000..1000].
        int offset = 10;
        int drawWidth = width - 2 * offset;
        int drawHeight = height - 2 * offset;
        double scaleX = (double) drawWidth / 2000.0;  // if your range is -1000..1000
        double scaleY = (double) drawHeight / 2000.0;
        int centerX = offset + drawWidth / 2;
        int centerY = offset + drawHeight / 2;

        // Draw Fire Stations.
        g.setColor(Color.BLUE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        for (Map.Entry<Integer, FireStation> entry : situation.getFireStations().entrySet()) {
            FireStation station = entry.getValue();
            int drawX = centerX + (int)(station.getX() * scaleX);
            int drawY = centerY - (int)(station.getY() * scaleY);
            g.drawString(stationIcon, drawX - 10, drawY + 10);
        }

        // Draw Fires (in red).
        g.setColor(Color.RED);
        for (Map.Entry<Integer, Fire> entry : situation.getActiveFires().entrySet()) {
            Fire fire = entry.getValue();
            int drawX = centerX + (int)(fire.getX() * scaleX);
            int drawY = centerY - (int)(fire.getY() * scaleY);
            g.drawString(fireIcon, drawX - 10, drawY + 10);
        }

        // Draw Trucks (in green).
        g.setColor(Color.GREEN.darker());
        for (MovingTruck truck : situation.getMovingTrucks()) {
            int truckX = centerX + (int)(truck.getCurrentX() * scaleX);
            int truckY = centerY - (int)(truck.getCurrentY() * scaleY);
            g.drawString(truckIcon, truckX - 10, truckY + 10);
            
            // If the truck is extinguishing, label it.
            if (truck.getState() == MovingTruck.State.EXTINGUISHING) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString("Extinguishing", truckX + 12, truckY);
                g.setFont(new Font("SansSerif", Font.PLAIN, 20));
                g.setColor(Color.GREEN.darker());
            }
        }
    }
}
