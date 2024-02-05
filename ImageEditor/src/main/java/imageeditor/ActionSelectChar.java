package imageeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implements an action that selects a specific character
 * 
 * @author Lorenzo Bianchi
 */
public class ActionSelectChar implements ActionListener {

	private EditorListener editorListener;
	private char character;

	/**
	 * Initializes an instance which selects the specified character
	 * 
	 * @param editorListener a reference to the application controller
	 * @param character      the character to select when the action is performed
	 */
	public ActionSelectChar(EditorListener editorListener, char character) {
		this.editorListener = editorListener;
		this.character = character;
	}
	
	/**
	 * Executes the action selecting the specified character 
	 * 
	 * @param e event's information
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		editorListener.characterSelected(character);
	}
}
