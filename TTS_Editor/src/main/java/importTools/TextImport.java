/**
 * TextImport is part of the importTools-class, which allows for importing
 * various files for further processing. TextImport allows for importing plain
 * text files.
 */
package importTools;

import java.io.File;
import java.util.ArrayList;

import javax.faces.context.FacesContext;

import editor.EditorManager;

/**
 * EMLTTSEditorWeb.importTools.TextImport.java offers methods for importing
 * plain text files into the editor environment.
 *
 * @author EML European Media Laboratory GmbH
 *
 * 03.02.2011 MaMi
 */
public class TextImport {

	/**
	 * TextImport.importText takes as argument 
	 * 
	 * @param uploadedFile - File containing the uploaded file to be imported
	 * 	into the editor environment.
	 * 
	 * May transform from simple String representation into correct data format
	 * (sentences representation!)
	 */
	public static void importText
	(
			File uploadedFile
	) 
	{
		ArrayList<String> fileContent = auxillary.FileIOMethods.getFileContent(uploadedFile.getAbsolutePath());
		String sentenceString = "";
		for (int a = 0; a < fileContent.size(); a++)
		{
			sentenceString = sentenceString + fileContent.get(a).toString() + "\n";
		}
		((EditorManager) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("editor")).setResult(sentenceString);
	}

}
