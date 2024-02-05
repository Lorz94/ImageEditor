package imageeditor;

import java.awt.Color;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import asciipanel.AsciiFont;
import asciipanel.AsciiPainter;
import asciipanel.AsciiPanelModel;
import asciipanel.AsciiRaster;

/**
 * This class models the state of the whole editor application.
 * It implements the observer pattern (extends the Observable class)
 * so that views can be notified of changes in the model properties.
 * 
 * @author Lorenzo Bianchi
 *
 */
public class EditorStateModel extends Observable {
	
	private static final int MAX_UNDO_STACK_SIZE = 20;
	
	/**
	 * Identifies a change of the undo stack
	 */
	public static final String UNDO_REDO_STACK = "undoStack";
	
	/**
	 * Identifies a change of the activeTool property
	 */
	public static final String ACTIVE_TOOL = "activeTool";
	
	/**
	 * Identifies a change of the activeCharacter property
	 */
	public static final String ACTIVE_CHARACTER = "activeCharacter";
	
	/**
	 * Identifies a change of the activeForeground property
	 */
	public static final String ACTIVE_FOREGROUND = "activeForeground";
	
	/**
	 * Identifies a change of the activeBackground property
	 */
	public static final String ACTIVE_BACKGROUND = "activeBackground";
	
	private Deque<AsciiRaster> undoStack = new LinkedList<AsciiRaster>();
	private Deque<AsciiRaster> redoStack = new LinkedList<AsciiRaster>();

	private ToolType activeTool = ToolType.PAINT;
	
	private char activeCharacter = 1;
	private Color activeForeground = Color.white;
	private Color activeBackground = Color.black;
	
	private AsciiPanelModel mainPanelModel = new AsciiPanelModel(AsciiFont.CP437_16x16);

	/**
	 * Constructs a new instance of the editor state model with default values.
	 */
	public EditorStateModel() {
		super();
		resetUndoRedoStack();
	}
	
	/**
	 * Returns true if there is some action to undo, false otherwise.
	 * 
	 * @return true when there are actions to undo
	 */
	public boolean canUndo() {
		return !undoStack.isEmpty();
	}
	
	/**
	 * Returns true if there is some action to redo, false otherwise.
	 * 
	 * @return true when there are actions to redo
	 */
	public boolean canRedo() {
		return !redoStack.isEmpty();
	}
	
	/**
	 * Clears the undo stack and restarts from the current state.
	 */
	public void resetUndoRedoStack() {
		undoStack.clear();
		redoStack.clear();
		
		setChanged();
		notifyObservers(UNDO_REDO_STACK);
	}
	
	/**
	 * Undoes the last edit action if possible, otherwise does nothing
	 */
	public void undo() {
		if (!canUndo())
			return;
		
		redoStack.push(mainPanelModel.getRaster());
		mainPanelModel.setRaster(undoStack.pop());
		
		setChanged();
		notifyObservers(UNDO_REDO_STACK);
	}
	
	/**
	 * Redoes the last undid edit action if possible, otherwise does nothing.
	 */
	public void redo() {
		if (!canRedo())
			return;
		
		undoStack.push(mainPanelModel.getRaster());
		mainPanelModel.setRaster(redoStack.pop());
		
		setChanged();
		notifyObservers(UNDO_REDO_STACK);
	}

	/**
	 * Gets the currently active painting tool
	 * 
	 * @return the id of the tool
	 */
	public ToolType getActiveTool() {
		return activeTool;
	}

	/**
	 * Sets the currently active painting tool
	 * 
	 * @param activeTool the id of a tool
	 */
	public void setActiveTool(ToolType activeTool) {
		if (this.activeTool == activeTool)
			return;

		this.activeTool = activeTool;
		setChanged();
		notifyObservers(ACTIVE_TOOL);
	}

	/**
	 * Gets the character that will be used for painting operations
	 * 
	 * @return a character code
	 */
	public char getActiveCharacter() {
		return activeCharacter;
	}

	/**
	 * Sets the character that will be used for painting operations
	 * 
	 * @param activeCharacter a character code
	 */
	public void setActiveCharacter(char activeCharacter) {
		if (this.activeCharacter == activeCharacter)
			return;

		this.activeCharacter = activeCharacter;
		setChanged();
		notifyObservers(ACTIVE_CHARACTER);
	}

	/**
	 * Gets the foreground color that will be used for painting operations
	 * 
	 * @return a color object
	 */
	public Color getActiveForeground() {
		return activeForeground;
	}

	/**
	 * Sets the foreground color that will be used for painting operations
	 * 
	 * @param activeForeground a color object
	 */
	public void setActiveForeground(Color activeForeground) {
		if (this.activeForeground == activeForeground)
			return;
		
		this.activeForeground = activeForeground;
		setChanged();
		notifyObservers(ACTIVE_FOREGROUND);
	}

	/**
	 * Gets the background color that will be used for painting operations
	 * 
	 * @return a color object
	 */
	public Color getActiveBackground() {
		return activeBackground;
	}

	/**
	 * Sets the background color that will be used for painting operations
	 * 
	 * @param activeBackground a color object
	 */
	public void setActiveBackground(Color activeBackground) {
		if (this.activeBackground == activeBackground)
			return;

		this.activeBackground = activeBackground;
		setChanged();
		notifyObservers(ACTIVE_BACKGROUND);
	}

	/**
	 * Gets the model of the main panel
	 * 
	 * @return an AsciiPanelModel instance
	 */
	public AsciiPanelModel getMainPanelModel() {
		return mainPanelModel;
	}

	/**
	 * Obtains a painter that paints to the current raster.
	 * The painter is initialized with active foreground and background colors.
	 * 
	 * @return an initialized instance of AsciiPainter
	 */
	public AsciiPainter beginPaint() {
		return mainPanelModel.beginPaint()
				.move(0, 0)
				.foreground(activeForeground)
				.background(activeBackground);
	}
	
	/**
	 * Inserts a new entry into the undo stack, then obtains a painter
	 * that paints to the current raster. The painter is initialized with
	 * active foreground and background colors.
	 * 
	 * @return an initialized instance of AsciiPainter
	 */
	public AsciiPainter beginUndoablePaint() {
		// if max size would be exceeded, forget earlier actions
		while (undoStack.size() >= MAX_UNDO_STACK_SIZE)
			undoStack.removeLast();

		// when a new action is performed, empty the redo stack
		redoStack.clear();

		// push current raster into the undo stack
		undoStack.push(mainPanelModel.getRaster());

		// create a copy of the current raster (the previous one is preserved for undoing)
		mainPanelModel.setRaster(mainPanelModel.getRaster().clone());
		
		setChanged();
		notifyObservers(UNDO_REDO_STACK);
			
		return beginPaint();
	}
	
	/**
	 * Notifies main panel model of changes made with the specified painter
	 * 
	 * @param painter a painter obtained by calling beginPaint()
	 */
	public void endPaint(AsciiPainter painter) {
		mainPanelModel.endPaint(painter);
	}
}
