
import net.ali.glyphfont.RenderUtil;
import net.ali.glyphfont.UnicodeGlyphFont;
import org.lwjgl.glfw.GLFWErrorCallback;

import org.lwjgl.opengl.*;

import java.awt.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class FontTest {

    private long window;
    private UnicodeGlyphFont timesNewRoman;
    private UnicodeGlyphFont segoe;
    private UnicodeGlyphFont calibri;
    private UnicodeGlyphFont arial;

    public static void main(String[] args) {
        new FontTest().run();
    }

    private void initFonts() {
        timesNewRoman = new UnicodeGlyphFont(new Font("Times New Roman", Font.PLAIN, 30), true);
        segoe = new UnicodeGlyphFont(new Font("Segoe UI", Font.PLAIN, 25), true);
        calibri = new UnicodeGlyphFont(new Font("Calibri", Font.ITALIC, 25), true);
        arial = new UnicodeGlyphFont(new Font("Arial", Font.BOLD, 25), true);
    }

    private void drawText() {
        float width = timesNewRoman.getStringWidth("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz");
        float height = timesNewRoman.getStringHeight("הפרויקט שלישלום, תודה שבדקת את") + 320;
        RenderUtil.glColour(0xFF000000);
        glBegin(GL_QUADS);
        glVertex2d(20, 80);
        glVertex2d(20, height);
        glVertex2d(20 + width, height);
        glVertex2d(20 + width, 80);
        glEnd();
        arial.drawString("The Quick Brown Fox Jumped Over The Lazy Dog", 20, 80, Color.HSBtoRGB(0f, 1, 1));
        calibri.drawString("The Quick Brown Fox Jumped Over The Lazy Dog", 20, 120, Color.HSBtoRGB(0f, 1, 1));
        segoe.drawString("The Quick Brown Fox Jumped Over The Lazy Dog", 20, 160, Color.HSBtoRGB(0f, 1, 1));
        timesNewRoman.drawString("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz", 20, 200, Color.HSBtoRGB(0.1f, 1, 1));
        timesNewRoman.drawString("Здравствуйте, спасибо, что ознакомились с моим проектом", 20, 240, Color.HSBtoRGB(0.2f, 1, 1));
        timesNewRoman.drawString("您好，谢谢您检查我的项目", 20, 280, Color.HSBtoRGB(0.3f, 1, 1));
        timesNewRoman.drawString("הפרויקט שלישלום, תודה שבדקת את", 20, 320, Color.HSBtoRGB(0.4f, 1, 1));
    }

    private void run() {
        init();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        window = glfwCreateWindow(1024, 1024, "Test Font Rendering", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 1024, 1024, 0, -1, 0);
        glMatrixMode(GL_MODELVIEW);
        initFonts();
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glClearColor(1f, 1f, 1f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);
            drawText();
            glfwSwapBuffers(window);
        }
    }
}
