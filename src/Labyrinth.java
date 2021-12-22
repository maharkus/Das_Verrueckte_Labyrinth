/**
 * Copyright 2012-2013 JogAmp Community. All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import de.hshl.obj.loader.OBJLoader;
import de.hshl.obj.loader.Resource;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2.GL_MAP1_VERTEX_3;
import static com.jogamp.opengl.math.FloatUtil.cos;
import static java.lang.Math.sin;


public class Labyrinth extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;

    // taking shader source code files from relative path
    private final String shaderPath = ".\\resources\\";
    private final String fragShader = "BlinnPhongPoint.frag";
    private final String vertShader = "BlinnPhongPoint.vert";


    private ShaderProgram shaderProgramLab;
    private ShaderProgram shaderProgramBones;
    private ShaderProgram shaderProgramTorches;
    private ShaderProgram shaderProgramPumpkin;

    private LightSource lightLab;
    private Material materialLab;
    private LightSource lightTorch;
    private Material materialTorch;
    private LightSource lightPumpkin;
    private Material materialPumpkin;

    // taking texture files from relative path
    private final String texturePath = ".\\resources\\";
    final String textureFileName = "rock_texture.jpg";

    // Pointers (names) for data transfer and handling on GPU
    private int[] vaoName;  // Name of vertex array object
    private int[] vboName;    // Name of vertex buffer object
    private int[] iboName;    // Name of index buffer object

    // Object for handling keyboard and mouse interaction
    private InteractionHandler interactionHandler;
    // Projection model view matrix tool
    private PMVMatrix pmvMatrix;


    Player player;
    float[] nextFocus = new float[3];
    boolean focusSet = false;

    AmbientSounds ambientSounds = new AmbientSounds();

    private int noOfWalls;
    private float[] wallPos;
    ArrayList<StopPoint> curvePoints = new ArrayList<>();

    private static final Path skullObj = Paths.get("./resources/models/Skull.obj");
    private static final Path skullRotObj = Paths.get("./resources/models/SkullRot.obj");
    private static final Path torchObj = Paths.get("./resources/models/torch.obj");
    private static final Path torch180Obj = Paths.get("./resources/models/torch180.obj");
    private static final Path boneObj = Paths.get("./resources/models/Bone.obj");
    private static final Path pumpkinObj = Paths.get("./resources/models/pumpkin.obj");
    private float[] skullRotVertices;
    private float[] bigSkullVertices;
    private float[] torchVertices;
    private float[] torch180Vertices;
    private float[] boneVertices;
    private float[] pumpkinVertices;

    public Labyrinth() {
        // Create the canvas with default capabilities
        super();
        // Add this object as OpenGL event listener
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }


    public Labyrinth(GLCapabilities capabilities) {
        // Create the canvas with the requested OpenGL capabilities
        super(capabilities);
        // Add this object as an OpenGL event listener
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }


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


    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));


        // Light of Labyrinth
        float[] lightPosition = {0.0f, 500.0f, 0f, 1f};
        float[] lightAmbientColor = {0.33333f, 0.11765f, 0.89412f, .5f};
        float[] lightDiffuseColor = {0.2f, 0.3f, 0.4f, .3f};
        float[] lightSpecularColor = {0.4f, 0.3f, 0.2f, .3f};
        lightLab = new LightSource(lightPosition, lightAmbientColor,
                lightDiffuseColor, lightSpecularColor);

        // Light of Torches
        float[] lightPositionT = {0.0f, 500f, 0f, 1f};
        float[] lightAmbientColorT = {1, 1, 1, .5f};
        float[] lightDiffuseColorT = {0.4f, 0.3f, 0.2f, .3f};
        float[] lightSpecularColorT = {0.4f, 0.3f, 0.2f, .3f};
        lightTorch = new LightSource(lightPositionT, lightAmbientColorT,
                lightDiffuseColorT, lightSpecularColorT);

        // Light of Pumpkins
        float[] lightPositionP = {0.0f, 0f, 0f, 1};
        float[] lightAmbientColorP = {.5f, .5f, .5f, .5f};
        float[] lightDiffuseColorP = {.5f, .5f, .5f, .5f};
        float[] lightSpecularColorP = {.5f, .5f, .5f, .5f};
        lightPumpkin = new LightSource(lightPositionP, lightAmbientColorP,
                lightDiffuseColorP, lightSpecularColorP);


        // Verify if VBO-Support is available
        if (!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
            System.out.println("Error: VBO support is missing");
        else
            System.out.println("VBO support is available");


        //Create Player
        player = new Player(new float[]{-155f, 0.3f, 240f}, new float[]{-155f, 0.3f, 0f});
        nextFocus = changeFocusPoint(player.getPosition(), player.getFocus(), 1 / 2f);


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
                210, 50, 10,
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
        };

        wallPos = new float[]{
                90, 0, -150,
                -60, 0, -100,
                -125, 0, -150,
                -10, 0, -50,
                40, 0, -100,
                140, 0, 0,
                140, 0, 150,
                90, 0, 50,
                40, 0, 50,
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
        };

        float[][] positions = new float[][]{
                //0
                {-155f, 0.3f, 180f},
                //1
                {-30f, 0.3f, 180f},
                //2
                {15f, 0.3f, 180f},
                //3
                {15f, 0.3f, 130f},
                //4
                {-30f, 0.3f, 80f},
                //5
                {65f, 0.3f, 80f},
                //6
                {65f, 0.3f, 180f},
                //7
                {110f, 0.3f, 180f},
                //8
                {110f, 0.3f, 80f},
                //9
                {165f, 0.3f, 80f},
                //10
                {165f, 0.3f, 180f},
                //11
                {165f, 0.3f, -70f},
                //12
                {165f, 0.3f, -120f},
                //13
                {115f, 0.3f, -70f},
                //14
                {115f, 0.3f, 20f},
                //15
                {45f, 0.3f, -70f},
                //16
                {45f, 0.3f, -10f},
                //17
                {45f, 0.3f, 20f},
                //18
                {-35f, 0.3f, 20f},
                //19
                {-35f, 0.3f, -10f},
                //20
                {-90f, 0.3f, -10f},
                //21
                {-35f, 0.3f, -120f},
                //22
                {-35f, 0.3f, -180f},
                //23
                {-155f, 0.3f, -180f},
                //24
                {150f, 0.3f, -180f},
                //25
                {150f, 0.3f, -220f}
        };

        int[][] directions = new int[][]{
                //0
                {1, 0, 0, 0},
                //1
                {2, 4, 0, 1},
                //2
                {2, 3, 1, 2},
                //3
                {3, 3, 3, 2},
                //4
                {5, 4, 4, 1},
                //5
                {5, 5, 4, 6},
                //6
                {7, 5, 6, 6},
                //7
                {7, 8, 6, 7},
                //8
                {9, 8, 8, 7},
                //9
                {9, 11, 8, 10},
                //10
                {10, 9, 10, 10},
                //11
                {11, 12, 13, 9},
                //12
                {12, 12, 21, 11},
                //13
                {11, 13, 15, 14},
                //14
                {14, 13, 14, 14},
                //15
                {13, 15, 15, 16},
                //16
                {16, 15, 16, 17},
                //17
                {17, 16, 18, 17},
                //18
                {17, 19, 18, 18},
                //19
                {19, 19, 20, 18},
                //20
                {19, 20, 20, 20},
                //21
                {12, 22, 21, 21},
                //22
                {24, 22, 23, 21},
                //23
                {22, 23, 23, 23},
                //24
                {22, 25, 22, 24},
                //25
                {25, 25, 25, 24}

        };

        for (int i = 0; i < positions.length; i++) {
            curvePoints.add(i, new StopPoint(positions[i], directions[i]));
        }


        // BEGIN: Allocating vertex array objects and buffers for each object
        noOfWalls = wallSizes.length / 3;
        int noOfObjects = noOfWalls + 2;
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

        initFloor(gl, 420, 1, 440, noOfObjects - 1);
        initBlender(drawable);


        // Switch on back face culling
        gl.glEnable(GL.GL_CULL_FACE);

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_FILL);

        gl.glCullFace(GL.GL_BACK);
//        gl.glCullFace(GL.GL_FRONT);
        // Switch on depth test
        gl.glEnable(GL.GL_DEPTH_TEST);


        // Create projection-model-view matrix
        pmvMatrix = new PMVMatrix();

        // Start parameter settings for the interaction handler might be called here
        interactionHandler.setEyeZ(2);
        // END: Preparing scene

    }

    private void initObject(GL3 gl, float width, float height, float depth, int i) {
        // BEGIN: Prepare cube for drawing (object 1)

        float[] color = {1f, 1f, 1f, 1f};
        gl.glBindVertexArray(vaoName[i]);
        shaderProgramLab = new ShaderProgram(gl);
        shaderProgramLab.loadShaderAndCreateProgram(shaderPath,
                vertShader, fragShader);

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
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 11 * 4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 11 * 4, 3 * 4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 11 * 4, 6 * 4);
        // Pointer for the vertex shader to the texture coordinates information per vertex
        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 11 * 4, 9 * 4);

        float[] matEmission = {0, 0, 0, 1f};
        float[] matAmbient = {0.53333f, 0.11765f, 0.59412f, 1};
        float[] matDiffuse = {0.8f, 0.0f, 0.0f, 1.0f};
        float[] matSpecular = {0.8f, 0.8f, 0.8f, 1.0f};
        float matShininess = 10.0f;
        materialLab = new Material(matEmission, matAmbient,
                matDiffuse, matSpecular, matShininess);

        // Load and prepare texture
        Texture texture = null;
        try {
            File textureFile = new File(texturePath+textureFileName);
            texture = TextureIO.newTexture(textureFile, true);
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

    }

    private void initFloor(GL3 gl, float width, float height, float depth, int i) {
        // BEGIN: Prepare cube for drawing (object 1)
        float[] color = {1f, 1f, 1f, 1f};

        gl.glBindVertexArray(vaoName[i]);

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
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 11 * 4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 11 * 4, 3 * 4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 11 * 4, 6 * 4);
        // Pointer for the vertex shader to the texture coordinates information per vertex
        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 11 * 4, 9 * 4);

        float[] matEmission = {0, 0, 0, 1f};
        float[] matAmbient = {0.53333f, 0.11765f, 0.59412f, 1};
        float[] matDiffuse = {0.8f, 0.0f, 0.0f, 1.0f};
        float[] matSpecular = {0.8f, 0.8f, 0.8f, 1.0f};
        float matShininess = 100.0f;
        materialLab = new Material(matEmission, matAmbient,
                matDiffuse, matSpecular, matShininess);

        // Load and prepare texture
        Texture texture = null;
        try {
            File textureFile = new File(texturePath+textureFileName);
            texture = TextureIO.newTexture(textureFile, true);
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

    public void initBlender(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL2 gl = drawable.getGL().getGL2();
        // Outputs information about the available and chosen profile
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        try {
            skullRotVertices = scaledObj(4, new OBJLoader().setLoadNormals(true).loadMesh(Resource.file(skullRotObj)).getVertices());
            bigSkullVertices = scaledObj(12, new OBJLoader().setLoadNormals(true).loadMesh(Resource.file(skullObj)).getVertices());
            torchVertices = scaledObj(1, new OBJLoader().setLoadNormals(true).loadMesh(Resource.file(torchObj)).getVertices());
            torch180Vertices = scaledObj(1, new OBJLoader().setLoadNormals(true).loadMesh(Resource.file(torch180Obj)).getVertices());
            boneVertices = scaledObj(0.75f, new OBJLoader().setLoadNormals(true).loadMesh(Resource.file(boneObj)).getVertices());
            pumpkinVertices = scaledObj(6, new OBJLoader().setLoadNormals(true).loadMesh(Resource.file(pumpkinObj)).getVertices());
        }
        catch (IOException fileException) {
            fileException.printStackTrace();
            System.exit(1);
        }

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 11 * 4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 11 * 4, 3 * 4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 11 * 4, 6 * 4);
        // Pointer for the vertex shader to the texture coordinates information per vertex
        gl.glEnableVertexAttribArray(3);
        gl.glVertexAttribPointer(3, 2, GL.GL_FLOAT, false, 11 * 4, 9 * 4);

        float[] matEmissionT = {0.1f, 0.1f, 0.1f, .2f};
        float[] matAmbientT = {0.1f, 0.1f, 0.1f, .2f};
        float[] matDiffuseT = {0.1f, 0.1f, 0.1f, .2f};
        float[] matSpecularT = {0.1f, 0.1f, 0.1f, .2f};
        float matShininessT = 200;
        materialTorch = new Material(matEmissionT, matAmbientT,
                matDiffuseT, matSpecularT, matShininessT);

        float[] matEmissionP = {1f, .33f, .05f, .2f};
        float[] matAmbientP = {1f, .33f, .05f, .2f};
        float[] matDiffuseP = {1f, .33f, .05f, .2f};
        float[] matSpecularP = {1f, .33f, .05f, .2f};
        float matShininessP = 10;
        materialPumpkin = new Material(matEmissionP, matAmbientP,
                matDiffuseP, matSpecularP, matShininessP);



        // enable shading
        gl.glEnable(gl.GL_LIGHTING);
        gl.glEnable(gl.GL_LIGHT0);
        gl.glCullFace(GL.GL_BACK);
        gl.glCullFace(GL.GL_FRONT);
        //gl.glEnable(gl.GL_CULL_FACE);
        gl.glEnable(gl.GL_DEPTH_TEST);
    }

    public void drawCurve(GL2 gl, ArrayList<StopPoint> curvePoints) {

        gl.glEnable(GL_MAP1_VERTEX_3);

        gl.glPointSize(20f);
        gl.glColor3f(1f, 1f, 1f);
        gl.glBegin(GL_POINTS);
        for (StopPoint curvePoint : curvePoints) {
            gl.glVertex3f(curvePoint.getPos()[0], curvePoint.getPos()[1], curvePoint.getPos()[2]);
        }

        gl.glEnd();
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        GL2 gl2 = drawable.getGL().getGL2();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        // Background color of the canvas
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Using the PMV-Tool for geometric transforms
        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
        pmvMatrix.glLoadIdentity();

        // Setting the camera position, based on user input
        pmvMatrix.gluLookAt(player.getPositionX(), player.getPositionY(), player.getPositionZ(),
                player.getFocusX(), player.getFocusY(), player.getFocusZ(),
                0f, 1f, 0f);
        //pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        //pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);


        //Place all walls
        for (int i = 0; i < noOfWalls; i++) {
            pmvMatrix.glPushMatrix();
            pmvMatrix.glTranslatef(wallPos[i * 3], wallPos[i * 3 + 1], wallPos[i * 3 + 2]);
            displayObject(gl, i);
            pmvMatrix.glPopMatrix();
        }

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, -25, 0);
        displayObject(gl, 21);
        pmvMatrix.glPopMatrix();


        //display blender .obj
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        displayBigSkull(gl2, -184f, 0, 178);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        pmvMatrix.glRotatef(70,0,1,0);
        displaySkull(gl2,-170.5f, -25,-75);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glRotatef(42,0,1,0);
        pmvMatrix.glTranslatef(0, 0, 0);
        displaySkull(gl2,-150.5f, -25,-15);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        pmvMatrix.glRotatef(2,0,1,0);
        displaySkull(gl2,-160.5f, -25,60);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        pmvMatrix.glRotatef(33,0,1,0);
        displaySkull(gl2,-120.5f, -25,5);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        pmvMatrix.glRotatef(21,0,1,0);
        displaySkull(gl2,-100.5f, -25,55);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        pmvMatrix.glRotatef(55,0,1,0);
        displaySkull(gl2,-125.5f, -25,-60);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        pmvMatrix.glRotatef(5,0,1,0);
        displaySkull(gl2,-90.5f, -25,-85);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0, 0, 0);
        pmvMatrix.glRotatef(10,0,1,0);
        displaySkull(gl2,-150.5f, -25,80);
        pmvMatrix.glPopMatrix();

        displayTorch(gl2, 82.5f,5,-10);
        displayTorch(gl2, 132.5f,5,-10);
        displayTorch(gl2, 132.5f,5,160);
        displayTorch(gl2, 187.5f,5,160);
        displayTorch(gl2, 32.5f, 5,150);
        displayTorch(gl2, 82.f, 5,110);
        displayTorch(gl2, -67, 5,-55);
        displayTorch(gl2, -67, 5,35);
        displayTorch(gl2, 187.5f,5,-10);
        displayTorch(gl2, 187.5f,5,-178);
        displayTorch(gl2, 187.5f,5,-126);

        displayTorch180(gl2, -52.5f,5,96);
        displayTorch180(gl2, -52.5f,5,-96);
        displayTorch180(gl2, -187.5f, 5,-55);
        displayTorch180(gl2, -187.5f,5,35);
        displayTorch180(gl2, -187.5f,5,-178);

        displayBone(gl2,-150.5f, -23,-100);
        displayBone(gl2,-100.5f, -23,70);
        displayBone(gl2,-130.5f, -23,30);
        displayBone(gl2,-105.5f, -23,100);
        displayBone(gl2,-170.5f, -23,-15);
        displayBone(gl2,-110.5f, -23,-30);
        displayBone(gl2,-135.5f, -23,-80);
        displayBone(gl2,-125.5f, -23,140);

        displayPumpkin(gl2, -160,-20,162);
        displayPumpkin(gl2, 180,-20,-192);
        displayPumpkin(gl2, 102,-20,62);
        displayPumpkin(gl2, -180, -20,-130);
        displayPumpkin(gl2, -180, -20,-130);

        drawCurve(gl2, curvePoints);
    }

    //Player Movements

    public void move(int curveIndex) {

        float[] newPos = curvePoints.get(curveIndex).getPos();

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                player.setPosition(moveBetweenPoints(player.getPosition(), newPos, player.getPosition()));
                player.setFocus(moveBetweenPoints(player.getPosition(), newPos, player.getFocus()));
                if (Arrays.equals(player.getPosition(), newPos)) {
                    t.purge();
                    t.cancel();
                    player.setPositionIndex(curveIndex);
                }
            }
        }, 0, 8);

        if (curveIndex == 0) {
            File welcome = new File("resources/sounds/welcome.wav");
            new PlaySound(welcome,0);
        }
        else if (curveIndex == 3 || curveIndex == 10 || curveIndex == 14 || curveIndex == 23) {
            File welcome = new File("resources/sounds/sackgasse.wav");
            new PlaySound(welcome,0);
        }
        else if (curveIndex == 25) {
            new EndGame();
            File spookySound = new File("resources/sounds/dootDoot.wav");
            new PlaySound(spookySound, 0f);
        }
        else if (Math.random() < 1) {
            new PlaySound(ambientSounds.getRandomFile(),-20);
        }

    }

    public void rotate(float deg) {
        if (!focusSet) {
            nextFocus = changeFocusPoint(player.getPosition(), player.getFocus(), deg);
            focusSet = true;
        }
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                player.setFocus(rotateBetweenPoints(player.getFocus(), nextFocus));
                if (Arrays.equals(player.getFocus(), nextFocus)) {
                    focusSet = false;
                    t.purge();
                    t.cancel();
                    player.setAngle(player.getAngle() + deg);
                    if (player.getAngle() < 0) {
                        player.setAngle(270);
                    } else if (player.getAngle() >= 360) {
                        player.setAngle(0);
                    }
                }
            }
        }, 0, 5);
    }

    private float[] changeFocusPoint(float[] pos, float[] focus, float deg) {

        //Help vector in 0/0/0
        float[] vector = new float[3];
        for (int i = 0; i < focus.length; i++) {
            vector[i] = focus[i] - pos[i];
        }

        //Convert degree to radiant
        deg = (float) Math.toRadians(deg);

        //Rotation applied via matrix
        focus[0] = (float) (cos(deg) * vector[0] + sin(deg) * vector[2] + pos[0]);
        focus[2] = (float) (-sin(deg) * vector[0] + cos(deg) * vector[2] + pos[2]);

        //Return new focus point
        return focus;
    }

    private float[] moveBetweenPoints(float[] pos1, float[] pos2, float[] applyTo) {


        // transition x and z value, keep y
        for (int i = 0; i < 2; i++) {
            int j = i * 2;
            if (pos1[j] < pos2[j]) {
                applyTo[j]++;
            } else if (pos1[j] - 1 > pos2[j]) {
                applyTo[j]--;
            } else {
                applyTo[j] = applyTo[j] + pos2[j] - pos1[j];
            }
        }

        return applyTo;
    }

    private float[] rotateBetweenPoints(float[] pos1, float[] pos2) {

        // transition x and z value, keep y
        for (int i = 0; i < 2; i++) {
            int j = i * 2;
            if (pos1[j] < pos2[j]) {
                pos1[j]++;
            } else if (pos1[j] - 1 > pos2[j]) {
                pos1[j]--;
            } else {
                pos1[j] = pos2[j];
            }
        }

        return pos1;
    }

    //Display methods

    private void displayObject(GL3 gl, int i) {
        // BEGIN: Draw the second object (object 1)
        gl.glUseProgram(shaderProgramLab.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        // transfer parameters of light source
        gl.glUniform4fv(3, 1, lightLab.getPosition(), 0);
        gl.glUniform4fv(4, 1, lightLab.getAmbient(), 0);
        gl.glUniform4fv(5, 1, lightLab.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, lightLab.getSpecular(), 0);
        // transfer material parameters
        gl.glUniform4fv(7, 1, materialLab.getEmission(), 0);
        gl.glUniform4fv(8, 1, materialLab.getAmbient(), 0);
        gl.glUniform4fv(9, 1, materialLab.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, materialLab.getSpecular(), 0);
        gl.glUniform1f(11, materialLab.getShininess());

        gl.glBindVertexArray(vaoName[i]);

        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, BoxTex.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);
    }

    public void displayTorch(GL2 gl, float xCoor, float yCoor, float zCoor) {

        shaderProgramTorches = new ShaderProgram(gl);
        shaderProgramTorches.loadShaderAndCreateProgram(shaderPath,
                vertShader, fragShader);

        gl.glUseProgram(shaderProgramTorches.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        // transfer of transposed inverse of model view matrix
        gl.glUniformMatrix4fv(2, 1, false,
                pmvMatrix.glGetMvitMatrixf());
// transfer parameters of light source
        gl.glUniform4fv(3, 1, lightTorch.getPosition(), 0);
        gl.glUniform4fv(4, 1, lightTorch.getAmbient(), 0);
        gl.glUniform4fv(5, 1, lightTorch.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, lightTorch.getSpecular(), 0);
// transfer material parameters
        gl.glUniform4fv(7, 1, materialTorch.getEmission(), 0);
        gl.glUniform4fv(8, 1, materialTorch.getAmbient(), 0);
        gl.glUniform4fv(9, 1, materialTorch.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, materialTorch.getSpecular(), 0);
        gl.glUniform1f(11, materialTorch.getShininess());

        gl.glBindVertexArray(vaoName[1]);

        // BEGIN: definition of scene content (i.e. objects, models)
        gl.glBegin(GL.GL_TRIANGLES);

        {

            for (int vertexIndex = 0; vertexIndex + 5 < torchVertices.length; vertexIndex += 6) {
                float x = torchVertices[vertexIndex] + xCoor;
                float y = torchVertices[vertexIndex + 1] + yCoor;
                float z = torchVertices[vertexIndex + 2] + zCoor;

                float nx = torchVertices[vertexIndex + 3];
                float ny = torchVertices[vertexIndex + 4];
                float nz = torchVertices[vertexIndex + 5];

                gl.glNormal3f(nx, ny, nz);
                gl.glVertex3f(x, y, z);
            }

        }
        gl.glEnd();
        // END: definition of scene content
    }

    public void displayTorch180(GL2 gl, float xCoor, float yCoor, float zCoor) {

        gl.glUseProgram(shaderProgramTorches.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        // transfer of transposed inverse of model view matrix
        gl.glUniformMatrix4fv(2, 1, false,
                pmvMatrix.glGetMvitMatrixf());
// transfer parameters of light source
        gl.glUniform4fv(3, 1, lightTorch.getPosition(), 0);
        gl.glUniform4fv(4, 1, lightTorch.getAmbient(), 0);
        gl.glUniform4fv(5, 1, lightTorch.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, lightTorch.getSpecular(), 0);
// transfer material parameters
        gl.glUniform4fv(7, 1, materialTorch.getEmission(), 0);
        gl.glUniform4fv(8, 1, materialTorch.getAmbient(), 0);
        gl.glUniform4fv(9, 1, materialTorch.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, materialTorch.getSpecular(), 0);
        gl.glUniform1f(11, materialTorch.getShininess());

        gl.glBindVertexArray(vaoName[1]);
        // BEGIN: definition of scene content (i.e. objects, models)
        gl.glBegin(GL.GL_TRIANGLES);
        {

            for (int vertexIndex = 0; vertexIndex + 5 < torch180Vertices.length; vertexIndex += 6) {
                float x = torch180Vertices[vertexIndex] + xCoor;
                float y = torch180Vertices[vertexIndex + 1] + yCoor;
                float z = torch180Vertices[vertexIndex + 2] + zCoor;

                float nx = torch180Vertices[vertexIndex + 3];
                float ny = torch180Vertices[vertexIndex + 4];
                float nz = torch180Vertices[vertexIndex + 5];

                gl.glNormal3f(nx, ny, nz);
                gl.glVertex3f(x, y, z);
            }

        }
        gl.glEnd();
        // END: definition of scene content
    }

    public void displayBigSkull(GL2 gl, float xCoor, float yCoor, float zCoor) {

        gl.glUseProgram(shaderProgramLab.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        // transfer of transposed inverse of model view matrix
        gl.glUniformMatrix4fv(2, 1, false,
                pmvMatrix.glGetMvitMatrixf());
// transfer parameters of light source
        gl.glUniform4fv(3, 1, lightLab.getPosition(), 0);
        gl.glUniform4fv(4, 1, lightLab.getAmbient(), 0);
        gl.glUniform4fv(5, 1, lightLab.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, lightLab.getSpecular(), 0);
// transfer material parameters
        gl.glUniform4fv(7, 1, materialLab.getEmission(), 0);
        gl.glUniform4fv(8, 1, materialLab.getAmbient(), 0);
        gl.glUniform4fv(9, 1, materialLab.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, materialLab.getSpecular(), 0);
        gl.glUniform1f(11, materialLab.getShininess());

        gl.glBindVertexArray(vaoName[1]);

        // BEGIN: definition of scene content (i.e. objects, models)
        gl.glBegin(GL.GL_TRIANGLES);
        {
            //float[] rotObj = rotatedObj(deg, skullVertices);

            for (int vertexIndex = 0; vertexIndex + 5 < bigSkullVertices.length; vertexIndex += 6) {
                float x = bigSkullVertices[vertexIndex] + xCoor;
                float y = bigSkullVertices[vertexIndex + 1] + yCoor;
                float z = bigSkullVertices[vertexIndex + 2] + zCoor;

                float nx = bigSkullVertices[vertexIndex + 3];
                float ny = bigSkullVertices[vertexIndex + 4];
                float nz = bigSkullVertices[vertexIndex + 5];

                gl.glNormal3f(nx, ny, nz);
                gl.glVertex3f(x, y, z);
            }
        }
        gl.glEnd();
        // END: definition of scene content
    }

    public void displaySkull(GL2 gl, float xCoor, float yCoor, float zCoor) {

        gl.glUseProgram(shaderProgramLab.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        // transfer of transposed inverse of model view matrix
        gl.glUniformMatrix4fv(2, 1, false,
                pmvMatrix.glGetMvitMatrixf());
// transfer parameters of light source
        gl.glUniform4fv(3, 1, lightLab.getPosition(), 0);
        gl.glUniform4fv(4, 1, lightLab.getAmbient(), 0);
        gl.glUniform4fv(5, 1, lightLab.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, lightLab.getSpecular(), 0);
// transfer material parameters
        gl.glUniform4fv(7, 1, materialLab.getEmission(), 0);
        gl.glUniform4fv(8, 1, materialLab.getAmbient(), 0);
        gl.glUniform4fv(9, 1, materialLab.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, materialLab.getSpecular(), 0);
        gl.glUniform1f(11, materialLab.getShininess());

        gl.glBindVertexArray(vaoName[1]);

        // BEGIN: definition of scene content (i.e. objects, models)
        gl.glBegin(GL.GL_TRIANGLES);
        {
            //float[] rotObj = rotatedObj(deg, skullVertices);

            for (int vertexIndex = 0; vertexIndex + 5 < skullRotVertices.length; vertexIndex += 6) {
                float x = skullRotVertices[vertexIndex] + xCoor;
                float y = skullRotVertices[vertexIndex + 1] + yCoor;
                float z = skullRotVertices[vertexIndex + 2] + zCoor;

                float nx = skullRotVertices[vertexIndex + 3];
                float ny = skullRotVertices[vertexIndex + 4];
                float nz = skullRotVertices[vertexIndex + 5];

                gl.glNormal3f(nx, ny, nz);
                gl.glVertex3f(x, y, z);
            }
        }
        gl.glEnd();
        // END: definition of scene content
    }


    public void displayBone(GL2 gl, float xCoor, float yCoor, float zCoor) {

        gl.glUseProgram(shaderProgramLab.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        // transfer of transposed inverse of model view matrix
        gl.glUniformMatrix4fv(2, 1, false,
                pmvMatrix.glGetMvitMatrixf());
// transfer parameters of light source
        gl.glUniform4fv(3, 1, lightLab.getPosition(), 0);
        gl.glUniform4fv(4, 1, lightLab.getAmbient(), 0);
        gl.glUniform4fv(5, 1, lightLab.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, lightLab.getSpecular(), 0);
// transfer material parameters
        gl.glUniform4fv(7, 1, materialLab.getEmission(), 0);
        gl.glUniform4fv(8, 1, materialLab.getAmbient(), 0);
        gl.glUniform4fv(9, 1, materialLab.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, materialLab.getSpecular(), 0);
        gl.glUniform1f(11, materialLab.getShininess());

        gl.glBindVertexArray(vaoName[1]);

        // BEGIN: definition of scene content (i.e. objects, models)
        gl.glBegin(GL.GL_TRIANGLES);
        {

            for (int vertexIndex = 0; vertexIndex + 5 < boneVertices.length; vertexIndex += 6) {
                float x = boneVertices[vertexIndex] + xCoor;
                float y = boneVertices[vertexIndex + 1] + yCoor;
                float z = boneVertices[vertexIndex + 2] + zCoor;

                float nx = boneVertices[vertexIndex + 3];
                float ny = boneVertices[vertexIndex + 4];
                float nz = boneVertices[vertexIndex + 5];

                gl.glNormal3f(nx, ny, nz);
                gl.glVertex3f(x, y, z);
            }

        }
        gl.glEnd();
        // END: definition of scene content
    }

    public void displayPumpkin(GL2 gl, float xCoor, float yCoor, float zCoor) {

        shaderProgramPumpkin = new ShaderProgram(gl);
        shaderProgramPumpkin.loadShaderAndCreateProgram(shaderPath,
                vertShader, fragShader);

        gl.glUseProgram(shaderProgramPumpkin.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        // transfer of transposed inverse of model view matrix
        gl.glUniformMatrix4fv(2, 1, false,
                pmvMatrix.glGetMvitMatrixf());
// transfer parameters of light source
        gl.glUniform4fv(3, 1, lightPumpkin.getPosition(), 0);
        gl.glUniform4fv(4, 1, lightPumpkin.getAmbient(), 0);
        gl.glUniform4fv(5, 1, lightPumpkin.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, lightPumpkin.getSpecular(), 0);
// transfer material parameters
        gl.glUniform4fv(7, 1, materialPumpkin.getEmission(), 0);
        gl.glUniform4fv(8, 1, materialPumpkin.getAmbient(), 0);
        gl.glUniform4fv(9, 1, materialPumpkin.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, materialPumpkin.getSpecular(), 0);
        gl.glUniform1f(11, materialPumpkin.getShininess());

        gl.glBindVertexArray(vaoName[1]);

        // BEGIN: definition of scene content (i.e. objects, models)
        gl.glBegin(GL.GL_TRIANGLES);
        {

            for (int vertexIndex = 0; vertexIndex + 5 < pumpkinVertices.length; vertexIndex += 6) {
                float x = pumpkinVertices[vertexIndex] + xCoor;
                float y = pumpkinVertices[vertexIndex + 1] + yCoor;
                float z = pumpkinVertices[vertexIndex + 2] + zCoor;

                float nx = pumpkinVertices[vertexIndex + 3];
                float ny = pumpkinVertices[vertexIndex + 4];
                float nz = pumpkinVertices[vertexIndex + 5];

                gl.glNormal3f(nx, ny, nz);
                gl.glVertex3f(x, y, z);
            }

        }
        gl.glEnd();
        // END: definition of scene content
    }

    private float[] scaledObj (float factor, float[] objScaled) {

        for (int i=0; i < objScaled.length / 3; i++) {
            objScaled[i*3] = factor * objScaled[i*3];
            objScaled[i*3+1] = factor * objScaled[i*3+1];
            objScaled[i*3+2] = factor * objScaled[i*3+2];
        }

        return objScaled;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("Reshape called.");
        System.out.println("x = " + x + ", y = " + y + ", width = " + width + ", height = " + height);

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width / (float) height, 0.01f, 10000f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Deleting allocated objects, incl. shader program.");
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgramLab.deleteShaderProgram();
        shaderProgramTorches.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);

        System.exit(0);
    }
}
