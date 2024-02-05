package asciipanel;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * Write data from an instance of AsciiRaster into a file.
 * 
 * @author Lorenzo Bianchi
 * @see asciipanel.AsciiRasterReader
 */
public class AsciiRasterWriter implements Closeable, Flushable {

	private BufferedWriter writer;
	
	/**
     * Class constructor
     * @param writer an open BufferedWriter to write into 
     */
	public AsciiRasterWriter(BufferedWriter writer) {
		this.writer = writer;
	}
	
	/**
	 * Write data from an instance of AsciiRaster into a file. 
	 * 
	 * @param rast an instance of AsciiRaster
	 * @throws java.io.IOException thrown if an error occurs while writing the file
	 */
	public void write(AsciiRaster rast) throws IOException {
		writer.write(rast.getWidth() + "\n");
		writer.write(rast.getHeight() + "\n");

		for (int x = 0; x < rast.getWidth(); x++) {
			for (int y = 0; y < rast.getHeight(); y++) {
				Color fg = rast.getForecolors()[x][y];
				Color bg = rast.getBackcolors()[x][y];

				if (fg == null)
					fg = Color.black;
				if (bg == null)
					bg = Color.black;

				writer.write((int) rast.getChars()[x][y] + "\t" + fg.getRGB() + "\t" + bg.getRGB() + "\n");
			}
		}
	}
	
	/**
     * Flush the underlying file
     */
	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	/**
     * Closes the underlying file
     */
	@Override
	public void close() throws IOException {
		writer.close();
	}

}
