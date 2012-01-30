/**
 * LexiconService contains methods for dealing with the information in the 
 * lexicon interface. These currently are:
 * 
 * removeRowAction
 * initNewLex
 * addRowAction
 * saveLexicon
 * suggestPhoneticAction
 * extractBaseformFromLex
 * 
 * Additionally it contains methods for extracting information from the
 * lexicon interface. These currently are:
 * 
 * lexiconList
 * table
 * useSelection
 * showLexicon
 */
package lexicon;

import java.util.ArrayList;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import popups.PopupManager;

import settings.userSettings.UserSettings;
import tuning.PhoneticTuning;
import editor.EditorManager;

/**
 * lexicon.LexiconService offers methods to interact with the lexicon interface.
 * These currently are:
 * 
 * removeRowAction - to remove lines from the table initNewLex - create a new,
 * empty lexicon addRowAction - adding a new row for further input saveLexicon -
 * saving the lexicon to the server suggestPhoneticAction - automatically
 * phonetise all words that as of now do not have a phonetic transcription
 * extractBaseformFromLex - for extracting the phonetic transcription from the
 * lexicon
 * 
 * Additionally it offers methods for accessing information in the lexicon
 * interface. These currently are:
 * 
 * lexiconList - ArrayList of Lexicon objects table - UIData a representation of
 * the table displaying the lexicon entries useSelection - boolean value whether
 * the currently displayed lexicon should be used during synthesis or not
 * showLexicon - boolean value to show the lexicon interface
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         11.10.2010 MaMi
 */
public class LexiconService {
	private static ArrayList<Lexicon> lexiconList;
	private UIData table;
	private boolean useSelection = false;
	public static boolean showLexicon;

	/**
	 * Empty constructor for creating a new lexicon, which also contains two
	 * examples for entries - one with an abbreviation and one with just a word
	 * and its transcription.
	 */
	public LexiconService() {
		lexiconList = new ArrayList<Lexicon>();
		lexiconList.add(new Lexicon("bsp", "beispiel", "baischpihl",
				"b aI S p i: l"));
		lexiconList.add(new Lexicon("blubb", "", "", "b l U p"));
		useSelection = true;
	}

	public void synthesizeAction(ActionEvent event) {
		int index = table.getRowIndex();
		Lexicon element = lexiconList.get(index);
		if (element.getPhonetic().length() > 0) {
			synthesis.SynthesisManager.get().synthesize(element.getPhonetic(),
					"phonetic");
		}
		if (element.getWord().length() > 0) {
			synthesis.SynthesisManager.get().synthesize(element.getWord(),
					"word");
		}
		if (element.getFull().length() > 0) {
			synthesis.SynthesisManager.get().synthesize(element.getWord(),
					"word");
		}
		if (element.getSoundslike().length() > 0) {
			synthesis.SynthesisManager.get().synthesize(element.getWord(),
					"word");
		}

	}

