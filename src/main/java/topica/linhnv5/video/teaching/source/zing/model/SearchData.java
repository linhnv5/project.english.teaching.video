package topica.linhnv5.video.teaching.source.zing.model;

import com.google.gson.annotations.SerializedName;

public class SearchData {

	@SerializedName("total")
	private int total;

	@SerializedName("items")
	private SearchItem[] items;

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * @return the items
	 */
	public SearchItem[] getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(SearchItem[] items) {
		this.items = items;
	}

}
