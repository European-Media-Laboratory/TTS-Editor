/**
 * SynthesisManager contains methods for the general access to synthesizing
 * data entered by the user. This is first converted to the 
 * EML-TTS-representation of the data, next translated in the appropriate format
 * for the engines and afterwards passed to the synthesis engines. This is 
 * managed by this class, which currently contains the following methods:
 * 
 * synthesizeAction
 * convertToMp3
 * saveToFile
 * 
 * Added:
 * 
 * pitch
 * speed
 * 
 * Added:
 * 
 * synthesize
 * 
 */
package synthesis;

import importTools.SubtitlingManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import popups.PopupManager;
import settings.userSettings.UserSettings;
import ttsEngines.Language;
import ttsEngines.TTSEngine;
import ttsEngines.Voice;
import ttsEngines.Voice.SynthesisType;
import tuning.TuningManager;
import auxillary.AudioTools;
import auxillary.Services;
import data.Constraints;
import data.Sentences;
import editor.EditorManager;
import editor.PlayerManager;

/**
 * EMLTTSEditorWeb.synthesisSynthesisManager.java offers methods for mananging
 * the actual synthesis procedure, which primarily runs in the background of the
 * TTS Editor Environment. It currently offers the following methods:
 * 
 * synthesizeAction - triggers the preprocessing, translation and handing over
 * to the synthesis engine convertToMp3 - triggers the conversion of the
 * generated wav files to mp3 saveToFile - saves the synthesis results to a
 * specified file, rather than the default
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         22.10.2010 MaMi
 * 
 *         Added
 * 
 *         increase/decrease speed increase/decrease pitch
 * 
 *         Added
 * 
 *         synthesize for external access to the method to avoid creating a
 *         synthesisManager object.
 */
public class SynthesisManager {
	static String fileName;
	static String path;
	private boolean showError = false;
	private String speed = "50";
	private String pitch = "50";
	private boolean showSpeedIncreaseError = false;
	private boolean showSpeedDecreaseError = false;
	private boolean showPitchIncreaseError = false;
	private boolean showPitchDecreaseError = false;
	private String currentContent = "";
	private Voice currentVoice;
	private boolean contextPitchPopup = false;
	private boolean contextRatePopup = false;
	private String contextPitch = "50";
	private String contextRate = "50";
	private final HashMap<String, Voice> voices;

	/**
	 * Constructor for accessing the synthesisManager.
	 */
	public SynthesisManager() {
		voices = new HashMap<String, Voice>();
		for (TTSEngine engine : Services.getAvailableTTSEngines()) {
			for (Language lang : engine.getLanguages()) {
				for (Voice voice : lang.getVoices()) {
					voices.put(voice.getName(), voice);
				}
			}
		}
	}

