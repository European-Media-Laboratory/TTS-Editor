/**
 * Syllables object stores information about the syllables contained in the 
 * user input and offers methods to access details within the objects. These
 * currently are:
 * 
 * id
 * value
 * emph
 * 
 * Additionally methods for extracting Syllables objects from files or 
 * wordslists are offered. These are currently:
 * 
 * createSyllableList
 * getSyllableListFromFile
 * getEmphBool
 */
package data;

import java.util.ArrayList;

/**
 * EMLTTSEditorWeb.data.Syllables.java offers methods for accessing details in
 * Syllables objects.
 * 
 * id - String representation of a numeric identifier of the syllable value -
 * String representation of the value of the syllable emph - whether a specific
 * syllable is emphasized or not
 * 
 * Additionally it offers methods for creating Syllables objects based on other
 * information
 * 
 * createSyllableList - for creating syllables lists based on words lists
 * getSyllableListFromFile - for extracting syllables objects from the saved
 * data.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         08.11.2010 MaMi
 */
public class Syllables {
	private String id;
	private String value;
	private boolean emph;

	static String endString = "/>";

	/**
	 * Constructor for creating a new (empty) Syllables object.
	 */
	public Syllables() {
		id = getId();
		value = getValue();
		setEmph(isEmph());
	}

	/**
	 * Constructor for creating a new Syllables object, which contains the
	 * information given in the arguments
	 * 
	 * @param id
	 *            - String representation of the numeric identifier
	 * @param value
	 *            - String containing the value of the syllable
	 * @param emph
	 *            - boolean value whether the syllable is emphasized or not
	 */
	public Syllables(String id, String value, boolean emph) {
		this.id = id;
		this.value = value;
		this.emph = emph;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * ADDITIONAL METHODS for Syllables Object creation and handling
	 */
	/**
	 * Syllables.createSyllableList takes as parameters
	 * 
	 * @param words
	 *            - ArrayList of Words objects representing the users input
	 * @return - ArrayList of Syllables objects containing the syllables
	 */
	public static ArrayList<Syllables> createSyllableList(ArrayList<Words> words) {
		int counter = 0;
		ArrayList<Syllables> returnList = new ArrayList<Syllables>();
		for (int a = 0; a < words.size(); a++) {
			Words word = words.get(a);
			String phonetic = word.getPhonetic();
			if (phonetic.contains(".")) {
				String[] phonElems = phonetic.split("\\.");
				for (int b = 0; b < phonElems.length; b++) {
					if (phonElems[b].length() > 0) {
						Syllables syllable = new Syllables();
						syllable.setId("" + counter);
						syllable.setValue(phonElems[b]);
						syllable.setEmph(false);
						counter = counter + 1;
						returnList.add(syllable);
					}
				}
			} else {
				Syllables syllable = new Syllables();
				syllable.setId("" + counter);
				syllable.setValue(phonetic);
				syllable.setEmph(false);
				counter = counter + 1;
				returnList.add(syllable);
			}
		}
		return returnList;
	}

	/**
	 * Syllables.getSyllableListFromFile takes as parameters
	 * 
	 * @param syllFile
	 *            - String representation of the FileName containing syllable
	 *            information
	 * @return - ArrayList of Syllables objects extracted from the file
	 */
	public static ArrayList<Syllables> getSyllableListFromFile(String syllFile) {
		String idString = "<id=";
		String valueString = "<value=";
		String emphString = "<emph=";
		String syllEndString = "</syllable>";
		ArrayList<Syllables> returnList = new ArrayList<Syllables>();
		ArrayList<String> content = auxillary.FileIOMethods
				.getFileContent(syllFile);
		if (content != null) {
			Syllables syll = new Syllables();
			for (int a = 0; a < content.size(); a++) {
				int end = content.get(a).toString().length()
						- endString.length();

				if (content.get(a).toString().startsWith(idString)) {
					syll.setId(content.get(a).toString()
							.substring(idString.length(), end));
				}
				if (content.get(a).toString().startsWith(valueString)) {
					syll.setValue(content.get(a).toString()
							.substring(valueString.length(), end));
				}
				if (content.get(a).toString().startsWith(emphString)) {
					boolean emphBool = auxillary.StringOperations
							.stringToBool(content.get(a).toString()
									.substring(emphString.length(), end));
					syll.setEmph(emphBool);
				}
				if (content.get(a).toString().startsWith(syllEndString)) {
					returnList.add(syll);
					syll = new Syllables();
				}
			}
		}
		return returnList;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for Syllables objects.
	 */
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
	 * @param emph
	 *            the emph to set
	 */
	public void setEmph(boolean emph) {
		this.emph = emph;
	}

	/**
	 * @return the emph
	 */
	public boolean isEmph() {
		return emph;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/

}
