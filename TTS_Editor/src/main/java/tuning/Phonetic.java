/**
 * Phonetic contains methods for accessing information in the phonetic
 * representation of a word. These currently contain:
 * 
 * word
 * baseform (i.e. phonetic transcription)
 * 
 */
package tuning;

/**
 * EMLTTSEditorWeb.tuning.Phonetic.java offers methods for accessing information
 * in the phonetic transcription of a word. These currently are:
 * 
 * word - String representation of the word baseform - String representation of
 * the phonetic transcription in SAMPA format.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.10.2010 MaMi
 */
public class Phonetic {
	private String word;
	private String baseform;
	private String[] phonetic;

	/**
	 * Constructor for creating a new phonetic object containing
	 * 
	 * @param word
	 *            - String representation of the word
	 * @param baseform
	 *            - String representation of the phonetic transcription of the
	 *            particular word
	 */
	public Phonetic(String word, String baseform) {
		this.setWord(word);
		this.setBaseform(baseform);
	}

	/**
	 * Empty constructor for creating a new (empty) phonetic object.
	 */
	public Phonetic() {
		setPhonetic(new String[2]);
		setWord("word");
		setBaseform("baseform");
		getPhonetic()[0] = getWord();
		getPhonetic()[1] = getBaseform();
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for Phonetic objects.
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
	 * @param baseform
	 *            the baseform to set
	 */
	public void setBaseform(String baseform) {
		this.baseform = baseform;
	}

	/**
	 * @return the baseform
	 */
	public String getBaseform() {
		return baseform;
	}

	/**
	 * @param phonetic
	 *            the phonetic to set
	 */
	public void setPhonetic(String[] phonetic) {
		this.phonetic = phonetic;
	}

	/**
	 * @return the phonetic
	 */
	public String[] getPhonetic() {
		return phonetic;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
