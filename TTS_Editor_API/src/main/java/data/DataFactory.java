/**
 * DataFactory takes care of the interaction with various data objects.
 * 
 * createNewData
 * gatherSentence
 * gatherWords
 * isWord
 * gatherTokens
 * 
 * 16. December 2011 
 * 	MaMi
 *  
 */
package data;

import java.util.ArrayList;

/**
 * TTS_Editor_API.data,DataFactory.java
 * 
 * createNewData - creates a data object based on the input gatherSentence -
 * extracts sentences from the input gatherWords - extracts words from the input
 * isWord - checks whether a token is a word or not gatherTokens - gathers
 * tokens from the input
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         16.12.2011 MaMi
 */
public class DataFactory {
	/**
	 * DataFactory.createNewData creates a new Data Object based on the contents
	 * in the main TTS Editor, by extracting sentences, tokens and words and
	 * sending it to a new Data object.
	 * 
	 * @return - Data object containing the information from the editor window.
	 */
	public static Data createNewData(String content) {
		ArrayList<String> tmpSentences = gatherSentence(content);
		ArrayList<String> tmpTokens = gatherTokens(content);
		ArrayList<String> tmpWords = gatherWords(tmpTokens);

		ArrayList<Words> words = Words.createWordList(tmpWords);
		ArrayList<Tokens> tokens = Tokens.createTokenList(tmpTokens, tmpWords);
		ArrayList<Sentences> sentences = Sentences.createSentenceList(
				tmpSentences, tokens, words);
		ArrayList<Syllables> syllables = Syllables.createSyllableList(words);
		ArrayList<Constraints> constraints = null;

		Data returnData = new Data(words, tokens, syllables, sentences,
				constraints);
		return returnData;
	}

	/**
	 * DataFactory.gatherSentence takes as arguments
	 * 
	 * @param content
	 *            - String representing the input as entered by the user in the
	 *            editor
	 * @return - ArrayList of sentences.
	 */
	private static ArrayList<String> gatherSentence(String content) {
		ArrayList<String> returnList = new ArrayList<String>();
		String[] elems = null;
		if (content.contains("\n")) {
			elems = content.split("\n");
		}

		if (content.contains(".")) {
			elems = content.split("\\.");
		}

		for (int a = 0; a < elems.length; a++) {
			if (elems[a].trim().length() > 1) {
				returnList.add(elems[a]);
			}
		}

		return returnList;
	}

	/**
	 * DataFactory.gatherWords takes as arguments
	 * 
	 * @param tmpTokens
	 *            - ArrayList containing tokens extracted from the data
	 * @return - ArrayList containing words only
	 */
	private static ArrayList<String> gatherWords(ArrayList<String> tmpTokens) {
		ArrayList<String> returnList = new ArrayList<String>();
		for (int a = 0; a < tmpTokens.size(); a++) {
			if (isWord(tmpTokens.get(a).toString())) {
				returnList.add(tmpTokens.get(a).toString());
			}
		}
		return returnList;
	}

	/**
	 * DataFactory.isWord takes as arguments
	 * 
	 * @param token
	 *            - String to be checked
	 * @return - boolean whether a token is a word or not.
	 */
	private static boolean isWord(String token) {
		if (token.contains(".")) {
			return false;
		}
		if (token.contains(":")) {
			return false;
		}
		if (token.contains(";")) {
			return false;
		}
		if (token.contains("!")) {
			return false;
		}
		if (token.contains("?")) {
			return false;
		}
		if (token.contains(",")) {
			return false;
		}
		return true;
	}

	/**
	 * DataFactory.gatherTokens takes as arguments
	 * 
	 * @param content
	 *            - String representing the input from the user in the editor
	 * @return - ArrayList of tokens extracted from the input.
	 */
	private static ArrayList<String> gatherTokens(String content) {
		ArrayList<String> returnList = new ArrayList<String>();
		String[] elems = content.split(" ");
		for (int a = 0; a < elems.length; a++) {
			if (isWord(elems[a]) == false) {
				returnList.add(elems[a]);
			} else {
				returnList.add(elems[a].substring(0, elems[a].length() - 1));
				returnList.add(elems[a].substring(elems[a].length() - 1));
			}
		}
		return returnList;
	}
}
