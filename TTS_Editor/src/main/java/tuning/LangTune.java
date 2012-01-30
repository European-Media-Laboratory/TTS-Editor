/**
 * The LangTune Object stores information about the language for each word. This
 * allows to change the language the words come from and use the appropriate
 * g2p method for the specific language. 
 */
package tuning;

/**
 * EMLTTSEditorWeb.tuning.LangTune.java offers methods for setting/getting the
 * language information for each word.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         17.11.2010 MaMi
 */
public class LangTune {

	private String word;
	private String lang;
	private String[] langTune;

	/**
	 * Empty constructor for creating a new language tuning object.
	 */
	public LangTune() {
		langTune = new String[2];
		langTune[0] = word;
		langTune[1] = lang;
	}

	/**
	 * Constructor for creating a new language tuning object based on the two
	 * parameters:
	 * 
	 * @param word
	 *            - String containing the word information
	 * @param lang
	 *            - String representing the current language information
	 */
	public LangTune(String word, String lang) {
		this.word = word;
		this.lang = lang;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for data objects.
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
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param langTune
	 *            the langTune to set
	 */
	public void setLangTune(String[] langTune) {
		this.langTune = langTune;
	}

	/**
	 * @return the langTune
	 */
	public String[] getLangTune() {
		return langTune;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
