/**
 * The SubtitlingManager takes care of actions regarding the import and handling
 * of subtitling projects that are imported or created during the interaction
 * with the editor.
 */
package importTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import popups.PopupManager;
import recognition.RecognitionItem;
import recognition.Recognizer;
import settings.userSettings.UserSettings;
import synthesis.SynthesisManager;
import tuning.TuningManager;
import auxillary.ConfigurationProperties;
import auxillary.ConfigurationProperties.PathKeys;
import auxillary.Services;

import com.icesoft.faces.async.render.SessionRenderer;

import data.Constraints;
import data.Sentences;
import editor.EditorManager;

/**
 * EMLTTSEditorWeb.importTools.SubtitlingManager.java offers methods for dealing
 * with subtitling/audio description projects.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         14.01.2011 MaMi
 */
public class SubtitlingManager {

	private static boolean showConstraints = false;
	private static ArrayList<Constraints> constraintList;
	private UIData constraintTable;
	private static ArrayList<Sentences> sentenceList;
	private int[] durations;
	private static boolean showPreview = false;
	private String flashvars;
	private boolean showPlayer = false;
	private boolean showUpload = true;
	private String file;
	private String previousDescription;
	private String nextDescription;
	private ArrayList<Constraints> reducedList;
	private String currentDescription;
	private int currentPos = 0;
	String valueChangeData;
	String transcriptionBackground = "#7A7A7A";
	private String previousTimeIndex;
	private String currentTimeIndex;
	private String nextTimeIndex;
	private boolean showSplittingPopup = false;
	private String splittingInfo;
	private String evenSplitting;
	private String splitSeconds;
	private int currentRowPos;
	private boolean showWaiting = false;
	private List<SelectItem> langList;
	private String recoLang;
	String soundOnlyFile;

