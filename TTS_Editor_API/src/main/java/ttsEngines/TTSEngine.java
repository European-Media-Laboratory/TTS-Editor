/**
 * TTSEngine interface provides methods for accessing a TTS Engine. The
 * implementation of the according module is left to the user/developer of the
 * TTS Editor.
 * 
 * The methods provided are:
 * 
 * 	getLanaguages
 * 	getConverter
 * 
 * 08. December 2011
 * 	MaMi
 *
 */
package ttsEngines;

import java.util.List;

/**
 *	TTS_Editor_API.ttsEngines.TTSEngine.java provides methods for specific TTS
 *	Engines. These are:
 *
 *	getLanguages - which returns a list of languages available with the specific
 *		engine
 *	getConverter - which takes care of converting the TTS Editor internal
 *		representation of the data into a format suitable for the specific
 *		Engine.
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.12.2011 MaMi
 */
public interface TTSEngine {

	List<Language> getLanguages();

	Converter getConverter();
	
}
