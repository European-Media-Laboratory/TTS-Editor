/**
 * Recognizer interface provides methods to add a recognizer module to the
 * TTS Editor. This has to be implemented by the user/developer of the TTS
 * Editor.
 * 
 * The provided methods are:
 * 
 * recognize
 * 
 * 08. December 2011
 * 	MaMi
 */
package recognition;

import java.util.List;

/**
 *	TTS_Editor_API.recognition.Recognizer.java interface offers methods for a 
 *	recognizer module.
 *
 *	recognize - sends a soundfile to the recognizer for decoding.
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.12.2011 MaMi
 */
public interface Recognizer 
{
	List<RecognitionItem> recognize(String fileName);
}
