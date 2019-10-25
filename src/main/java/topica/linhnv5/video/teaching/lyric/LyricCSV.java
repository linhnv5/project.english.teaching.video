package topica.linhnv5.video.teaching.lyric;

import com.opencsv.bean.CsvBindByName;

public class LyricCSV {

	@CsvBindByName(column = "from")
	private double start;

	@CsvBindByName(column = "end")
	private double end;

	@CsvBindByName(column = "lyric")
	private String lyric;
	
	@CsvBindByName(column = "lyricTrans")
	private String lyricTrans;
	
	@CsvBindByName(column = "mark")
	private String mark;

	@CsvBindByName(column = "markDictionary")
	private String markDictionary;

	@CsvBindByName(column = "markType")
	private String type;

	@CsvBindByName(column = "markAPI")
	private String api;
	
	@CsvBindByName(column = "markTrans")
	private String trans;

	/**
	 * @return the start
	 */
	public double getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(double start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public double getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(double end) {
		this.end = end;
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

	/**
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}

	/**
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}

	/**
	 * @return the markInfi
	 */
	public String getMarkDictionary() {
		return markDictionary;
	}

	/**
	 * @param markDictionary the markInfi to set
	 */
	public void setMarkDictionary(String markDictionary) {
		this.markDictionary = markDictionary;
	}

	/**
	 * @return the api
	 */
	public String getApi() {
		return api;
	}

	/**
	 * @param api the api to set
	 */
	public void setApi(String api) {
		this.api = api;
	}

	/**
	 * @return the type
	 */
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
	 * @return the trans
	 */
	public String getTrans() {
		return trans;
	}

	/**
	 * @param trans the trans to set
	 */
	public void setTrans(String trans) {
		this.trans = trans;
	}

}
