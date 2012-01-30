/**
 * SubtitlingImport allows for importing subtitling projects, which contain
 * information about sentences to be synthesized, their duration and pauses in
 * between sentences.
 */
package importTools;

import java.io.File;
import java.util.ArrayList;

import javax.faces.context.FacesContext;

import settings.userSettings.UserSettings;
import data.Constraints;
import data.Data;
import data.DataManager;
import data.Sentences;
import editor.EditorManager;

/**
 * EMLTTSEditorWeb.importTools.SubtitlingImport.java contains methods for
 * importing text files containing sentences to be synthesized and their
 * constraints.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         14.01.2011 MaMi
 */
public class SubtitlingImport {

	private static ArrayList<Constraints> constraintList;
	private static ArrayList<Sentences> sentenceList;

	/**
	 * SubtitlingImport.importProject is the main method, which takes as
	 * argument
	 * 
	 * @param uploadedFile
	 *            - File representing the uploaded File containing the sentences
	 *            and their constraints.
	 */
	public static void importProject(File uploadedFile) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SubtitlingImport.importProject";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<String> fileContent = auxillary.FileIOMethods
				.getFileContent(uploadedFile.getAbsolutePath());
		sentenceList = new ArrayList<Sentences>();
		constraintList = new ArrayList<Constraints>();
		String text = "";
		int counter = 0;
		Constraints constraints = new Constraints();
		String[] constraintElems = null;
		for (int a = 0; a < fileContent.size(); a++) {
			logInfo = "File Content " + a + ": "
					+ fileContent.get(a).toString();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (fileContent.get(a).toString().contains("-->")) {
				String newLine = fileContent.get(a).toString()
						.replace("-->", "");
				newLine = newLine.trim();
				newLine = newLine.replace("  ", " ");
				fileContent.set(a, newLine);
			}
			if (checkNumber(fileContent.get(a).toString())) {
				logInfo = "is Number";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				if (text.length() > 1) {
					logInfo = "Text " + text;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					if (constraintElems != null) {
						constraints.setStart(constraintElems[0]);
						constraints.setEnd(constraintElems[1]);
						constraints.setTranscription(false);
						if (constraintElems.length > 2) {
							long duration = SubtitlingManager
									.transformDuration(constraintElems[2]);
							constraints.setDuration("" + duration);
						}

						Sentences newSent = new Sentences();
						newSent.setId("" + counter);
						constraints.setSentID(newSent.getId());
						constraintList.add(constraints);
						constraints = new Constraints();
						constraintElems = null;

						newSent.setValue(text);
						getSentenceList().add(newSent);
						text = "";
						counter = counter + 1;
					}
				}
				constraintElems = fileContent.get(a).toString().split(" ");
			} else {
				text = text + " " + fileContent.get(a).toString();
			}
		}

