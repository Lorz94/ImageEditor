package imageeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import asciipanel.AsciiFont;
import asciipanel.AsciiPanelModel;

/**
 * Displays the ASCII character grid and allows you to select one
 * 
 * @author Lorenzo Bianchi
 */
public class CharacterSelectorDialog extends JDialog implements Observer {
	private static final long serialVersionUID = -3046096625552352801L;

	private EditorStateModel editorState;
	private EditorListener editorListener;
	
	private JPanel palettePanel;
	private JButton[] paletteButtons;

	/**
	 * Gets a reference to the current editor state model
	 * 
	 * @return the state model of the editor
	 */
	public EditorStateModel getEditorState() {
		return editorState;
	}

	/**
	 * Sets the current editor state model and updates the view
	 * 
	 * @param editorState the new state model of the editor
	 */
	public void setEditorState(EditorStateModel editorState) {
		if (this.editorState == editorState)
			return;
		
		if (this.editorState != null) {
			this.editorState.getMainPanelModel().deleteObserver(this);
			this.editorState.deleteObserver(this);
		}

		this.editorState = editorState;

		if (editorState != null) {
			updatePalette();
			editorState.addObserver(this);
			this.editorState.getMainPanelModel().addObserver(this);
		}
	}

	/**
	 * Initializes an instance of the character selector dialog
	 * 
	 * @param owner			 the parent frame of the dialog
	 * @param editorListener a reference to the editor controller
	 * @param editorState	 a reference to the editor state model
	 */
	public CharacterSelectorDialog(Frame owner, EditorListener editorListener, EditorStateModel editorState) {
		super(owner, "Select character", false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		if (editorListener == null)
			throw new NullPointerException("the editor listener cannot be null");

		this.editorListener = editorListener;
		
		setResizable(false);
		
		palettePanel = new JPanel();
		palettePanel.setLayout(null);

		paletteButtons = new JButton[256];
		
		for (int i = 0; i < 256; ++i) {
			paletteButtons[i] = new JButton();
			paletteButtons[i].addActionListener(new ActionSelectChar(editorListener, (char) i));
			palettePanel.add(paletteButtons[i]);
		}
		
		add(palettePanel, BorderLayout.CENTER);
		
		setEditorState(editorState);
	}
	
	private void updatePalette() {
		AsciiFont font = editorState.getMainPanelModel().getFont();

		for (int x = 0; x < 16; ++x) {
			for (int y = 0; y < 16; ++y) {
				int i = y*16 + x;
				paletteButtons[i].setIcon(new ImageIcon(font.getGlyph(i)));
				paletteButtons[i].setBounds(x*font.getWidth(), y*font.getHeight(), font.getWidth(), font.getHeight());
			}
		}
		
		// resize the dialog based on font size
		Dimension paletteSize = new Dimension(16*font.getWidth(), 16*font.getHeight());
		
		palettePanel.setMinimumSize(paletteSize);
		palettePanel.setMaximumSize(paletteSize);
		palettePanel.setPreferredSize(paletteSize);

		pack();
	}

	/**
	 * Receives and handles updates from the model
	 * 
	 * @param o   the observable that sent the update
	 * @param arg should be a string specifying the property that changed
	 * @see EditorStateModel
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg == null || !(arg instanceof String))
			return;
		
		switch ((String)arg) {
		case AsciiPanelModel.FONT:
			updatePalette();
			break;
		}
	}
}
