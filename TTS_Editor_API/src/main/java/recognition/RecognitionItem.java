/**
 * RecognitionItems store information returned from a speech recognition module
 * for further usage in the TTS Editor. This is primarily aimed at the
 * pre-processing of movies to be used in the audio description scenario.
 * 
 * A RecognitionItem consists of
 * 
 * 	word
 * 	phonetic
 * 	start
 * 	end
 * 
 * 08. December 2011
 * 	MaMi
 */
package recognition;

/**
 * 
 *	TTS_Editor_API.recognition.RecognitionItem.java stores the following
 *	information about recognized elements:
 *
 *	word - string representation of the recognized data
 *	phonetic - phonetic representation of the recognized data
 *	start - start time
 *	end - end time
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.12.2011 MaMi
 */
public class RecognitionItem 
{

	private final String word;
	private final String phonetic;
	private final long start;
	private final long end;
	
	public RecognitionItem
	(
			String word, 
			String phonetic, 
			long start, 
			long end
	) 
	{
		super();
		this.word = word;
		this.phonetic = phonetic;
		this.start = start;
		this.end = end;
	}
	public String getWord() {
		return word;
	}
	public String getPhonetic() {
		return phonetic;
	}
	public long getStart() {
		return start;
	}
	public long getEnd() {
		return end;
	}	
}
