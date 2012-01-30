/**
 * The Data object store the information of the data currently being handled.
 * Currently these are:
 * wordList
 * tokenList
 * syllableList
 * sentenceList
 */
package data;

import java.util.ArrayList;

/**
 * EMLTTSEditorWeb.data.Data.java offers access to various parts of the data
 * structure:
 * 
 * wordList - containing the words in the project tokenList - containing the
 * tokens in the project syllableList - containing the syllables in the project
 * sentenceList - containing the sentences in the project
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         05.11.2010 MaMi
 */
public class Data {

	private ArrayList<Words> wordList;
	private ArrayList<Tokens> tokenList;
	private ArrayList<Syllables> syllableList;
	private ArrayList<Sentences> sentenceList;
	private ArrayList<Constraints> constraintList;

	/**
	 * Constructor for creating a data object based on parameters
	 * 
	 * @param wordList
	 * @param tokenList
	 * @param syllableList
	 * @param sentenceList
	 */
	public Data(ArrayList<Words> wordList, ArrayList<Tokens> tokenList,
			ArrayList<Syllables> syllableList,
			ArrayList<Sentences> sentenceList,
			ArrayList<Constraints> constraintList) {
		this.setWordList(wordList);
		this.setTokenList(tokenList);
		this.setSyllableList(syllableList);
		this.setSentenceList(sentenceList);
		this.setConstraintList(constraintList);
	}

	/**
	 * Constructor for creating a new data object.
	 */
	public Data() {
		wordList = new ArrayList<Words>();
		tokenList = new ArrayList<Tokens>();
		syllableList = new ArrayList<Syllables>();
		sentenceList = new ArrayList<Sentences>();
		constraintList = new ArrayList<Constraints>();
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for data objects.
	 */
	/**
	 * @param wordList
	 *            the wordList to set
	 */
	public void setWordList(ArrayList<Words> wordList) {
		this.wordList = wordList;
	}

	/**
	 * @return the wordList
	 */
	public ArrayList<Words> getWordList() {
		return wordList;
	}

	/**
	 * @param tokenList
	 *            the tokenList to set
	 */
	public void setTokenList(ArrayList<Tokens> tokenList) {
		this.tokenList = tokenList;
	}

	/**
	 * @return the tokenList
	 */
	public ArrayList<Tokens> getTokenList() {
		return tokenList;
	}

	/**
	 * @param syllableList
	 *            the syllableList to set
	 */
	public void setSyllableList(ArrayList<Syllables> syllableList) {
		this.syllableList = syllableList;
	}

	/**
	 * @return the syllableList
	 */
	public ArrayList<Syllables> getSyllableList() {
		return syllableList;
	}

	/**
	 * @param sentenceList
	 *            the sentenceList to set
	 */
	public void setSentenceList(ArrayList<Sentences> sentenceList) {
		this.sentenceList = sentenceList;
	}

	/**
	 * @return the sentenceList
	 */
	public ArrayList<Sentences> getSentenceList() {
		return sentenceList;
	}

	/**
	 * @param constraintList
	 *            the constraintList to set
	 */
	public void setConstraintList(ArrayList<Constraints> constraintList) {
		this.constraintList = constraintList;
	}

	/**
	 * @return the constraintList
	 */
	public ArrayList<Constraints> getConstraintList() {
		return constraintList;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
