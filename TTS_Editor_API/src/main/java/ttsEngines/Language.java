/**
 * Language interface provides methods to be implemented via a synthesis module.
 * The details have to be implemented by the user/developer of the TTS Editor.
 * 
 * The provided methods are
 * 
 * getVoices
 * 
 * 08. December 2011
 * 	MaMi
 */
package ttsEngines;

import java.util.List;

/**
 *	TTS_Editor_API.ttsEngines.Language.java provides an interface to access
 *	the voices available for a specific engine. This module has to be implemented
 *	by the user/developer of the TTS Editor based on the available synthesis 
 *	engines.
 *
 *	The provided methods are:
 *
 *	getVoices - returns a list of voice objects, to which data can be sent for
 *		synthesis.
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.12.2011 MaMi
 */
public abstract class Language {

	public abstract List<Voice> getVoices();
	
}
