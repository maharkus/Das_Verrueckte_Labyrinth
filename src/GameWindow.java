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
public class GameWindow {

    private static final long serialVersionUID = 1L;
    private static String FRAME_TITLE = "Start Code Main Window - Fixed Function Pipeline with Menu";

    private static final int WINDOW_WIDTH = 1920;
    private static final int WINDOW_HEIGHT = 1080;

    private static final int GLCANVAS_WIDTH = 640;  // width of the canvas
    private static final int GLCANVAS_HEIGHT = 480; // height of the canvas
    private static final int FRAME_RATE = 60; // target frames per seconds

    public static JButton button = null;
    public static JLabel noCameraText = null;
    Labyrinth canvas;
    Player player;
    JSplitPane splitPane;
    FPSAnimator animator;

    /**
     * Standard constructor generating a Java Swing window for displaying an OpenGL canvas.
     */
    public GameWindow(JFrame frame) {
        // Setup an OpenGL context for the GLCanvas
        // Using the JOGL-Profile GL2
        // GL2: Compatibility profile, OpenGL Versions 1.0 to 3.0
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
        GridLayout menuGrid = new GridLayout(3,2);
        menuPanel.setLayout(menuGrid);


        JButton btnForward = new JButton("Vorwärts");
        btnForward.addActionListener(e -> {
            canvas.move(canvas.curvePoints.get(canvas.player.getPositionIndex()).getDirections()[(int) (canvas.player.getAngle()/90)]);
        });
        btnForward.setSize(100, 50);
        menuPanel.add(btnForward);

        JButton btnLeft = new JButton("Links");
        btnLeft.addActionListener(e -> {
            canvas.rotate(90f);
        });
        btnLeft.setSize(100, 50);
        menuPanel.add(btnLeft);


        JButton btnRight = new JButton("Rechts");
        btnRight.addActionListener(e -> {
            canvas.rotate(-90f);
        });
        btnRight.setSize(100, 50);
        menuPanel.add(btnRight);

        // Create and add glpanel to right side of split pane
        JPanel canvasPanel = new JPanel();
        splitPane.setRightComponent(canvasPanel);
        canvasPanel.add(canvas);
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
           menuPanel.setLayout(new GridLayout(3,1));
           noCameraText = new JLabel("Es wurde keine Kamera erkannt!");
           noCameraText.setHorizontalAlignment(SwingConstants.CENTER);
           button = new JButton("Erneut versuchen");
           button.addActionListener(e -> {
               System.out.println("Attempt to find camera again");
               createCameraView(menuPanel);
           });


           menuPanel.add(noCameraText);
        }
    }
}
