/**
 * StressTuning contains methods for accessing and dealing with stress tuning
 * information entered by the user. This currently cotnains:
 * 
 * stressString
 * 
 * Added:
 * 
 * getSyllables
 * 
 */
package tuning;

import javax.faces.context.FacesContext;

import editor.EditorManager;

/**
 * EMLTTSEditorWeb.tuning.StressTuning.java offers methods for accessing the
 * stress tuning information entered by the user. This currently contains:
 * 
 * stressString - String representation of the stress information.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         03.11.2010 MaMi TODO
 */
public class StressTuning {

	private String stressString;

	/**
	 * Empty constructor that creates a new stress tuning object based on the
	 * input in the main editor window.
	 */
	public StressTuning() {
		String content = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		String[] contentElems = content.split(" ");
		if (stressString != null) {
			stressString = "";
		}
		for (int a = 0; a < contentElems.length; a++) {
			stressString = stressString + contentElems[a] + "\n";
		}
	}

	/**
	 * StressTuning.getSyllabeles takes no arguments
	 * 
	 * @return - String Array of Syllables contained in the editor entered by
	 *         the user.
	 */
	public static String[] getSyllables() {
		String phoneticString = PhoneticTuning
				.phonetiseString(((EditorManager) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("editor")).getResult());
		String[] phoneticStringElems = phoneticString.split("\n");
		String[] returnString = new String[phoneticStringElems.length];
		for (int a = 0; a < phoneticStringElems.length; a++) {
			String[] wordPhonPairs = phoneticStringElems[a].split("\t");
			String[] syllables = wordPhonPairs[1].split("\\.");
			String newSyll = "";
			for (int b = 0; b < syllables.length; b++) {
				newSyll = newSyll + syllables[b] + "\t";
			}
			returnString[a] = newSyll.trim();
		}
		return returnString;
	}

	/**
	 * StressTuning.getMaxSyllableSize takes as arguments
	 * 
	 * @param syllables
	 *            Array of syllables
	 * @return the longest syllable in the Array
	 */
	public static String getMaxSyllableSize(String[] syllables) {
		int size = 0;
		for (int a = 0; a < syllables.length; a++) {
			String[] elems = syllables[a].split("\t");
			if (elems.length > size) {
				size = elems.length;
			}
		}
		String sizeString = "" + size;
		return sizeString;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for StressTuning.
	 */
	/**
	 * @param stressString
	 *            the stressString to set
	 */
	public void setStressString(String stressString) {
		this.stressString = stressString;
	}

	/**
	 * @return the stressString
	 */
	public String getStressString() {
		return stressString;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
