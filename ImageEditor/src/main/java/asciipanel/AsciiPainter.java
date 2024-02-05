package asciipanel;

import java.awt.Color;

/**
 * An instance of AsciiPainter exposes methods to draw graphics into an AsciiRaster instance,
 * called the surface.
 * 
 * In order to draw into an AsciiRaster, one can create an instance of AsciiPainter and use
 * its methods to draw something. All methods support chaining to enhance readability and ease
 * of use.
 * 
 * A painter works like a pen, defined by a position (specified by properties penX and penY),
 * a foreground color and a background color (specified by properties foregroundColor and
 * backgroundColor). When drawing methods are invoked, the drawings will appear at the position
 * of the pen and with the colors of the pen. 
 * 
 * @author Lorenzo Bianchi
 */
public class AsciiPainter {

	private AsciiRaster surface;
	
	private Color backgroundColor = Color.black;
	private Color foregroundColor = Color.white;
	private AsciiBrush activeBrush;
	private int penX;
	private int penY;

	/**
	 * Gets the instance of AsciiRaster where the painter draws.
	 * 
	 * @return an instance of AsciiRaster
	 */
	public AsciiRaster getSurface() {
		return surface;
	}

	/**
	 * Gets the background color used for painting
	 * 
	 * @return the background color used for painting
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Sets the background color to be used for painting
	 * 
	 * @param backgroundColor the background color to be used for painting
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Gets the foreground color used for painting
	 * 
	 * @return the foreground color used for painting
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * Sets the foreground color to be used for painting
	 * 
	 * @param foregroundColor the foreground color to be used for painting
	 */
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	/**
	 * Gets the brush used for drawing lines
	 * 
	 * @return the brush used for drawing lines
	 */
	public AsciiBrush getActiveBrush() {
		return activeBrush;
	}

	/**
	 * Sets the brush to be used for drawing lines
	 * 
	 * @param activeBrush the brush to be used for drawing lines
	 */
	public void setActiveBrush(AsciiBrush activeBrush) {
		this.activeBrush = activeBrush;
	}

	/**
	 * Gets the horizontal coordinate of the pen. Drawing methods will draw
	 * at the pen position.
	 * 
	 * @return the horizontal coordinate of the pen
	 */
	public int getPenX() {
		return penX;
	}

	/**
	 * Sets the horizontal coordinate of the pen. Drawing methods will draw
	 * at the pen position.
	 * 
	 * @param penX the horizontal coordinate of the pen
	 */
	public void setPenX(int penX) {
		this.penX = penX;
	}

	/**
	 * Gets the vertical coordinate of the pen. Drawing methods will draw
	 * at the pen position.
	 * 
	 * @return the vertical coordinate of the pen
	 */
	public int getPenY() {
		return penY;
	}

	/**
	 * Sets the vertical coordinate of the pen. Drawing methods will draw
	 * at the pen position.
	 * 
	 * @param penY the vertical coordinate of the pen
	 */
	public void setPenY(int penY) {
		this.penY = penY;
	}
	
	/**
	 * Create a new painter instance with the specified surface.
	 * 
	 * @param surface the AsciiRaster instance this painter will draw into
	 */
	public AsciiPainter(AsciiRaster surface) {
		if (surface == null)
			throw new NullPointerException("AsciiPainter instantiated with null surface");
		
		this.surface = surface;
	}
	