	/**
	 * SynthesisManager.synthesizeAction takes as parameters
	 * 
	 * @param event
	 *            - ActionEvent which triggered the execution of this method.
	 * 
	 *            Based on the input by the user (editor, tuning, lexicon, etc.)
	 *            the text to be synthesized is transformed to the internal
	 *            EML-TTS-representation and translated to the appropriate
	 *            format for the engine. As the flash-player used for playback
	 *            of the generated sound files and the engines normally create
	 *            .wav files, these have to be converted to mp3 format.
	 */
	public void synthesizeAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SynthesisManager.synthesizeAction!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String eventId = event.getComponent().getId();
		String content = "";
		logInfo = eventId;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (eventId.contains("contextSynthesis")) {
			logInfo = "EventID contains CONTEXTSYNTHESIS!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			content = auxillary.StringOperations
					.extractSelection(
							((TuningManager) FacesContext.getCurrentInstance()
									.getExternalContext().getSessionMap()
									.get("tuningManager")).getSelection(),
							((EditorManager) FacesContext.getCurrentInstance()
									.getExternalContext().getSessionMap()
									.get("editor")).getResult());
			currentContent = content;
		} else {
			if (((EditorManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("editor"))
					.getResult().length() < 1) {
				showError = true;
			} else {
				content = ((EditorManager) FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get("editor"))
						.getResult();
			}
		}
		logInfo = "enrichted " + content;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		path = ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"))
				.getUserPath();
		fileName = path + "tmp.wav";
		logInfo = "PATH " + path;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "CONTENT before sending to SYNTH " + content;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		String convertedContent = currentVoice.getEngine().getConverter()
				.convert(content);
		currentVoice.synthesize(convertedContent, fileName);

		logInfo = "fileName? " + fileName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Synthesized to " + fileName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Encoding as mp3 ....";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String mp3FileName = fileName.replace("wav", "mp3");
		if (new File(mp3FileName).exists()) {
			new File(mp3FileName).delete();
		}
		convertToMp3(mp3FileName);
		logInfo = "mp3FileName Before " + mp3FileName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		mp3FileName = mp3FileName.replace("/var/www", "");
		logInfo = "mp3FileName After " + mp3FileName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		PlayerManager.setNewFlashVars(mp3FileName, false);
		String fullPathFileName = fileName;

		SubtitlingManager sm = ((SubtitlingManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("subtitlingManager"));
		if (sm.isShowConstraints()) {
			logInfo = "Selection? "
					+ ((TuningManager) FacesContext.getCurrentInstance()
							.getExternalContext().getSessionMap()
							.get("tuningManager")).getSelection();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			long realDuration = SubtitlingManager
					.computeRealDuration(fullPathFileName);
			long constraintDuration = SubtitlingManager.getConstraintDuration();
			logInfo = realDuration + " " + realDuration;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (realDuration > constraintDuration) {
				logInfo = "SHOW WARNING!!!";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				popups.PopupManager.setShowConstraintWarning(true);
			}
		}
	}

	/**
	 * SynthesisManager.closePopupAction takes no argument and closes error
	 * message displays.
	 */
	public void closePopupAction() {
		showError = false;
		showSpeedIncreaseError = false;
		showSpeedDecreaseError = false;
		showPitchIncreaseError = false;
		showPitchDecreaseError = false;
		contextPitchPopup = false;
		contextRatePopup = false;
	}

	/**
	 * SynthesisManager.convertToMp3 takes as arguments
	 * 
	 * @param mp3FileName
	 *            - String representing the output file to which the wav file is
	 *            to be converted.
	 */
	static void convertToMp3(String mp3FileName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SynthesisManager.convertToMp3";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (fileName == null) {
			fileName = mp3FileName.replace(".mp3", ".wav");
		}
		if (fileName.length() < 1) {
			fileName = mp3FileName.replace(".mp3", ".wav");
		}
		if (mp3FileName.endsWith("mp3")) {
			AudioTools.convertWav2MP3(fileName, mp3FileName);
		} else {
			AudioTools.convertWav2Flv(fileName, mp3FileName);
		}
	}

	/**
	 * SynthesisManager.saveToFile takes as arguments
	 * 
	 * @param sentences
	 *            - String representation of the text to be synthesized
	 * @param saveName
	 *            - String representation of the fileName to which the synthesis
	 *            result should be saved.
	 */
	public void saveToFile(Sentences sentences, String saveName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SynthesisManager.saveToFile!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		fileName = path + saveName;
		logInfo = "enrichted " + sentences;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String convertedContent = currentVoice.getEngine().getConverter()
				.convert(sentences);
		currentVoice.synthesize(convertedContent, fileName);
	}

	/**
	 * SynthesisManager.decreaseSpeed takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering this action, which decreases the
	 *            speaking rate by 5 points (0-100)
	 */
	public void decreaseSpeed(ActionEvent event) {
		if (Integer.parseInt(speed) == 0) {
			setShowSpeedDecreaseError(true);
		}
		speed = "" + (Integer.parseInt(speed) - 5);
		if (Integer.parseInt(speed) < 0) {
			speed = "0";
		}
	}

	/**
	 * SynthesisManager.increaseSpeed takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering this action, which increases the
	 *            speaking rate by 5 (0-100)
	 */
	public void increaseSpeed(ActionEvent event) {
		if (Integer.parseInt(speed) == 100) {
			setShowSpeedIncreaseError(true);
		}
		speed = "" + (Integer.parseInt(speed) + 5);
		if (Integer.parseInt(speed) > 100) {
			speed = "100";
		}
	}

