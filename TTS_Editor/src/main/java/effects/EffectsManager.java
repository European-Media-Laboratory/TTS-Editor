/**
 * The EffectsManager contains methods for creating various effects, like
 * mixing one soundfile with the synthesized speech.
 */
package effects;

import java.io.File;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import auxillary.AudioTools;

import popups.PopupManager;

import settings.userSettings.UserSettings;

import editor.PlayerManager;

/**
 * EMLTTSEditorWeb.effects.EffectsManager.java offers methods for creating
 * effects apart from the plain synthesis.
 * 
 * This is currently 
 * 
 * mix - which takes a soundfile and adds it underneath the synthesized speech.
 *
 * @author EML European Media Laboratory GmbH
 *
 * 15.12.2010 MaMi
 */
public class EffectsManager 
{
	private static String loudness = "50";
	private static boolean showEffects = false;
	private static String backgroundFileName;
	private static String absolutePathFileName;
	
	/**
	 * EffectsManager.mixAction is the main accesspoint for this type of effect
	 * and calls the actual mix function. It takes as argument
	 * 
	 * @param event - ActionEvent from which this method is called.
	 */
	public void mixAction
	(
			ActionEvent event
	)
	{
		File uploadedFile = new File(absolutePathFileName);
		mix(uploadedFile);
	}
	
