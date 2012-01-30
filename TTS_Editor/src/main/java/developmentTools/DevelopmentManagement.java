/**
 * Development Management offers methods for creating a new voice for the
 * TTS Editor and the underlying TTS Engine respectively.
 * 
 * These are:
 * 
 * extractZipFiles
 * startRecordingProcess
 * getHeaderInfo
 * byteArrayToShort
 * correspondingTxt
 * correspondingWav
 * createCorpusFile
 * buildDirectoryHierarchyFor
 * extractTextFiles
 * showNextScriptItem
 * addToScript
 * extractDataFromFile
 * 
 * 28. February 2011
 * 	MaMi
 * 
 */
package developmentTools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import popups.PopupManager;
import settings.userSettings.UserSettings;
import data.ScriptData;

/**
 * TTSEditor.developmentTools.DevelopmentManagement.java offers methods for
 * uploading recorded data or script data for recording aimed at creating a new
 * custom voice. These methods include:
 * 
 * extractZipFiles - extract the wav and txt files from a zip-archive
 * startRecordingProcess - starting the recording process for recording a new
 * voice getHeaderInfo byteArrayToShort correspondingTxt - checks whether a wav
 * file has a corresponding txt file correspondingWav - checks whether a txt
 * file has a corresponding wav file createCorpusFile - creates the corpus file
 * for the data representation buildDirectoryHierarchyFor extractTextFiles -
 * extracts the text files showNextScriptItem - displays the next item for
 * recording addToScript extractDataFromFile - extracts data from a text file
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         28.02.2011 MaMi
 */
public class DevelopmentManagement {
	static byte[] buffer = new byte[16384];
	static int len;
	static ArrayList<String> missingTxtFiles;
	static ArrayList<String> missingWavFiles;
	private static boolean showMissingItems = false;
	private boolean showTest = false;
	private boolean showUpload = true;
	private boolean showRecording = false;
	private static String currentSentence;
	private static String currentPos;
	private static String currentComment;
	private static ArrayList<ScriptData> scriptData;
	private static boolean showScriptItems = false;
	private static boolean showScriptData = false;
	private static boolean showScriptUploading = false;

	public DevelopmentManagement() {

	}

	/**
	 * This has to be implemented by the user/developer of the TTS Editor.
	 * 
	 * @param event
	 */
	public void startDevelopmentProcess(ActionEvent event) {
		// To Do implement processing of voice creation process either using
		// Mary or Festival or something else completely.

		// Forced Alignment or some other method of creating a sound database!
	}

