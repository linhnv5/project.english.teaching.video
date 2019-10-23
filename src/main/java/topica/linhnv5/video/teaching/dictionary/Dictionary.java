package topica.linhnv5.video.teaching.dictionary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.dictionary.model.WordInfo;

/**
 * Dictionary service, use to get word information
 * @author ljnk975
 */
@Service
public class Dictionary {

	/**
	 * Search word infomation from dictionary
	 * @param word the word to be search
	 * @return     the word type(n, v, adj, ...), pronunciation, translate
	 */
	public WordInfo searchWord(String word) {
		System.out.println("Word: "+word);

		Document doc;
		try {
			doc = Jsoup.connect("https://dictionary.cambridge.org/dictionary/english-vietnamese/" + word).get();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		Elements elements = doc.select(".dpos-h.di-head.normal-entry");
		if (elements.isEmpty()) {
			System.out.println("   Not found");
			return null;
		}

		WordInfo wordInfo = new WordInfo(word);

		// Word
		elements = doc.select(".tw-bw.dhw.dpos-h_hw.di-title");

		if (elements.size() == 0) {
			System.out.println("   Word not found in doc!");
			return null;
		}

		wordInfo.setWord(elements.get(0).html());

		// Type
		elements = doc.select(".pos.dpos");

		if(elements.size() > 0) {
			wordInfo.setType(elements.get(0).html());
			if (wordInfo.getTypeShort().equals(""))
				System.out.println("Type: "+wordInfo.getType());
		}

		// Pronoun
		elements = doc.select(".ipa.dipa");

		if (elements.size() > 0)
			wordInfo.setPronoun("/"+elements.get(0).html()+"/");

		// Trans
		elements = doc.select(".trans.dtrans");

		if (elements.size() > 0)
			wordInfo.setTrans(elements.get(0).html());

		System.out.println("   Found");
		return wordInfo;
	}

}
