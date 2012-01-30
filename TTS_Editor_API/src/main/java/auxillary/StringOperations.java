/**
 * StringOperations offers a set of methods for dealing or transforming Strings.
 * These currently include:
 * 
 * stringToList
 * stringToBool
 * 
 * 02. November 2010 
 * MaMi
 * 
 * extractSelection
 * uniqList
 * 
 * 14. February 2011
 * MaMi
 * 
 * arrayToString
 * trimToSize
 * 
 * 31. April 2011
 * MaMi
 */
package auxillary;

import java.util.ArrayList;

/**
 * EMLTTSEditorWeb.auxillary.StringOperations.java offers methods for
 * 
 * stringToList - transforming a string to an ArrayList containing the elements
 * within the string
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         02.11.2010 MaMi
 * 
 *         Added method
 * 
 *         stringToBool - transforming a string representation of a boolean
 *         value to a boolean value.
 * 
 *         extractSelection - for extracting the marked sequence of characters
 *         from the input uniqList - removes empty lines and duplicates from a
 *         String and saves it to list.
 * 
 *         14.02.2011 MaMi
 * 
 *         Added:
 * 
 *         arrayToString - change an array of Strings into a string trimToSize -
 *         changing a single line to several lines with maximum length
 * 
 *         21.04.2011 MaMi
 */
public class StringOperations {

	/**
	 * StringOperations.stringToList takes as argument
	 * 
	 * @param content
	 *            - String representing the content to be transformed
	 * @return - ArrayList containing the elements from the content.
	 */
	public static ArrayList<String> stringToList(String content) {
		ArrayList<String> returnList = new ArrayList<String>();
		String[] contentArray = content.split(" ");
		for (int a = 0; a < contentArray.length; a++) {
			returnList.add(contentArray[a]);
		}
		return returnList;
	}

	/**
	 * StringOperations.stringToBool takes as arguments
	 * 
	 * @param stringBool
	 *            - String representation of a boolean value
	 * @return - the boolean value contained in the String representation.
	 */
	public static boolean stringToBool(String stringBool) {
		if (stringBool.equalsIgnoreCase("true")) {
			return true;
		}
		if (stringBool.equalsIgnoreCase("false")) {
			return false;
		}
		return false;
	}

	/**
	 * StringOperations.extractSelection takes a String containing a specific
	 * selection, including a tab-separator and extracts the respective elements
	 * from the main interaction editor. It takes as parameters
	 * 
	 * @param selection
	 *            - String containing the selection
	 * @return String containing the respective elements from the main editor.
	 */
	public static String extractSelection(String selection, String content) {
		String[] selectionElems = selection.split("\t");
		if (selection.length() > 0) {
			int start = Integer.parseInt(selectionElems[0]);
			int end = Integer.parseInt(selectionElems[1]);
			String selectionToTune = content.substring(start, end);
			return selectionToTune;
		}
		return "";
	}

	/**
	 * StringOperations.uniqList takes as argument
	 * 
	 * @param originalContent
	 *            - String containing content
	 * @return - ArrayList containing only uniq words from the content
	 */
	public static ArrayList<String> uniqList(String originalContent) {
		ArrayList<String> returnList = new ArrayList<String>();
		String[] contentElems = originalContent.split(" ");
		for (int a = 0; a < contentElems.length; a++) {
			if (returnList.indexOf(contentElems[a].toLowerCase()) < 0
					&& (contentElems[a].equalsIgnoreCase("\n") == false)) {
				returnList.add(contentElems[a].toLowerCase());
			}
		}
		return returnList;
	}

	/**
	 * StringOperations.arrayToString takes as argument
	 * 
	 * @param footer
	 *            - Array of Strings
	 * @return String representation of the strings in the array.
	 */
	public static String arrayToString(String[] footer) {
		String returnContent = "";
		for (int a = 0; a < footer.length; a++) {
			returnContent = returnContent + footer[a] + "\n";
		}
		return returnContent.trim();
	}

	/**
	 * StringOperations.trimToSize takes as arguments
	 * 
	 * @param value
	 *            - the string to be trimmed
	 * @param size
	 *            - the maximum line length to be applied
	 * @return String containing the value in several lines of maximum SIZE
	 *         length
	 */
	public static String trimToSize(String value, int size) {
		String newValue = "";
		if (value.length() > size) {
			String[] valElems = value.split(" ");
			String currentLine = "";
			for (int a = 0; a < valElems.length; a++) {
				if (currentLine.trim().length() >= size) {
					newValue = newValue + currentLine.trim() + "\n";
					currentLine = "";
				}
				currentLine = currentLine + valElems[a] + " ";
			}
			newValue = newValue + currentLine.trim();
		} else {
			return value;
		}
		return newValue;
	}
}
