package topica.linhnv5.video.teaching.model;

import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Word info model, contain infomation about word
 * @author ljnk975
 */
public class WordInfo {

	/**
	 * The word to search
	 */
	private String word;

	/**
	 * Infinity word in dictionary
	 */
	private String wordDictionary;

	/**
	 * Type of word
	 */
	private String type;

	/**
	 * Word API
	 */
	private String api;

	/**
	 * Word translate
	 */
	private String trans;

	/**
	 * Image view of this word
	 */
	private BufferedImage image;

	public WordInfo(String word) {
		this.word = word;
	}

	/**
	 * @return the infi
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word the infi to set
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * @return the word
	 */
	public String getWordDictionary() {
		return wordDictionary;
	}

	/**
	 * @param word the word to set
	 */
	public void setWordDictionary(String word) {
		this.wordDictionary = word;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the pronoun
	 */
	public String getAPI() {
		return api;
	}

	/**
	 * @param api the pronoun to set
	 */
	public void setAPI(String api) {
		this.api = api;
	}

	/**
	 * @return the trans
	 */
	public String getTrans() {
		return trans;
	}

	/**
	 * @return the trans
	 */
	public String[] getTrans(int maxw, FontMetrics metric) {
		if (trans == null)
			return null;

		String[] arTrans = trans.split(" ");
		List<String> list = new ArrayList<String>();

		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < arTrans.length; i++) {
			if (buff.length() > 0)
				buff.append(" ");
			if (metric.stringWidth(buff.toString()+arTrans[i]) > maxw) {
				list.add(buff.toString()); buff.setLength(0);
			}
			buff.append(arTrans[i]);
		}
		if (buff.length() > 0)
			list.add(buff.toString());
	
		return list.toArray(new String[0]);
	}

	/**
	 * @param trans the trans to set
	 */
	public void setTrans(String trans) {
		this.trans = Character.toUpperCase(trans.charAt(0))+trans.substring(1);
	}

	public static String getTypeShort(String type) {
		if (type == null)
			return null;
		return type.equals("noun") ? "n"
				: type.equals("verb") ? "v"
				: type.equals("adjective") ? "adj"
				: type.equals("adverb") ? "adv"
				: type.equals("preposition") ? "prep"
				: type.equals("pronoun") ? "pron"
				: type.equals("conjunction") ? "conj"
				: type.contains("article") ? "art"
				: type.equals("interjection") ? "interj"
				: type;
	}

	/**
	 * @return the image
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
