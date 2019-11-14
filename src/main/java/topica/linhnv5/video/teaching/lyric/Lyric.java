package topica.linhnv5.video.teaching.lyric;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.StringUtils;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Translate.TranslateOption;

import topica.linhnv5.video.teaching.model.WordInfo;
import topica.linhnv5.video.teaching.service.DictionaryService;

/**
 * Lyric: Contain timestamp and lyric text.
 *
 * @author IntelleBitnify
 * @version 1.0 (10/6/2019)
 */
public class Lyric {

	private double from;
	private double to;

	private String lyric;
	private String lyricTrans;

	private WordInfo mark;

	/************************************************************
	 * Default Constructor: Creates the object with default Lyric state
	 * 
	 * @return address of new Lyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public Lyric() {
		this.from = 0;
		this.to   = 0;
		this.lyric = "<no data>";
	}

	/************************************************************
	 * Alternate Constructor: Creates the object if the imports are valid and FAILS
	 * otherwise
	 * 
	 * @param inTimestamp The timestamp of a lyric in a song (in seconds)
	 * @param toTimestamp The timestamp of a lyric in a song (in seconds)
	 * @param inLyric     The Lyric of the song in that timestamp
	 * 
	 * @return address of new Lyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public Lyric(double inTimestamp, double toTimestamp, String inLyric) {
		this.from = inTimestamp;
		this.lyric = inLyric;
	}

	/************************************************************
	 * Copy Constructor: Creates an object with an identical object state as the
	 * import
	 * 
	 * @param inLyric The Lyric object that contain information about the lyric and
	 *                timestamp
	 * 
	 * @return address of new Lyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *************************************************************/
	public Lyric(Lyric inLyric) {
		this.from  = inLyric.getFromTimestamp();
		this.to    = inLyric.getToTimestamp();
		this.lyric = inLyric.getSourceLyric();
	}

	// ACCESSOR

	/************************************************************
	 * Get the lyric from this class
	 * 
	 * @return lyric (String)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public String getLyric(String markColor) {
		StringBuilder buff = new StringBuilder();
		buff.append(this.mark != null ? 
				this.lyric.replaceAll(this.mark.getWord(), "<b><font color=\""+markColor+"\">"+this.mark.getWord()+"</font></b>")
				: this.lyric);
		if (this.lyricTrans != null) {
			buff.append("\n")
				.append("<font size=10>")
				.append(this.lyricTrans)
				.append("</font>")
				;
		}
		return buff.toString();
	}

	/**
	 * Get the source lyric without trans and mark
	 * @return the source lyric
	 */
	public String getSourceLyric() {
		return this.lyric;
	}

	/**
	 * Get the mark text from this lyric
	 * @return the mark (String)
	 * 
	 * @author ljnk975
	 */
	public WordInfo getMark() {
		return mark;
	}

	/************************************************************
	 * Get the timestamp from this class
	 * 
	 * @return timestamp (real)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public double getFromTimestamp() {
		return this.from;
	}

	/************************************************************
	 * Get the timestamp from this class
	 * 
	 * @return timestamp (real)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public double getToTimestamp() {
		return this.to;
	}

	/********************************************************************
	 * Generates cloned object address of Lyric
	 * 
	 * @return cloneLyric (Lyric)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public Lyric clone() {
		// Define data structure
		Lyric cloneLyric = new Lyric(this.from, this.to, this.lyric);

		return cloneLyric;
	}

	/********************************************************************
	 * Check the literal value equality of the import
	 * 
	 * @param inObject Any object to compare with the Lyric object
	 * 
	 * @return isEqual (boolean)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public boolean equals(Object inObject) {
		// Define data structure
		boolean isEqual;
		Lyric inLyric;

		// Define default value
		isEqual = false;

		if (inObject instanceof Lyric) {
			inLyric = (Lyric) inObject;
			if (this.from == inLyric.getFromTimestamp()) // Check equality of timestamp
			{
				if (this.lyric.equals(inLyric.lyric)) // Check equality of lyric
				{
					isEqual = true;
				}
			}
		}
		return isEqual;
	}

	/********************************************************************
	 * Generates a String representation of this class state information
	 * 
	 * @return lyricString (String)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public String toString() {
		// Define data structure
		String lyricString;

		// Generate a string representation about what this class contain
		lyricString = String.format("%.2f", this.from) + " " + this.lyric;

		return lyricString;
	}

	/************************************************************
	 * Sets the lyric based on the imported inLyric
	 * 
	 * @param inLyric The lyric in this particular line
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public void setLyric(String inLyric) {
		this.lyric = inLyric;
	}

	/**
	 * Sets the mark text
	 * @param mark the mark to set
	 * 
	 * @author ljnk975
	 */
	public void setMark(WordInfo mark) {
		this.mark = mark;
	}

	/************************************************************
	 * Sets the timestamp based on the imported inTimestamp
	 * 
	 * @param inTimestamp The type of fuel used by the engine ("BATTERY" or "DIESEL"
	 *                    or "BIO")
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public void setFromTimestamp(double inTimestamp) {
		this.from = inTimestamp;
	}

	/************************************************************
	 * Sets the timestamp based on the imported inTimestamp
	 * 
	 * @param inTimestamp The type of fuel used by the engine ("BATTERY" or "DIESEL"
	 *                    or "BIO")
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public void setToTimestamp(double toTimestamp) {
		this.to = toTimestamp;
	}

	private boolean isMeta() {
		return lyric.equals("") || StringUtils.startsWithIgnoreCase(lyric, "Bài hát") || StringUtils.startsWithIgnoreCase(lyric, "Ca sĩ") || StringUtils.startsWithIgnoreCase(lyric, "Song") || StringUtils.startsWithIgnoreCase(lyric, "Singer");
	}

	public WordInfo markSomeText(DictionaryService dictionary) {
		if (isMeta())
			return null;

		String[] aLyric = lyric.split(" ");

		if (aLyric.length == 0)
			return null;

		int next, i = 0; WordInfo wordInfo;

		do {
			if (++i >= aLyric.length)
				return null;

			next = ThreadLocalRandom.current().nextInt(aLyric.length);
		} while(aLyric[next].contains("'") || (wordInfo = dictionary.searchWord(aLyric[next])) == null);

		return this.mark = wordInfo;
	}

	public void translate(Translate translate) {
		if (isMeta())
			return;

		Translation translation = translate.translate(this.lyric,
				TranslateOption.sourceLanguage("en"),
				TranslateOption.targetLanguage("vi"));

		this.lyricTrans = translation.getTranslatedText();
	}

	/**
	 * @return the lyricTrans
	 */
	public String getLyricTrans() {
		return lyricTrans;
	}

	/**
	 * @param lyricTrans the lyricTrans to set
	 */
	public void setLyricTrans(String lyricTrans) {
		this.lyricTrans = lyricTrans;
	}

}
