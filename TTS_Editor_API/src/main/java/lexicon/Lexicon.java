/**
 * Lexicon object contains information about lexicon elements. This currently
 * are
 * 
 * word
 * full
 * soundslike
 * phonetic
 * 
 */
package lexicon;

import java.util.ArrayList;


/**
 * EMLTTSEditorWeb.lexicon.Lexicon.java offers methods to access information
 * contained in Lexicon objects.
 * 
 * word - String representing the word or abbreviation
 * full - String representing the full word in case word was an abbreviation
 * soundslike - String representing a description of how the word could sound
 * phonetic - String representation of the phonetic transcription of the word
 * 
 * @author EML European Media Laboratory GmbH
 *
 * 11.10.2010 MaMi
 */
public class Lexicon 
{	
	private String word;
	private String full;
	private String soundslike;
	private String phonetic;
	private String[] lexicon;
	
	/**
	 * Constructor for creating new a Lexicon object based on the parameters
	 * 
	 * @param word - String representation of the word/abbreviation
	 * @param full - String representation of the full word for abbreviations
	 * @param soundslike - String representation of what the word could sound 
	 * 	like
	 * @param phonetic - String representation of the phonetic transcription
	 */
	public Lexicon
	(
			String word, 
			String full, 
			String soundslike, 
			String phonetic
	) 
	{
		this.word = word;
		this.full = full;
		this.soundslike = soundslike;
		this.phonetic = phonetic;
	}
	
	/**
	 * Constructor for creating a new, empty lexicon object.
	 */
	public Lexicon()
	{
		lexicon = new String[4];
		word = "word/abbrev";
		full = "full";
		soundslike = "soundslike";
		phonetic = "phonetic";
		lexicon[0] = word;
		lexicon[1] = full;
		lexicon[2] = soundslike;
		lexicon[3] = phonetic;
	}

	public static Lexicon extractLexiconEntryFromFile
	(
			ArrayList<String> lexContent, 
			int pos
	) 
	{
		
		Lexicon lexiconEntry = new Lexicon();
		String before = "role=\"bold\">";
		for (int a = pos; a < lexContent.size(); a++)
		{
			int index = lexContent.get(a).toString().indexOf(before);
			int subStringStartIndex = index + before.length();
			int subStringEndIndex = lexContent.get(a).toString().indexOf("</emphasis>");
			if (lexContent.get(a).toString().contains("</item>"))
			{	
				return lexiconEntry;
			}
			else
			{
				if (lexContent.get(a).toString().contains("vendor=\"word\""))
				{
					String word = lexContent.get(a).toString().substring(subStringStartIndex, subStringEndIndex);
					lexiconEntry.setWord(word);
				}
				else
				{
					if (lexContent.get(a).toString().contains("vendor=\"full\""))
					{
						String full = lexContent.get(a).toString().substring(subStringStartIndex, subStringEndIndex);
						lexiconEntry.setFull(full);
					}
					else						
					{
						if (lexContent.get(a).toString().contains("vendor=\"soundslike\""))
						{
							String soundslike = lexContent.get(a).toString().substring(subStringStartIndex, subStringEndIndex);
							lexiconEntry.setSoundslike(soundslike);
						}
						else
						{
							if (lexContent.get(a).toString().contains("vendor=\"phonetic\""))
							{
								String baseform = lexContent.get(a).toString().substring(subStringStartIndex, subStringEndIndex);
								lexiconEntry.setPhonetic(baseform);
							}
//							else
//							{
//								return lexiconEntry;
//							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for Lexicon objects. 
	 */
	/**
	 * @param word the word to set
	 */
	public void setWord
	(
			String word
	) 
	{
		this.word = word;
	}
	/**
	 * @return the word
	 */
	public String getWord
	() 
	{
		return word;
	}
	/**
	 * @param full the full to set
	 */
	public void setFull
	(
			String full
	) 
	{
		this.full = full;
	}
	/**
	 * @return the full
	 */
	public String getFull
	() 
	{
		return full;
	}
	/**
	 * @param soundslike the soundslike to set
	 */
	public void setSoundslike
	(
			String soundslike
	) 
	{
		this.soundslike = soundslike;
	}
	/**
	 * @return the soundslike
	 */
	public String getSoundslike
	() 
	{
		return soundslike;
	}
	/**
	 * @param phonetic the phonetic to set
	 */
	public void setPhonetic
	(
			String phonetic
	) 
	{
		this.phonetic = phonetic;
	}
	/**
	 * @return the phonetic
	 */
	public String getPhonetic
	() 
	{
		return phonetic;
	}
	/**
	 * @param lexicon the lexicon to set
	 */
	public void setLexicon
	(
			String[] lexicon
	) 
	{
		this.lexicon = lexicon;
	}
	/**
	 * @return the lexicon
	 */
	public String[] getLexicon
	() 
	{
		return lexicon;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/


}
