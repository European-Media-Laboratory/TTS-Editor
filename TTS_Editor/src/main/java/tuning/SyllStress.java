/**
 * SyllStress objects store the information about words, syllables and their
 * respective stress information. It also keeps the information which syllable
 * belongs to which word. 
 * The information currently stored is:
 * 
 * syllable
 * word
 * syllableStress
 * wordStress
 */
package tuning;

/**
 * EMLTTSEditorWeb.tuning.SyllStress.java offers methods to access the word,
 * syllable and stress information based on the input by the user.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         16.11.2010 MaMi
 */
public class SyllStress {
	private String syll;
	private String word;
	private boolean syllStress;
	private boolean wordStress;
	private String sentenceID;
	private String[] stress;

	/**
	 * Empty construction which creates a new SyllStress object containing
	 * information about the word, the syllables and the respective stress
	 * informtion.
	 */
	public SyllStress() {
		stress = new String[5];
		stress[0] = syll;
		stress[1] = word;
		stress[2] = "" + isSyllStress();
		stress[3] = "" + isWordStress();
		stress[4] = "" + getSentenceID();

	}

	/**
	 * Constructor that takes the parameters
	 * 
	 * @param syll
	 *            - String containing the syllable information
	 * @param word
	 *            - String containing the word information
	 * @param syllStress
	 *            - boolean representing the stress information
	 * @param wordStress
	 *            - boolean representing the stress information
	 * 
	 *            in order to create a new SyllStress object.
	 */
	public SyllStress(String syll, String word, boolean syllStress,
			boolean wordStress, int sentenceID) {
		this.syll = syll;
		this.word = word;
		this.syllStress = syllStress;
		this.wordStress = wordStress;
		this.sentenceID = "" + sentenceID;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for SyllStress objects.
	 */
	/**
	 * @param syll
	 *            the syll to set
	 */
	public void setSyll(String syll) {
		this.syll = syll;
	}

	/**
	 * @return the syll
	 */
	public String getSyll() {
		return syll;
	}

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
	public void setStress(String[] stress) {
		this.stress = stress;
	}

	/**
	 * @return the stress
	 */
	public String[] getStress() {
		return stress;
	}

	/**
	 * @param syllStress
	 *            the syllStress to set
	 */
	public void setSyllStress(boolean syllStress) {
		this.syllStress = syllStress;
	}

	/**
	 * @return the syllStress
	 */
	public boolean isSyllStress() {
		return syllStress;
	}

	/**
	 * @param wordStress
	 *            the wordStress to set
	 */
	public void setWordStress(boolean wordStress) {
		this.wordStress = wordStress;
	}

	/**
	 * @return the wordStress
	 */
	public boolean isWordStress() {
		return wordStress;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/

	/**
	 * @param sentenceID
	 *            the sentenceID to set
	 */
	public void setSentenceID(String sentenceID) {
		this.sentenceID = sentenceID;
	}

	/**
	 * @return the sentenceID
	 */
	public String getSentenceID() {
		return sentenceID;
	}
}
