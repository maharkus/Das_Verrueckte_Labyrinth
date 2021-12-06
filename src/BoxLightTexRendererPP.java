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

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;

/**
 * Performs the OpenGL graphics processing using the Programmable Pipeline and the
 * OpenGL Core profile
 *
 * Starts an animation loop.
 * Zooming and rotation of the Camera is included (see InteractionHandler).
 * 	Use: left/right/up/down-keys and +/-Keys
 * Draws a simple box with light and textures.
 * Serves as a template (start code) for setting up an OpenGL/Jogl application
 * using a vertex and fragment shader.
 *
 * Please make sure setting the file path and names of the shader correctly (see below).
 *
 * Core code is based on a tutorial by Chua Hock-Chuan
 * http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 *
 * and on an example by Xerxes Rånby
 * http://jogamp.org/git/?p=jogl-demos.git;a=blob;f=src/demos/es2/RawGL2ES2demo.java;hb=HEAD
 *
 * @author Karsten Lehn
 * @version 12.11.2017, 18.9.2019
 *
 */
public class BoxLightTexRendererPP extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;

    // taking shader source code files from relative path
    private final String shaderPath = ".\\resources\\";
    // Shader for object 0
//    final String vertexShader0FileName = "O0_Basic.vert";
//    final String fragmentShader0FileName = "O0_Basic.frag";
    private final String vertexShader0FileName = "BlinnPhongPointTex.vert";
    private final String fragmentShader0FileName = "BlinnPhongPointTex.frag";


    // taking texture files from relative path
    private final String texturePath = ".\\resources\\";