	/**
	 * Changes the pen position to the specified coordinates.
	 * 
	 * @param penX the horizontal coordinate of the pen
	 * @param penY the vertical coordinate of the pen
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter moveTo(int penX, int penY) {
		setPenX(penX);
		setPenY(penY);
		return this;
	}
	
	/**
	 * Move the pen position by a given offset.
	 * 
	 * @param deltaX value to be added to the horizontal coordinate of the pen
	 * @param deltaY value to be added to the vertical coordinate of the pen
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter move(int deltaX, int deltaY) {
		setPenX(penX + deltaX);
		setPenY(penY + deltaY);
		return this;
	}
	
	/**
	 * Sets the background color to be used for painting. This method is an
	 * alternative to the setter that allows method chaining.
	 * 
	 * @param color the background color to be used for painting
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter background(Color color) {
		setBackgroundColor(color);
		return this;
	}
	
	/**
	 * Sets the foreground color to be used for painting. This method is an
	 * alternative to the setter that allows method chaining.
	 * 
	 * @param color the foreground color to be used for painting
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter foreground(Color color) {
		setForegroundColor(color);
		return this;
	}
	
	/**
	 * Sets the brush to be used for drawing lines. This method is an
	 * alternative to the setter that allows method chaining.
	 * 
	 * @param brush the brush to be used for drawing lines
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter brush(AsciiBrush brush) {
		setActiveBrush(brush);
		return this;
	}
	
	/**
	 * Clear the entire surface to whatever the default background color is.
	 * 
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter clear() {
		return clear(' ');
	}

	/**
	 * Clear the entire surface with the specified character and whatever the default
	 * foreground and background colors are.
	 * 
	 * @param character the character to write
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter clear(char character) {
		int oldPenX = penX, oldPenY = penY;
		
		return moveTo(0, 0).fillRect(character, surface.getWidth(), surface.getHeight()).moveTo(oldPenX, oldPenY);
	}

	/**
	 * Fills an axis-aligned rectangle with the specified character and current background and
	 * foreground colors. The area to fill is determined by its diagonal: one end of the diagonal
	 * is given by the position of the pen and the other end is given by arguments (endX, endY).
	 * The end coordinates (endX and endY) are excluded from the fill area. The position of the pen
	 * is updated to (endX, endY).
	 * 
	 * The filled rectangle is clipped to the surface area: if the coordinates lie outside the surface,
	 * only the part that lies inside will be filled.
	 * 
	 * @param character the character that will be used to fill the rectangle
	 * @param endX      horizontal coordinate of the second end of the diagonal
	 * @param endY      vertical coordinate of the second end of the diagonal
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter fillRectTo(char character, int endX, int endY) {
		if (character < 0 || character >= 256)
			throw new IllegalArgumentException(
					"character " + character + " must be within range [0," + 256 + "].");

		// Compute coordinates of the sides of the rectangle,
		// that is, given any two opposite corners of the rectangle, in any order,
		// we compute the top-left and bottom-right corners.
		int left = Math.min(penX, endX);
		int top = Math.min(penY, endY);
		int right = Math.max(penX, endX);
		int bottom = Math.max(penY, endY);

		// restrict corner coordinates to the raster area
		left = clamp(left, 0, surface.getWidth());
		top = clamp(top, 0, surface.getHeight());
		right = clamp(right, 0, surface.getWidth());
		bottom = clamp(bottom, 0, surface.getHeight());
		
		// when left == right or top == bottom, at least one side of
		// the rectangle has length 0, so we can skip the loop entirely
		if (left < right && top < bottom) {
			for (int x = left; x < right; ++x) {
				for (int y = top; y < bottom; ++y) {
					surface.getChars()[x][y] = character;
					surface.getBackcolors()[x][y] = backgroundColor;
					surface.getForecolors()[x][y] = foregroundColor;
				}
			}
		}

		// move the pen to endX, endY
		return moveTo(endX, endY);
	}
	
	/**
	 * Fills an axis-aligned rectangle with the specified character, width and height and current
	 * background and foreground colors. One corner of the rectangle is given by the pen position,
	 * width and height of the rectangle are given by method parameters. The position of the pen is
	 * moved to the opposite corner of the rectangle, as if by calling this.move(width, height).
	 * 
	 * If width is negative, the rectangle is drawn above the pen position. If height is negative,
	 * the rectangle is drawn to the left of the pen position.
	 * 
	 * The filled rectangle is clipped to the surface area: if the coordinates lie outside the surface,
	 * only the part that lies inside will be filled.
	 * 
	 * @param character the character that will be used to fill the rectangle
	 * @param width width of the rectangle to fill
	 * @param height height of the rectangle to fill
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter fillRect(char character, int width, int height) {
		return fillRectTo(character, penX + width, penY + height);
	}
	
	/**
	 * Write a character to the pen's position. This updates the pen's position by
	 * moving one step to the right, as if by calling this.move(1, 0).
	 * 
	 * If the pen's position is outside the surface area, nothing is drawn but the pen is
	 * updated.
	 * 
	 * @param character the character to write
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter write(char character) {
		if (character < 0 || character > 256)
			throw new IllegalArgumentException(
					"character " + character + " must be within range [0," + 256 + "].");

		if (penX >= 0 && penX < surface.getWidth() &&
				penY >= 0 && penY < surface.getHeight()) {
			surface.getChars()[penX][penY] = character;
			surface.getBackcolors()[penX][penY] = backgroundColor;
			surface.getForecolors()[penX][penY] = foregroundColor;
		}

		return move(1, 0);
	}
	
	/**
	 * Write a string to the pen's position. This updates the pen's position as if
	 * by calling this.move(string.length(), 0).
	 * 
	 * If the pen's position is or goes outside the surface area, only the part of
	 * the string inside the surface is drawn.
	 * 
	 * @param string the string to write
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter write(String string) {
		if (string == null)
			throw new NullPointerException("string must not be null");

		for (int i = 0; i < string.length(); ++i)
			write(string.charAt(i));

		return this;
	}
	
	/**
	 * Write a string to the center of the panel at the vertical position of the pen. This
	 * moves the pen's position to the end of the string.
	 * 
	 * If the pen's position is or goes outside the surface area, only the part of
	 * the string inside the surface is drawn.
	 * 
	 * @param string the string to write
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter writeCenter(String string) {
		if (string == null)
			throw new NullPointerException("string must not be null");

		penX = (surface.getWidth() - string.length()) / 2;
		return write(string);
	}
	
	/**
	 * Runs a flood-fill algorithm starting from the position of the pen, filling a contiguous
	 * area of the surface with the specified character and current background and foreground
	 * colors. The fill algorithm stops as soon as it finds a character different from the one
	 * in the starting position.
	 * 
	 * If the current position of the pen is outside the surface area, the algorithm starts
	 * from the nearest position that is inside the area.
	 * 
	 * @param character character used to fill
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter fill(char character) {
		int startX = clamp(penX, 0, surface.getWidth() - 1);
		int startY = clamp(penY, 0, surface.getHeight() - 1);
		
		floodFill(character, startX, startY);
		
		return this;
	}
	
	/**
	 * Recursive flood fill implementation
	 */
	private void floodFill(char character, int x, int y) {
		char oldchar = surface.getChars()[x][y];
		Color oldBg = surface.getBackcolors()[x][y];
		Color oldFg = surface.getForecolors()[x][y];

		if (oldchar == character && oldBg == backgroundColor && oldFg == foregroundColor)
			return;

		surface.getChars()[x][y] = character;
		surface.getBackcolors()[x][y] = backgroundColor;
		surface.getForecolors()[x][y] = foregroundColor;

		if (x < surface.getWidth() - 1) {
			if (surface.getChars()[x + 1][y] == oldchar)
				floodFill(character, x + 1, y);
		}
		if (x > 0) {
			if (surface.getChars()[x - 1][y] == oldchar)
				floodFill(character, x - 1, y);
		}
		if (y > 0) {
			if (surface.getChars()[x][y - 1] == oldchar)
				floodFill(character, x, y - 1);
		}
		if (y < surface.getHeight() - 1) {
			if (surface.getChars()[x][y + 1] == oldchar)
				floodFill(character, x, y + 1);
		}
	}
	
