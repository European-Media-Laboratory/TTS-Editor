/**
 * EditorComponents provides access to properties of the editor component in 
 * the main window. These currently are:
 * 
 * tuningSelection
 * editor
 * tuningSelectionId
 * hiddenTextFieldID
 */
package editor;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * EMLTTSEditorWeb.editor.EditorComponents.java contains methods for accessing
 * properties of the editor component, where data can be entered. In order to
 * access the data of the editor and copy selection for further usage into a
 * hidden textfield.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         08.12.2010 MaMi
 */
public class EditorComponents {
	UIComponent tuningSelection;
	UIComponent editor;

	/**
	 * EditorComponents.getTuningSelectionId takes no arguments and returns the
	 * id of the editor component where text is entered and selected.
	 * 
	 * @return String representing the ID of the editor component
	 */
	public String getTuningSelectionId() {
		FacesContext context = FacesContext.getCurrentInstance();
		return editor.getClientId(context);
	}

	/**
	 * EditorComponents.getHiddenTextFieldID takes no arguments and returns the
	 * ID of the hidden textfield where the selected text is to be copied t.
	 * 
	 * @return String representing the ID of the editor component
	 */
	public String getHiddenTextFieldID() {
		FacesContext context = FacesContext.getCurrentInstance();
		return getTuningSelection().getClientId(context);
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for information from
	 * SynthesisManager.
	 */
	/**
	 * @param editor
	 *            the editor to set
	 */
	public void setEditor(UIComponent editor) {
		this.editor = editor;
	}

	/**
	 * @return the editor
	 */
	public UIComponent getEditor() {
		return editor;
	}

	/**
	 * @param tuningSelection
	 *            the tuningSelection to set
	 */
	public void setTuningSelection(UIComponent tuningSelection) {
		this.tuningSelection = tuningSelection;
	}

	/**
	 * @return the tuningSelection
	 */
	public UIComponent getTuningSelection() {
		return tuningSelection;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
