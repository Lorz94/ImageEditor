package asciipanel;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;

/**
 * Create an instance of AsciiRaster by loading data from a file.
 * 
 * @author Lorenzo Bianchi
 */
public class AsciiRasterReader implements Closeable {
    private BufferedReader reader;

    /**
     * Class constructor
     * @param reader an open BufferedReader to read from
     */
    public AsciiRasterReader(BufferedReader reader) {
        this.reader = reader;
    }
    
    /**
	 * Create an instance of AsciiRaster by loading data from a file.
	 * 
	 *  The file's format consists of readable text:
	 *   - the first line contains an integer that indicates the grid's width;
	 *   - the second line contains an integer that indicates the grid's height;
	 *   - the following lines consist of three integers which respectively indicate 
	 *     the ASCII character, the foreground color and the background color of each tile 
	 *     of the grid and they are separated by a character TAB
	 * 
	 * Grid data consists of blocks sx which consist of sy lines. 
	 * Each block provides the data of each column of the grid.
	 * 
	 * @return AsciiRaster instance initialized with the data in the file
	 * @throws java.io.IOException thrown if an error occurs while reading the file
	 */
    public AsciiRaster read() throws IOException {
    	try {
	    	int sx = Integer.parseInt(reader.readLine());
			int sy = Integer.parseInt(reader.readLine());
	
			AsciiRaster res = new AsciiRaster(sx, sy);
	
			int x = 0;
			int y = 0;
	
			while (reader.ready()) {
				String line = reader.readLine();
				String[] lines = line.split("\t");
				Color fg = new Color(Integer.parseInt(lines[1]));
				Color bg = new Color(Integer.parseInt(lines[2]));
				char ch = (char) Integer.parseInt(lines[0]);
				res.getChars()[x][y] = ch;
				res.getForecolors()[x][y] = fg;
				res.getBackcolors()[x][y] = bg;
				y++;
				if (y == sy) {
					y = 0;
					x++;
					if (x == sx)
						break;
				}
			}
	
			return res;
    	} catch (NumberFormatException ex) {
    		return null; // invalid format, return null
    	}
    }

    /**
     * Closes the underlying file
     */
	@Override
	public void close() throws IOException {
		reader.close();
	}
}
