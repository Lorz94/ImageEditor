package asciipanel;

/**
 * Instances of AsciiBrush implement a strategy for drawing lines into an AsciiRaster surface.
 * This interface is used as a strategy pattern by AsciiPainter to implement line drawing functions.
 * 
 * @author Lorenzo Bianchi
 *
 */
public interface AsciiBrush {
	/**
	 * Implementations must draw a line into the painter's surface from the point (fromX, fromY)
	 * to the point (toX, toY).
	 * 
	 * @param painter the painter whose surface the implementation must draw into
	 * @param fromX   horizontal coordinate of the starting point
	 * @param fromY   vertical coordinate of the starting point
	 * @param toX	  horizontal coordinate of the final point
	 * @param toY	  vertical coordinate of the final point
	 */
	void stroke(AsciiPainter painter, int fromX, int fromY, int toX, int toY);
}
