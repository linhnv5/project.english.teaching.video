package topica.linhnv5.video.teaching.source.zing.model;

import com.google.gson.annotations.SerializedName;

public class ArtistItem {

	@SerializedName("name")
	private String name;
	
	@SerializedName("link")
	private String link;
	
	@SerializedName("id")
	private String id;

	@SerializedName("spotlight")
	private boolean spotlight;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the spotlight
	 */
	public boolean isSpotlight() {
		return spotlight;
	}

	/**
	 * @param spotlight the spotlight to set
	 */
	public void setSpotlight(boolean spotlight) {
		this.spotlight = spotlight;
	}

}
