package topica.linhnv5.video.teaching.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.model.WordInfo;
import topica.linhnv5.video.teaching.service.DictionaryService;

/**
 * Dictionary service, use to get word information
 * @author ljnk975
 */
@Service
public class DictionaryServiceImpl implements DictionaryService {

	/**
	 * Search word infomation from dictionary
	 * @param word the word to be search
	 * @return     the word type(n, v, adj, ...), pronunciation, translate
	 */
	public WordInfo searchWord(String word) {
		System.out.println("Dic:SearchWord: "+word);

		Document doc;
		try {
			doc = Jsoup.connect("https://dictionary.cambridge.org/dictionary/english-vietnamese/" + word).get();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		Elements elements = doc.select(".dpos-h.di-head.normal-entry");
		if (elements.isEmpty()) {
			System.out.println("   not found");
			return null;
		}

		WordInfo wordInfo = new WordInfo(word);

		// Word
		elements = doc.select(".tw-bw.dhw.dpos-h_hw.di-title");

		if (elements.size() == 0) {
			System.out.println("   word not found in doc!");
			return null;
		}

		wordInfo.setWordDictionary(elements.get(0).html());

		// Type
		elements = doc.select(".pos.dpos");

		if(elements.size() > 0) {
			wordInfo.setType(WordInfo.getTypeShort(elements.get(0).html()));
//			if (wordInfo.getTypeShort().equals(""))
//				System.out.println("   typemis: "+wordInfo.getType());
		}

		// Pronoun
		elements = doc.select(".ipa.dipa");

		if (elements.size() > 0)
			wordInfo.setAPI("/"+elements.get(0).html()+"/");

		// Trans
		elements = doc.select(".trans.dtrans");

		if (elements.size() > 0)
			wordInfo.setTrans(elements.get(0).html());

		System.out.println("   found");
		return wordInfo;
	}

}
