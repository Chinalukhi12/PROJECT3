import javax.swing.*;
import java.awt.*;

public class MainFrame {

    public static final int WIDTH = 1440;
    public static final int HEIGHT = 800;
    public static Main m;
    public static MainMenu menu;
    public static int gameState = 0;
    public static void main(String[] args) {

        SwingUtilities.invokeLater(()->{
            JFrame f = new JFrame();
            f.setSize(WIDTH, HEIGHT+20);
            f.setResizable(false);
            f.setTitle(" AntiPlague Coronavirus Game ");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            f.setResizable(true);
            m = new Main();
            menu = new MainMenu();

            f.add(menu);
            f.add(m);

            f.setVisible(true);
            menu.setVisible(true);
            m.setVisible(false);

        });
    }
}
