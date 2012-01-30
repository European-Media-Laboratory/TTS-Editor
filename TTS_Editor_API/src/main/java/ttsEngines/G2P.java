/**
 * G2P interface provides the methods for accessing G2P tools. This has to be
 * implemented by the user/developer of the TTS Editor. Several methods are 
 * provided:
 * 
 * doG2P
 * phonetise
 * phonetise
 * doG2pExternal
 * 
 * 15. December 2011
 * 	MaMi
 */
package ttsEngines;

import java.util.Collection;
import java.util.List;

import data.Words;

/**
 *	TTS_Editor_AP.IttsEngines.G2P.java provides the interface to various G2P
 *	methods:
 *
 * doG2P - phonetise a single word in a specific language
 * phonetise - phonetise a list of Strings in a specific language
 * phonetise - phonetise a single string in a sepecific language
 * doG2pExternal - phonetise a single word in a specific language
 *
 * @author EML European Media Laboratory GmbH
 *
 * 15.12.2011 MaMi
 */

public interface G2P 
{
	String doG2P(Words word, String lang);
	List<String> phonetise(Collection<String> words, String lang);
	String phonetise(String word, String lang);
	String doG2PExternal(Words word, String lang);
}
