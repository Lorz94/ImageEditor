package imageeditor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import asciipanel.AsciiPainter;
import asciipanel.AsciiRaster;
import asciipanel.AsciiRasterReader;
import asciipanel.AsciiRasterWriter;

/**
 * Main controller of the editor application. This class implements
 * the singleton pattern. It also works as a concrete command handler
 * for the view, by implementing EditorListener.
 * 
 * @author Lorenzo Bianchi
 */
public class ImageEditor implements EditorListener {

	private static ImageEditor instance = null;
	
	private EditorStateModel state;
	private EditorView view;
	
	/**
	 * Gets the unique instance of this class.
	 * 
	 * @return a global instance of ImageEditor
	 */
	public static ImageEditor getInstance() {
		if (instance == null)
			instance = new ImageEditor();
		
		return instance;
	}

	/**
	 * Gets the state model of the application.
	 * 
	 * @return an instance of EditorStateModel
	 */
	public EditorStateModel getState() {
		return state;
	}

	/**
	 * Gets the main view (main window) of the application.
	 * 
	 * @return an instance of EditorView
	 */
	public EditorView getView() {
		return view;
	}
	
	/**
	 * Initializes the state model and the main view.
	 */
	private ImageEditor() {
		state = new EditorStateModel();
		
		view = new EditorView(this, state);
		view.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit the application when the view is closed
	}
	
	/**
	 * Opens the main window and starts the application.
	 * 
	 * @param args command line arguments passed to main()
	 */
	public void start(String[] args) {
		initRaster(80, 60);
		view.setVisible(true); // open the application window
	}

	private void initRaster(int width, int height) {
		state.getMainPanelModel().setRaster(new AsciiRaster(width, height));
		
		// initialize the image
		AsciiPainter painter = state.beginPaint();

		painter
			.clear()
			.move(0, 0);

		state.endPaint(painter);
		
		state.resetUndoRedoStack();
	}
	
	/**
	 * Handles the new command from the main view.
	 * Shows a NewImageDialog to let the user select width and height.
	 * 
	 * @param e event's information
	 */
	@Override
	public void newActionPerformed(ActionEvent e) {
		new NewImageDialog(view, this, state).setVisible(true);
	}
	
	/**
	 * Handles the load command from the main view.
	 * Shows a file chooser and in the event of success, loads an ascii raster 
	 * from the selected file and shows it on the panel
	 * 
	 * @param e event's information
	 */
	@Override
	public void loadActionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser("resources/");
		int returnVal = fileChooser.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try (AsciiRasterReader r = new AsciiRasterReader(new BufferedReader(new FileReader(fileChooser.getSelectedFile())))) {
				AsciiRaster raster = r.read();

				if (raster == null) {
					JOptionPane.showMessageDialog(view, "Invalid file format", "Load failed", JOptionPane.ERROR_MESSAGE);
					return;
				}

				state.getMainPanelModel().setRaster(raster);
				state.resetUndoRedoStack();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(view, ex.getMessage(), "Load failed", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Handles the save command from the main view.
	 * Shows a file chooser and in the event of success it saves the ASCII raster to the selected file.
	 *
	 * @param e event's information
	 */
	@Override
	public void saveActionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser("resources/");
		int returnVal = fileChooser.showSaveDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try (AsciiRasterWriter w = new AsciiRasterWriter(new BufferedWriter(new FileWriter(fileChooser.getSelectedFile())))) {
				w.write(state.getMainPanelModel().getRaster());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(view, ex.getMessage(), "Save failed", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Handles the import command from the main view.
	 * Shows a customized file chooser and in the event of success it imports and converts
	 * the selected image file into an ASCII raster.
	 *
	 * @param e event's information
	 */
	@Override
	public void importActionPerformed(ActionEvent e) {
		ImportDialog importDialog = new ImportDialog("resources/");
		int returnVal = importDialog.showImportDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			try {
				// load image from file
				BufferedImage image = ImageIO.read(importDialog.getSelectedFile());
				if (image == null) {
					JOptionPane.showMessageDialog(view, "Unknown image format", "Import failed", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// helper class that converts the image to ascii art
				ImageConverter converter = new ImageConverter(
						state.getMainPanelModel().getFont(),
						importDialog.getThreshold(),
						importDialog.getAllColors());
				
				// clear the raster
				AsciiPainter painter = state.beginUndoablePaint();
				painter.clear();
				
				// perform conversion
				converter.convertTo(painter.getSurface(), image);

				// update model
				state.endPaint(painter);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(view, ex.getMessage(), "Import failed", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undoActionPerformed(ActionEvent e) {
		state.undo();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redoActionPerformed(ActionEvent e) {
		state.redo();
	}
	
	/**
	 * Handles the approve command from the NewImageDialog dialog.
	 * Reinitializes the ascii raster with the dimension specified in the NewImageDialog dialog
	 * 
	 * @param width  requested image width
	 * @param height requested image height
	 */
	@Override
	public void newImageApproved(int width, int height) {
		initRaster(width, height);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toolSelected(ToolType tool) {
		state.setActiveTool(tool);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void characterSelected(char character) {
		state.setActiveCharacter(character);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void foregroundSelected(Color foreground) {
		state.setActiveForeground(foreground);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void backgroundSelected(Color background) {
		state.setActiveBackground(background);
	}

	/**
	 * Create an instance of the controller and start the application.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		getInstance().start(args);
	}
}
