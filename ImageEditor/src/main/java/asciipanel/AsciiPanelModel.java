/**
 * 
 */
package asciipanel;

import java.util.Observable;

/**
 * This class models the state of an AsciiPanel instance, that is
 * provides a raster and a font and notifies changes to observers.
 * 
 * The methods beginPaint and endPaint provide a way to notify an
 * AsciiPanel of changes to the content of the raster so that the
 * panel can repaint itself. All changes to raster content must be
 * preceded by a call to beginPaint and followed by a call to
 * endPaint, otherwise no notification is sent.
 * 
 * @author Lorenzo Bianchi
 */
public class AsciiPanelModel extends Observable {
	
	/**
	 * Identifies a change of the raster property
	 */
	public static final String RASTER = "raster";
	
	/**
	 * Identifies a change in the contents of the raster
	 */
	public static final String RASTER_CONTENT = "rasterContent";
	
	/**
	 * Identifies a change of the font property
	 */
	public static final String FONT = "font";

	private AsciiRaster raster;
	private AsciiFont font;
	
	/**
	 * Constructs an AsciiPanelModel with null raster and and default font (CP437_9x16)
	 */
	public AsciiPanelModel() {
		this(null, null);
	}
	
	/**
	 * Constructs an AsciiPanelModel with specified raster and default font (CP437_9x16)
	 * 
	 * @param raster an instance of AsciiRaster or null
	 */
	public AsciiPanelModel(AsciiRaster raster) {
		this(raster, null);
	}
	
	/**
	 * Constructs an AsciiPanelModel with null raster and specified font
	 * 
	 * @param font   an instance of AsciiFont (if null, CP437_9x16 is used)
	 */
	public AsciiPanelModel(AsciiFont font) {
		this(null, font);
	}
	
	/**
	 * Constructs an AsciiPanelModel with specified raster and font
	 * 
	 * @param raster an instance of AsciiRaster or null
	 * @param font   an instance of AsciiFont (if null, CP437_9x16 is used)
	 */
	public AsciiPanelModel(AsciiRaster raster, AsciiFont font) {
		super();
		this.raster = raster;
		this.font = font != null ? font : AsciiFont.CP437_9x16;
	}

	/**
	 * Gets the raster that is currently displayed
	 * 
	 * @return a raster object
	 */
	public AsciiRaster getRaster() {
		return raster;
	}

	/**
	 * Sets the raster to display
	 * 
	 * @param raster a raster object
	 */
	public void setRaster(AsciiRaster raster) {
		// if the raster object is the same, notify that only raster content has changed
		String whatChanged = this.raster == raster ? RASTER_CONTENT : RASTER;

		this.raster = raster;
		setChanged();
		notifyObservers(whatChanged);
	}
	
	/**
	 * Gets the font that is used to display the raster
	 * 
	 * @return a font object
	 */
	public AsciiFont getFont() {
		return font;
	}

	/**
	 * Sets the font that is used to display the raster
	 * 
	 * @param font a font object
	 */
	public void setFont(AsciiFont font) {
		if (this.font == font)
			return;

		this.font = font;
		setChanged();
		notifyObservers(FONT);
	}
	
	/**
	 * Obtains a painter that paints to the current raster.
	 * 
	 * @return an initialized instance of AsciiPainter
	 */
	public AsciiPainter beginPaint() {
		return new AsciiPainter(raster);
	}
	
	/**
	 * Notifies observer of changes made with the specified painter
	 * 
	 * @param painter a painter obtained by calling beginPaint()
	 */
	public void endPaint(AsciiPainter painter) {
		setRaster(painter.getSurface());
	}
}
