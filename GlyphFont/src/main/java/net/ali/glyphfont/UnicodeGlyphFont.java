package net.ali.glyphfont;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class UnicodeGlyphFont {

    private final Font font;
    private final boolean antiAlias;
    private final Map<Integer, GlyphPage> GLYPH_REGISTRY = new HashMap<>();
    private static final int IMG_SIZE = 1024;
    private final float MARGIN;

    /**
     * Construct a glyph font with specified font
     * Defaults to use antialiasing and defaults to initialise a cache with ASCII characters
     *
     * @param font the font to use
     */
    public UnicodeGlyphFont(Font font) {
        this(font, true);
    }

    /**
     * Construct a glyph font with specified font and specified anti-aliasing preferences
     * Defaults to initialise a cache with ASCII characters
     *
     * @param font      the font to use
     * @param antiAlias the anti-aliasing preference
     */
    public UnicodeGlyphFont(Font font, boolean antiAlias) {
        this(font, antiAlias, 0);
    }

    /**
     * Construct a glyph font with specified font, specified anti-aliasing preferences and specified
     * initial glyph pages to cache
     *
     * @param font         the font to use
     * @param antiAlias    the anti-aliasing preference
     * @param initialCache the initial glyph pages to cache
     */
    public UnicodeGlyphFont(Font font, boolean antiAlias, int... initialCache) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.MARGIN = (float) Math.floor(font.getSize() / 5f);

        for (int id : initialCache)
            setupGlyph(id);
    }

    /**
     * Draws a specified string to the screen with the specified x, y co-ordinates and colour
     *
     * @param text   the text to draw
     * @param x      the x co-ordinate
     * @param y      the y co-ordinate
     * @param colour the colour
     * @return the width of the string drawn
     */
    public float drawString(String text, float x, float y, int colour) {
        glPushMatrix();
        boolean blend = glIsEnabled(GL_BLEND);
        boolean lighting = glIsEnabled(GL_LIGHTING);
        boolean texture = glIsEnabled(GL_TEXTURE_2D);
        boolean alpha = glIsEnabled(GL_ALPHA_TEST);
        if (!texture)
            glEnable(GL_TEXTURE_2D);
        if (!blend)
            glEnable(GL_BLEND);
        if (lighting)
            glDisable(GL_LIGHTING);
        if (!alpha)
            glEnable(GL_ALPHA_TEST);
        RenderUtil.glColour(colour);
        float width = 0;
        for (char c : text.toCharArray()) {
            float size = drawCharacter(c, x, y);
            x += size;
            width += size;
        }
        if (!texture)
            glDisable(GL_TEXTURE_2D);
        if (!blend)
            glDisable(GL_BLEND);
        if (lighting)
            glEnable(GL_LIGHTING);
        if (!alpha)
            glDisable(GL_ALPHA_TEST);
        glPopMatrix();
        return width;
    }

    /**
     * Draws a specified character to the screen with the specified x, y co-ordinates and colour
     *
     * @param character the character to draw
     * @param x         the x co-ordinate
     * @param y         the y co-ordinate
     * @param colour    the colour
     * @return the width of the character drawn
     */
    public float drawCharacter(char character, float x, float y, int colour) {
        RenderUtil.glColour(colour);
        return drawCharacter(character, x, y);
    }

    /**
     * Draws a specified character's glyph page at specified x, y co-ordinates.
     * This method is mainly intended for testing purposes
     *
     * @param c      the character of glyph page
     * @param x      the x co-ordinate
     * @param y      the y co-ordinate
     * @param colour the colour
     */
    public void drawGlyph(char c, float x, float y, int colour) {
        GlyphPage glyphPage = getGlyphPage(c);
        glPushMatrix();
        boolean blend = glIsEnabled(GL_BLEND);
        boolean lighting = glIsEnabled(GL_LIGHTING);
        boolean texture = glIsEnabled(GL_TEXTURE_2D);
        if (!texture)
            glEnable(GL_TEXTURE_2D);
        if (!blend)
            glEnable(GL_BLEND);
        if (lighting)
            glDisable(GL_LIGHTING);
        RenderUtil.glColour(colour);
        int textureId = glGetInteger(GL_TEXTURE_2D);
        if (textureId != glyphPage.getTexture().getTextureID())
            glBindTexture(GL_TEXTURE_2D, glyphPage.getTexture().getTextureID());
        RenderUtil.drawTextureQuad(x, y, IMG_SIZE, IMG_SIZE, 0, 0, 1, 1);
        if (textureId != glyphPage.getTexture().getTextureID())
            glBindTexture(GL_TEXTURE_2D, textureId);
        if (!texture)
            glDisable(GL_TEXTURE_2D);
        if (!blend)
            glDisable(GL_BLEND);
        if (lighting)
            glEnable(GL_LIGHTING);
        glPopMatrix();
    }

    /**
     * Returns the width of the specified string if drawn to the screen
     *
     * @param text the text
     * @return the width of the specified text
     */
    public float getStringWidth(String text) {
        return (float) text.chars().mapToDouble(c -> getCharacterWidth((char) c)).sum();
    }

    /**
     * Returns the width of the specified character if drawn to the screen
     *
     * @param c the character
     * @return the width of the specified character
     */
    public float getCharacterWidth(char c) {
        return (float) getGlyphPage(c).getCharacterData(c).getWidth();
    }

    /**
     * Returns the height of the specified string if drawn to the screen
     *
     * @param text the text
     * @return the height of the specified text
     */
    public float getStringHeight(String text) {
        return (float) text.chars().mapToDouble(c -> getCharacterHeight((char) c)).max().orElse(0);
    }

    /**
     * Returns the height of the specified character if drawn to the screen
     *
     * @param c the character
     * @return the height of the specified character
     */
    public float getCharacterHeight(char c) {
        return (float) getGlyphPage(c).getCharacterData(c).getHeight();
    }

    /**
     * Returns the max-height of the standard ascii characters if drawn to the screen
     *
     * @return the max-height of standard ascii characters
     */
    public float getMaxHeight() {
        return getMaxHeight('a');
    }

    /**
     * Returns the max-height of the specified character's glyph page if drawn to the screen
     *
     * @param c the character
     * @return the max-height of the specified character's glyph page
     */
    public float getMaxHeight(char c) {
        return (float) getGlyphPage(c).getMaxHeight();
    }

    private float drawCharacter(char character, float x, float y) {
        GlyphPage glyphPage = getGlyphPage(character);
        CharacterData characterData = glyphPage.getCharacterData(character);
        int textureId = glGetInteger(GL_TEXTURE_2D);
        if (textureId != glyphPage.getTexture().getTextureID())
            glBindTexture(GL_TEXTURE_2D, glyphPage.getTexture().getTextureID());
        double width = characterData.getWidth();
        double height = characterData.getHeight();
        double textureX = characterData.getX() / IMG_SIZE;
        double textureY = characterData.getY() / IMG_SIZE;
        double textureWidth = width / IMG_SIZE;
        double textureHeight = height / IMG_SIZE;
        RenderUtil.drawTextureQuad(x, y, width, height, textureX, textureY, textureWidth, textureHeight);
        if (textureId != glyphPage.getTexture().getTextureID())
            glBindTexture(GL_TEXTURE_2D, textureId);
        return (float) width;
    }

    private void setupGlyph(int id) {
        Map<Integer, CharacterData> charData = new HashMap<>();
        BufferedImage bufferedImage = new BufferedImage(IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = setupGraphics(bufferedImage);
        if (id > 0) graphics2D.setFont(new Font("DEFAULT", font.getStyle(), font.getSize()));
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        float x = MARGIN;
        float y = MARGIN;
        double maxHeight = 0;
        for (int i = id * 256; i < (id + 1) * 256; i++) {
            String character = String.valueOf((char) i);
            Rectangle2D dimensions = fontMetrics.getStringBounds(character, graphics2D);
            double width = dimensions.getWidth();
            double height = dimensions.getHeight();
            if (x + width > IMG_SIZE) {
                x = MARGIN;
                y += maxHeight + MARGIN;
                maxHeight = 0;
            }
            if (height > maxHeight)
                maxHeight = height;
            graphics2D.drawString(character, x, y + fontMetrics.getAscent());
            charData.put(i, new CharacterData(x, y, width, height));
            x += width + MARGIN;
        }
        FontTexture texture = new FontTexture(bufferedImage);
        GLYPH_REGISTRY.put(id, new GlyphPage(texture, maxHeight, charData));
        graphics2D.dispose();
    }

    private int getGlyphID(char c) {
        return c >> 8 & 0xFF;
    }

    private GlyphPage getGlyphPage(char c) {
        int glyphID = getGlyphID(c);
        if (!GLYPH_REGISTRY.containsKey(glyphID))
            setupGlyph(glyphID);
        return GLYPH_REGISTRY.get(glyphID);
    }

    private Graphics2D setupGraphics(BufferedImage bufferedImage) {
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(new Color(255, 255, 255, 0));
        graphics2D.fill(new Rectangle(0, 0, IMG_SIZE, IMG_SIZE));
        graphics2D.setColor(Color.WHITE);
        graphics2D.setFont(font);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return graphics2D;
    }

}
