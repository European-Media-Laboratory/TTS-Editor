/**
 * ProjectIOMethods contains methods for interacting with the project format,
 * which internally stores the data entered by the user. These currently are:
 * 
 * saveProject
 * writeSyllableFile
 * writeWordFile
 * writeTokenFile
 * writeSentenceFile
 * createSettingsInfo
 * setFileNames
 * openProject
 * 
 * The data is separated into different information types and saved in the 
 * respective files. A central project file contains information about the
 * id of the project and thereby points to the files containing the single
 * information types. This settings file also contains information about whether
 * the user was using various tuning options, that that upon opening a project
 * these can be opened as well. 
 * 
 * The data is stored in an xml-like format, which makes it easy to extract the
 * information and which would also allow to change it into another format if
 * necessary.
 * 
 * Added: 
 * 
 * getWordListContent
 */
package project;

import importTools.SubtitlingManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import lexicon.LexiconService;
import settings.userSettings.UserSettings;
import tuning.TuningManager;
import data.Constraints;
import data.Data;
import data.DataFactory;
import data.DataManager;
import data.Sentences;
import data.Syllables;
import data.Tokens;
import data.Words;
import editor.EditorManager;

/**
 * EMLTTSEditorWeb.project.ProjectIOMethods.java offers methods for interacting
 * with the internally saved project files, opening project files and saving
 * data to project files. These methods are currently:
 * 
 * saveProject - for saving data to the appropriate project file format on the
 * server side writeSyllableFile - for saving the syllable information to the
 * appropriate file format writeWordFile - for saving the word information to
 * the appropriate file format writeTokenFile - for saving the token information
 * to the appropriate file format writeSentenceFile - for saving the sentence
 * information to the appropriate file format createSettingsInfo - for saving
 * the project settings to the appropriate file format setFileNames - sets the
 * filenames for each information file according to the project name openProject
 * - open an earlier saved project and display in the editor
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         05.11.2010 MaMi
 * 
 *         Added:
 * 
 *         getWordListContent - for extracting the data from the ArrayList of
 *         Words objects into an ArrayList in xml-representation.
 */
public class ProjectIOMethods {

	static String settingsFile;
	static String sentFile;
	static String tokFile;
	static String wordsFile;
	static String syllFile;
	static String phonFile;
	static String lexFile;
	static String projectFile;
	private static ArrayList<SelectItem> tuningList;
	private static Data newData;

	static String id = "<id=";
	static String value = "<value=";
	static String phonetic = "<phonetic=";
	static String emph = "<emph=";
	static String type = "<type=";
	static String words = "<words=";
	static String tokens = "<tokens=";
	static String language = "<language=";
	static String end = "/>";

	/**
	 * ProjectIOMethods.saveProject takes as arguments
	 * 
	 * @param saveName
	 *            - String representing the name of the project and triggers
	 *            setting the appropriate filenames, extracting the information
	 *            from the editor environment and triggers saving the
	 *            information to the appropriate file formats.
	 * @param facesContext
	 */
	public static void saveProject(String saveName, FacesContext facesContext) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "project.ProjectIOMethods.saveProject";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		addProjectToSettings(saveName, facesContext);
		saveName = ((UserSettings) facesContext.getExternalContext()
				.getSessionMap().get("userSettings")).getUserPath()
				+ saveName;
		setFileNames(saveName);

		ArrayList<String> settingsInfo = createSettingsInfo(saveName);

