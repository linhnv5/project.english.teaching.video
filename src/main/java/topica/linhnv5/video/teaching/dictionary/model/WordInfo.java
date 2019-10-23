package topica.linhnv5.video.teaching.dictionary.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Word info model, contain infomation about word
 * @author ljnk975
 */
public class WordInfo {

	private String infi;

	private String word;
	private String type;
	private String pronoun;
	private String trans;

	public WordInfo(String infi) {
		this.infi = infi;
	}

	/**
	 * @return the infi
	 */
	public String getInfi() {
		return infi;
	}

	/**
	 * @param infi the infi to set
	 */
	public void setInfi(String infi) {
		this.infi = infi;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
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
	public String getPronoun() {
		return pronoun;
	}

	/**
	 * @param pronoun the pronoun to set
	 */
	public void setPronoun(String pronoun) {
		this.pronoun = pronoun;
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
	public String[] getTrans(int maxw) {
		if (trans == null)
			return null;

		String[] arTrans = trans.split(" ");
		List<String> list = new ArrayList<String>();

		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < arTrans.length; i++) {
			if (buff.length() > 0)
				buff.append(" ");
			buff.append(arTrans[i]);
			if (buff.length() > maxw) {
				list.add(buff.toString()); buff.setLength(0);
			}
		}
		if (buff.length() > 0)
			list.add(buff.toString());
	
		return list.toArray(new String[0]);
	}

	/**
	 * @param trans the trans to set
	 */
	public void setTrans(String trans) {
		this.trans = trans;
	}

	public String getTypeShort() {
		if (this.type == null)
			return null;
		return this.type.equals("noun") ? "n"
				: this.type.equals("verb") ? "v"
				: this.type.equals("adjective") ? "adj"
				: this.type.equals("adverb") ? "adv"
				: this.type.equals("preposition") ? "prep"
				: this.type.equals("pronoun") ? "pron"
				: this.type.equals("conjunction") ? "conj"
				: this.type.contains("article") ? "art"
				: this.type.equals("interjection") ? "interj"
				: "";
	}

}