	/**
	 * SynthesisManager.decreasePitch takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering this action, which decreases the
	 *            pitch of the voice by 5 points (0-100).
	 */
	public void decreasePitch(ActionEvent event) {
		if (Integer.parseInt(pitch) == 0) {
			setShowPitchDecreaseError(true);
		}
		pitch = "" + (Integer.parseInt(pitch) - 5);
		if (Integer.parseInt(pitch) < 0) {
			pitch = "0";
		}
	}

	/**
	 * SynthesisManager.increasePitch takes as arguments
	 * 
	 * @param event
	 *            - ActionEvent triggering this action, which increases the
	 *            pitch of the voice by 5 points (0-100).
	 */
	public void increasePitch(ActionEvent event) {
		if (Integer.parseInt(pitch) == 100) {
			setShowPitchIncreaseError(true);
		}
		pitch = "" + (Integer.parseInt(pitch) + 5);
		if (Integer.parseInt(pitch) > 100) {
			pitch = "100";
		}
	}

	/**
	 * SynthesisManager.synthesize takes as arguments
	 * 
	 * @param baseform
	 *            String representing input to be synthesized
	 * @param type
	 *            String defining the input type to be synthesized
	 * @param mp3FileName
	 */
	public String synthesize(String baseform, String type) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SynthesisManager.synthesize";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String synthesizeString = baseform;

		logInfo = "SynthesizeString " + synthesizeString;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (synthesizeString.length() > 0) {
			String fileName;
			try {
				fileName = File.createTempFile("ttseditor", ".wav")
						.getAbsolutePath();
				currentVoice.synthesize(
						type.equals("phonetic") ? SynthesisType.BASEFORM
								: SynthesisType.TEXT, synthesizeString,
						fileName);
				return fileName;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * SynthesisManager.fitToConstraintAuto takes as argument
	 * 
	 * @param e
	 *            - ActionEvent which called this function to automatically fit
	 *            a specific section of synthesized speech to the required
	 *            constraints.
	 */
	public void fitToConstraintAuto(ActionEvent e) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SynthesisManager.fitToConstraintAuto";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		long constraintDuration = SubtitlingManager.getConstraintDuration();
		String content = Preprocessing.enrichtContent(currentContent);
		logInfo = "enrichted " + content;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		content = "\\duration(start,value=" + constraintDuration + ") "
				+ content.replace(".", "") + "\\duration(end)";

		currentVoice.synthesize(content, fileName);
		SubtitlingManager sm = ((SubtitlingManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("subtitlingManager"));
		ArrayList<Constraints> constraintList = sm.getConstraintList();
		int index = SubtitlingManager.getIndexOfSelection();
		long realDuration = SubtitlingManager.computeRealDuration(fileName);
		if (index > -1) {
			constraintList.get(index).setRealDuration("" + realDuration);
		}
		PopupManager.setShowConstraintWarning(false);
	}

	/**
	 * SynthesisManager.saveToFile takes as argument
	 * 
	 * @param content
	 *            - String representing the content to by synthesized
	 * @param saveName
	 *            - String representing the fileName under which the file should
	 *            be saved.
	 */
	public void saveToFile(String content, String saveName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SaveToFile! " + saveName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (path == null) {
			auxillary.FileIOMethods
					.appendStringToFile("PATH IS NULL!", logFile);
			path = ((UserSettings) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userSettings"))
					.getUserPath();
			auxillary.FileIOMethods.appendStringToFile(path, logFile);
		}
		fileName = path + saveName;
		auxillary.FileIOMethods.appendStringToFile(fileName, logFile);
		currentVoice.synthesize(content, fileName);
	}

	/**
	 * SynthesisManager.voiceChangeListener takes as argument
	 * 
	 * @param vce
	 *            - ValueChangeEvent that takes care of noticing if the selected
	 *            voice has changed. This is necessary to direct the synthesis
	 *            request to the engine appropriate for the voice.
	 */
	public void voiceChangeListener(ValueChangeEvent vce) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "";
		if (vce.getNewValue() == null) {
			logInfo = "No Voices!";
		} else {
			logInfo = "ValueChangeListener Select Voice"
					+ vce.getNewValue().toString();
			currentVoice = voices.get(vce.getNewValue().toString());
		}
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
	}

	public void setNewVoice(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "ActionEvent Set New Voice"
				+ event.getPhaseId().toString();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		auxillary.FileIOMethods.appendStringToFile(currentVoice.getName(),
				logFile);
	}

	public String getCurrentVoiceName() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SynthesisManager.getCurrentVoiceName "
				+ currentVoice.getName();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		if (currentVoice != null) {
			auxillary.FileIOMethods.appendStringToFile(
					"currentVoice is not Null!", logFile);
			String[] currentVoiceElems = currentVoice.getName().split(" ");
			auxillary.FileIOMethods.appendStringToFile(""
					+ currentVoiceElems.length, logFile);
			auxillary.FileIOMethods.appendStringToFile(currentVoiceElems[0],
					logFile);
			return currentVoiceElems[0];
		}
		return null;
	}

