/**
 * The Constraints object is part of the Data-Structure containing information
 * required for subtitling projects. Subtitling projects contain information
 * about start and end time for a specific sentence, pauses between sentences
 * and a maximum duration for sentences. These are stored in this object, which
 * is part of the Data structure.
 * 
 * 14. January 2011
 * MaMi
 * 
 * Added:
 * 
 * Constraints Constructor
 * equals
 * splitPause
 * changeType
 * 
 * 21. April 2011 MaMi
 */
package data;

import java.util.ArrayList;

/**
 * EMLTTSEditorWeb.data.Constraints.java offers methods to access information
 * from subtitling projects concerning sentences to by synthesized for a
 * subtitling project. Additionally, it contains methods for extracting
 * constraints from a saved file.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         14.01.2011 MaMi
 * 
 *         Added:
 * 
 *         Constraints Constructor adding the SubtitlingManager equals - for
 *         comparison of two constraints objects splitPause - for splitting
 *         automatically created pauses changeType - for changing type from
 *         "transcription" to "pause"
 * 
 *         21.04.2011 MaMi
 */
public class Constraints {
	private String start;
	private String end;
	private String duration;
	private String sentID;
	private String value;
	private String realDuration;
	private String pause;
	private boolean transcription;

	static String endString = "/>";

	/**
	 * Constructor for creating a new Constraints object
	 */
	public Constraints() {
		start = getStart();
		end = getEnd();
		duration = getDuration();
		sentID = getSentID();
		value = getValue();
		realDuration = getRealDuration();
		pause = getPause();
		transcription = isTranscription();
	}

	/**
	 * Constructor for creating a new Constraints object based on given
	 * parameters.
	 * 
	 * @param start
	 * @param end
	 * @param duration
	 * @param sentID
	 * @param value
	 * @param realDuration
	 * @param pause
	 * @param backgroundColour
	 */
	public Constraints(String start, String end, String duration,
			String sentID, String value, String realDuration, String pause,
			String backgroundColour, boolean transcription

	) {
		this.start = start;
		this.end = end;
		this.duration = duration;
		this.sentID = sentID;
		this.value = value;
		this.realDuration = realDuration;
		this.pause = pause;
		this.transcription = transcription;
	}

	/**
	 * Constraints.equals checks whether two Constraints objects are the same
	 * based on their start and end time information.
	 */
	@Override
	public boolean equals(Object o) {
		// check for self-comparison
		if (this == o)
			return true;

		if (!(o instanceof Constraints))
			return false;

		// cast to native object is now safe
		Constraints ub = (Constraints) o;

		if (this.getStart().equals(ub.getStart())
				&& this.getEnd().equals(ub.getEnd()))
			return true;
		else
			return false;
	}

	/**
	 * Constraints.getConstraintsListFromFile extracts the constraints
	 * information from a given sentence and project file given as argument.
	 * 
	 * @param sentFile
	 *            - String representing the fileName where the information is
	 *            stored
	 * @return - ArrayList<Constraints> containing the information
	 */
	public static ArrayList<Constraints> getConstraintsListFromFile(
			String sentFile) {
		if (sentFile.endsWith(".sent")) {
			sentFile = sentFile.replace(".sent", ".adc");
		} else {
			sentFile = sentFile + ".adc";
		}
		String idString = "<id=";
		String valueString = "<value=";
		String sentEndString = "</constraint>";
		ArrayList<Constraints> returnList = new ArrayList<Constraints>();
		ArrayList<String> content = auxillary.FileIOMethods
				.getFileContent(sentFile);
		if (content == null) {
			return null;
		}
		Constraints constraint = new Constraints();
		for (int a = 0; a < content.size(); a++) {
			int end = content.get(a).toString().length() - endString.length();
			if (content.get(a).toString().startsWith(idString)) {
				constraint.setSentID(content.get(a).toString()
						.substring(idString.length(), end));
			}
			if (content.get(a).toString().startsWith(valueString)) {
				constraint.setValue(content.get(a).toString()
						.substring(valueString.length(), end));
			}
			String startString = "<start=";
			if (content.get(a).toString().startsWith(startString)) {
				constraint.setStart(content.get(a).toString()
						.substring(startString.length(), end));
			}
			String timeEndString = "<end=";
			if (content.get(a).toString().startsWith(timeEndString)) {
				constraint.setEnd(content.get(a).toString()
						.substring(timeEndString.length(), end));
			}
			String durationString = "<duration=";
			if (content.get(a).toString().startsWith(durationString)) {
				constraint.setDuration(content.get(a).toString()
						.substring(durationString.length(), end));
			}
			String realDurationString = "<realDuration=";
			if (content.get(a).toString().startsWith(realDurationString)) {
				constraint.setRealDuration(content.get(a).toString()
						.substring(realDurationString.length(), end));
			}
			String pauseString = "<pause=";
			if (content.get(a).toString().startsWith(pauseString)) {
				constraint.setPause(content.get(a).toString()
						.substring(pauseString.length(), end));
			}
			String transcriptionString = "<transcription=";
			if (content.get(a).toString().startsWith(transcriptionString)) {
				if (content.get(a).toString()
						.substring(transcriptionString.length(), end)
						.equals("true")) {
					constraint.setTranscription(true);
				}
			}
			if (content.get(a).toString().startsWith(sentEndString)) {
				returnList.add(constraint);
				constraint = new Constraints();
			}
		}
		return returnList;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for data objects.
	 */
	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param sentID
	 *            the sentID to set
	 */
	public void setSentID(String sentID) {
		this.sentID = sentID;
	}

	/**
	 * @return the sentID
	 */
	public String getSentID() {
		return sentID;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param realDuration
	 *            the realDuration to set
	 */
	public void setRealDuration(String realDuration) {
		this.realDuration = realDuration;
	}

	/**
	 * @return the realDuration
	 */
	public String getRealDuration() {
		return realDuration;
	}

	/**
	 * @param pause
	 *            the pause to set
	 */
	public void setPause(String pause) {
		this.pause = pause;
	}

	/**
	 * @return the pause
	 */
	public String getPause() {
		return pause;
	}

	/**
	 * @param transcription
	 *            the transcription to set
	 */
	public void setTranscription(boolean transcription) {
		this.transcription = transcription;
	}

	/**
	 * @return the transcription
	 */
	public boolean isTranscription() {
		return transcription;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
