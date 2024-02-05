package imageeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputListener;

import asciipanel.AsciiPainter;
import asciipanel.AsciiPanel;
import asciipanel.AsciiPanelModel;
import asciipanel.AsciiRaster;

/**
 * The main window of the application.
 * Implements the observer pattern in order to receive updates from the state model.
 * 
 * @author Lorenzo Bianchi
 */
public class EditorView extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	
	private EditorStateModel editorState;
	private EditorListener editorListener;
	private MouseInputListener tool;

	private AsciiPanel mainPanel;
	private JScrollPane mainScrollPane;

	private JPanel toolPanel;
	private JPanel foregroundSelector;
	private JPanel backgroundSelector;

	private JPanel activeCharacterDisplayContainer;
	private AsciiPanel activeCharacterDisplay;
	
	private CharacterSelectorDialog characterSelector;
	
	private ButtonGroup toolButtonGroup = new ButtonGroup();
	private EnumMap<ToolType, JToggleButton> toolButtons = new EnumMap<ToolType, JToggleButton>(ToolType.class);
	
	private ButtonGroup toolMenuItemGroup = new ButtonGroup();
	private EnumMap<ToolType, JRadioButtonMenuItem> toolMenuItems = new EnumMap<ToolType, JRadioButtonMenuItem>(ToolType.class);
	
	private JMenuItem editUndoMenuItem;
	private JMenuItem editRedoMenuItem;
	
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
			mainPanel.setModel(editorState.getMainPanelModel());
			activeCharacterDisplay.getModel().setFont(editorState.getMainPanelModel().getFont());

			mainScrollPane.getHorizontalScrollBar().setUnitIncrement(editorState.getMainPanelModel().getFont().getWidth()); // fast scrolling with mouse wheel
			mainScrollPane.getVerticalScrollBar().setUnitIncrement(editorState.getMainPanelModel().getFont().getHeight());
			mainScrollPane.getViewport().revalidate();
			
			updateUndoRedo();
			updateActiveTool();
			updateActiveCharacterDisplay();
			
			editorState.addObserver(this);
			editorState.getMainPanelModel().addObserver(this); // start observing the panel model
			
			pack();
		}
	}

	/**
	 * Initializes the view
	 * 
	 * @param editorListener a reference to the editor controller
	 * @param editorState    a reference to the editor state model
	 */
	public EditorView(EditorListener editorListener, EditorStateModel editorState) {
		super("ASCII EditorView");

		if (editorListener == null)
			throw new NullPointerException("the editor listener cannot be null");

		this.editorListener = editorListener;
		
		characterSelector = new CharacterSelectorDialog(this, editorListener, editorState);

		mainPanel = new AsciiPanel();
		mainScrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		mainScrollPane.setPreferredSize(new Dimension(1000, 700)); // default window size
		mainScrollPane.getViewport().setOpaque(true);
		mainScrollPane.getViewport().setBackground(Color.gray);
		mainScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // prevent repeated repaint

		add(mainScrollPane, BorderLayout.CENTER);

		for (ToolType tool : ToolType.values()) {
			JToggleButton toolButton = new JToggleButton(tool.icon);
			toolButtonGroup.add(toolButton);
			
			toolButton.setToolTipText(tool.label);
			toolButton.addActionListener(new ActionSelectTool(editorListener, tool));
			
			toolButtons.put(tool, toolButton);
		}

		JButton previousCharacterButton = new JButton("-");
		JButton nextCharacterButton = new JButton("+");
	
		activeCharacterDisplayContainer = new JPanel(new GridBagLayout());
		activeCharacterDisplayContainer.setMinimumSize(new Dimension(40, 40));
		activeCharacterDisplayContainer.setPreferredSize(new Dimension(40, 40));

		activeCharacterDisplay = new AsciiPanel(new AsciiPanelModel(new AsciiRaster(1, 1)));
		
		activeCharacterDisplayContainer.add(activeCharacterDisplay);

		JPanel colorSelectorPanel = new JPanel(new GridBagLayout());
		
		colorSelectorPanel.setMinimumSize(new Dimension(0, 40));
		colorSelectorPanel.setPreferredSize(new Dimension(0, 40));

		foregroundSelector = new JPanel();
		backgroundSelector = new JPanel();

		// Build layout for colorSelectorPanel
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		
		colorSelectorPanel.add(foregroundSelector, gbc);
		colorSelectorPanel.add(backgroundSelector, gbc);

		// Build layout for toolPanel
		toolPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.ipady = 15;

		for (ToolType tool : ToolType.values()) {
			toolPanel.add(toolButtons.get(tool), gbc);
		}
		
		gbc.gridwidth = 1;
		gbc.ipady = 0;

		toolPanel.add(previousCharacterButton, gbc);
		toolPanel.add(activeCharacterDisplayContainer, gbc);
		toolPanel.add(nextCharacterButton, gbc);

		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		toolPanel.add(colorSelectorPanel, gbc);

		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.weighty = 1;
		
		toolPanel.add(Box.createVerticalGlue(), gbc);
		
		add(toolPanel, BorderLayout.LINE_START);

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem fileNewMenuItem = new JMenuItem("New");
		JMenuItem fileLoadMenuItem = new JMenuItem("Load");
		JMenuItem fileSaveMenuItem = new JMenuItem("Save");
		JMenuItem fileImportMenuItem = new JMenuItem("Import");

		menuBar.add(fileMenu);
		fileMenu.add(fileNewMenuItem);
		fileMenu.add(fileLoadMenuItem);
		fileMenu.add(fileSaveMenuItem);
		fileMenu.add(fileImportMenuItem);
		
		JMenu editMenu = new JMenu("Edit");
		editUndoMenuItem = new JMenuItem("Undo");
		editRedoMenuItem = new JMenuItem("Redo");
		
		menuBar.add(editMenu);
		editMenu.add(editUndoMenuItem);
		editMenu.add(editRedoMenuItem);
		
		JMenu toolMenu = new JMenu("Tools");

		for (ToolType tool : ToolType.values()) {
			JRadioButtonMenuItem toolItem = new JRadioButtonMenuItem(tool.label);
			toolMenuItemGroup.add(toolItem);
			
			toolItem.addActionListener(new ActionSelectTool(editorListener, tool));
			
			toolMenuItems.put(tool, toolItem);
			toolMenu.add(toolItem);
		}
		
		menuBar.add(toolMenu);

		this.setJMenuBar(menuBar);
		menuBar.setVisible(true);

		fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMaskEx()));
		fileNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorView.this.editorListener.newActionPerformed(e);
			}
		});

		fileLoadMenuItem.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMaskEx()));
		fileLoadMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorView.this.editorListener.loadActionPerformed(e);
			}
		});

		fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMaskEx()));
		fileSaveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorView.this.editorListener.saveActionPerformed(e);
			}
		});
		
		fileImportMenuItem.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMaskEx()));
		fileImportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorView.this.editorListener.importActionPerformed(e);
			}
		});
		
		editUndoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMaskEx()));
		editUndoMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorView.this.editorListener.undoActionPerformed(e);
			}
		});
		
		editRedoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMaskEx()));
		editRedoMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditorView.this.editorListener.redoActionPerformed(e);
			}
		});

		activeCharacterDisplayContainer.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				characterSelector.setVisible(true);
				characterSelector.requestFocus();
			}
		});
	
		previousCharacterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (EditorView.this.editorState.getActiveCharacter() > 0)
					EditorView.this.editorListener.characterSelected((char) (EditorView.this.editorState.getActiveCharacter() - 1));
			}
		});
		
		nextCharacterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (EditorView.this.editorState.getActiveCharacter() < 255)
					EditorView.this.editorListener.characterSelected((char) (EditorView.this.editorState.getActiveCharacter() + 1));
			}
		});

		foregroundSelector.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(EditorView.this, "Choose Foreground Color", EditorView.this.editorState.getActiveForeground());

				if (newColor != null)
					EditorView.this.editorListener.foregroundSelected(newColor);
			}
		});

		backgroundSelector.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(EditorView.this, "Choose Background Color", EditorView.this.editorState.getActiveBackground());

				if (newColor != null)
					EditorView.this.editorListener.backgroundSelected(newColor);
			}
		});
		
		setEditorState(editorState);
	}
	
	/**
	 * Updates the state of undo and redo menu items.
	 */
	private void updateUndoRedo() {
		editUndoMenuItem.setEnabled(editorState.canUndo());
		editRedoMenuItem.setEnabled(editorState.canRedo());
	}

	/**
	 * Attaches an instance of the active tool to the main panel.
	 * A tool is an implementation of MouseInputListener that is
	 * tasked with handling clicks and movements on the main AsciiPanel of the editor.
	 */
	private void updateActiveTool() {
		if (tool != null) {
			mainPanel.removeMouseListener(tool);
			mainPanel.removeMouseMotionListener(tool);
		}

		tool = editorState.getActiveTool() == null ? null : editorState.getActiveTool().getTool(editorState);

		if (tool != null) {
			mainPanel.addMouseListener(tool);
			mainPanel.addMouseMotionListener(tool);
			
			// Update selected tool button
			toolButtons.get(editorState.getActiveTool()).setSelected(true);
			toolMenuItems.get(editorState.getActiveTool()).setSelected(true);
		} else {
			// No tool is selected: deselect all buttons
			toolButtonGroup.clearSelection();
			toolMenuItemGroup.clearSelection();
		}
	}

	/**
	 * Updates the active character display and the background and foreground selectors
	 */
	private void updateActiveCharacterDisplay() {
		foregroundSelector.setBackground(editorState.getActiveForeground());
		backgroundSelector.setBackground(editorState.getActiveBackground());
		
		activeCharacterDisplayContainer.setBackground(editorState.getActiveBackground());
		
		AsciiPainter painter = activeCharacterDisplay.getModel().beginPaint();
		
		painter
			.clear()
			.moveTo(0, 0)
			.background(editorState.getActiveBackground())
			.foreground(editorState.getActiveForeground())
			.write(editorState.getActiveCharacter());
		
		activeCharacterDisplay.getModel().endPaint(painter);
	}

	/**
	 * Receives and handles updates from the model. Each component
	 * of the view is updated only when its associated property has
	 * changed.
	 * 
	 * @param o   the observable that sent the update
	 * @param arg should be a string specifying the property that changed
	 * @see EditorStateModel
	 * @see AsciiPanelModel
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg == null || !(arg instanceof String))
			return;

		switch ((String)arg) {
		case EditorStateModel.UNDO_REDO_STACK:
			updateUndoRedo();
			break;
		case EditorStateModel.ACTIVE_TOOL:
			updateActiveTool();
			break;
		case EditorStateModel.ACTIVE_CHARACTER:
		case EditorStateModel.ACTIVE_FOREGROUND:
		case EditorStateModel.ACTIVE_BACKGROUND:
			updateActiveCharacterDisplay();
			break;
		case AsciiPanelModel.RASTER:
		    mainScrollPane.getViewport().revalidate(); // size may have changed, update scroll pane
			break;
		case AsciiPanelModel.FONT:
			activeCharacterDisplay.getModel().setFont(editorState.getMainPanelModel().getFont());
			mainScrollPane.getViewport().revalidate(); // size may have changed, update scroll pane
			break;
		}
	}
}
