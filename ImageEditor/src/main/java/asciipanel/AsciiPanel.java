package asciipanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

/**
 * It implements a swing panel that displays an ascii art image.
 * the image can be uploaded and edited through appropriate methods.
 * 
 * @author Lorenzo Bianchi
 */
public class AsciiPanel extends JPanel implements Observer {
	private static final long serialVersionUID = -4167851861147593092L;

	private Image offscreenBuffer;
	private Graphics offscreenGraphics;
	
	private AsciiPanelModel model;

	/**
	 * Gets the current model of the panel
	 * 
	 * @return an instance of AsciiPanelModel
	 */
	public AsciiPanelModel getModel() {
		return model;
	}

	/**
	 * Sets the model of the panel
	 * 
	 * @param model the model to use
	 */
	public void setModel(AsciiPanelModel model) {
		if (model == null)
			throw new NullPointerException("the model of AsciiPanel cannot be null");
		
		if (this.model == model)
			return;
		
		if (this.model != null)
			this.model.deleteObserver(this);

		this.model = model;
		
		updateSize();
		repaint();
		model.addObserver(this);
	}

	/**
	 * It gets the height in characters of the ascii art. A standard terminal is 24 characters high
	 * 
	 * @return the height in characters of the ascii art. A standard terminal is 24 characters high
	 */
	public int getHeightInCharacters() {
		return model.getRaster() == null ? 0 : model.getRaster().getHeight();
	}

	/**
	 * It gets the width in characters of the ascii art. A standard terminal is 80 characters wide
	 * 
	 * @return the width in characters of the ascii art. A standard terminal is 80 characters wide
	 */
	public int getWidthInCharacters() {
		return model.getRaster() == null ? 0 : model.getRaster().getWidth();
	}
	
	/**
	 * Converts a position in pixel coordinates on the panel into a position in
	 * character coordinates on the raster.
	 * 
	 * @param px a position in pixels
	 * @return coordinates of the ASCII raster cell that lies below the specified pixel
	 */
	public Point pixelCoordToRasterCoord(Point px) {
		return new Point(px.x / model.getFont().getWidth(), px.y / model.getFont().getHeight());
	}

	/**
	 * Default constructor, creates an instance with a default model
	 */
	public AsciiPanel() {
		this(new AsciiPanelModel());
	}

	/**
	 * Class constructor specifying the associated model.
	 * 
	 * @param model the model to use
	 */
	public AsciiPanel(AsciiPanelModel model) {
		super();
		setModel(model);
	}
	
	/**
	 * It updates the panel's graphic interface 
	 * 
	 * @param g graphic context
	 */
	@Override
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * It draws the current raster in the panel. When no raster is present, the
	 * panel is black.
	 * 
	 * @param g graphic context
	 */
	@Override
	public void paint(Graphics g) {
		if (g == null)
			throw new NullPointerException();
		
		// clear the surface of the panel
		g.setColor(getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (model.getRaster() == null)
			return;

		for (int x = 0; x < model.getRaster().getWidth(); x++) {
			for (int y = 0; y < model.getRaster().getHeight(); y++) {
				Color bg = model.getRaster().getBackcolors()[x][y];
				Color fg = model.getRaster().getForecolors()[x][y];
				if (bg == null)
					bg = Color.BLACK;
				if (fg == null)
					fg = Color.BLACK;

				LookupOp op = buildColorLUT(bg, fg);
				BufferedImage img = op.filter(model.getFont().getGlyph(model.getRaster().getChars()[x][y]), null);
				offscreenGraphics.drawImage(img, x * model.getFont().getWidth(), y * model.getFont().getHeight(), null);
			}
		}
		g.drawImage(offscreenBuffer, 0, 0, this);
	}

	/**
	 * Create a <code>LookupOp</code> object (lookup table) mapping the original
	 * pixels to the background and foreground colors, respectively.
	 * 
	 * @param bgColor the background color
	 * @param fgColor the foreground color
	 * @return the <code>LookupOp</code> object (lookup table)
	 */
	private LookupOp buildColorLUT(Color bgColor, Color fgColor) {
		short[] a = new short[256];
		short[] r = new short[256];
		short[] g = new short[256];
		short[] b = new short[256];
	
		byte bga = (byte) (bgColor.getAlpha());
		byte bgr = (byte) (bgColor.getRed());
		byte bgg = (byte) (bgColor.getGreen());
		byte bgb = (byte) (bgColor.getBlue());
	
		byte fga = (byte) (fgColor.getAlpha());
		byte fgr = (byte) (fgColor.getRed());
		byte fgg = (byte) (fgColor.getGreen());
		byte fgb = (byte) (fgColor.getBlue());
	
		for (int i = 0; i < 256; i++) {
			if (i == 0) {
				a[i] = bga;
				r[i] = bgr;
				g[i] = bgg;
				b[i] = bgb;
			} else {
				a[i] = fga;
				r[i] = fgr;
				g[i] = fgg;
				b[i] = fgb;
			}
		}
	
		short[][] table = { r, g, b, a };
		return new LookupOp(new ShortLookupTable(0, table), null);
	}
	
	private void updateSize() {
		Dimension panelSize = new Dimension(model.getFont().getWidth() * getWidthInCharacters(), model.getFont().getHeight() * getHeightInCharacters());
		setPreferredSize(panelSize);

		if (model.getRaster() == null) {
			offscreenBuffer = null;
			offscreenGraphics = null;
		} else {
			if (offscreenGraphics != null)
				offscreenGraphics.dispose();

			offscreenBuffer = new BufferedImage(panelSize.width, panelSize.height, BufferedImage.TYPE_INT_RGB);
			offscreenGraphics = offscreenBuffer.getGraphics();
		}
	}

	/**
	 * Receives and handles updates from the model.
	 * 
	 * @param o   the observable that sent the update
	 * @param arg should be a string specifying the property that changed
	 * @see AsciiPanelModel
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg == null || !(arg instanceof String))
			return;
		
		switch ((String)arg) {
		case AsciiPanelModel.RASTER:
		case AsciiPanelModel.FONT:
			updateSize();
			repaint();
			break;
		case AsciiPanelModel.RASTER_CONTENT:
			repaint();
			break;
		}
	}
}