	/**
	 * LexiconService.removeRowAction takes as parameters
	 * 
	 * @param event
	 *            - ActionEvent that caused the action to be triggered. The
	 *            method itself removes the specifically requested row from the
	 *            table displayed in the lexicon interface.
	 */
	public void removeRowAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "LexiconService.removeRowAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		int index = table.getRowIndex();
		lexiconList.remove(index);
	}

	/**
	 * LexiconService.initNewLex takes as parameters
	 * 
	 * @param event
	 *            - ActionEvent that triggered the execution of this method. It
	 *            clears the current lexicon interface and creates a new, empty
	 *            one and displays the lexicon interface.
	 */
	public void initNewLex(ActionEvent event) {

		lexiconList.clear();
		lexiconList.add(new Lexicon("", "", "", ""));
		lexiconList.add(new Lexicon("", "", "", ""));
		showLexicon = true;
	}

	/**
	 * LexiconService.addRowAction takes as parameters
	 * 
	 * @param event
	 *            - ActionEvent that triggered the execution of this method,
	 *            which adds a new, emtpy row to the lexicon.
	 */
	public void addRowAction(ActionEvent event) {
		lexiconList.add(new Lexicon("", "", "", ""));
	}

	/**
	 * LexiconService.saveLexicon takes as arguments
	 * 
	 * @param saveName
	 *            - String representing the fileName with which the lexicon will
	 *            be saved
	 * @param lexContent
	 *            = ArrayList of Lexicon objects representing the content in the
	 *            lexicon interface.
	 */
	public static void saveLexicon(String saveName,
			ArrayList<Lexicon> lexContent) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "LexiconService.saveLexicon";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<String> fileContent = new ArrayList<String>();
		fileContent = auxillary.XMLMethods.prepareLexHeader(fileContent,
				saveName);
		for (int a = 0; a < lexContent.size(); a++) {
			String itemStart = "<item>";
			fileContent.add(itemStart);
			String wordEntry = auxillary.XMLMethods.createEntry(
					lexContent.get(a).getWord(), "word");
			fileContent.add(wordEntry);
			String fullEntry = auxillary.XMLMethods.createEntry(
					lexContent.get(a).getFull(), "full");
			fileContent.add(fullEntry);
			String soundslikeEntry = auxillary.XMLMethods.createEntry(
					lexContent.get(a).getSoundslike(), "soundslike");
			fileContent.add(soundslikeEntry);
			String phoneticEntry = auxillary.XMLMethods.createEntry(lexContent
					.get(a).getPhonetic(), "phonetic");
			fileContent.add(phoneticEntry);
			String itemEnd = "</item>";
			fileContent.add(itemEnd);
			logInfo = lexContent.get(a).getWord() + " ";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			logInfo = lexContent.get(a).getFull() + " ";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			logInfo = lexContent.get(a).getSoundslike() + " ";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			logInfo = lexContent.get(a).getPhonetic() + " ";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		auxillary.FileIOMethods.writeListToFile(fileContent, saveName);
	}

	/**
	 * LexiconService.noEntries takes as arguments
	 * 
	 * @param lexContent
	 *            ArrayList of Lexicon entries and
	 * @return boolean representing the information whether the list is empty or
	 *         not.
	 */
	public static boolean noEntries(ArrayList<Lexicon> lexContent) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "LexiconService.noEntries";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String testString = "";
		logInfo = "" + lexContent.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		for (int a = 0; a < lexContent.size(); a++) {
			testString = testString + lexContent.get(a).getWord();
			testString = testString + lexContent.get(a).getFull();
			logInfo = "?? " + testString;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		logInfo = "LENGTH " + testString.trim().length();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (testString.trim().length() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * LexiconService.suggestPhoneticAction takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent that triggered the execution of the method,
	 *            which takes the content in the lexicon interface (lexiconList
	 *            and for each entry that does not yet has a phonetic or
	 *            soundslike transcription the phonetic transcription is
	 *            automatically created using a g2p method.
	 */
	public void suggestPhoneticAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "LexiconService.suggestPhoneticAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<String> wordList = new ArrayList<String>();
		for (int a = 0; a < lexiconList.size(); a++) {
			if (lexiconList.get(a).getPhonetic().length() < 1) {
				String word = lexiconList.get(a).getWord();
				wordList.add(word.toLowerCase());
			}
		}
		String phonString = PhoneticTuning
				.phonetiseString(((EditorManager) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("editor")).getResult());
		String[] wordBaseformPairs = phonString.split("\n");
		for (int a = 0; a < wordBaseformPairs.length; a++) {
			String word = wordBaseformPairs[a].split("\t")[0];
			String baseform = wordBaseformPairs[a].split("\t")[1];
			logInfo = word + " " + baseform;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			for (int b = 0; b < lexiconList.size(); b++) {
				logInfo = lexiconList.get(b).getWord() + " " + word;
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				if (lexiconList.get(b).getWord().equalsIgnoreCase(word)) {
					lexiconList.get(b).setPhonetic(baseform);
				}
			}
		}
	}

	/**
	 * LexiconService.extractBaseformFromLex takes as arguments
	 * 
	 * @param wordBaseformPair
	 *            - String containing a word and its phonetic transcription.
	 * @return - String containing the phonetic transcription extracted from the
	 *         lexicon.
	 */
	public static String extractBaseformFromLex(String wordBaseformPair) {
		String word = wordBaseformPair.split("\t")[0];
		for (int a = 0; a < lexiconList.size(); a++) {
			Lexicon item = lexiconList.get(a);
			if (item.getWord().equalsIgnoreCase(word)) {
				return item.getPhonetic();
			}
		}
		return null;
	}

	/**
	 * LexiconService.openLexicon takes as argument
	 * 
	 * @param saveName
	 *            - string representing the lexicon to be opened.
	 * 
	 *            and displays the content of the saved data in the lexicon tab.
	 */
	public static void openLexicon(String saveName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "LexiconService.openLexicon " + saveName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<String> lexContent = auxillary.FileIOMethods
				.getFileContent(saveName);
		lexiconList.clear();
		if (lexContent == null || lexContent.size() < 1) {
			PopupManager pm = new PopupManager();
			pm.setArbitraryErrorValue("Lexicon is empty!");
			pm.setShowArbitraryError(true);
		} else {
			for (int a = 0; a < lexContent.size(); a++) {
				logInfo = "Line " + lexContent.get(a).toString();
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				if (lexContent.get(a).toString().startsWith("<item>")) {
					Lexicon lexEntry = Lexicon.extractLexiconEntryFromFile(
							lexContent, a);
					if (lexEntry != null) {
						logInfo = "LexEntry is not NULL";
						auxillary.FileIOMethods.appendStringToFile(logInfo,
								logFile);
						lexiconList.add(lexEntry);
					} else {
						logInfo = "LexEntry IS null";
						auxillary.FileIOMethods.appendStringToFile(logInfo,
								logFile);

					}
				}
			}
		}
		showLexicon = true;
	}

	/**
	 * LexiconService.findEntry takes as argument
	 * 
	 * @param word
	 *            - string representing the word to be found.
	 * @return int - representing the position within the lexicon where the word
	 *         has been found. If it was not found, -1 is returned.
	 */
	public static int findEntry(String word) {
		for (int a = 0; a < lexiconList.size(); a++) {
			if (word.equalsIgnoreCase(lexiconList.get(a).getWord())) {
				return a;
			}
			if (word.equalsIgnoreCase(lexiconList.get(a).getFull())) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * LexiconService.phonetiseLexicon takes the lexicon and checks for existing
	 * baseforms. In case an entry does not have a baseform it will be created
	 * using a g2p method. If the entry contains a soundslike description of a
	 * word, this is used to produce the phonetic transcription.
	 */
	public static void phonetiseLexicon() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.phoneticTuning";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Lexicon> lexiconList = ((LexiconService) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("table")).getLexiconList();
		for (int a = 0; a < lexiconList.size(); a++) {
			String word = lexiconList.get(a).getWord();
			String full = lexiconList.get(a).getFull();
			String soundslike = lexiconList.get(a).getSoundslike();
			String baseform = lexiconList.get(a).getPhonetic();
			if (baseform.length() < 1) {
				String missingWord;
				if (soundslike.length() > 1) {
					missingWord = soundslike;
				} else {
					if (full.length() > 1) {
						missingWord = full;
					} else {
						missingWord = word;
					}
				}
				String phonetisedElem = PhoneticTuning
						.phonetiseString(missingWord);
				String[] phonElems = phonetisedElem.split("\t");
				logInfo = "PhonElems " + phonElems.length;
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				lexiconList.get(a).setPhonetic(phonetisedElem.split("\t")[1]);
			}
		}
	}
	
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for LexiconServices.
	 */
	/**
	 * @param lexiconList
	 *            the lexiconList to set
	 */
	@SuppressWarnings("static-access")
	public void setLexiconList(ArrayList<Lexicon> lexiconList) {
		this.lexiconList = lexiconList;
	}

	/**
	 * @return the lexiconList
	 */
	public ArrayList<Lexicon> getLexiconList() {
		return lexiconList;
	}

	/**
	 * @param table
	 *            the table to set
	 */
	public void setTable(UIData table) {
		this.table = table;
	}

	/**
	 * @return the table
	 */
	public UIData getTable() {
		return table;
	}

	/**
	 * @param useSelection
	 *            the useSelection to set
	 */
	public void setUseSelection(boolean useSelection) {
		this.useSelection = useSelection;
	}

	/**
	 * @return the useSelection
	 */
	public boolean isUseSelection() {
		return useSelection;
	}

	/**
	 * @param showLexicon
	 *            the showLexicon to set
	 */
	public void setShowLexicon(boolean showLexicon) {
		this.showLexicon = showLexicon;
	}

	/**
	 * @return the showLexicon
	 */
	public boolean isShowLexicon() {
		return showLexicon;
	}

	/**************************************************************
	 * /**************************************************************
	 * ************************************************************
	 **************************************************************/


}
