/**
 * PopupManager contains methods for managing various popups shown during the
 * interaction with the EML TTS Editor (file upload, file saving, etc.). These
 * methods currently are:
 * 
 * createNewProject
 * openFileNameDialog
 * openFileOpenDialog
 * uploadActionListener
 * closePopupAction
 * 
 * Additionally, it contains methods for accessing information from the popups
 * or for showing the popups. These currently are:
 * 
 * savePopup;
 * loadFilePopup
 * appID
 * saveName
 * currentFile
 * showProjectID
 * projectID
 * 
 * fileUploadProgressModel
 * openDownloadDialog
 */
package popups;

import importTools.SubtitlingManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import project.ProjectManager;
import project.Settings;

import settings.session.SessionManagement;
import settings.userSettings.UserSettings;
import synthesis.SynthesisManager;
import tuning.SyllStress;
import tuning.TuningManager;

import lexicon.Lexicon;
import lexicon.LexiconService;

import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;

import editor.EditorManager;

/**
 * EMLTTSEditorWeb.popups.PopupManager.java offers methods for interacting with
 * various popups which are required during the interaction with the EML TTS
 * editor. These currently are:
 * 
 * createNewProject - shows a popup where a user can enter the current projects
 * name. This name will then be also used to save the data from the editor to
 * files. openFileNameDialog - shows a popup to save various datatypes to the
 * appropriate files on the server. openFileOpenDialog - shows a popup to open
 * and afterwards upload a file from the client to the server.
 * uploadActionListener - saves the uploaded file and if necessary creates a
 * representation of the uploaded data in the editor environment.
 * closePopupAction - upon closing the popup if necessary other actions are also
 * triggered.
 * 
 * Additionally it offers access to various information concerning popups. These
 * currently are:
 * 
 * savePopup - boolean whether the popup for saving data should be shown
 * loadFilePopup - boolean whether the popup for loading a file from the client
 * should be shown appID - String representing the application that called for
 * showing a popup saveName - String that is shown in the popup dialog
 * currentFile - FileInfo object representing an uploaded file showProjectID -
 * boolean whether the popup for entering a name for a new TTS Project should be
 * shown projectID - String representing the name for a new TTS Project.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.10.2010 MaMi
 * 
 *         Added distinction between closePopup and cancelPopup
 * 
 *         cancelPopupAction
 * 
 *         29.10.2010 MaMi
 * 
 *         Added methods:
 * 
 *         fileUploadProgressModel openDownloadDialog
 * 
 *         20.07.2011 MaMi
 */
public class PopupManager {
	private boolean savePopup = false;
	private boolean loadFilePopup = false;
	private String appID;
	private String saveName = "FileName";
	private FileInfo currentFile;
	private boolean showProjectID;
	private String projectID;
	private boolean showLexError = false;
	private static boolean showConstraintWarning = false;
	private boolean showArbitraryError = false;
	private String arbitraryErrorValue;
	private boolean loadLexicon = false;
	private boolean showPlayer = true;
	private boolean rendModelUploadPart2;
	private int fileProgressModel;
	private boolean showDownloadPopup = false;
	private boolean showTextDownloadPopup = false;
	private boolean showAllSoundDownloadPopup = false;
	private boolean showProjectExistsWarning = false;
	private String projectExistsWarningText;
	public boolean doOverwrite = false;

	/**
	 * Empty constructor for creating a popup manager object.
	 */
	public PopupManager() {
	}

