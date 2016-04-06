package lwjgl.test;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.libffi.Closure;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HelloWorld {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWWindowSizeCallback wsCallback;
    private Closure debugProc;
    private float rotation = 0.0f;
    // Creating cubes
    private final float CUBE_SIZE = 2.0f;

    private Cube3D firstCube = new Cube3D(CUBE_SIZE);
    private Cube3D secondCube = new Cube3D(CUBE_SIZE);
    private Cube3D thirdCube = new Cube3D(CUBE_SIZE);

    // The window handle
    private long window;
    private int width, height;

    public void run() {
        try {
            init();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
            wsCallback.release();
            //keyCallback.free();
            //wsCallback.free();
            if (debugProc != null)
                debugProc.release();
            //debugProc.free();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
            //errorCallback.free();
        }
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL11.GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwPollEvents();

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        int WIDTH = 600;
        int HEIGHT = 600;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
            }
        });
        glfwSetWindowSizeCallback(window, wsCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0) {
                    width = w;
                    height = h;
                }
            }
        });

        // My custom key callbacks
        // First cube key callbacks
        /*glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_UP && action == GLFW_PRESS) {
                    System.out.println("Up key was pressed.");
                    firstCube
                            .setStretchY(firstCube.getStretchY() + 0.5f)
                            .setyPos(firstCube.getyPos() + 1.0f).render();
                    System.out.println(firstCube.getStretchY() + ": " + firstCube.getyPos());
                }
            }
        });*/

        firstCube.setStretchY(8.0f);

        secondCube
                .setStretchX(4.0f)
                .setyPos(firstCube.getStretchY() - firstCube.getSize() / 2)
                .setxPos(secondCube.getStretchX() + secondCube.getSize() / 2);

        thirdCube
                .setStretchY(4.0f)
                .setxPos(secondCube.getxPos() + secondCube.getStretchX() - secondCube.getSize() / 2)
                .setyPos(secondCube.getyPos() - thirdCube.getStretchY() - secondCube.getSize() / 2);

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glViewport(0, 0, width, height);

            // Place for my code

            // Orthogonal projection (useful for getting our model properly visible)
            glMatrixMode(GL_PROJECTION);

            // Do not touch. This resets The Current Modelview Matrix
            glLoadIdentity();

            // Setting up the actual projection position
            glOrtho(-20, 20, -20, 20, -20, 20);

            // Switching to model view
            glMatrixMode(GL_MODELVIEW);

            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);