		if (constraintElems != null) {
			constraints.setStart(constraintElems[0]);
			constraints.setEnd(constraintElems[1]);
			if (constraintElems.length > 2) {
				constraints.setDuration(constraintElems[2]);
			}
			if (text.length() > 1) {
				Sentences newSent = new Sentences();
				newSent.setId("" + counter);
				newSent.setValue(text);
				getSentenceList().add(newSent);
				text = "";
				constraints.setSentID(newSent.getId());
				constraintList.add(constraints);
			}
			constraints = new Constraints();
			constraintElems = null;
		}
		logInfo = "COnstraintList!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		for (int a = 0; a < constraintList.size(); a++) {
			constraintList.get(a).setValue(sentenceList.get(a).getValue());
			logInfo = constraintList.get(a).getStart() + " "
					+ constraintList.get(a).getEnd() + " "
					+ constraintList.get(a).getDuration() + " "
					+ constraintList.get(a).getValue() + " "
					+ sentenceList.get(a).getValue();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (constraintList.get(a).getDuration() != null) {
				if (constraintList.get(a).getDuration().contains(":")) {
					long duration = SubtitlingManager
							.transformDuration(constraintList.get(a)
									.getDuration());
					constraintList.get(a).setDuration("" + duration);
				}
			}
		}
		determinePauses();
		logInfo = getSentenceList().size() + " " + constraintList.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		Data importData = new Data();
		importData.setSentenceList(getSentenceList());
		importData.setConstraintList(constraintList);
		String sentenceString = DataManager.extractSentences(getSentenceList());
		logInfo = "SentenceString " + sentenceString;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		((EditorManager) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("editor")).setResult(sentenceString);
		SubtitlingManager.initShowConstraints();
	}

	/**
	 * SubtitlingImport.determinePauses takes the constraints presented in the
	 * uploaded file and determines the pauses between the sentences. This is
	 * important to ensure that in these pauses there is silence.
	 */
	private static void determinePauses() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SubtitlingImport.determinePauses";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "SIZE: " + constraintList.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		for (int a = 0; a < constraintList.size() - 1; a++) {
			double pause = determinePause(constraintList.get(a),
					constraintList.get(a + 1));
			constraintList.get(a).setPause("" + pause);
		}
		logInfo = "Done Pauses!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
	}

	/**
	 * SubtitlingImport.determinePause takes as arguments
	 * 
	 * @param firstConstraint
	 *            - Constraints representing the first sentence
	 * @param secondConstraint
	 *            - Constraints representing the second sentence
	 * @return - double representing the pause between the two pauses.
	 */
	static double determinePause(Constraints firstConstraint,
			Constraints secondConstraint) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "SubtitlingImport.determinePause";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String pauseStart = firstConstraint.getEnd();
		String[] pauseStartElems = pauseStart.split(":");
		String pauseEnd = secondConstraint.getStart();
		String[] pauseEndElems = pauseEnd.split(":");
		double pauseStartFrames = 0.0;
		double pauseEndFrames = 0.0;
		double result = 0.0;
		if (compStartEndElem(pauseStartElems[0], pauseEndElems[0]) == false) {
			pauseStartFrames = pauseStartFrames
					+ (Integer.parseInt(pauseStartElems[0]) * 60 * 60);
			pauseEndFrames = pauseEndFrames
					+ (Integer.parseInt(pauseEndElems[0]) * 60 * 60);
		}
		if (compStartEndElem(pauseStartElems[1], pauseEndElems[1]) == false) {
			pauseStartFrames = pauseStartFrames
					+ (Integer.parseInt(pauseStartElems[1]) * 60);
			pauseEndFrames = pauseEndFrames
					+ (Integer.parseInt(pauseEndElems[1]) * 60);
		}
		if (compStartEndElem(pauseStartElems[2], pauseEndElems[2]) == false) {
			pauseStartFrames = ((pauseStartFrames + (Integer
					.parseInt(pauseStartElems[2]))) * 25);
			pauseEndFrames = ((pauseEndFrames + (Integer
					.parseInt(pauseEndElems[2]))) * 25);
		}
		if (pauseStartElems.length > 3) {
			pauseStartFrames = pauseStartFrames
					+ (Integer.parseInt(pauseStartElems[3]));
			pauseEndFrames = pauseEndFrames
					+ (Integer.parseInt(pauseEndElems[3]));
			logInfo = "FINAL " + pauseStartFrames + " " + pauseEndFrames;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			result = (((pauseEndFrames - pauseStartFrames) / 25) * 1000);
			logInfo = "" + result;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		return result;
	}

	/**
	 * SubtitlingImport.compStartEndElem takes as argument
	 * 
	 * @param startElem
	 *            - String representing the startElement
	 * @param endElem
	 *            - String representing the endElement
	 * @return - boolean whether the two elements are equal or not.
	 */
	private static boolean compStartEndElem(String startElem, String endElem) {
		if (startElem.equals(endElem)) {
			return true;
		}
		return false;
	}

	/**
	 * SubtitlingImport.checkNumber takes as argument
	 * 
	 * @param line
	 *            - String representing a line from the uploaded file
	 * @return - boolean whether the line is the time information or not.
	 */
	static boolean checkNumber(String line) {
		String[] lineElems = line.split(" ");
		if (lineElems.length > 3) {
			return false;
		}
		boolean returnValue = false;
		for (int a = 0; a < lineElems.length; a++) {
			String tmp = lineElems[a].replaceAll(":", "");
			try {
				Integer.parseInt(tmp);
				returnValue = true;
			} catch (NumberFormatException nfe) {
				returnValue = false;
			}
		}
		return returnValue;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for Lexicon objects.
	 */
	/**
	 * @param constraintList
	 *            the constraintList to set
	 */
	public void setConstraintList(ArrayList<Constraints> constraintList) {
		this.constraintList = constraintList;
	}

	/**
	 * @return the constraintList
	 */
	public static ArrayList<Constraints> getConstraintList() {
		return constraintList;
	}

	/**
	 * @param sentenceList
	 *            the sentenceList to set
	 */
	public void setSentenceList(ArrayList<Sentences> sentenceList) {
		this.sentenceList = sentenceList;
	}

	/**
	 * @return the sentenceList
	 */
	public static ArrayList<Sentences> getSentenceList() {
		return sentenceList;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
