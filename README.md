# Glyph Font Renderer
 OpenGL Unicode Glyph Font Renderer. Utilises smart on-demand caching of characters in "Glyph Pages".

 <img src="https://image.prntscr.com/image/1SHUfWvcRsy71aTEKb52kA.png">


### Example usage:
Creating a UnicodeGlyphFont object is simple. Keep in mind everytime you initialise a Glyph it creates a new cache so for best performance create a certain UnicodeGlyphFont object once.

 ```java
 private static final UnicodeGlyphFont font = new UnicodeGlyphFont(new Font("Calibri", Font.PLAIN, 30));
 ```

 Then to render a string with the font it is also very simple. Just make sure you call drawString on each 2D frame.
 ```java
font.drawString("Hello World!", 5, 5, -1);
```

### TODO:

- Change from GL immediate mode to a shader based approach
- Add a signed distance field option for rendering