	/**
	 * EffectsManager.mix is the actual mixing method, which takes as argument
	 * 
	 * @param uploadedFile - File representing the file to be added to the
	 * 	synthesized speech. 
	 */
	public static void mix
	(
			File uploadedFile
	)
	{
		absolutePathFileName = uploadedFile.getAbsolutePath();
		String wavFileName = uploadedFile.getAbsolutePath();

		showEffects = true;
		backgroundFileName = uploadedFile.getName();
		
		wavFileName.replaceAll(" ", "-");
		if (wavFileName.endsWith(".mp3"))
		{
			wavFileName = decodeWavFile(wavFileName);			
		}
		wavFileName = downSampleFile(wavFileName);
		String path = ((UserSettings) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userSettings")).getUserPath();
		String fileName = path + "tmp.wav";
		String outFileName = path + "mixedTMP.wav";
		File outFile = new File(outFileName);
		if (outFile.exists())
		{
			outFile.delete();
		}
		String command = "sox -m ";
		if (loudness.equalsIgnoreCase("50") == false)
		{
			wavFileName = changeLoudness(wavFileName);
		}
		command = command + fileName + " " + wavFileName + " " + outFileName;					
		
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "EffectsManager.mix";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = command;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		Process p = null;
		try 
		{
			logInfo = "Executing! " + command;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			p = Runtime.getRuntime().exec(command);
		} 
		catch (IOException e1) 
		{
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("Sox could not be launched.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Could not launch sox!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		try
		{
			p.waitFor();
		}
		catch (InterruptedException e1) 
	    {
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("This action was interrupted.");
			pm.setShowArbitraryError(true);
	    	logInfo = "Something went wrong!";
	    	auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}		
		
		String mp3FileName = outFileName.replace("wav", "flv");
		AudioTools.convertWav2Flv(outFileName, mp3FileName);

		PlayerManager.setNewFlashVars(mp3FileName, false);
	}
	
	/**
	 * EffectsManager.changeLoundness allows for chainging the loudness of a 
	 * 	specific file and takes as argument
	 * 
	 * @param wavFileName - String representing the file to be changed
	 * @return - String representing the changed file
	 */
	private static String changeLoudness
	(
			String wavFileName
	) 
	{
		String returnFileName = wavFileName.replace(".wav", "Loud.wav");
		int loud = Integer.parseInt(loudness);
		double factor = 0.0;
		if (loud > 50)
		{
			factor = loud - 50.0;
		}
		if (loud < 50)
		{
			factor = loud/100;
		}
		String loudCommand = "sox -v " + factor + " " + wavFileName + " " + returnFileName;
		File returnFile = new File(returnFileName);
		if (returnFile.exists())
		{
			returnFile.delete();
		}
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "EffectsManager.changeLoudness";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = loudCommand;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		Process pSox = null;
		try 
		{
			logInfo = "Executing! " + loudCommand;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			pSox= Runtime.getRuntime().exec(loudCommand);
		} 
		catch (IOException e1) 
		{
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("Lame could not be launched.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Could not launch lame!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		try
		{
			pSox.waitFor();
		}
		catch (InterruptedException e1) 
	    {
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("This action was interrupted.");
			pm.setShowArbitraryError(true);
			logInfo = "Something went wrong!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}		
		return returnFileName;
	}
	
	/**
	 * EffectsManager.downSampleFile allows for downsampling a given wav-file
	 * 	which allows for further effects on the respective file. It takes as
	 * 	argument
	 * 
	 * @param wavFileName - String representing the wav-file to be changed
	 * @return - String representing the changed wav-file.
	 */
	private static String downSampleFile
	(
			String wavFileName
	) 
	{
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "EffectsManager.downSampleFile";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String returnName = wavFileName.replace(".wav", "RED.wav");
		if (new File(returnName).exists())
		{
			new File(returnName).delete();
		}
		String soxCommand = "sox -r 32000 " + wavFileName + " " + returnName;
		logInfo = soxCommand;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		Process pSox = null;
		try 
		{
			logInfo = "Executing! " + soxCommand;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			pSox= Runtime.getRuntime().exec(soxCommand);
		} 
		catch (IOException e1) 
		{
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("Could not launch sox.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Could not launch sox!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		try
		{
			pSox.waitFor();
		}
		catch (InterruptedException e1) 
	    {
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("This action was interrupted.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Something went wrong!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}		
		return returnName;
	}
	
	/**
	 * EffectsManager.decodeWavFile allows for decoding a .wav-file to a .mp3-
	 * 	file. It takes as argument 
	 * 
	 * @param wavFileName - String representing the wav-file
	 * @return - String representing the mp3-file.
	 */
	private static String decodeWavFile
	(
			String wavFileName
	) 
	{
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "EffectsManager.decodeWavFile";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		String returnName = wavFileName.replace(".mp3", ".wav");
		if (new File(returnName).exists())
		{
			new File(returnName).delete();
		}
		String lameCommand = "lame --silent -h --decode " + wavFileName + " " + returnName;
		logInfo = lameCommand;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		Process pLame = null;
		try 
		{
			logInfo = "Executing! " + lameCommand;
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
			pLame = Runtime.getRuntime().exec(lameCommand);
		} 
		catch (IOException e1) 
		{
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("Lame could not be launched.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Could not launch lame!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}
		try
		{
			pLame.waitFor();
		}
		catch (InterruptedException e1) 
	    {
			PopupManager pm = ((PopupManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("popupManager"));
			pm.setArbitraryErrorValue("This action was interrupted.");
			pm.setShowArbitraryError(true);
			logInfo = "ERROR Something went wrong!";
			auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		}		
		return returnName;
	}

	/**
	 * EffectsManager.decreaseLoudness takes as argument
	 * 
	 * @param event - ActionEvent which calls this function allowing for 
	 * 	decreasing the volume.
	 */
	public void decreaseLoudness
	(
		ActionEvent event
	)
	{
		loudness = "" + (Integer.parseInt(loudness) - 5);
	}
	
	/**
	 * EffectsManager.increaseLoudness takes as argument
	 * 
	 * @param event - ActionEvent which calls this function allowing for 
	 * 	increasing the volume.
	 */
	public void increaseLoudness
	(
			ActionEvent event
	)
	{
		loudness = "" + (Integer.parseInt(loudness) + 5);
	}
	
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for LexiconServices. 
	 */
	/**
	 * @param loudness the loudness to set
	 */
	public void setLoudness(String loudness) {
		this.loudness = loudness;
	}
	/**
	 * @return the loudness
	 */
	public String getLoudness() {
		return loudness;
	}
	/**
	 * @param showEffects the showEffects to set
	 */
	public void setShowEffects(boolean showEffects) {
		this.showEffects = showEffects;
	}
	/**
	 * @return the showEffects
	 */
	public boolean isShowEffects() {
		return showEffects;
	}
	/**
	 * @param backgroundFileName the backgroundFileName to set
	 */
	public void setBackgroundFileName(String backgroundFileName) {
		this.backgroundFileName = backgroundFileName;
	}
	/**
	 * @return the backgroundFileName
	 */
	public String getBackgroundFileName() {
		return backgroundFileName;
	}
	/**
	 * @param absolutePathFileName the absolutePathFileName to set
	 */
	public void setAbsolutePathFileName(String absolutePathFileName) {
		this.absolutePathFileName = absolutePathFileName;
	}
	/**
	 * @return the absolutePathFileName
	 */
	public String getAbsolutePathFileName() {
		return absolutePathFileName;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
}
