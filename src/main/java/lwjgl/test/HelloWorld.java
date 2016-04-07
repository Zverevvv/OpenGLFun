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

    private Cube3D floorCube = new Cube3D(CUBE_SIZE * 20);

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
            final float Z_PROJECTION = 300; // change to increase Z projection distance

            glOrtho(-30, 30, -30, 30, -Z_PROJECTION, Z_PROJECTION);
            glRotatef(1.0f, 1.0f, 0.0f, 0.0f);

            // Switching to model view
            glMatrixMode(GL_MODELVIEW);

            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);


            // Rotation controls (TOUCH CAREFULLY!)
            final float ROTATION_STEP = 1f;

            // HINT:
            // 1. To rotate main cube - arrows
            // 2. To rotate second cube - SHIFT + arrows
            // 3. To rotate third cube - CTRL + arrows
            if (glfwGetKey(window, GLFW_KEY_UP) == 1) {
                if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == 1) {
                    secondCube
                            .setStretchX(secondCube.getStretchX() + ROTATION_STEP)
                            .setxPos(secondCube.getxPos() + ROTATION_STEP);

                    thirdCube
                            .setxPos(thirdCube.getxPos() + ROTATION_STEP * 2);
                } else if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == 1) {
                    float thirdCubeSize = thirdCube.getSize() * thirdCube.getStretchY();
                    float allowedSize = (firstCube.getSize() * firstCube.getStretchY()) - secondCube.getSize();

                    if (thirdCubeSize < allowedSize) {
                        thirdCube
                                .setStretchY(thirdCube.getStretchY() + ROTATION_STEP)
                                .setyPos(thirdCube.getyPos() - ROTATION_STEP);
                    }
                } else {
                    firstCube
                            .setStretchY(firstCube.getStretchY() + ROTATION_STEP)
                            .setyPos(firstCube.getyPos() + ROTATION_STEP);

                    float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                    float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                    secondCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setyPos(secondCube.getyPos() + ROTATION_STEP * 2);

                    thirdCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setyPos(thirdCube.getyPos() + ROTATION_STEP * 2);
                }
            } else if (glfwGetKey(window, GLFW_KEY_DOWN) == 1) {
                if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == 1) {
                    if (secondCube.getStretchX() >= 2) {
                        secondCube
                                .setStretchX(secondCube.getStretchX() - ROTATION_STEP)
                                .setxPos(secondCube.getxPos() - ROTATION_STEP);

                        thirdCube
                                .setxPos(thirdCube.getxPos() - ROTATION_STEP * 2);
                    }
                } else if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == 1) {
                    if (thirdCube.getStretchY() >= 2) {
                        thirdCube
                                .setStretchY(thirdCube.getStretchY() - ROTATION_STEP)
                                .setyPos(thirdCube.getyPos() + ROTATION_STEP);
                    }
                } else {
                    if (firstCube.getStretchY() >= secondCube.getSize() / 1.5 + thirdCube.getStretchY()) {
                        firstCube
                                .setStretchY(firstCube.getStretchY() - ROTATION_STEP)
                                .setyPos(firstCube.getyPos() - ROTATION_STEP);

                        float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                        float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                        secondCube
                                .setRotationPoint(0.0f, rotPointY, 0.0f)
                                .setyPos(secondCube.getyPos() - ROTATION_STEP * 2);

                        thirdCube
                                .setRotationPoint(0.0f, rotPointY, 0.0f)
                                .setyPos(thirdCube.getyPos() - ROTATION_STEP * 2);
                    }
                }
            } else if (glfwGetKey(window, GLFW_KEY_RIGHT) == 1) {
                if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == 1) {
                    float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                    float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                    secondCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateX(secondCube.getRotateX() + ROTATION_STEP);

                    thirdCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateX(thirdCube.getRotateX() + ROTATION_STEP);
                } else if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == 1) {
                    float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                    float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                    thirdCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setAddRotateY(thirdCube.getAddRotateY() + ROTATION_STEP);
                } else {
                    firstCube
                            .setRotateY(firstCube.getRotateY() + ROTATION_STEP);

                    float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                    float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                    secondCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateY(secondCube.getRotateY() + ROTATION_STEP);

                    thirdCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateY(thirdCube.getRotateY() + ROTATION_STEP);
                }
            } else if (glfwGetKey(window, GLFW_KEY_LEFT) == 1) {
                if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == 1) {
                    float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                    float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                    secondCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateX(secondCube.getRotateX() - ROTATION_STEP);

                    thirdCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateX(thirdCube.getRotateX() - ROTATION_STEP);
                } else if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == 1) {
                    float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                    float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                    thirdCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setAddRotateY(thirdCube.getAddRotateY() - ROTATION_STEP);
                } else {
                    firstCube
                            .setRotateY(firstCube.getRotateY() - ROTATION_STEP);

                    float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
                    float rotPointY = firstCube.getyPos() + halfCubeDist - (firstCube.getSize() / 2);

                    secondCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateY(secondCube.getRotateY() - ROTATION_STEP);

                    thirdCube
                            .setRotationPoint(0.0f, rotPointY, 0.0f)
                            .setRotateY(thirdCube.getRotateY() - ROTATION_STEP);
                }
            }


            firstCube.render();
            secondCube.render();
            thirdCube.render();

            float halfCubeDist = (firstCube.getSize() * firstCube.getStretchY()) / 2;
            float rotPointY = firstCube.getyPos() - halfCubeDist;

            floorCube
                    .setyPos(rotPointY * 3.5f)
                    .setStretchX(2000)
                    .setStretchZ(2000);

            floorCube.render();

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
        private float rotateX = 0.0f, rotateY = 0.0f, rotateZ = 0.0f, aRotateY = 0.0f;
        private float rotPointX = 0.0f, rotPointY = 0.0f, rotPointZ = 0.0f;
        private float xPos = 0.0f, yPos = 0.0f, zPos = 0.0f;
        private boolean rndColorize = true;

        public Cube3D() {
        }

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

        public Cube3D setRotationPoint(float x, float y, float z) {
            this.rotPointX = x;
            this.rotPointY = y;
            this.rotPointZ = z;
            return this;
        }

        public Cube3D setAddRotateY(float aRotPointY) {
            this.aRotateY = aRotPointY;
            return this;
        }

        public float getAddRotateY() {
            return this.aRotateY;
        }

        public void render() {
            // Saving current matrix state
            glPushMatrix();

            // Applying rotation transformation
            glTranslatef(this.rotPointX, this.rotPointY, this.rotPointZ);

            glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);
            glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
            glRotatef(rotateX, 1.0f, 0.0f, 0.0f);

            glTranslatef(-this.rotPointX, -this.rotPointY, -this.rotPointZ);

            // Additional rotation
            glTranslatef(this.xPos, this.yPos, this.zPos);

            glRotatef(aRotateY, 0.0f, 1.0f, 0.0f);

            glTranslatef(-this.xPos, -this.yPos, -this.zPos);

            // Initializing quadratic mode
            glBegin(GL_QUADS);

            // Preparing coordinates (just to simplify code below)
            final float halfSizeX = (size * stretchX) / 2;
            final float halfSizeY = (size * stretchY) / 2;
            final float halfSizeZ = (size * stretchZ) / 2;

            // Front face
            if (rndColorize) glColor3f(1f, 0f, 0f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, -halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, halfSizeY + yPos, halfSizeZ + zPos);

            // Back face
            if (rndColorize) glColor3f(0f, 1f, 0f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);

            // Top face
            if (rndColorize) glColor3f(0f, 0f, 1f);
            glVertex3f(-halfSizeX + xPos, halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, halfSizeY + yPos, -halfSizeZ + zPos);

            // Bottom face
            if (rndColorize) glColor3f(0f, 0f, 1f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, -halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, halfSizeZ + zPos);

            // Right face
            if (rndColorize) glColor3f(1f, 0f, 1f);
            glVertex3f(halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(halfSizeX + xPos, -halfSizeY + yPos, halfSizeZ + zPos);

            // Left face
            if (rndColorize) glColor3f(0f, 1f, 1f);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, -halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, -halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, halfSizeY + yPos, halfSizeZ + zPos);
            glVertex3f(-halfSizeX + xPos, halfSizeY + yPos, -halfSizeZ + zPos);

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