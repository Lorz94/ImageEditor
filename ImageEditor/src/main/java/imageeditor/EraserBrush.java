package imageeditor;

import asciipanel.AsciiPainter;
import asciipanel.LinearBrush;

/**
 * This implementation of the AsciiBrush interface erases a line by replacing the
 * character at each point of the line with a space, while preserving foreground and
 * background colors.
 * 
 * @author Lorenzo Bianchi
 */
public class EraserBrush extends LinearBrush {

	/**
	 * Replaces the character at the specified point with a space.
	 * 
	 * @param painter the painter whose surface the implementation must draw into
	 * @param x		  horizontal coordinate of a point on the line
	 * @param y		  vertical coordinate of a point on the line
	 */
	@Override
	protected void dot(AsciiPainter painter, int x, int y) {
		painter.getSurface().getChars()[x][y] = ' ';
	}

}
