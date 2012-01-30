/**
 * Tokens object stores information about tokens contained in the users input.
 * These currently are:
 * 
 * id
 * value
 * type
 * 
 * Additionally it contains methods for creating Tokens objects based on 
 * information in files or information extracted from the user input.
 * 
 * createTokenList
 * getTokenListFromFile
 */
package data;

import java.util.ArrayList;


/**
 * EMLTTSEditorWeb.data.Tokens.java offers methods for accessing information 
 * about token objects.
 * 
 * id - String representation of a numeric identifier
 * value - String containing the token value
 * type - String information about the type of token (word, number, punctuation)
 *
 * Additionally it offers methods for creating/extracting Tokens objects
 * 
 * createTokenList - creates Token objects based on user input
 * getTokenListFromFile - extracts Token objects from earlier saved token files
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.11.2010 MaMi
 */
public class Tokens 
{
	private String id;
	private String value;
	private String type;
	private String[] token;
	
	static String endString = "/>";
	
	/**
	 * Constructor for creating a new Tokens object.
	 */
	public Tokens()
	{
		setToken(new String[3]);
		setId(getId());
		setValue(getValue());
		setType(getType());
		getToken()[0] = getId();
		getToken()[1] = getValue();
		getToken()[2] = getType();
	}
	
	/**
	 * Constructor for creating a new Tokens object containing the information
	 * given in the parameters
	 * 
	 * @param id - String representation of the numeric identifier
	 * @param value - String value of the token
	 * @param type - String representation of the type
	 */
	public Tokens
	(
			String id,
			String value,
			String type
	)
	{
		this.setId(id);
		this.setValue(value);
		this.setType(type);
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * ADDITIONAL METHODS for Tokens Object creation and handling
	 */
	/**
	 * Tokens.createTokenList takes as parameters
	 * 
	 * @param tmpTokens - ArrayList of String representations of tokens
	 * @param tmpWords - ArrayList of String representations of words
	 * @return - ArrayList of Tokens objects based on words and tokens lists
	 */
	public static ArrayList<Tokens> createTokenList
	(
			ArrayList<String> tmpTokens,
			ArrayList<String> tmpWords
	)
	{
		ArrayList<Tokens> returnList = new ArrayList<Tokens>();
		for (int a = 0; a < tmpTokens.size(); a++)
		{
			Tokens token = new Tokens();
			token.setId("" + a);
			token.setValue(tmpTokens.get(a).toString());
			if (tmpWords.contains(tmpTokens.get(a).toString()))
			{
				token.setType("word");
			}
			else
			{
				if (tmpTokens.get(a).toString().trim().length() == 1)
				{
					token.setType("sign");
				}
				else
				{
					if (tmpTokens.get(a).toString().trim().length() > 0)
					{
						token.setType("other");						
					}
				}
			}
			returnList.add(token);
		}
		return returnList;
	}

	/**
	 * Tokens.getTokenListFromFile takes as parameters
	 * 
	 * @param tokFile
	 *            - String representation of the FileName containing earlier
	 *            saved token information
	 * @return - ArrayList of Token objects extracted from the saved file.
	 */
	public static ArrayList<Tokens> getTokenListFromFile(String tokFile) {
		String idString = "<id=";
		String valueString = "<value=";
		String typeString = "<type=";
		String tokenEndString = "</token>";
		ArrayList<Tokens> returnList = new ArrayList<Tokens>();
		ArrayList<String> content = auxillary.FileIOMethods.getFileContent(tokFile);
		if (content != null) {
			Tokens token = new Tokens();
			for (int a = 0; a < content.size(); a++) {
				if (content.get(a).toString().endsWith(endString)) {
					int end = content.get(a).toString().length()
							- endString.length();
					if (content.get(a).toString().startsWith(idString)) {
						token.setId(content.get(a).toString()
								.substring(idString.length(), end));
					}
					if (content.get(a).toString().startsWith(valueString)) {
						token.setValue(content.get(a).toString()
								.substring(valueString.length(), end));
					}
					if (content.get(a).toString().startsWith(typeString)) {
						token.setType(content.get(a).toString()
								.substring(typeString.length(), end));
					}
				}
				if (content.get(a).toString().startsWith(tokenEndString)) {
					returnList.add(token);
					token = new Tokens();
				}
			}
		}
		return returnList;
	}

	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**************************************************************
	 * ************************************************************
	 **************************************************************/
	/**
	 * AUTOMATICALLY created getter and setter methods for sentences objects. 
	 */
	/**
	 * @param id the id to set
	 */
	public void setId
	(
			String id
	) 
	{
		this.id = id;
	}
	/**
	 * @return the id
	 */
	public String getId
	() 
	{
		return id;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue
	(
			String value
	) 
	{
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public String getValue
	() 
	{
		return value;
	}
	/**
	 * @param type the type to set
	 */
	public void setType
	(
			String type
	) 
	{
		this.type = type;
	}
	/**
	 * @return the type
	 */
	public String getType
	() 
	{
		return type;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken
	(
			String[] token
	) 
	{
		this.token = token;
	}
	/**
	 * @return the token
	 */
	public String[] getToken
	() 
	{
		return token;
	}
	/**************************************************************
	 * ************************************************************
	 **************************************************************/


}
