package imageeditor;

import asciipanel.AsciiPainter;
import asciipanel.AsciiRaster;
import asciipanel.LinearBrush;

/**
 * This implementation of the AsciiBrush interface draws a line by writing a
 * specific character at each point of the line, with the active background
 * and foreground color of the painter that is used to draw.
 * 
 * @author Lorenzo Bianchi
 */
public class CharacterBrush extends LinearBrush {
	
	private char character;
	
	/**
	 * Initializes an istance of the character brush that draws
	 * a line using the given character.
	 * 
	 * @param character the character to use when drawing the line
	 */
	public CharacterBrush(char character) {
		this.character = character;
	}

	/**
	 * Writes the character specified at construction at the point specified
	 * by arguments, using foreground and background colors of the provided painter.
	 * 
	 * @param painter the painter whose surface the implementation must draw into
	 * @param x		  horizontal coordinate of a point on the line
	 * @param y		  vertical coordinate of a point on the line
	 */
	@Override
	protected void dot(AsciiPainter painter, int x, int y) {
		AsciiRaster surface = painter.getSurface();
		surface.getChars()[x][y] = character;
		surface.getForecolors()[x][y] = painter.getForegroundColor();
		surface.getBackcolors()[x][y] = painter.getBackgroundColor();
	}

}