	/**
	 * Draws a given raster into the surface, with optional transparency. The raster is
	 * drawn with the upper left corner (or optionally the center) placed at the pen's position.
	 * 
	 * Only the part of the raster that lies inside the surface is drawn.
	 * 
	 * @param raster	  the raster to draw
	 * @param transparent when true, cells of the raster that have character equal to 0 will not be drawn
	 * @param center	  when true, the center of the raster is placed at the pen's position
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter drawRaster(AsciiRaster raster, boolean transparent, boolean center) {
		if (raster == null)
			throw new NullPointerException("raster cannot be null");

		int left = penX;
		int top = penY;
		
		if (center) {
			left -= raster.getWidth() / 2;
			top -= raster.getHeight() / 2;
		}
		
		int right = left + raster.getWidth();
		int bottom = top + raster.getHeight();
		
		int clipLeft = clamp(left, 0, surface.getWidth());
		int clipTop = clamp(top, 0, surface.getHeight());
		int clipRight = clamp(right, 0, surface.getWidth());
		int clipBottom = clamp(bottom, 0, surface.getHeight());
		
		// skip loops if destination area is zero-sized
		if (clipLeft < clipRight && clipTop < clipBottom) {
			for (int x = clipLeft; x < clipRight; ++x) {
				for (int y = clipTop; y < clipBottom; ++y) {
					if (!transparent || raster.getChars()[x - left][y - top] != 0) {
						surface.getChars()[x][y] = raster.getChars()[x - left][y - top];
						surface.getBackcolors()[x][y] = raster.getBackcolors()[x - left][y - top];
						surface.getForecolors()[x][y] = raster.getForecolors()[x - left][y - top];
					}
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Draws characters from a given raster into the surface, with optional transparency. The
	 * raster is drawn with the upper left corner (or optionally the center) placed at the
	 * pen's position. This method draws only draws characters from the raster, using current
	 * background and foreground colors of the pen.
	 * 
	 * Only the part of the raster that lies inside the surface is drawn.
	 * 
	 * @param raster	  the raster to draw
	 * @param transparent when true, cells of the raster that have character equal to 0 will not be drawn
	 * @param center	  when true, the center of the raster is placed at the pen's position
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter drawRasterChars(AsciiRaster raster, boolean transparent, boolean center) {
		if (raster == null)
			throw new NullPointerException("raster cannot be null");

		int left = penX;
		int top = penY;
		
		if (center) {
			left -= raster.getWidth() / 2;
			top -= raster.getHeight() / 2;
		}
		
		int right = left + raster.getWidth();
		int bottom = top + raster.getHeight();
		
		int clipLeft = clamp(left, 0, surface.getWidth());
		int clipTop = clamp(top, 0, surface.getHeight());
		int clipRight = clamp(right, 0, surface.getWidth());
		int clipBottom = clamp(bottom, 0, surface.getHeight());
		
		// skip loops if destination area is zero-sized
		if (clipLeft < clipRight && clipTop < clipBottom) {
			for (int x = clipLeft; x < clipRight; ++x) {
				for (int y = clipTop; y < clipBottom; ++y) {
					if (!transparent || raster.getChars()[x - left][y - top] != 0) {
						surface.getChars()[x][y] = raster.getChars()[x - left][y - top];
						surface.getBackcolors()[x][y] = backgroundColor;
						surface.getForecolors()[x][y] = foregroundColor;
					}
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Draws a single dot with the active brush at the pen's position. If the
	 * pen is outside the surface area, nothing is drawn.
	 * 
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter drawDot() {
		if (penX >= 0 && penX < surface.getWidth() &&
				penY >= 0 && penY < surface.getHeight()) {
			activeBrush.stroke(this, penX, penY, penX, penY);
		}
		return this;
	}
	
	// Bit flags for the Cohen-Sutherland algorithm
	private static final int CS_INSIDE = 0;
	private static final int CS_LEFT = 1;
	private static final int CS_TOP = 2;
	private static final int CS_RIGHT = 4;
	private static final int CS_BOTTOM = 8;
	
	private int endpointCode(double x, double y) {
		int code = CS_INSIDE;
		
		if (x < 0)
			code |= CS_LEFT;
		else if (x >= surface.getWidth())
			code |= CS_RIGHT;
		
		if (y < 0)
			code |= CS_TOP;
		else if (y >= surface.getHeight())
			code |= CS_BOTTOM;
		
		return code;
	}
	
	/**
	 * Draws a line with the active brush from the pen's position to
	 * (endX, endY). The pen is moved to (endX, endY).
	 * 
	 * Only the part of the line that intersects the surface area is drawn.
	 * 
	 * @param endX	horizontal coordinate of the first endpoint of the line
	 * @param endY	vertical coordinate of the second endpoint of the line
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter drawLineTo(int endX, int endY) {
		// Cohen-Sutherland line clipping algorithm
		// adapted from https://en.wikipedia.org/wiki/Cohen%E2%80%93Sutherland_algorithm
		//
		// This algorithm computes the portion of the line that is inside the raster.
		// The brush strategy is only called with coordinates that are entirely inside the surface.

		double x0 = penX, y0 = penY,
				x1 = endX, y1 = endY;
		
		int code0 = endpointCode(x0, y0);
		int code1 = endpointCode(x1, y1);
		
		int width = surface.getWidth(), height = surface.getHeight();
		
		while (code0 != CS_INSIDE || code1 != CS_INSIDE) {
			if ((code0 & code1) != 0)
				return moveTo(endX, endY);
			
			// Now certainly code0 != code1 and at least one of the two is not zero
			int code = Math.max(code0, code1);
			double x, y;

			if ((code & CS_TOP) != 0) {
				x = x0 + (x1 - x0) * (-0.5 - y0) / (y1 - y0);
				y = 0;
			} else if ((code & CS_BOTTOM) != 0) {
				x = x0 + (x1 - x0) * (height - 0.5 - y0) / (y1 - y0);
				y = height - 1;
			} else if ((code & CS_LEFT) != 0) {
				x = 0;
				y = y0 + (y1 - y0) * (-0.5 - x0) / (x1 - x0);
			} else { // code & CS_RIGHT != 0
				x = width - 1;
				y = y0 + (y1 - y0) * (width - 0.5 - x0) / (x1 - x0);
			}

			if (code == code0) {
				x0 = x;
				y0 = y;
				code0 = endpointCode(x0, y0);
			} else { // code == code1
				x1 = x;
				y1 = y;
				code1 = endpointCode(x1, y1);
			}
		}

		activeBrush.stroke(this, (int) Math.round(x0), (int) Math.round(y0), (int) Math.round(x1), (int) Math.round(y1));

		return moveTo(endX, endY);
	}
	
	/**
	 * Draws a line with the active brush from the pen's position to
	 * (penX + deltaX, penY + deltaY). The pen is moved as if by calling
	 * this.move(deltaX, deltaY).
	 * 
	 * @param deltaX value to be added to the horizontal coordinate of the pen
	 * @param deltaY value to be added to the vertical coordinate of the pen
	 * @return this for convenient chaining of method calls
	 */
	public AsciiPainter drawLine(int deltaX, int deltaY) {
		return drawLineTo(penX + deltaX, penY + deltaY);
	}
	
	/**
	 * Restricts an arbitrary integer n to the interval [min, max]. When n is inside
	 * [min, max], it returns n. When n is below min, it returns min. When n is above max,
	 * it returns max.
	 *
	 * @param n		an arbitrary integer
	 * @param min	the lower end of the interval
	 * @param max	the uppr end of the interval
	 * @return n if min <= n <= max; otherwise min if n < min, max if n > max
	 */
	private static int clamp(int n, int min, int max) {
		return Math.min(Math.max(n, min), max);
	}
}