	public void contextPitchTuning(ActionEvent event) {
		contextPitchPopup = true;
	}

	public void contextRateTuning(ActionEvent event) {
		contextRatePopup = true;
	}

	public void decreaseContextRate(ActionEvent event) {
		if (Integer.parseInt(contextRate) == 0) {
			setShowSpeedDecreaseError(true);
		}
		contextRate = "" + (Integer.parseInt(contextRate) - 5);
		if (Integer.parseInt(pitch) < 0) {
			pitch = "0";
		}
	}

	public void increaseContextRate(ActionEvent event) {
		if (Integer.parseInt(contextRate) == 100) {
			setShowSpeedIncreaseError(true);
		}
		contextRate = "" + (Integer.parseInt(contextRate) + 5);
		if (Integer.parseInt(contextRate) > 100) {
			contextRate = "100";
		}
	}

	public void decreaseContextPitch(ActionEvent event) {
		if (Integer.parseInt(contextPitch) == 0) {
			setShowPitchDecreaseError(true);
		}
		contextPitch = "" + (Integer.parseInt(contextPitch) - 5);
		if (Integer.parseInt(contextPitch) < 0) {
			contextPitch = "0";
		}
	}

	public void increaseContextPitch(ActionEvent event) {
		if (Integer.parseInt(contextPitch) == 100) {
			setShowPitchIncreaseError(true);
		}
		contextPitch = "" + (Integer.parseInt(contextPitch) + 5);
		if (Integer.parseInt(contextPitch) > 100) {
			contextPitch = "100";
		}
	}

