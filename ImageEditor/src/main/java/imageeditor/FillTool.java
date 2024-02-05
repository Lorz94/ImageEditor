package imageeditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import asciipanel.AsciiPainter;
import asciipanel.AsciiPanel;

/**
 * Implements the MouseInputListener interface to
 * provide the behavior of the fill tool.
 * 
 * @author Lorenzo Bianchi
 */
public class FillTool extends MouseInputAdapter {
	private EditorStateModel editorState;
	
	/**
	 * Initializes an instance of the fill tool
	 * 
	 * @param editorState a reference to the editor state model
	 */
	public FillTool(EditorStateModel editorState) {
		this.editorState = editorState;
	}
	
	/**
	 * Called when the fill tool is active and the user clicks into the main AsciiPanel.
	 * Implements the fill operation.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		Point charPos = ((AsciiPanel)e.getComponent()).pixelCoordToRasterCoord(e.getPoint());
		
		AsciiPainter painter = editorState.beginUndoablePaint();
		painter.moveTo(charPos.x, charPos.y);

		if (e.getButton() == MouseEvent.BUTTON1) { // if left button, fill
			painter
				.fill(editorState.getActiveCharacter());
		}

		editorState.endPaint(painter);
	}
}
