/**
 * EmotionTuning offers methods to add basic emotional expression to the
 * synthesized output.
 * 
 * addEmotionInformation
 * setAfraidEmotion
 * setAngryEmotion
 * setSadEmotion
 * extractEmotion
 * 
 * 27. July 2011 
 * 	MaMi
 * 
 */
package tuning;

import java.util.ArrayList;
import java.util.Random;

import synthesis.SynthesisManager;

/**
 * EMLTTSEditorWeb.tuning.EmotionTuning.java
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         27.07.2011 MaMi TODO
 */
public class EmotionTuning {

	static SynthesisManager sm;
	static TuningManager tm;

	/**
	 * EmotionTuning.addEmotionInformation takes as arguments
	 * 
	 * @param answer
	 *            - the String representation of the text to be enhanced with
	 *            emotion parameters
	 * @param sm
	 *            - the synthesisManager
	 * 
	 *            and returns a String representation of the text including the
	 *            emotion that has been added.
	 */
	public static String addEmotionInformation(String answer) {
		SynthesisManager sm = SynthesisManager.get();
		String returnAnswer = answer;

		String emotion = extractEmotion(answer);

		String replacementString = "EMOTION(" + emotion + ")";

		returnAnswer = answer.replace(replacementString, "");
		
		if (emotion.equals("happy")) {
			returnAnswer = setHappyEmotion(returnAnswer);
		}
		if (emotion.equals("sad")) {
			returnAnswer = setSadEmotion(returnAnswer);
		}
		if (emotion.equals("angry")) {
			returnAnswer = setAngryEmotion(returnAnswer);
		}
		if (emotion.equals("afraid")) {
			returnAnswer = setAfraidEmotion(returnAnswer);
		}

		return returnAnswer;
	}

	/**
	 * EmotionTuning.setAfraidEmotion takes as argument
	 * 
	 * @param returnAnswer
	 *            - String representation of the text
	 * @return String representation of the text including parameters that
	 *         change the expression from neutral to afraid.
	 * 
	 */
	private static String setAfraidEmotion(String returnAnswer) {
		String returnString = "";
		String[] returnAnswerElems = returnAnswer.split(" ");
		int pauseRate = (returnAnswerElems.length / 100) * 50;

		if (pauseRate < 1) {
			pauseRate = 1;
		}

		ArrayList<String> randNumList = new ArrayList<String>();
		Random r = new Random();
		while (randNumList.size() <= pauseRate) {
			int randNum = r.nextInt(returnAnswerElems.length);
			if (randNumList.contains(randNum) == false) {
				randNumList.add("" + randNum);
			}
		}

		int randNumCounter = 0;

		for (int a = 0; a < returnAnswerElems.length; a++) {
			if (randNumCounter < randNumList.size()) {
				if (a == Integer.parseInt(randNumList.get(randNumCounter)
						.toString())) {
					returnAnswerElems[a] = "\\PAUSE(50) "
							+ returnAnswerElems[a];
					randNumCounter = randNumCounter + 1;
				}
			}
			returnString = returnString + returnAnswerElems[a] + " ";
		}
		if (sm != null) {
			sm.setPitch("30");
			sm.setSpeed("20");
		} 
		returnString = "\\PITCH(30) " + returnString;
		returnString = "\\SPEED(20) " + returnString;
		returnString = "\\LOUD(30) " + returnString;
		return returnString;
	}

	/**
	 * EmotionTuning.setAngryEmotion takes as argument
	 * 
	 * @param returnAnswer
	 *            - String representation of the text
	 * @return String representation of the text including parameters that
	 *         change the expression from neutral to angry.
	 * 
	 */
	private static String setAngryEmotion(String returnAnswer) {
		String returnString = "";
		String[] returnAnswerElems = returnAnswer.split(" ");
		for (int a = 0; a < returnAnswerElems.length; a++) {
			returnAnswerElems[a] = "\\STRESS(yes) " + returnAnswerElems[a];
			returnString = returnString + returnAnswerElems[a] + " ";
		}
		if (sm != null) {
			sm.setPitch("60");
			sm.setSpeed("70");
		} 
		returnString = "\\PITCH(60) " + returnString;
		returnString = "\\SPEED(70) " + returnString;
		returnString = "\\LOUD(70) " + returnString;
		return returnString.trim();
	}

	/**
	 * EmotionTuning.setSadEmotion takes as argument
	 * 
	 * @param returnAnswer
	 *            - String representation of the text
	 * @return String representation of the text including parameters that
	 *         change the expression from neutral to sad.
	 * 
	 */
	private static String setSadEmotion(String returnAnswer) {
		String returnString = "";
		String[] returnAnswerElems = returnAnswer.split(" ");
		for (int a = 0; a < returnAnswerElems.length; a++) {
			if (a == returnAnswerElems.length - 1) {
				returnAnswerElems[a] = "\\STRESS(yes) " + returnAnswerElems[a];
			}
			returnString = returnString + returnAnswerElems[a] + " ";
		}
		if (sm != null) {
			sm.setPitch("30");
			sm.setSpeed("20");
		}
		returnString = "\\PITCH(30) " + returnString;
		returnString = "\\SPEED(20) " + returnString;
		returnString = "\\LOUD(30) " + returnString;
		return returnString.trim();
	}

	/**
	 * EmotionTuning.setHappyEmotion takes as argument
	 * 
	 * @param returnAnswer
	 *            - String representation of the text
	 * @return String representation of the text including parameters that
	 *         change the expression from neutral to happy.
	 * 
	 */
	private static String setHappyEmotion(String returnAnswer) {
		String returnString = "";
		String[] returnAnswerElems = returnAnswer.split(" ");
		for (int a = 0; a < returnAnswerElems.length; a++) {
			if (a == returnAnswerElems.length - 1) {
				returnAnswerElems[a] = "\\STRESS(yes) " + returnAnswerElems[a];
			}
			returnString = returnString + returnAnswerElems[a] + " ";
		}
		if (sm != null) {
			sm.setPitch("60");
			sm.setSpeed("70");
		} 
		returnString = "\\PITCH(60) " + returnString;
		returnString = "\\SPEED(70) " + returnString;
		returnString = "\\LOUD(70) " + returnString;
		return returnString.trim();
	}

	/**
	 * EmotionTuning.extractEmotion takes as argument
	 * 
	 * @param answer
	 *            - String representation containing an emotional expression
	 * @return - String representation of the emotion in the sentence.
	 */
	private static String extractEmotion(String answer) {
		int startIndex = answer.indexOf("(");
		int endIndex = answer.indexOf(")");
		String emotion = answer.substring(startIndex + 1, endIndex);
		return emotion;
	}

}
