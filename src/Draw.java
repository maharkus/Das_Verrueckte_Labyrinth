import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;


public class Draw extends JPanel{

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Graphics2D g2d = (Graphics2D) g;

        g.setColor(new Color(255, 0 , 0));
        g.drawRect(0, 0, 200, 100);


        //g2d.setColor(new Color(255, 0, 0));
        //g.fillRect(0, 0, 190, 90);

    }
    public static JPanel drawRectangle(){
        Draw rect = new Draw();
        JPanel rectPanel = new JPanel();
        rectPanel.add(rect);
        rectPanel.setVisible(true);
        return rectPanel;
    }
    //@Override
    //public Dimension getPreferredSize(){
    //    return new Dimension(200,100);
    //}
}
