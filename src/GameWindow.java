import java.awt.*;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import com.sun.tools.javac.Main;


public class GameWindow {

    private static final long serialVersionUID = 1L;
    private static String FRAME_TITLE = "Start Code Main Window - Fixed Function Pipeline with Menu";

    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

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
        splitPane.setPreferredSize(frame.getSize());
        splitPane.setSize(frame.getSize());
        splitPane.setDividerLocation(500);
        splitPane.setEnabled(false);

        // Create and add menu panel to left side of split pane
        JPanel menuPanel = new JPanel();
        splitPane.setLeftComponent(menuPanel);
        GridBagLayout menuGrid = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        menuPanel.setLayout(menuGrid);
        menuPanel.setSize(500, splitPane.getHeight());

        JButton btnForward = new JButton("Vorwärts");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 40;
        c.weightx = 3;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        btnForward.addActionListener(e -> {
            canvas.move(canvas.curvePoints.get(canvas.player.getPositionIndex()).getDirections()[(int) (canvas.player.getAngle()/90)]);
        });
        btnForward.setSize(100, 50);

        menuPanel.add(btnForward, c);

        JButton btnLeft = new JButton("Links");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy =55;
        btnLeft.addActionListener(e -> {
            canvas.rotate(90f);
        });
        btnLeft.setSize(100, 50);
        menuPanel.add(btnLeft);

        JButton btnRight = new JButton("Rechts");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        btnRight.addActionListener(e -> {
            canvas.rotate(-90f);
        });
        btnRight.setSize(100, 50);
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
            Camera webcam = new Camera(Webcam.getDefault());
            menuPanel.add(webcam.getPanel());
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
