/**
 * XMLMethods offers a set of methods for dealing with the xml-like data
 * structures used in the TTS Editor. These currently include:
 * 
 * prepareLexHeader
 * prepareLexFooter
 * createEntry
 * extractEntries
 * extractElement
 * 
 */
package auxillary;

import java.util.ArrayList;

import lexicon.Lexicon;

/**
 * EMLTTSEditorWeb.auxillary.XMLMethods.java offers methods for
 * 
 * prepareLexHeader - creating the header for the file for saving the lexicon
 * data prepareLexFooter - creating the footer for the file for saving the
 * lexicon data createEntry - creating a single lexicon entry extractEntries -
 * extracting entries from a list of lexicon entries extractElement - extracting
 * terminal symbols from a line
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         13.10.2010 MaMi
 */
public class XMLMethods {
	static String rowStart = "<row>";
	static String rowEnd = "</row>";

	/**
	 * XMLMethods.prepareLexHeader takes as argument
	 * 
	 * @param fileContent
	 *            - ArrayList containing the data to be saved
	 * @param saveName
	 *            - String representing the file to which the data is saved
	 * @return - ArrayList containing the Header plus the data
	 */
	public static ArrayList<String> prepareLexHeader(
			ArrayList<String> fileContent, String saveName) {
		String header = "<chapter>";
		String chapTitle = "<title> " + saveName + " </title>";
		String section = "<section>";
		String secTitle = chapTitle;
		String para = "<para> </para>";
		String table = "<table>";
		String tabTitle = chapTitle;
		String tgroup = "<tgroup col=\"4\">";
		String colspecWord = "<colspec align=\"left\" colname=\"word\"/>";
		String colspecFull = "<colspec align=\"left\" colname=\"full\"/>";
		String colspecsoundslike = "<colspec align=\"left\" colname=\"soundslike\"/>";
		String colspecPhonetic = "<colspec align=\"left\" colname=\"phonetic\"/>";
		String thead = "<thead>";
		String rowEntryWord = "<entry align=\"center\"> Word </entry>";
		String rowEntryFull = "<entry align=\"center\"> Full </entry>";
		String rowEntrySoundslike = "<entry align=\"center\"> Soundslike </entry>";
		String rowEntryPhonetic = "<entry align=\"center\"> Phonetic </entry>";
		String theadEnd = "</thead>";
		String tbodyStart = "<tbody>";

		String headerFull = header + "\n" + chapTitle + "\n" + section + "\n"
				+ secTitle + "\n" + para + "\n" + table + "\n" + tabTitle
				+ "\n" + tgroup + "\n" + tabTitle + "\n" + tgroup + "\n"
				+ colspecWord + "\n" + colspecFull + "\n" + colspecsoundslike
				+ "\n" + colspecPhonetic + "\n" + thead + "\n" + rowStart
				+ "\n" + rowEntryWord + "\n" + rowEntryFull + "\n"
				+ rowEntrySoundslike + "\n" + rowEntryPhonetic + "\n" + rowEnd
				+ "\n" + theadEnd + "\n" + tbodyStart + "\n";

		fileContent.add(headerFull);
		return fileContent;
	}

	/**
	 * XMLMethods.prepareLexFooter takes as argument
	 * 
	 * @param fileContent
	 *            - ArrayList containing the data to be saved
	 * @return - ArrayList containing the data and the footer added.
	 */
	public static ArrayList<String> prepareLexFooter(ArrayList<String> fileContent) {
		String tbodyEnd = "</tbody>";
		String tgroupEnd = "</tgroup>";
		String tableEnd = "</table>";
		String sectionEnd = "</section>";
		String chapterEnd = "</chapter>";

		String footer = tbodyEnd + "\n" + tgroupEnd + "\n" + tableEnd + "\n"
				+ sectionEnd + "\n" + chapterEnd;
		fileContent.add(footer);
		return fileContent;
	}

	/**
	 * XMLMethods.createEntry takes as arguments
	 * 
	 * @param lexEntry
	 *            - String containing a new entry
	 * @param vendorType
	 *            - String type of information to be saved
	 * @return - String containing the entry for saving in the xml-like data
	 *         structure.
	 */
	public static String createEntry(String lexEntry, String vendorType) {
		String entry = "<entry vendor=\"" + vendorType
				+ "\"> <emphasis role=\"bold\"> " + lexEntry
				+ " </emphasis> </entry>";
		String returnString = rowStart + "\n" + entry + "\n" + rowEnd;
		return returnString;
	}

	/**
	 * XMLMethods.extractElement takes as arguments
	 * 
	 * @param line
	 *            - String containing the information from a single line
	 * @return - String containing the nonterminal information contained in the
	 *         single line.
	 * 
	 */
	private static String extractElement(String line) {
		String[] elements = line.split("<");
		int index = elements[2].indexOf(">");
		return elements[2].substring(index + 1, elements[2].length());
	}

	/**
	 * XMLMethods.extractEntries takes as arguments
	 * 
	 * @param fileContent
	 *            - ArrayList containing the fileContent where the data is saved
	 * @return - ArrayList of Lexicon entries.
	 */
	public static ArrayList<Lexicon> extractEntries(ArrayList<String> fileContent) {
		ArrayList<Lexicon> returnLex = new ArrayList<Lexicon>();
		Lexicon newEntry = new Lexicon();
		for (int a = 0; a < fileContent.size(); a++) {
			if (fileContent.get(a).toString().contains("vendor=\"word\"")) {
				newEntry.setWord(extractElement(fileContent.get(a).toString()));
			}
			if (fileContent.get(a).toString().contains("vendor=\"full\"")) {
				newEntry.setFull(extractElement(fileContent.get(a).toString()));
			}
			if (fileContent.get(a).toString().contains("vendor=\"soundslike\"")) {
				newEntry.setSoundslike(extractElement(fileContent.get(a)
						.toString()));
			}
			if (fileContent.get(a).toString().contains("vendor=\"phonetic\"")) {
				newEntry.setPhonetic(extractElement(fileContent.get(a)
						.toString()));
			}
			if (fileContent.get(a).toString().contains("</item>")) {
				returnLex.add(newEntry);
				newEntry = new Lexicon();
			}
		}
		return returnLex;
	}
}
