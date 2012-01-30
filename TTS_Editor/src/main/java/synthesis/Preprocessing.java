/**
 * Preprocessing contains methods for processing data before it is sent to the
 * synthesizer for synthesis. These currently are:
 * 
 * enrichContent
 * changePhonInput
 * 
 * Added 
 * 
 * createSyllableText
 * findStartPos
 * isOneSyll
 * 
 */
package synthesis;

import importTools.SubtitlingManager;

import java.util.ArrayList;

import javax.faces.context.FacesContext;

import settings.userSettings.UserSettings;
import tuning.LangTune;
import tuning.Phonetic;
import tuning.PhoneticTuning;
import tuning.SyllStress;
import tuning.TuningManager;
import data.Constraints;

/**
 * EMLTTSEditorWeb.synthesis.Preprocessing.java offers methods for processing
 * data entered by the user before it is sent to the synthesizer. These
 * currently contain:
 * 
 * enrichtContent - which takes care of adding tuning information to the string
 * which represents the text to be synthesized changedPhonInput - checks whether
 * the phonetic tuning was actually touched
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         05.11.2010 MaMi
 * 
 *         Added:
 * 
 *         createSyllableText - for creating the information containing stressed
 *         words or syllables findStartPos - auxillary method for
 *         createSyllableText for finding out where the stress has to be placed
 *         isOneSyll - auxillary method for checking the length of the word
 */
public class Preprocessing {
	/**
	 * Preprocessing.createSyllableText takes as arguments
	 * 
	 * @param stressInfo
	 *            - ArrayList of SyllStress Objects containing the information
	 *            about stressed words and syllables
	 * @param startPos
	 *            - int representing the start position, where the stress
	 *            information should be added
	 * @return - String containing enriched text, containing stress information
	 */
	private static String createSyllableText(ArrayList<SyllStress> stressInfo,
			int startPos) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "Preprocessing.createSyllableText";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String returnText = "\\sampa(";
		int pos = 0;
		if (startPos > 0) {
			pos = findStartPos(stressInfo, startPos, stressInfo.get(startPos)
					.getWord());
		}

		for (int a = pos; a < stressInfo.size(); a++) {
			logInfo = "StartPosWord " + stressInfo.get(a).getWord();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			String syllable = stressInfo.get(a).getSyll().trim();
			syllable = syllable.replaceAll(" ", "");
			if (a == startPos) {
				returnText = returnText + ")" + "\\stress=yes \\sampa("
						+ syllable.trim() + ")) \\sampa(";
			} else {
				if (stressInfo.get(a).getWord()
						.equals(stressInfo.get(startPos).getWord()) == false) {
					returnText = returnText.trim() + ")";
					return returnText;
				} else {
					returnText = returnText + syllable.trim();

				}
			}
		}

