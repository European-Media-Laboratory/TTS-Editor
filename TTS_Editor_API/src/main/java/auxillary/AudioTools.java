/**
 * AudioTools offers a set of methods for audio conversion, extraction and 
 * manipulation. It primarily contains methods which are wrappers around command
 * line tools like ffmpeg and sox.
 * 
 * convertFlv2Wav
 * convertSampleRate
 * convertWav2Flv
 * extractAndSaveSound
 * convertWav2MP3
 * 
 * 06 July 2011 
 * 	MaMi
 */
package auxillary;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * EMLTTSEditorWeb.auxillary.AudioTools.java offers methods for:
 * 
 * convertFlv2Wav - converting media-files into wav-files using ffmpeg
 * convertSampleRate - converting the sample rate using sox convertWav2Flv -
 * converting wav-files into media files using ffmpeg extractAndSaveSound -
 * extracting sound from movie files and save using MP4Box and ffmpeg
 * convertWav2MP3 - converting wav-files into mp3-files, using lame
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         06.07.2011 MaMi
 */
public class AudioTools {
	private static Log log = LogFactory.getLog(AudioTools.class);

	/**
	 * AudioTools.convertFlv2Wav takes as argument
	 * 
	 * @param fileName
	 *            - representing the file to be converted
	 * @return String - representing the converted file
	 * 
	 *         In case the result file already exists it is deleted and the new
	 *         result file is written.
	 */
	public static String convertFlv2Wav(String fileName) {
		String returnName = fileName.replace(".flv", ".wav");
		File returnFile = new File(returnName);
		if (returnFile.exists()) {
			returnFile.delete();
		}
		String convertCommand = "ffmpeg -i " + fileName + " -ar 16000 "
				+ returnName;
		Process convertP = null;
		try {
			convertP = Runtime.getRuntime().exec(convertCommand);
			convertP.waitFor();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return returnName;
	}

	/**
	 * AudioTools.convertSampleRate takes as arguments
	 * 
	 * @param fileName
	 *            - representing the file to be converted
	 * @param string
	 *            - the sampleRate to which it should be converted
	 * @return String - representing the resulting file.
	 * 
	 *         In case the result file already exists it is deleted and the new
	 *         file is created.
	 */
	public static String convertSampleRate(String fileName, String newSampleRate) {
		String returnName = fileName.replace(".wav", newSampleRate + ".wav");
		File returnFile = new File(returnName);
		if (returnFile.exists()) {
			returnFile.delete();
		}
		String convertCommand = "sox " + fileName + " " + returnName + " rate "
				+ newSampleRate;
		Process convertP = null;
		try {
			convertP = Runtime.getRuntime().exec(convertCommand);
			convertP.waitFor();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return returnName;
	}

	/**
	 * AudioTools.convertWav2Flv takes as argument
	 * 
	 * @param outFileName
	 *            - representing the original file
	 * @param mp3FileName
	 *            - representing the target file name.
	 * 
	 *            In case the file already exists, the old file is deleted and a
	 *            new one is created.
	 */
	public static void convertWav2Flv(String outFileName, String mp3FileName) {
		String convertCommand = "";

		if (mp3FileName.endsWith(".wav")) {
			convertCommand = "ffmpeg -i " + outFileName + " -ac 1 -ar 16000 "
					+ mp3FileName;
		} else {
			convertCommand = "ffmpeg -i " + outFileName
					+ " -ac 1 -ar 16000 -acodec libspeex " + mp3FileName;
		}
		File resultFile = new File(mp3FileName);
		if (resultFile.exists()) {
			resultFile.delete();
		}
		Process convertP = null;
		try {
			convertP = Runtime.getRuntime().exec(convertCommand);
			convertP.waitFor();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * AudioTools.extractAndSaveSound takes as argument
	 * 
	 * @param absolutePath
	 *            - representing the movie file
	 * @return - String representing the plain sound file
	 * 
	 *         If the extracted file already exists, it is first deleted.
	 */
	public static String extractAndSaveSound(String absolutePath)
			throws IOException {
		String ffmpegOut = absolutePath.replace(".wmv", "tmp.mp4");
		String soundExtract = File.createTempFile("soundout", ".aac")
				.getAbsolutePath();

		String mp4boxExtractCommand = "MP4Box " + "-raw 2 " + absolutePath
				+ " -out " + soundExtract;
		if (absolutePath.endsWith(".mp4") == false) {
			String ffmpegCommand = "ffmpeg -i "
					+ absolutePath
					+ " -acodec libfaac -ab 128k -pass 1 "
					+ "-vcodec libx264 -b 700000 -flags +loop -cmp +chroma -partitions +parti4x4+partp8x8+partb8x8 "
					+ "-flags2 +mixed_refs -subq 5 -trellis 1 -refs 5 -bf 3 -b_strategy 1 -coder 1 -me_range 16 -g 250 "
					+ "-keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -bt 700000 -rc_eq 'blurCplx^(1-qComp)' "
					+ "-qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 " + ffmpegOut;
			File mp4File = new File(ffmpegOut);
			if (mp4File.exists()) {
				mp4File.delete();
			}
			Process p = null;
			try {
				p = Runtime.getRuntime().exec(ffmpegCommand);

				InputStreamReader errorStreamReader = new InputStreamReader(
						p.getErrorStream());
				BufferedReader bufferedErrorReader = new BufferedReader(
						errorStreamReader);

				InputStreamReader inputStreamReader = new InputStreamReader(
						p.getInputStream());
				BufferedReader bufferedInputReader = new BufferedReader(
						inputStreamReader);

				String lineError;
				String lineInput;
				try {
					lineError = bufferedErrorReader.readLine();
					while (lineError != null) {
						lineError = bufferedErrorReader.readLine();
					}
					lineInput = bufferedInputReader.readLine();
					while (lineInput != null) {
						lineInput = bufferedInputReader.readLine();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				bufferedErrorReader.close();
				bufferedInputReader.close();
				errorStreamReader.close();
				inputStreamReader.close();
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mp4boxExtractCommand = "MP4Box " + "-raw 2 " + ffmpegOut + " -out "
					+ soundExtract;
			File soundEx = new File(soundExtract);
			if (soundEx.exists()) {
				soundEx.delete();
			}
		}

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(mp4boxExtractCommand);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String returnFile = soundExtract.replace(".aac", ".wav");
		File wavFile = new File(returnFile);
		if (wavFile.exists()) {
			wavFile.delete();
		}
		convertWav2Flv(soundExtract, returnFile);
		return returnFile;
	}

	/**
	 * AudioTools.convertWav2MP3 takes as arguments
	 * 
	 * @param fileName
	 *            - representing the wav-file
	 * @param mp3FileName
	 *            - representing the resulting mp3-file.
	 * 
	 *            If the file already exists, it is first deleted.
	 */
	public static void convertWav2MP3(String fileName, String mp3FileName) {
		File file = new File(mp3FileName);
		File oldFile = new File(mp3FileName);
		String oldMp3File = mp3FileName;
		if (file.exists()) {
			oldFile = new File(oldMp3File);
			file.delete();
		}
		String command = "lame --silent -h " + fileName + " " + mp3FileName;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(command);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		File newFile = new File(mp3FileName);
		newFile.renameTo(oldFile);
	}

	/**
	 * AudioTools.addSilence takes as argument
	 * 
	 * @param synthFile
	 *            - String representation of the synthesized sound file
	 * @param startTime
	 *            - long start time
	 * @param end
	 *            - long end
	 * @param fullDuration
	 *            - long the targeted duration
	 * @return - String representation of the final sound file
	 */
	public static String addSilence(String synthFile, long startTime, long end,
			long fullDuration) {

		long endDuration = fullDuration - end;
		String soxSilenceAddition = "sox " + synthFile + " pad " + startTime
				+ " " + endDuration;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(soxSilenceAddition);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return synthFile;
	}

	/**
	 * AudioTools.mixSound takes as argument
	 * 
	 * @param soundFile
	 *            - String representation of the soundfile
	 * @param synthFile
	 *            - String representation of the synthesized data
	 * @return - String representation of the resulting file
	 */
	public static String mixSound(String soundFile, String synthFile) {
		String outFile = soundFile.replace(".wav", "_synthAdd.wav");
		String soxMixCommand = "sox -m " + soundFile + " " + synthFile + " "
				+ outFile;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(soxMixCommand);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outFile;
	}

	/**
	 * AudioTools.mixSoundAndMovie takes as argument
	 * 
	 * @param soundAllFile
	 *            - String representing the file containing synthesized speech
	 *            and original movie sound
	 * @param file
	 *            - String representing the movie file
	 * @return - String representing the final file
	 */
	public static String mixSoundAndMovie(String soundAllFile, String file) {
		String movieOnly = file.replace(".mp4", "movieOnly.mp4");
		String outFileTMP = file.replace(".mp4", "newTMP.mp4");
		String mp4BoxAddCommand = "MP4Box -add " + movieOnly + "#1 -add "
				+ soundAllFile + " -new " + outFileTMP;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(mp4BoxAddCommand);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String outFile = file.replace(".mp4", "new.mp4");
		String ffmpegResultCommand = "ffmpeg -i "
				+ outFileTMP
				+ " -acodec libfaac -ab 128k -pass 2 "
				+ "-vcodec libx264 -b 700000 -flags +loop -cmp +chroma -partitions "
				+ "+parti4x4+partp8x8+partb8x8 -flags2 +mixed_refs -subq 5 -trellis 1"
				+ " -refs 5 -bf 3 -b_strategy 1 -coder 1 -me_range 16 -g 250 "
				+ "-keyint_min 25 -sc_threshold 40 -i_qfactor 0.71 -bt 700000 "
				+ "-rc_eq 'blurCplx^(1-qComp)' -qcomp 0.6 -qmin 10 -qmax 51 -qdiff 4 "
				+ outFile;

		try {
			p = Runtime.getRuntime().exec(ffmpegResultCommand);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outFile;
	}
}
