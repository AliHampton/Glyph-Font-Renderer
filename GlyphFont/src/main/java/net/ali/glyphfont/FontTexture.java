package net.ali.glyphfont;

import java.awt.image.BufferedImage;

public class FontTexture {

    private final int textureID;

    public FontTexture(BufferedImage image) {
        textureID = RenderUtil.uploadTexture(image);
    }

    public int getTextureID() {
        return textureID;
    }
}
