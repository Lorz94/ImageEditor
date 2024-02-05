package imageeditor;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import asciipanel.AsciiPainter;
import asciipanel.AsciiPanel;

/**
 * Implements the MouseInputListener interface to
 * provide the behavior of the paint tool.
 * 
 * @author Lorenzo Bianchi
 */
public class PaintTool extends MouseInputAdapter {
	
	private EditorStateModel editorState;

	private AsciiPainter painter;

	/**
	 * Initializes an instance of the paint tool
	 * 
	 * @param editorState a reference to the editor state model
	 */
	public PaintTool(EditorStateModel editorState) {
		this.editorState = editorState;
	}
	
	/**
	 * Called when the paint tool is active and the user presses a mouse button into the main AsciiPanel.
	 * Starts the paint operation.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Point charPos = ((AsciiPanel)e.getComponent()).pixelCoordToRasterCoord(e.getPoint());

			painter = editorState.beginUndoablePaint();

			painter
				.moveTo(charPos.x, charPos.y)
				.brush(new CharacterBrush(editorState.getActiveCharacter()))
				.drawDot();
			
			editorState.endPaint(painter);
		}
	}
	
	/**
	 * Called when the paint tool is active and the user releases a mouse button into the main AsciiPanel.
	 * Ends the paint operation.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (painter == null)
			return;

		if (e.getButton() == MouseEvent.BUTTON1) {
			Point charPos = ((AsciiPanel)e.getComponent()).pixelCoordToRasterCoord(e.getPoint());
			
			if (charPos.x != painter.getPenX() || charPos.y != painter.getPenY())
				painter.drawLineTo(charPos.x, charPos.y);
			
			editorState.endPaint(painter);
			painter = null;
		}
	}
	
	/**
	 * Called when the paint tool is active and the user moves the mouse around the main AsciiPanel while a button is pressed.
	 * Paints the area covered by the mouse movement.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (painter == null)
			return;
		
		Point charPos = ((AsciiPanel)e.getComponent()).pixelCoordToRasterCoord(e.getPoint());
		
		// Characters occupy many pixels. If the mouse moves inside the same character
		// we don't want to draw. When the mouse moves onto a new character, we draw something.
		if (charPos.x != painter.getPenX() || charPos.y != painter.getPenY()) {
			painter.drawLineTo(charPos.x, charPos.y);
			editorState.endPaint(painter);
		}
	}
}
