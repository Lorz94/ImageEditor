/**
 * 
 */
package imageeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implements an action that selects a specific tool
 * 
 * @author Lorenzo Bianchi
 *
 */
public class ActionSelectTool implements ActionListener {

	private EditorListener editorListener;
	private ToolType tool;

	/**
	 * Initializes an instance which selects the specified tool
	 * 
	 * @param editorListener a reference to the application controller
	 * @param tool the tool to select when the action is performed
	 */
	public ActionSelectTool(EditorListener editorListener, ToolType tool) {
		this.editorListener = editorListener;
		this.tool = tool;
	}
	
	/**
	 * Executes the action selecting the specified tool
	 * 
	 * @param e event's information
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		editorListener.toolSelected(tool);
	}
}
