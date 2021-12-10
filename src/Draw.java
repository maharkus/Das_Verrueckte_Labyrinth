import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;


public class Draw extends JPanel{

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(255, 0 , 0));
        g2d.drawRect(00, 0, 200, 150);


        //g2d.setColor(new Color(255, 0, 0));
        //g2d.fillRect(30, 50, 420, 120);

    }
    public static Draw drawRectangle(){
        Draw rect = new Draw();
        return rect;
    }
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(210,160);
    }
}













