package topica.linhnv5.video.teaching.lyric;

import java.util.Random;

import org.springframework.util.StringUtils;

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
	private String mark;

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
		this.lyric = inLyric.getLyric();
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
	public String getLyric() {
		return this.lyric;
	}

	/**
	 * Get the mark text from this lyric
	 * @return the mark (String)
	 * 
	 * @author ljnk975
	 */
	public String getMark() {
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
				if (this.lyric.equals(inLyric.getLyric())) // Check equality of lyric
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

	public static Random r = new Random();

	public void markSomeText() {
		if (lyric.equals("") || StringUtils.startsWithIgnoreCase(lyric, "Bài hát") || StringUtils.startsWithIgnoreCase(lyric, "Ca sĩ"))
			return;

		String[] aLyric = lyric.split(" ");

		if (aLyric.length > 0) {
			int next;
			do {
				next = r.nextInt(aLyric.length);
			} while(aLyric[next].contains("'"));
			this.mark = aLyric[next];

			aLyric[next] = "<b><font color=\"yellow\">"+aLyric[next]+"</font></b>";
		}

		this.lyric = String.join(" ", aLyric);
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
	public void setMark(String mark) {
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

}
