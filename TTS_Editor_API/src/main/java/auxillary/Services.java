package auxillary;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import recognition.Recognizer;
import ttsEngines.G2P;
import ttsEngines.TTSEngine;

/**
 * The services class reads the configuration to provide the modular g2p,
 * recognizer and tts services to the TTS Editor.
 * 
 * It reads the services.properties which needs to be placed within the
 * classpath. The format is the default properties format and has the following
 * possible keys.
 * 
 * <ul>
 * <li>g2p.class: Defines the class name of the service class implementing the
 * G2P interface.</li>
 * <li>recognizer.class: Defines the class name of the service class
 * implementing the Recognizer interface.</li>
 * <li>tts: Defines the number of tts engines.</li>
 * <li>tts.<number>.class: Defines the class of the this TTSEngine
 * implementation.</li>
 * </ul>
 * 
 * If the constructors of the classes expect parameters, these parameters can be
 * supplied in the configuration file. Use the @Parameter annotation to define
 * the name of the key in the configuration file that should be mapped to this
 * parameter. Note that only String parameters are possible.
 * 
 * Example configuration:
 * 
 * <pre>
 * g2p.class=test.DummyG2P
 * recognizer.class=test.DummyRecognizer
 * tts=2
 * tts.1.class=test.DummyTTS
 * tts.2.class=test.DummyTTSWithParams
 * tts.2.foo=a real value
 * </pre>
 * 
 * tts.2 has a constructor with one parameter called "foo". This parameter will
 * be set to "a real value" upon instantiation. This also works for "g2p." and
 * "recognizer.".
 * 
 * 
 * @author Stephan Mehlhase
 */
public final class Services {
	private static final Log log = LogFactory.getLog(Services.class);

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Parameter {
		String value();
	}

	private static final String G2P_KEY = "g2p";
	private static final String RECOGNIZER_KEY = "recognizer";
	private static final String TTS_ENGINE_KEY = "tts";

	private static Semaphore initLock = new Semaphore(1);

	private static void init() {
		try {
			initLock.acquire();
		} catch (InterruptedException e) {
			return;
		}
		if (services == null) {
			Properties props = new Properties();
			try {
				InputStream is = Services.class.getClassLoader()
						.getResourceAsStream(ConfigurationProperties.SERVICES_PROPERTIES);
				if (is == null) {
					log.warn("Could not load services properties file, continuing anyway, Good Luck!");
				} else {
					props.load(is);
				}
			} catch (IOException e) {
				log.error("Could not read services configuration file", e);
			}

			G2P g2pService = null;
			try {
				g2pService = createService(props, G2P_KEY);
			} catch (Exception e) {
				log.error("Could create g2p service", e);
			}
			Recognizer reco = null;
			try {
				reco = createService(props, RECOGNIZER_KEY);
			} catch (Exception e) {
				log.error("Could create recognition service", e);
			}
			List<TTSEngine> engines;

			String count = props.getProperty(TTS_ENGINE_KEY);
			if (count == null) {
				log.warn("Could not read count from properties file, trying with count = 1");
				engines = new ArrayList<TTSEngine>(1);
				try {
					engines.add((TTSEngine) createService(props, TTS_ENGINE_KEY
							+ ".1"));
				} catch (Exception e) {
					log.warn("Still could not read configuration, trying with base key");
					try {
						engines.add((TTSEngine) createService(props,
								TTS_ENGINE_KEY));
					} catch (Exception e1) {
						log.warn("There will be no TTSEngine available, that's gonna hurt!");
					}
				}
			} else {
				int noEngines = 1;
				try {
					noEngines = Integer.valueOf(count);
				} catch (NumberFormatException ex) {
					log.warn("Could not read count from properties file, trying with count = 1");
				}
				engines = new ArrayList<TTSEngine>(noEngines);

				for (int i = 1; i < noEngines + 1; i++) {
					try {
						engines.add((TTSEngine) createService(props,
								TTS_ENGINE_KEY + "." + i));
					} catch (Exception e) {
						log.warn("Could not read engine number " + i
								+ ", skipping...");
					}
				}

			}
			services = new ServiceHolder(g2pService, reco, engines);
		}
		initLock.release();
	}

