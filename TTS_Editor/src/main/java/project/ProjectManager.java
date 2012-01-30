/**
 * ProjectManager reads and stores the settings for the user, the project and 
 * the overall settings for the TTS Editor.
 * 
 */
package project;

import java.util.ArrayList;

import javax.faces.component.UIData;

/**
 * TTSEditor.project.ProjectManager.java
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         10.11.2010 MaMi
 */
public class ProjectManager {

	private boolean showSettings;
	private String size;
	private ArrayList<Settings> settingsArray;
	private UIData settingsTable;

	public ProjectManager() {
	}

	/**
	 * @param showSettings
	 *            the showSettings to set
	 */
	public void setShowSettings(boolean showSettings) {
		this.showSettings = showSettings;
	}

	/**
	 * @return the showSettings
	 */
	public boolean isShowSettings() {
		return showSettings;
	}

	/**
	 * @param settingsTable
	 *            the settingsTable to set
	 */
	public void setSettingsTable(UIData settingsTable) {
		this.settingsTable = settingsTable;
	}

	/**
	 * @return the settingsTable
	 */
	public UIData getSettingsTable() {
		return settingsTable;
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
	 * @param settingsArray
	 *            the settingsArray to set
	 */
	public void setSettingsArray(ArrayList<Settings> settingsArray) {
		this.settingsArray = settingsArray;
	}

	/**
	 * @return the settingsArray
	 */
	public ArrayList<Settings> getSettingsArray() {
		return settingsArray;
	}

}
