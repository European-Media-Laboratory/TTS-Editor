/**
 * The CorporateIDsettings class is part of the settings package within the 
 * web application tutorial. It serves the purpose of setting various parameters
 * concerning the corporate ID of the web application. These parameters are set
 * in the corporateID.properties file. 
 */
package settings.corporateIDsettings;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * WebApplicationTemplateICE.settings.corporateIDsettings.CorporateIDsettings.
 * java
 * 
 * Setting corporate ID settings in the header/footer.jspx files.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         20.10.2010 MaMi
 */
public class CorporateIDsettings {
	/**
	 * A set of global variables for the setting of properties.
	 */
	private String packageName = "settings.corporateIDsettings.corporateID";
	private ResourceBundle resourceBundle;
	private Map<String, String> settings;

	/**
	 * The constructor CorporateIDsettings initializes the properties as defined
	 * by the file, which contains the properties, which is set in the
	 * packageName.
	 */
	public CorporateIDsettings() {
		initializeResourceBundleMap();
	}

	/**
	 * Initialize the properties by gathering the information in the
	 * "packageName" (maybe change the name to be clearer?). The properties can
	 * then be accessed by addressing the key and the value is returned. For
	 * debugging purposes the System.out.println can be uncommented to see what
	 * is actually read from the properties file.
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
	 * Automatically created setter method to set a new properties item, which
	 * consists of a key and a value.
	 * 
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	/**
	 * Automatically created getter method to get the proerties.
	 * 
	 * @return the settings
	 */
	public Map<String, String> getSettings() {
		return settings;
	}
}