	@SuppressWarnings("unchecked")
	private static <T> T createService(Properties props, String baseKey)
			throws Exception {
		String className = props.getProperty(baseKey + ".class");
		if (className == null) {
			throw new NullPointerException(
					baseKey
							+ ".class was not set to read a G2P class. Check services.properties");
		}
		Class<?> c = Class.forName(className);

		Constructor<?>[] ctors = c.getConstructors();
		for (Constructor<?> ctor : ctors) {
			// Try to find constructor
			Annotation[][] annotations = ctor.getParameterAnnotations();
			// Default constructor, let's try!
			if (annotations.length == 0)
				return (T) c.newInstance();
			else {
				Object[] parameterValues = new String[annotations.length];
				boolean usableConstructor = true;
				// Constructor with one or more parameters.
				int parameter = 0;
				for (Annotation[] parameterAnnotations : annotations) {
					// Check whether for each parameter there is the @Parameter
					// annotation
					if (parameterAnnotations.length == 0) {
						// If not, we can not use this constructor, try the
						// next.
						usableConstructor = false;
						break;
					} else {
						// There are annotations, try to find the @Parameter
						for (Annotation annotation : parameterAnnotations) {
							if (annotation.annotationType().equals(
									Parameter.class)) {
								// Found it, read the name of the key:
								Parameter p = (Parameter) annotation;
								String key = baseKey + "." + p.value();

								String value = props.getProperty(key);
								if (value == null) {
									usableConstructor = false;
									break;
								}
								parameterValues[parameter] = value;
								break;
							}
						}
						if (!usableConstructor)
							break;
					}
					parameter++;
				}
				if (!usableConstructor)
					continue;
				// At this point, the constructor is valid and all values in
				// parameterValues are not null.
				try {
					return (T) ctor.newInstance(parameterValues);
				} catch (IllegalArgumentException e) {
					log.warn("Parameter types of ctor " + ctor.toString()
							+ " failed.", e);
					continue;
				} catch (InstantiationException e) {
					log.warn(
							"Could not instantiate ctor " + ctor.toString()
									+ " with "
									+ Arrays.toString(parameterValues) + ".", e);
					continue;
				} catch (IllegalAccessException e) {
					log.warn("Could not access ctor " + ctor.toString());
					continue;
				} catch (InvocationTargetException e) {
					log.warn(
							"Could not invoke ctor " + ctor.toString()
									+ " with "
									+ Arrays.toString(parameterValues) + ".", e);
					continue;
				}
			}
		}

		throw new NullPointerException("No constructor of " + className
				+ " could be instantiated.");
	}

	private static class ServiceHolder {
		final G2P g2p;
		final Recognizer recognition;
		final Collection<TTSEngine> ttsEngines;

		public ServiceHolder(G2P g2p, Recognizer reco,
				Collection<TTSEngine> ttsEngines) {
			this.g2p = g2p;
			this.recognition = reco;
			this.ttsEngines = ttsEngines;
		}
	}

	private static ServiceHolder services;

	/**
	 * @return Returns the class implementing the {@link G2P} interface. Might
	 *         return {@code null} if reading from the configuration has failed.
	 */
	public static G2P getG2PService() {
		init();
		return services.g2p;
	}

	/**
	 * @return Returns a collection of available {@link TTSEngine}s. This
	 *         collection might be empty.
	 */
	public static Collection<TTSEngine> getAvailableTTSEngines() {
		init();
		return services.ttsEngines;
	}

	/**
	 * @return Returns the class implementing the {@link Recognizer} interface.
	 *         Might return {@code null} if reading from the configuration has
	 *         failed.
	 */
	public static Recognizer getRecognizer() {
		init();
		return services.recognition;
	}
}
