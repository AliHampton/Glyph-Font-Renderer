package net.ali.glyphfont;

import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL45;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    public static void glColour(int hex) {
        float red = (hex >> 16 & 0xFF) / 255f;
        float green = (hex >> 8 & 0xFF) / 255f;
        float blue = (hex & 0xFF) / 255f;
        float alpha = (hex >> 24 & 0xFF) / 255f;
        glColor4f(red, green, blue, alpha);
    }

    public static int uploadTexture(BufferedImage image) {
        int[] textureData = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), textureData, 0, image.getWidth());
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                int pixel = textureData[j * image.getWidth() + i];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();
        int textureID = glGenTextures();
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        GL14.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        GL45.glGenerateTextureMipmap(GL_TEXTURE_2D);
        return textureID;
    }
}
