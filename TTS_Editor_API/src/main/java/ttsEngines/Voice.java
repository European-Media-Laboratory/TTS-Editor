/**
 * Voice object stores information from the Engine and allows to use an engine
 * for synthesis.
 */
package ttsEngines;

/**
 *TTS_Editor_API.ttsEngines.Voice.java
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.12.2011 MaMi
 * TODO
 */
public abstract class Voice {
	public enum SynthesisType {
		TEXT, BASEFORM;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public TTSEngine getEngine() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * For saving to a file - would also be necessary for synthesize(sentences)?!
	 * And in principle all synthesize methods should save the synthesized speech --
	 * necessary for setting flashvar parameter ...
	 */
	public abstract void synthesize(SynthesisType type, String content, String fileName);
	
	public void synthesize(String content, String fileName) {
		synthesize(SynthesisType.TEXT, content, fileName);
	}
}
