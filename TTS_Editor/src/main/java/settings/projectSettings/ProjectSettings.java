/**
 * The ProjectSettings class is part of the settings package in this tutorial.
 * It takes care of setting project specific items in the header/footer.jspx.
 * It refers to the file project.properties, where values (e.g. project name)
 * can be changed, to be entered at the appropriate position. 
 * This class is very similar to the CorporateIDsettings class. The major
 * difference is, that the project settings have to be adapted for each new
 * project, whereas corporateIDsettings should only be changed if there is also
 * a major change in the corporate ID.
 */
package settings.projectSettings;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * WebApplicationTemplateICE.settings.projectSettings.ProjectSettings.java
 * 
 * Setting the project properties in header/footer.jspx.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         20.10.2010 MaMi
 */
public class ProjectSettings {
	/**
	 * A set of global variables for setting the project properties.
	 */
	private String packageName = "settings.projectSettings.project";
	private ResourceBundle resourceBundle;
	private Map<String, String> settings;

	/**
	 * The constructor which initializes reading the properties from the
	 * properties file. By that it is possible to access properties by calling
	 * the key and receiving the value set in the properties file.
	 */
	public ProjectSettings() {
		initializeResourceBundleMap();
	}

	/**
	 * Here the properties are read from the properties file and read into the
	 * map. For debugging purposes the System.out.println can be uncommented to
	 * see what is actually read from the properties file.
	 */
	private void initializeResourceBundleMap() {
		resourceBundle = ResourceBundle.getBundle(packageName);
		setSettings(new HashMap<String, String>());
		Enumeration<?> enumer = resourceBundle.getKeys();
		while (enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			settings.put(key, resourceBundle.getString(key));
		}
	}

	/**
	 * Automatically created setter method for setting properties map.
	 * 
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	/**
	 * Automatically created getter method for getting the properties map.
	 * 
	 * @return the settings
	 */
	public Map<String, String> getSettings() {
		return settings;
	}

}
