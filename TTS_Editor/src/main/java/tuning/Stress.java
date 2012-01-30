/**
 * Stress contains methods for accessing information concerning stress tuning
 * in the users input. This includes:
 * 
 * word
 * stress
 * 
 */
package tuning;

/**
 * EMLTTSEditorWeb.tuning.Stress.java offers methods for accessing stress
 * specific tuning information in the user input. These are currently:
 * 
 * word - String representation of the word stress - boolean whether a word is
 * stress or not
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.10.2010 MaMi
 */
public class Stress {

	private String word;
	private boolean stress;
	private String[] syllables;
	private String[] wordStress;
	private boolean selected = false;
	private boolean boxSelect = false;

	/**
	 * Constructor for stress objects takes as parameters
	 * 
	 * @param word
	 *            - String representation of the word
	 * @param stress
	 *            - boolean whether a word is stressed or not
	 */
	public Stress(String word, boolean stress, String[] syllElems) {
		this.word = word;
		this.stress = stress;
		this.syllables = syllElems;
	}

	/**
	 * Empty constructor for creating a new, empty stress object.
	 */
	public Stress() {
		setWordStress(new String[3]);
		setWord("word");
		setStress(true);
		getWordStress()[0] = getWord();
		if (isStress()) {
			getWordStress()[1] = "true";
		} else {
			getWordStress()[1] = "false";
		}
		getWordStress()[2] = "syllables";
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for Stress objects.
	 */
	/**
	 * @param word
	 *            the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param stress
	 *            the stress to set
	 */
	public void setStress(boolean stress) {
		this.stress = stress;
	}

	/**
	 * @return the stress
	 */
	public boolean isStress() {
		return stress;
	}

	/**
	 * @param wordStress
	 *            the wordStress to set
	 */
	public void setWordStress(String[] wordStress) {
		this.wordStress = wordStress;
	}

	/**
	 * @return the wordStress
	 */
	public String[] getWordStress() {
		return wordStress;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param boxSelect
	 *            the boxSelect to set
	 */
	public void setBoxSelect(boolean boxSelect) {
		this.boxSelect = boxSelect;
	}

	/**
	 * @return the boxSelect
	 */
	public boolean isBoxSelect() {
		return boxSelect;
	}

	/**
	 * @param syllables
	 *            the syllables to set
	 */
	public void setSyllables(String[] syllables) {
		this.syllables = syllables;
	}

	/**
	 * @return the syllables
	 */
	public String[] getSyllables() {
		return syllables;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
