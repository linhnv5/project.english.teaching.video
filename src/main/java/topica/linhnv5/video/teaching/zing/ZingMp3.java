package topica.linhnv5.video.teaching.zing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import topica.linhnv5.video.teaching.util.HttpUtil;
import topica.linhnv5.video.teaching.zing.model.SearchItem;
import topica.linhnv5.video.teaching.zing.model.SearchResult;
import topica.linhnv5.video.teaching.zing.model.StreamResult;

@Service
public class ZingMp3 {

	@Value("${zing.mp3.apikey}")
	private String apiKey;

	@Value("${zing.mp3.seckey}")
	private String secretKey;

	@Value("${zing.mp3.cookie}")
	private String cookie;

	public String sendRequest(String requestString) throws ZingServiceException {
		try {
			return HttpUtil.sendRequest(requestString, cookie);
		} catch (Exception e) {
			throw new ZingServiceException(e.getMessage());
		}
	}

	public byte[] sendRequestBinary(String requestString) throws ZingServiceException {
		try {
			return HttpUtil.sendRequestBinary(requestString, cookie);
		} catch (Exception e) {
			throw new ZingServiceException(e.getMessage());
		}
	}

	/**
	 * This method is used to get a parameter string from the Map.
	 * 
	 * @param params key-value parameters
	 * @return A String containing the url parameter.
	 * @throws MusixMatchException
	 */
	public static String getURLString(String methodName, Map<String, Object> params) throws ZingServiceException {
		String paramString = new String();

		paramString += methodName + "?";

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			try {
				paramString += entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new ZingServiceException("Problem encoding " + entry.getValue());
			}
			paramString += "&";
		}

		paramString = paramString.substring(0, paramString.length() - 1);

		return paramString;
	}

	private long getCTime() {
		return System.currentTimeMillis() / 1000;
	}

	private String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

	private String getHash265(String mss) throws Exception {
		return bytesToHex(MessageDigest.getInstance("SHA-256").digest(mss.getBytes("UTF-8")));
	}

	private String getHMAC512(String mss) throws Exception {
		final String HMAC_SHA512 = "HmacSHA512";

		Mac sha512_HMAC = Mac.getInstance(HMAC_SHA512);      
		sha512_HMAC.init(new SecretKeySpec(secretKey.getBytes("UTF-8"), HMAC_SHA512));

        return bytesToHex(sha512_HMAC.doFinal(mss.getBytes("UTF-8")));
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
			String sha256 = getHash265(String.format("ctime=%d", ctime));
			sig = getHMAC512(String.format("/search%s", sha256));
		} catch(Exception e) {
			throw new ZingServiceException("");
		}

		params.put("api_key", apiKey);
		params.put("sig",   sig);
		params.put("ctime", ctime);
		params.put("type", "song");
		params.put("start", 0);
		params.put("count", 20);
		params.put("q", trackName);

		String request  = getURLString("https://zingmp3.vn/api/search", params);
		String response = sendRequest(request);

		Gson gson = new Gson();

		SearchResult result = gson.fromJson(response, SearchResult.class);

		// 
		for (int i = 0; i < result.getData().getItems().length; i++) {
			SearchItem item = result.getData().getItems()[i];
			if (StringUtils.containsIgnoreCase(item.getArtistsNames(), artistName) && StringUtils.containsIgnoreCase(trackName, item.getTitle())) {
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
			String sha256 = getHash265(String.format("ctime=%did=%s", ctime, id));
			sig = getHMAC512(String.format("/song/get-streamings%s", sha256));
		} catch(Exception e) {
			throw new ZingServiceException("");
		}

		params.put("api_key", apiKey);
		params.put("sig",   sig);
		params.put("ctime", ctime);
		params.put("id", id);

		String request  = getURLString("https://zingmp3.vn/api/song/get-streamings", params);
		String response = sendRequest(request);

		Gson gson = new Gson();

		StreamResult result = gson.fromJson(response, StreamResult.class);

		return result;
	}

}