	/**
	 * DevelopmentManagement.extractZipFiles takes as argument
	 * 
	 * @param absolutePath
	 *            - String representation of the zip file that contains the data
	 * 
	 *            and extracts the files, checking that the data set is
	 *            complete, i.e. that for each wav file there is a corresponding
	 *            txt file and vice versa.
	 */
	public static void extractZipFiles(String absolutePath) {
		missingTxtFiles = new ArrayList<String>();
		missingWavFiles = new ArrayList<String>();
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "DevelopmentManagement.extractZipFiles";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String path = ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"))
				.getUserPath();
		auxillary.FileIOMethods.appendStringToFile("path " + path, logFile);
		String targetDir = path + "zipFiles";
		auxillary.FileIOMethods.appendStringToFile("target " + targetDir,
				logFile);
		File targetFile = new File(targetDir);
		int counter = 0;
		if (targetFile.exists() == false) {
			targetFile.mkdir();
		}
		File archive = new File(absolutePath);
		try {
			ZipFile zipFile = new ZipFile(archive);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			buffer = new byte[16384];
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				String entryFileName = entry.getName();

				File dir = buildDirectoryHierarchyFor(entryFileName, targetFile);
				if (dir.exists() == false) {
					dir.mkdirs();
				}

				if (entry.isDirectory() == false) {
					if (entry.getName().endsWith(".txt")) {
						auxillary.FileIOMethods.appendStringToFile(
								"CurrentEntry " + entry.getName(), logFile);
						if (correspondingWav(entry.getName(), entries)) {
							auxillary.FileIOMethods.appendStringToFile(
									"Found corresponding .wav file", logFile);
							auxillary.FileIOMethods.appendStringToFile(
									"entry ends with .txt", logFile);
							createCorpusFile(path, entry, targetDir, dir,
									zipFile, counter);
						} else {
							auxillary.FileIOMethods.appendStringToFile(
									"No corresponding .wav file", logFile);
							missingTxtFiles.add(entry.getName());
						}
					}
					if (entry.getName().endsWith(".wav")) {
						auxillary.FileIOMethods.appendStringToFile(
								"CurrentEntry " + entry.getName(), logFile);

						if (correspondingTxt(entry.getName(), entries)) {
							auxillary.FileIOMethods.appendStringToFile(
									"Found corresponding .txt file!", logFile);
							auxillary.FileIOMethods.appendStringToFile(
									"entry ends with .wav", logFile);
							BufferedOutputStream bos = new BufferedOutputStream(
									new FileOutputStream(new File(targetFile,
											entryFileName)));

							BufferedInputStream bis = new BufferedInputStream(
									zipFile.getInputStream(entry));
							int[] headerInfo = getHeaderInfo(bis, entry);
							auxillary.FileIOMethods.appendStringToFile(
									"Channels " + headerInfo[2], logFile);
							auxillary.FileIOMethods.appendStringToFile(
									"Sample Rate " + headerInfo[3], logFile);
							auxillary.FileIOMethods.appendStringToFile(
									"ByteRate " + headerInfo[4], logFile);
							// Do something with headerinfo!
							while ((len = bis.read(buffer)) > 0) {
								bos.write(buffer, 0, len);
							}
							bos.flush();
							bos.close();
							bis.close();
						} else {
							auxillary.FileIOMethods.appendStringToFile(
									"No corresponding .txt file!", logFile);
							missingWavFiles.add(entry.getName());
						}
					}
					counter = counter + 1;
				}
			}
		} catch (ZipException e) {
			auxillary.FileIOMethods.appendStringToFile("ZIP Exception!",
					logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		} catch (IOException e) {
			auxillary.FileIOMethods.appendStringToFile("IOException", logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}
		auxillary.FileIOMethods.appendStringToFile("finished extracting",
				logFile);
		String fileName = path + "corpus.xml";
		String corpusName = "blubb";
		ArrayList<String> corpusContent = auxillary.FileIOMethods
				.getFileContent(fileName);
		String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
				+ "<corpus name=\"" + corpusName + "\">";
		String corpusEnd = "</corpus>";
		corpusContent.add(0, xmlHeader);
		corpusContent.add(corpusEnd);
		File corpusFile = new File(fileName);
		corpusFile.delete();
		auxillary.FileIOMethods.writeListToFile(corpusContent, fileName);
		auxillary.FileIOMethods.appendStringToFile("Starting PostProcess!",
				logFile);
		// postprocessWavFiles(absolutePath);
		System.out.println("Finished Postprocessing");
		// createXMLCorpusFile(fileName, targetDir);
		if (missingWavFiles == null) {
			auxillary.FileIOMethods.appendStringToFile(
					"MissingWavFiles is NULL", logFile);
		} else {
			auxillary.FileIOMethods.appendStringToFile("MissingWavFiles "
					+ missingWavFiles.size(), logFile);
			if (missingWavFiles.size() > 0) {
				showMissingItems = true;
			}
		}
		if (missingTxtFiles == null) {
			auxillary.FileIOMethods.appendStringToFile(
					"MissingTxtFiles is NULL", logFile);
		} else {
			if (missingTxtFiles.size() > 0) {
				showMissingItems = true;
			}
			auxillary.FileIOMethods.appendStringToFile("MissingTxtFiles "
					+ missingTxtFiles.size(), logFile);
		}

	}

	/**
	 * DevelopmentManagement.startRecordingProcess takes as argument
	 * 
	 * @param event
	 *            - that triggered this
	 * 
	 *            and starts the recording process for recording a new voice
	 *            based on a text script.
	 */
	public void startRecordingProcess(ActionEvent event) {
		setShowScriptUploading(true);
		showUpload = false;
	}

	private static int[] getHeaderInfo(BufferedInputStream bis, ZipEntry entry) {
		int[] headerInfo = new int[7];
		try {
			byte[] ba = new byte[44];
			if (bis.read(ba, 0, 44) <= 0) {
				// log.info("Can't read the (44)Header-Bytes of the wave-file");
				return null;
			}

			byte[] tmp4 = new byte[4];
			byte[] tmp2 = new byte[2];

			// log.debug("Post HEADER info ("+fileName+")");
			// RIFF chunk descriptor
			// chunkID
			for (int i = 0; i < 4; i++)
				tmp4[i] = ba[i];
			// log.debug("chunkID: " + new String(tmp4));
			tmp4 = new byte[4];
			// chunkSize ( Size - 8 )
			for (int i = 4; i < 8; i++)
				tmp4[i - 4] = ba[i];
			headerInfo[0] = byteArrayToInt(tmp4);
			// log.debug("chunkSize: " + headerInfo[0]);

			tmp4 = new byte[4];
			// format(riffType) e.g. WAVE
			for (int i = 8; i < 12; i++)
				tmp4[i - 8] = ba[i];
			// log.debug("format: " + new String(tmp4));
			tmp4 = new byte[4];

			// "fmt" sub-chunk - sound information
			// subchunk1ID
			for (int i = 12; i < 16; i++)
				tmp4[i - 12] = ba[i];
			// log.debug("subchunk1ID: " + new String(tmp4));
			tmp4 = new byte[4];
			// subchunk1Size - the following bytes in the fmt-chunk
			for (int i = 16; i < 20; i++)
				tmp4[i - 16] = ba[i];
			// log.debug("subchunk1Size: " + byteArrayToInt(tmp4));
			tmp4 = new byte[4];
			// audioFormat
			for (int i = 20; i < 22; i++)
				tmp2[i - 20] = ba[i];
			headerInfo[1] = byteArrayToShort(tmp2);
			// log.debug("audioFormat: " + headerInfo[1]);
			tmp2 = new byte[2];
			// numChannels - 1 = mono, 2 = stereo
			for (int i = 22; i < 24; i++)
				tmp2[i - 22] = ba[i];
			headerInfo[2] = byteArrayToShort(tmp2);
			// log.debug("numChannels: " + headerInfo[2]);
			tmp2 = new byte[2];
			// sampleRate
			for (int i = 24; i < 28; i++)
				tmp4[i - 24] = ba[i];
			headerInfo[3] = byteArrayToInt(tmp4);
			// log.debug("sampleRate: " + headerInfo[3]);
			tmp4 = new byte[4];
			// byteRate - Sample-Rate * Block-Align
			for (int i = 28; i < 32; i++)
				tmp4[i - 28] = ba[i];
			headerInfo[4] = byteArrayToInt(tmp4);
			// log.debug("byteRate: " + headerInfo[4]);
			tmp4 = new byte[4];
			// blockAlign - Kanäle * bitsPerSample / 8
			for (int i = 32; i < 34; i++)
				tmp2[i - 32] = ba[i];
			headerInfo[5] = byteArrayToShort(tmp2);
			// log.debug("blockAlign: " + headerInfo[5]);
			tmp2 = new byte[2];
			// bitsPerSample - 8 oder 16
			for (int i = 34; i < 36; i++)
				tmp2[i - 34] = ba[i];
			headerInfo[6] = byteArrayToShort(tmp2);
			// log.debug("bitsPerSample: " + headerInfo[6]);
			tmp2 = new byte[2];

			// subchunk2ID
			for (int i = 36; i < 40; i++)
				tmp4[i - 36] = ba[i];
			// log.debug("subchunk2ID: " + new String(tmp4));
			tmp4 = new byte[4];
			// subchunk2Size - Length of data-Block ; NO header bytes
			for (int i = 40; i < 44; i++)
				tmp4[i - 40] = ba[i];
			// log.debug("subchunk2Size: " + byteArrayToInt(tmp4));
			tmp4 = new byte[4];

			// bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return headerInfo;
	}

	private static int byteArrayToShort(byte[] tmp2) {
		return (short) (((tmp2[1] & 0xff << 8)) | ((tmp2[0] & 0xff)));
	}

	public static final int byteArrayToInt(byte[] b) {
		return ((b[3] & 0xFF) << 24) | ((b[2] & 0xFF) << 16)
				| ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
	}

	/**
	 * DevelopmentManagement.correspondingTxt takes as arguments
	 * 
	 * @param name
	 *            - String representing the current wav file
	 * @param entries
	 *            - the list of entries in the zip archive
	 * @return - boolean value that states whether the corresponding txt file is
	 *         part of the zip archive or not.
	 */
	private static boolean correspondingTxt(String name,
			Enumeration<? extends ZipEntry> entries) {
		String txtName = name.replace(".wav", ".txt");
		int counter = 0;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			String entryFileName = entry.getName();
			if (txtName.equals(entryFileName)) {
				return true;
			}
			counter = counter + 1;
		}
		return false;
	}

	/**
	 * DevelopmentManagement.correspondingWav takes as arguments
	 * 
	 * @param name
	 *            - String representation of the current txt file
	 * @param entries
	 *            - the list of entries in the zip archive
	 * @return - boolean value that states whether the corresponding txt file is
	 *         part of the zip archive or not.
	 */
	private static boolean correspondingWav(String name,
			Enumeration<? extends ZipEntry> entries) {
		String wavName = name.replace(".txt", ".wav");
		int counter = 0;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			String entryFileName = entry.getName();
			if (wavName.equals(entryFileName)) {
				return true;
			}
			counter = counter + 1;
		}
		return false;
	}

