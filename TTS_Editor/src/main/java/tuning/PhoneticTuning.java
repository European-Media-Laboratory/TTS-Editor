/**
 * PhoneticTuning contains methods for dealing with the phonetic tuning the user
 * performs in the editor. It contains access to
 * 
 * phonString
 * 
 * and methods:
 * 
 * phonetiseString
 * extractPhonStringFromFile
 * 
 * Added: 
 *
 * mapPhonemes
 * langCommand
 * 
 */
package tuning;

import java.util.ArrayList;

import javax.faces.context.FacesContext;

import popups.PopupManager;

import lexicon.LexiconService;
import settings.userSettings.UserSettings;
import ttsEngines.G2P;
import auxillary.FileIOMethods;
import auxillary.Services;
import data.Words;
import editor.EditorManager;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * EMLTTSEditorWeb.tuning.PhoneticTuning.java offers methods for creating and
 * extracting phonetic transcriptions for words and offerng to the user for
 * further tuning. The information is stored in
 * 
 * phonString
 * 
 * and the methods offered are:
 * 
 * phonetiseString - sets the phonString to the phonetic transcription
 * extractPhonStringFromFile - extracts the phonetic transcriptions from the
 * file phonetiseString - creates a phonetic transcription and returns the
 * result.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.10.2010 MaMi
 * 
 *         Added:
 * 
 *         mapPhonemes - for mapping phoneme set from one language to another
 *         langCommand - for switching the g2p model used according to language
 * 
 *         Uses the local g2p method only if the web service is unavailable.
 */
public class PhoneticTuning {
	private static String phonString;

	private G2P g2pService;

	/**
	 * Constructor for PhoneticTuning object takes as arguments
	 * 
	 * @param wordList
	 *            - ArrayList of word objects, which contains pairs of words and
	 *            their phonetic transcription.
	 */
	public PhoneticTuning(ArrayList<Words> wordList) {
		if (phonString != null) {
			phonString = "";
		}
		for (int a = 0; a < wordList.size(); a++) {
			String word = wordList.get(a).getValue();
			String phonetic = wordList.get(a).getPhonetic();
			phonString = phonString + word + "\t" + phonetic + "\n";
		}
		if (phonString.startsWith("null")) {
			phonString = phonString.replace("null", "");
		}
		phonString.trim();
	}

	/**
	 * PhoneticTuning.phonetiseString takes as argument
	 * 
	 * @param phoneticStringArray
	 *            - String[] of words of which their phonetic transcription
	 *            should be created. This is currently done using a python based
	 *            g2p method (Aachen g2p), which writes the results to a file.
	 * 
	 */
	private static void phonetiseString(String[] phoneticStringArray,
			String fileName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PhoneticTuning.phonetiseString";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String phoneticString = auxillary.StringOperations
				.arrayToString(phoneticStringArray);
		auxillary.FileIOMethods.appendStringToFile(phoneticString, logFile);
		/*
		 * Web-Based Phonetiser!!!
		 */
		G2P g2pService = Services.getG2PService();
		if (g2pService == null) {
			PopupManager pm = new PopupManager();
			pm.setArbitraryErrorValue("No g2p Service!");
			pm.setShowArbitraryError(true);
		} else {
			ArrayList<String> resultList = new ArrayList<String>();
			ArrayList<LangTune> langTunedText = ((TuningManager) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("tuningManager")).getLangTuneArray();
			if (langTunedText != null) {
				auxillary.FileIOMethods.appendStringToFile(
						"langTunedText not NULL", logFile);
				for (int a = 0; a < phoneticStringArray.length; a++) {
					String transcription = phoneticStringArray[a] + "\t";
					int index = findWord(phoneticStringArray[a], langTunedText);
					if (index > -1) {
						if (langTunedText.get(index).getLang()
								.equalsIgnoreCase("en")) {
							transcription = transcription
									+ g2pService.phonetise(
											phoneticStringArray[a], "EnGB");
						}
						if (langTunedText.get(index).getLang()
								.equalsIgnoreCase("es")) {
							transcription = transcription
									+ g2pService.phonetise(
											phoneticStringArray[a], "EsES");
						}
						if (langTunedText.get(index).getLang()
								.equalsIgnoreCase("de")) {
							transcription = transcription
									+ g2pService.phonetise(
											phoneticStringArray[a], "DeDE");
						}
					} else {
						transcription = transcription
								+ g2pService.phonetise(phoneticStringArray[a],
										"DeDE");
					}
					logInfo = "Tuned Transcription " + transcription;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					resultList.add(transcription);
				}
			} else {
				logInfo = "Simple List transcription! "
						+ phoneticStringArray.length;
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				ArrayList<String> wordList = new ArrayList<String>();
				for (int a = 0; a < phoneticStringArray.length; a++) {
					auxillary.FileIOMethods.appendStringToFile(
							phoneticStringArray[a], logFile);
					wordList.add(phoneticStringArray[a].trim());
				}
				logInfo = "wordList Size " + wordList.size();
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				resultList.addAll(g2pService.phonetise(wordList, "DeDE"));
			}

			logInfo = "DONE! " + resultList.size();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			String outFile = fileName.replace("txt", "out");
			logInfo = outFile;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			FileIOMethods.writeListToFile(resultList, outFile);
			extractPhonStringFromFile(outFile);
		}
	}

