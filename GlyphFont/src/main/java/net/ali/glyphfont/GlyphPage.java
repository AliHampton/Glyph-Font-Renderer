package net.ali.glyphfont;

import java.util.Map;

public class GlyphPage {

    private final FontTexture texture;
    private final int maxHeight;
    private final Map<Integer, CharacterData> characterRegistry;

    public GlyphPage(FontTexture texture, int maxHeight, Map<Integer, CharacterData> characterRegistry) {
        this.texture = texture;
        this.maxHeight = maxHeight;
        this.characterRegistry = characterRegistry;
    }

    public FontTexture getTexture() {
        return texture;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public CharacterData getCharacterData(char c) {
        return characterRegistry.get((int) c);
    }
}
