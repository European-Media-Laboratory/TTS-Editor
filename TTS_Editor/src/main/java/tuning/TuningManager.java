/**
 * TuningManager contains methods for dealing with the tuning information
 * entered by the user. These currently are:
 * 
 * addToLexicon
 * phoneticTuning
 * stressTuning
 * extractPhoneticInfo
 * extractStressInfo
 * syllStressInfo
 * langTuning
 * selection
 * modeChange
 * phoneticTable
 * phoneticEditor
 * stressTable
 * stressEditor
 * pauseInsertion
 * 
 * Additionally, it contains methods for accessing tuning information. These
 * currently are:
 * 
 * showPhonetic
 * showStress
 * size
 * array
 * phoneticArray
 * stressArray
 * phoneticTable
 * 
 */
package tuning;

import java.util.ArrayList;
import java.util.Date;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import popups.PopupManager;

import lexicon.Lexicon;
import lexicon.LexiconService;
import settings.userSettings.UserSettings;
import synthesis.SynthesisManager;
import data.Sentences;
import data.Syllables;
import data.Words;
import editor.EditorManager;

/**
 * EMLTTSEditorWeb.tuning.TuningManager.java offers methods for dealing with the
 * tuning methods offered to the user. These currently are:
 * 
 * addToLexicon - allows for adding an element from the phonetic tuning directly
 * to the lexicon for saving phoneticTuning - allows access to the phonetic
 * tuning methods stressTuning - allows access to the stress tuning methods
 * extractPhoneticInfo - extracts the user input from the phonetic tuning
 * extractStressInfo - extracts the user input from the stress tuning
 * 
 * Additionally, it offers to access information specific to the tuningManager.
 * These currently are:
 * 
 * showPhonetic - boolean which determines whether the phonetic tuning is shown
 * showStress - boolean which determines whether the stress tuning is shown size
 * - the size for either stress or phonetic tuning phoneticArray - the
 * information for the phonetic tuning stressArray - the information for the
 * sterss tuning phoneticTable - the table that contains the phonetic tuning
 * data
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.10.2010 MaMi
 * 
 *         Added tuning methods:
 * 
 *         showSyllStress - boolean which determines whether the stress tuning
 *         is done syllable wise showLangTuning - boolean which determines
 *         whether to show the language tuning options or not changeSelection -
 *         String which contains words from the input selected by the user using
 *         the mouse modeChage - String containing information about whether to
 *         show the GUI based tuning or the text based tuning pauseLength -
 *         String representing the length of inserted pause
 * 
 * 
 *         30.11.2010 MaMi
 */
public class TuningManager {

	private boolean showPhonetic = false;
	private boolean showStress = false;
	private String size;
	private String array;
	private ArrayList<Phonetic> phoneticArray;
	private ArrayList<Stress> stressArray;
	private ArrayList<SyllStress> syllStressArray;
	private UIData phoneticTable;
	public boolean showPhonetiseListError = false;
	private String syllablesSize;
	private boolean showLangTuning = false;
	private ArrayList<LangTune> langTuneArray;
	private String selection;
	private String modeChange = "Text";
	private boolean renderPhoneticTable = true;
	private boolean renderPhoneticEditor = false;
	private String editor;
	private boolean renderStressTable = true;
	private boolean renderStressEditor = false;
	private String stressEditor;
	private boolean renderLanguageTable = true;
	private boolean renderLanguageEditor = false;
	private String languageEditor;
	private boolean showPauseInsertion = false;
	private String pauseLength;
	private boolean showPlayer = false;
	private String flashvars = "";
	private boolean showRecording = false;

	/**
	 * TuningManager.changeSelection takes as argument
	 * 
	 * @param event
	 * 
	 *            and changes the selection.
	 */
	public void changeSelection(ValueChangeEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "CHANGE Selection " + (String) event.getNewValue();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		selection = (String) event.getNewValue();
		if (checkSelection() == false) {
			selection = null;
		}
	}

	/**
	 * TuningManager.checkSelection
	 * 
	 * @return
	 * 
	 *         returns whether the text in the editor contains the selection or
	 *         not.
	 */
	private boolean checkSelection() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.checkSelection";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		String text = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		auxillary.FileIOMethods.appendStringToFile(text, logFile);

