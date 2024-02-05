package asciipanel;

import java.awt.Color;

/**
 * This class displays a rectangular grid of characters ascii. Each character has
 * a foreground color and a background color associated with it.
 * 
 * @author Lorenzo Bianchi
 *
 */
public class AsciiRaster implements Cloneable {
	private char[][] chars;
	private Color[][] forecolors;
	private Color[][] backcolors;
	private int width;
	private int height;

	/**
	 * Class constructor
	 * @param width number of columns in the grid
	 * @param height number of lines in the grid
	 */
	public AsciiRaster(int width, int height) {
		setWidth(width);
		setHeight(height);
		setChars(new char[width][height]);
		setForecolors(new Color[width][height]);
		setBackcolors(new Color[width][height]);
	}
	
	/**
	 * Creates a deep copy of the raster, whose character and color arrays are independent
	 * from the original ones.
	 */
	@Override
	public AsciiRaster clone() {
		AsciiRaster copy;

		try {
			copy = (AsciiRaster)super.clone();
			copy.setChars(new char[width][height]);
			copy.setForecolors(new Color[width][height]);
			copy.setBackcolors(new Color[width][height]);
		} catch (CloneNotSupportedException e) {
			copy = new AsciiRaster(width, height);
		}
		
		// copy bidimensional arrays
		for (int x = 0; x < width; ++x) {
			System.arraycopy(chars[x], 0, copy.chars[x], 0, height);
			System.arraycopy(forecolors[x], 0, copy.forecolors[x], 0, height);
			System.arraycopy(backcolors[x], 0, copy.backcolors[x], 0, height);
		}
		
		return copy;
	}

	/**
	 * It gets number of columns in the grid
	 * 
	 * @return number of columns in the grid
	 */
	public int getWidth() {
		return width;
	}

	private void setWidth(int width) {
		this.width = width;
	}

	/**
	 * It gets number of lines in the grid
	 * 
	 * @return number of lines in the grid
	 */
	public int getHeight() {
		return height;
	}

	private void setHeight(int height) {
		this.height = height;
	}

	/**
	 * It gets the two-dimensional array of characters
	 * 
	 * @return the two-dimensional array of characters
	 */
	public char[][] getChars() {
		/*
		 * if (chars==null) {
		 * 
		 * System.out.println("raster chars null!"); System.exit(1); }
		 */

		return chars;
	}

	private void setChars(char[][] chars) {
		this.chars = chars;
	}

	/**
	 * It gets the two-dimensional array of foreground colors
	 * 
	 * @return the two-dimensional array of foreground colors
	 */
	public Color[][] getForecolors() {
		return forecolors;
	}

	private void setForecolors(Color[][] forecolors) {
		this.forecolors = forecolors;
	}

	/**
	 * It gets the two-dimensional array of background colors
	 * 
	 * @return the two-dimensional array of background colors
	 */
	public Color[][] getBackcolors() {
		return backcolors;
	}

	private void setBackcolors(Color[][] backcolors) {
		this.backcolors = backcolors;
	}
}