	public void closeContextPitchChangeAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.closePauseInsertionAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Pause Length " + contextPitch;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Insert ContextPitch Information in Text!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String text = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		String selection = ((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getSelection();
		logInfo = "Selection " + selection;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		int posEnd = Integer.parseInt(selection.split("\t")[1]);
		int posStart = Integer.parseInt(selection.split("\t")[0]);
		String newText = text.substring(0, posStart) + " PITCH(" + contextPitch
				+ ") " + text.substring(posStart, posEnd) + " PITCH(DEFAULT)"
				+ text.substring(posEnd);
		((EditorManager) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("editor")).setResult(newText);
		contextPitchPopup = false;
	}

	public void closeContextRateChangeAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "TuningManager.closePauseInsertionAction";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Pause Length " + contextPitch;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Insert ContextPitch Information in Text!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String text = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		String selection = ((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getSelection();
		logInfo = "Selection " + selection;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		int posEnd = Integer.parseInt(selection.split("\t")[1]);
		int posStart = Integer.parseInt(selection.split("\t")[0]);
		String newText = text.substring(0, posStart) + " SPEED(" + contextRate
				+ ") " + text.substring(posStart, posEnd) + " SPEED(DEFAULT)"
				+ text.substring(posEnd);
		((EditorManager) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("editor")).setResult(newText);
		contextRatePopup = false;
	}

	public String synthesizeSingleSentence(String currentSentence) {
		path = ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"))
				.getUserPath();
		String fileName = path + "detDur.wav";
		currentVoice.synthesize(currentSentence, fileName);
		return fileName;
	}

	public static void convertToMp3(String inFile, String outFile) {
		AudioTools.convertWav2Flv(inFile, outFile);
	}
	
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for information from
	 * SynthesisManager.
	 */
	/**
	 * @param showError
	 *            the showError to set
	 */
	public void setShowError(boolean showError) {
		this.showError = showError;
	}

	/**
	 * @return the showError
	 */
	public boolean isShowError() {
		return showError;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}

	/**
	 * @return the speed
	 */
	public String getSpeed() {
		return speed;
	}

	/**
	 * @param showSpeedDecreaseError
	 *            the showSpeedDecreaseError to set
	 */
	public void setShowSpeedDecreaseError(boolean showSpeedDecreaseError) {
		this.showSpeedDecreaseError = showSpeedDecreaseError;
	}

	/**
	 * @return the showSpeedDecreaseError
	 */
	public boolean isShowSpeedDecreaseError() {
		return showSpeedDecreaseError;
	}

	/**
	 * @param showSpeedIncreaseError
	 *            the showSpeedIncreaseError to set
	 */
	public void setShowSpeedIncreaseError(boolean showSpeedIncreaseError) {
		this.showSpeedIncreaseError = showSpeedIncreaseError;
	}

	/**
	 * @return the showSpeedIncreaseError
	 */
	public boolean isShowSpeedIncreaseError() {
		return showSpeedIncreaseError;
	}

	/**
	 * @param pitch
	 *            the pitch to set
	 */
	public void setPitch(String pitch) {
		this.pitch = pitch;
	}

	/**
	 * @return the pitch
	 */
	public String getPitch() {
		return pitch;
	}

	/**
	 * @param showPitchIncreaseError
	 *            the showPitchIncreaseError to set
	 */
	public void setShowPitchIncreaseError(boolean showPitchIncreaseError) {
		this.showPitchIncreaseError = showPitchIncreaseError;
	}

	/**
	 * @return the showPitchIncreaseError
	 */
	public boolean isShowPitchIncreaseError() {
		return showPitchIncreaseError;
	}

	/**
	 * @param showPitchDecreaseError
	 *            the showPitchDecreaseError to set
	 */
	public void setShowPitchDecreaseError(boolean showPitchDecreaseError) {
		this.showPitchDecreaseError = showPitchDecreaseError;
	}

	/**
	 * @return the showPitchDecreaseError
	 */
	public boolean isShowPitchDecreaseError() {
		return showPitchDecreaseError;
	}

	/**
	 * @param currentContent
	 *            the currentContent to set
	 */
	public void setCurrentContent(String currentContent) {
		this.currentContent = currentContent;
	}

	/**
	 * @return the currentContent
	 */
	public String getCurrentContent() {
		return currentContent;
	}





	/**
	 * @param contextPitchPopup
	 *            the contextPitchPopup to set
	 */
	public void setContextPitchPopup(boolean contextPitchPopup) {
		this.contextPitchPopup = contextPitchPopup;
	}

	/**
	 * @return the contextPitchPopup
	 */
	public boolean isContextPitchPopup() {
		return contextPitchPopup;
	}

	/**
	 * @param contextRatePopup
	 *            the contextRatePopup to set
	 */
	public void setContextRatePopup(boolean contextRatePopup) {
		this.contextRatePopup = contextRatePopup;
	}

	/**
	 * @return the contextRatePopup
	 */
	public boolean isContextRatePopup() {
		return contextRatePopup;
	}

	/**
	 * @param contextPitch
	 *            the contextPitch to set
	 */
	public void setContextPitch(String contextPitch) {
		this.contextPitch = contextPitch;
	}

	/**
	 * @return the contextPitch
	 */
	public String getContextPitch() {
		return contextPitch;
	}

	/**
	 * @param contextRate
	 *            the contextRate to set
	 */
	public void setContextRate(String contextRate) {
		this.contextRate = contextRate;
	}

	/**
	 * @return the contextRate
	 */
	public String getContextRate() {
		return contextRate;
	}

	public Collection<String> getVoiceNames() {
		return voices.keySet();
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	
	public static SynthesisManager get() {
		return ((SynthesisManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("synthesize"));
	}

}
