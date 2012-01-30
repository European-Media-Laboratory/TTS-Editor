/**
 * Sentences object stores information about the sentences in the input and 
 * offers methods to access details of the objects. These currently are:
 * 
 * id
 * value
 * words
 * tokens
 * 
 * Additionally methods for dealing with saved data or extracting data from
 * String input by the user. These currently are:
 * 
 * createSentenceList
 * getWordList
 * getTokenList
 * getSentenceListFromFile
 * extractItemList
 */
package data;

import java.util.ArrayList;

/**
 * EMLTTSEditorWeb.data.Sentences.java offers access to various parts of the
 * Sentences object:
 * 
 * id - numeric identifier of a single sentence in the input value - value of
 * the specific sentence words - ids of the words contained in the specific
 * sentence tokens - ids of the tokens contained in the specific sentence
 * 
 * Additionllay it offers methods for creating or extracting Sentence specific
 * data:
 * 
 * createSentenceList - for creating an ArrayList of Sentences objects based on
 * simple data getWordList - extracting the word identifiers included in a
 * single sentence getTokenList - extracting the token identifiers included in a
 * single sentence getSentenceListFromFile - extracting the Sentence objects
 * from a file extractItemList - extracting items from a string
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         08.11.2010 MaMi
 */
public class Sentences {
	private String id;
	private String value;
	private ArrayList<Words> words;
	private ArrayList<Tokens> tokens;
	private Constraints constraints;

	static String endString = "/>";

	/**
	 * Constructor for creating a new Sentences object.
	 */
	public Sentences() {
		id = getId();
		value = getValue();
		words = getWords();
		tokens = getTokens();
		constraints = getConstraints();
	}

