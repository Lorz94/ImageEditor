package asciipanel;

/**
 * A useful abstract class that simplifies the implementation of many kind of brushes:
 * specifically those that work by executing an operation for each point on the line.
 * 
 * The stroke method uses Bresenham's line algorithm to iterate over the points
 * of the line, and calls the abstract protected method dot for each point. Subclasses
 * must implement the abstract method to provide a strategy for drawing points.
 * 
 * @author Lorenzo Bianchi
 */
public abstract class LinearBrush implements AsciiBrush {

	/**
	 * Draws a line by applying the abstract method dot to each point on the line.
	 * 
	 * @param painter the painter whose surface the implementation must draw into
	 * @param fromX   horizontal coordinate of the starting point
	 * @param fromY   vertical coordinate of the starting point
	 * @param toX	  horizontal coordinate of the final point
	 * @param toY	  vertical coordinate of the final point
	 */
	@Override
	public void stroke(AsciiPainter painter, int fromX, int fromY, int toX, int toY) {
		// Bresenham's line algorithm, from Wikipedia
		int x = fromX, y = fromY;
		
		int dx =  Math.abs(toX - fromX);
	    int dy = -Math.abs(toY - fromY);
	    int sx = (fromX < toX) ? 1 : -1;
	    int sy = (fromY < toY) ? 1 : -1;

	    int err = dx + dy;

	    while (true) {
	    	dot(painter, x, y);
	    	
	    	if (x == toX && y == toY)
	    		break;

	    	int e2 = 2*err;
	    	
	    	if (e2 >= dy) {
	    	    err += dy;
	    	    x += sx;
	    	}

	    	if (e2 <= dx) {
	    	    err += dx;
	    	    y += sy;
	    	}
	    }
	}
	
	/**
	 * Subclasses must implement this method to draw each point on the line.
	 * 
	 * @param painter the painter whose surface the implementation must draw into
	 * @param x		  horizontal coordinate of a point on the line
	 * @param y		  vertical coordinate of a point on the line
	 */
	protected abstract void dot(AsciiPainter painter, int x, int y);

}
