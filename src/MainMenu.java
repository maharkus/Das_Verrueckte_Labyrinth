import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainMenu {

    public static JButton StartButton(String text, int width, int height) {

        final JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(102, 0, 153));
        button.setFont(new Font("Futura", 0, 20));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                button.setBackground(Color.BLACK);
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(102, 0, 153));
                new BoxLightTexMainWindowPP();
            }
        });
        return button;

    }

    public static JButton ExitButton(String text, int width, int height) {

        final JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0));
        button.setFont(new Font("Futura", 0, 20));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(10,10,255));
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(0, 0, 0));
                System.exit(0);
            }
        });
        return button;

    }

    public MainMenu() {

        JFrame frame = new JFrame();
        ImagePanel panel = new ImagePanel((new ImageIcon("resources/laby.jpg")).getImage());
        JLabel header = new JLabel("Das Verrückte Labyrinth", 0);
        header.setFont(new Font("Futura", 0, 32));
        header.setForeground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200));
        panel.setBackground(Color.BLACK);
        GridLayout layout = new GridLayout(3, 1);
        layout.setVgap(32);
        panel.setLayout(layout);
        frame.add(panel);
        panel.add(header);
        panel.add(StartButton("START", 50, 200));
        panel.add(ExitButton("EXIT", 50, 100));
        frame.setTitle("Das Verrückkte Labyrinth");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo((Component)null);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);

    }

    public static void main(String[] args) {

        new MainMenu();

    }
}