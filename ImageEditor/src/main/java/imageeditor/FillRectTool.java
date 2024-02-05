package imageeditor;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import asciipanel.AsciiPainter;
import asciipanel.AsciiPanel;
import asciipanel.AsciiRaster;

/**
 * Implements the MouseInputListener interface to
 * provide the behavior of the rectangle fill tool.
 * 
 * @author Lorenzo Bianchi
 */
public class FillRectTool extends MouseInputAdapter {
	
	private EditorStateModel editorState;

	private AsciiPainter painter;
	private AsciiRaster initialRaster;
	private Point startPos;
	private Point endPos;

	/**
	 * Initializes an instance of the rectangle fill tool
	 * 
	 * @param editorState a reference to the editor state model
	 */
	public FillRectTool(EditorStateModel editorState) {
		this.editorState = editorState;
	}

	/**
	 * Called when the rectangle fill tool is active and the user presses a mouse button into the main AsciiPanel.
	 * Starts the rectangle fill operation.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			startPos = ((AsciiPanel)e.getComponent()).pixelCoordToRasterCoord(e.getPoint());
			endPos = startPos;

			painter = editorState.beginUndoablePaint();

			// save the starting point
			initialRaster = painter.getSurface().clone();

			fillRect(painter, startPos, endPos);
			
			editorState.endPaint(painter);
		}
	}

	/**
	 * Called when the rectangle fill tool is active and the user releases a mouse button into the main AsciiPanel.
	 * Ends the rectangle fill operation.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (painter == null)
			return;
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			Point charPos = ((AsciiPanel)e.getComponent()).pixelCoordToRasterCoord(e.getPoint());
			
			if (!charPos.equals(endPos)) {
				endPos = charPos;

				painter
					.moveTo(0, 0)
					.drawRaster(initialRaster, false, false);
			    fillRect(painter, startPos, endPos);
			    
			    editorState.endPaint(painter);
			}
			
			initialRaster = null;
		}
	}

	/**
	 * Called when the rectangle fill tool is active and the user moves the mouse around the main AsciiPanel while a button is pressed.
	 * Fills the rectangular area defined by the mouse movement.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (initialRaster == null)
			return;

		Point charPos = ((AsciiPanel)e.getComponent()).pixelCoordToRasterCoord(e.getPoint());
		
		if (!charPos.equals(endPos)) {
			endPos = charPos;

			painter
				.moveTo(0, 0)
				.drawRaster(initialRaster, false, false);
		    fillRect(painter, startPos, endPos);
		    
		    editorState.endPaint(painter);
		}
	}

	private void fillRect(AsciiPainter painter, Point from, Point to) {
		// Compute coordinates of the sides of the rectangle,
		// that is, given any two opposite corners of the rectangle, in any order,
		// we compute the top-left and bottom-right corners.
		int left = Math.min(from.x, to.x);
		int top = Math.min(from.y, to.y);
		int right = Math.max(from.x, to.x);
		int bottom = Math.max(from.y, to.y);
		
		painter
			.moveTo(left, top)
			.fillRectTo(editorState.getActiveCharacter(), right + 1, bottom + 1);
	}
}
