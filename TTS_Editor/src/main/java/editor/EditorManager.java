/**
 * EditorManager contains methods for accessing user input entered via the
 * editor window. These are currently:
 * 
 * result
 * 
 * 22. October 2010 MaMi
 * 
 * Added:
 * 
 * editorChangeListener
 * applyChanges
 * 
 * 21. April 2011 MaMi
 */
package editor;

import importTools.SubtitlingManager;

import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import settings.session.SessionManagement;
import settings.userSettings.UserSettings;
import tuning.LangTune;
import tuning.Phonetic;
import tuning.PhoneticTuning;
import tuning.SyllStress;
import tuning.TuningManager;
import data.Constraints;

/**
 * EMLTTSEditorWeb.editor.EditorManager.java offers methods for accessing user
 * input entered via the editor window.
 * 
 * result - String representation of the text entered in the editor.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.10.2010 MaMi
 * 
 *         Added:
 * 
 *         editorChangeListener - registering changes in the main editor window
 *         applyChanges - apply changes that are done on the editor also to
 *         other parts (e.g. tuning tabs) of the overall TTS Editor
 * 
 *         21.04.2011 MaMi
 */
public class EditorManager {

	private String result;
	String valueChangeData;

	/**
	 * Empty constructor for creating a welcome content in the editor window.
	 */
	public EditorManager() {
		result = ((SessionManagement) (FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("sessionManagement")))
				.getEditorValue();
	}

	/**
	 * EditorManager.editorChangeListener takes as arguments
	 * 
	 * @param vce
	 *            - the event causing the changes on the editor
	 */
	public void editorChangeListener(ValueChangeEvent vce) {
		valueChangeData = (String) vce.getNewValue();
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ValueChangeListener " + result + " "
				+ valueChangeData;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
	}

	/**
	 * EditorManager.applyChanges takes as argument
	 * 
	 * @param event
	 *            - event triggering this event, which causes the changes made
	 *            in the main editor tab to be also applied to other tabs - e.g.
	 *            tuning methods.
	 */
	public void applyChanges(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "applyChanges ";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String[] resultElems = valueChangeData.split("\\s");
		logInfo = "ResultElemsLength " + resultElems.length;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		TuningManager tm = (TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager");
		if (tm.isShowLangTuning()) {
			ArrayList<LangTune> langTuneArray = tm.getLangTuneArray();

			int index = 0;
			for (int a = 0; a < resultElems.length; a++) {
				if (resultElems[a].equals(langTuneArray.get(index).getWord()) == false) {
					langTuneArray.get(index).setWord(resultElems[a]);
					langTuneArray.get(index).setLang("de");
				}
				index = index + 1;
			}
			if (langTuneArray.size() > index) {
				for (int b = index; b < langTuneArray.size(); b++) {
					langTuneArray.remove(b);
				}
			}

		}
		if (tm.isShowPhonetic()) {
			// set new text in phonetic tuning
			ArrayList<Phonetic> phoneticTuneArray = tm.getPhoneticArray();
			int index = 0;
			for (int a = 0; a < resultElems.length; a++) {
				if (resultElems[a].equals(phoneticTuneArray.get(index)
						.getWord()) == false) {
					phoneticTuneArray.get(index).setWord(resultElems[a]);
					phoneticTuneArray.get(index).setBaseform(
							PhoneticTuning.phonetiseString(resultElems[a]));
				}
				index = index + 1;
			}
			if (phoneticTuneArray.size() > index) {
				for (int b = index; b < phoneticTuneArray.size(); b++) {
					phoneticTuneArray.remove(b);
				}
			}
		}
		if (tm.isShowStress()) {
			ArrayList<SyllStress> stressTuneArray = tm.getSyllStressArray();
			int index = 0;
			for (int a = 0; a < resultElems.length; a++) {
				if (resultElems[a].equals(stressTuneArray.get(index).getWord()) == false) {
//					String baseform = 
					// change syllables!
				}
				index = index + 1;
			}
		}

		SubtitlingManager sm = (SubtitlingManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("subtitlingManager");
		if (sm.isShowConstraints()) {
			String[] resultLines = result.split("\n");
			int index = 0;
			ArrayList<Constraints> constraintList = sm.getConstraintList();
			for (int a = 0; a < resultLines.length; a++) {
				if (resultLines[a].equalsIgnoreCase(constraintList.get(index)
						.getValue()) == false) {
					constraintList.get(index).setValue(resultLines[a]);
				}
				index = index + 1;
			}
			sm.setConstraintList(constraintList);
		}
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for editor objects.
	 */
	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/

}
