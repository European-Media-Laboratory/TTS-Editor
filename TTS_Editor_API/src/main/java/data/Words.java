/**
 * Words objects stores information about words contained in the user input.
 * This currently are:
 * 
 * id
 * value
 * emph
 * phonetic
 * 
 * Added the information about 
 * 
 * language
 * 
 * Additionally it contains methods for creating/accessing words objects based
 * on user input or saved files
 * 
 * createWordList
 * getWordListFromFile
 * 
 * Added method:
 * 
 * extractDataFromFileRepresentation
 * 
 */
package data;

import java.util.ArrayList;


/**
 * EMLTTSEditorWeb.data.Words.java offers methods to access information about
 * words in the user input.
 * These are currently:
 * 
 * id - String representation of a numeric identifier
 * value - String representation of the word
 * emph - boolean information about emphasis
 * phonetic - phonetic transcription of the word
 *
 * Additionally, it offers methods to create Words objects
 * 
 * createWordList - creates Words objects based on user input
 * getWordListFromFile - extracts Words objects from saved data.
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.11.2010 MaMi
 * 
 *  
 * Added information about the words in the user input:
 * 
 * language - String representation about the language the specific words comes from
 * 
 * Added method:
 * 
 * extractDataFromFileRepresentation
 *
 * 29.11.2010 MaMi
 * 
 */
public class Words 
{
	private String id;
	private String value;
	private boolean emph;
	private String phonetic;
	private String language;
	private ArrayList<Syllables> syllables;

	static String idString = "<id=";
	static String valueString = "<value=";
	static String emphString = "<emph=";
	static String phoneticString = "<phonetic=";
	static String langString = "<language=";
	static String wordEndString = "</word>";
	
	static String endString = "/>";
	
	/**
	 * Constructor for creating a Words object based on the parameters
	 * 
	 * @param id - String representation of the numeric identifier
	 * @param value - String representation of the word
	 * @param emph - boolean information about emphasis
	 * @param phonetic - phonetic transcription of the word
	 */
	public Words
	(
			String id,
			String value,
			boolean emph,
			String phonetic,
			String language,
			ArrayList<Syllables> syllables
	)
	{
		this.id = id;
		this.value = value;
		this.emph = emph;
		this.phonetic = phonetic;
		this.language = language;
		this.setSyllables(syllables);
	}
	
	/**
	 * Constructor for creating a new (empty) Words object.
	 */
	public Words()
	{
		value = getValue();
		id = getId();
		emph = isEmph();
		phonetic = getPhonetic();
		language = getLanguage();
		syllables = getSyllables();
	}
	
	
	/**
	 * Words.createWordList takes as parameters
	 * 
	 * @param tmpWords
	 *            - ArrayList of Strings representing the words contained in the
	 *            users input
	 * @return - ArrayList of Words objects
	 */
	public static ArrayList<Words> createWordList
	(
			ArrayList<String> tmpWords
	) 
	{
		ArrayList<Words> returnList = new ArrayList<Words>();
		String words = "";
		for (int a = 0; a < tmpWords.size(); a++) {
			words = words
					+ tmpWords.get(a).toString().toLowerCase().replace(".", "")
					+ " ";
		}
		int counter = 0;
		for (int a = 0; a < tmpWords.size(); a++)
		{
			Words word = new Words();
			word.setId("" + counter);
			word.setValue(tmpWords.get(a).toString());
			word.setPhonetic("");
			word.setEmph(false);
			word.setLanguage("DE");
			counter = counter + 1;
			returnList.add(word);
		}
		return returnList;
	}

	/**
	 * Words.extractDataFromFileRepresetation takes as parameters
	 * 
	 * @param content
	 *            - ArrayList extracted from the data or the file where the data
	 *            is stored
	 * @return - ArrayList of Words Objects containing the data that was
	 *         previously stored in the ArrayList.
	 */
	public static ArrayList<Words> extractDataFromFileRepresentation(
			ArrayList<String> content) {
		ArrayList<Words> returnList = new ArrayList<Words>();
		Words word = new Words();
		for (int a = 0; a < content.size(); a++) {
			int end = content.get(a).toString().length() - endString.length();

			if (content.get(a).toString().startsWith(idString)) {
				word.setId(content.get(a).toString()
						.substring(idString.length(), end));
			}
			if (content.get(a).toString().startsWith(valueString)) {
				word.setValue(content.get(a).toString()
						.substring(valueString.length(), end));
			}
			if (content.get(a).toString().startsWith(emphString)) {
				boolean emphBool = auxillary.StringOperations
						.stringToBool(content.get(a).toString()
								.substring(emphString.length(), end));
				word.setEmph(emphBool);
			}
			if (content.get(a).toString().startsWith(phoneticString)) {
				word.setPhonetic(content.get(a).toString()
						.substring(phoneticString.length(), end));
			}
			if (content.get(a).toString().startsWith(langString)) {
				word.setLanguage(content.get(a).toString()
						.substring(langString.length(), end));
			}
			if (content.get(a).toString().startsWith(wordEndString)) {
				returnList.add(word);
				word = new Words();
			}
		}
		return returnList;
	}
	
	/**
	 * Words.getWordListFromFile takes as parameters
	 * 
	 * @param wordsFile
	 *            - String representation of the fileName which contains earlier
	 *            saved words information
	 * @return - ArrayList of Words objects containing the information stored in
	 *         the file.
	 */
	public static ArrayList<Words> getWordListFromFile
	(
		String wordsFile
	) 
	{
		ArrayList<String> content = auxillary.FileIOMethods.getFileContent(wordsFile);
		if (content != null) {
			ArrayList<Words> returnList = extractDataFromFileRepresentation(content);
			return returnList;
		}
		return null;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/	
	/**
	 * AUTOMATICALLY created getter and setter methods for sentences objects. 
	 */
	/**
	 * @param id the id to set
	 */
	public void setId
	(
			String id
	) 
	{
		this.id = id;
	}
	/**
	 * @return the id
	 */
	public String getId
	() 
	{
		return id;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue
	(
			String value
	) 
	{
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public String getValue
	() 
	{
		return value;
	}
	/**
	 * @param emph the emph to set
	 */
	public void setEmph
	(
			boolean emph
	) 
	{
		this.emph = emph;
	}
	/**
	 * @return the emph
	 */
	public boolean isEmph
	() 
	{
		return emph;
	}
	/**
	 * @param phonetic the phonetic to set
	 */
	public void setPhonetic
	(
			String phonetic
	) 
	{
		this.phonetic = phonetic;
	}
	/**
	 * @return the phonetic
	 */
	public String getPhonetic
	()
	{
		return phonetic;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @return the syllables
	 */
	public ArrayList<Syllables> getSyllables() {
		return syllables;
	}
	/**
	 * @param syllables2 the syllables to set
	 */
	public void setSyllables(ArrayList<Syllables> syllables) {
		this.syllables = syllables;
	}	
	/**************************************************************
	 * ************************************************************
	 **************************************************************/


}
