/**
 * 
 */
package imageeditor;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.MouseInputListener;

/**
 * An enum that defines available tools with their icon, label and implementation.
 * The list of values is used to generate the toolbar in the main view.
 * 
 * Each value of the enumeration implements a factory pattern that creates
 * an instance of the associated tool class. These classes implement the MouseInputListener
 * interface, so they can be used to handle mouse interaction with the main AsciiPanel.
 * 
 * @author Lorenzo Bianchi
 */
public enum ToolType {
	/**
	 * An enum item representing the paint tool. The getTool method
	 * constructs an instance of the PaintTool class.
	 */
	PAINT("Paint", "brush.png") {
		@Override
		public MouseInputListener getTool(EditorStateModel editorState) {
			return new PaintTool(editorState);
		}
	},
	
	/**
	 * An enum item representing the rectangle draw tool. The getTool method
	 * constructs an instance of the DrawRectTool class.
	 */
	DRAW_RECT("Draw rectangle", "draw_rect.png") {
		@Override
		public MouseInputListener getTool(EditorStateModel editorState) {
			return new DrawRectTool(editorState);
		}
	},
	
	/**
	 * An enum item representing the rectangle fill tool. The getTool method
	 * constructs an instance of the FillRectTool class.
	 */
	FILL_RECT("Fill rectangle", "fill_rect.png") {
		@Override
		public MouseInputListener getTool(EditorStateModel editorState) {
			return new FillRectTool(editorState);
		}
	},
	
	/**
	 * An enum item representing the eraser tool. The getTool method
	 * constructs an instance of the FillTool class.
	 */
	ERASE("Erase", "eraser.png") {
		@Override
		public MouseInputListener getTool(EditorStateModel editorState) {
			return new EraseTool(editorState);
		}
	},

	/**
	 * An enum item representing the fill tool. The getTool method
	 * constructs an instance of the FillTool class.
	 */
	FILL("Fill", "bucket.png") {
		@Override
		public MouseInputListener getTool(EditorStateModel editorState) {
			return new FillTool(editorState);
		}
	},

	/**
	 * An enum item representing the pick tool. The getTool method
	 * constructs an instance of the PickTool class.
	 */
	PICK("Pick", "dropper.png") {
		@Override
		public MouseInputListener getTool(EditorStateModel editorState) {
			return new PickTool(editorState);
		}
	};
	
	/**
	 * The label of the tool (used for the button tooltip)
	 */
	public final String label;
	
	/**
	 * The icon of the tool (used as the button icon).
	 */
	public final Icon icon;
	
	private ToolType(String label, String path) {
		this.label = label;

		BufferedImage img = null;
		try {
		    img = ImageIO.read(ToolType.class.getClassLoader().getResource(path));
		} catch (IOException e) {
		    e.printStackTrace();
		    System.exit(1);
		}

		this.icon = new ImageIcon(img);
	}
	
	/**
	 * Abstract factory method: implementations must create an instance of the class
	 * that implements the functionality of the tool corresponding to the enum value.
	 *  
	 * @param editorState a reference to the editor state model
	 * @return an instance of MouseInputListener that implements the functionality of a tool
	 */
	public abstract MouseInputListener getTool(EditorStateModel editorState);
};
