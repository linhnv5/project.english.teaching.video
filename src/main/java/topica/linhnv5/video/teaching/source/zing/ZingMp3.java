package topica.linhnv5.video.teaching.source.zing;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import topica.linhnv5.video.teaching.source.zing.model.SearchItem;
import topica.linhnv5.video.teaching.source.zing.model.SearchResult;
import topica.linhnv5.video.teaching.source.zing.model.StreamResult;
import topica.linhnv5.video.teaching.util.HashUtil;
import topica.linhnv5.video.teaching.util.HttpUtil;

@Service
public class ZingMp3 {

	@Value("${zing.mp3.apikey}")
	private String apiKey;

	@Value("${zing.mp3.seckey}")
	private String secretKey;

	@Value("${zing.mp3.cookie}")
	private String cookie;

	public String sendRequest(String request, Map <String, Object> params, boolean ignoreLine) throws ZingServiceException {
		try {
			return HttpUtil.sendRequest(request, params, cookie, ignoreLine);
		} catch (Exception e) {
			throw new ZingServiceException(e.getMessage());
		}
	}

	public byte[] sendRequestBinary(String requestString, Map <String, Object> params) throws ZingServiceException {
		try {
			return HttpUtil.sendRequestBinary(requestString, params, cookie);
		} catch (Exception e) {
			throw new ZingServiceException(e.getMessage());
		}
	}

	private long getCTime() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * Search track in zingmp3, return infomation about track include lyric, music stream, ...
	 * @param trackName  name of track
	 * @param artistName name of artist
	 * @return           search result, null if not found
	 * @throws ZingServiceException exception occur when doin zingmp3 api
	 */
	public SearchItem getMatchingTrack(String trackName, String artistName) throws ZingServiceException {
		Map<String, Object> params = new HashMap<String, Object>();

		long ctime = getCTime();
		String sig;

		try {
			String sha256 = HashUtil.getHash265(String.format("ctime=%d", ctime));
			sig = HashUtil.getHMAC512(String.format("/search%s", sha256), secretKey);
		} catch(Exception e) {
			throw new ZingServiceException("");
		}

		params.put("api_key", apiKey);
		params.put("sig",   sig);
		params.put("ctime", ctime);
		params.put("type", "song");
		params.put("start", 0);
		params.put("count", 20);
		params.put("q", trackName+" "+artistName);

		String response = sendRequest("https://zingmp3.vn/api/search", params, true);

		Gson gson = new Gson();

		SearchResult result = gson.fromJson(response, SearchResult.class);

		// 
		for (int i = 0; i < result.getData().getItems().length; i++) {
			SearchItem item = result.getData().getItems()[i];
			if (StringUtils.containsIgnoreCase(item.getArtistsNames(), artistName) && StringUtils.containsIgnoreCase(item.getTitle(), trackName) && item.getLyric() != null && !item.getLyric().equals("")) {
//				System.out.println("Found Lyric");
				return item;
			}
		}

		return null;
	}

	/**
	 * Get streaming info
	 * @param id id of track
	 * @return   stream result
	 * @throws ZingServiceException
	 */
	public StreamResult getStream(String id) throws ZingServiceException {
		Map<String, Object> params = new HashMap<String, Object>();

		long ctime = getCTime();
		String sig;

		try {
			String sha256 = HashUtil.getHash265(String.format("ctime=%did=%s", ctime, id));
			sig = HashUtil.getHMAC512(String.format("/song/get-streamings%s", sha256), secretKey);
		} catch(Exception e) {
			throw new ZingServiceException("");
		}

		params.put("api_key", apiKey);
		params.put("sig",   sig);
		params.put("ctime", ctime);
		params.put("id", id);

		String response = sendRequest("https://zingmp3.vn/api/song/get-streamings", params, true);

		Gson gson = new Gson();

		StreamResult result = gson.fromJson(response, StreamResult.class);

		return result;
	}

}
