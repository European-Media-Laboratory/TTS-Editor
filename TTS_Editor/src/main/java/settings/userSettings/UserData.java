/**
 * UserData loads the relevant data for the specific user after his/her login.
 * This information includes the preferred language, interaction method, created
 * lexica (if any) and projects (two default projects as tutorials are available
 * for every user.
 */
package settings.userSettings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * EMLTTSEditorWeb.settings.userSettingsUserData.java creates the relevant user
 * data based on settings saved at earlier stages. 
 *
 * @author EML European Media Laboratory GmbH
 *
 * 08.02.2011 MaMi
 */
public class UserData 
{	
	private String language;
	private String interaction;
	List<SelectItem> lexiconList;
	List<SelectItem> projectList;
	
	/**
	 * Constructor for creating the UserData object based on information stored
	 * in a file. It takes as arguments
	 * 
	 * @param userSettingsFileContent - ArrayList containing the information 
	 * 	about the specific user.
	 */
	public UserData
	(
			ArrayList<String> userSettingsFileContent
	) 
	{
		String langStart = "<language=\"";
		String end = "\"/>";
		String interactionStart = "<interaction=\"";
		String lexiconStart = "<lexicon>";
		
		String projectStart = "<project>";
		
		for (int a = 0; a < userSettingsFileContent.size(); a++)
		{
			if (userSettingsFileContent.get(a).toString().startsWith(langStart))
			{
				setLanguage(userSettingsFileContent.get(a).toString().substring(langStart.length(), userSettingsFileContent.get(a).toString().length()-end.length()));
			}
			if (userSettingsFileContent.get(a).toString().startsWith(interactionStart))
			{
				setInteraction(userSettingsFileContent.get(a).toString().substring(interactionStart.length(), userSettingsFileContent.get(a).toString().length()-end.length()));				
			}
			if (userSettingsFileContent.get(a).toString().startsWith(lexiconStart))
			{
				lexiconList = getInfoList(userSettingsFileContent, a);
			}
			if (userSettingsFileContent.get(a).toString().startsWith(projectStart))
			{
				projectList = getInfoList(userSettingsFileContent, a);
			}
		}
	}

	/**
	 * UserData.getInfoList extracts the selected item in the selectManyMenu for
	 * selecting a specific project.
	 * 
	 * @param userSettingsFileContent
	 * @param pos
	 * @return - List<SelectItem> containing the selectItem elements to be
	 * 	displayed
	 */
	private List<SelectItem> getInfoList
	(
			ArrayList<String> userSettingsFileContent, 
			int pos
	) 
	{
		String valueStart = "<value=\"";
		String end = "\"/>";
		List<SelectItem> returnList = new ArrayList<SelectItem>();
		for (int a = pos+1; a < userSettingsFileContent.size(); a++)
		{
			if (userSettingsFileContent.get(a).toString().startsWith("</lexicon>") || userSettingsFileContent.get(a).toString().startsWith("</project>"))
			{
				if (returnList.size() > 0)
				{
					return returnList;
				}
			}
			if (userSettingsFileContent.get(a).toString().startsWith(valueStart))
			{
				String info = userSettingsFileContent.get(a).toString().substring(valueStart.length(), userSettingsFileContent.get(a).toString().length()-end.length());
				if (returnList.contains(new SelectItem(info)) == false)
				{
					returnList.add(new SelectItem(info));
				}
			}
		}
		return null;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param interaction the interaction to set
	 */
	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	/**
	 * @return the interaction
	 */
	public String getInteraction() {
		return interaction;
	}

}
