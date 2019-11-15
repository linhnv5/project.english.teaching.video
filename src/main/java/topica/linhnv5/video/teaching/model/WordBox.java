package topica.linhnv5.video.teaching.model;

/**
 * Word box model, contain word info, x, y, duration
 * @author ljnk975
 */
public class WordBox {

	/**
	 * Word in lyric
	 */
	private String word;

	/**
	 * Word info
	 */
	private WordInfo wordDictionary;

	/**
	 * X of word box
	 */
	private float x;
	
	/**
	 * Y of word box
	 */
	private float y;
	
	/**
	 * Word box duration
	 */
	private double duration;

	/**
	 * Wordbox start
	 */
	private double from;
	
	/**
	 * Wordbox end
	 */
	private double to;

	public WordBox(String word) {
		this.word = word;
	}

	public WordBox(String word, WordInfo wordInfo) {
		this.word = word;
		this.wordDictionary = wordInfo;
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
	 * @return the wordDictionary
	 */
	public WordInfo getWordDictionary() {
		return wordDictionary;
	}

	/**
	 * @param wordDictionary the wordDictionary to set
	 */
	public void setWordDictionary(WordInfo wordDictionary) {
		this.wordDictionary = wordDictionary;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * @return the from
	 */
	public double getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(double from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public double getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(double to) {
		if (to - from < duration)
			to = from + duration;
		this.to = to;
	}

}
