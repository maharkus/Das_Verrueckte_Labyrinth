import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import com.github.sarxos.webcam.Webcam;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import com.sun.tools.javac.Main;


public class GameWindow {

    // width of the canvas
    private static final int GLCANVAS_WIDTH = 640;
    // height of the canvas
    private static final int GLCANVAS_HEIGHT = 480;
    // target frames per seconds
    private static final int FRAME_RATE = 60;


    public static JButton button = null;
    public static JLabel noCameraText = null;
    Labyrinth canvas;
    Player player;
    JSplitPane splitPane;
    FPSAnimator animator;

    public GameWindow(JFrame frame) {

        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // Create the OpenGL Canvas for rendering content
        canvas = new Labyrinth(capabilities);
        player = canvas.player;

        // Create an animator object for calling the display method of the GLCanvas
        // at the defined frame rate.
        animator = new FPSAnimator(canvas, FRAME_RATE, true);

        // Create and add split pane to window
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        //splitPane.setBorder(null);
        splitPane.setDividerSize(1);

        splitPane.setPreferredSize(frame.getSize());
        splitPane.setSize(frame.getSize());
        splitPane.setDividerLocation(500);
        splitPane.setEnabled(false);

        // Create and add menu panel to left side of split pane
        JPanel menuPanel = new JPanel();
        splitPane.setLeftComponent(menuPanel);
//        GridBagLayout menuGrid = new GridBagLayout();
//        GridBagConstraints c = new GridBagConstraints();
//        menuPanel.setLayout(menuGrid);

        menuPanel.setBackground(Color.BLACK);
        menuPanel.setSize(500, splitPane.getHeight());

        GridLayout grid = new GridLayout(4,1, 0,32);
        menuPanel.setLayout(grid);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(150, 50, 0, 50));



        JButton btnForward = new JButton("Vorwärts");
        btnForward.setBackground(new Color(102, 0, 153));
        btnForward.setForeground(Color.WHITE);
        btnForward.setFont(new Font("Arial", Font.BOLD, 52));
        //btnForward.setBorder(BorderFactory.createEmptyBorder(32, 64, 32, 64));
        btnForward.setFocusPainted(false);
        btnForward.setOpaque(true);
        btnForward.setBorderPainted(false);

        btnForward.addActionListener(e -> {
            canvas.move(canvas.curvePoints.get(canvas.player.getPositionIndex()).getDirections()[(int) (canvas.player.getAngle()/90)]);
        });
        menuPanel.add(btnForward);



        JButton btnLeft = new JButton("Links");
        btnLeft.setBackground(new Color(102, 0, 153));
        btnLeft.setForeground(Color.WHITE);
        btnLeft.setFont(new Font("Arial", Font.BOLD, 52));
        //btnLeft.setBorder(BorderFactory.createEmptyBorder(32, 115, 32, 115));
        btnLeft.setFocusPainted(false);
        btnLeft.setOpaque(true);
        btnLeft.setBorderPainted(false);

        btnLeft.addActionListener(e -> {
            canvas.rotate(90f);
        });
        menuPanel.add(btnLeft);



        JButton btnRight = new JButton("Rechts");
        btnRight.setBackground(new Color(102, 0, 153));
        btnRight.setForeground(Color.WHITE);
        btnRight.setFont(new Font("Arial", Font.BOLD, 52));
        //btnRight.setBorder(BorderFactory.createEmptyBorder(32, 94, 32, 94));
        btnRight.setFocusPainted(false);
        btnRight.setOpaque(true);
        btnRight.setBorderPainted(false);

        btnRight.addActionListener(e -> {
            canvas.rotate(-90f);
        });
        menuPanel.add(btnRight);



        // Create and add glpanel to right side of split pane / Game Content
        JPanel canvasPanel = new JPanel();
        splitPane.setRightComponent(canvasPanel);
        canvasPanel.add(canvas);
        GridLayout gridLayout = new GridLayout(1, 1);
        canvasPanel.setLayout(gridLayout);
        canvas.setPreferredSize(new Dimension(GLCANVAS_WIDTH, GLCANVAS_HEIGHT));

        createCameraView(menuPanel);

        // Set canvas size to size of glpanel
        canvas.setSize(canvasPanel.getSize());

        // OpenGL: request focus for canvas
        canvas.requestFocusInWindow();
    }

    public void createCameraView(JPanel menuPanel) {

        if(Webcam.getDefault()!=null) {
            menuPanel.add(Camera.Webcam.getVidpanel());
        }
        else {
         noCameraText = new JLabel("Es wurde keine Kamera erkannt!");
           button = new JButton("Erneut versuchen");
           button.addActionListener(e -> {
               System.out.println("Attempt to find camera again");
               createCameraView(menuPanel);
           });
           
           menuPanel.add(noCameraText);
        }
    }
}
