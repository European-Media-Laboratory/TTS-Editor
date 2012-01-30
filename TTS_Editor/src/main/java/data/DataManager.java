/**
 * DataManager offers a set of methods for dealing with Data objects or creating
 * new Data objects. These currently include:
 * 
 * createNewData
 * gatherWords
 * isWord
 * gatherTokens
 * endsWithPunctuation
 * gatherSentence
 * extractSavedData
 * extractSentences
 * prepareTextbasedPhoneticTuning
 * 
 */
package data;

import importTools.SubtitlingManager;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;

import settings.userSettings.UserSettings;
import tuning.Phonetic;

/**
 * EMLTTSEditorWeb.data.DataManager.java offers methods for
 * 
 * createNewData - creating a new/empty data object gatherWords - gathering the
 * words from a list of tokens isWord - determining wether a token is a word (or
 * not) gatherTokens - gather the tokens from a list of sentences
 * endsWithPunctuation - separate word elements from punctuation elements
 * gatherSentence - extracting sentences from the text extractSavedData -
 * creating a new Data object and filling it with data from a saved file
 * extractSentences - extracting sentences from a sentenceList
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         08.11.2010 MaMi
 * 
 *         Added method
 * 
 *         prepareTextbasedPhoneticTuning for allowing the tuning of phonetic
 *         transcription via a plain editor within the application.
 * 
 *         29.11.2010 MaMi
 */
public class DataManager {

	/**
	 * DataManager.extractSavedData takes as arguments
	 * 
	 * @param sentFile
	 *            - String representing the sentence file
	 * @param tokFile
	 *            - String representing the token file
	 * @param wordsFile
	 *            - String representing the words file
	 * @param syllFile
	 *            - String representing the syllables file
	 * @return - Data object containing the information saved in the respective
	 *         files.
	 */
	public static Data extractSavedData(String sentFile, String tokFile,
			String wordsFile, String syllFile, String projectFile) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "DataManager.extractSavedData Extracting Saved Data";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		logInfo = "Extracting Sentences..." + sentFile;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Sentences> sentences = Sentences
				.getSentenceListFromFile(sentFile);

		logInfo = "Extracting Words ...";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Words> words = Words.getWordListFromFile(wordsFile);

		logInfo = "Extracting Tokens ...";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Tokens> tokens = Tokens.getTokenListFromFile(tokFile);

		logInfo = "Extracting Syllables ...";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Syllables> syllables = Syllables
				.getSyllableListFromFile(syllFile);

		logInfo = "Extracting Constraints...";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Constraints> constraints;
		if (sentences != null) {
			constraints = Constraints.getConstraintsListFromFile(sentFile);
		} else {
			constraints = null;
		}

