/**
 * LanguageTuning contains methods for setting and getting the language 
 * information for a set of words entered by the user. 
 */
package tuning;

import java.util.ArrayList;

import javax.faces.context.FacesContext;

import editor.EditorManager;

/**
 * EMLTTSEditorWeb.tuning.LangTuning.java offers methods for tuning the language
 * of words entered by the user.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         17.11.2010 MaMi
 */
public class LangTuning {
	private static ArrayList<LangTune> langTuning;
	private ArrayList<String> languages;

	/**
	 * Empty constructor for creating a language tuning object based on the
	 * information entered by the user via the editor.
	 */
	public LangTuning() {
		langTuning = new ArrayList<LangTune>();
		String content = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult();
		String[] contElems = content.split(" ");
		for (int a = 0; a < contElems.length; a++) {
			LangTune newElem = new LangTune();
			newElem.setWord(contElems[a]);
			newElem.setLang("DE");
			langTuning.add(newElem);
		}
		setLanguages(new ArrayList<String>());
		getLanguages().add("DE");
		getLanguages().add("EN");
		getLanguages().add("ES");
	}

	/**
	 * Constructor for creating a language tuning object based on the
	 * information given in the text based language tuning interaction.
	 * 
	 * @param languageEditor
	 *            - String representing the language tuning information
	 */
	public LangTuning(String languageEditor) {
		langTuning = new ArrayList<LangTune>();
		String[] langTuneElems = languageEditor.split("\n");
		for (int a = 0; a < langTuneElems.length; a++) {
			LangTune newElem = new LangTune();
			String[] wordLangPair = langTuneElems[a].split("\t");
			newElem.setWord(wordLangPair[0]);
			newElem.setLang(wordLangPair[1]);
			langTuning.add(newElem);
		}
	}

	/**
	 * LangTuning.selectionLangTuning takes as arguments
	 * 
	 * @param selectionPos
	 *            - String containing the marked part of the input entered by
	 *            the user and selected by using the mouse
	 * @return - ArrayList of LangTune objects for setting the language for the
	 *         selection only.
	 */
	public static ArrayList<LangTune> selectionLangTuning(String selectionPos) {
		ArrayList<LangTune> returnList = new ArrayList<LangTune>();
		String[] selectionElems = selectionPos.split("\t");
		String selection = ((EditorManager) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("editor"))
				.getResult().substring(Integer.parseInt(selectionElems[0]),
						Integer.parseInt(selectionElems[1]));
		String[] contElems = selection.split(" ");
		for (int a = 0; a < contElems.length; a++) {
			LangTune newElem = new LangTune();
			newElem.setWord(contElems[a]);
			newElem.setLang("DE");
			returnList.add(newElem);
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
	 * @param langTuning
	 *            the langTuning to set
	 */
	public void setLangTuning(ArrayList<LangTune> langTuning) {
		this.langTuning = langTuning;
	}

	/**
	 * @return the langTuning
	 */
	public ArrayList<LangTune> getLangTuning() {
		return langTuning;
	}

	/**
	 * @param languages
	 *            the languages to set
	 */
	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}

	/**
	 * @return the languages
	 */
	public ArrayList<String> getLanguages() {
		return languages;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/

}
