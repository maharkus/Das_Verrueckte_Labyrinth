import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class EndGame {

    public static JButton EndButton(String text, int width, int height) {

        final JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(102, 0, 153));
        button.setFont(new Font("Arial", Font.BOLD, 30));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
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

    public EndGame() {

        JFrame frame = new JFrame();
        ImagePanel panel = new ImagePanel((new ImageIcon("resources/endGameScreenSmol.jpg")).getImage());

        JLabel header = new JLabel("GLÜCKWUNSCH!!!", 0);
        header.setFont(new Font("Arial", Font.BOLD, 42));
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

    public static void main(String[] args) {

        new EndGame();

    }
}