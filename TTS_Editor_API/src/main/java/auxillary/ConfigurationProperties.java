package auxillary;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigurationProperties {
	public static final String CONFIGURATION_DIR = "./config/";
	public static final String SERVICES_PROPERTIES = CONFIGURATION_DIR + "services.properties";
	public static final String PATHS_PROPERTIES = CONFIGURATION_DIR + "path.properties";
	
	public final class PathKeys {
		public static final String KEY_BASE_WWW_PATH = "baseWWWPath";
		public static final String KEY_BASE_WWW_IP = "baseWWWIp";
	}
	
	public static Properties getProperties(String file) throws IOException { 
		InputStream is = ConfigurationProperties.class.getClassLoader().getResourceAsStream(file);
		Properties prop = new Properties();
		prop.load(is);
		return prop;
	}
	
}
