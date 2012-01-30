/**
 * Converter interface provides methods for converting from the internal TTS
 * Editor representation to any format suitable for the used TTS Engine. The
 * methods have to be implemented by the user/developer of the TTS Editor.
 * 
 * The methods provided are:
 * 
 * convert
 * convert
 * 
 * 08. December 2011
 * 	MaMi
 */
package ttsEngines;

import data.Sentences;

/**
 *	TTS_Editor_API.converters.Converter.java Interface provides methods to be
 *	implemented. 
 *
 *	convert - convert the string representation of the content to be synthesized.
 *		Additional information for tuning etc. has to be picked from the GUI.
 *	convert - convert the sentences representation of the content to synthesized.
 *		Additional information for tuning etc. is stored in the elements of the
 *		sentences object. 
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.12.2011 MaMi
 */
public interface Converter {

	/*
	 * Would convert(sentences) not be sufficient, as internally, everything
	 * should(!) be handled as sentences?!
	 */
	String convert(String content);

	String convert(Sentences sentences);
	
}
