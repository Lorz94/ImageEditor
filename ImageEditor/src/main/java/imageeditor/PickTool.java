package imageeditor;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import asciipanel.AsciiPanel;
import asciipanel.AsciiRaster;

/**
 * Implements the MouseInputListener interface to
 * provide the behavior of the pick tool.
 * 
 * @author Lorenzo Bianchi
 */
public class PickTool extends MouseInputAdapter {
	private EditorStateModel editorState;
	
	/**
	 * Initializes an instance of the pick tool
	 * 
	 * @param editorState a reference to the editor state model
	 */
	public PickTool(EditorStateModel editorState) {
		this.editorState = editorState;
	}
	
	/**
	 * Called when the pick tool is active and the user clicks into the main AsciiPanel.
	 * Implements the pick operation.
	 * 
	 * @param e mouse event information
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		AsciiPanel mainPanel = (AsciiPanel)e.getComponent();

		Point charPos = mainPanel.pixelCoordToRasterCoord(e.getPoint());
		int px = charPos.x, py = charPos.y;
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			AsciiRaster raster = editorState.getMainPanelModel().getRaster();

			editorState.setActiveCharacter(raster.getChars()[px][py]);
			editorState.setActiveForeground(raster.getForecolors()[px][py]);
		    editorState.setActiveBackground(raster.getBackcolors()[px][py]);
			editorState.setActiveTool(ToolType.PAINT);
		}
	}
}
