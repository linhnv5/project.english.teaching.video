package topica.linhnv5.video.teaching.source.nct.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tracklist")
public class TrackList {

	private String type;

	private Track track;

	/**
	 * @return the type
	 */
	@XmlElement(name = "type")
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
	 * @return the track
	 */
	@XmlElement(name = "track")
	public Track getTrack() {
		return track;
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(Track track) {
		this.track = track;
	}

}
