/**
 * 
 */
package imageeditor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A customized file chooser which allows you to import images and 
 * convert them into ascii-art specifying conversion parameters
 * 
 * @author Lorenzo Bianchi
 *
 */
public class ImportDialog extends JFileChooser {
	private static final long serialVersionUID = -7254817035375420050L;
	
	private JFormattedTextField thresholdTextField;
	private JCheckBox allColorsCheckbox;

	/**
	 * Initializes an instance of the import dialog (a customized file chooser)
	 * 
	 * @param currentDirectoryPath initial directory for the file chooser
	 */
	public ImportDialog(String currentDirectoryPath) {
		super(currentDirectoryPath);
		setDialogTitle("Import and convert image");
		
		JPanel conversionPanel = new JPanel(new GridBagLayout());
		conversionPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 10, 0, 0),
				BorderFactory.createTitledBorder("Options")));
		
		JLabel thresholdLabel = new JLabel("Threshold:");
		thresholdTextField = new JFormattedTextField(240);
		allColorsCheckbox = new JCheckBox("All colors", true);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(10, 10, 0, 10);

		conversionPanel.add(thresholdLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 0, 10);
		gbc.ipady = 10;
		
		conversionPanel.add(thresholdTextField, gbc);
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(10, 10, 0, 10);
		gbc.ipady = 0;
		
		conversionPanel.add(allColorsCheckbox, gbc);
		
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weighty = 1;

		conversionPanel.add(Box.createVerticalGlue(), gbc);
		
		setAccessory(conversionPanel); // add our option panel to the file chooser
		setMultiSelectionEnabled(false);
	}
	
	/**
	 * Show the file chooser with a custom label on the approve button
	 * 
	 * @param parent the parent component of the dialog; can be null
	 * @return the return state of the file chooser
	 */
	public int showImportDialog(Component parent) {
		// this is a custom method that sets the label
		// of the approve button to "Import", so that
		// the controller does not need to do it
		
		return showDialog(parent, "Import");
	}
	
	/**
	 * Gets the value of the threshold field
	 * 
	 * @return the value of the threshold option
	 */
	public int getThreshold() {
		return (int) thresholdTextField.getValue();
	}
	
	/**
	 * Gets the state of the "All colors" checkbox
	 *
	 * @return the state of the all colors option
	 */
	public boolean getAllColors() {
		return allColorsCheckbox.isSelected();
	}
}
