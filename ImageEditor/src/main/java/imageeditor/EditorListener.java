/**
 * 
 */
package imageeditor;

import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * Listener for actions performed in the main editor view.
 * This implements a form of the command pattern where multiple
 * commands are provided by the same interface.
 * 
 * The application controller implements this interface to receive
 * messages from the view and handle the corresponding events.
 * 
 * @author Lorenzo Bianchi
 */
public interface EditorListener {
	/**
	 * Handles the new command from the main view.
	 * 
	 * @param e event's information
	 */
	void newActionPerformed(ActionEvent e);
	
	/**
	 * Handles the load command from the main view.
	 * 
	 * @param e event's information
	 */
	void loadActionPerformed(ActionEvent e);
	
	/**
	 * Handles the save command from the main view.
	 * 
	 * @param e event's information
	 */
	void saveActionPerformed(ActionEvent e);
	
	/**
	 * Handles the import command from the main view.
	 * 
	 * @param e event's information
	 */
	void importActionPerformed(ActionEvent e);
	
	/**
	 * Handles the undo command from the main view
	 * 
	 * @param e event's information
	 */
	void undoActionPerformed(ActionEvent e);
	
	/**
	 * Handles the redo command from the main view
	 * 
	 * @param e event's information
	 */
	void redoActionPerformed(ActionEvent e);

	/**
	 * Called when a new image is approved in the new image dialog.
	 * 
	 * @param width image width specified by the user
	 * @param height image height specified by the user
	 */
	void newImageApproved(int width, int height);
	
	/**
	 * Called when a new tool is selected by the user.
	 * 
	 * @param tool the tool to be activated
	 */
	void toolSelected(ToolType tool);
	
	/**
	 * Called when a new character is selected by the user.
	 * 
	 * @param character the selected character
	 */
	void characterSelected(char character);
	
	/**
	 * Called when a new foreground color is selected by the user.
	 * 
	 * @param foreground the selected color
	 */
	void foregroundSelected(Color foreground);
	
	/**
	 * Called when a new background color is selected by the user.
	 * 
	 * @param background the selected color
	 */
	void backgroundSelected(Color background);
}