		String selectionString = auxillary.StringOperations.extractSelection(
				selection, text);
		auxillary.FileIOMethods.appendStringToFile(selectionString, logFile);
		if (text != null) {
			if (text.contains(selectionString)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Empty constructor for the TuningManager class
	 */
	public TuningManager() {
	}

	/**
	 * TuningManager.synthesizeAction takes as argument
	 * 
	 * @param event
	 * 
	 *            and synthesizes the phonetic transcription of the selected
	 *            word.
	 */
	public void synthesizeAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		int index = phoneticTable.getRowIndex();
		Phonetic element = phoneticArray.get(index);
		String mp3FileName = SynthesisManager.get().synthesize(
				element.getBaseform(), "baseform");
		mp3FileName = mp3FileName.replace("/var/www", "");
		String timestamp = "?timestamp=" + (new Date()).getTime();
		String logInfo = "Setting Flashvars to " + mp3FileName + timestamp;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		flashvars = "file="
				+ mp3FileName
				+ timestamp
				+ "&type=sound&autostart=true&stretching=fill&respectduration=true";
		setShowPlayer(true);
		auxillary.FileIOMethods.appendStringToFile(
				"new FlashVars " + flashvars, logFile);
	}

	/**
	 * TuningManager.addToLexicon takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent that triggered calling this function, which
	 *            allows to take an element from the phonetic tuning
	 *            (word/baseform pair) and copy it to the lexicon interface.
	 */
	public void addToLexicon(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.addToLexicon AddElement to Lexicon";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		int index = phoneticTable.getRowIndex();
		Phonetic element = phoneticArray.get(index);
		logInfo = ""
				+ FacesContext.getCurrentInstance().getExternalContext()
						.getSessionMap().containsKey("table");
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (lexicon.LexiconService.showLexicon == false) {
			lexicon.LexiconService.showLexicon = true;
		}
		ArrayList<Lexicon> lexList = ((LexiconService) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("table")).getLexiconList();
		Lexicon newElem = new Lexicon();
		newElem.setWord(element.getWord());
		newElem.setFull("");
		newElem.setSoundslike("");
		newElem.setPhonetic(element.getBaseform());
		lexList.add(newElem);
	}

	/**
	 * TuningManager.phoneticTuning takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent that called this method, which shows the
	 *            phonetic tuning panel and the phonetic representation of the
	 *            text which is currently in the editor. Additionally, it allows
	 *            to phonetise lists of words, when entered in the lexicon
	 *            interface.
	 */
	public void phoneticTuning(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.phoneticTuning";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "CompID " + event.getComponent().getId();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (event.getComponent().getId().equals("phonetic")
				|| event.getComponent().getId().equals("contextPhonTuning")) {
			logInfo = "ShowPhonetic ??? " + showPhonetic;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			logInfo = "Selection " + selection;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

			String phoneticString;
			if (selection.length() > 0) {
				phoneticString = PhoneticTuning.phonetiseString(selection);
			} else {
				phoneticString = PhoneticTuning
						.phonetiseString(((EditorManager) FacesContext
								.getCurrentInstance().getExternalContext()
								.getSessionMap().get("editor")).getResult());
			}

			logInfo = "Result " + phoneticString;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			phoneticArray = new ArrayList<Phonetic>();
			if (phoneticString == null || phoneticString.length() < 1) {
				PopupManager pm = new PopupManager();
				pm.setArbitraryErrorValue("No phonetic String!");
				pm.setShowArbitraryError(true);
			} else {
				String[] phoneticStringArray = phoneticString.split("\n");

				for (int a = 0; a < phoneticStringArray.length; a++) {
					String[] phonElems = phoneticStringArray[a].split("\t");
					logInfo = phoneticStringArray[a] + " add to List!";
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					phoneticArray.add(new Phonetic(phonElems[0], phonElems[1]));
				}
				size = "" + getPhoneticArray().size();
				logInfo = "size " + size;
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				showPhonetic = true;
			}
			if (event.getComponent().getId().equals("phonetiseList")) {
				LexiconService.phonetiseLexicon();
			}
		}
	}

