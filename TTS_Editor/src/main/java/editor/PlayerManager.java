/**
 * PlayerManager takes care of the flashplayer and offers methods to set new
 * flashvars in order to play the correct file.
 * 
 * setNewFlashVars
 * 
 * 17. November 2010
 * 	MaMi
 */
package editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import settings.userSettings.UserSettings;
import auxillary.ConfigurationProperties;
import auxillary.ConfigurationProperties.PathKeys;

import com.icesoft.faces.context.Resource;

/**
 * TTSEditor.editor.PlayerManager.java
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         17.11.2010 MaMi
 */
public class PlayerManager {
	private static final Log log = LogFactory.getLog(PlayerManager.class);
	private static final String baseWWWPath;
	private static final String baseWWWIp;

	static {
		Properties pathProperties;
		try {
			pathProperties = ConfigurationProperties
					.getProperties(ConfigurationProperties.PATHS_PROPERTIES);
		} catch (IOException e) {
			log.error("Could not load "
					+ ConfigurationProperties.PATHS_PROPERTIES
					+ " file. We will probably crash soon.", e);
			pathProperties = new Properties();
		}
		baseWWWPath = pathProperties.getProperty(PathKeys.KEY_BASE_WWW_PATH);
		baseWWWIp = pathProperties.getProperty(PathKeys.KEY_BASE_WWW_IP);
	}

	private static String flashvars;
	private Resource wavResource;
	static int vol = 90;
	private static String style;
	static String timestamp;

	/**
	 * 
	 */
	public PlayerManager() {
		timestamp = "?timestamp=" + (new Date()).getTime();
		flashvars = "file=tmp.mp3" + timestamp
				+ "&type=sound&stretching=fill&respectduration=true&volume="
				+ vol;
		setWavResource(new MyResource(FacesContext.getCurrentInstance()
				.getExternalContext(), "tmp.wav"));
	}

	public static void setNewFlashVars(String mp3FileName,
			boolean autoStartParam) {
		if (vol == 90) {
			vol = 89;
		} else {
			vol = 90;
		}
		@SuppressWarnings("deprecation")
		String logFile = UserSettings.getLogFileName();
		String logInfo = "PlayerManager.setNewFlashVars";
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		logInfo = "Setting FlashVars Original FileName " + mp3FileName;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);
		mp3FileName = mp3FileName.replace(baseWWWPath, "");
		timestamp = "?timestamp=" + (new Date()).getTime();
		logInfo = "Setting Flashvars to " + mp3FileName + timestamp;
		auxillary.FileIOMethods.appendStringToFile(logInfo, logFile);

		flashvars = "file=http://" + baseWWWIp + mp3FileName + timestamp
				+ "&autostart=" + autoStartParam
				+ "&stretching=fill&respectduration=true&volume=" + vol;
		auxillary.FileIOMethods.appendStringToFile(
				"new FlashVars " + flashvars, logFile);
	}

	/**
	 * @param flashvars
	 *            the flashvars to set
	 */
	@SuppressWarnings("static-access")
	public void setFlashvars(String flashvars) {
		this.flashvars = flashvars;
	}

	/**
	 * @return the flashvars
	 */
	public String getFlashvars() {
		return flashvars;
	}

	/**
	 * @param wavResource
	 *            the wavResource to set
	 */
	public void setWavResource(Resource wavResource) {
		this.wavResource = wavResource;
	}

	/**
	 * @return the wavResource
	 */
	public Resource getWavResource() {
		return wavResource;
	}

	/**
	 * @param style
	 *            the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	@SuppressWarnings("serial")
	class MyResource implements Resource, Serializable {
		private String resourceName;
		private InputStream inputStream;
		private final Date lastModified;

		public MyResource(ExternalContext ec, String resourceName) {
			this.resourceName = resourceName;
			this.lastModified = new Date();
		}

		/**
		 * This intermediate step of reading in the files from the JAR, into a
		 * byte array, and then serving the Resource from the
		 * ByteArrayInputStream, is not strictly necessary, but serves to
		 * illustrate that the Resource content need not come from an actual
		 * file, but can come from any source, and also be dynamically
		 * generated. In most cases, applications need not provide their own
		 * concrete implementations of Resource, but can instead simply make use
		 * of com.icesoft.faces.context.ByteArrayResource,
		 * com.icesoft.faces.context.FileResource,
		 * com.icesoft.faces.context.JarResource.
		 */
		public InputStream open() throws IOException {
			File f = null;
			if (inputStream == null) {
				String RESOURCE_PATH = ((UserSettings) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get("userSettings")).getUserPath();
				f = new File(RESOURCE_PATH + resourceName);
				if (!f.exists()) {
					@SuppressWarnings("deprecation")
					String logFile = UserSettings.getLogFileName();
					String logInfo = "MyResource";
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
					logInfo = "file does not exists :: " + resourceName;
					auxillary.FileIOMethods
							.appendStringToFile(logInfo, logFile);
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
			return inputStream;
		}

		public String calculateDigest() {
			return resourceName;
		}

		public Date lastModified() {
			return lastModified;
		}

		public void withOptions(Options arg0) throws IOException {
		}
	}

	public static void setNewStyle(String visibility) {
		style = ("width:300px;height:150px;");
	}
}
