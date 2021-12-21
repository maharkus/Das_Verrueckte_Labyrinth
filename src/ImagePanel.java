import java.awt.*;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel(String image) {
        this((new ImageIcon(image)).getImage());
    }

    public ImagePanel(Image image) {
        this.image = image;
        Dimension imageSize = new Dimension(image.getWidth((ImageObserver)null), image.getHeight((ImageObserver)null));
        this.setPreferredSize(imageSize);
        this.setMinimumSize(imageSize);
        this.setMaximumSize(imageSize);
        this.setSize(imageSize);
        this.setLayout((LayoutManager)null);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(this.image, 0, 0, (ImageObserver)null);
    }
}