package asciipanel;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class holds information about a loaded font, that is the glyph collection
 * and the width and height in pixels of glyphs.
 * 
 * @author Lorenzo Bianchi
 */
public class AsciiFont {
	
	/**
	 * The CP437_8x8 font
	 */
	public static final AsciiFont CP437_8x8 = loadStaticResource("cp437_8x8.png", 8, 8);
	
	/**
	 * The CP437_10x10 font
	 */
	public static final AsciiFont CP437_10x10 = loadStaticResource("cp437_10x10.png", 10, 10);
	
	/**
	 * The CP437_12x12 font
	 */
	public static final AsciiFont CP437_12x12 = loadStaticResource("cp437_12x12.png", 12, 12);
	
	/**
	 * The CP437_16x16 font
	 */
	public static final AsciiFont CP437_16x16 = loadStaticResource("cp437_16x16.png", 16, 16);
	
	/**
	 * The CP437_9x16 font
	 */
	public static final AsciiFont CP437_9x16 = loadStaticResource("cp437_9x16.png", 9, 16);
	
	/**
	 * The DRAKE_10x10 font
	 */
	public static final AsciiFont DRAKE_10x10 = loadStaticResource("drake_10x10.png", 10, 10);
	
	/**
	 * The TAFFER_10x10 font
	 */
	public static final AsciiFont TAFFER_10x10 = loadStaticResource("taffer_10x10.png", 10, 10);
	
	/**
	 * The QBICFEET_10x10 font
	 */
	public static final AsciiFont QBICFEET_10x10 = loadStaticResource("qbicfeet_10x10.png", 10, 10);
	
	/**
	 * The TALRYTH_15_15 font
	 */
	public static final AsciiFont TALRYTH_15_15 = loadStaticResource("talryth_square_15x15.png", 15, 15);
	
	private BufferedImage[] glyphs;

	/**
	 * Gets font glyph width in pixels.
	 * 
	 * @return font glyph width in pixels
	 */
	public int getWidth() {
		return glyphs[0].getWidth();
	}

	/**
	 * Gets font glyph height in pixels.
	 * 
	 * @return font glyph height in pixels
	 */
	public int getHeight() {
		return glyphs[0].getHeight();
	}
	
	/**
	 * Returns the font glyph corresponding to the given ASCII character code.
	 * 
	 * @param i an ASCII character code between 0 and 255
	 * @return a BufferedImage containing a font glyph
	 */
	public BufferedImage getGlyph(int i) {
		return glyphs[i];
	}

	/**
	 * Class constructor
	 * 
	 * @param glyphSprite image containing a 16x16 grid of glyphs
	 * @param glyphWidth  glyph width in pixels
	 * @param glyphHeight glyph height in pixels
	 */
	public AsciiFont(BufferedImage glyphSprite, int glyphWidth, int glyphHeight) {
		if (glyphSprite.getWidth() < glyphWidth*16 || glyphSprite.getHeight() < glyphHeight*16)
			throw new IllegalArgumentException("glyph sprite image is too small for specified glyph width and height");
		
		glyphs = new BufferedImage[256];
		
		for (int y = 0; y < 16; ++y) {
			for (int x = 0; x < 16; ++x) {
				int i = 16*y + x;
				int sx = x*glyphWidth;
				int sy = y*glyphHeight;
				
				glyphs[i] = new BufferedImage(glyphWidth, glyphHeight, BufferedImage.TYPE_INT_ARGB);
				glyphs[i].getGraphics().drawImage(glyphSprite, 0, 0, glyphWidth, glyphHeight,
						sx, sy, sx + glyphWidth, sy + glyphHeight, null);
			}
		}
	}
	
	/**
	 * Builds a AsciiFont instance from the specified sprite resource
	 * 
	 * @param path        the path of the sprite image resource to load
	 * @param glyphWidth  glyph width in pixels
	 * @param glyphHeight glyph height in pixels
	 * @return initialized AsciiFont instance
	 * @throws java.io.IOException thrown if an error occurs while loading the resource
	 */
	public static AsciiFont fromResource(String path, int glyphWidth, int glyphHeight) throws IOException {
		BufferedImage glyphSprite = ImageIO.read(AsciiFont.class.getClassLoader().getResource(path));
		return new AsciiFont(glyphSprite, glyphWidth, glyphHeight);
	}
	
	private static AsciiFont loadStaticResource(String path, int glyphWidth, int glyphHeight) {
		try { return fromResource(path, glyphWidth, glyphHeight);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
