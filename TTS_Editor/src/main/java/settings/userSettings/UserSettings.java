/**
 * UserSettings contains methods for accessing settings that can be influenced
 * by the user of the TTS Editor Workplace, such as the interaction language.
 * This can currently be switched between German and English, which results in
 * feedback given to the user in either language. 
 */
package settings.userSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import synthesis.SynthesisManager;
import auxillary.ConfigurationProperties;

/**
 * EMLTTSEditorWeb.settings.userSettings.UserSettings.java
 * 
 * The messages presented to the user are set in the settings_LANG.properties
 * file. If the for example the interaction language is switched to English all
 * header and information given to the user is presented in English rather than
 * the standard German.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.11.2010 MaMi
 */
public class UserSettings {
	private static final Log log = LogFactory.getLog(UserSettings.class);

	public static UserSettings get() {
		return ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"));
	}

	private static final String PACKAGE_NAME = "settings.userSettings.settings";
	private static final String KEY_BASE_PATH = "baseUserPath";
	private static final String KEY_DEFAULT_ARCHIVE_PATH = "defaultsArchivePath";
	private static final String KEY_DEFAULT_SETTINGS_NAME = "defaultSettingsName";

	static {
		Properties pathProperties;
		try {
			pathProperties = ConfigurationProperties
					.getProperties(ConfigurationProperties.PATHS_PROPERTIES);
		} catch (IOException e) {
			log.error("Could not load "
					+ ConfigurationProperties.PATHS_PROPERTIES
					+ " file. We will probably crash soon.", e);
			pathProperties = new Properties();
		}
		baseUserPath = pathProperties.getProperty(KEY_BASE_PATH);
		defaultsArchiveFile = pathProperties.getProperty(
				KEY_DEFAULT_ARCHIVE_PATH, "./defaults/defaults.zip");
		defaultSettingsFile = pathProperties.getProperty(
				KEY_DEFAULT_SETTINGS_NAME, "default.settings");
	}

	private static final String baseUserPath;
	private static final String defaultsArchiveFile;
	private static final String defaultSettingsFile;

	private Map<String, String> settings;
	private String lang;
	private final String userName;
	private String logFile;
	private final String userPath;

	private List<SelectItem> lexiconList;
	private List<SelectItem> projectList;

	private int lexListSize;
	private int projectListSize;
	private String userSettingsFileName;

	/**
	 * Default Constructor for the UserSettings class. Sets the settings
	 * according to the settings saved for the particular User.
	 */
	@SuppressWarnings({ "static-access", "rawtypes" })
	public UserSettings() {
		userName = FacesContext.getCurrentInstance().getExternalContext()
				.getRemoteUser();
		userPath = baseUserPath + userName + "/";

		setUserSettingsFileName(userPath + userName + ".settings");
		assureUserSettingsExist(getUserSettingsFileName());
		String logFileName = "log_";
		String date = getCurrentDate();
		logFileName = logFileName + date;
		logFile = userPath + userName + "_" + logFileName + ".log";
		ArrayList userSettingsFileContent = auxillary.FileIOMethods
				.getFileContent(getUserSettingsFileName());
		UserData userData = new UserData(userSettingsFileContent);

		lang = userData.getLanguage();
		Locale locale = null;
		if (lang.equalsIgnoreCase("en")) {
			locale = locale.ENGLISH;
		}
		if (lang.equalsIgnoreCase("de")) {
			locale = locale.GERMAN;
		}
		ResourceBundle resourceBundle = ResourceBundle.getBundle(PACKAGE_NAME,
				locale);
		setSettings(new HashMap<String, String>());
		Enumeration<String> enumer = resourceBundle.getKeys();
		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			settings.put(key, resourceBundle.getString(key));
			String settingsInfo = key + "=" + resourceBundle.getString(key);
			auxillary.FileIOMethods.appendStringToFile(settingsInfo, logFile);
		}
		lexiconList = userData.lexiconList;
		String logInfo = "Lex Size: " + lexiconList.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setLexListSize(lexiconList.size());

