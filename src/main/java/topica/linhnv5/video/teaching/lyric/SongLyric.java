package topica.linhnv5.video.teaching.lyric;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.cloud.translate.Translate;

import topica.linhnv5.video.teaching.model.WordBox;
import topica.linhnv5.video.teaching.service.DictionaryService;

/**
 * SongLyric: Class that hold an entire song lyric with its timestamp.
 *
 * @author IntelleBitnify
 * @version 1.0 (10/6/2019)
 */
public class SongLyric {

	// Private Class Fields
	private List<Lyric> song;

	// List of mark text
	private List<WordBox> mark;

	/************************************************************
	 * Default Constructor: Creates the object with default SongLyric state
	 * 
	 * @return address of new Lyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public SongLyric() {
		this.song = new ArrayList<>();
		this.mark = new ArrayList<>();
	}

	/************************************************************
	 * Alternate Constructor: Creates the object if the imports are valid and FAILS
	 * otherwise
	 * 
	 * @param inSong The List of Lyric and timestamp for the entire song
	 * 
	 * @return address of new Lyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public SongLyric(List<Lyric> inSong) {
		this.song = inSong;
	}

	/************************************************************
	 * Copy Constructor: Creates an object with an identical object state as the
	 * import
	 * 
	 * @param inSongLyric The SongLyric object
	 * 
	 * @return address of new Lyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *************************************************************/
	public SongLyric(SongLyric inSongLyric) {
		this.song = inSongLyric.getSong();
	}

	// ACCESSOR

	/************************************************************
	 * Get the song from this class
	 * 
	 * @return song (List of Lyric)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public List<Lyric> getSong() {
		return this.song;
	}

	/********************************************************************
	 * Generates cloned object address of SongLyric
	 * 
	 * @return cloneSongLyric (SongLyric)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public SongLyric clone() {
		// Define data structure
		SongLyric cloneSongLyric;

		cloneSongLyric = new SongLyric(this.song);

		return cloneSongLyric;
	}

	/********************************************************************
	 * Check the literal value equality of the import
	 * 
	 * @param inObject Any object to compare with the SongLyric object
	 * 
	 * @return isEqual (boolean)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public boolean equals(Object inObject) {
		// Define data structure
		int i, countEquals;
		boolean isEqual;
		SongLyric inSongLyric;
		List<Lyric> inSong;

		// Define default value
		isEqual = false;
		countEquals = 0;
		if (inObject instanceof SongLyric) {
			inSongLyric = (SongLyric) inObject;
			inSong = inSongLyric.getSong();
			for (i = 0; (inSong.get(i).equals(this.song.get(i)) == true); i++) // Check the equality of each lyric in
																				// list
			{
				countEquals++; // Keep track of the equals
			}
			if (countEquals == inSong.size()) // If all is equals
			{
				isEqual = true;
			}
		}
		return isEqual;
	}

	/********************************************************************
	 * Generates a String representation of this class state information
	 * 
	 * @return songLyricString (String)
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public String toString() {
		// Define data structure
		int i;
		String songLyricString;

		// Define default value
		songLyricString = "";
		for (i = 0; i < song.size(); i++)
			songLyricString += (song.get(i)).toString() + "\n";

		return songLyricString;
	}

	// MUTATORS

	/************************************************************
	 * Sets the lyric based on the imported inLyric
	 * 
	 * @param inSong The entire song that contain all of the lyric
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public void setSong(List<Lyric> inSong) {
		this.song = inSong;
	}

	/********************************************************************
	 * Add one line of lyric to the song
	 * 
	 * @param inLyric One line of lyric
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public void addLyric(Lyric inLyric) {
		this.song.add(inLyric);
	}

	/********************************************************************
	 * Change the speed of song lyric
	 * 
	 * @param inLyric One line of lyric
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public void changeSpeed(double inPlaybackSpeed) {
		// Define data structure
		int i;

		// Change the timestamp to mach the speed
		for (i = 0; i < this.song.size(); i++)
			(this.song.get(i)).setFromTimestamp(((this.song.get(i)).getFromTimestamp() / inPlaybackSpeed));
	}

	/**
	 * @return the mark
	 */
	public List<WordBox> getMark() {
		return mark;
	}

	/**
	 * @param mark the mark to set
	 */
	public void setMark(List<WordBox> mark) {
		this.mark = mark;
	}

	/**
	 * Mark text randomly in song lyric
	 * @param dictionary dictonary service
	 * @param textTime   minimun time of text show
	 */
	public void markRandomText(DictionaryService dictionary, int textTime) {
//		int nText = 0;

		Lyric before = null;

		for (Lyric l : this.song) {
			if (before != null && l.getFromTimestamp()-before.getToTimestamp() < textTime)
				continue;
			if (ThreadLocalRandom.current().nextInt(100) < 40) {
				if (l.markSomeText(dictionary) != null) {
//					nText++;
					before = l; mark.add(l.getMark());
				}
			}
		}

//		System.out.println("NText: "+nText);
	}

	/**
	 * Translate the lyric
	 * @param translate
	 */
	public void translate(Translate translate) {
		for (Lyric l : this.song)
			l.translate(translate);
	}

}
