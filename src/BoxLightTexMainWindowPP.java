/**
 * Copyright 2012-2013 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.io.IOException;

/**
 * Container class of the graphics application.
 * Creates a Window (JFrame) where the OpenGL canvas is displayed in.
 * Starts an animation loop, which triggers the renderer.
 *
 * Displays a triangle using the fixed function pipeline.
 *
 * Based on a tutorial by Chua Hock-Chuan
 * http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 *
 * and on an example by Xerxes Rånby
 * http://jogamp.org/git/?p=jogl-demos.git;a=blob;f=src/demos/es2/RawGL2ES2demo.java;hb=HEAD
 *
 * @author Karsten Lehn, Darius Schippritt (changes since 2021)
 * @version 26.8.2015, 16.9.2015, 10.9.2017, 17.9.2018, 19.9.2018, 27.10.2021
 *
 */
public class BoxLightTexMainWindowPP extends JFrame {

    private static final long serialVersionUID = 1L;
    private static String FRAME_TITLE = "Start Code Main Window - Fixed Function Pipeline with Menu";

    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    private static final int GLCANVAS_WIDTH = 640;  // width of the canvas
    private static final int GLCANVAS_HEIGHT = 480; // height of the canvas
    private static final int FRAME_RATE = 60; // target frames per seconds

    public static JButton button = null;
    public static JLabel noCameraText = null;

    /**
     * Standard constructor generating a Java Swing window for displaying an OpenGL canvas.
     */
    public BoxLightTexMainWindowPP() {
        // Setup an OpenGL context for the GLCanvas
        // Using the JOGL-Profile GL2
        // GL2: Compatibility profile, OpenGL Versions 1.0 to 3.0
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // Create the OpenGL Canvas for rendering content
        GLCanvas canvas = new Larbrynth(capabilities);
        //canvas.setPreferredSize(new Dimension(GLCANVAS_WIDTH, GLCANVAS_HEIGHT));
        //canvas.setSize(new Dimension(GLCANVAS_WIDTH, GLCANVAS_HEIGHT));

        // Create an animator object for calling the display method of the GLCanvas
        // at the defined frame rate.
        final FPSAnimator animator = new FPSAnimator(canvas, FRAME_RATE, true);

        // Create the window container
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        // Create and add split pane to window
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setEnabled(false);

        // Create and add menu panel to left side of split pane
        JPanel menuPanel = new JPanel();
        splitPane.setLeftComponent(menuPanel);

        // Create and add glpanel to right side of split pane
        JPanel glPanel = new JPanel();
        splitPane.setRightComponent(glPanel);
        glPanel.add(canvas);

        createCameraView(menuPanel);

        // Add split pane to window
        this.getContentPane().add(splitPane);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Thread to stop the animator
                // before the program exits
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }
                }.start();
            }
        });
        this.setResizable(true);
        this.setTitle(FRAME_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        animator.start();

        // Set canvas size to size of glpanel
        canvas.setSize(glPanel.getSize());

        // OpenGL: request focus for canvas
        canvas.requestFocusInWindow();
    }

    public void createCameraView(JPanel menuPanel) {

        if(Webcam.getDefault()!=null) {
            Camera webcam = new Camera(Webcam.getDefault());
            menuPanel.add(webcam.getPanel());
        }
        else {
           menuPanel.setLayout(new GridLayout(3,1));
           noCameraText = new JLabel("Es wurde keine Kamera erkannt!");
           noCameraText.setHorizontalAlignment(SwingConstants.CENTER);
           button = new JButton("Erneut versuchen");
           button.addActionListener(e -> {
               System.out.println("Attempt to find camera again");
               createCameraView(menuPanel);
           });
           menuPanel.add(noCameraText);
           menuPanel.add(button);
        }
    }

    /**
     * Creates the main window and starts the program
     * @param args The arguments are not used
     */
    public static void main(String[] args) throws IOException {
        // Ensure thread safety
        SwingUtilities.invokeLater(new Runnable() {
                                       @Override
                                       public void run() {
                                           new BoxLightTexMainWindowPP();

                                       }
                                   }
        );

    }
}