//            firstCube
//                    .setStretchY(8.0f)
//                    //.setRotateY(rotation++) //simple rotation
//                    .render();
//
//            secondCube
//                    .setStretchX(4.0f)
//                    .setyPos(firstCube.getStretchY() - firstCube.getSize() / 2)
//                    .setxPos(secondCube.getStretchX() + secondCube.getSize() / 2)
//                    .setRotationPoint(0.0f, firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
//                    //.setRotateY(rotation) // rotation around first cube
//                    //.setRotateX(rotation) // rotation around itself
//                    .render();
//
//            thirdCube
//                    .setStretchY(4.0f)
//                    .setxPos(secondCube.getxPos() + secondCube.getStretchX() - secondCube.getSize() / 2)
//                    .setyPos(secondCube.getyPos() - thirdCube.getStretchY() - secondCube.getSize() / 2)
//                    //.setRotationPoint(0.0f, firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f) // rotation around first cube
//                    //.setRotateY(rotation) // rotation around first cube
//                    .setRotationPoint(thirdCube.getxPos(), firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
//                    //.setRotateY(rotation) // rotation around itself
//                    //.setRotateX(rotation) // rotation around second cube
//                    .render();

            // Key press handling for first cube

            if (glfwGetKey(window, GLFW_KEY_F2) == 1) {
                firstCube
                        .setStretchY(firstCube.getStretchY() + 0.2f)
                        .setyPos(firstCube.getyPos() + 0.2f);
                secondCube
                        .setyPos(secondCube.getyPos() + 0.4f);
                thirdCube
                        .setyPos(thirdCube.getyPos() + 0.4f);
            } else if (glfwGetKey(window, GLFW_KEY_F3) == 1) {
                // Can be reworked properly in according with the angle of second cube.
                if (firstCube.getStretchY() > secondCube.getSize() / 1.5 + thirdCube.getStretchY()) {
                    firstCube
                            .setStretchY(firstCube.getStretchY() - 0.2f)
                            .setyPos(firstCube.getyPos() - 0.2f);
                    secondCube
                            .setyPos(secondCube.getyPos() - 0.4f);
                    thirdCube
                            .setyPos(thirdCube.getyPos() - 0.4f);
                }
            } else if (glfwGetKey(window, GLFW_KEY_F4) == 1) {
                firstCube
                        .setRotateY(firstCube.getRotateY() + 2.0f);
                secondCube
                        .setRotationPoint(0.0f, firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
                        .setRotateY(secondCube.getRotateY() + 2.0f);
                thirdCube
                        .setRotationPoint(0.0f, firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
                        .setRotateY(thirdCube.getRotateY() + 2.0f);
            } else if (glfwGetKey(window, GLFW_KEY_F1) == 1) {
                firstCube
                        .setRotateY(firstCube.getRotateY() - 2.0f);
                secondCube
                        .setRotationPoint(0.0f, firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
                        .setRotateY(secondCube.getRotateY() - 2.0f);
                thirdCube
                        .setRotationPoint(0.0f, firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
                        .setRotateY(thirdCube.getRotateY() - 2.0f);
            }


            // Key press handling for second cube

            if (glfwGetKey(window, GLFW_KEY_F6) == 1) {
                secondCube
                        .setStretchX(secondCube.getStretchX() + 0.2f)
                        .setxPos(secondCube.getxPos() + 0.2f);
                thirdCube
                        .setxPos(thirdCube.getxPos() + 0.4f);
            } else if (glfwGetKey(window, GLFW_KEY_F7) == 1) {
                if (secondCube.getStretchX() > 1) {
                    secondCube
                            .setStretchX(secondCube.getStretchX() - 0.2f)
                            .setxPos(secondCube.getxPos() - 0.2f);
                    thirdCube
                            .setxPos(thirdCube.getxPos() - 0.4f);
                }
            } else if (glfwGetKey(window, GLFW_KEY_F8) == 1) {
                secondCube
                        .setRotationPoint(secondCube.getxPos(), firstCube.getStretchY() - firstCube.getSize() / 2, secondCube.getzPos())
                        .setRotateX(secondCube.getRotateX() + 2.0f);
                thirdCube
                        .setRotationPoint(thirdCube.getxPos(), firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
                        .setRotateX(thirdCube.getRotateX() + 2.0f);
            } else if (glfwGetKey(window, GLFW_KEY_F5) == 1) {
                secondCube
                        .setRotationPoint(secondCube.getxPos(), firstCube.getStretchY() - firstCube.getSize() / 2, secondCube.getzPos())
                        .setRotateX(secondCube.getRotateX() - 2.0f);
                thirdCube
                        .setRotationPoint(thirdCube.getxPos(), firstCube.getStretchY() - firstCube.getSize() / 2, 0.0f)
                        .setRotateX(thirdCube.getRotateX() - 2.0f);
            }

            // Key press handling for third cube

            if (glfwGetKey(window, GLFW_KEY_F10) == 1) {
                System.out.println("You pressed UP + LEFT SHIFT");
            } else if (glfwGetKey(window, GLFW_KEY_F11) == 1) {
                System.out.println("You pressed DOWN + LEFT SHIFT");
            } else if (glfwGetKey(window, GLFW_KEY_F12) == 1) {
                System.out.println("You pressed RIGHT + LEFT SHIFT");
            } else if (glfwGetKey(window, GLFW_KEY_F9) == 1) {
                System.out.println("You pressed LEFT + LEFT SHIFT");
            }

            firstCube.render();
            secondCube.render();
            thirdCube.render();

            // End of my code
            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private class Cube3D {
        private float size = 1.0f;
        private float stretchX = 1.0f, stretchY = 1.0f, stretchZ = 1.0f;
        private float rotateX = 0.0f, rotateY = 0.0f, rotateZ = 0.0f;
        private float rotPointX = 0.0f, rotPointY = 0.0f, rotPointZ = 0.0f;
        private float xPos = 0.0f, yPos = 0.0f, zPos = 0.0f;
        private boolean rndColorize = true;

        public Cube3D() {}

        public Cube3D(float size) {
            this.size = size;
        }

        public Cube3D(float size, float xPos, float yPos, float zPos) {
            this.size = size;
            this.xPos = xPos;
            this.yPos = yPos;
            this.zPos = zPos;
        }

        public Cube3D turnOffRndColors() {
            this.rndColorize = false;
            return this;
        }

        public Cube3D turnOnRndColors() {
            this.rndColorize = true;
            return this;
        }

        public float getSize() {
            return size;
        }

        public Cube3D setSize(float size) {
            this.size = size;
            return this;
        }

        public float getxPos() {
            return xPos;
        }

        public Cube3D setxPos(float xPos) {
            this.xPos = xPos;
            return this;
        }

        public float getyPos() {
            return yPos;
        }

        public Cube3D setyPos(float yPos) {
            this.yPos = yPos;
            return this;
        }

        public float getzPos() {
            return zPos;
        }

        public Cube3D setzPos(float zPos) {
            this.zPos = zPos;
            return this;
        }

        public float getStretchX() {
            return stretchX;
        }

        public Cube3D setStretchX(float stretchX) {
            this.stretchX = stretchX;
            return this;
        }

        public float getStretchY() {
            return stretchY;
        }

        public Cube3D setStretchY(float stretchY) {
            this.stretchY = stretchY;
            return this;
        }

        public float getStretchZ() {
            return stretchZ;
        }

        public Cube3D setStretchZ(float stretchZ) {
            this.stretchZ = stretchZ;
            return this;
        }

        public float getRotateX() {
            return rotateX;
        }

        public Cube3D setRotateX(float rotateX) {
            this.rotateX = rotateX;
            return this;
        }

        public float getRotateY() {
            return rotateY;
        }

        public Cube3D setRotateY(float rotateY) {
            this.rotateY = rotateY;
            return this;
        }

        public float getRotateZ() {
            return rotateZ;
        }

        public Cube3D setRotateZ(float rotateZ) {
            this.rotateZ = rotateZ;
            return this;
        }

        public Cube3D setRotationPoint(float x, float y, float z) {
            this.rotPointX = x;
            this.rotPointY = y;
            this.rotPointZ = z;
            return this;
        }

        public void render() {
            // Saving current matrix state
            glPushMatrix();

            // Applying rotation transformation
            //double dRotateX = rotateX;
            //double dRotateY = rotateY;
            //double dRotateZ = rotateZ;

            glTranslatef( this.rotPointX,  this.rotPointY,  this.rotPointZ);
            glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
            glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
            glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);
            glTranslatef(-this.rotPointX, -this.rotPointY, -this.rotPointZ);

            // Initializing quadratic mode
            glBegin(GL_QUADS);

            // Preparing coordinates (just to simplify code below)
            final float halfSizeX = (size * stretchX) / 2;
            final float halfSizeY = (size * stretchY) / 2;
            final float halfSizeZ = (size * stretchZ) / 2;

            // Front face
            if (rndColorize) glColor3f(1f, 0f, 0f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos, -halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos,  halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos,  halfSizeY + yPos,  halfSizeZ + zPos);

            // Back face
            if (rndColorize) glColor3f(0f, 1f, 0f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos,  halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos,  halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);

            // Top face
            if (rndColorize) glColor3f(0f, 0f, 1f);
            glVertex3f(-halfSizeX + xPos,  halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos,  halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos,  halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos,  halfSizeY + yPos, -halfSizeZ + zPos);

            // Bottom face
            if (rndColorize) glColor3f(0f, 0f, 1f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos, -halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos,  halfSizeZ + zPos);

            // Right face
            if (rndColorize) glColor3f(1f, 0f, 1f);
            glVertex3f( halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos,  halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos,  halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f( halfSizeX + xPos, -halfSizeY + yPos,  halfSizeZ + zPos);

            // Left face
            if (rndColorize) glColor3f(0f, 1f, 1f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos,  halfSizeY + yPos,  halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos,  halfSizeY + yPos, -halfSizeZ + zPos);

            // We have ended drawing up cube sides
            glEnd();

            // Saving our figure
            glPopMatrix();
        }
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }

}