	/**
	 * TuningManager.stressTuning takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent that called the method, which shows the stress
	 *            tuning panel and allows for manipulating the stress
	 *            information.
	 */
	public void stressTuning(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.showStress";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (showStress == true) {
		}
		logInfo = selection;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Selection Length " + selection.length();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (selection.length() > 0) {
			syllStressArray = SyllStressTuning.getSyllStressArray(selection);
			selection = "";
		} else {
			SyllStressTuning tuning = new SyllStressTuning();
			syllStressArray = tuning.getSyllStressArray();
		}
		size = "" + syllStressArray.size();
		showStress = true;
	}

	/**
	 * TuningManager.langTuning takes as argument
	 * 
	 * @param event
	 * 
	 *            and displays the tab where the language for each word can be
	 *            changed.
	 */
	public void langTuning(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.Show Language Tuning";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (isShowLangTuning() == true) {
			getLangTuneArray().clear();
		}

		if (selection.length() > 0) {
			langTuneArray = LangTuning.selectionLangTuning(selection);
		} else {
			LangTuning tuning = new LangTuning();
			langTuneArray = tuning.getLangTuning();
		}
		logInfo = "Size: " + langTuneArray.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		size = "" + langTuneArray.size();
		showLangTuning = true;
		selection = "";
	}

	/**
	 * TuningManager.extractPhoneticInfo takes as arguments
	 * 
	 * @param wordList
	 *            - ArrayList of Word objects containing the word information
	 *            and creates the according phonetic transcription.
	 */
	public void extractPhoneticInfo(ArrayList<Words> wordList) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.extractPhoneticInfo";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (showPhonetic == true) {
			showPhonetic = false;
			phoneticArray = null;
		}
		phoneticArray = new ArrayList<Phonetic>();
		for (int a = 0; a < wordList.size(); a++) {
			logInfo = wordList.get(a).getValue() + " "
					+ wordList.get(a).getPhonetic();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			Words word = wordList.get(a);
			phoneticArray
					.add(new Phonetic(word.getValue(), word.getPhonetic()));
		}
		size = "" + phoneticArray.size();
		showPhonetic = true;
	}

	/**
	 * TuningManager.extractLangInfo takes as argument
	 * 
	 * @param wordList
	 *            - a list of word elements representing the words
	 * 
	 *            and checks whether any language tuning was performed on the
	 *            list of words.
	 */
	public void extractLangInfo(ArrayList<Words> wordList) {
		if (showLangTuning == true) {
			setShowLangTuning(false);
			getLangTuneArray().clear();
		}
		setLangTuneArray(new ArrayList<LangTune>());
		for (int a = 0; a < wordList.size(); a++) {
			Words word = wordList.get(a);
			getLangTuneArray().add(
					new LangTune(word.getValue(), word.getLanguage()));
		}
		size = "" + langTuneArray.size();
		showLangTuning = true;
	}

	/**
	 * TuningManager.extractStressInfo takes as arguments
	 * 
	 * @param syllableList
	 * @param wordList
	 * @param sentList
	 *            - ArrayList of Word objects and for each element in this list
	 *            it is noted whether the word is stressed or not.
	 */
	public void extractStressInfo(ArrayList<Syllables> syllableList,
			ArrayList<Words> wordList, ArrayList<Sentences> sentList) {
		if (showStress == true) {
			setShowStress(false);
			getStressArray().clear();
		}
		setSyllStressArray(new ArrayList<SyllStress>());
		int syllableCounter = 0;
		int wordCounter = 0;
		for (int a = 0; a < sentList.size(); a++) {
			int sentID = Integer.parseInt(sentList.get(a).getId());

			String[] sentValue = sentList.get(a).getValue().split(" ");
			int end = wordCounter + sentValue.length;
			for (int b = wordCounter; b < end; b++)
			{
				if (b < wordList.size()) {
					String word = wordList.get(b).getValue();
					String phonetic = wordList.get(b).getPhonetic();
					String[] phoneticElems = phonetic.split("\\.");
					boolean wordStress = wordList.get(b).isEmph();
					for (int c = 0; c < phoneticElems.length; c++) {
						if (phoneticElems[c].trim().length() > 1) {
							Syllables syllable = syllableList
									.get(syllableCounter);
							getSyllStressArray().add(
									new SyllStress(syllable.getValue(), word,
											wordStress, syllable.isEmph(),
											sentID));
							syllableCounter = syllableCounter + 1; // TEST!
						}
					}
				}
			}
			wordCounter = wordCounter + sentValue.length;
		}
		size = "" + getSyllStressArray().size();
		showStress = true;
	}

	/**
	 * TuningManager.closeTabAction takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering this action, which based on the event
	 *            calling this event closes the appropriate tab.
	 */
	public void closeTabAction(ActionEvent event) {
		if (event.getComponent().getId().equals("closePhoneticTuningTab")) {
			showPhonetic = false;
		}
		if (event.getComponent().getId().equals("closeStressTuningTab")) {
			showStress = false;
		}
		if (event.getComponent().getId().equals("closeLanguageTuningTab")) {
			showLangTuning = false;
		}
	}

	/**
	 * TuningManager.closePopupAction takes no argument and results in closing
	 * the popup windows displayed.
	 */
	public void closePopupAction() {
		showPhonetic = false;
		showPauseInsertion = false;
	}

	/**
	 * TuningManager.changeModeAction takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering this action, which changes the
	 *            interaction mode from GUI to Text depending on the tuning
	 *            currently used.
	 */
	public void changeModeAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.changeModeAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Mode " + modeChange;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		if (event.getComponent().getId().equals("modeChangeButtonPhonetic")) {
			changePhoneticMode();
		}
		if (event.getComponent().getId().equals("modeChangeButtonStress")) {
			changeStressMode();
		}
		if (event.getComponent().getId().equals("modeChangeButtonLanguage")) {
			changeLanguageMode();
		}
	}

	/**
	 * TuningManager.changeLanguageMode takes no arguments and depending on the
	 * current interaction mode sets the interaction mode to the other (GUI vs.
	 * Text) and displays the current information appropriately.
	 */
	private void changeLanguageMode() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningMananger.changeLanguageMode";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (modeChange.equalsIgnoreCase("Text")) {
			languageEditor = "";
			for (int a = 0; a < langTuneArray.size(); a++) {
				languageEditor = languageEditor
						+ langTuneArray.get(a).getWord() + "\t"
						+ langTuneArray.get(a).getLang() + "\n";
			}
			languageEditor = languageEditor.trim();
			renderLanguageTable = false;
			renderLanguageEditor = true;
			modeChange = "GUI";
		} else {
			if (modeChange.equalsIgnoreCase("GUI")) {
				LangTuning tuning = new LangTuning(languageEditor);
				langTuneArray = tuning.getLangTuning();
				size = "" + langTuneArray.size();
				renderLanguageEditor = false;
				renderLanguageTable = true;
				modeChange = "Text";
			}
		}
	}

	/**
	 * TuningManager.changeStressMode takes no arguments and depending on the
	 * current interaction mode sets the interaction mode to the other (GUI vs.
	 * Text) and displays the current information appropriately.
	 */
	private void changeStressMode() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.changeStressMode " + modeChange;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (modeChange.equalsIgnoreCase("Text")) {
			logInfo = "Is Text " + syllStressArray.size();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			renderStressTable = false;
			stressEditor = "";
			stressEditor.trim();
			renderStressEditor = true;
			modeChange = "GUI";
		} else {
			if (modeChange.equalsIgnoreCase("GUI")) {
				logInfo = "Is GUI";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				renderStressEditor = false;
				size = "" + syllStressArray.size();
				modeChange = "Text";
				renderStressTable = true;
			}
		}
	}

	/**
	 * TuningManager.changePhoneticMode takes no arguments and depending on the
	 * current interaction mode sets the interaction mode to the other (GUI vs.
	 * Text) and displays the current information appropriately.
	 */
	private void changePhoneticMode() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningMananger.changePhoneticMode";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Is GUI BlaBlubberTuet!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Get EditorBasedTuning.TransformEditorToGUIPhonetic";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		size = "" + getPhoneticArray().size();
		logInfo = "size " + size;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		renderPhoneticTable = true;
		renderPhoneticEditor = false;
		modeChange = "Text";
	}

	/**
	 * TuningManager.pauseTuningAction takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering this action, which allows the user to
	 *            enter information about a pause to be inserted in the
	 *            synthesized speech.
	 */
	public void pauseTuningAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.pauseTuningAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "insert Pause!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = selection;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setShowPauseInsertion(true);
	}

	/**
	 * TuningManager.closePauseInsertionAction takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering the action, which causes the popup
	 *            dialog with the user to be closed.
	 */
	public void closePauseInsertionAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.closePauseInsertionAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Pause Length " + pauseLength;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = selection;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Insert Pause Information in Text!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String text = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		logInfo = "Selection " + selection;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		int pos = Integer.parseInt(selection.split("\t")[1]);
		String newText = text.substring(0, pos) + " PAUSE(" + pauseLength
				+ ") " + text.substring(pos);
		((EditorManager) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("editor")).setResult(newText);
		setShowPauseInsertion(false);
	}

	/**
	 * TuningManager.recording takes as argument
	 * 
	 * @param event
	 * 
	 *            and triggers the display of the recording functionality.
	 */
	public void recording(ActionEvent event) {
		showRecording = true;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for TuningManager.
	 */
	/**
	 * @param showPhonetic
	 *            the showPhonetic to set
	 */
	public void setShowPhonetic(boolean showPhonetic) {
		this.showPhonetic = showPhonetic;
	}

	/**
	 * @return the showPhonetic
	 */
	public boolean isShowPhonetic() {
		return showPhonetic;
	}

	/**
	 * @param showStress
	 *            the showStress to set
	 */
	public void setShowStress(boolean showStress) {
		this.showStress = showStress;
	}

	/**
	 * @return the showStress
	 */
	public boolean isShowStress() {
		return showStress;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param array
	 *            the array to set
	 */
	public void setArray(String array) {
		this.array = array;
	}

	/**
	 * @return the array
	 */
	public String getArray() {
		return array;
	}

	/**
	 * @param phoneticArray
	 *            the phoneticArray to set
	 */
	public void setPhoneticArray(ArrayList<Phonetic> phoneticArray) {
		this.phoneticArray = phoneticArray;
	}

	/**
	 * @return the phoneticArray
	 */
	public ArrayList<Phonetic> getPhoneticArray() {
		return phoneticArray;
	}

	/**
	 * @param stressArray
	 *            the stressArray to set
	 */
	public void setStressArray(ArrayList<Stress> stressArray) {
		this.stressArray = stressArray;
	}

	/**
	 * @return the stressArray
	 */
	public ArrayList<Stress> getStressArray() {
		return stressArray;
	}

	/**
	 * @param phoneticTable
	 *            the phoneticTable to set
	 */
	public void setPhoneticTable(UIData phoneticTable) {
		this.phoneticTable = phoneticTable;
	}

	/**
	 * @return the phoneticTable
	 */
	public UIData getPhoneticTable() {
		return phoneticTable;
	}

	/**
	 * @param showPhonetiseListError
	 *            the showPhonetiseListError to set
	 */
	public void setShowPhonetiseListError(boolean showPhonetiseListError) {
		this.showPhonetiseListError = showPhonetiseListError;
	}

	/**
	 * @return the showPhonetiseListError
	 */
	public boolean isShowPhonetiseListError() {
		return showPhonetiseListError;
	}

	/**
	 * @param syllablesSize
	 *            the syllablesSize to set
	 */
	public void setSyllablesSize(String syllablesSize) {
		this.syllablesSize = syllablesSize;
	}

	/**
	 * @return the syllablesSize
	 */
	public String getSyllablesSize() {
		return syllablesSize;
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

	/**
	 * @param showLangTuning
	 *            the showLangTuning to set
	 */
	public void setShowLangTuning(boolean showLangTuning) {
		this.showLangTuning = showLangTuning;
	}

	/**
	 * @return the showLangTuning
	 */
	public boolean isShowLangTuning() {
		return showLangTuning;
	}

	/**
	 * @param langTuneArray
	 *            the langTuneArray to set
	 */
	public void setLangTuneArray(ArrayList<LangTune> langTuneArray) {
		this.langTuneArray = langTuneArray;
	}

	/**
	 * @return the langTuneArray
	 */
	public ArrayList<LangTune> getLangTuneArray() {
		return langTuneArray;
	}

	/**
	 * @param selection
	 *            the selection to set
	 */
	public void setSelection(String selection) {
		this.selection = selection;
	}

	/**
	 * @return the selection
	 */
	public String getSelection() {
		return selection;
	}

	/**
	 * @param modeChange
	 *            the modeChange to set
	 */
	public void setModeChange(String modeChange) {
		this.modeChange = modeChange;
	}

	/**
	 * @return the modeChange
	 */
	public String getModeChange() {
		return modeChange;
	}

	/**
	 * @param renderPhoneticTable
	 *            the renderPhoneticTable to set
	 */
	public void setRenderPhoneticTable(boolean renderPhoneticTable) {
		this.renderPhoneticTable = renderPhoneticTable;
	}

	/**
	 * @return the renderPhoneticTable
	 */
	public boolean isRenderPhoneticTable() {
		return renderPhoneticTable;
	}

	/**
	 * @param renderPhoneticEditor
	 *            the renderPhoneticEditor to set
	 */
	public void setRenderPhoneticEditor(boolean renderPhoneticEditor) {
		this.renderPhoneticEditor = renderPhoneticEditor;
	}

	/**
	 * @return the renderPhoneticEditor
	 */
	public boolean isRenderPhoneticEditor() {
		return renderPhoneticEditor;
	}

	/**
	 * @param editor
	 *            the editor to set
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}

	/**
	 * @return the editor
	 */
	public String getEditor() {
		return editor;
	}

	/**
	 * @param renderStressTable
	 *            the renderStressTable to set
	 */
	public void setRenderStressTable(boolean renderStressTable) {
		this.renderStressTable = renderStressTable;
	}

	/**
	 * @return the renderStressTable
	 */
	public boolean isRenderStressTable() {
		return renderStressTable;
	}

	/**
	 * @param renderStressEditor
	 *            the renderStressEditor to set
	 */
	public void setRenderStressEditor(boolean renderStressEditor) {
		this.renderStressEditor = renderStressEditor;
	}

	/**
	 * @return the renderStressEditor
	 */
	public boolean isRenderStressEditor() {
		return renderStressEditor;
	}

	/**
	 * @param stressEditor
	 *            the stressEditor to set
	 */
	public void setStressEditor(String stressEditor) {
		this.stressEditor = stressEditor;
	}

	/**
	 * @return the stressEditor
	 */
	public String getStressEditor() {
		return stressEditor;
	}

	/**
	 * @param renderLanguageTable
	 *            the renderLanguageTable to set
	 */
	public void setRenderLanguageTable(boolean renderLanguageTable) {
		this.renderLanguageTable = renderLanguageTable;
	}

	/**
	 * @return the renderLanguageTable
	 */
	public boolean isRenderLanguageTable() {
		return renderLanguageTable;
	}

	/**
	 * @param renderLanguageEditor
	 *            the renderLanguageEditor to set
	 */
	public void setRenderLanguageEditor(boolean renderLanguageEditor) {
		this.renderLanguageEditor = renderLanguageEditor;
	}

	/**
	 * @return the renderLanguageEditor
	 */
	public boolean isRenderLanguageEditor() {
		return renderLanguageEditor;
	}

	/**
	 * @param languageEditor
	 *            the languageEditor to set
	 */
	public void setLanguageEditor(String languageEditor) {
		this.languageEditor = languageEditor;
	}

	/**
	 * @return the languageEditor
	 */
	public String getLanguageEditor() {
		return languageEditor;
	}

	/**
	 * @param showPauseInsertion
	 *            the showPauseInsertion to set
	 */
	public void setShowPauseInsertion(boolean showPauseInsertion) {
		this.showPauseInsertion = showPauseInsertion;
	}

	/**
	 * @return the showPauseInsertion
	 */
	public boolean isShowPauseInsertion() {
		return showPauseInsertion;
	}

	/**
	 * @param pauseLength
	 *            the pauseLength to set
	 */
	public void setPauseLength(String pauseLength) {
		this.pauseLength = pauseLength;
	}

	/**
	 * @return the pauseLength
	 */
	public String getPauseLength() {
		return pauseLength;
	}

	/**
	 * @param showPlayer
	 *            the showPlayer to set
	 */
	public void setShowPlayer(boolean showPlayer) {
		this.showPlayer = showPlayer;
	}

	/**
	 * @return the showPlayer
	 */
	public boolean isShowPlayer() {
		return showPlayer;
	}

	/**
	 * @param flashvars
	 *            the flashvars to set
	 */
	public void setFlashvars(String flashvars) {
		this.flashvars = flashvars;
	}

	/**
	 * @return the flashvars
	 */
	public String getFlashvars() {
		return flashvars;
	}

	/**
	 * @param showRecording
	 *            the showRecording to set
	 */
	public void setShowRecording(boolean showRecording) {
		this.showRecording = showRecording;
	}

	/**
	 * @return the showRecording
	 */
	public boolean isShowRecording() {
		return showRecording;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
