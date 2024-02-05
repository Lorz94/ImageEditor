package imageeditor;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * A dialog window which allows you to reinitialize the ascii panel specifying the dimensions
 * 
 * @author Lorenzo Bianchi
 *
 */
public class NewImageDialog extends JDialog {
	private static final long serialVersionUID = -1844498949903148619L;

	/**
	 * Initializes an instance of the new image dialog
	 * 
	 * @param owner			 the parent frame of the dialog
	 * @param editorListener a reference to the editor controller
	 * @param editorState	 a reference to the editor state model
	 */
	public NewImageDialog(Frame owner, EditorListener editorListener, EditorStateModel editorState) {
		super(owner, "New image", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		if (editorListener == null)
			throw new NullPointerException("the editor listener cannot be null");
		
		if (editorState == null)
			throw new NullPointerException("the editor state model cannot be null");

		setResizable(false);
		setLayout(new GridBagLayout());
		
		// set a margin for the dialog window
		((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel widthLabel = new JLabel("Width:");
		JFormattedTextField widthTextField = new JFormattedTextField(editorState.getMainPanelModel().getRaster().getWidth());
		JLabel heightLabel = new JLabel("Height:");
		JFormattedTextField heightTextField = new JFormattedTextField(editorState.getMainPanelModel().getRaster().getHeight());
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());

		JButton approveButton = new JButton("Create New");
		JButton cancelButton = new JButton("Cancel");
		
		// build button panel
		GridBagConstraints gbc = new GridBagConstraints();
		
		buttonPanel.add(approveButton);
		
		gbc.insets = new Insets(0, 10, 0, 0);
		
		buttonPanel.add(cancelButton, gbc);
		
		// build main grid
		
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 10, 0); // insets adds space AROUND the component

		add(widthLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 10, 10, 0);
		gbc.ipady = 10; // ipady (and ipadx) adds space INSIDE the component

		add(widthTextField, gbc);
		
		gbc.gridx = 0; // new row
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.ipady = 0;

		add(heightLabel, gbc);
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 10, 10, 0);
		gbc.ipady = 10;

		add(heightTextField, gbc);
		
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.ipady = 0;
		
		add(buttonPanel, gbc);

		pack();
		setLocationRelativeTo(owner);
		
		getRootPane().setDefaultButton(approveButton); // when enter is pressed we want to activate approve

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		approveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editorListener.newImageApproved((int) widthTextField.getValue(), (int) heightTextField.getValue());
				dispose();
			}
		});
	}
}
