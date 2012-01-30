/**
 * SyllStressTuning contains methods for accessing and setting stress information
 * both on word an syllable level. Additionally, it allows for setting and 
 * getting stress information based on a selection of words from the users
 * input.
 * 
 * These methods currently are:
 * 
 * booleanStress
 * getSyllStressArray
 * 
 */
package tuning;

import java.util.ArrayList;

import javax.faces.context.FacesContext;

import popups.PopupManager;

import settings.userSettings.UserSettings;

import editor.EditorManager;

/**
 * EMLTTSEditorWeb.tuning.SyllStressTuning.java offers methods for accessing and
 * setting stress information both on the word and the syllable level.
 * 
 * The method
 * 
 * getSyllStressArray
 * 
 * allows for getting and setting syllable information based on a subset of the
 * users input.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         16.11.2010 MaMi
 */
public class SyllStressTuning {
	private String stressString;
	private static ArrayList<SyllStress> syllStressArray;

	/**
	 * Empty constructor for creating a new syllStressArray object containing
	 * the information entered by the user via the editor.
	 */
	public SyllStressTuning() {
		syllStressArray = new ArrayList<SyllStress>();
		String content = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		String phonetisedCont = PhoneticTuning.phonetiseString(content);
		if (phonetisedCont == null || phonetisedCont.length() < 1) {
			PopupManager pm = new PopupManager();
			pm.setArbitraryErrorValue("No phonetisedContent!");
			pm.setShowArbitraryError(true);
		} else {
			String[] phonElems = phonetisedCont.split("\n");
			for (int a = 0; a < phonElems.length; a++) {
				String[] wordBaseformPair = phonElems[a].split("\t");
				String[] syllElems = wordBaseformPair[1].split("\\.");
				for (int b = 0; b < syllElems.length; b++) {
					if (syllElems[b].length() > 0) {
						SyllStress syllStressObj = new SyllStress();
						syllStressObj.setWord(wordBaseformPair[0]);
						syllStressObj.setSyll(syllElems[b]);
						syllStressObj.setWordStress(false);
						syllStressObj.setSyllStress(false);
						syllStressArray.add(syllStressObj);
					}
				}
			}
		}
	}

	/**
	 * Constructor for creating a new syllStressArray object containing the
	 * information given by the user in the stress tuning interaction.
	 * 
	 * @param stressEditor
	 *            - String representing the information contained in the text
	 *            based stress tuning interaction.
	 */
	public SyllStressTuning(String stressEditor) {
		syllStressArray = new ArrayList<SyllStress>();
		String[] syllLineElems = stressEditor.split("\n");
		String word = "";
		String wordStress = "";
		for (int a = 0; a < syllLineElems.length; a++) {
			String[] wordSyllStressElems = syllLineElems[a].split("\t");
			SyllStress syllStressObj = new SyllStress();
			if (syllLineElems[a].startsWith("\t")) {
				syllStressObj.setWord(word);
				syllStressObj.setWordStress(auxillary.StringOperations
						.stringToBool(wordStress));
				syllStressObj.setSyll(wordSyllStressElems[1]);
				syllStressObj.setSyllStress(auxillary.StringOperations
						.stringToBool(wordSyllStressElems[2]));
			} else {
				word = wordSyllStressElems[0];
				wordStress = wordSyllStressElems[3];
			}
			if (syllStressObj.getWord() != null) {
				syllStressArray.add(syllStressObj);
			}
		}
	}

