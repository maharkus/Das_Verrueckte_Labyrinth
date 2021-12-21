import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class EndGame {

    public static Font spookyFont;

    public Font spookyFont() {
        try {
            spookyFont = Font.createFont(Font.TRUETYPE_FONT, new File
                    ("resources/fonts/StrangerCreature-MVpBr.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File
                    ("resources/fonts/StrangerCreature-MVpBr.ttf")));
        }
        catch(IOException | FontFormatException e) {

        }
        return spookyFont;
    }

    public EndGame() {

        JFrame frame = new JFrame();
        ImagePanel panel = new ImagePanel((new ImageIcon("resources/endGameScreenSmol.jpg")).getImage());

        JLabel header = new JLabel("GLÜCKWUNSCH", 0);
        header.setFont(spookyFont().deriveFont(60f));
        header.setForeground(new Color(102, 0, 153));

        JLabel text = new JLabel("Du hast den Weg heraus gefunden.", 0);
        text.setFont(new Font("Arial", Font.BOLD, 22));
        text.setForeground(new Color(255, 255, 255));

        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        frame.setBackground(new Color(0,0,0));

        GridLayout layout = new GridLayout(3, 1);
        layout.setVgap(32);
        panel.setLayout(layout);
        frame.add(panel);
        panel.add(header);
        panel.add(text);
        panel.add(EndButton("EXIT GAME", 30, 75));
        frame.setTitle("Das Verrückkte Labyrinth");
        frame.setSize(700, 400);
        frame.setResizable(false);
        frame.setLocationRelativeTo((Component)null);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }

    public static JButton EndButton(String text, int width, int height) {

        final JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(102, 0, 153));
        button.setFont(spookyFont.deriveFont(30f));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                button.setForeground(Color.BLACK);
                button.setBackground(new Color(255,255,255));
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(0, 0, 0));
                System.exit(0);
            }
        });
        return button;

    }

    public static void main(String[] args) {

        new EndGame();

    }
}