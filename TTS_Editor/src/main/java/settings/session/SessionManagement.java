/**
 * SessionManagement takes care of setting user dependent variables on login and
 * removing these on logout.
 */
package settings.session;

import importTools.SubtitlingManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lexicon.LexiconService;
import popups.PopupManager;
import project.ProjectIOMethods;
import settings.userSettings.UserSettings;
import tuning.TuningManager;
import editor.EditorManager;

/**
 * EMLTTSEditorWeb.settings.sessionSessionManagement.java offers methods for
 * setting and keeping information based on the currently logged in user.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         16.12.2010 MaMi
 */
public class SessionManagement {
	private boolean showEditor = false;
	private boolean showWelcome = true;
	private int tabIndex = 0;
	private String projectName;
	private String lexiconName;
	private String newProjectName;
	private String editorValue;
	String globalProjectName;
	private boolean showDevWizard = false;
	private boolean showWarningPopup = false;
	private String logoutText = "";
	private boolean showTherapist = false;

	/**
	 * SessionManagement.logout takes care of actions included in the logout
	 * procedure.
	 */
	public String logout() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "trying to log you out!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		FacesContext context = FacesContext.getCurrentInstance();

		logInfo = "trying to access external context";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ExternalContext ec = context.getExternalContext();

		logInfo = "getting httpServletRequest";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		HttpServletRequest request = (HttpServletRequest) ec.getRequest();
		logInfo = "invalidating session";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		request.getSession(false).invalidate();

