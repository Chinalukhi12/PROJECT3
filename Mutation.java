import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Mutation {

    public boolean isUpgrade = false;
    public static Font f = new Font("Arial", Font.PLAIN, 13);
    public int level;
    public boolean isActive = false;
    public boolean applied = false;
    public String desc = " ";
    public int price;


    public ArrayList<Mutation> ancestors = new ArrayList<>();

    public Mutation(String desc, int level, Mutation... mutations) {
        this.desc = desc;
        this.level = level;
        ancestors.addAll(Arrays.asList(mutations));
        updateProperties();
    }
    public Mutation(String desc, int level, int price, Mutation... mutations){
        this(desc,level,mutations);
        this.price = price;
    }

    public Mutation(String desc, int level, int price, boolean isUpgrade, Mutation... mutations){
        this(desc+" \n Price: $"+price,level,mutations);
        this.price = price;
        this.isUpgrade = isUpgrade;
    }

    public void apply() {
        if (isActive && !applied && Main.upgradePts>=price) {
            applied = true;
            Main.upgradePts-=price;
        }
    }

    public int getWidth() {
        int blockWidth = getBlockSizeX() * 2 / 3;
        return blockWidth;
    }

    public int getBlockSizeX() {
        Main.v.mutations.keySet().size();
        int sizeOfLayerX = 0;

        if (isUpgrade){
            sizeOfLayerX = Main.upgrades.keySet().size();
            return GUI.upgradesPanel.getWidth() / sizeOfLayerX;
        }else{
            sizeOfLayerX = Main.v.mutations.keySet().size();
            return GUI.mutationsPanel.getWidth() / sizeOfLayerX;
        }

    }

    public int getBlockSizeY() {
        int sizeOfLayerY = 0;
        if (isUpgrade){
            sizeOfLayerY = Main.upgrades.get(level).size();
            return GUI.upgradesPanel.getHeight() / sizeOfLayerY;
        }else{
            sizeOfLayerY = Main.v.mutations.get(level).size();
            return GUI.mutationsPanel.getHeight() / sizeOfLayerY;
        }
    }

    public int getHeight() {
        int blockHeight = getBlockSizeY() * 2 / 3;
        return blockHeight;
    }

    public int getX() {
        return level * (getBlockSizeX()) + (getBlockSizeX() - getWidth()) / 2;
    }

    public int getY() {
        if (isUpgrade){
            return Main.upgrades.get(level).indexOf(this) * (getBlockSizeY()) + (getBlockSizeY() - getHeight()) / 2;
        }else {
            return Main.v.mutations.get(level).indexOf(this) * (getBlockSizeY()) + (getBlockSizeY() - getHeight()) / 2;
        }
    }
    public boolean contains(int x, int y){

        return (x>=getX()&&x<=getX()+getWidth()&&y>=getY()&&y<=getY()+getHeight());
    }
    public void updateProperties(){
        if (level != 0) {
            isActive = ancestors.stream().anyMatch((l) -> l.isActive && l.applied);
        } else {
            isActive = true;
        }
    }
    public void show(Graphics2D g2d) {
        updateProperties();
        g2d.setColor(Color.lightGray);
        if (isActive) g2d.setColor(Color.gray);
        if (applied) g2d.setColor(Color.black);
        g2d.fillRect(getX(), getY(), getWidth(), getHeight());

        for (Mutation anc : ancestors) {
            g2d.setColor(Color.black);
            if (anc.isActive) {
                g2d.setColor(Color.red);
            }
            if (anc.applied) {
                g2d.setColor(Color.green);
            }
            g2d.drawLine(getX(), getY() + getHeight()/2 ,
                    anc.getX() + getWidth(), anc.getY() + anc.getHeight()/2);
        }


        String[] words = desc.split(" ");
        int length = words.length;
        int maxLengthWord = Arrays.stream(words).mapToInt(String::length).max().getAsInt();
        int fontSize = Math.min((getHeight()-40)/length,(getWidth()/maxLengthWord));
        f = new Font("Arial", Font.BOLD, fontSize);
        int prevY = getY() + 20;
        if (Main.upgradePts<price&&isActive&&price>0&&!applied) {
            g2d.setColor(Color.red);
        }else{
            g2d.setColor(Color.white);
        }
        for (String s : words) {
            g2d.setFont(f);
            g2d.drawString(s,getX() + 15,prevY);
            prevY = (int) (prevY+ f.getStringBounds(s,g2d.getFontRenderContext()).getHeight());
        }


    }
}