	/**
	 * PhoneticTuning.findWord takes as arguments
	 * 
	 * @param word
	 *            - String representation of the word to be found
	 * @param langTunedText
	 *            - language tuning information
	 * @return - integer corresponding to the position of the word.
	 */
	public static int findWord(String word, ArrayList<LangTune> langTunedText) {
		for (int a = 0; a < langTunedText.size(); a++) {
			if (langTunedText.get(a).getWord().equalsIgnoreCase(word)) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * PhoneticTuning.phonetiseStringLanguage takes as arguments
	 * 
	 * @param word
	 *            - String representation of the word to be phonetically
	 *            transcribed
	 * @param lang
	 *            - String representation of the language
	 * @return - the phonetic transcription of the word in the corresponding
	 *         language
	 */
	public String phonetiseStringLanguage(String word, String lang) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PhoneticTuning.phonetiseStringLanguage";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "phonetise several foreign words " + word + " " + lang;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<String> resultList = new ArrayList<String>();
		resultList.addAll(g2pService.phonetise(Arrays.asList(word.split(" ")),
				lang));

		String returnString = "";
		for (int a = 0; a < resultList.size(); a++) {
			returnString = returnString + resultList.get(a).toString() + "\n";
		}
		return returnString;
	}

	/**
	 * PhoneticTuning.extractPhonStringFromFile takes as argumemts
	 * 
	 * @param outFile
	 *            - String representation of the file, where the automatically
	 *            created transcriptions are stored, which can then be extracted
	 *            for further use.
	 */
	private static void extractPhonStringFromFile(String outFile) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PhoneticTuning.ExtractPhonStringFromFile";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (phonString != null) {
			phonString = "";
		}
		ArrayList<String> fileContent = FileIOMethods.getFileContent(outFile);
		for (int a = 0; a < fileContent.size(); a++) {
			if (fileContent.get(a).toString().trim().length() > 0) {
				boolean useList = ((LexiconService) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("table")).isUseSelection();
				if (useList == true) {
					String lexBaseform = LexiconService
							.extractBaseformFromLex(fileContent.get(a)
									.toString());
					if (lexBaseform != null) {
						String newElem = fileContent.get(a).toString()
								.split("\t")[0]
								+ "\t" + lexBaseform;
						phonString = phonString + newElem + "\n";
					} else {
						phonString = phonString + fileContent.get(a).toString()
								+ "\n";
					}
				} else {
					phonString = phonString + fileContent.get(a).toString()
							+ "\n";
				}
			}
		}
		if (phonString != null) {
			if (phonString.startsWith("null")) {
				phonString = phonString.replace("null", "");
			}
			phonString.trim();
		}
		logInfo = phonString;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
	}

	/**
	 * PhoneticTuning.phonetiseString takes as arguments
	 * 
	 * @param word
	 *            - String representation of either a single word or several
	 *            words to be transcribed
	 * @return - String representation of the word - transcript - pair.
	 */
	public static String phonetiseString(String word) {
		String[] wordArray = word.split("\\s+");
		String path = ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"))
				.getUserPath();
		String fileName = path + "phonetiseTMP.txt";
		phonetiseString(wordArray, fileName);
		return phonString;
	}

	/**
	 * PhoneticTuning.getPhoneticString takes as argument
	 * 
	 * @param selection
	 *            - String representation of the selected items from the editor
	 * @return - phonetic transcription of the selected items.
	 */
	public String getPhoneticString(String selection) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.phoneticTuning";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String returnString;
		if (selection.length() > 0) {
			logInfo = "Selected Elements";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			String selectionToTune = auxillary.StringOperations
					.extractSelection(selection, ((EditorManager) FacesContext
							.getCurrentInstance().getExternalContext()
							.getSessionMap().get("editor")).getResult());
			returnString = phonetiseString(selectionToTune);
			selection = "";
		} else {
			logInfo = "phonetise everything!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			returnString = getPhonString();
		}
		return returnString;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for PhoneticTuning.
	 */
	/**
	 * @param phonString
	 *            the phonString to set
	 */
	@SuppressWarnings("static-access")
	public void setPhonString(String phonString) {
		this.phonString = phonString;
	}

	/**
	 * @return the phonString
	 */
	public String getPhonString() {
		return phonString;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/

}
