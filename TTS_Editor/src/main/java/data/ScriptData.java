/**
 * ScriptData offers methods for adding data to the Development Environment, which
 * can then be recorded using the Development Environment of the TTSEditor and
 * a Voice Talent. The recorded voice can then be further processed and used as
 * a voice in the TTS Editor.
 * 
 * 13. April 2011
 * MaMi
 */
package data;

/**
 * EMLTTSEditorWeb.data.ScriptData.java contains methods for dealing with data
 * that can be recorded by a voice talent and used for creating a new voice for
 * the Editor.
 * 
 * @author EML European Media Laboratory GmbH
 * 
 *         13.04.2011 MaMi
 */
public class ScriptData {
	private int sentNum;
	private String value;
	private String comment;
	Object[] scriptData;

	/**
	 * Constructor for creating a new ScriptData Object containing the current
	 * sentence number, the sentence and an optional comment for the voice
	 * talent (e.g. advise on how to pronounce certain words).
	 */
	public ScriptData() {
		scriptData = new Object[3];
		sentNum = getSentNum();
		value = getValue();
		comment = getComment();
		scriptData[0] = sentNum;
		scriptData[1] = value;
		scriptData[2] = comment;
	}

	/**
	 * Constructor for creating a new ScriptData Object and also passing the
	 * relevant information as arguments. This relevant information is the
	 * current sentence number, the sentence and an optional comment for the
	 * voice talent (e.g. advise on how to pronounce certain words).
	 */
	public ScriptData(int sentNum, String value, String comment) {
		this.sentNum = sentNum;
		this.value = value;
		this.comment = comment;
	}

	/**
	 * @param sentNum
	 *            the sentNum to set
	 */
	public void setSentNum(int sentNum) {
		this.sentNum = sentNum;
	}

	/**
	 * @return the sentNum
	 */
	public int getSentNum() {
		return sentNum;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

}