	/**
	 * PopupManager.createNewProject takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent which triggered the execution of this method,
	 *            which initializes the creation of a new project, by showing a
	 *            popup dialog which allows the user to enter a name for the
	 *            current project.
	 */
	public void createNewProject(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PopupManager.createNewProject";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setAppID(event.getComponent().getId());
		setShowProjectID(true);
		logInfo = "TestPhonetic";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.isShowPhonetic()) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setShowPhonetic(false);
		}
		logInfo = "TestSelection";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getSelection().length() > 0) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setSelection("");
		}
		logInfo = "TestStress";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.isShowStress()) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setShowStress(false);
		}
		logInfo = "TestPhoneticArray ";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getPhoneticArray() != null) {
			logInfo = "Setting phonetic Array to null";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setPhoneticArray(null);
		}
		logInfo = "TestStressArray";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getStressArray() != null) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setStressArray(null);
		}
		logInfo = "testSyllStressArray";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getSyllStressArray().size() > 0) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setSyllStressArray(null);
		}
		logInfo = "TestEditor";
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getEditor() != null) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setEditor("");
		}
		logInfo = "TestStressEditor";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getStressEditor().length() > 0) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setStressEditor("");
		}
		logInfo = "TestStressArray2";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<SyllStress> stressInfo = ((TuningManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("tuningManager")).getSyllStressArray();
		if (stressInfo != null) {
			((TuningManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("tuningManager"))
					.setSyllStressArray(null);
		}
	}

	/**
	 * PopupManager.openFileNameDialog takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent which triggered the exection of this method,
	 *            which allows for entering a name under which the data should
	 *            be saved.
	 */
	public void openFileNameDialog(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PopupManager.openFileNameDialog";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setAppID(event.getComponent().getId());
		logInfo = "OpenFileNameDialog ID " + event.getComponent().getId();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setAppID(event.getComponent().getId());
		if (getAppID().equalsIgnoreCase("saveLex")) {
			logInfo = "saveLex.... ?????";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			ArrayList<Lexicon> lexContent = ((LexiconService) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("table")).getLexiconList();
			if (LexiconService.noEntries(lexContent)) {
				setShowLexError(true);
			} else {
				saveName = "LexName";
			}
		}
		if (getAppID().equalsIgnoreCase("saveWave")) {
			saveName = "WaveName";
		}
		if (getAppID().equalsIgnoreCase("saveProject")) {
			System.out.println("SAVING PROJECT! " + projectID);
			if (projectID == null) {
				// saveName = "SaveProject";
				ArrayList<Settings> settingsArray = ((ProjectManager) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("projectManager"))
						.getSettingsArray();
				if (settingsArray != null) {
					System.out.println(settingsArray.size());
					saveName = settingsArray.get(0).getId();
					auxillary.FileIOMethods.appendStringToFile(saveName,
							logFile);
				} else {
					saveName = "SaveProject";
				}
			} else {
				saveName = projectID;
			}
		}
		System.out.println(isSavePopup());
		setSavePopup(true);
	}

	/**
	 * PopupManager.openFileOpenDialog takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent that triggered the execution of this method,
	 *            which opens a dialog where the user can choose a local
	 *            (client-side) file, which is then uploaded.
	 */
	@SuppressWarnings("static-access")
	public void openFileOpenDialog(ActionEvent event) {
		showPlayer = false;
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PopupManager.openFileOpenDialog";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setAppID(event.getComponent().getId());
		logInfo = "OpenFileOpenDialog ID " + event.getComponent().getId();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (getAppID().contains("add")) {
			logInfo = "appID contains ADD";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			saveName = "list/lexName";
			setLoadFilePopup(true);
		} else {
			if (getAppID().equals("saveProject")) {
				saveName = "OpenProject";
				setLoadFilePopup(true);
			} else {
				if (getAppID().equals("importMovie")) {
					saveName = "ImportSubtitleing";
					SubtitlingManager sm = (SubtitlingManager) FacesContext
							.getCurrentInstance().getExternalContext()
							.getSessionMap().get("subtitlingManager");
					sm.setShowPreview(true);
					SessionManagement sessionManager = (SessionManagement) FacesContext
							.getCurrentInstance().getExternalContext()
							.getSessionMap().get("sessionManagement");
					sessionManager.setTabIndex(8);
				} else {
					if (getAppID().equals("importSub")) {
						setLoadFilePopup(true);
						SubtitlingManager sm = (SubtitlingManager) FacesContext
								.getCurrentInstance().getExternalContext()
								.getSessionMap().get("subtitlingManager");
						sm.setShowUpload(true);
						sm.setShowPlayer(false);
						sm.setShowPreview(true);
					} else {
						if (getAppID().equals("importText")) {
							saveName = "ImportText";
							setLoadFilePopup(true);
						} else {
							if (getAppID().equals("openLex")) {
								saveName = "OpenLexicon";
								auxillary.FileIOMethods.appendStringToFile(
										saveName, logFile);
								setLoadLexicon(true);
							} else {
								if (getAppID().equals("mix")) {
									saveName = "Upload File";
									setLoadFilePopup(true);
								}
								logInfo = "appID is something else " + appID;
								auxillary.FileIOMethods.appendStringToFile(
										logInfo, logFile);
								saveName = "FileName";
							}
						}
					}
				}
			}
		}
	}

	/**
	 * PopupManager.uploadActionListener takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent that triggered the execution of this method,
	 *            which saves the uploaded file to the server system and
	 *            triggers the execution of further actions, which take care of
	 *            displaying the uploaded data.
	 */

	public void uploadActionListener(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PopupManager.uploadActionListener";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		logInfo = "UploadActionListener " + appID;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		InputFile inputFile = (InputFile) event.getSource();
		setCurrentFile(inputFile.getFileInfo());
		FileInfo fileInfo = inputFile.getFileInfo();

		logInfo = "fileInfo " + fileInfo.getFileName();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		if (appID == null) {
			appID = event.getComponent().getId().trim();
		}
		if (appID.equals(event.getComponent().getId().trim()) == false) {
			if (event.getComponent().getId().trim()
					.equals("inputFileComponent") == false) {
				appID = event.getComponent().getId().trim();
			}
		}
		if (fileInfo.getStatus() == FileInfo.SAVED) {
			auxillary.FileIOMethods.appendStringToFile("SAVED! " + appID,
					logFile);
			File uploadedFile = fileInfo.getFile();
			logInfo = "UploadedFile " + uploadedFile.getAbsolutePath();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (appID.equals("uploadDevModeDataScript")) {
				auxillary.FileIOMethods.appendStringToFile(
						"uploading text zip file", logFile);
				developmentTools.DevelopmentManagement
						.extractTextFiles(uploadedFile.getAbsolutePath());
			} else {
				if (appID.equals("uploadDevModeDataWav")) {
					rendModelUploadPart2 = true;
					auxillary.FileIOMethods.appendStringToFile(
							"uploading wav zip file", logFile);
					developmentTools.DevelopmentManagement
							.extractZipFiles(uploadedFile.getAbsolutePath());
				} else {
					if (appID.equals("uploadMovie")) {
						auxillary.FileIOMethods.appendStringToFile(
								"uploading Movie!", logFile);
						auxillary.FileIOMethods.appendStringToFile(
								"uploading movie file", logFile);
						SubtitlingManager subtitlingManager = (SubtitlingManager) FacesContext
								.getCurrentInstance().getExternalContext()
								.getSessionMap().get("subtitlingManager");
						try {
							subtitlingManager.prepareMovie(uploadedFile
									.getAbsolutePath());
						} catch (IOException e) {
							setArbitraryErrorValue("I/O Error while preparing movie.");
							setShowArbitraryError(true);
						}
					} else {
						ArrayList<String> fileContent = auxillary.FileIOMethods
								.getFileContent(uploadedFile.getAbsolutePath());
						auxillary.FileIOMethods.appendStringToFile(
								"After File Load " + appID, logFile);
						if (appID.equals("addWordList")) {
							for (int a = 0; a < fileContent.size(); a++) {
								ArrayList<Lexicon> lexiconList = ((LexiconService) FacesContext
										.getCurrentInstance()
										.getExternalContext().getSessionMap()
										.get("table")).getLexiconList();
								lexiconList.add(new Lexicon(fileContent.get(a)
										.toString(), "", "", ""));
							}
						}
						if (appID.equals("addLexicon")) {
							ArrayList<Lexicon> lexiconList = ((LexiconService) FacesContext
									.getCurrentInstance().getExternalContext()
									.getSessionMap().get("table"))
									.getLexiconList();
							ArrayList<Lexicon> newList = auxillary.XMLMethods
									.extractEntries(fileContent);
							for (int a = 0; a < newList.size(); a++) {
								if (lexiconList.contains(newList.get(a)) == false) {
									lexiconList.add(newList.get(a));
								}
							}
						}
						if (appID.equals("openLex")) {
							ArrayList<Lexicon> lexiconList = ((LexiconService) FacesContext
									.getCurrentInstance().getExternalContext()
									.getSessionMap().get("table"))
									.getLexiconList();
							lexiconList.clear();
							ArrayList<Lexicon> newList = auxillary.XMLMethods
									.extractEntries(fileContent);
							for (int a = 0; a < newList.size(); a++) {
								if (lexiconList.contains(newList.get(a)) == false) {
									lexiconList.add(newList.get(a));
								}
							}
						}
						if (appID.equals("openProject")) {
							logInfo = "openProject";
							auxillary.FileIOMethods.appendStringToFile(logInfo,
									logFile);
							TuningManager tuningManager = ((TuningManager) FacesContext
									.getCurrentInstance().getExternalContext()
									.getSessionMap().get("tuningManager"));
							project.ProjectIOMethods.openProject(fileContent,
									FacesContext.getCurrentInstance());
							tuningManager.setShowPhonetic(true);
							LexiconService lexServ = ((LexiconService) FacesContext
									.getCurrentInstance().getExternalContext()
									.getSessionMap().get("table"));
							lexServ.setShowLexicon(true);
						}
						if (appID.equals("mix")) {
							logInfo = "mix Soundfiles!";
							auxillary.FileIOMethods.appendStringToFile(logInfo,
									logFile);
							effects.EffectsManager.mix(uploadedFile);
						}
						if (appID.equals("importSub")) {
							logInfo = "Import subtitle files";
							auxillary.FileIOMethods.appendStringToFile(logInfo,
									logFile);
							importTools.SubtitlingImport
									.importProject(uploadedFile);
						}
						if (appID.equals("importText")) {
							logInfo = "Import Plain Text";
							auxillary.FileIOMethods.appendStringToFile(logInfo,
									logFile);
							importTools.TextImport.importText(uploadedFile);
						}
					}
				}
			}
		} else if (fileInfo.getStatus() == InputFile.INVALID) {
			auxillary.FileIOMethods.appendStringToFile("INVALID", logFile);
		}
		// file size exceeded the limit
		else if (fileInfo.getStatus() == InputFile.SIZE_LIMIT_EXCEEDED) {
			auxillary.FileIOMethods.appendStringToFile("SIZE_LIMIT_EXCEEDED",
					logFile);
		}
		// indicate that the request size is not specified.
		else if (fileInfo.getStatus() == InputFile.UPLOADING) {
			auxillary.FileIOMethods.appendStringToFile("UPLOADING", logFile);
		} else if (fileInfo.getStatus() == InputFile.INVALID_CONTENT_TYPE) {
			auxillary.FileIOMethods.appendStringToFile("INVALID_CONTENT_TYPE",
					logFile);
		} else if (fileInfo.getStatus() == InputFile.UNKNOWN_SIZE) {
			auxillary.FileIOMethods.appendStringToFile("UNKNOWN_SIZE", logFile);
		} else if (fileInfo.getStatus() == InputFile.UNSPECIFIED_NAME) {
			auxillary.FileIOMethods.appendStringToFile("UNSPECIFIED_NAME",
					logFile);
		} else {
			auxillary.FileIOMethods.appendStringToFile("NOT SAVED", logFile);
			logInfo = "Not Saved!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		setLoadFilePopup(false);
		showPlayer = true;
	}

	/**
	 * PopupManager.closePopupAction takes care of triggering the appropriate
	 * actions depending on which popup triggered the closing action and also
	 * closes the popup, allowing for continuing to interact with the TTS
	 * Editor.
	 */
	public void closePopupAction() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PopupManager.closePopupAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "closePopupAction " + appID;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (appID != null) {
			if (appID.equals("saveLex")) {
				saveName = saveName + ".etl";
				ArrayList<Lexicon> lexContent = ((LexiconService) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("table")).getLexiconList();
				lexicon.LexiconService.saveLexicon(saveName, lexContent);
			}
			if (appID.equals("saveWave")) {
				String content = ((EditorManager) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("editor")).getResult();
				SynthesisManager sm = SynthesisManager.get();
				sm.synthesize(content, saveName);
			}
			if (appID.equals("saveProject")) {
				logInfo = "saveProject";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				FacesContext facesContext = FacesContext.getCurrentInstance();
				project.ProjectIOMethods.saveProject(saveName, facesContext);
			}
			if (appID.equals("newProject")) {
				logInfo = "create new Project!";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				project.ProjectIOMethods.newProject(getProjectID());
			}
			if (appID.equals("openLex")) {
				logInfo = "Open Lexicon";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				lexicon.LexiconService.openLexicon(saveName);
			}
			if (appID.contains("importSub")) {
				setShowConstraintWarning(false);
			}
			if (doOverwrite == true) {
				logInfo = "doOverwrite";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				FacesContext facesContext = FacesContext.getCurrentInstance();
				project.ProjectIOMethods.saveProject(saveName, facesContext);
			}
			if (appID.equals("saveNewName")) {
				setShowProjectExistsWarning(false);
			}
		}
		setSavePopup(false);
		setLoadFilePopup(false);
		if (isShowProjectID()) {
			setShowProjectID(false);
		}
		setShowLexError(false);
		setShowConstraintWarning(false);
		showPlayer = true;
	}

	/**
	 * PopupManager.cancelPopupAction takes care of closing popups that might be
	 * open, without taking any action (as opposed to closePopupAction).
	 */
	public void cancelPopupAction() {
		setSavePopup(false);
		setLoadFilePopup(false);
		setShowProjectID(false);
		setShowLexError(false);
		setShowArbitraryError(false);
		setShowTextDownloadPopup(false);
		setShowAllSoundDownloadPopup(false);
		setShowDownloadPopup(false);
		showPlayer = true;
	}

	/**
	 * PopupManager.fileUploadProgressModel takes care of displaying the
	 * progress of an upload, which is especially important for larger files. It
	 * takes as argument
	 * 
	 * @param event
	 *            - EventObject that is uploaded.
	 */
	public void fileUploadProgressModel(EventObject event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		InputFile ifile = (InputFile) event.getSource();
		fileProgressModel = ifile.getFileInfo().getPercent();
		auxillary.FileIOMethods.appendStringToFile(fileProgressModel + "%",
				logFile);
	}

	/**
	 * PopupManager.openDownloadDialog takes care of offering access to
	 * downloading sound- or text files. It takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this action.
	 */
	public void openDownloadDialog(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		showPlayer = false;
		auxillary.FileIOMethods.appendStringToFile("openDownloadDialog "
				+ event.getComponent().getId(), logFile);
		if (event.getComponent().getId().equals("exportSub")) {
			auxillary.FileIOMethods.appendStringToFile(
					"exportAudioDescription", logFile);
			setShowTextDownloadPopup(true);
		} else {
			if (event.getComponent().getId().equals("exportSound")) {
				auxillary.FileIOMethods.appendStringToFile("exportSound",
						logFile);
				setShowAllSoundDownloadPopup(true);
			} else {
				auxillary.FileIOMethods.appendStringToFile("ExportOthers!",
						logFile);
				setShowDownloadPopup(true);
			}
		}
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for PopupManager.
	 */
	/**
	 * @param savePopup
	 *            the savePopup to set
	 */
	public void setSavePopup(boolean savePopup) {
		this.savePopup = savePopup;
	}

	/**
	 * @return the savePopup
	 */
	public boolean isSavePopup() {
		return savePopup;
	}

	/**
	 * @param loadFilePopup
	 *            the loadFilePopup to set
	 */
	public void setLoadFilePopup(boolean loadFilePopup) {
		this.loadFilePopup = loadFilePopup;
	}

	/**
	 * @return the loadFilePopup
	 */
	public boolean isLoadFilePopup() {
		return loadFilePopup;
	}

	/**
	 * @param appID
	 *            the appID to set
	 */
	public void setAppID(String appID) {
		this.appID = appID;
	}

	/**
	 * @return the appID
	 */
	public String getAppID() {
		return appID;
	}

	/**
	 * @param saveName
	 *            the saveName to set
	 */
	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	/**
	 * @return the saveName
	 */
	public String getSaveName() {
		return saveName;
	}

	/**
	 * @param currentFile
	 *            the currentFile to set
	 */
	public void setCurrentFile(FileInfo currentFile) {
		this.currentFile = currentFile;
	}

	/**
	 * @return the currentFile
	 */
	public FileInfo getCurrentFile() {
		return currentFile;
	}

	/**
	 * @param showProjectID
	 *            the showProjectID to set
	 */
	public void setShowProjectID(boolean showProjectID) {
		this.showProjectID = showProjectID;
	}

	/**
	 * @return the showProjectID
	 */
	public boolean isShowProjectID() {
		return showProjectID;
	}

	/**
	 * @param projectID
	 *            the projectID to set
	 */
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	/**
	 * @return the projectID
	 */
	public String getProjectID() {
		return projectID;
	}

	/**
	 * @param showLexError
	 *            the showLexError to set
	 */
	public void setShowLexError(boolean showLexError) {
		this.showLexError = showLexError;
	}

	/**
	 * @return the showLexError
	 */
	public boolean isShowLexError() {
		return showLexError;
	}

	/**
	 * @param showConstraintWarning
	 *            the showConstraintWarning to set
	 */
	public static void setShowConstraintWarning(boolean showConstraintWarning) {
		PopupManager.showConstraintWarning = showConstraintWarning;
	}

	/**
	 * @return the showConstraintWarning
	 */
	public boolean isShowConstraintWarning() {
		return showConstraintWarning;
	}

	/**
	 * @param showArbitraryError
	 *            the showArbitraryError to set
	 */
	public void setShowArbitraryError(boolean showArbitraryError) {
		this.showArbitraryError = showArbitraryError;
	}

	/**
	 * @return the showArbitraryError
	 */
	public boolean isShowArbitraryError() {
		return showArbitraryError;
	}

	/**
	 * @param arbitraryErrorValue
	 *            the arbitraryErrorValue to set
	 */
	public void setArbitraryErrorValue(String arbitraryErrorValue) {
		this.arbitraryErrorValue = arbitraryErrorValue;
	}

	/**
	 * @return the arbitraryErrorValue
	 */
	public String getArbitraryErrorValue() {
		return arbitraryErrorValue;

	}

	/**
	 * @param loadLexicon
	 *            the loadLexicon to set
	 */
	public void setLoadLexicon(boolean loadLexicon) {
		this.loadLexicon = loadLexicon;
	}

	/**
	 * @return the loadLexicon
	 */
	public boolean isLoadLexicon() {
		return loadLexicon;
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
	 * @param rendModelUploadPart2
	 *            the rendModelUploadPart2 to set
	 */
	public void setRendModelUploadPart2(boolean rendModelUploadPart2) {
		this.rendModelUploadPart2 = rendModelUploadPart2;
	}

	/**
	 * @return the rendModelUploadPart2
	 */
	public boolean isRendModelUploadPart2() {
		return rendModelUploadPart2;
	}

	/**
	 * @param fileProgressModel
	 *            the fileProgressModel to set
	 */
	public void setFileProgressModel(int fileProgressModel) {
		this.fileProgressModel = fileProgressModel;
	}

	/**
	 * @return the fileProgressModel
	 */
	public int getFileProgressModel() {
		return fileProgressModel;
	}

	/**
	 * @param showDownloadPopup
	 *            the showDownloadPopup to set
	 */
	public void setShowDownloadPopup(boolean showDownloadPopup) {
		this.showDownloadPopup = showDownloadPopup;
	}

	/**
	 * @return the showDownloadPopup
	 */
	public boolean isShowDownloadPopup() {
		return showDownloadPopup;
	}

	/**
	 * @param showTextDownloadPopup
	 *            the showTextDownloadPopup to set
	 */
	public void setShowTextDownloadPopup(boolean showTextDownloadPopup) {
		this.showTextDownloadPopup = showTextDownloadPopup;
	}

	/**
	 * @return the showTextDownloadPopup
	 */
	public boolean isShowTextDownloadPopup() {
		return showTextDownloadPopup;
	}

	/**
	 * @param showAllSoundDownloadPopup
	 *            the showAllSoundDownloadPopup to set
	 */
	public void setShowAllSoundDownloadPopup(boolean showAllSoundDownloadPopup) {
		this.showAllSoundDownloadPopup = showAllSoundDownloadPopup;
	}

	/**
	 * @return the showAllSoundDownloadPopup
	 */
	public boolean isShowAllSoundDownloadPopup() {
		return showAllSoundDownloadPopup;
	}

	/**
	 * @param showProjectExistsWarning
	 *            the showProjectExistsWarning to set
	 */
	public void setShowProjectExistsWarning(boolean showProjectExistsWarning) {
		this.showProjectExistsWarning = showProjectExistsWarning;
	}

	/**
	 * @return the showProjectExistsWarning
	 */
	public boolean isShowProjectExistsWarning() {
		return showProjectExistsWarning;
	}

	/**
	 * @param projectExistsWarningText
	 *            the projectExistsWarningText to set
	 */
	public void setProjectExistsWarningText(String projectExistsWarningText) {
		this.projectExistsWarningText = projectExistsWarningText;
	}

	/**
	 * @return the projectExistsWarningText
	 */
	public String getProjectExistsWarningText() {
		return projectExistsWarningText;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
