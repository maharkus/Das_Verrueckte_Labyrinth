import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

class ImagePanel extends JPanel {
    private Image img;

    public ImagePanel(String img) {
        this((new ImageIcon(img)).getImage());
    }

    public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth((ImageObserver)null), img.getHeight((ImageObserver)null));
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        this.setSize(size);
        this.setLayout((LayoutManager)null);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(this.img, 0, 0, (ImageObserver)null);
    }
}