		logInfo = "getting httpsServletResponse";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		@SuppressWarnings("unused")
		HttpServletResponse response = (HttpServletResponse) ec.getResponse();
		return "logout";
	}

	/**
	 * SessionManagement.saveUserSettings takes care of saving the current
	 * status of the project on logout.
	 */
	private void saveUserSettings() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SessionManagement.saveUserSettings", logFile);
		ArrayList<String> settingsList = new ArrayList<String>();
		UserSettings settings = (UserSettings) ((UserSettings) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userSettings")).getSettings();
		auxillary.FileIOMethods.appendStringToFile(settings.getLang(), logFile);
		String langSettings = "<language=\"" + settings.getLang() + "\"/>";
		settingsList.add(langSettings);
		List<SelectItem> lexiconList = settings.getLexiconList();
		if (lexiconList != null) {
			String lexListStart = "<lexiconList>";
			settingsList.add(lexListStart);
			for (int a = 0; a < lexiconList.size(); a++) {
				String lexStart = "<lexicon>";
				settingsList.add(lexStart);
				String id = "<id=\"" + a + "\"/>";
				settingsList.add(id);
				String value = "<value=\"" + lexiconList.get(a).toString()
						+ "\"/>";
				settingsList.add(value);
				String lexEnd = "</lexicon>";
				settingsList.add(lexEnd);
			}
			String lexListEnd = "</lexiconList>";
			settingsList.add(lexListEnd);
		}
		List<SelectItem> projectList = settings.getProjectList();
		if (projectList != null) {
			String projectListStart = "<projectList>";
			settingsList.add(projectListStart);
			for (int a = 0; a < projectList.size(); a++) {
				String projectStart = "<project>";
				settingsList.add(projectStart);
				String id = "<id=\"" + a + "\"/>";
				settingsList.add(id);
				String value = "<value=\"" + projectList.get(a).toString()
						+ "\"/>";
				settingsList.add(value);
				String projectEnd = "</project>";
				settingsList.add(projectEnd);
			}
			String projectListEnd = "</projectList>";
			settingsList.add(projectListEnd);
		}
		auxillary.FileIOMethods.writeListToFile(settingsList, "UserName.settings");
	}

	/**
	 * SessionManagement.continueLogin takes care of loading the correct or new
	 * project after login based on information by the user. It takes as
	 * argument
	 * 
	 * @param event
	 *            - ActionEvent calling the method after login.
	 */
	@SuppressWarnings("deprecation")
	public void continueLogin(ActionEvent event) {
		logoutText = "Logout(" + UserSettings.receiveUserName() + ")";
		String logFile = UserSettings.getLogFileName();
		String logInfo = "continue login";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String id = event.getComponent().getId();
		logInfo = "ID " + id;

		if (id.equalsIgnoreCase("newProject")) {
			logInfo = "continueLogin new Project " + newProjectName;
			String userPath = ((UserSettings) (FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("userSettings"))).getUserPath();
			String projectFileName = userPath + newProjectName + ".etp";
			File projectFile = new File(projectFileName);
			if (projectFile.exists()) {
				PopupManager pm = (PopupManager) (FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("popupManager"));
				pm.setArbitraryErrorValue("Project with this name already exists!");
				pm.setShowArbitraryError(true);
				return;
			} else {
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				ProjectIOMethods.newProject(newProjectName);
				globalProjectName = newProjectName;
				if ((EditorManager) FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get("editor") != null) {
					((EditorManager) FacesContext.getCurrentInstance()
							.getExternalContext().getSessionMap().get("editor"))
							.setResult("");
				}
			}

		} else {
			logInfo = "continueLogin " + projectName;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			editorValue = ProjectIOMethods.openProject(projectName,
					FacesContext.getCurrentInstance());
			if ((EditorManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("editor") != null) {
				((EditorManager) FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get("editor"))
						.setResult(editorValue);
			}
			globalProjectName = projectName;
			auxillary.FileIOMethods.appendStringToFile(projectName, logFile);
		}
		System.out.println("After checking that project does not yet exist.");
		showEditor = true;
		showWelcome = false;
		tabIndex = 1;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
	}

	/**
	 * SessionManagement.projectSetting takes as argument
	 * 
	 * @param event
	 *            - ValueChangeEvent in order to set the proper project name for
	 *            loading.
	 */
	public void projectSetting(ValueChangeEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ValueChangeListener Select Project "
				+ event.getNewValue().toString();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		projectName = event.getNewValue().toString();
	}

	/**
	 * SessionManagement.lexiconSetting takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method
	 * 
	 *            sets the lexicon name.
	 */
	public void lexiconSetting(ValueChangeEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ValueChangeListener Select Lexicon"
				+ event.getNewValue().toString();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setLexiconName(event.getNewValue().toString());
	}

	/**
	 * SessionManagement.openLexicon takes as argument
	 * 
	 * @param e
	 *            - ActionEvent that triggered this method
	 * 
	 *            Opens a lexicon selected from the list of possible lexica.
	 */
	public void openLexicon(ActionEvent e) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SessionManagement.openLexicon";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		LexiconService ls = (LexiconService) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("table");
		UserSettings settings = (UserSettings) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("userSettings");
		logInfo = "LexName? " + lexiconName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (lexiconName.contains("/var/www/") == false) {
			lexiconName = settings.getUserPath() + lexiconName;
			if (lexiconName.endsWith(".etl") == false) {
				lexiconName = lexiconName + ".etl";
			}
			logInfo = "LexName After Change " + lexiconName;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		PopupManager pm = (PopupManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("popupManager");
		pm.setLoadLexicon(false);
		ls.openLexicon(lexiconName);
	}

	/**
	 * SessionManagement.removeProject takes as argument
	 * 
	 * @param e
	 *            - ActionEvent that triggered this method
	 * 
	 *            Removes a project from the list of projects created by the
	 *            user.
	 */
	public void removeProject(ActionEvent e) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SessionManagement.removeProject", logFile);
		auxillary.FileIOMethods.appendStringToFile(
				"SessionManagement.removeProject", logFile);
		auxillary.FileIOMethods.appendStringToFile("Selected Project "
				+ projectName, logFile);
		PopupManager pm = (PopupManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("popupManager");
		pm.setArbitraryErrorValue("This will permanently remove the selected Project!");
		pm.setShowArbitraryError(true);
	}

	/**
	 * SessionManagement.newProjectName takes as argument
	 * 
	 * @param event
	 *            - ValueChangeEvent to create a new project using the name
	 *            given by the user.
	 */
	public void newProjectName(ValueChangeEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ValueChangeListener New Project"
				+ event.getNewValue().toString();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setNewProjectName(event.getNewValue().toString());
	}

	/**
	 * SessionManangement.cancelPopupAction takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method
	 * 
	 *            closes a popup without further action
	 */
	public void cancelPopupAction(ActionEvent event) {

		System.out.println("CancelPopupAction " + event.getComponent().getId());
		if (event.getComponent().getId().equals("cancel2")) {
			showWarningPopup = false;
		} else {
			if (event.getComponent().getId().equals("save")) {
				FacesContext fc = FacesContext.getCurrentInstance();
				ProjectIOMethods.saveProject(projectName, fc);
			} else {
				showWarningPopup = false;
			}
		}

		showWelcome = true;
		tabIndex = 0;
		showEditor = false;

		TuningManager tm = ((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"));
		if (tm != null) {
			tm.setRenderLanguageEditor(false);
			tm.setRenderPhoneticEditor(false);
			tm.setRenderStressEditor(false);
			tm.setShowLangTuning(false);
			tm.setShowPhonetic(false);
			tm.setShowStress(false);

			tm.setLangTuneArray(null);
			tm.setPauseLength("0");
			tm.setPhoneticArray(null);
			tm.setSelection(null);
			tm.setSize("0");
			tm.setStressArray(null);
			tm.setSyllStressArray(null);
			tm.setSyllablesSize("0");
		}
		SubtitlingManager sm = ((SubtitlingManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("subtitlingManager"));
		if (sm != null) {
			sm.setShowConstraints(false);
			sm.setShowPreview(false);
		}
		String path = ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"))
				.getUserPath();
		String fileName = path + "empty.wav";
		String plainFileName = fileName.replace("wav", "");
		plainFileName = plainFileName.replace("/var/www", "");
		showWarningPopup = false;
	}

	/**
	 * SessionManagement.openProject takes as argument
	 * 
	 * @param event
	 *            - ActionEvent calling this method which opens a project
	 *            selected by the user, closing the welcome tab and opening the
	 *            main editor along with any other information saved in the
	 *            previous usage of the specific project.
	 */
	public void openProject(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SessionManagement.openProject ";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		System.out.println("setting Show Warning auf true");
		setShowWarningPopup(true);
	}

	/**
	 * SessionManagement.startDevelopment takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method.
	 * 
	 *            Opens the dialogue for the development of new voices for usage
	 *            in the TTS Editor.
	 */
	public void startDevelopment(ActionEvent event) {
		showWelcome = false;
		setShowDevWizard(true);
		tabIndex = 11;
	}

	/**
	 * SessionManagement.returnToLogin returns to the login screen after logout.
	 * 
	 * @param event
	 */
	public void returnToLogin(ActionEvent event) {
		tabIndex = 0;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for Lexicon objects.
	 */
	/**
	 * @param showEditor
	 *            the showEditor to set
	 */
	public void setShowEditor(boolean showEditor) {
		this.showEditor = showEditor;
	}

	/**
	 * @return the showEditor
	 */
	public boolean isShowEditor() {
		return showEditor;
	}

	/**
	 * @param showWelcome
	 *            the showWelcome to set
	 */
	public void setShowWelcome(boolean showWelcome) {
		this.showWelcome = showWelcome;
	}

	/**
	 * @return the showWelcome
	 */
	public boolean isShowWelcome() {
		return showWelcome;
	}

	/**
	 * @param tabIndex
	 *            the tabIndex to set
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * @return the tabIndex
	 */
	public int getTabIndex() {
		return tabIndex;
	}

	/**
	 * @param projectName
	 *            the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param newProjectName
	 *            the newProjectName to set
	 */
	public void setNewProjectName(String newProjectName) {
		this.newProjectName = newProjectName;
	}

	/**
	 * @return the newProjectName
	 */
	public String getNewProjectName() {
		return newProjectName;
	}

	/**
	 * @param editorValue
	 *            the editorValue to set
	 */
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}

	/**
	 * @return the editorValue
	 */
	public String getEditorValue() {
		return editorValue;
	}

	/**
	 * @param lexiconName
	 *            the lexiconName to set
	 */
	public void setLexiconName(String lexiconName) {
		this.lexiconName = lexiconName;
	}

	/**
	 * @return the lexiconName
	 */
	public String getLexiconName() {
		return lexiconName;
	}

	/**
	 * @param showDevWizard
	 *            the showDevWizard to set
	 */
	public void setShowDevWizard(boolean showDevWizard) {
		this.showDevWizard = showDevWizard;
	}

	/**
	 * @return the showDevWizard
	 */
	public boolean isShowDevWizard() {
		return showDevWizard;
	}

	/**
	 * @param showWarningPopup
	 *            the showWarningPopup to set
	 */
	public void setShowWarningPopup(boolean showWarningPopup) {
		this.showWarningPopup = showWarningPopup;
	}

	/**
	 * @return the showWarningPopup
	 */
	public boolean isShowWarningPopup() {
		return showWarningPopup;
	}

	/**
	 * @param logoutText
	 *            the logoutText to set
	 */
	public void setLogoutText(String logoutText) {
		this.logoutText = logoutText;
	}

	/**
	 * @return the logoutText
	 */
	public String getLogoutText() {
		return logoutText;
	}

	/**
	 * @param showTherapist
	 *            the showTherapist to set
	 */
	public void setShowTherapist(boolean showTherapist) {
		this.showTherapist = showTherapist;
	}

	/**
	 * @return the showTherapist
	 */
	public boolean isShowTherapist() {
		return showTherapist;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/

}
