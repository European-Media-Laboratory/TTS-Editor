/**
 * OutputResourceBean provides methods for downloading created files - for 
 * example sound files of the synthesized data.
 * 
 * OutputResourceBean
 * getPdfResource
 * fileNameListener
 * toByteArray
 * 
 * class MyResource
 * 
 * 18. March 2011 
 * 	MaMi
 */
package popups;

import importTools.SubtitlingManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import settings.userSettings.UserSettings;

import com.icesoft.faces.context.Resource;

/**
 * EMLTTSEditorWeb.popups.OutputResourceBean.java
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         18.03.2011 MaMi
 */
public class OutputResourceBean {
	private Resource pdfResource;
	private Resource txtResource;
	private String fileName = "Choose-a-new-file-name";

	public OutputResourceBean() {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		try {
			auxillary.FileIOMethods
					.appendStringToFile("ResourceBean!", logFile);
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			PopupManager pm = (PopupManager) ec.getSessionMap().get(
					"popupManager");
			String path = ((UserSettings) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userSettings"))
					.getUserPath();
			if (pm.isShowDownloadPopup()) {
				auxillary.FileIOMethods.appendStringToFile(
						"ShowDownloadPopup!", logFile);
				pdfResource = new MyResource(ec, path + "tmp.wav");
				fileName = "synth" + new Date().getTime() + ".wav";
			} else {
				if (pm.isShowTextDownloadPopup()) {
					auxillary.FileIOMethods.appendStringToFile(
							"TxtDownloadPopup", logFile);
					String fileNameTMP = path + "descriptions.txt";
					SubtitlingManager.saveDescriptionsTMP(fileNameTMP);
					txtResource = new MyResource(ec, fileNameTMP);
					fileName = "descriptions" + new Date().getTime() + ".txt";
				} else {
					if (pm.isShowAllSoundDownloadPopup()) {
						auxillary.FileIOMethods.appendStringToFile(
								"AllSoundDownload", logFile);
						String fileNameTMP = path + "allSound.wav";
						SubtitlingManager sm = (SubtitlingManager) ec
								.getSessionMap().get("subtitlingManager");
						if (sm.isShowPlayer()) {
							String movieFile = sm.getFile();
							auxillary.AudioTools.extractAndSaveSound(movieFile);
							pdfResource = new MyResource(ec, fileNameTMP);
							fileName = "allSound" + new Date().getTime()
									+ ".wav";

						} else {
							pm.setArbitraryErrorValue("No Video uploaded!");
							pm.setShowArbitraryError(true);
						}
					}
				}
			}
		} catch (Exception e) {
			auxillary.FileIOMethods.appendStackTraceToLog(logFile,
					e.getStackTrace());
		}
	}

	public Resource getPdfResource() {
		return pdfResource;
	}

	public void fileNameListener(ValueChangeEvent vce) {
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		auxillary.FileIOMethods.appendStringToFile(
				"OutputResourceBean.fileNameListener", logFile);
		fileName = (String) vce.getNewValue();
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int len = 0;
		while ((len = input.read(buf)) > -1)
			output.write(buf, 0, len);
		return output.toByteArray();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @param txtResource
	 *            the txtResource to set
	 */
	public void setTxtResource(Resource txtResource) {
		this.txtResource = txtResource;
	}

	/**
	 * @return the txtResource
	 */
	public Resource getTxtResource() {
		return txtResource;
	}

	class MyResource implements Resource, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String resourceName;
		private InputStream inputStream;
		private final Date lastModified;
		private ExternalContext extContext;

		public MyResource(ExternalContext ec, String resourceName) {
			this.extContext = ec;
			this.resourceName = resourceName;
			this.lastModified = new Date();
		}

		public InputStream open() throws IOException {
			String logFile = UserSettings.getLogFileName();
			auxillary.FileIOMethods.appendStringToFile(
					"OutputResourceBean.MyResource.open", logFile);
			File f = null;
			try {
				if (inputStream == null) {
					f = new File(resourceName);
					if (!f.exists()) {
						auxillary.FileIOMethods.appendStringToFile(
								"file does not exists :: " + resourceName,
								logFile);
					} else {

						FileInputStream fir = new FileInputStream(f);
						if (fir != null && fir.available() > 0) {
							ByteArrayOutputStream output = new ByteArrayOutputStream();
							byte[] buf = new byte[4096];
							int len = 0;
							while ((len = fir.read(buf)) != -1) {
								output.write(buf, 0, len);
							}
							byte[] byteArray = output.toByteArray();
							inputStream = new ByteArrayInputStream(byteArray);
						}
					}
				}
			} catch (Exception e) {
				auxillary.FileIOMethods.appendStringToFile(e.getMessage(),
						logFile);
			}
			return inputStream;
		}

		@Override
		public String calculateDigest() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date lastModified() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void withOptions(Options arg0) throws IOException {
			// TODO Auto-generated method stub

		}
	}
}