	/**
	 * SyllStressTuning.getSyllStressArray takes as argument
	 * 
	 * @param selectionPos
	 *            - String containing a subset of the users input
	 * @return - ArrayList of SyllStress objects containing the current stress
	 *         information for the selection
	 */
	public static ArrayList<SyllStress> getSyllStressArray(String selectionPos) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SyllStressTuning.getSyllStressArray " + selectionPos;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<SyllStress> returnList = new ArrayList<SyllStress>();
		String[] selectionElems = selectionPos.split("\t");
		int start = Integer.parseInt(selectionElems[0]);
		int end = Integer.parseInt(selectionElems[1]);
		String selection = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult().substring(start, end);
		int sentenceID = 0;
		String content = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		String[] contentElems = content.split("\n");
		int currentLength = 0;
		for (int a = 0; a < contentElems.length; a++) {
			currentLength = currentLength + contentElems[a].length();
			if (currentLength > start && currentLength < end) {
				sentenceID = a;
			}
		}
		logInfo = selection;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String phonetisedCont = PhoneticTuning.phonetiseString(selection);
		String[] phonElems = phonetisedCont.split("\n");
		for (int a = 0; a < phonElems.length; a++) {
			String[] wordBaseformPair = phonElems[a].split("\t");
			String[] syllElems = wordBaseformPair[1].split("\\.");
			for (int b = 0; b < syllElems.length; b++) {
				if (syllElems[b].length() > 0) {
					SyllStress syllStressObj = new SyllStress();
					syllStressObj.setWord(wordBaseformPair[0]);
					syllStressObj.setSyll(syllElems[b]);
					syllStressObj.setWordStress(false);
					syllStressObj.setSyllStress(false);
					syllStressObj.setSentenceID("" + sentenceID);
					returnList.add(syllStressObj);
				}
			}
		}
		return returnList;
	}

	public static String createTxtTuningData(String words, int sentenceID) {
		syllStressArray = new ArrayList<SyllStress>();

		String phonetisedCont = PhoneticTuning.phonetiseString(words);
		String[] phonElems = phonetisedCont.split("\n");
		for (int a = 0; a < phonElems.length; a++) {
			String[] wordBaseformPair = phonElems[a].split("\t");
			String[] syllElems = wordBaseformPair[1].split("\\.");
			for (int b = 0; b < syllElems.length; b++) {
				if (syllElems[b].length() > 0) {
					SyllStress syllStressObj = new SyllStress();
					syllStressObj.setWord(wordBaseformPair[0]);
					syllStressObj.setSyll(syllElems[b]);
					syllStressObj.setWordStress(false);
					syllStressObj.setSyllStress(false);
					syllStressObj.setSentenceID("" + sentenceID);
					syllStressArray.add(syllStressObj);
				}
			}
		}
		String returnString = "<wordList>\n";
		String currentWord = "";
		for (int a = 0; a < syllStressArray.size(); a++) {
			if (syllStressArray.get(a).getWord().equals(currentWord)) {
				returnString = returnString + "<syllable>\n";
				returnString = returnString + "<value="
						+ syllStressArray.get(a).getSyll() + "/>\n";
				returnString = returnString + "<emph="
						+ syllStressArray.get(a).isSyllStress() + "/>\n";
				returnString = returnString + "</syllable>\n";
			} else {
				if (currentWord.length() > 0) {
					returnString = returnString + "</syllableList>\n";
					returnString = returnString + "</word>\n";
				}
				currentWord = syllStressArray.get(a).getWord();
				returnString = returnString + "<word>\n";
				returnString = returnString + "<value=" + currentWord + "/>\n";
				returnString = returnString + "<emph="
						+ syllStressArray.get(a).isWordStress() + "/>\n";
				returnString = returnString + "<syllableList>\n";
				returnString = returnString
						+ "<syllableList>\n<syllable>\n<value="
						+ syllStressArray.get(a).getSyll() + "/>\n";
				returnString = returnString + "<emph="
						+ syllStressArray.get(a).isSyllStress() + "/>\n";
				returnString = returnString + "</syllable>";
			}
		}
		returnString = returnString + "</syllableList>\n";
		returnString = returnString + "</word>\n";
		returnString = returnString + "</wordList>";
		return returnString;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for data objects.
	 */
	/**
	 * @param stressString
	 *            the stressString to set
	 */
	public void setStressString(String stressString) {
		this.stressString = stressString;
	}

	/**
	 * @return the stressString
	 */
	public String getStressString() {
		return stressString;
	}

	/**
	 * @param syllStressArray
	 *            the syllStressArray to set
	 */
	public void setSyllStressArray(ArrayList<SyllStress> syllStressArray) {
		this.syllStressArray = syllStressArray;
	}

	/**
	 * @return the syllStressArray
	 */
	public ArrayList<SyllStress> getSyllStressArray() {
		return syllStressArray;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/

}
