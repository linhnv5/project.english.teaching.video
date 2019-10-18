package topica.linhnv5.video.teaching.zing.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import topica.linhnv5.video.teaching.zing.model.SearchItem;
import topica.linhnv5.video.teaching.zing.model.SearchResult;
import topica.linhnv5.video.teaching.zing.model.StreamResult;

@Service
public class ZingMp3 {

	@Value("${zing.mp3.sig}")
	private String sig;

	@Value("${zing.mp3.apikey}")
	private String apiKey;

	public static String sendRequest(String requestString) throws ZingServiceException {
		StringBuffer buffer = new StringBuffer();

		try {
			URL url = new URL(requestString);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = "gzip".equals(conn.getContentEncoding()) ? new GZIPInputStream(conn.getInputStream()) : conn.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String str;

			while ((str = in.readLine()) != null)
				buffer.append(str);

			in.close();
		} catch (MalformedURLException e) {
			throw new ZingServiceException(e.getMessage());
		} catch (IOException e) {
			throw new ZingServiceException(e.getMessage());
		}

		return buffer.toString();
	}

	public static byte[] sendRequestBinary(String requestString) throws ZingServiceException {
//		System.out.println("Request: "+requestString);
		try {
			URL url = new URL(requestString);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

//			boolean redirect = false;
//
//			// normally, 3xx is redirect
//			int status = conn.getResponseCode();
//			if (status != HttpURLConnection.HTTP_OK) {
//				if (status == HttpURLConnection.HTTP_MOVED_TEMP
//					|| status == HttpURLConnection.HTTP_MOVED_PERM
//						|| status == HttpURLConnection.HTTP_SEE_OTHER)
//				redirect = true;
//			}
//
//			System.out.println("Response Code ... " + status+" location="+conn.getHeaderField("Location"));
//
//			if (redirect)
//				return sendRequestBinary(conn.getHeaderField("Location"));

			InputStream is = conn.getInputStream();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] ab = new byte[1024];

			int lent;
			while ((lent = is.read(ab)) > 0)
				bos.write(ab, 0, lent);

			return bos.toByteArray();
		} catch (MalformedURLException e) {
			throw new ZingServiceException(e.getMessage());
		} catch (IOException e) {
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

	/**
	 * Search track in zingmp3, return infomation about track include lyric, music stream, ...
	 * @param trackName  name of track
	 * @param artistName name of artist
	 * @return           search result, null if not found
	 * @throws ZingServiceException exception occur when doin zingmp3 api
	 */
	public SearchItem getMatchingTrack(String trackName, String artistName) throws ZingServiceException {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("sig", sig);
		params.put("api_key", apiKey);
		params.put("ctime", 1571114522);
		params.put("type", "song");
		params.put("start", 0);
		params.put("count", 20);
		params.put("q", trackName);

		String request  = getURLString("https://zingmp3.vn/api/search", params);
		String response = sendRequest(request);

		System.out.println("Request: "+request);

		Gson gson = new Gson();

		SearchResult result = gson.fromJson(response, SearchResult.class);

		// 
		for (int i = 0; i < result.getData().getItems().length; i++) {
			SearchItem item = result.getData().getItems()[i];
			if (StringUtils.containsIgnoreCase(item.getArtistsNames(), artistName) && StringUtils.containsIgnoreCase(trackName, item.getTitle())) {
				System.out.println("Found Lyric");
				return item;
			}
		}

		return null;
	}

	public StreamResult getStream(String id) throws ZingServiceException {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("api_key", apiKey);
		params.put("sig", "d8eeab57e9a8cf63bc7404113c6f3c3df056c6b466cfdb896c4cecf2f59e59ed9d25310ddb794c756d176f0fa4881526b5434037a5dc7131b90700eb42c4c654");
		params.put("ctime", 1571293130);
		params.put("id", id);

		String request  = getURLString("https://zingmp3.vn/api/song/get-streamings", params);
		String response = sendRequest(request);

		Gson gson = new Gson();

		StreamResult result = gson.fromJson(response, StreamResult.class);

		return result;
	}

}