//    final String textureFileName = "GelbGruenPalette.png";
    final String textureFileName = "wall3.jpg";
    final String floorTextureName = "dwayne_rock.jpg";

    private ShaderProgram shaderProgram0;
    private ShaderProgram shaderProgram;

    // Pointers (names) for data transfer and handling on GPU
    private int[] vaoName;  // Name of vertex array object
    private int[] vboName;	// Name of vertex buffer object
    private int[] iboName;	// Name of index buffer object

    // Define Materials
    private Material material0;

    // Define light sources
    private LightSource light0;

    // Object for handling keyboard and mouse interaction
    private InteractionHandler interactionHandler;
    // Projection model view matrix tool
    private PMVMatrix pmvMatrix;

    private int noOfObjects;
    private int noOfWalls;
    private float[] wallPos;

    /**
     * Standard constructor for object creation.
     */
    public BoxLightTexRendererPP() {
        // Create the canvas with default capabilities
        super();
        // Add this object as OpenGL event listener
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Create the canvas with the requested OpenGL capabilities
     * @param capabilities The capabilities of the canvas, including the OpenGL profile
     */
    public BoxLightTexRendererPP(GLCapabilities capabilities) {
        // Create the canvas with the requested OpenGL capabilities
        super(capabilities);
        // Add this object as an OpenGL event listener
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Helper method for creating an interaction handler object and registering it
     * for key press and mouse interaction callbacks.
     */
    private void createAndRegisterInteractionHandler() {
        // The constructor call of the interaction handler generates meaningful default values
        // Nevertheless the start parameters can be set via setters
        // (see class definition of the interaction handler)
        interactionHandler = new InteractionHandler();
        this.addKeyListener(interactionHandler);
        this.addMouseListener(interactionHandler);
        this.addMouseMotionListener(interactionHandler);
        this.addMouseWheelListener(interactionHandler);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * that is called when the OpenGL renderer is started for the first time.
     * @param drawable The OpenGL drawable
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        // Verify if VBO-Support is available
        if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
            System.out.println("Error: VBO support is missing");
        else
            System.out.println("VBO support is available");

        // BEGIN: Preparing scene
        float[] wallSizes = new float[]{
                200, 50, 10,
                10, 50, 110,
                130, 50, 10,
                10, 50, 110,
                200, 50, 10,
                10, 50, 110,
                10, 50, 110,
                10, 50, 210,
                150, 50, 10,
                10, 50, 110,
                50, 50, 10,
                10, 50, 50,
                10, 50, 130,
                130, 50, 10,

                //Outer walls
                20, 50, 400,
                40, 50, 20,
                340, 50, 20,
                340, 50, 20,
                40, 50, 20,
                20, 50, 400,
                420, 1, 440,
        };

        wallPos = new float[] {
                90, 0, -150,
                -60, 0, -100,
                -125, 0, -150,
                -10, 0, -50,
                90, 0, -100,
                140, 0, 0,
                140, 0, 150,
                90, 0, 50,
                60, 0, 50,
                40, 0, 150,
                20, 0, 100,
                -10, 0, 120,
                -60, 0, 90,
                -125, 0, 150,

                //Outer walls
                -200, 0, 0,
                190, 0, -210,
                -40, 0, -210,
                40, 0, 210,
                -190, 0, 210,
                200, 0, 0,
                0, -25, 0
        };

        // BEGIN: Allocating vertex array objects and buffers for each object
        noOfWalls = wallSizes.length/3;
        noOfObjects = noOfWalls + 1;
        // create vertex array objects for noOfObjects objects (VAO)
        vaoName = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoName, 0);
        if (vaoName[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboName, 0);
        if (vboName[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboName, 0);
        if (iboName[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object

        // Initialize objects to be drawn (see respective sub-methods)
        for (int i = 0; i < noOfWalls; i++) {
                initObject(gl, wallSizes[i * 3], wallSizes[i * 3 + 1], wallSizes[i * 3 + 2], i);
        }
        initFloor(gl, 400, 1, 400, 21);

        // Specify light parameters
        float[] lightPosition = {0.0f, 3.0f, 3.0f, 1.0f};
        float[] lightAmbientColor = {0.6f, 0.7f, 0.8f, 1f};
        float[] lightDiffuseColor = {0.1f, 0.25f, 0.3f, 1f};
        float[] lightSpecularColor = {0.3f, 0.4f, 0.5f, 0.6f};
        light0 = new LightSource(lightPosition, lightAmbientColor,
                lightDiffuseColor, lightSpecularColor);
        // END: Preparing scene

        // Switch on back face culling
//        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
//        gl.glCullFace(GL.GL_FRONT);
        // Switch on depth test
        gl.glEnable(GL.GL_DEPTH_TEST);

        // Create projection-model-view matrix
        pmvMatrix = new PMVMatrix();

        // Start parameter settings for the interaction handler might be called here
        interactionHandler.setEyeZ(800);
        // END: Preparing scene
    }

    /**
     * Initializes the GPU for drawing object0
     * @param gl OpenGL context
     */

    /**
     * Initializes the GPU for drawing object1
     * @param gl OpenGL context
     */
    private void initObject(GL3 gl, float width,float height, float depth, int i) {
        // BEGIN: Prepare cube for drawing (object 1)
        float[] color= {1f, 1f, 1f, 1f};
        gl.glBindVertexArray(vaoName[i]);
        shaderProgram0 = new ShaderProgram(gl);
        shaderProgram0.loadShaderAndCreateProgram(shaderPath,
                vertexShader0FileName, fragmentShader0FileName);

        float[] cubeVertices = BoxTex.makeBoxVertices(width, height, depth, color);
        int[] cubeIndices = BoxTex.makeBoxIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4L,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[i]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4L,
                IntBuffer.wrap(cubeIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 11*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 11*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 11*4, 6*4);
        // Pointer for the vertex shader to the texture coordinates information per vertex
        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 11*4, 9*4);

        // Specification of material parameters (blue material)
//        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
//        float[] matAmbient =  {0.0f, 0.0f, 0.1f, 1.0f};
//        float[] matDiffuse =  {0.1f, 0.2f, 0.7f, 1.0f};
//        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
//        float matShininess = 200.0f;

        // Metallic material
        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.4f, 0.4f, 0.4f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.4f, 0.6f, 0.8f, 1.0f};
        float matShininess = 1.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        // Load and prepare texture
        Texture texture = null;
        try {
            File textureFile = new File(texturePath + textureFileName);
            texture = TextureIO.newTexture(textureFile, true);

            texture.setTexParameteri(gl, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
            texture.setTexParameteri(gl, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
            texture.setTexParameteri(gl, gl.GL_TEXTURE_WRAP_S, gl.GL_REPEAT);
            texture.setTexParameteri(gl, gl.GL_TEXTURE_WRAP_T, gl.GL_REPEAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (texture != null)
            System.out.println("Texture loaded successfully from: " + texturePath+textureFileName);
        else
            System.err.println("Error loading textue.");
        System.out.println("  Texture height: " + texture.getImageHeight());
        System.out.println("  Texture width: " + texture.getImageWidth());
        System.out.println("  Texture object: " + texture.getTextureObject(gl));
        System.out.println("  Estimated memory size of texture: " + texture.getEstimatedMemorySize());

        texture.enable(gl);
        // Activate texture in slot 0 (might have to go to "display()")
        gl.glActiveTexture(GL_TEXTURE0);
        // Use texture as 2D texture (might have to go to "display()")
        gl.glBindTexture(GL_TEXTURE_2D, texture.getTextureObject(gl));
        // END: Prepare cube for drawing
    }

    private void initFloor(GL3 gl, float width,float height, float depth, int i) {
        // BEGIN: Prepare cube for drawing (object 1)
        float[] color= {1f, 1f, 1f, 1f};
        gl.glBindVertexArray(vaoName[i]);
        shaderProgram = new ShaderProgram(gl);
        shaderProgram.loadShaderAndCreateProgram(shaderPath,
                vertexShader0FileName, fragmentShader0FileName);

        float[] cubeVertices = BoxTex.makeBoxVertices(width, height, depth, color);
        int[] cubeIndices = BoxTex.makeBoxIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[i]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4L,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[i]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4L,
                IntBuffer.wrap(cubeIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 11*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 11*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 11*4, 6*4);
        // Pointer for the vertex shader to the texture coordinates information per vertex
        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 11*4, 9*4);

        // Specification of material parameters (blue material)
//        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
//        float[] matAmbient =  {0.0f, 0.0f, 0.1f, 1.0f};
//        float[] matDiffuse =  {0.1f, 0.2f, 0.7f, 1.0f};
//        float[] matSpecular = {0.7f, 0.7f, 0.7f, 1.0f};
//        float matShininess = 200.0f;

        // Metallic material
        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.4f, 0.4f, 0.4f, 1.0f};
        float[] matDiffuse =  {0.5f, 0.5f, 0.5f, 1.0f};
        float[] matSpecular = {0.4f, 0.6f, 0.8f, 1.0f};
        float matShininess = 1.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        // Load and prepare texture
        Texture texture2 = null;
        try {
            File textureFile2 = new File(texturePath + textureFileName);
            texture2 = TextureIO.newTexture(textureFile2, true);

            texture2.setTexParameteri(gl, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
            texture2.setTexParameteri(gl, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
            texture2.setTexParameteri(gl, gl.GL_TEXTURE_WRAP_S, gl.GL_REPEAT);
            texture2.setTexParameteri(gl, gl.GL_TEXTURE_WRAP_T, gl.GL_REPEAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (texture2 != null)
            System.out.println("Texture loaded successfully from: " + texturePath + textureFileName);
        else
            System.err.println("Error loading textue.");
        System.out.println("  Texture height: " + texture2.getImageHeight());
        System.out.println("  Texture width: " + texture2.getImageWidth());
        System.out.println("  Texture object: " + texture2.getTextureObject(gl));
        System.out.println("  Estimated memory size of texture: " + texture2.getEstimatedMemorySize());

        texture2.enable(gl);
        // Activate texture in slot 0 (might have to go to "display()")
        gl.glActiveTexture(GL_TEXTURE0);
        // Use texture as 2D texture (might have to go to "display()")
        gl.glBindTexture(GL_TEXTURE_2D, texture2.getTextureObject(gl));
        // END: Prepare cube for drawing
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called by the OpenGL animator for every frame.
     * @param drawable The OpenGL drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // Background color of the canvas
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // For monitoring the interaction settings
/*        System.out.println("Camera: z = " + interactionHandler.getEyeZ() + ", " +
                "x-Rot: " + interactionHandler.getAngleXaxis() +
                ", y-Rot: " + interactionHandler.getAngleYaxis() +
                ", x-Translation: " + interactionHandler.getxPosition()+
                ", y-Translation: " + interactionHandler.getyPosition());// definition of translation of model (Model/Object Coordinates --> World Coordinates)
*/
        // Using the PMV-Tool for geometric transforms
        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
        pmvMatrix.glLoadIdentity();
        // Setting the camera position, based on user input
        pmvMatrix.gluLookAt(0f, 700f, 0,
                0f, 0f, 0f,
                0f, 0f, -1f);
        pmvMatrix.glTranslatef(interactionHandler.getxPosition(), interactionHandler.getyPosition(), 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);

        //Place all walls
        for(int i = 0; i < noOfWalls; i++) {
            pmvMatrix.glPushMatrix();
            pmvMatrix.glTranslatef(wallPos[i * 3], wallPos[i * 3 + 1], wallPos[i * 3 + 2]);
            displayObject(gl, i);
            pmvMatrix.glPopMatrix();
        }
    }

    private void displayObject(GL3 gl, int i) {
        // BEGIN: Draw the second object (object 1)
        gl.glUseProgram(shaderProgram0.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        // transfer parameters of light source
        gl.glUniform4fv(3, 1, light0.getPosition(), 0);
        gl.glUniform4fv(4, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(5, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, light0.getSpecular(), 0);
        // transfer material parameters
        gl.glUniform4fv(7, 1, material0.getEmission(), 0);
        gl.glUniform4fv(8, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(9, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, material0.getSpecular(), 0);
        gl.glUniform1f(11, material0.getShininess());

        gl.glBindVertexArray(vaoName[i]);

        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, BoxTex.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, i);
    }


    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when the OpenGL window is resized.
     * @param drawable The OpenGL drawable
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("Reshape called.");
        System.out.println("x = " + x + ", y = " + y + ", width = " + width + ", height = " + height);

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.01f, 10000f);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when OpenGL canvas ist destroyed.
     * @param drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Deleting allocated objects, incl. shader program.");
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram0.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);

        System.exit(0);
    }
}
