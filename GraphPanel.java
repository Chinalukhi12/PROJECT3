import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphPanel extends JPanel {
    public ArrayList<Integer> infectedGraphPts = new ArrayList();
    public ArrayList<Integer> deadGraphPts = new ArrayList();

    public double scaleX = 1;
    public double scaleY = 1;

    public GraphPanel(){
        setVisible(false);
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(MainFrame.WIDTH/2,MainFrame.HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (infectedGraphPts.size()>=getWidth()/4){
            infectedGraphPts.remove(0);
            deadGraphPts.remove(0);
        }
        if (isVisible()) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.red);
            g2d.setStroke(new BasicStroke(1));

            for (int i1 = infectedGraphPts.size() - 1; i1 >= 1; i1--) {
                int valueDead = deadGraphPts.get(i1);
                int valueInfected = infectedGraphPts.get(i1)-valueDead;

                double infected = (valueInfected)/10000d;
                double dead = (valueDead)/10000d;
                double healthy = (1-infected-dead);
                double[] proportins = new double[]{
                    healthy,infected,dead
                };
                Color[] colors = new Color[]{
                        new Color(0,200,0),
                        new Color(255,10,0),
                        Color.darkGray
                };
                int prevY = 0;
                for (int i = 0; i < proportins.length; i++) {
                    g2d.setColor(colors[i]);
                    g2d.fillRect(i1*4, prevY,4, (int) (proportins[i]*getHeight()));
                    prevY += (int) (proportins[i]*getHeight());
                }
            }
        }
    }
}
