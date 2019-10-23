package topica.linhnv5.video.teaching.service;

import topica.linhnv5.video.teaching.model.WordInfo;

/**
 * Dictionary service, use to get word information
 * @author ljnk975
 */
public interface DictionaryService {

	/**
	 * Search word infomation from dictionary
	 * @param word the word to be search
	 * @return     the word type(n, v, adj, ...), pronunciation, translate
	 */
	public WordInfo searchWord(String word);

}
