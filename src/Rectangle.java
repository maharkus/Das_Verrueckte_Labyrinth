import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;


public class Rectangle extends JPanel{

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(255, 0 , 0));
        g2d.drawRect(10, 0, 200, 150);
    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(210,160);
    }
}













