package imageeditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.util.HashMap;
import java.util.Map;

import asciipanel.AsciiFont;
import asciipanel.AsciiRaster;

/**
 * An helper class for converting pixel images to ASCII art
 * 
 * @author Lorenzo Bianchi
 */
public class ImageConverter {
	private AsciiFont font;
	private int threshold;
	private boolean allColors;
	
	/**
	 * Initializes an image converter
	 * 
	 * @param font      the font to be used for the conversion algorithm
	 * @param threshold the brightness limit used for associating characters to pixels
	 * @param allColors when this option is false, reduces the colors in the image before conversion
	 */
	public ImageConverter(AsciiFont font, int threshold, boolean allColors) {
		this.font = font;
		this.threshold = threshold;
		this.allColors = allColors;
	}
	
	/**
	 * Converts the given image to ASCII art and writes the result to the given raster.
	 * 
	 * The algorithm scales the image so that each pixel corresponds to a cell in the raster grid.
	 * Then a character is associated to each pixel based on its brightness. The threshold
	 * option limits the brightness to a maximum level to keep characters varied and interesting.
	 * 
	 * @param raster the raster where the result is to be written
	 * @param image  the image to convert
	 */
	public void convertTo(AsciiRaster raster, BufferedImage image) {
		System.out.println("Resizing...");
		
		BufferedImage resized = new BufferedImage(raster.getWidth(), raster.getHeight(), image.getType());
		Graphics2D g = resized.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, raster.getWidth(), raster.getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
		g.dispose();
		
		System.out.println("Converting...");

		Map<Integer, Integer> index2numpixels = new HashMap<Integer, Integer>();
		for (int i = 0; i < 256; i++) {
			BufferedImage bi = font.getGlyph(i);
			if (bi == null)
				continue;
			int tot = 0;
			for (int x = 0; x < bi.getWidth(); x++) {
				for (int y = 0; y < bi.getHeight(); y++) {
					int c = bi.getRGB(x, y); //valore del pixel di colore nella posizione (x,y)
					Color cc = new Color(c, false);
					if (cc.getRed() > 0 || cc.getGreen() > 0 || cc.getBlue() > 0)
						tot++; //numero di pixel nel glifo non neri
				}
			}
			int key = (int) ((float) (tot) / (float) (bi.getWidth() * bi.getHeight()) * 255.f);
			if (!index2numpixels.containsKey(key))
				index2numpixels.put(key, i);

		}

		int[][] buffer = new int[resized.getWidth()][resized.getHeight()];

		for (int x = 0; x < resized.getWidth(); x++)
			for (int y = 0; y < resized.getHeight(); y++) {
				int c = resized.getRGB(x, y);
				Color cc = new Color(c);
				int ri = Math.max(Math.max(cc.getRed(), cc.getGreen()), cc.getBlue());
				buffer[x][y] = Math.min(ri, threshold);
			}

		if (!allColors) {
			BufferedImage ci = convert4(resized);
			resized = ci;
		}

		for (int x = 0; x < resized.getWidth(); x++) {
			for (int y = 0; y < resized.getHeight(); y++) {
				int k = 255 - buffer[x][y];

				while (!index2numpixels.containsKey(k) && k > 0) {
					k--;
				}
				
				int c = resized.getRGB(x, y);
				Color cc = new Color(c);

				raster.getForecolors()[x][y] = cc;
				raster.getChars()[x][y] = (char) k;
			}
		}
	}
	
	/**
	 * Converts the source image to 4-bit colour using the default 16-colour
	 * palette:
	 * <ul>
	 * <li>BLACK</li>
	 * <li>dark RED</li>
	 * <li>dark GREEN</li>
	 * <li>dark YELLOW</li>
	 * <li>dark BLUE</li>
	 * <li>dark MAGENTA</li>
	 * <li>dark CYAN</li>
	 * <li>dark grey</li>
	 * <li>light grey</li>
	 * <li>RED</li>
	 * <li>GREEN</li>
	 * <li>YELLOW</li>
	 * <li>BLUE</li>
	 * <li>MAGENTA</li>
	 * <li>CYAN</li>
	 * <li>WHITE</li>
	 * </ul>
	 * No transparency.
	 * 
	 * @param src the source image to convert
	 * @return a copy of the source image with a 4-bit colour depth, with the
	 *         default colour pallette
	 */
	public static BufferedImage convert4(BufferedImage src) {
		int[] cmap = new int[] { 0x000000, 0x800000, 0x008000, 0x808000, 0x000080, 0x800080, 0x008080, 0x808080,
				0xC0C0C0, 0xFF0000, 0x00FF00, 0xFFFF00, 0x0000FF, 0xFF00FF, 0x00FFFF, 0xFFFFFF };
		return convert4(src, cmap);
	}

	/**
	 * Converts the source image to 4-bit colour using the given colour map. No
	 * transparency.
	 * 
	 * @param src  the source image to convert
	 * @param cmap the colour map, which should contain no more than 16 entries The
	 *             entries are in the form RRGGBB (hex).
	 * @return a copy of the source image with a 4-bit colour depth, with the custom
	 *         colour pallette
	 */
	public static BufferedImage convert4(BufferedImage src, int[] cmap) {
		IndexColorModel icm = new IndexColorModel(4, cmap.length, cmap, 0, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY, icm);
		ColorConvertOp cco = new ColorConvertOp(src.getColorModel().getColorSpace(),
				dest.getColorModel().getColorSpace(), null);
		cco.filter(src, dest);

		return dest;
	}
}
