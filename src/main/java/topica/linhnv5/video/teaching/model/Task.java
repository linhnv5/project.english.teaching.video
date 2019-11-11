package topica.linhnv5.video.teaching.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Task table, hold information about task
 * @author ljnk975
 */
@Entity
@Table(name = "Task")
public class Task {

	@Id
	@Column(name = "id", updatable = false, nullable = false)
	private String id;

	// 0 - from video, 1 - from mp3
	@Column(name = "type")
	private byte type;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "music_id", referencedColumnName = "id")
 	private Music music;

	@Column(name = "back")
	private String backFile;

	@Column(name = "input")
	private String inputFile;

	@Column(name = "sub")
	private String subFile;

	@Column(name = "output")
	private String outputFile;

	@Column(name = "errorOccur")
	private String error;

	@Column(name = "time")
	private long timeConsume;

	// Type
	public static byte TASK_FROM_VIDEO_TYPE = 0;
	public static byte TASK_FROM_MUSIC_TYPE = 1;

	public Task() {
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public byte getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(byte type) {
		this.type = type;
	}

	/**
	 * @return the music
	 */
	public Music getMusic() {
		return music;
	}

	/**
	 * @param music the music to set
	 */
	public void setMusic(Music music) {
		this.music = music;
	}

	/**
	 * @return the backFile
	 */
	public String getBackFile() {
		return backFile;
	}

	/**
	 * @param backFile the backFile to set
	 */
	public void setBackFile(String backFile) {
		this.backFile = backFile;
	}

	/**
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the subFile
	 */
	public String getSubFile() {
		return subFile;
	}

	/**
	 * @param subFile the subFile to set
	 */
	public void setSubFile(String subFile) {
		this.subFile = subFile;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the timeConsume
	 */
	public Long getTimeConsume() {
		return timeConsume;
	}

	/**
	 * @param timeConsume the timeConsume to set
	 */
	public void setTimeConsume(long timeConsume) {
		this.timeConsume = timeConsume;
	}

}