		Data returnData = new Data(words, tokens, syllables, sentences,
				constraints);
		return returnData;
	}

	/**
	 * DataManager.extractSentences takes as arguments
	 * 
	 * @param sentenceList
	 *            - ArrayList of Sentence objects
	 * @return - String containing the information in the sentence objects
	 */
	public static String extractSentences(ArrayList<Sentences> sentenceList) {
		String returnString = "";
		for (int a = 0; a < sentenceList.size(); a++) {
			Sentences sentence = sentenceList.get(a);
			returnString = returnString + sentence.getValue() + "\n";
		}
		return returnString.trim();
	}

	/**
	 * DataManager.prepareTextbasedPhoneticTuning takes as arguments
	 * 
	 * @param phoneticArray
	 *            - ArrayList of Phonetic objects
	 * @return - ArrayList of Words objects
	 * 
	 *         The ArrayList of Words objects is then translated to be made
	 *         available for editing in a plain editor via the application
	 */
	public static ArrayList<Words> prepareTextbasedPhoneticTuning(
			ArrayList<Phonetic> phoneticArray) {
		ArrayList<Words> returnList = new ArrayList<Words>();
		for (int a = 0; a < phoneticArray.size(); a++) {
			Words newWord = new Words();
			newWord.setValue(phoneticArray.get(a).getWord());
			newWord.setPhonetic(phoneticArray.get(a).getBaseform());
			returnList.add(newWord);
		}
		return returnList;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * ADDITIONAL METHODS for Words Object creation and handling
	 */
	/**
	 * Constraints.splitPause takes as arguments
	 * 
	 * @param event
	 *            - the event calling this method, which takes care of splitting
	 *            the automatically created pause into several pauses - either
	 *            in a specific amount of pieces or into pieces of a specific
	 *            length.
	 */
	public static void splitPause(ActionEvent event, Constraints constraints,
			SubtitlingManager sm) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "Constraints.SplitPause";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		auxillary.FileIOMethods.appendStringToFile("Split Pause! ", logFile);
		auxillary.FileIOMethods.appendStringToFile(constraints.getEnd(),
				logFile);
		auxillary.FileIOMethods.appendStringToFile(constraints.getStart(),
				logFile);
		try {
			auxillary.FileIOMethods.appendStringToFile("ConstraintList.size() "
					+ sm.getConstraintList().size(), logFile);
			auxillary.FileIOMethods.appendStringToFile(constraints.getStart()
					+ " " + constraints.getEnd(), logFile);
			long startTrans = SubtitlingManager
					.transformDurationNew(constraints.getStart());
			long endTrans = SubtitlingManager.transformDurationNew(constraints
					.getEnd());
			auxillary.FileIOMethods.appendStringToFile(startTrans + " "
					+ endTrans, logFile);
			long duration = endTrans - startTrans;
			auxillary.FileIOMethods.appendStringToFile("Duration " + duration,
					logFile);
			duration = (duration / 1000);
			sm.setCurrentRowPos(sm.getConstraintList().indexOf(constraints));
			sm.setSplittingInfo("The current pause is " + duration
					+ " seconds long.");
			sm.setShowSplittingPopup(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constraints.changeType takes as arguments
	 * 
	 * @param event
	 *            - the event calling this method, which takes care of changing
	 *            whether this Constraint is a transcription or a pause. This
	 *            enables the user to change the type in case the automatic
	 *            recognition got it wrong.
	 */
	public static void changeType(ActionEvent event, Constraints constraints,
			SubtitlingManager sm) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "Constraints.changeType";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		auxillary.FileIOMethods.appendStringToFile(
				"Change Type of current Constraint", logFile);
		auxillary.FileIOMethods.appendStringToFile("is Transcription "
				+ constraints.isTranscription(), logFile);
		if (constraints.isTranscription()) {
			constraints.setTranscription(false);
		} else {
			constraints.setTranscription(true);
		}
		int index = sm.getConstraintList().indexOf(constraints);
		auxillary.FileIOMethods.appendStringToFile("INDEX " + index, logFile);
		sm.getConstraintList().set(index, constraints);
	}

	/**
	 * savename ends with .adc - audio description constraints
	 * 
	 * @param constraintList
	 * @param saveName
	 */
	public static void saveConstraintsData(
			ArrayList<Constraints> constraintList, String saveName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"Constraints.saveConstraintsData", logFile);
		auxillary.FileIOMethods.appendStringToFile(saveName, logFile);
		String constraintsSaveName = saveName + ".adc";
		ArrayList<String> saveData = new ArrayList<String>();
		String constraintListStart = "<constraintList>";
		saveData.add(constraintListStart);
		for (int a = 0; a < constraintList.size(); a++) {
			String constraintStart = "<constraint>";
			saveData.add(constraintStart);

			if (constraintList.get(a).getStart() != null) {
				String start = "<start=" + constraintList.get(a).getStart()
						+ "/>";
				saveData.add(start);
			}
			if (constraintList.get(a).getEnd() != null) {
				String end = "<end=" + constraintList.get(a).getEnd() + "/>";
				saveData.add(end);
			}
			if (constraintList.get(a).getDuration() != null) {
				String duration = "<duration="
						+ constraintList.get(a).getDuration() + "/>";
				saveData.add(duration);
			}
			if (constraintList.get(a).getSentID() != null) {
				String sentID = "<sentID=" + constraintList.get(a).getSentID()
						+ "/>";
				saveData.add(sentID);
			}
			if (constraintList.get(a).getValue() != null) {
				String value = "<value=" + constraintList.get(a).getValue()
						+ "/>";
				saveData.add(value);
			}
			if (constraintList.get(a).getRealDuration() != null) {
				String realDuration = "<realDuration="
						+ constraintList.get(a).getRealDuration() + "/>";
				saveData.add(realDuration);
			}
			if (constraintList.get(a).getPause() != null) {
				String pause = "<pause=" + constraintList.get(a).getPause()
						+ "/>";
				saveData.add(pause);
			}
			if (constraintList.get(a).isTranscription()) {
				String transcription = "<transcription="
						+ constraintList.get(a).isTranscription() + "/>";
				saveData.add(transcription);
			}
			String constraintEnd = "</constraint>";
			saveData.add(constraintEnd);
		}
		String constraintListEnd = "</constraintList>";
		saveData.add(constraintListEnd);
		auxillary.FileIOMethods.writeListToFile(saveData, constraintsSaveName);
	}

}
