package topica.linhnv5.video.teaching.source.nct.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "track")
public class Track {

	private String title;

	private String creator;

	private String location;

	private String info;

	private String image;

	private String thumb;

	private String bgImage;

	private String coverImage;

	private String avatar;

	private String lyric;

	private String newTab;

	private String kBit;

	private String key;

	/**
	 * @return the title
	 */
	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the creator
	 */
	@XmlElement(name = "creator")
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the location
	 */
	@XmlElement(name = "location")
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the info
	 */
	@XmlElement(name = "info")
	public String getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @return the image
	 */
	@XmlElement(name = "image")
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the thumb
	 */
	@XmlElement(name = "thumb")
	public String getThumb() {
		return thumb;
	}

	/**
	 * @param thumb the thumb to set
	 */
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	/**
	 * @return the bgImage
	 */
	@XmlElement(name = "bgimage")
	public String getBgImage() {
		return bgImage;
	}

	/**
	 * @param bgImage the bgImage to set
	 */
	public void setBgImage(String bgImage) {
		this.bgImage = bgImage;
	}

	/**
	 * @return the coverImage
	 */
	@XmlElement(name = "coverimage")
	public String getCoverImage() {
		return coverImage;
	}

	/**
	 * @param coverImage the coverImage to set
	 */
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	/**
	 * @return the avatar
	 */
	@XmlElement(name = "avatar")
	public String getAvatar() {
		return avatar;
	}

	/**
	 * @param avatar the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * @return the lyric
	 */
	@XmlElement(name = "lyric")
	public String getLyric() {
		return lyric;
	}

	/**
	 * @param lyric the lyric to set
	 */
	public void setLyric(String lyric) {
		this.lyric = lyric;
	}

	/**
	 * @return the newTab
	 */
	@XmlElement(name = "newTab")
	public String getNewTab() {
		return newTab;
	}

	/**
	 * @param newTab the newTab to set
	 */
	public void setNewTab(String newTab) {
		this.newTab = newTab;
	}

	/**
	 * @return the kBit
	 */
	@XmlElement(name = "kbit")
	public String getKBit() {
		return kBit;
	}

	/**
	 * @param kBit the kBit to set
	 */
	public void setKBit(String kBit) {
		this.kBit = kBit;
	}

	/**
	 * @return the key
	 */
	@XmlElement(name = "key")
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

}
