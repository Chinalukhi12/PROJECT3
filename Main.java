

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JPanel implements MouseListener {

    public static ArrayList<Country> countries = new ArrayList<>();
    public static VirusInformation v;
    public static HashMap<Integer, List<Mutation>> upgrades;
    public static BufferedImage mapImage;
    public boolean doneInit;
    public static int upgradePts = 200;
    public static GraphPanel graph;

    public static String username = "Enter Your Name ~~ { Guest Name }";

    public static int maxInfectedOverTime = 0;
    public static int totalPop = 0;
    public static int totalInfected = 0;
    public static int totalDead = 0;
    public static int graphFillDelay = 0;
    public static long speed = 1;

    public Main() {
        this.setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
        mapImage = new BufferedImage(MainFrame.WIDTH, MainFrame.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        upgrades = new HashMap<>();
        initMap();
        initCountries();
        totalPop = countries.stream().mapToInt(c -> c.population).sum();
        v = new VirusInformation();
        setPreferredSize(new Dimension(MainFrame.WIDTH, MainFrame.HEIGHT));
        setLayout(new BorderLayout());
        addMouseListener(this);
        graph = new GraphPanel();
        add(new GUI(), BorderLayout.SOUTH);
        add(graph, BorderLayout.EAST);

        Thread paintThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                if (isVisible()){
                    try {
                        if (speed!=0) {
                            Thread.sleep(42 / speed);
                        }else{
                            while (speed==0) {
                                Thread.sleep(500);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.repaint();
                    totalDead = countries.stream().mapToInt(c -> c.dead).sum();
                    totalInfected = countries.stream().mapToInt(c -> c.infected).sum();
                    GUI.infectedMenu.setValue(totalInfected-totalDead);
                    GUI.infectedMenu.setString("Total infected: "
                            +String.format("%.2f", GUI.infectedMenu.getPercentComplete()*100)+"%");

                    GUI.deadMenu.setValue(Main.totalDead);
                    GUI.deadMenu.setString("Total dead: "
                            +String.format("%.2f", GUI.deadMenu.getPercentComplete()*100)+"%");

                    graphFillDelay++;

                    if (graphFillDelay>=50) {
                        Main.graph.infectedGraphPts.add((int) (GUI.infectedMenu.getPercentComplete() * 10000));
                        Main.graph.deadGraphPts.add((int) (GUI.deadMenu.getPercentComplete() * 10000));
                        graphFillDelay = 0;
                    }
                    if (totalInfected>=maxInfectedOverTime){
                        maxInfectedOverTime = totalInfected;
                    }
                    GUI.upgradePoints.setText("$" + upgradePts);


                    if (MainFrame.gameState==1) {
                        if (totalInfected == 0) {
                            try {
                               MainMenu.writeScoresToFile();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        v.lifeTime++;
                        if (v.lifeTime >= v.Delay) {
                            for (int i : v.mutations.keySet()) {
                                for (Mutation m : v.mutations.get(i)) {
                                    m.updateProperties();
                                }
                            }

                            Object[] s = v.mutations.values().stream().flatMap(Collection::stream)
                                    .filter((Mutation m) -> m.isActive && !m.applied).toArray();

                            if (s.length > 0) {
                                Mutation tmp = (Mutation) s[(int) (Math.random() * s.length)];
                                tmp.apply();
                            }
                            v.lifeTime = 0;
                        }
                    }
                }
            }
        });
        paintThread.start();
        doneInit = true;
    }

    public void initMap(){
        try{
            Image tmp = ImageIO.read(new File("src/world_map.png"));
            BufferedImage bg = new BufferedImage(MainFrame.WIDTH, MainFrame.HEIGHT, BufferedImage.TYPE_INT_RGB);

            Graphics tempG = bg.createGraphics();
            tempG.drawImage(tmp, 0, 0, null);
            tempG.dispose();

            for (int i = 0; i < mapImage.getHeight(); i++) {
                for (int j = 0; j < mapImage.getWidth(); j++) {
                    if (bg.getRGB(j,i)==new Color(24,0,255).getRGB()) {
                        mapImage.setRGB(j,i, new Color(28, 71,99).getRGB());
                    }
                    else if (Math.abs(bg.getRGB(j,i)-Color.white.getRGB())<Math.abs(bg.getRGB(j,i)-Color.black.getRGB())) {
                        mapImage.setRGB(j, i, Color.lightGray.getRGB());
                    }else{
                        mapImage.setRGB(j,i,Color.black.getRGB());
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("error");
        }
    }
    public void initCountries(){
        countries.add(new Country(934, 157, "Russia", 1500000, 3,
                new Airport(914, 167),
                new Airport(1224,188),
                new Port(916,115)));

        countries.add(new Country(905, 205, "Ukraine", 400000, 3,
                new Airport(896, 205)));

        countries.add(new Country(853, 194, "Poland", 380000, 3));

        countries.add(new Country(886, 188, "Belarus", 95000, 3));


        countries.add(new Country(791, 221, "France", 670000, 4,
                new Airport(783, 214)));

        countries.add(new Country(819, 200, "Germany", 830000, 4));

        countries.add(new Country(765, 254, "Spain", 470000, 4,
                new Airport(765, 251)));

        countries.add(new Country(825, 235, "Italy", 600000, 4));

        countries.add(new Country(new ArrayList<int[]>(){{
            add(new int[]{397, 249});
            add(new int[]{270, 133});
        }}, "USA", 3280000, 4,
                new Airport(450, 250),
                new Port(432,310)));

        countries.add(new Country(384, 165, "Canada", 380000, 4,
                new Airport(400, 160)));
        countries.add(new Country(316, 319, "Mexico", 1260000, 3,
                new Port(318,368)));
        countries.add(new Country(746, 260, "Portugal", 102000, 3));
        countries.add(new Country(813, 220, "Switzerland", 85000, 4));
        countries.add(new Country(841, 205, "Czech Republic", 106500, 3));
        countries.add(new Country(836, 217, "Austria", 106500, 4));
        countries.add(new Country(854, 211, "Slovakia", 54500, 2));
        countries.add(new Country(857, 220, "Hungary", 97700, 2));
        countries.add(new Country(880, 226, "Romania", 194100, 2));
        countries.add(new Country(894, 219, "Moldova", 35460, 2));
        countries.add(new Country(883, 239, "Bulgaria", 70000, 2));

        countries.add(new Country(new ArrayList<int[]>(){{
            add(new int[]{773, 192});
            add(new int[]{755, 183});
        }}, "UK", 666000, 4,
                new Airport(776,195),
                new Port(772,165)));

        countries.add(new Country(871, 131, "Finland", 55000, 4));
        countries.add(new Country(838, 138, "Sweden", 102300, 4));
        countries.add(new Country(812, 146, "Norway", 54000, 4));
        countries.add(new Country(749, 188, "Ireland", 49000, 3));
        countries.add(new Country(814, 173, "Denmark", 58000, 4));
        countries.add(new Country(802, 193, "Netherlands", 172800, 4));
        countries.add(new Country(798, 202, "Belgium", 114600, 4));
        countries.add(new Country(551, 512, "Brazil", 2090000, 3,
                new Airport(502,490),
                new Port(615,480)));
        countries.add(new Country(445, 516, "Peru", 320000, 2));
        countries.add(new Country(477, 587, "Chile", 187300, 2));
        countries.add(new Country(450, 442, "Colombia", 496500, 1));
        countries.add(new Country(496, 412, "Venezuela", 289000, 1));
        countries.add(new Country(1129, 257, "China", 13930000, 4,
                new Airport(1258,303),
                new Port(1294,343)));
        countries.add(new Country(1398, 597, "Australia", 250000, 4,
                new Airport(1398,597),
                new Port(1334,624)));

        countries.add(new Country(1108, 319, "India", 13530000, 3,
                new Airport(1124,365),
                new Port(1124,418)));
        countries.add(new Country(913,316, "Africa",12160000,0,
                new Airport(882,433),
                new Airport(886,617),
                new Port(704,359),
                new Port(907,628)));


        for (Country c:countries.stream().filter((c)->c.transportNodes.size()>0).collect(Collectors.toList())) {
            for (Port p : c.transportNodes.stream().filter((t) -> t instanceof Port).map((o) -> (Port) o).collect(Collectors.toCollection(ArrayList::new))) {
                for (Country otherCountry:countries.stream().filter((otherCountry)->otherCountry.transportNodes.size()>0).collect(Collectors.toCollection(ArrayList::new))){
                        for (Port otherPort : otherCountry.transportNodes.stream().filter((otherPort) -> otherPort instanceof Port).map((o) -> (Port) o).collect(Collectors.toCollection(ArrayList::new))) {



                            if (!p.equals(otherPort)) {
                                    File pathTxt = new File("src/paths/" + c.name + "_" + c.transportNodes.indexOf(p) +
                                                "_" + otherCountry.name + "_" + otherCountry.transportNodes.indexOf(otherPort) + ".txt");


                                    if (pathTxt.exists()){

                                        try {
                                            FileReader fr = new FileReader(pathTxt);
                                            BufferedReader br = new BufferedReader(fr);
                                            ArrayList<int[]> path = new ArrayList<>();
                                            for (String s: br.lines().collect(Collectors.toCollection(ArrayList::new))){
                                                int[] coords = new int[]{Integer.parseInt(s.split("-")[0]),
                                                                         Integer.parseInt(s.split("-")[1])};
                                                path.add(coords);
                                                p.paths.put(otherPort,path);
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }


                                    }else{
                                        p.region = p.expandRegionForVoyagePath(p.x, p.y, otherPort.x, otherPort.y, new Color(28, 71, 99));
                                        ArrayList<int[]> path = p.findPath(otherPort.x, otherPort.y);
                                        Collections.reverse(path);
                                        p.paths.put(otherPort, path);
                                        try {
                                            pathTxt.createNewFile();
                                            FileWriter fw = new FileWriter(pathTxt);
                                            BufferedWriter bw = new BufferedWriter(fw);
                                            path.forEach((arr) -> {
                                                try {
                                                    bw.append(arr[0] + "-" + arr[1]);
                                                    bw.newLine();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                            bw.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }

    public static ArrayList<int[]> expandRegion(int x, int y, Color bg){
        BufferedImage copy = mapImage.getSubimage(0,0, MainFrame.WIDTH, MainFrame.HEIGHT);
        ArrayList<int[]> result = new ArrayList<>();
        int[] first = new int[]{x,y};
        if (copy.getRGB(x,y)==bg.getRGB()) {
                copy.setRGB(x,y,Color.green.getRGB());
            }

            ArrayList<int[]> queue = new ArrayList<>();
            queue.add(first);
            result.add(first);
            while(!queue.isEmpty()) {
                int[] current = queue.get(0);
                result.add(current);
                queue.remove(0);
                int j = current[0];
                int i = current[1];
                for (int[] arr : new int[][]{
                        {j, i - 1},
                        {j, i + 1},
                        {j - 1, i},
                        {j + 1, i}
                }) {
                    if (arr[1] >= 0 && arr[1] < MainFrame.HEIGHT && arr[0] >= 0 && arr[0] < MainFrame.WIDTH && copy.getRGB(arr[0],arr[1]) == bg.getRGB()) {
                        copy.setRGB(arr[0],arr[1], Color.blue.getRGB());
                        queue.add(new int[]{arr[0], arr[1]});
                    }
                }
            }
            return result;
    }

    public static double dist(double a, double b, double c, double d){
        return Math.sqrt(Math.pow(c-a,2)+Math.pow(d-b,2));
    }
    @Override
    public void paintComponent(Graphics g){
        if (isVisible()){
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(mapImage, 0, 0, null);
            if (doneInit && MainFrame.gameState == 1) {
                for (Country c : countries) {
                    c.update();
                    c.show(g2d);
                }
            }


            if (GUI.areSeaWaysEnabled || GUI.areAirWaysEnabled) {
                g2d.setColor(Color.pink);
                g2d.setStroke(new BasicStroke(1));
                for (Country c : countries.stream().filter((c) -> c.transportNodes.size() > 0).collect(Collectors.toList())) {
                    if (GUI.areSeaWaysEnabled) {
                        for (Port p : c.transportNodes.stream().filter((t) -> t instanceof Port).map((o) -> (Port) o).collect(Collectors.toCollection(ArrayList::new))) {
                            for (Port other : p.paths.keySet()) {
                                for (int[] arr : p.paths.get(other)) {
                                    g2d.fillRect(arr[0], arr[1], 1, 1);
                                }
                            }
                        }
                    }

                    if (GUI.areAirWaysEnabled) {
                        for (Airport airport : c.transportNodes.stream().filter((t) -> t instanceof Airport).map((o) -> (Airport) o).collect(Collectors.toCollection(ArrayList::new))) {
                            for (Country otherC : countries.stream().filter((otherC) -> otherC.transportNodes.size() > 0).collect(Collectors.toList())) {
                                for (Airport otherAir : otherC.transportNodes.stream().filter((t) -> t instanceof Airport).map((o) -> (Airport) o).collect(Collectors.toCollection(ArrayList::new))) {
                                    g2d.drawLine(airport.x, airport.y, otherAir.x, otherAir.y);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println(e.getX()+" "+e.getY());
        //System.out.println();
        if (!Main.v.isAutoPickupEnabled) {
            Country:
            for (Country c : countries) {
                for (Country.BonusPopup p : c.bonuses) {
                    if (p.contains(e.getX(), e.getY())) {
                        p.activate();
                        c.bonuses.remove(p);
                        break Country;
                    }
                }
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