	private static Log log = LogFactory.getLog(SubtitlingManager.class);
	private static final String baseWWWPath;

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
		baseWWWPath = pathProperties.getProperty(PathKeys.KEY_BASE_WWW_PATH);
	}

	/**
	 * Constructor for creating a new SubtitlingManager object
	 */
	public SubtitlingManager() {
		constraintList = new ArrayList<Constraints>();
		sentenceList = new ArrayList<Sentences>();
		setShowConstraints(false);
		setShowPreview(false);
		SessionRenderer.addCurrentSession("sub");
	}

	/**
	 * SubtitlingManager.determineDuration takes as argument
	 * 
	 * @param event
	 *            - ActionEvent calling the method for determining the duration
	 *            of the synthesized sentence.
	 * 
	 *            24. June 2011: Changed to give a guess-timate of the duration
	 *            based on the number of characters in the field. Thus allowing
	 *            for faster processing at run-time.
	 */
	public void determineDuration(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SubtitlingManager.determineDuration!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		int index = constraintTable.getRowIndex();
		logInfo = "Index " + index;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		logInfo = "sentenceList " + sentenceList.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		logInfo = "constraintList " + constraintList.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		String sentence = constraintList.get(index).getValue();
		logInfo = "CurrentSentence " + sentence;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		long approxDuration = sentence.length() * 45;

		long durationLong = 0;
		if (constraintList.get(index).getDuration().contains(":")) {
			durationLong = transformDuration(constraintList.get(index)
					.getDuration());
		} else {
			durationLong = Long.parseLong(constraintList.get(index)
					.getDuration());
		}

		if (approxDuration > durationLong) {
			logInfo = "Exact Duration is Longer than allowed duration!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
	}

	/**
	 * SubtitlingManager.transformDuration takes as argument
	 * 
	 * @param duration
	 *            - String representing the duration set in the constraints
	 *            file, which has the format 10:mm:ss:msms:ff(25) and calculates
	 *            the milliseconds
	 * @return - long representing the duration constraint of a sentence in ms.
	 */
	public static long transformDuration(String duration) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SubtitlingManager.transformDuration!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		auxillary.FileIOMethods.appendStringToFile(duration, logFile);
		String[] durationElems = duration.split(":");
		long returnDuration = Long.parseLong(durationElems[0]) * 25;
		auxillary.FileIOMethods.appendStringToFile("returnDuration 1 "
				+ returnDuration, logFile);
		long framesTime = Long.parseLong(durationElems[1]);
		auxillary.FileIOMethods.appendStringToFile("framesTime " + framesTime,
				logFile);
		returnDuration = (returnDuration + framesTime);
		auxillary.FileIOMethods.appendStringToFile("returnDuration 2 "
				+ returnDuration, logFile);
		double returnDurationDouble = (returnDuration / 25.0);
		auxillary.FileIOMethods.appendStringToFile("returnDurationDouble "
				+ returnDurationDouble, logFile);
		returnDuration = (long) (returnDurationDouble * 1000);
		auxillary.FileIOMethods.appendStringToFile("returnDuration final "
				+ returnDuration, logFile);
		return returnDuration;
	}

	/**
	 * SubtitlingManager.initShowConstraints initializes the tab in the editor
	 * for displaying the constraints set by the project.
	 */
	public static void initShowConstraints() {
		constraintList = importTools.SubtitlingImport.getConstraintList();
		sentenceList = importTools.SubtitlingImport.getSentenceList();
		for (int a = 0; a < constraintList.size(); a++) {
			int sentID = Integer.parseInt(constraintList.get(a).getSentID());
			constraintList.get(a).setValue(sentenceList.get(sentID).getValue());
			constraintList.get(a).setRealDuration("?");
		}

		setShowConstraints(true);
		setShowPreview(true);
	}

	/**
	 * SubtitlingManager.computeRealDuration takes as argument
	 * 
	 * @param fullFileName
	 *            - String representing the temporarily synthesized speech,
	 *            which is saved to a file.
	 * @return - long representing the duration of the currently synthesized
	 *         speech.
	 */
	public static long computeRealDuration(String fullFileName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SubtitlingManager.computeRealDuration!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		AudioInputStream audioInputStream = null;
		File file = new File(fullFileName);

		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
		} catch (Exception e) {
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("Could not get AudioInputStream.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Could not get AudioInputStream!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}

		AudioFormat audioFormat = audioInputStream.getFormat();

		float sampleRate = audioFormat.getSampleRate();
		float frameLength = audioInputStream.getFrameLength();
		try {
			audioInputStream.close();
		} catch (IOException e) {
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("Could not close AudioInputStream.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Could not close AudioInputStream!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}

		long duration = (long) Math.floor(frameLength * 1000 / sampleRate);
		logInfo = "Duration: " + duration;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		return duration;
	}

	/**
	 * SubtitlingManager.getConstraintDuration extracts the correct duration set
	 * by the constraint for the selected sentence.
	 * 
	 * @return - long representing the duration set by the constraints for the
	 *         selected sentence.
	 */
	public static long getConstraintDuration() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.getConstraintDuration", logFile);
		int index = getIndexOfSelection();
		auxillary.FileIOMethods.appendStringToFile("INDEX " + index, logFile);
		if (index > -1) {
			if (constraintList.get(index).getDuration().length() > 0) {
				long returnConstraint = Long.parseLong(constraintList
						.get(index).getDuration());
				return returnConstraint;
			}
		}
		return -1;
	}

	/**
	 * SubtitlingManager.getIndexOfSelection takes the selected items and
	 * determines the index in the constraintList of the specified items.
	 * 
	 * @return - int representing the position of the selection in the overall
	 *         constraintList.
	 */
	public static int getIndexOfSelection() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SubtitlingManager.getIndexOfSelection!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String selection = ((TuningManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("tuningManager"))
				.getSelection();
		if (selection != null) {
			if (selection.length() > 0) {
				logInfo = selection;
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				String[] selectionElems = selection.split("\t");
				int start = Integer.parseInt(selectionElems[0]);
				int end = Integer.parseInt(selectionElems[1]);
				int currentTextLength = 0;
				for (int a = 0; a < constraintList.size(); a++) {
					String value = constraintList.get(a).getValue();
					logInfo = value;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					currentTextLength = currentTextLength + value.length();
					if (currentTextLength > start && currentTextLength <= end) {
						return a;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * SubtitlingManager.recoLangListener takes as argument
	 * 
	 * @param vce
	 *            - valueChangeEvent that triggered this method.
	 * 
	 *            Determines the language that should be used for the
	 *            recognition of the movie.
	 */
	public void recoLangListener(ValueChangeEvent vce) {
		recoLang = vce.getNewValue().toString();
	}

	/**
	 * SubtitlingManager.prepareMovie provides methods for including an uploaded
	 * movie in the editor. It takes as arguments
	 * 
	 * @param absolutePath
	 *            - pointing to the uploaded movie file
	 * 
	 *            Two possibilities for uploading a movie exist: 1.) A
	 *            description has already been uploaded and the movie is to
	 *            follow. In this case the descriptions are displayed along with
	 *            the movie. 2.) The movie starts a new project. In this case
	 *            the movie is analyzed before automatically created information
	 *            is loaded and displayed. Based on the automatic information,
	 *            the descriptions can be created.
	 */
	public void prepareMovie(String absolutePath) throws IOException {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.prepareMovie", logFile);
		auxillary.FileIOMethods.appendStringToFile("prepareMovie "
				+ absolutePath, logFile);
		auxillary.FileIOMethods.appendStringToFile("showPlayer " + showPlayer,
				logFile);
		absolutePath = absolutePath.replace(baseWWWPath, "");
		setFile(absolutePath);
		soundOnlyFile = auxillary.AudioTools.extractAndSaveSound(absolutePath);
		String timestamp = "?timestamp=" + (new Date()).getTime();
		String captionFileName = "";
		if (isShowConstraints() == true) {
			captionFileName = createCaptionsFromDescriptions();
			if (absolutePath.endsWith(".mp4")) {
				auxillary.FileIOMethods.appendStringToFile("Ends with MP4",
						logFile);

				if (captionFileName == null) {
					auxillary.FileIOMethods.appendStringToFile(
							"CaptionFileName is NULL", logFile);
				} else {
					auxillary.FileIOMethods.appendStringToFile(captionFileName,
							logFile);
					setFlashvars("file=" + absolutePath + timestamp);
					auxillary.FileIOMethods.appendStringToFile("FLASHVARS???? "
							+ getFlashvars(), logFile);
				}
			} else {
				String absolutePathNew = "";
				setFlashvars("file=" + absolutePathNew + timestamp);
				auxillary.FileIOMethods.appendStringToFile("FLASHVARS???? "
						+ getFlashvars(), logFile);
			}
			analyzeCaptionFile(captionFileName);
			setShowPlayer(true);
			setShowUpload(false);
			auxillary.FileIOMethods.appendStringToFile("isShowPlayer "
					+ isShowPlayer(), logFile);
		} else {
			auxillary.FileIOMethods.appendStringToFile(
					"isShowConstrainsts is false!!!", logFile);
			auxillary.FileIOMethods.appendStringToFile(soundOnlyFile, logFile);
			auxillary.FileIOMethods.appendStringToFile("VideoTools.RUN! "
					+ soundOnlyFile, logFile);
			Recognizer reco = Services.getRecognizer();
			List<RecognitionItem> result = reco.recognize(soundOnlyFile);

			constraintList = new ArrayList<Constraints>();
			for (RecognitionItem item : result) {
				Constraints newConstraint = new Constraints();
				newConstraint.setEnd(String.valueOf(item.getEnd()));
				newConstraint.setStart(String.valueOf(item.getStart()));
				newConstraint.setValue(item.getWord());
				newConstraint.setTranscription(true);
				constraintList.add(newConstraint);
			}

			showPreview = true;
			showPlayer = true;
			showConstraints = true;
			showUpload = false;
			setConstraintList(constraintList);
			setReducedList(constraintList);
			createDescriptions();
			String movieFile = absolutePath.replace(".wmv", "tmp.mp4");
			setFlashvars("file=" + movieFile + timestamp);
			auxillary.FileIOMethods.appendStringToFile(
					"FLashVars " + flashvars, logFile);
		}
	}

	/**
	 * SubtitlingManager.analyzeCaptionFile takes as argument
	 * 
	 * @param captionFileName
	 *            - String representing the caption file
	 * 
	 *            and transforms the caption file into constraints for audio
	 *            description.
	 */
	void analyzeCaptionFile(String captionFileName) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile("analyze Caption File "
				+ captionFileName, logFile);
		auxillary.FileIOMethods.appendStringToFile(captionFileName, logFile);
		ArrayList<String> fileContent = auxillary.FileIOMethods
				.getFileContent(captionFileName);
		auxillary.FileIOMethods.appendStringToFile(
				"trying to get data from file " + captionFileName, logFile);
		ArrayList<Constraints> constraintList = new ArrayList<Constraints>();
		auxillary.FileIOMethods.appendStringToFile(
				"Initializing constraintList", logFile);
		String text = "";
		auxillary.FileIOMethods.appendStringToFile("Init text", logFile);
		Constraints constraints = new Constraints();
		auxillary.FileIOMethods.appendStringToFile("Init single constraint",
				logFile);
		ArrayList<String> sentences = new ArrayList<String>();
		if (fileContent != null) {
			auxillary.FileIOMethods.appendStringToFile("CaptionFileContent "
					+ fileContent.size(), logFile);
			auxillary.FileIOMethods.appendStringToFile(
					"FileContent is NOT null! " + fileContent.size(), logFile);
			for (int a = 0; a < fileContent.size(); a++) {
				auxillary.FileIOMethods.appendStringToFile(fileContent.get(a)
						.toString() + " " + a + " " + fileContent.size(),
						logFile);
				String line = fileContent.get(a).toString()
						.replaceAll(",", ":");
				auxillary.FileIOMethods.appendStringToFile("Replaced " + line,
						logFile);
				if (SubtitlingImport.checkNumber(line)) {
					if (line.contains(" ")) {
						String[] lineElems = line.split(" ");
						constraints = new Constraints();
						auxillary.FileIOMethods.appendStringToFile(
								"Start + End " + lineElems[0] + " "
										+ lineElems[1], logFile);
						if (lineElems.length > 2) {
							constraints.setStart(lineElems[0]);
							constraints.setEnd(lineElems[2]);
							constraintList.add(constraints);
							if (text.length() > 0) {
								sentences.add(text);
								text = "";
							}
						} else {
							auxillary.FileIOMethods.appendStringToFile(
									"SOme Else", logFile);
							String currentLine = fileContent.get(a).toString();
							currentLine = currentLine.replace("</font>", "");
							currentLine = currentLine.replace(
									"<font color=\"#FFF00\">", "");
							text = text + fileContent.get(a).toString() + " ";
						}
					}
				} else {
					auxillary.FileIOMethods.appendStringToFile(
							"Some other else", logFile);
					String currentLine = fileContent.get(a).toString();
					currentLine = currentLine.replace("</font>", "");
					currentLine = currentLine.replace(
							"<font color=\"#FFF00\">", "");
					text = text + fileContent.get(a).toString() + " ";
				}
			}
		} else {
			auxillary.FileIOMethods.appendStringToFile("FileContent is NULL!",
					logFile);
		}
		auxillary.FileIOMethods.appendStringToFile("constraintList.size() "
				+ constraintList.size(), logFile);
		reducedList = new ArrayList<Constraints>();
		String start = "00:00:00:00";
		int counter = 0;
		String collectedText = "";
		for (int a = 0; a < constraintList.size() - 1; a++) {
			auxillary.FileIOMethods.appendStringToFile("ConstraintInfo "
					+ constraintList.get(a).getEnd() + " "
					+ constraintList.get(a + 1).getStart(), logFile);
			auxillary.FileIOMethods.appendStringToFile("constraintValue "
					+ sentences.get(counter).toString(), logFile);
			constraintList.get(a).setValue(sentences.get(a).toString());
			collectedText = collectedText + sentences.get(counter).toString()
					+ "\n";
			counter = counter + 1;
			double pause = SubtitlingImport.determinePause(
					constraintList.get(a), constraintList.get(a + 1));
			constraintList.get(a).setPause("" + pause);
			if (pause > 5000) {
				Constraints pauseConstraint = new Constraints();
				pauseConstraint.setStart(constraintList.get(a).getEnd());
				pauseConstraint.setEnd(constraintList.get(a + 1).getStart());
				pauseConstraint.setValue("PAUSE");
				pauseConstraint.setTranscription(false);
				reducedList.add(pauseConstraint);
				start = constraintList.get(a + 1).getStart();
			} else {
				Constraints constraint = new Constraints();
				constraint.setStart(start);
				constraint.setEnd(constraintList.get(a).getEnd());
				constraint.setValue(collectedText);
				constraint.setTranscription(true);
				collectedText = "";
				reducedList.add(constraint);
			}
		}

		setConstraintList(reducedList);
		auxillary.FileIOMethods.appendStringToFile("ReducedList.size() "
				+ reducedList.size(), logFile);
		for (int a = 0; a < reducedList.size(); a++) {
			auxillary.FileIOMethods.appendStringToFile(reducedList.get(a)
					.getStart()
					+ " "
					+ reducedList.get(a).getEnd()
					+ " "
					+ reducedList.get(a).getValue(), logFile);
		}
		setShowConstraints(true);
		createDescriptions();
	}

	/**
	 * SubtitlingManager.createDescriptions creates the descriptions for the the
	 * current time step for display in the preview view.
	 */
	private void createDescriptions() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"subtitlingManager.createDescriptions", logFile);
		auxillary.FileIOMethods.appendStringToFile(getCurrentPos() + " "
				+ reducedList.size(), logFile);
		if (getCurrentPos() < reducedList.size()) {
			currentTimeIndex = reducedList.get(getCurrentPos()).getStart()
					+ " " + reducedList.get(getCurrentPos()).getEnd();
			currentDescription = reducedList.get(getCurrentPos()).getValue();
			if (getCurrentPos() > 0) {
				previousTimeIndex = reducedList.get(getCurrentPos() - 1)
						.getStart()
						+ " "
						+ reducedList.get(getCurrentPos() - 1).getEnd();
				previousDescription = reducedList.get(getCurrentPos() - 1)
						.getValue();
			} else {
				previousTimeIndex = "NA";
				previousDescription = "NA";
			}
			if ((getCurrentPos() + 1) > reducedList.size()) {
				nextTimeIndex = "NA";
				nextDescription = "NA";
			} else {
				nextTimeIndex = reducedList.get(getCurrentPos() + 1).getStart()
						+ " " + reducedList.get(getCurrentPos() + 1).getEnd();
				nextDescription = reducedList.get(getCurrentPos() + 1)
						.getValue();
			}
		} else {
			auxillary.FileIOMethods.appendStringToFile(
					"something is wrong ...", logFile);
		}
	}

	/**
	 * SubtitlingManager.loadNewFile takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this event.
	 * 
	 *            It allows for uploading a new movie file to the preview tab.
	 */
	public void loadNewFile(ActionEvent event) {
		showUpload = true;
		showPlayer = false;
	}

	/**
	 * SubtitlingManager.createNewCaptions takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method.
	 * 
	 *            It adds data from the descriptions as captions to be displayed
	 *            in the preview.
	 */
	public void createNewCaptions(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.createNewCaptions", logFile);
		String captionFileName = createCaptionFile("someMovie");
		String timestamp = "?timestamp=" + (new Date()).getTime();
		if (captionFileName == null) {
			auxillary.FileIOMethods.appendStringToFile(
					"CaptionFileName is NULL", logFile);
		} else {
			auxillary.FileIOMethods
					.appendStringToFile(captionFileName, logFile);
			flashvars = "file=" + getFile() + timestamp
					+ "&plugins=captions&captions.file=" + captionFileName;
			auxillary.FileIOMethods.appendStringToFile("FLASHVARS???? "
					+ flashvars, logFile);
		}
	}

	/**
	 * SubtitlingManager.descriptionChangeListener takes as argument
	 * 
	 * @param vce
	 *            - ValueChangeEvent that triggered this method
	 * 
	 *            It waits for changes in the description editor and registers
	 *            these changes for further usage.
	 */
	public void descriptionChangeListener(ValueChangeEvent vce) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.descriptionChangeListener", logFile);
		auxillary.FileIOMethods.appendStringToFile(
				"newValue " + vce.getNewValue(), logFile);
		valueChangeData = (String) vce.getNewValue();
	}

	/**
	 * SubtitlingManager.goToPreviousChung takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered the method.
	 * 
	 *            It moves the active chunk one step back and creates the
	 *            appropriate descriptions of the previous and following parts.
	 *            This is the partner method for goToNextChunk.
	 */
	public void goToPreviousChunk(ActionEvent event) {
		setCurrentPos(getCurrentPos() - 1);
		createDescriptions();
	}

	/**
	 * SubtitlingManager.applyChanges takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method.
	 * 
	 *            It applies the changes made to the descriptions,
	 *            transcriptions etc. to the other tabs (editor and constraints
	 *            view).
	 */
	public void applyChanges(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.ApplyChanges! " + valueChangeData, logFile);
		auxillary.FileIOMethods.appendStringToFile("getCurrentPos "
				+ getCurrentPos(), logFile);
		String currentConstraintValue = reducedList.get(getCurrentPos())
				.getValue();
		auxillary.FileIOMethods.appendStringToFile(currentConstraintValue,
				logFile);
		if (reducedList.get(getCurrentPos()).isTranscription() == false) {
			auxillary.FileIOMethods.appendStringToFile(
					"currentConstraingValue contains PAUSE!", logFile);
			if (currentConstraintValue.equals("PAUSE")) {
				currentConstraintValue = valueChangeData;
			} else {
				currentConstraintValue = valueChangeData;
			}
		}
		auxillary.FileIOMethods.appendStringToFile(currentConstraintValue,
				logFile);
		reducedList.get(getCurrentPos()).setValue(currentConstraintValue);
		setConstraintList(reducedList);
		EditorManager em = (EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor");
		String newResult = "";
		for (int a = 0; a < reducedList.size(); a++) {
			if (reducedList.get(a).isTranscription() == false) {
				if (reducedList.get(a).getValue().equals("PAUSE") == false) {
					newResult = newResult + reducedList.get(a).getValue()
							+ "\n";
				}
			}
		}
		em.setResult(newResult);
	}

	/**
	 * SubtitlingManager.showChunk takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggers the method.
	 * 
	 *            It prepares to preview the currently active part of the
	 *            uploaded movie.
	 */
	public void showChunk(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.showChunk " + event.getComponent().getId()
						+ currentPos, logFile);
		int currentPosTMP = currentPos;
		if (event.getComponent().getId().equals("showPrev")) {
			currentPosTMP = currentPosTMP - 1;
		}
		if (event.getComponent().getId().equals("showNext")) {
			currentPosTMP = currentPosTMP + 1;
		}
		String startTime = constraintList.get(currentPosTMP).getStart();
		String endTime = constraintList.get(currentPosTMP).getEnd();
		long startDuration = transformDurationNew(startTime);
		auxillary.FileIOMethods.appendStringToFile(startDuration + " " + startTime, logFile);
		long end = SubtitlingManager.transformDurationNew(endTime);
		auxillary.FileIOMethods.appendStringToFile(end + " " + endTime, logFile);
		long duration = (end - startDuration) / 1000;
		long startNew = startDuration / 1000;
		flashvars = "file="
				+ file
				+ "&duration=" + duration + "&start=" + startNew
				+ "&provider=http";
		auxillary.FileIOMethods.appendStringToFile(flashvars, logFile);
	}

	/**
	 * SubtitlingManager.goToNextChunk takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggers this method.
	 * 
	 *            It moves the active chunk one step forward and creates the
	 *            descriptions for the surrounding chunks. This is the partner
	 *            method for goToPreviousChunk.
	 */
	public void goToNextChunk(ActionEvent event) {
		setCurrentPos(getCurrentPos() + 1);
		createDescriptions();
	}

	/**
	 * SubtitlingManager.goToPreviousPause takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method.
	 * 
	 *            It allows for moving to the previous available pause in the
	 *            data. This might jump over several chunks. This is the partner
	 *            method for goToNextPause.
	 */
	public void goToPreviousPause(ActionEvent event) {
		setCurrentPos(findPreviousPause());
		if (getCurrentPos() > -1) {
			createDescriptions();
		} else {
			PopupManager pm = (PopupManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("popupManager");
			pm.setArbitraryErrorValue("ERROR No Previous Pause");
			pm.setShowArbitraryError(true);
		}
	}

	/**
	 * SubtitlingManager.findPreviousPause
	 * 
	 * @return - integer representing the position of the previous pause in the
	 *         description data.
	 */
	private int findPreviousPause() {
		for (int a = getCurrentPos() - 1; a >= 0; a--) {
			if (reducedList.get(a).isTranscription() == false) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * SubtitlingManager.goToNextPause takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method
	 * 
	 *            It allows for moving to the next available pause in the data.
	 *            This might jump over several chunks. It is the partner method
	 *            for goToPreviousPause.
	 */
	public void goToNextPause(ActionEvent event) {
		setCurrentPos(findNextPause());
		if (getCurrentPos() > -1) {
			createDescriptions();
		} else {
			PopupManager pm = (PopupManager) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("popupManager");
			pm.setArbitraryErrorValue("ERROR No next Pause");
			pm.setShowArbitraryError(true);
		}
	}

	/**
	 * SubtitlingManager.findNextPause
	 * 
	 * @return - integer representing the position of the next pause in the
	 *         description data.
	 */
	private int findNextPause() {
		for (int a = getCurrentPos() + 1; a < reducedList.size(); a++) {
			if (reducedList.get(a).isTranscription() == false) {
				return a;
			}
		}
		return -1;
	}

	/**
	 * SubtitlingManager.showOnlyPauses takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method.
	 * 
	 *            It allows to display only the pauses in the constraint view,
	 *            rather than all data for all chunks available.
	 */
	public void showOnlyPauses(ActionEvent event) {
		ArrayList<Constraints> newList = new ArrayList<Constraints>();
		for (int a = 0; a < reducedList.size(); a++) {
			if (reducedList.get(a).isTranscription() == false) {
				newList.add(reducedList.get(a));
			}
		}
		constraintList = newList;
	}

	/**
	 * SubtitlingManager.createCaptionFile takes as argument
	 * 
	 * @param absolutePath
	 *            - String representing the fileName to which the captions
	 *            should be written.
	 * @return - String representing the changed caption file name.
	 */
	private String createCaptionFile(String absolutePath) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.createCaptionFile", logFile);
		ArrayList<Constraints> constraintList = getConstraintList();
		String captionFileName = null;
		ArrayList<String> captionContent = new ArrayList<String>();
		if (constraintList == null) {
			auxillary.FileIOMethods.appendStringToFile("NO CONSTRAINTS!",
					logFile);
		} else {
			captionFileName = absolutePath;
			for (int a = 0; a < constraintList.size(); a++) {
				Constraints constraint = (Constraints) constraintList.get(a);
				String value = constraint.getValue();
				String start = constraint.getStart();
				int index = start.lastIndexOf(":");
				start = start.substring(0, index) + ","
						+ start.substring(index + 1);
				start = start.replaceFirst("01", "00");
				auxillary.FileIOMethods.appendStringToFile(start, logFile);
				String end = constraint.getEnd();
				index = end.lastIndexOf(":");
				end = end.substring(0, index) + "," + end.substring(index + 1);
				end = end.replaceFirst("01", "00");
				auxillary.FileIOMethods.appendStringToFile(end, logFile);
				String newContent = a + "\n" + start + " --> " + end + "\n"
						+ value + "\n";
				captionContent.add(newContent);
			}
		}
		if (captionContent != null) {
			auxillary.FileIOMethods.appendStringToFile(
					"captionContent NOT null", logFile);
			auxillary.FileIOMethods.writeListToFile(captionContent,
					captionFileName);
		}
		captionFileName = captionFileName.replace("/var/www", "");
		return captionFileName;
	}

	/**
	 * SubtitlingManager.transformDurationNew takes as argument
	 * 
	 * @param time
	 *            - String representing a time index
	 * @return - long representing a transformed time index in ms
	 */
	public static long transformDurationNew(String time) {
		String[] timeElems = time.split(":");
		long frameTime = 0;
		if (timeElems.length > 3) {
			if (Long.parseLong(timeElems[3]) > 25) {
				frameTime = Long.parseLong(timeElems[3]);
			} else {
				frameTime = (Long.parseLong(timeElems[3]) / 25) * 1000;
			}
		}
		long seconds = Long.parseLong(timeElems[2]) * 1000;
		frameTime = (frameTime + seconds);
		long minutes = Long.parseLong(timeElems[1]) * 60 * 1000;
		frameTime = (frameTime + minutes);
		long hours = Long.parseLong(timeElems[0]) * 60 * 60 * 1000;
		frameTime = frameTime + hours;
		return frameTime;
	}

	/**
	 * SubtitlingManager.closePopupAction takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered the method.
	 * 
	 *            It closes popups that might have been opened.
	 */
	public void closePopupAction(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.closePopupAction "
						+ event.getComponent().getId(), logFile);
		auxillary.FileIOMethods.appendStringToFile("Information even"
				+ evenSplitting, logFile);
		auxillary.FileIOMethods.appendStringToFile("Information seconds "
				+ splitSeconds, logFile);
		if (event.getComponent().getId().equals("cancel2")) {
			// perform Action!
			auxillary.FileIOMethods.appendStringToFile("Only this instance "
					+ evenSplitting + " " + splitSeconds, logFile);
			if (evenSplitting.trim().length() > 0) {
				createEvenSplitting();
			} else {
				if (splitSeconds.trim().length() > 0) {
					createSecondsSplitting();
				}
			}
		}
		splitSeconds = null;
		evenSplitting = null;
		constraintList = reducedList;
		setShowSplittingPopup(false);
	}

	/**
	 * SubtitlingManager.createSecondsSplitting allows for splitting constraint
	 * elements into chunks of the same length in seconds. The appropriate
	 * amount of elements in the constraints view is added.
	 */
	private void createSecondsSplitting() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.createSecondsSplitting", logFile);
		auxillary.FileIOMethods.appendStringToFile("splitSeconds "
				+ splitSeconds + " " + getCurrentRowPos(), logFile);
		splitSeconds = "" + (Long.parseLong(splitSeconds) * 1000);
		long startTrans = transformDurationNew(constraintList.get(
				getCurrentRowPos()).getStart());
		long endTrans = transformDurationNew(constraintList.get(
				getCurrentRowPos()).getEnd());
		auxillary.FileIOMethods.appendStringToFile(startTrans + " " + endTrans,
				logFile);
		int counter = 0;
		while (startTrans < endTrans) {
			auxillary.FileIOMethods.appendStringToFile(startTrans + " "
					+ splitSeconds, logFile);
			long newEnd = startTrans + Long.parseLong(splitSeconds);
			Constraints newConstraint = new Constraints();
			String newStartTime = transformBack(startTrans);
			String newEndTime = transformBack(newEnd);
			newConstraint.setStart("" + newStartTime);
			newConstraint.setEnd("" + newEndTime);
			newConstraint.setValue("PAUSE");
			startTrans = newEnd;
			if (counter == 0) {
				reducedList.set(getCurrentRowPos() + counter, newConstraint);
			} else {
				reducedList.add(getCurrentRowPos() + counter, newConstraint);
			}
			counter = counter + 1;
		}
	}

	/**
	 * SubtitlingManager.createEvenSplitting allows for splitting constraints
	 * into the given amount of chunks evenly.
	 */
	private void createEvenSplitting() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile("EvenSplitting "
				+ evenSplitting + " " + getCurrentRowPos(), logFile);
		long startTrans = transformDurationNew(constraintList.get(
				getCurrentRowPos()).getStart());
		long endTrans = transformDurationNew(constraintList.get(
				getCurrentRowPos()).getEnd());
		auxillary.FileIOMethods.appendStringToFile(startTrans + " " + endTrans,
				logFile);
		long duration = endTrans - startTrans;
		double stretches = duration / Double.parseDouble(evenSplitting);
		for (int a = 0; a < Integer.parseInt(evenSplitting); a++) {
			long newEnd = (long) (startTrans + stretches);
			auxillary.FileIOMethods.appendStringToFile("newEnd " + newEnd,
					logFile);
			Constraints newConstraint = new Constraints();
			String newStartTime = transformBack(startTrans);
			auxillary.FileIOMethods.appendStringToFile(newStartTime, logFile);
			String newEndTime = transformBack(newEnd);
			auxillary.FileIOMethods.appendStringToFile(newEndTime, logFile);
			newConstraint.setStart("" + newStartTime);
			newConstraint.setEnd("" + newEndTime);
			newConstraint.setValue("PAUSE");
			startTrans = newEnd;
			if (a == 0) {
				reducedList.set(getCurrentRowPos() + a, newConstraint);
			} else {
				reducedList.add(getCurrentRowPos() + a, newConstraint);
			}
		}
	}

	/**
	 * SubtitlingManager.transformBack takes as argument
	 * 
	 * @param transTime
	 *            - long representing a time index
	 * @return - String representing the time index
	 */
	private static String transformBack(long transTime) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.transformBack", logFile);
		auxillary.FileIOMethods.appendStringToFile("TransTime " + transTime,
				logFile);

		String[] timeElems = { "00", "00", "00", "00" };
		String returnString = "";
		long reduction = 0;
		while (transTime > 1000) {
			if ((transTime / 1000) > 1) {
				long seconds = transTime / 1000;
				if (seconds / 60 > 1) {
					long minutes = seconds / 60;
					if ((minutes / 60) > 1) {
						String hrs = "" + minutes / 60;
						timeElems[0] = hrs;
						reduction = Long.parseLong(hrs) * 60 * 60 * 1000;
					} else {
						timeElems[1] = "" + minutes;
						reduction = minutes * 60 * 1000;
					}
				} else {
					timeElems[2] = "" + seconds;
					reduction = seconds * 1000;
				}
			} else {
				timeElems[3] = "" + ((transTime / 1000) * 25);
			}
			transTime = transTime - reduction;
		}
		for (int a = 0; a < timeElems.length; a++) {
			returnString = returnString + timeElems[a] + ":";
		}
		returnString = returnString.substring(0, returnString.length() - 1);
		return returnString;
	}

	/**
	 * SubtitlingManager.splittingFactorChange takes as argument
	 * 
	 * @param vce
	 *            - ValueChangeEvent that triggered this method.
	 * 
	 *            This takes care of noticing changes when the splitting methods
	 *            are called.
	 */
	public void splittingFactorChange(ValueChangeEvent vce) {
		if (vce.getComponent().getId().equals("evenSplitting")) {
			evenSplitting = (String) vce.getNewValue();
			splitSeconds = null;
		}
		if (vce.getComponent().getId().equals("splitSeconds")) {
			splitSeconds = (String) vce.getNewValue();
			evenSplitting = null;
		}
	}

	/**
	 * SubtitlingManager.createPreviewSoFar takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method
	 * 
	 *            This creates the preview of the movie with both subtitles and
	 *            synthesized descriptions up to the current position in the
	 *            movie.
	 */
	public void createPreviewSoFar(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile("createPreviewSoFar",
				logFile);
		auxillary.FileIOMethods.appendStringToFile(
				"Synthesize Descriptions up to this point", logFile);
		auxillary.FileIOMethods.appendStringToFile(
				"Gathering Descriptions up to here!", logFile);

		String fullDescription = getFullDescription();
		SynthesisManager sm = SynthesisManager.get();
		String synthFile = "previewSynth.wav";
		sm.synthesize(fullDescription, synthFile);

		String soundFile = soundOnlyFile;
		String startTime = constraintList.get(currentPos).getStart();
		String endTime = constraintList.get(currentPos).getEnd();

		long fullDuration = computeRealDuration(soundFile);
		long startDuration = SubtitlingManager.transformDurationNew(startTime);
		long end = SubtitlingManager.transformDurationNew(endTime);
		synthFile = auxillary.AudioTools.addSilence(synthFile, startDuration, end,
				fullDuration);

		String soundAllFile = auxillary.AudioTools.mixSound(soundFile, synthFile);

		auxillary.FileIOMethods.appendStringToFile(
				"    do magic to sound and video of current piece", logFile);

		String output = auxillary.AudioTools.mixSoundAndMovie(soundAllFile, file);

		auxillary.FileIOMethods.appendStringToFile(
				"Create Captions for Descriptions", logFile);

		String captionFile = createCaptionsFromDescriptions();
		auxillary.FileIOMethods.appendStringToFile(
				"Set Flashvars to new Movie", logFile);
		String timestamp = "?timestamp=" + (new Date()).getTime();
		startDuration = 0;
		auxillary.FileIOMethods.appendStringToFile(startDuration + " " + startTime, logFile);
		end = SubtitlingManager.transformDurationNew(endTime);
		auxillary.FileIOMethods.appendStringToFile(end + " " + endTime, logFile);
		long duration = (end - startDuration) / 1000;
		long startNew = startDuration / 1000;
		flashvars = "file=" + output + timestamp
				+ "&plugins=captions&captions.file=" + captionFile
				+ "&duration=" + duration + "&start=" + startNew; 
		auxillary.FileIOMethods.appendStringToFile(flashvars, logFile);
	}

	/**
	 * SubtitlingManager.createCaptionsFromDescriptions
	 * 
	 * @return - String representing the new captions file, which contains the
	 *         descriptions in .srt-format.
	 */
	private String createCaptionsFromDescriptions() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"SubtitlingManager.createCaptionsFromDescriptions", logFile);
		String path = ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"))
				.getUserPath();
		String captionFileName = path + "caption.srt";
		ArrayList<String> captionContent = new ArrayList<String>();
		if (reducedList == null) {
			reducedList = constraintList;
		}
		for (int a = 0; a < reducedList.size(); a++) {
			Constraints constraint = (Constraints) reducedList.get(a);
			if (constraint.isTranscription() == false) {
				if (constraint.getValue().equals("PAUSE") == false) {
					String value = constraint.getValue();
					String start = constraint.getStart();
					int index = start.lastIndexOf(":");
					start = start.substring(0, index) + ","
							+ start.substring(index + 1);
					start = start.replaceFirst("01", "00");
					auxillary.FileIOMethods.appendStringToFile(start, logFile);
					String end = constraint.getEnd();
					index = end.lastIndexOf(":");
					end = end.substring(0, index) + ","
							+ end.substring(index + 1);
					end = end.replaceFirst("01", "00");
					auxillary.FileIOMethods.appendStringToFile(end, logFile);
					String newContent = a + "\n" + start + " --> " + end + "\n"
							+ value + "\n";
					captionContent.add(newContent);
				}
			}
		}
		auxillary.FileIOMethods
				.writeListToFile(captionContent, captionFileName);
		return captionFileName;
	}

	/**
	 * SubtitlingManager.getFullDescription
	 * 
	 * @return - String containing all descriptions in the constraint list that
	 *         are not transcriptions.
	 */
	private String getFullDescription() {
		String returnDescription = "";
		for (int a = 0; a < reducedList.size(); a++) {
			if (reducedList.get(a).isTranscription() == false) {
				returnDescription = returnDescription
						+ reducedList.get(a).getValue();
			}
			if (reducedList.get(a).getStart().equals(currentTimeIndex)) {
				return returnDescription;
			}
		}
		return returnDescription;
	}

	/**
	 * SubtitlingManager.createCurrentPreview takes as argument
	 * 
	 * @param event
	 *            - ActionEvent that triggered this method.
	 * 
	 *            It allows for creating the preview of the current chunk
	 *            including the transcription and synthesized description.
	 */
	public void createCurrentPreview(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile("createCurrentPreview",
				logFile);
		auxillary.FileIOMethods.appendStringToFile(
				"Synthesize Description of this chunk", logFile);
		auxillary.FileIOMethods.appendStringToFile("Synthesizing "
				+ currentDescription, logFile);

		SynthesisManager sm = SynthesisManager.get();
		String synthFile = "syntFile.wav";
		sm.synthesize(currentDescription, synthFile);

		String soundFile = soundOnlyFile;
		String startTime = constraintList.get(currentPos).getStart();
		String endTime = constraintList.get(currentPos).getEnd();

		long fullDuration = computeRealDuration(soundFile);
		long startDuration = SubtitlingManager.transformDurationNew(startTime);
		long end = SubtitlingManager.transformDurationNew(endTime);
		synthFile = auxillary.AudioTools.addSilence(synthFile, startDuration, end,
				fullDuration);

		auxillary.FileIOMethods
				.appendStringToFile(
						"Create Captions for this chunk -- unnecessary for just one example!",
						logFile);
		auxillary.FileIOMethods.appendStringToFile("Add Synthesis to Movie",
				logFile);
		auxillary.FileIOMethods.appendStringToFile(
				"    extract current piece from movie", logFile);

		String soundAllFile = auxillary.AudioTools.mixSound(soundFile, synthFile);

		auxillary.FileIOMethods.appendStringToFile(
				"    do magic to sound and video of current piece", logFile);

		String output = auxillary.AudioTools.mixSoundAndMovie(soundAllFile, file);

		auxillary.FileIOMethods.appendStringToFile(
				"Set Flashvars to new Movie", logFile);

		auxillary.FileIOMethods.appendStringToFile(
				"Set Flashvars to new Movie", logFile);
		String timestamp = "?timestamp=" + (new Date()).getTime();
		startDuration = SubtitlingManager.transformDurationNew(startTime);
		auxillary.FileIOMethods.appendStringToFile(startDuration + " " + startTime, logFile);
		end = SubtitlingManager.transformDurationNew(endTime);
		auxillary.FileIOMethods.appendStringToFile(end + " " + endTime, logFile);
		long duration = (end - startDuration) / 1000;
		long startNew = startDuration / 1000;
		flashvars = "file=" + output + timestamp + "&duration=" + duration
				+ "&start=" + startNew;
		auxillary.FileIOMethods.appendStringToFile(flashvars, logFile);
	}

	/**
	 * SubtitlingManager.saveDescriptionsTMP is temporary method for saving the
	 * descriptions to a file.
	 * 
	 * @param fileNameTMP
	 */
	public static void saveDescriptionsTMP(String fileNameTMP) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods
				.appendStringToFile("SubtitlingManager.saveDescriptionsTMP "
						+ fileNameTMP, logFile);
		ArrayList<String> descriptionList = new ArrayList<String>();
		for (int a = 0; a < constraintList.size(); a++) {
			if (constraintList.get(a).isTranscription() == false) {
				String newEntry = constraintList.get(a).getStart() + " "
						+ constraintList.get(a).getEnd() + " ";
				if (constraintList.get(a).getDuration() != null) {
					newEntry = newEntry + constraintList.get(a).getDuration()
							+ "\n";
				} else {
					newEntry = newEntry
							+ calculateDuration(constraintList.get(a)
									.getStart(), constraintList.get(a).getEnd())
							+ "\n";
				}
				if (constraintList.get(a).getValue().length() > 35) {
					String newString = auxillary.StringOperations.trimToSize(
							constraintList.get(a).getValue(), 35);
					newEntry = newEntry + newString + "\n";
				} else {
					newEntry = newEntry + constraintList.get(a).getValue()
							+ "\n";
				}
				descriptionList.add(newEntry);
			}
		}
		auxillary.FileIOMethods.appendStringToFile("Descriptions to save "
				+ descriptionList.size(), logFile);
		auxillary.FileIOMethods.writeListToFile(descriptionList, fileNameTMP);
	}

	/**
	 * SubtitlingManager.calculateDuration takes as argument
	 * 
	 * @param start
	 *            - String representing the starttime
	 * @param end
	 *            - String representing the endtime
	 * @return - String representing the difference between start- and end-time
	 *         in ms.
	 */
	public static String calculateDuration(String start, String end) {
		long startNum = transformDurationNew(start);
		long endNum = transformDurationNew(end);
		String returnString = "";
		long diff = endNum - startNum;
		returnString = transformBack(diff);
		return returnString;
	}
	
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for Lexicon objects.
	 */
	/**
	 * @param showConstraints
	 *            the showConstraints to set
	 */
	public static void setShowConstraints(boolean showConstraints) {
		SubtitlingManager.showConstraints = showConstraints;
	}

	/**
	 * @return the showConstraints
	 */
	public boolean isShowConstraints() {
		return showConstraints;
	}

	/**
	 * @param constraintList
	 *            the constraintList to set
	 */
	@SuppressWarnings("static-access")
	public void setConstraintList(ArrayList<Constraints> constraintList) {
		this.constraintList = constraintList;
	}

	/**
	 * @return the constraintList
	 */
	public ArrayList<Constraints> getConstraintList() {
		return constraintList;
	}

	/**
	 * @param constraintTable
	 *            the contraintTable to set
	 */
	public void setConstraintTable(UIData constraintTable) {
		this.constraintTable = constraintTable;
	}

	/**
	 * @return the contraintTable
	 */
	public UIData getConstraintTable() {
		return constraintTable;
	}

	/**
	 * @param sentenceList
	 *            the sentenceList to set
	 */
	@SuppressWarnings("static-access")
	public void setSentenceList(ArrayList<Sentences> sentenceList) {
		this.sentenceList = sentenceList;
	}

	/**
	 * @return the sentenceList
	 */
	public ArrayList<Sentences> getSentenceList() {
		return sentenceList;
	}

	/**
	 * @param durations
	 *            the durations to set
	 */
	public void setDurations(int[] durations) {
		this.durations = durations;
	}

	/**
	 * @return the durations
	 */
	public int[] getDurations() {
		return durations;
	}

	/**
	 * @param showPreview
	 *            the showPreview to set
	 */
	public static void setShowPreview(boolean showPreview) {
		SubtitlingManager.showPreview = showPreview;
	}

	/**
	 * @return the showPreview
	 */
	public boolean isShowPreview() {
		return showPreview;
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
	 * @param showUpload
	 *            the showUpload to set
	 */
	public void setShowUpload(boolean showUpload) {
		this.showUpload = showUpload;
	}

	/**
	 * @return the showUpload
	 */
	public boolean isShowUpload() {
		return showUpload;
	}

	/**
	 * @param previousDescription
	 *            the previousDescription to set
	 */
	public void setPreviousDescription(String previousDescription) {
		this.previousDescription = previousDescription;
	}

	/**
	 * @return the previousDescription
	 */
	public String getPreviousDescription() {
		return previousDescription;
	}

	/**
	 * @param nextDescription
	 *            the nextDescription to set
	 */
	public void setNextDescription(String nextDescription) {
		this.nextDescription = nextDescription;
	}

	/**
	 * @return the nextDescription
	 */
	public String getNextDescription() {
		return nextDescription;
	}

	/**
	 * @param reducedList
	 *            the reducedList to set
	 */
	public void setReducedList(ArrayList<Constraints> reducedList) {
		this.reducedList = reducedList;
	}

	/**
	 * @return the reducedList
	 */
	public ArrayList<Constraints> getReducedList() {
		return reducedList;
	}

	/**
	 * @param currentDescription
	 *            the currentDescription to set
	 */
	public void setCurrentDescription(String currentDescription) {
		this.currentDescription = currentDescription;
	}

	/**
	 * @return the currentDescription
	 */
	public String getCurrentDescription() {
		return currentDescription;
	}

	/**
	 * @param previousTimeIndex
	 *            the previousTimeIndex to set
	 */
	public void setPreviousTimeIndex(String previousTimeIndex) {
		this.previousTimeIndex = previousTimeIndex;
	}

	/**
	 * @return the previousTimeIndex
	 */
	public String getPreviousTimeIndex() {
		return previousTimeIndex;
	}

	/**
	 * @param currentTimeIndex
	 *            the currentTimeIndex to set
	 */
	public void setCurrentTimeIndex(String currentTimeIndex) {
		this.currentTimeIndex = currentTimeIndex;
	}

	/**
	 * @return the currentTimeIndex
	 */
	public String getCurrentTimeIndex() {
		return currentTimeIndex;
	}

	/**
	 * @param nextTimeIndex
	 *            the nextTimeIndex to set
	 */
	public void setNextTimeIndex(String nextTimeIndex) {
		this.nextTimeIndex = nextTimeIndex;
	}

	/**
	 * @return the nextTimeIndex
	 */
	public String getNextTimeIndex() {
		return nextTimeIndex;
	}

	/**
	 * @param showSplittingPopup
	 *            the showSplittingPopup to set
	 */
	public void setShowSplittingPopup(boolean showSplittingPopup) {
		this.showSplittingPopup = showSplittingPopup;
	}

	/**
	 * @return the showSplittingPopup
	 */
	public boolean isShowSplittingPopup() {
		return showSplittingPopup;
	}

	/**
	 * @param splittingInfo
	 *            the splittingInfo to set
	 */
	public void setSplittingInfo(String splittingInfo) {
		this.splittingInfo = splittingInfo;
	}

	/**
	 * @return the splittingInfo
	 */
	public String getSplittingInfo() {
		return splittingInfo;
	}

	/**
	 * @param evenSplitting
	 *            the evenSplitting to set
	 */
	public void setEvenSplitting(String evenSplitting) {
		this.evenSplitting = evenSplitting;
	}

	/**
	 * @return the evenSplitting
	 */
	public String getEvenSplitting() {
		return evenSplitting;
	}

	/**
	 * @param splitSeconds
	 *            the splitSeconds to set
	 */
	public void setSplitSeconds(String splitSeconds) {
		this.splitSeconds = splitSeconds;
	}

	/**
	 * @return the splitSeconds
	 */
	public String getSplitSeconds() {
		return splitSeconds;
	}

	/**
	 * @param currentRowPos
	 *            the currentRowPos to set
	 */
	public void setCurrentRowPos(int currentRowPos) {
		this.currentRowPos = currentRowPos;
	}

	/**
	 * @return the currentRowPos
	 */
	public int getCurrentRowPos() {
		return currentRowPos;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param currentPos
	 *            the currentPos to set
	 */
	public void setCurrentPos(int currentPos) {
		this.currentPos = currentPos;
	}

	/**
	 * @return the currentPos
	 */
	public int getCurrentPos() {
		return currentPos;
	}

	/**
	 * @param showWaiting
	 *            the showWaiting to set
	 */
	public void setShowWaiting(boolean showWaiting) {
		this.showWaiting = showWaiting;
	}

	/**
	 * @return the showWaiting
	 */
	public boolean isShowWaiting() {
		return showWaiting;
	}

	/**
	 * @param langList
	 *            the langList to set
	 */
	public void setLangList(List<SelectItem> langList) {
		this.langList = langList;
	}

	/**
	 * @return the langList
	 */
	public List<SelectItem> getLangList() {
		return langList;
	}

	/**
	 * @param recoLang
	 *            the recoLang to set
	 */
	public void setRecoLang(String recoLang) {
		this.recoLang = recoLang;
	}

	/**
	 * @return the recoLang
	 */
	public String getRecoLang() {
		return recoLang;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/



}
