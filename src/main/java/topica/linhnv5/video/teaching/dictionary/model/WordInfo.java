package topica.linhnv5.video.teaching.dictionary.model;

public class WordInfo {

	private String word;
	private String type;
	private String pronoun;
	private String trans;

	public WordInfo() {
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

	public String getWordWithType() {
		if (this.type == null)
			return this.word;
		return this.word+" ("+this.getTypeShort()+")";
	}

}
