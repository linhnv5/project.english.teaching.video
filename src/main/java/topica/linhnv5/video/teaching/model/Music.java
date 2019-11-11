package topica.linhnv5.video.teaching.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Music table, hold information about music source
 * @author ljnk975
 */
@Entity
@Table(name = "Music")
public class Music {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private long id;

	@Column(name = "chart")
	private String chart;

	@Column(name = "artist")
	private String artist;

	@Column(name = "source")
	private String musicSource;

	@Column(name = "file")
	private String file;

	@Column(name = "sub")
	private String sub;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the chart
	 */
	public String getChart() {
		return chart;
	}

	/**
	 * @param chart the chart to set
	 */
	public void setChart(String chart) {
		this.chart = chart;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the musicSource
	 */
	public String getMusicSource() {
		return musicSource;
	}

	/**
	 * @param musicSource the musicSource to set
	 */
	public void setMusicSource(String musicSource) {
		this.musicSource = musicSource;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the sub
	 */
	public String getSub() {
		return sub;
	}

	/**
	 * @param sub the sub to set
	 */
	public void setSub(String sub) {
		this.sub = sub;
	}

}