	/**
	 * DevelopmentManagement.createCorpusFile takes as arguments
	 * 
	 * @param path
	 *            - String representation of the path
	 * @param entry
	 *            - current entry from the zip archive
	 * @param targetDir
	 *            - String representation of the directory where the data is to
	 *            be stored
	 * @param dir
	 *            - directory
	 * @param zipFile
	 *            - the zipfile containing the data
	 * @param counter
	 *            - counter
	 * 
	 *            and creates a corpus file based on the wav and txt files and
	 *            the content of the latter.
	 */
	private static void createCorpusFile(String path, ZipEntry entry,
			String targetDir, File dir, ZipFile zipFile, int counter) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "DevelopmentManagement.createCorpusFile";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String fileName = path + "corpus.xml";
		File corpusFile = new File(fileName);
		BufferedOutputStream bos = null;
		if (corpusFile.exists()) {
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						fileName), true));
			} catch (IOException e) {
				auxillary.FileIOMethods.appendStringToFile(
						"Init of appending filewriter", logFile);
				auxillary.FileIOMethods.appendStackTraceToLog(logFile,
						e.getStackTrace());
			}
		} else {
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						fileName)));
			} catch (IOException e) {
				auxillary.FileIOMethods.appendStringToFile(
						"Init of regular fileWriter", logFile);
				auxillary.FileIOMethods.appendStackTraceToLog(logFile,
						e.getStackTrace());
			}
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(
					zipFile.getInputStream(entry));
			String recordingIndicator = "<recording audio=\"" + targetDir + "/"
					+ entry.getName().replace("txt", "wav") + "\" name=\"test"
					+ counter + "\">";
			String segment = "<segment start=\"0.0\" end=\"inf\" name=\"test"
					+ counter + "\">";
			String orth = "<orth>";
			String pre = recordingIndicator + "\n" + segment + "\n" + orth
					+ "\n";
			byte[] preBytes = pre.getBytes();
			bos.write(preBytes);
			String value = "";
			while ((len = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
				value = value + bis.read(buffer);
			}
			String orthEnd = "</orth>";
			String segmentEnd = "</segment>";
			String recordingEnd = "</recording>";
			String post = orthEnd + "\n" + segmentEnd + "\n" + recordingEnd
					+ "\n";
			byte[] postBytes = post.getBytes();
			bos.write(postBytes);
			counter = counter + 1;
		} catch (IOException e) {
			auxillary.FileIOMethods.appendStringToFile("Error in writing",
					logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}

		try {
			bos.flush();
			bos.close();
		} catch (IOException e) {
			auxillary.FileIOMethods.appendStringToFile(
					"Finalizing of fileWriter", logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}

	}

	/**
	 * DevelopmentManagement.buildDirectoryHierarachyFor takes as arguments
	 * 
	 * @param entryName
	 *            - String representation of the current entry
	 * @param destDir
	 *            - File where the content of the current entry is to be stored
	 * @return - a file representation of the new saved data.
	 */
	private static File buildDirectoryHierarchyFor(String entryName,
			File destDir) {
		int lastIndex = entryName.lastIndexOf('/');
		String internalPathToEntry = entryName.substring(0, lastIndex + 1);
		return new File(destDir, internalPathToEntry);
	}

	/**
	 * DevelopmentManagement.extractTextFiles takes as argument
	 * 
	 * @param absolutePath
	 *            - String representation of the files from where to extract the
	 *            text data.
	 */
	public static void extractTextFiles(String absolutePath) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"DevelopmentManagement.extractTextFiles", logFile);
		String path = ((UserSettings) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userSettings"))
				.getUserPath();
		String scriptFileName = path + "script.txt";
		auxillary.FileIOMethods.appendStringToFile(scriptFileName, logFile);
		File scriptFile = new File(scriptFileName);
		ArrayList<String> fileContent = new ArrayList<String>();
		if (absolutePath.endsWith(".zip")) {
			auxillary.FileIOMethods.appendStringToFile(
					"absolutePath ends with .zip " + absolutePath, logFile);
			String targetDir = path + "voiceData";
			File targetFile = new File(targetDir);
			if (targetFile.exists() == false) {
				targetFile.mkdir();
			}
			File archive = new File(absolutePath);
			try {
				ZipFile zipFile = new ZipFile(archive);
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				buffer = new byte[16384];
				while (entries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) entries.nextElement();

					String entryFileName = entry.getName();

					File dir = buildDirectoryHierarchyFor(entryFileName,
							targetFile);
					if (dir.exists() == false) {
						dir.mkdirs();
					}

					if (entry.isDirectory() == false) {
						if (entry.getName().endsWith(".txt")) {
							auxillary.FileIOMethods.appendStringToFile(
									"CurrentEntry " + entry.getName(), logFile);
							addToScript(entry, scriptFile, zipFile);
						}
					}
				}
				fileContent = auxillary.FileIOMethods.getFileContent(scriptFile
						.getAbsolutePath());
			} catch (ZipException e) {
				auxillary.FileIOMethods.appendStringToFile("ZIP Exception!",
						logFile);
				auxillary.FileIOMethods.appendStackTraceToLog(logFile,
						e.getStackTrace());
			} catch (IOException e) {
				auxillary.FileIOMethods.appendStringToFile("IOException",
						logFile);
				auxillary.FileIOMethods.appendStackTraceToLog(logFile,
						e.getStackTrace());
			}
		} else {
			auxillary.FileIOMethods.appendStringToFile(
					"AbsolutePath ends otherwise " + absolutePath, logFile);
			if (absolutePath.endsWith(".txt")) {
				fileContent = auxillary.FileIOMethods
						.getFileContent(absolutePath);
				auxillary.FileIOMethods.appendStringToFile(
						"FileContent.size() " + fileContent.size(), logFile);
			} else {
				PopupManager pm = (PopupManager) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("popupManager");
				pm.setArbitraryErrorValue("Please upload either a .zip-Archive or a plain text (.txt) file.");
				pm.setShowArbitraryError(true);
			}
		}

		scriptData = extractDataFromFile(fileContent);
		auxillary.FileIOMethods.appendStringToFile("ScriptData Done "
				+ scriptData.size(), logFile);
		showScriptUploading = false;
		currentPos = "0";
		currentSentence = scriptData.get(Integer.parseInt(currentPos))
				.getValue();
		currentComment = scriptData.get(Integer.parseInt(currentPos))
				.getComment();
		showScriptItems = true;
		showScriptData = true;

	}

	/**
	 * DevelopmentManagement.showNextScriptItem takes as argument
	 * 
	 * @param event
	 *            - triggering this event
	 * 
	 *            and displays the next item to be recorded.
	 */
	public void showNextScriptItem(ActionEvent event) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"DevelopmentManagement.showNextScriptItem", logFile);
		currentPos = "" + (Integer.parseInt(currentPos) + 1);
		currentSentence = auxillary.StringOperations.trimToSize(
				scriptData.get(Integer.parseInt(currentPos)).getValue(), 50);
		auxillary.FileIOMethods.appendStringToFile("CurrentSentence "
				+ currentSentence, logFile);
		currentComment = auxillary.StringOperations.trimToSize(
				scriptData.get(Integer.parseInt(currentPos)).getComment(), 50);
		auxillary.FileIOMethods.appendStringToFile("CurrentComment "
				+ currentComment, logFile);
	}

	private static void addToScript(ZipEntry entry, File scriptFile,
			ZipFile zipFile) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"DevelopmentManagement.addToScript", logFile);
		BufferedOutputStream bos = null;
		if (scriptFile.exists()) {
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						scriptFile.getAbsolutePath()), true));
			} catch (IOException e) {
				auxillary.FileIOMethods.appendStringToFile(
						"Init of appending filewriter", logFile);
				auxillary.FileIOMethods.appendStackTraceToLog(logFile,
						e.getStackTrace());
			}
		} else {
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						scriptFile.getAbsolutePath())));
			} catch (IOException e) {
				auxillary.FileIOMethods.appendStringToFile(
						"Init of regular fileWriter", logFile);
				auxillary.FileIOMethods.appendStackTraceToLog(logFile,
						e.getStackTrace());
			}
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(
					zipFile.getInputStream(entry));
			String value = "";
			while ((len = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
				value = value + bis.read(buffer);
			}
		} catch (IOException e) {
			auxillary.FileIOMethods.appendStringToFile("Error in writing",
					logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}

		try {
			bos.flush();
			bos.close();
		} catch (IOException e) {
			auxillary.FileIOMethods.appendStringToFile(
					"Finalizing of fileWriter", logFile);
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}
	}

	/**
	 * DevelopmentManagement.extractDataFromFile takes as argument
	 * 
	 * @param fileContent
	 *            - the content of a file containing data to be read
	 * @return - ArrayList<ScriptData> containing the data to be read by the
	 *         voice talent in order to create a new voice.
	 */
	private static ArrayList<ScriptData> extractDataFromFile(
			ArrayList<String> fileContent) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"DevelopmentManagement.extractDataFromFile", logFile);
		ArrayList<ScriptData> returnList = new ArrayList<ScriptData>();
		ScriptData element = new ScriptData();
		int counter = 0;
		for (int a = 0; a < fileContent.size(); a++) {
			element.setSentNum(counter);
			if (a + 1 < fileContent.size()) {
				if (fileContent.get(a + 1).toString().startsWith("KOMMENTAR")) {
					element.setComment(fileContent.get(a + 1).toString());
				}
			} else {
				element.setValue(fileContent.get(a).toString());
				returnList.add(element);
				return returnList;
			}
			if (fileContent.get(a).toString().startsWith("KOMMENTAR") == false) {
				element.setValue(fileContent.get(a).toString());
				returnList.add(element);
				counter = counter + 1;
				element = new ScriptData();
			}
		}
		returnList.add(element);
		return returnList;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for information from
	 * SynthesisManager.
	 */
	/**
	 * @param showMissingItems
	 *            the showMissingItems to set
	 */
	public void setShowMissingItems(boolean showMissingItems) {
		this.showMissingItems = showMissingItems;
	}

	/**
	 * @return the showMissingItems
	 */
	public boolean isShowMissingItems() {
		return showMissingItems;
	}

	/**
	 * @param showTest
	 *            the showTest to set
	 */
	public void setShowTest(boolean showTest) {
		this.showTest = showTest;
	}

	/**
	 * @return the showTest
	 */
	public boolean isShowTest() {
		return showTest;
	}

	/**
	 * @param showUpload
	 *            the showUpload to set
	 */
	public void setShowUpload(boolean showUpload) {
		this.showUpload = showUpload;
	}

	/**
	 * @return the showUpload
	 */
	public boolean isShowUpload() {
		return showUpload;
	}

	/**
	 * @param showRecording
	 *            the showRecording to set
	 */
	public void setShowRecording(boolean showRecording) {
		this.showRecording = showRecording;
	}

	/**
	 * @return the showRecording
	 */
	public boolean isShowRecording() {
		return showRecording;
	}

	/**
	 * @param currentSentence
	 *            the currentSentence to set
	 */
	public void setCurrentSentence(String currentSentence) {
		this.currentSentence = currentSentence;
	}

	/**
	 * @return the currentSentence
	 */
	public String getCurrentSentence() {
		return currentSentence;
	}

	/**
	 * @param currentPos
	 *            the currentPos to set
	 */
	public void setCurrentPos(String currentPos) {
		this.currentPos = currentPos;
	}

	/**
	 * @return the currentPos
	 */
	public String getCurrentPos() {
		return currentPos;
	}

	/**
	 * @param currentComment
	 *            the currentComment to set
	 */
	public void setCurrentComment(String currentComment) {
		this.currentComment = currentComment;
	}

	/**
	 * @return the currentComment
	 */
	public String getCurrentComment() {
		return currentComment;
	}

	/**
	 * @param scriptData
	 *            the scriptData to set
	 */
	public void setScriptData(ArrayList<ScriptData> scriptData) {
		this.scriptData = scriptData;
	}

	/**
	 * @return the scriptData
	 */
	public ArrayList<ScriptData> getScriptData() {
		return scriptData;
	}

	/**
	 * @param showScriptItems
	 *            the showScriptItems to set
	 */
	public void setShowScriptItems(boolean showScriptItems) {
		this.showScriptItems = showScriptItems;
	}

	/**
	 * @return the showScriptItems
	 */
	public boolean isShowScriptItems() {
		return showScriptItems;
	}

	/**
	 * @param showScriptData
	 *            the showScriptData to set
	 */
	public void setShowScriptData(boolean showScriptData) {
		this.showScriptData = showScriptData;
	}

	/**
	 * @return the showScriptData
	 */
	public boolean isShowScriptData() {
		return showScriptData;
	}

	/**
	 * @param showScriptUploading
	 *            the showScriptUploading to set
	 */
	public void setShowScriptUploading(boolean showScriptUploading) {
		this.showScriptUploading = showScriptUploading;
	}

	/**
	 * @return the showScriptUploading
	 */
	public boolean isShowScriptUploading() {
		return showScriptUploading;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