		projectList = userData.projectList;
		logInfo = "Project Size " + projectList.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setProjectListSize(projectList.size());

	}

	/**
	 * UserSettings.checkExist takes as arguments
	 * 
	 * @param userSettingsFile
	 *            - String representing the file that contains the settings for
	 *            a particular user and checks whether a project of the
	 *            specified name already exists.
	 */
	private void assureUserSettingsExist(String userSettingsFile) {
		File userDir = new File(userPath);
		if (userDir.exists()) {
			File userSettings = new File(userSettingsFile);
			if (userSettings.exists()) {
				return;
			} else {
				copyDataFromDefaults();
				log.info("Directory for " + userName + " successfully created.");
			}
		} else {

			boolean createDir = (new File(userPath)).mkdirs();
			if (createDir) {
				copyDataFromDefaults();
				log.info("Directory for " + userName + " successfully created.");
			} else {
				log.warn("Could not create user directory.");
			}
		}
	}

	/**
	 * UserSettings.copyData takes as arguments
	 * 
	 * @param source
	 *            - File representing the original File
	 * @param userDir
	 *            - File representing the target directory
	 * 
	 *            In case a new user is logged in, the tutorial projects and
	 *            basic settings files are copied from a dummy user directory.
	 */
	private void copyDataFromDefaults() {
		ZipInputStream archive = new ZipInputStream(UserSettings.class
				.getClassLoader().getResourceAsStream(defaultsArchiveFile));
		ZipEntry entry;
		try {
			while ((entry = archive.getNextEntry()) != null) {
				String fileName = entry.getName();
				log.info("Extracting " + fileName + "...");
				if (fileName.equals(defaultSettingsFile))
					fileName = userName + ".settings";
				FileOutputStream out = new FileOutputStream(userPath + "/"
						+ fileName);
				IOUtils.copy(archive, out);
				IOUtils.closeQuietly(out);
			}
		} catch (Exception e) {
			log.error("Could not extract default to userDir " + userPath, e);
		}
	}

	/**
	 * UserSettings.getCurrentDate
	 * 
	 * @return - String representing the current date.
	 * 
	 *         This is for creating log files for user sessions.
	 */
	public static String getCurrentDate() {
		String DATE_FORMAT_NOW = "yyy-MM-dd HH:mm:ss";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	/**
	 * UserSettings.newLang takes as argument
	 * 
	 * @param event
	 *            - ValueChangeEvent triggering this method.
	 * 
	 *            Changes the interaction language of the Editor from German to
	 *            English or vice versa.
	 */
	public void newLang(ValueChangeEvent event) {
		String logInfo = "New Event Value " + (String) event.getNewValue();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		setLang((String) event.getNewValue());
		Locale locale = null;
		if (getLang().equalsIgnoreCase("en")) {
			locale = Locale.ENGLISH;
		}
		if (getLang().equalsIgnoreCase("de")) {
			locale = Locale.GERMAN;
		}
		ResourceBundle resourceBundle = ResourceBundle.getBundle(PACKAGE_NAME,
				locale);
		setSettings(new HashMap<String, String>());
		Enumeration<String> enumer = resourceBundle.getKeys();
		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			settings.put(key, resourceBundle.getString(key));
			logInfo = key + "=" + resourceBundle.getString(key);
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
	}

	/**
	 * @deprecated
	 */
	public static String receiveUserName() {
		return UserSettings.get().getUserName();
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for UserSettings objects.
	 */
	/**
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	/**
	 * @return the settings
	 */
	public Map<String, String> getSettings() {
		return settings;
	}

	/**
	 * @param lang
	 *            the lang to set
	 */
	private void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lexiconList
	 *            the lexiconList to set
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setLexiconList(ArrayList lexiconList) {
		this.lexiconList = lexiconList;
	}

	/**
	 * @return the lexiconList
	 */
	public List<SelectItem> getLexiconList() {
		return lexiconList;
	}

	/**
	 * @param projectList
	 *            the projectList to set
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setProjectList(ArrayList projectList) {
		this.projectList = projectList;
	}

	/**
	 * @return the projectList
	 */
	public List<SelectItem> getProjectList() {
		return projectList;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param lexListSize
	 *            the lexListSize to set
	 */
	public void setLexListSize(int lexListSize) {
		this.lexListSize = lexListSize;
	}

	/**
	 * @return the lexListSize
	 */
	public int getLexListSize() {
		return lexListSize;
	}

	/**
	 * @param projectListSize
	 *            the projectListSize to set
	 */
	public void setProjectListSize(int projectListSize) {
		this.projectListSize = projectListSize;
	}

	/**
	 * @return the projectListSize
	 */
	public int getProjectListSize() {
		return projectListSize;
	}

	/**
	 * @return the userPath
	 */
	public String getUserPath() {
		return userPath;
	}

	/**
	 * @param logFile
	 *            the logFile to set
	 */
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	/**
	 * @return the logFile
	 */
	public String getLogFile() {
		return logFile;
	}

	/**
	 * @deprecated
	 */
	public static String getLogFileName() {
		return UserSettings.get().getLogFile();
	}

	/**
	 * @param userSettingsFileName
	 *            the userSettingsFileName to set
	 */
	public void setUserSettingsFileName(String userSettingsFileName) {
		this.userSettingsFileName = userSettingsFileName;
	}

	/**
	 * @return the userSettingsFileName
	 */
	public String getUserSettingsFileName() {
		return userSettingsFileName;
	}

	/**
	 * @return the voiceList
	 */
	public List<SelectItem> getVoiceList() {
		LinkedList<SelectItem> voiceList = new LinkedList<SelectItem>();
		for (String name : SynthesisManager.get().getVoiceNames()) {
			voiceList.add(new SelectItem(name));
		}
		return voiceList;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