		String editorResult = ((EditorManager) (facesContext
				.getExternalContext().getSessionMap().get("editor")))
				.getResult();
		if (editorResult.length() > 0) {
			Data newData = DataFactory.createNewData(editorResult);
			writeSenteceFile(newData.getSentenceList());
			writeTokenFile(newData.getTokenList());
			writeWordFile(newData.getWordList());
			writeSyllableFile(newData.getSyllableList());
		} else {
			auxillary.FileIOMethods.appendStringToFile(
					"ERROR no data in Editor!", logFile);
			auxillary.FileIOMethods.appendStringToFile("Saving anyway?",
					logFile);
			auxillary.FileIOMethods.appendStringToFile("If Yes: ", logFile);
			auxillary.FileIOMethods.appendStringToFile("Saving!", logFile);
			auxillary.FileIOMethods.appendStringToFile("If No", logFile);
		}
		ArrayList<Constraints> constraintList = ((SubtitlingManager) (facesContext
				.getExternalContext().getSessionMap().get("subtitlingManager")))
				.getConstraintList();
		auxillary.FileIOMethods.appendStringToFile("ConstraintList.size() "
				+ constraintList.size(), logFile);
		if (constraintList != null) {
			if (constraintList.size() > 0) {
				DataManager.saveConstraintsData(constraintList, saveName);
			}
		}
		auxillary.FileIOMethods.appendStringToFile("SettingsInfo "
				+ settingsInfo.size() + " " + settingsFile, logFile);
		auxillary.FileIOMethods.writeListToFile(settingsInfo, settingsFile);
	}

	/**
	 * ProjectIOMethods.addProjectToSettings takes as arguments
	 * 
	 * @param saveName
	 *            - the name of the project to be added
	 * @param facesContext
	 * 
	 *            and adds the new project to the list of available projects for
	 *            the specific user.
	 */
	private static void addProjectToSettings(String saveName,
			FacesContext facesContext) {
		List<SelectItem> projectList = ((UserSettings) facesContext
				.getExternalContext().getSessionMap().get("userSettings"))
				.getProjectList();
		projectList.add(new SelectItem(saveName));
		((UserSettings) facesContext.getExternalContext().getSessionMap()
				.get("userSettings")).setProjectList((ArrayList<SelectItem>) projectList);
		writeNewSettings(facesContext);
	}

	/**
	 * ProjectIOMethods.writeNewSettings takes as arguments
	 * 
	 * @param facesContext
	 * 
	 *            and writes the new settings file for the specific user.
	 */
	private static void writeNewSettings(FacesContext facesContext) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "FileIOMethods.writeNewSettings";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<String> newSettings = new ArrayList<String>();
		String settingsFile = ((UserSettings) facesContext.getExternalContext()
				.getSessionMap().get("userSettings")).getUserSettingsFileName();

		String language = ((UserSettings) facesContext.getExternalContext()
				.getSessionMap().get("userSettings")).getLang();
		language = "<language=\"" + language + "\"/>";
		newSettings.add(language);

		String lexStart = "<lexicon>";
		newSettings.add(lexStart);
		List<SelectItem> lexList = ((UserSettings) facesContext
				.getExternalContext().getSessionMap().get("userSettings"))
				.getLexiconList();
		for (int a = 0; a < lexList.size(); a++) {
			String lexEntry = "<value=\""
					+ lexList.get(a).getLabel().toString() + "\"/>";
			logInfo = lexEntry;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (newSettings.indexOf(lexEntry) < 0) {
				newSettings.add(lexEntry);
			}
		}
		String lexEnd = "</lexicon>";
		newSettings.add(lexEnd);

		String projectStart = "<project>";
		newSettings.add(projectStart);
		List<SelectItem> projectList = ((UserSettings) facesContext
				.getExternalContext().getSessionMap().get("userSettings"))
				.getProjectList();
		for (int a = 0; a < projectList.size(); a++) {
			String projectEntry = "<value=\""
					+ projectList.get(a).getLabel().toString() + "\"/>";
			logInfo = projectEntry;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (newSettings.indexOf(projectEntry) < 0) {
				newSettings.add(projectEntry);
			}
		}
		String projectEnd = "</project>";
		newSettings.add(projectEnd);

		auxillary.FileIOMethods.writeListToFile(newSettings, settingsFile);
	}

	/**
	 * ProjectIOMethods.writeSyllableFile takes as argument
	 * 
	 * @param syllableList
	 *            - ArrayList of Syllables objects, which is then written to the
	 *            appropriate file.
	 */
	private static void writeSyllableFile(ArrayList<Syllables> syllableList) {
		String listStart = "<syllableList>";
		String listEnd = "</syllableList>";
		String itemStart = "<syllable>";
		String itemEnd = "</syllable>";

		ArrayList<String> contentList = new ArrayList<String>();
		contentList.add(listStart);
		for (int a = 0; a < syllableList.size(); a++) {
			Syllables syllable = syllableList.get(a);
			contentList.add(itemStart);
			contentList.add(id + syllable.getId() + end);
			contentList.add(value + syllable.getValue() + end);
			contentList.add(emph + syllable.isEmph() + end);
			contentList.add(itemEnd);
		}
		contentList.add(listEnd);

		auxillary.FileIOMethods.writeListToFile(contentList, syllFile);
	}

	/**
	 * ProjectIOMethods.writeWordFile takes as argument
	 * 
	 * @param wordList
	 *            - ArrayList of Words objects which are then written to the
	 *            appropriate file.
	 */
	private static void writeWordFile(ArrayList<Words> wordList) {
		ArrayList<String> contentList = getWordListContent(wordList);
		auxillary.FileIOMethods.writeListToFile(contentList, wordsFile);
	}

	/**
	 * ProjectIOMethods.writeTokenFile takes as arguments
	 * 
	 * @param tokenList
	 *            - ArrayList of Token objects which are written to the
	 *            appropriate file.
	 */
	private static void writeTokenFile(ArrayList<Tokens> tokenList) {
		String listStart = "<tokenList>";
		String listEnd = "</tokenList>";
		String itemStart = "<token>";
		String itemEnd = "</token>";

		ArrayList<String> contentList = new ArrayList<String>();
		contentList.add(listStart);
		for (int a = 0; a < tokenList.size(); a++) {
			Tokens token = tokenList.get(a);
			contentList.add(itemStart);
			contentList.add(id + token.getId() + end);
			contentList.add(value + token.getValue() + end);
			contentList.add(type + token.getType() + end);
			contentList.add(itemEnd);
		}
		contentList.add(listEnd);

		auxillary.FileIOMethods.writeListToFile(contentList, tokFile);
	}

	/**
	 * ProjectIOMethods.writeSentenceFile takes as arguments
	 * 
	 * @param sentenceList
	 *            - ArrayList of Sentences objects which are then written to the
	 *            appropriate file
	 */
	private static void writeSenteceFile(ArrayList<Sentences> sentenceList) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "project.ProjectIOMethods.writeSentenceFile";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "creating Sentence List " + sentenceList.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String listStart = "<sentenceList>";
		String listEnd = "</sentenceList>";
		String itemStart = "<sentence>";
		String itemEnd = "</sentence>";
		String constraintStart = "<constraints>";
		String constraintEnd = "</constraints>";

		ArrayList<String> contentList = new ArrayList<String>();
		contentList.add(listStart);
		boolean constraints = ((SubtitlingManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("subtitlingManager")).isShowConstraints();
		logInfo = "constraints " + constraints;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Constraints> constraintList = null;
		if (constraints == true) {
			constraintList = ((SubtitlingManager) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("subtitlingManager")).getConstraintList();
		}
		for (int a = 0; a < sentenceList.size(); a++) {
			Sentences sentence = sentenceList.get(a);
			contentList.add(itemStart);
			contentList.add(id + sentence.getId().trim() + end);
			contentList.add(value + sentence.getValue().trim() + end);
			contentList.add(words + sentence.getWords() + end);
			contentList.add(tokens + sentence.getTokens() + end);
			logInfo = "" + sentenceList.size(); 
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (constraints == true) {
				if (a < constraintList.size()) {
					contentList.add(constraintStart);
					String conStart = "<start="
							+ constraintList.get(a).getStart() + end;
					contentList.add(conStart);
					String conEnd = "<end=" + constraintList.get(a).getEnd()
							+ end;
					contentList.add(conEnd);
					String conDur = "<duration="
							+ constraintList.get(a).getDuration() + end;
					contentList.add(conDur);
					String conRealDur = "<realduration="
							+ constraintList.get(a).getRealDuration() + end;
					contentList.add(conRealDur);
					String conPause = "<pause="
							+ constraintList.get(a).getPause() + end;
					contentList.add(conPause);
					contentList.add(constraintEnd);
				}
				contentList.add(itemEnd);
			}
		}
		contentList.add(listEnd);

		auxillary.FileIOMethods.writeListToFile(contentList, sentFile);
	}

	/**
	 * ProjectIOMethods.createSettingsInfo takes as arguments
	 * 
	 * @param saveName
	 *            - String representing the project identifier
	 * @return - ArrayList containing the information for the central project
	 *         file, which contains information about the user, project specific
	 *         settings and points to the information connected to this project.
	 */
	private static ArrayList<String> createSettingsInfo(String saveName) {
		ArrayList<String> returnList = new ArrayList<String>();

		String projectStart = "<project>";
		returnList.add(projectStart);

		String settingsStart = "<settings>";
		returnList.add(settingsStart);

		String voiceSetting = "<voice=Katrin/>";
		returnList.add(voiceSetting);

		String engineSetting = "<engine=loquendo/>";
		returnList.add(engineSetting);

		String idSetting = "<id=" + saveName + "/>";
		returnList.add(idSetting);

		String lexSetting;
		if (((LexiconService) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("table"))
				.isUseSelection()) {
			lexSetting = "<lex=yes/>";
		} else {
			lexSetting = "<lex=no/>";
		}
		returnList.add(lexSetting);

		String phonTuning;
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.isShowPhonetic()) {
			phonTuning = "<phon=yes/>";
		} else {
			phonTuning = "<phon=no/>";
		}
		returnList.add(phonTuning);

		String stressTuning;
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.isShowStress()) {
			stressTuning = "<stress=yes/>";
		} else {
			stressTuning = "<stress=no/>";
		}
		returnList.add(stressTuning);

		String langTuning;
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.isShowLangTuning()) {
			langTuning = "<language=yes/>";
		} else {
			langTuning = "<language=no/>";
		}
		returnList.add(langTuning);

		String constraints;
		if (((SubtitlingManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("subtitlingManager"))
				.isShowConstraints()) {
			constraints = "<constraints=yes/>";
		} else {
			constraints = "<constraints=no/>";
		}
		returnList.add(constraints);

		String movie = null;
		if (((SubtitlingManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("subtitlingManager"))
				.getFile() != null) {
			if (((((SubtitlingManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap()
					.get("subtitlingManager")).getFile()).length()) > 0) {
				movie = "<movie="
						+ ((SubtitlingManager) FacesContext
								.getCurrentInstance().getExternalContext()
								.getSessionMap().get("subtitlingManager"))
								.getFile() + "/>";
			}
		}
		returnList.add(movie);

		String valueStart = "<value=";
		String valueEnd = "/>";
		String content = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		content = valueStart + content + valueEnd;
		returnList.add(content);

		String settingsEnd = "</settings>";
		returnList.add(settingsEnd);

		String projectEnd = "</project>";
		returnList.add(projectEnd);

		return returnList;
	}

	/**
	 * ProjectIOMethods.setFileNames takes as arguments
	 * 
	 * @param saveName
	 *            - String representing the project identifier, which allows to
	 *            identify the information files from the project settings file.
	 * 
	 *            The central file is called "EML TTS Projectfile - ETP".
	 */
	private static void setFileNames(String saveName) {
		settingsFile = saveName + ".etp";
		sentFile = saveName + ".sent";
		tokFile = saveName + ".tok";
		wordsFile = saveName + ".wds";
		syllFile = saveName + ".syl";
		phonFile = saveName + ".phon";
		lexFile = saveName + ".etl";
	}

	/**
	 * ProjectIOMethods.openProject takes as argument
	 * 
	 * @param fileContent
	 *            - ArrayList containing the information from the settings file
	 *            and triggers the extraction of the additional information for
	 *            displaying in the Editor Environment.
	 * @param facesContext
	 */
	public static String openProject(ArrayList<String> fileContent,
			FacesContext facesContext) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ProjectIOMethods.openProject";
		auxillary.FileIOMethods.appendStringToFile("OpenProject 1", logFile);
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "OPENPROJECT! " + fileContent.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		Settings projectSettings = new Settings(fileContent);
		logInfo = projectSettings.getId();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String saveName = projectSettings.getId();
		if (projectSettings.getId().contains("/var/www/") == false) {
			saveName = ((UserSettings) facesContext.getExternalContext()
					.getSessionMap().get("userSettings")).getUserPath()
					+ projectSettings.getId();
		}
		logInfo = "SaveName " + saveName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setFileNames(saveName);
		auxillary.FileIOMethods.appendStringToFile(
				"Before extracting Saved Data", logFile);
		Data savedData = DataManager.extractSavedData(sentFile, tokFile,
				wordsFile, syllFile, projectFile);
		auxillary.FileIOMethods.appendStringToFile(
				"After loading of saved Data", logFile);

		if (savedData != null) {
			auxillary.FileIOMethods.appendStringToFile("savedData is not NULL",
					logFile);
			setNewData(savedData);
		} else {
			auxillary.FileIOMethods.appendStringToFile("savedData IS null",
					logFile);
			setNewData(new Data());
		}

		TuningManager tuningManager = new TuningManager();
		auxillary.FileIOMethods.appendStringToFile(
				"Creating new TuningManager", logFile);
		if (((TuningManager) facesContext.getExternalContext().getSessionMap()
				.get("tuningManager")) != null) {
			auxillary.FileIOMethods.appendStringToFile(
					"TuningManager is not NULL", logFile);
			tuningManager = ((TuningManager) facesContext.getExternalContext()
					.getSessionMap().get("tuningManager"));
		}

		logInfo = "PhonSetting " + projectSettings.getPhon();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "StressSetting " + projectSettings.getStress();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "ValueSetting " + projectSettings.getValue();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "ConstraintSetting " + projectSettings.getConstraints();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "LanguageSetting " + projectSettings.getLanguage();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		tuningList = new ArrayList<SelectItem>();
		if (projectSettings.getLanguage() != null
				&& projectSettings.getLanguage().equals("yes")) {
			logInfo = "ProjectSetting for language tuning is yes";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			tuningManager.extractLangInfo(getNewData().getWordList());
			auxillary.FileIOMethods.appendStringToFile(
					"Setting LangTuning to TRUE", logFile);
			tuningManager.setShowLangTuning(true);
			tuningList.add(new SelectItem("language"));
		}
		if (projectSettings.getPhon() != null
				&& projectSettings.getPhon().equals("yes")) {
			logInfo = "ProjectSetting for phoneticTuning is yes";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			tuningManager.extractPhoneticInfo(getNewData().getWordList());
			auxillary.FileIOMethods.appendStringToFile(
					"Setting PhoneticTuning to TRUE", logFile);
			tuningManager.setShowPhonetic(true);
			tuningList.add(new SelectItem("phonetic"));
		}
		if (projectSettings.getStress() != null
				&& projectSettings.getStress().equals("yes")) {
			logInfo = "ProjectSetting for stressTuning is yes";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			logInfo = getNewData().getSyllableList().size() + " "
					+ getNewData().getWordList().size() + " "
					+ getNewData().getSentenceList().size();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			tuningManager.extractStressInfo(getNewData().getSyllableList(),
					getNewData().getWordList(), getNewData().getSentenceList());
			auxillary.FileIOMethods.appendStringToFile(
					"Setting StressTuning to TRUE", logFile);
			tuningManager.setShowStress(true);
			tuningList.add(new SelectItem("stress"));
		}
		SubtitlingManager sm = ((SubtitlingManager) facesContext
				.getExternalContext().getSessionMap().get("subtitlingManager"));
		if (projectSettings.getConstraints().equals("yes")) {
			logInfo = "ProjectSetting for constraints is yes";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			ArrayList<Constraints> constraintList = Constraints
					.getConstraintsListFromFile(saveName);
			auxillary.FileIOMethods.appendStringToFile("ConstraintListSize "
					+ constraintList.size(), logFile);
			sm.setConstraintList(constraintList);
			sm.setShowConstraints(true);
			sm.setShowPreview(true);
			if (projectSettings.getMovie().length() > 0) {
				sm.setFile(projectSettings.getMovie());
				sm.setShowPlayer(true);
				sm.setShowUpload(false);
				sm.setPreviousTimeIndex("NA");
				sm.setCurrentTimeIndex(constraintList.get(0).getStart() + " "
						+ constraintList.get(0).getEnd());
				sm.setCurrentDescription(constraintList.get(0).getValue());
				sm.setNextTimeIndex(constraintList.get(1).getStart() + " "
						+ constraintList.get(1).getEnd());
				sm.setNextDescription(constraintList.get(1).getValue());
				sm.setCurrentRowPos(0);
				sm.setCurrentPos(0);
				sm.setReducedList(constraintList);
				String timestamp = "?timestamp=" + (new Date()).getTime();
				sm.setFlashvars("file=" + projectSettings.getMovie()
						+ timestamp);
				sm.setShowPreview(true);
			}
		}
		String sentenceString = DataManager.extractSentences(getNewData()
				.getSentenceList());
		logInfo = "SentenceString ProjectIOMethods" + sentenceString;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		return sentenceString;
	}

	/**
	 * ProjectIOMethods.newProject takes as argument
	 * 
	 * @param projectID
	 *            - String representing the project identifier and creates a new
	 *            project, which at the end can be saved using the project
	 *            identifier.
	 */
	public static void newProject(String projectID) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ProjectIOMethods.newProject";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setFileNames(projectID);
		Settings settings = new Settings();
		logInfo = "New Settings";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		settings.setId(projectID);
		auxillary.FileIOMethods.appendStringToFile("setID " + projectID,
				logFile);
		settings.setLex("LEX");
		auxillary.FileIOMethods.appendStringToFile("setLex", logFile);
		ArrayList<Settings> settingsArray = new ArrayList<Settings>();
		auxillary.FileIOMethods.appendStringToFile("after settingsArrayIni",
				logFile);
		settingsArray.add(settings);
		auxillary.FileIOMethods.appendStringToFile("after adding settings",
				logFile);
		ProjectManager projectManager = new ProjectManager();
		auxillary.FileIOMethods.appendStringToFile("After PM INI "
				+ projectManager.isShowSettings(), logFile);
		projectManager.setSettingsArray(settingsArray);
		EditorManager editorManager = new EditorManager();
		editorManager.setResult("");
		projectManager.setShowSettings(true);
		auxillary.FileIOMethods.appendStringToFile(settings.getId(), logFile);
		auxillary.FileIOMethods.appendStringToFile(settings.getLex(), logFile);
		auxillary.FileIOMethods.appendStringToFile("End", logFile);
	}

	/**
	 * ProjectIOMethods.getWordListContent takes as arguments
	 * 
	 * @param wordList
	 *            - ArrayList of Words Objects
	 * @return - ArrayList of Words information in xml-representation for saving
	 *         to the appropriate file or for presenting in a simple editor.
	 */
	public static ArrayList<String> getWordListContent(ArrayList<Words> wordList) {
		String listStart = "<wordList>";
		String listEnd = "</wordList>";
		String itemStart = "<word>";
		String itemEnd = "</word>";

		ArrayList<String> contentList = new ArrayList<String>();
		contentList.add(listStart);
		for (int a = 0; a < wordList.size(); a++) {
			Words word = wordList.get(a);
			contentList.add(itemStart);
			contentList.add(id + word.getId() + end);
			contentList.add(value + word.getValue() + end);
			contentList.add(emph + word.isEmph() + end);
			contentList.add(phonetic + word.getPhonetic() + end);
			contentList.add(language + word.getLanguage() + end);
			contentList.add(itemEnd);
		}
		contentList.add(listEnd);
		return contentList;
	}

	/**
	 * ProjectIOMethods.openProject is a method for opening projects based on
	 * their name, where the content of the project is not yet gathered. It
	 * takes as argument
	 * 
	 * @param projectName
	 *            - String representing the project to be loaded
	 * @param facesContext
	 *            - FacesContext representing the context which is being used
	 * @return - String containing the value information from the project for
	 *         display in the editor.
	 */
	public static String openProject(String projectName,
			FacesContext facesContext) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ProjectIOMethods.openProject " + projectName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		auxillary.FileIOMethods.appendStringToFile("Open Project 2", logFile);
		logInfo = "Open Project From Project List";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String userPath = ((UserSettings) (FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings")))
				.getUserPath();
		String projectFileName = userPath + projectName + ".etp";
		ArrayList<String> fileContent = auxillary.FileIOMethods
				.getFileContent(projectFileName);
		String editorValue = openProject(fileContent, facesContext);
		return editorValue;
	}

	/**
	 * @param tuningList
	 *            the tuningList to set
	 */
	public void setTuningList(ArrayList<SelectItem> tuningList) {
		this.tuningList = tuningList;
	}

	/**
	 * @return the tuningList
	 */
	public static ArrayList<SelectItem> getTuningList() {
		return tuningList;
	}

	/**
	 * @param newData
	 *            the newData to set
	 */
	public static void setNewData(Data newData) {
		ProjectIOMethods.newData = newData;
	}

	/**
	 * @return the newData
	 */
	public static Data getNewData() {
		return newData;
	}
}
