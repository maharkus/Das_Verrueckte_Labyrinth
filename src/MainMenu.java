import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.video.Video;

public class MainMenu extends JFrame{

    GameWindow game;
    Labyrinth canvas;

    public Font spookyFont;
    Image icon = Toolkit.getDefaultToolkit().getImage("resources/transparentLogo.png");

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

    public MainMenu() {

        ImagePanel panel = new ImagePanel((new ImageIcon("resources/laby.jpg")).getImage());

        this.setIconImage(icon);
        JLabel header = new JLabel("Das Verrückte Labyrinth", 0);
        header.setFont(spookyFont().deriveFont(80f));
        header.setForeground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(300, 500, 200, 500));
        panel.setBackground(Color.BLACK);
        GridLayout layout = new GridLayout(3, 1);
        layout.setVgap(32);
        panel.setLayout(layout);
        this.add(panel);
        panel.add(header);
        panel.add(StartButton("START", 50, 200, this));
        panel.add(ExitButton("EXIT", 50, 100));

        this.setTitle("Das Verrückkte Labyrinth");
        this.setSize(1920, 1080);
        this.setLocationRelativeTo((Component)null);
        this.setDefaultCloseOperation(3);
        this.setVisible(true);
    }

    public JButton StartButton(String text, int width, int height, JFrame frame) {

        final JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(102, 0, 153));
        button.setFont(spookyFont.deriveFont(50f));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(Color.BLACK);
                button.setBackground(new Color(102, 0, 153));
                frame.getContentPane().removeAll();
                frame.repaint();
                JPanel loadingScreen = new JPanel();
                loadingScreen.setBackground(new Color(0, 0,0, 255));
                loadingScreen.setSize(frame.getSize());
                frame.add(loadingScreen);
                game = new GameWindow(frame);
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
                                if (game.animator.isStarted()) {
                                    game.animator.stop();
                                }
                                System.exit(0);
                            }
                        }.start();
                    }
                });


                java.util.Timer t = new Timer();
                t.schedule((
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {

                                        loadingScreen.setVisible(false);
                                        t.purge();
                                        t.cancel();
                                    }
                                }),
                        3000
                );
            }
        });
        return button;
    }

    public JButton ExitButton(String text, int width, int height) {

        final JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0));
        button.setFont(spookyFont.deriveFont(50f));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
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
        File background = new File("resources/sounds/backgroundMusic.wav");
        new PlayMusic(background,-20);
    }

    public Labyrinth getCanvas() {
        return canvas;
    }
}