		returnText = returnText.trim() + ")";
		return returnText;
	}

	/**
	 * Preprocessing.findStartPos takes as arguments
	 * 
	 * @param stressInfo
	 *            - ArrayList of SyllStress objects
	 * @param a
	 *            - the current position in the ArrayList
	 * @param refWord
	 *            - the reference word to look out for
	 * @return - int representing the position
	 */
	private static int findStartPos(ArrayList<SyllStress> stressInfo, int a,
			String refWord) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "Preprocessing.findStartPos";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		while (a > 0) {
			logInfo = "findStartPos " + refWord + " "
					+ stressInfo.get(a).getWord();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (refWord.equals(stressInfo.get(a).getWord()) == true) {
				return a;
			} else {
				a = a - 1;
			}
		}
		return 0;
	}

	/**
	 * Preprocessing.isOneSyll takes as argument
	 * 
	 * @param stressInfo
	 *            - ArrayList of SyllStress objects
	 * @param a
	 *            - current position in the ArrayList
	 * @return - boolean whether the word contains only one syllable or not
	 */
	private static boolean isOneSyll(ArrayList<SyllStress> stressInfo, int a) {
		String word = stressInfo.get(a).getWord();
		if (word.equals(stressInfo.get(a - 1).getWord())) {
			return false;
		}
		if (word.equals(stressInfo.get(a + 1).getWord())) {
			return false;
		}
		return true;
	}

	/**
	 * Preprocessing.changedPhonInput takes as arguments
	 * 
	 * @param baseform
	 *            - String representing the phonetic transcription of a word to
	 *            be added to the EML-TTS-representation
	 * @param pos
	 *            - int representing the position where this word or is
	 *            transcription can be found
	 * @param phonetisedContent
	 *            - String representing the original phonetic transcription as
	 *            returned from the g2p method
	 * @return - boolean whether the phonetic transcription was changed
	 */
	public static boolean changedPhonInput(String baseform, int pos,
			String phonetisedContent) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "Preprocessing.changedPhonInput " + baseform + " || "
				+ pos + " || " + phonetisedContent;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String[] origContent = phonetisedContent.split("\n");
		auxillary.FileIOMethods.appendStringToFile("" + origContent.length,
				logFile);
		if (origContent.length >= pos) {
			if (pos < origContent.length) {
				String[] wordBaseformPair = origContent[pos].split("\t");
				auxillary.FileIOMethods.appendStringToFile(baseform, logFile);
				logInfo = "comparing " + baseform.trim() + " "
						+ wordBaseformPair[1].trim();
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				if (baseform.trim().equals(wordBaseformPair[1].trim())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Preprocessing.enrichtContent takes as argument
	 * 
	 * @param content
	 *            - String representing the content to be synthesized
	 * @return - String representation where additional information from other
	 *         tuning options is added.
	 */
	public static String enrichtContent(String content) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "Preprocessing.enrichtContent";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		content = includeConstraints(content);
		logInfo = "Constraints Included \n" + content;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String[] textElems = content.split(" ");

		logInfo = "Searching for Pauses .....";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Integer> pausePos = new ArrayList<Integer>();
		ArrayList<Double> pauseLength = new ArrayList<Double>();
		for (int a = 0; a < textElems.length; a++) {
			if (textElems[a].contains("PAUSE")) {
				logInfo = "Text contains pauses!";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				pausePos.add(a);
				String newText = textElems[a].replace("PAUSE", "");
				int startIndex = newText.indexOf("(");
				int endIndex = newText.indexOf(")");
				logInfo = "Length: " + newText;
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				pauseLength.add(Double.parseDouble(newText.substring(
						startIndex + 1, endIndex)));
				textElems[a] = " ";
			}
		}
		logInfo = "pausePosLength " + pausePos.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "pauseLengthLength " + pauseLength.size();
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		logInfo = "Searching for Duration Information ...";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		ArrayList<Integer> durationPos = new ArrayList<Integer>();
		ArrayList<Integer> durationLength = new ArrayList<Integer>();
		for (int a = 0; a < textElems.length; a++) {
			if (textElems[a].contains("DURATION")) {
				logInfo = "Text contains Duration";
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				durationPos.add(a);
				String newText = textElems[a].replace("DURATION", "");
				newText = newText.replace("(", "");
				newText = newText.replace(")", "");
				logInfo = "DurationLength: " + newText;
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				durationLength.add(Integer.parseInt(newText));
				textElems[a] = " ";
			}
		}

		ArrayList<LangTune> langInfo = ((TuningManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("tuningManager")).getLangTuneArray();
		if (langInfo != null) {
			for (int a = 0; a < langInfo.size(); a++) {
				logInfo = langInfo.get(a).getWord() + " "
						+ langInfo.get(a).getLang();
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			}
		}
		logInfo = "textElems after LangTune " + textElems.length;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		ArrayList<Phonetic> phonInfo = ((TuningManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("tuningManager")).getPhoneticArray();
		if (phonInfo != null) {
			String phonetisedContent = PhoneticTuning.phonetiseString(content);
			for (int a = 0; a < phonInfo.size(); a++) {
				Phonetic wordPhonPair = (Phonetic) phonInfo.get(a);
				if (changedPhonInput(wordPhonPair.getBaseform(), a,
						phonetisedContent) == false) {
					if (a < textElems.length) {
						String baseform = wordPhonPair.getBaseform()
								.replaceAll(" ", "");
						baseform.replace(".", "");
						String newText = "\\sampa(" + baseform.trim() + ")";
						textElems[a] = newText;
					}
				}
			}
		}
		logInfo = "textElems after phoneticTuning " + textElems.length;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		ArrayList<SyllStress> stressInfo = ((TuningManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("tuningManager")).getSyllStressArray();
		if (stressInfo != null) {
			logInfo = "StressInfo is not NULL!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			logInfo = "" + stressInfo.size();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			;
			int counter = 0;
			String word = stressInfo.get(counter).getWord();
			for (int a = 0; a < stressInfo.size(); a++) {
				SyllStress stressObj = stressInfo.get(a);
				if (word.equals(stressObj.getWord()) == false) {
					counter = counter + 1;
					word = stressObj.getWord();
				}
				if (stressObj.isWordStress()) {
					String newText = "\\stress{" + stressObj.getWord() + "}";
					textElems[counter] = newText;
				}
				if (stressObj.isSyllStress()) {
					if (isOneSyll(stressInfo, a)) {
						String newText = "\\stress{" + stressObj.getWord()
								+ "}";
						textElems[counter] = newText;
					} else {
						logInfo = "Blubb " + counter + " " + textElems[counter];
						auxillary.FileIOMethods.appendStringToFile(logInfo,
								logFile);
						String newText = createSyllableText(stressInfo, a);
						logInfo = "NewText2 " + newText;
						auxillary.FileIOMethods.appendStringToFile(logInfo,
								logFile);
						textElems[counter] = newText;
					}
				}
			}
		}
		logInfo = "TextElems after Stress Tuning " + textElems.length;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		String returnText = "";
		for (int a = 0; a < textElems.length; a++) {
			if (pausePos.indexOf(a) != -1) {
				returnText = returnText + "\\pause="
						+ pauseLength.get(pausePos.indexOf(a));
			}
			returnText = returnText + textElems[a] + " ";
		}

		returnText = "\\speed="
				+ ((SynthesisManager) FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get("synthesize"))
						.getSpeed()
				+ " \\pitch="
				+ ((SynthesisManager) FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get("synthesize"))
						.getPitch() + " " + returnText;

		logInfo = "ReturnText " + returnText;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		return returnText;
	}

	/**
	 * Preprocessing.includeConstraints takes as argument
	 * 
	 * @param content
	 *            - String representing the text to be synthesized
	 * @return - String representing the text to be synthesized enriched with
	 *         constraints information
	 */
	private static String includeConstraints(String content) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "Preprocessing.includeConstraints";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		SubtitlingManager sm = (SubtitlingManager) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("subtitlingManager");
		if (sm.getConstraintList() != null) {
			logInfo = "subtitlingManager != null";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			if (sm.getConstraintList().size() > 0) {
				logInfo = "" + sm.getConstraintList().size();
				auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
				String newContent = "";
				ArrayList<Constraints> constraintList = sm.getConstraintList();
				for (int a = 0; a < constraintList.size(); a++) {
					logInfo = "Position " + a;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					String value = constraintList.get(a).getValue();
					logInfo = "Value " + value;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					String pause = constraintList.get(a).getPause();
					logInfo = "pause " + pause;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					String duration = constraintList.get(a).getDuration();
					logInfo = "duration " + duration;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					String realDuration = constraintList.get(a)
							.getRealDuration();
					logInfo = "realDuration " + realDuration;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					newContent = newContent + "DURATION(" + duration + ")";
					if (Long.parseLong(realDuration) < Long.parseLong(duration)) {
						Double pauseLong = 0.0;
						if (pause.length() > 0) {
							pauseLong = Double.parseDouble(pause);
						}
						Long diff = Long.parseLong(duration)
								- Long.parseLong(realDuration);
						pauseLong = pauseLong + diff;
						pause = "" + pauseLong;
					}
					newContent = newContent + value;
					newContent = newContent + "PAUSE(" + pause + ")";
					newContent = newContent + "\n";
				}
				return newContent;
			} else {
				return content;
			}
		} else {
			logInfo = "SubtitlingManager is NULL";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		return content;
	}

}
