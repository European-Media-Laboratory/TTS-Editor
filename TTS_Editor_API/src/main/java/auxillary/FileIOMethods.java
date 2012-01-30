/**
 * FileIOMethods offers a set of methods to read and write Files on the server
 * side. These currently include:
 * 
 * save
 * saveProject
 * writeListToFile
 * getFileContent
 * writeArrayListToFile
 * writeFile
 * writeArrayToFile
 * 
 * 11. November 2010
 * MaMi
 * 
 * writeStringToFile
 * 
 * 14. February 2011
 * MaMi
 * 
 * appendStackTraceToLog
 * appendStringToFile
 * writeNewSettings
 * copyFile
 * 
 * 21. April 2011
 * MaMi
 */
package auxillary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * EMLTTSEditorWeb.auxillary.FileIOMethods.java offers methods for
 * 
 * save - saving various data types to a file saveProject - saving project
 * specific data to a file writeListToFile - writing the contents of an
 * ArrayList to a file getFileContent - reading the contents of a file into an
 * ArrayList writeArraylistToFile - same as writeListToFile - clean! writeFile -
 * writing a File to a new File (copy) writeArrayToFile - writing an Array to a
 * file
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         13.10.2010 MaMi
 * 
 *         Added:
 * 
 *         writeStringToFile - writing a String to a File
 * 
 *         14.02.2011 MaMi
 * 
 *         Added:
 * 
 *         appendStackTraceToLog - for adding stacktraces to the logfile for
 *         better analysis appendStringToFile - for adding arbitrary strings to
 *         a given file (mostly logfile) writeNewSettings - for writing the
 *         updated settings information for the current user copyFile - for
 *         copying the servlet output to the proper output
 * 
 *         21.04.2011 MaMi
 */
public class FileIOMethods {
	
	private static final Log log = LogFactory.getLog(FileIOMethods.class);

	/**
	 * FileIOMethods.appendStackTraceToLog takes as arguments
	 * 
	 * @param logFile
	 *            - the logFile to write to
	 * @param stackTrace
	 *            - the stackTrace information from the error
	 * 
	 *            and adds the information to the respective logfile. This is
	 *            for a better/ easier analysis of what cause an error and in
	 *            order to find a solution.
	 */
	public static void appendStackTraceToLog(String logFile,
			StackTraceElement[] stackTrace) {
		String logInfo = "STACKTRACE INFORMATION!";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		for (int a = 0; a < stackTrace.length; a++) {
			logInfo = stackTrace[a].toString();
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
	}

	/**
	 * FileIOMethods.writeListToFile takes as arguments
	 * 
	 * @param fileContent
	 *            - ArrayList containing the data to be saved
	 * @param saveName
	 *            - String representing the fileName to which the data is saved.
	 */
	public static void writeListToFile(ArrayList<String> fileContent, String saveName) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(saveName);
			for (int a = 0; a < fileContent.size(); a++) {
				if (fileContent.get(a) != null) {
					fileWriter.write(fileContent.get(a).toString() + "\n");
				}
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FileIOMethods.getFileContent takes as arguments
	 * 
	 * @param fileName
	 *            - String representing the file from which the content is to be
	 *            extracted and
	 * @return - ArrayList containing the content of the file.
	 * 
	 *         The ArrayList containing the content is returned to the calling
	 *         method/function.
	 */
	public static ArrayList<String> getFileContent(String fileName) {
		ArrayList<String> returnList = new ArrayList<String>();
		File newFile = new File(fileName);
		if (newFile.exists() == false) {
			return null;
		}
		BufferedReader wordListFile = null;
		try {
			wordListFile = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
		} catch (FileNotFoundException e) {
			return null;
		}

		String word;
		try {
			while ((word = wordListFile.readLine()) != null) {
				returnList.add(word);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnList;
	}

	/**
	 * FileIOMethods.writeArrayListToFile takes as arguments
	 * 
	 * @param fileContent
	 *            - ArrayList containing the data to be saved
	 * @param fileName
	 *            - String representing the fileName to which the data is saved.
	 */
	public static void writeArrayListToFile(ArrayList<String[]> fileContent,
			String fileName) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(fileName);
			for (int a = 0; a < fileContent.size(); a++) {
				String[] elem = (String[]) fileContent.get(a);
				fileWriter.write(elem[0] + "\t" + elem[1] + "\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FileIOMethods.writeFile takes as arguments
	 * 
	 * @param newFileName
	 *            - String represeting the new File to which the data from the
	 *            uploaded file is to be saved
	 * @param uploadedFile
	 *            - File being uploaded from the client
	 */
	public static void writeFile(String newFileName, File uploadedFile) {
		try {
			FileWriter fileWriter = new FileWriter(newFileName);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FileIOMEthods.writeArrayToFile takes as arguments
	 * 
	 * @param stringArray
	 *            - String[] containing data to be saved
	 * @param fileName
	 *            - String representing the fileName to which the data is saved.
	 */
	public static void writeArrayToFile(String[] stringArray, String fileName) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(fileName);
			for (int a = 0; a < stringArray.length; a++) {
				fileWriter.write(stringArray[a].toLowerCase() + "\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FileIOMethods.writeStringToFile takes as arguments
	 * 
	 * @param returnContent
	 *            - the String containing the information
	 * @param fileName
	 *            - the fileName to which the information is to be written
	 */
	public static void writeStringToFile(String returnContent, String fileName) {
		FileWriter fileWriter = null;
		try {
			File fileCheck = new File(fileName);
			if (fileCheck.exists()) {
				fileCheck.delete();
			}
			fileWriter = new FileWriter(fileName);
			fileWriter.write(returnContent);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * FileIOMethods.appendStringToFile takes as argument
	 * 
	 * @param logInfo
	 *            - the String containing the information to be written to
	 * @param logFileName
	 *            - the file to which the information is added
	 * 
	 *            This is primarily used for logs, which serve for error
	 *            analysis.
	 */
	public static void appendStringToFile(String logInfo, String logFileName) {
		if (logFileName != null) {
			String fileName = logFileName;
			File newFile = new File(fileName);
			String timestamp = "blubb";
			logInfo = timestamp + ": " + logInfo;
			if (logInfo != null) {
				if (newFile.exists()) {
					try {
						FileWriter logFile = new FileWriter(fileName, true);
						logFile.write(logInfo + "\n");
						logFile.flush();
						logFile.close();
					} catch (IOException exception) {
						
						log.error("Could not initialize FileWriter for appending to logFile " + fileName);
						exception.printStackTrace();
					}
				} else {
					try {
						FileWriter newLogFile = new FileWriter(fileName);
						newLogFile.write(logInfo + "\n");
						newLogFile.flush();
						newLogFile.close();
					} catch (IOException e) {
						log.error("Could not initialize new logFile " + fileName);
						e.printStackTrace();
					}
				}
			}
		} else {
			log.info(logInfo);
		}
	}

	/**
	 * FileIOMethods.copyFile takes as argument
	 * 
	 * @param fileName
	 *            - the file to be copied to
	 * 
	 *            and copies the hard-coded servlet-output to the local version.
	 * 
	 */
	public static void copyFile(String inFileName, String outFileName) {
		log.info("FileIOMethods.copyFile " + inFileName);
		File wavFile = new File(inFileName);
		File newWavFile = new File(outFileName);
		wavFile.renameTo(newWavFile);
	}
}
