/**
 * Settings provides settings for projects to be saved.
 */
package project;

import java.util.ArrayList;

/**
 * EMLTTSEditorWebprojectSettings.java
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         09.11.2010 MaMi TODO
 */
public class Settings {
	private String value;
	private String voice;
	private String engine;
	private String id;
	private String lex;
	private String phon;
	private String stress;
	private String constraints;
	private String language;
	private String movie;
	private String[] settings;

	String voiceStart = "<voice=";
	String engineStart = "<engine=";
	String idStart = "<id=";
	String lexStart = "<lex=";
	String phonStart = "<phon=";
	String stressStart = "<stress=";
	String valueStart = "<value=";
	String constraintStart = "<constraints=";
	String languageStart = "<language=";
	String movieStart = "<movie=";
	String end = "/>";

	public Settings() {
		settings = new String[9];
		value = "";
		voice = "";
		engine = "";
		id = "";
		lex = "";
		phon = "";
		stress = "";
		constraints = "";
		language = "";
		settings[0] = value;
		settings[1] = voice;
		settings[2] = engine;
		settings[3] = id;
		settings[4] = lex;
		settings[5] = phon;
		settings[6] = stress;
		settings[7] = constraints;
		settings[8] = language;
	}

	public Settings(ArrayList<String> fileContent) {
		for (int a = 0; a < fileContent.size(); a++) {
			int endPoint = fileContent.get(a).toString().length()
					- end.length();

			if (fileContent.get(a).toString().startsWith(voiceStart)) {
				this.setVoice(fileContent.get(a).toString()
						.substring(voiceStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(engineStart)) {
				this.setEngine(fileContent.get(a).toString()
						.substring(engineStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(idStart)) {
				this.setId(fileContent.get(a).toString()
						.substring(idStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(lexStart)) {
				this.setLex(fileContent.get(a).toString()
						.substring(lexStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(phonStart)) {
				this.setPhon(fileContent.get(a).toString()
						.substring(phonStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(stressStart)) {
				this.setStress(fileContent.get(a).toString()
						.substring(stressStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(valueStart)) {
				String value = extractValue(fileContent, a, endPoint);
				this.setValue(value);
			}
			if (fileContent.get(a).toString().startsWith(constraintStart)) {
				this.setConstraints(fileContent.get(a).toString()
						.substring(constraintStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(languageStart)) {
				this.setLanguage(fileContent.get(a).toString()
						.substring(languageStart.length(), endPoint));
			}
			if (fileContent.get(a).toString().startsWith(movieStart)) {
				this.setMovie(fileContent.get(a).toString()
						.substring(movieStart.length(), endPoint));
			}
		}
	}

	/**
	 * Settings.extractValue takes as parameter
	 * 
	 * @param fileContent
	 *            - the content of the settings file
	 * @param pos
	 *            - the current position within the settings file
	 * @param endPoint
	 *            - the endpoint with the current settings file
	 * @return String representing the extracted value of a specific parameter.
	 */
	private String extractValue(ArrayList<String> fileContent, int pos,
			int endPoint) {
		String returnString = fileContent.get(pos).toString()
				.substring(valueStart.length(), endPoint);
		for (int a = pos + 1; a < fileContent.size(); a++) {
			if (fileContent.get(a).toString().endsWith(end)) {
				returnString = returnString + "\n"
						+ fileContent.get(a).toString().replace(end, "");
				return returnString;
			}
			returnString = returnString + "\n" + fileContent.get(a).toString();
		}
		return returnString;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for settings objects.
	 */
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
	 * @param voice
	 *            the voice to set
	 */
	public void setVoice(String voice) {
		this.voice = voice;
	}

	/**
	 * @return the voice
	 */
	public String getVoice() {
		return voice;
	}

	/**
	 * @param engine
	 *            the engine to set
	 */
	public void setEngine(String engine) {
		this.engine = engine;
	}

	/**
	 * @return the engine
	 */
	public String getEngine() {
		return engine;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param lex
	 *            the lex to set
	 */
	public void setLex(String lex) {
		this.lex = lex;
	}

	/**
	 * @return the lex
	 */
	public String getLex() {
		return lex;
	}

	/**
	 * @param phon
	 *            the phon to set
	 */
	public void setPhon(String phon) {
		this.phon = phon;
	}

	/**
	 * @return the phon
	 */
	public String getPhon() {
		return phon;
	}

	/**
	 * @param stress
	 *            the stress to set
	 */
	public void setStress(String stress) {
		this.stress = stress;
	}

	/**
	 * @return the stress
	 */
	public String getStress() {
		return stress;
	}

	/**
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(String[] settings) {
		this.settings = settings;
	}

	/**
	 * @return the settings
	 */
	public String[] getSettings() {
		return settings;
	}

	/**
	 * @param constraints
	 *            the constraints to set
	 */
	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}

	/**
	 * @return the constraints
	 */
	public String getConstraints() {
		return constraints;
	}

	public String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @param movie
	 *            the movie to set
	 */
	public void setMovie(String movie) {
		this.movie = movie;
	}

	/**
	 * @return the movie
	 */
	public String getMovie() {
		return movie;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
