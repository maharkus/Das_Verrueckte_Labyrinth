import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainMenu extends JFrame{


    public MainMenu() {

        ImagePanel panel = new ImagePanel((new ImageIcon("resources/laby.jpg")).getImage());
        JLabel header = new JLabel("Das Verrückte Labyrinth", 0);
        header.setFont(new Font("Futura", 0, 32));
        header.setForeground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200));
        panel.setBackground(Color.BLACK);
        GridLayout layout = new GridLayout(3, 1);
        layout.setVgap(32);
        panel.setLayout(layout);
        this.add(panel);
        panel.add(header);
        panel.add(StartButton("START", 50, 200, this));
        panel.add(ExitButton("EXIT", 50, 100));
        this.setTitle("Das Verrückkte Labyrinth");
        this.setSize(1000, 700);
        this.setLocationRelativeTo((Component)null);
        this.setDefaultCloseOperation(3);
        this.setVisible(true);

    }


    public static JButton StartButton(String text, int width, int height, JFrame frame) {

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
                frame.getContentPane().removeAll();
                frame.repaint();

                GameWindow game = new GameWindow(frame);
                frame.add(game.splitPane);
                frame.setVisible(true);
                game.animator.start();
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Thread to stop the animator
                        // before the program exits
                        new Thread() {
                            @Override
                            public void run() {
                                if (game.animator.isStarted()) game.animator.stop();
                                System.exit(0);
                            }
                        }.start();
                    }
                });
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

    public static void main(String[] args) {

        new MainMenu();

    }
}