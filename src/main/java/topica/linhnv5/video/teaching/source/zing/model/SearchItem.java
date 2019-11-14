package topica.linhnv5.video.teaching.source.zing.model;

import com.google.gson.annotations.SerializedName;

public class SearchItem {

	@SerializedName("id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("artists")
	private ArtistItem[] artists;

	@SerializedName("artists_names")
	private String artistsNames;
	
	@SerializedName("raw_id")
	private int rawId;

	@SerializedName("alias")
	private String alias;
	
	@SerializedName("link")
	private String link;
	
	@SerializedName("thumbnail")
	private String thumbnail;

	@SerializedName("zing_choise")
	private boolean zingChoise;

	@SerializedName("thumbnail_medium")
	private String thumbnailMedium;
	
	@SerializedName("lyric")
	private String lyric;
	
	@SerializedName("listen")
	private int listen;
	
	@SerializedName("status_name")
	private String statusName;

	@SerializedName("status_code")
	private int statusCode;
	
	@SerializedName("created_at")
	private int createdAt;
	
	@SerializedName("privacy")
	private String privacy;
	
	@SerializedName("streaming_status")
	private int streamingStatus;
	
	@SerializedName("isVN")
	private boolean isVN;

	@SerializedName("duration")
	private int duration;
	
	@SerializedName("is_worldwide")
	private boolean isWorldwide;

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
	 * @return the title
	 */
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
	 * @return the artists
	 */
	public ArtistItem[] getArtists() {
		return artists;
	}

	/**
	 * @param artists the artists to set
	 */
	public void setArtists(ArtistItem[] artists) {
		this.artists = artists;
	}

	/**
	 * @return the artistsNames
	 */
	public String getArtistsNames() {
		return artistsNames;
	}

	/**
	 * @param artistsNames the artistsNames to set
	 */
	public void setArtistsNames(String artistsNames) {
		this.artistsNames = artistsNames;
	}

	/**
	 * @return the rawId
	 */
	public int getRawId() {
		return rawId;
	}

	/**
	 * @param rawId the rawId to set
	 */
	public void setRawId(int rawId) {
		this.rawId = rawId;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * @return the zingChoise
	 */
	public boolean isZingChoise() {
		return zingChoise;
	}

	/**
	 * @param zingChoise the zingChoise to set
	 */
	public void setZingChoise(boolean zingChoise) {
		this.zingChoise = zingChoise;
	}

	/**
	 * @return the thumbnailMedium
	 */
	public String getThumbnailMedium() {
		return thumbnailMedium;
	}

	/**
	 * @param thumbnailMedium the thumbnailMedium to set
	 */
	public void setThumbnailMedium(String thumbnailMedium) {
		this.thumbnailMedium = thumbnailMedium;
	}

	/**
	 * @return the lyric
	 */
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
	 * @return the listen
	 */
	public int getListen() {
		return listen;
	}

	/**
	 * @param listen the listen to set
	 */
	public void setListen(int listen) {
		this.listen = listen;
	}

	/**
	 * @return the statusName
	 */
	public String getStatusName() {
		return statusName;
	}

	/**
	 * @param statusName the statusName to set
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the createdAt
	 */
	public int getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the privacy
	 */
	public String getPrivacy() {
		return privacy;
	}

	/**
	 * @param privacy the privacy to set
	 */
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	/**
	 * @return the streamingStatus
	 */
	public int getStreamingStatus() {
		return streamingStatus;
	}

	/**
	 * @param streamingStatus the streamingStatus to set
	 */
	public void setStreamingStatus(int streamingStatus) {
		this.streamingStatus = streamingStatus;
	}

	/**
	 * @return the isVN
	 */
	public boolean isVN() {
		return isVN;
	}

	/**
	 * @param isVN the isVN to set
	 */
	public void setVN(boolean isVN) {
		this.isVN = isVN;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the isWorldwide
	 */
	public boolean isWorldwide() {
		return isWorldwide;
	}

	/**
	 * @param isWorldwide the isWorldwide to set
	 */
	public void setWorldwide(boolean isWorldwide) {
		this.isWorldwide = isWorldwide;
	}

}