	/**
	 * Constructor takes as parameters
	 * 
	 * @param id
	 *            - String representation of a numeric identifier
	 * @param value
	 *            - String containing the information in the sentence
	 * @param words
	 *            - list of numeric identifiers of the words contained in the
	 *            sentence
	 * @param tokens
	 *            - list of numeric identifiers of the tokens contained in the
	 *            sentence
	 * 
	 *            and creates a new Sentences object containing the given
	 *            information.
	 */
	public Sentences(String id, String value, ArrayList<Words> words,
			ArrayList<Tokens> tokens, Constraints constraints) {
		this.id = id;
		this.value = value;
		this.words = words;
		this.tokens = tokens;
		this.constraints = constraints;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * ADDITIONAL METHODS for Sentence Object creation and handling
	 */
	/**
	 * Sentences.createSentenceList takes as arguments
	 * 
	 * @param tmpSentences
	 *            - ArrayList containing sentences in plain String
	 *            representation
	 * @param tokens
	 *            - ArrayList containing tokens in plain String representation
	 * @param words
	 *            - ArrayList containing words in plain String representation
	 * @return - ArrayList of Sentences objects
	 */
	public static ArrayList<Sentences> createSentenceList(
			ArrayList<String> tmpSentences, ArrayList<Tokens> tokens,
			ArrayList<Words> words) {
		ArrayList<Sentences> returnList = new ArrayList<Sentences>();
		for (int a = 0; a < tmpSentences.size(); a++) {
			Sentences sent = new Sentences();
			sent.setId("" + a);
			sent.setValue(tmpSentences.get(a).toString());
			ArrayList<Tokens> tokenList = getTokenList(tokens, tmpSentences
					.get(a).toString());
			sent.setTokens(tokenList);
			ArrayList<Words> wordList = getWordList(words, tmpSentences.get(a)
					.toString());
			sent.setWords(wordList);
			returnList.add(sent);
		}
		return returnList;
	}

	/**
	 * Sentences.getWordList takes as arguments
	 * 
	 * @param words
	 *            - ArrayList of Words objects
	 * @param sentence
	 *            - String representing a single sentence
	 * @return - ArrayList containing the string representation of the numeric
	 *         identifiers pointing to the words contained in the respective
	 *         sentence
	 */
	private static ArrayList<Words> getWordList(ArrayList<Words> words,
			String sentence) {
		ArrayList<Words> returnList = new ArrayList<Words>();
		for (int a = 0; a < words.size(); a++) {
			Words word = words.get(a);
			if (sentence.contains(word.getValue())) {
				sentence.replace(word.getValue(), "");
				returnList.add(word);
			}
			if (sentence.length() < 1) {
				return returnList;
			}
		}
		return returnList;
	}

	/**
	 * Sentences.getTokenList takes as arguments
	 * 
	 * @param tokens
	 *            - ArrayList of Tokens objects
	 * @param sentence
	 *            - String representing a single sentence
	 * @return - ArrayList containing the string representation of the numeric
	 *         identifiers pointing to the tokens contained in the respective
	 *         sentence
	 */
	private static ArrayList<Tokens> getTokenList(ArrayList<Tokens> tokens,
			String sentence) {
		ArrayList<Tokens> returnList = new ArrayList<Tokens>();
		for (int a = 0; a < tokens.size(); a++) {
			Tokens token = tokens.get(a);
			if (sentence.contains(token.getValue())) {
				sentence.replace(token.getValue(), "");
				returnList.add(token);
			}
			if (sentence.length() < 1) {
				return returnList;
			}
		}
		return returnList;
	}

	/**
	 * Sentences.extractItemList takes as arguments
	 * 
	 * @param substring
	 *            - String representing a part of a line from a file
	 * @param string
	 * @return - ArrayList of elements in the substring
	 * 
	 *         Should be replaced by auxillary.StringOperations.stringToList!
	 */
	private static ArrayList<String> extractItemList(String substring) {
		String[] elems = substring.split(" ");
		ArrayList<String> returnList = new ArrayList<String>();
		for (int a = 0; a < elems.length; a++) {
			returnList.add(elems[a]);
		}
		return returnList;
	}

	/**
	 * Sentences.getSentenceListFromFile takes as arguments
	 * 
	 * @param sentFile
	 *            - String representing the fileName containing the sentences
	 *            saved
	 * @return - ArrayList of Sentences objects containing the sentences
	 *         information
	 */
	public static ArrayList<Sentences> getSentenceListFromFile(String sentFile) {
		String idString = "<id=";
		String valueString = "<value=";
		String wordsString = "<words=";
		String tokensString = "<tokens=";
		String sentEndString = "</sentence>";
		ArrayList<Sentences> returnList = new ArrayList<Sentences>();
		ArrayList<String> content = auxillary.FileIOMethods
				.getFileContent(sentFile);
		if (content != null) {
			Sentences sent = new Sentences();
			for (int a = 0; a < content.size(); a++) {
				int end = content.get(a).toString().length()
						- endString.length();

				if (content.get(a).toString().startsWith(idString)) {
					sent.setId(content.get(a).toString()
							.substring(idString.length(), end));
				}
				if (content.get(a).toString().startsWith(valueString)) {
					sent.setValue(content.get(a).toString()
							.substring(valueString.length(), end));
				}
				if (content.get(a).toString().startsWith(wordsString)) {
					ArrayList<String> wordsList = extractItemList(content.get(a)
							.toString().substring(wordsString.length(), end));
					sent.setWordsList(wordsList);
				}
				if (content.get(a).toString().startsWith(tokensString)) {
					ArrayList<String> tokensList = extractItemList(
							content.get(a).toString()
									.substring(tokensString.length(), end));
					sent.setTokensList(tokensList);
				}
				if (content.get(a).toString().startsWith(sentEndString)) {
					returnList.add(sent);
					sent = new Sentences();
				}
			}
		}
		return returnList;
	}

	/**
	 * Sentences.setWordsList takes as argument 
	 * @param wordsList - ArrayList<Strings>
	 * 
	 * and transforms it into an ArrayList<Words> and sets the respective value.
	 */
	private void setWordsList(ArrayList<String> wordsList) {
		ArrayList<Words> words = new ArrayList<Words>();
		for (int a = 0; a < wordsList.size(); a++)
		{
			Words word = new Words();
			word.setValue(wordsList.get(a));
			words.add(word);
		}
		setWords(words);
	}

	/**
	 * Sentences.setTokensList takes as argument 
	 * @param wordsList - ArrayList<Strings>
	 * 
	 * and transforms it into an ArrayList<Tokens> and sets the respective value.
	 */
	private void setTokensList(ArrayList<String> tokensList) {
		ArrayList<Tokens> tokens = new ArrayList<Tokens>();
		for (int a = 0; a < tokensList.size(); a++)
		{
			Tokens token = new Tokens();
			token.setValue(tokensList.get(a));
			tokens.add(token);
		}
		setTokens(tokens);
	}
	
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for sentences objects.
	 */
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param words
	 *            the words to set
	 */
	public void setWords(ArrayList<Words> words) {
		this.words = words;
	}

	/**
	 * @return the words
	 */
	public ArrayList<Words> getWords() {
		return words;
	}

	/**
	 * @param tokens
	 *            the tokens to set
	 */
	public void setTokens(ArrayList<Tokens> tokens) {
		this.tokens = tokens;
	}

	/**
	 * @return the tokens
	 */
	public ArrayList<Tokens> getTokens() {
		return tokens;
	}

	/**
	 * @return the constraints
	 */
	public Constraints getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints
	 *            the constraints to set
	 */
	public void setConstraints(Constraints constraints) {
		this.constraints = constraints;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
