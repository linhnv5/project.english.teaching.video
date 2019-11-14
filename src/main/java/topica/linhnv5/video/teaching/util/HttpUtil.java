package topica.linhnv5.video.teaching.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Http Utility contain function to request http, decode url string, ...
 * 
 * @author ljnk975
 */
public class HttpUtil {

	/**
	 * This method is used to get a parameter string from the Map.
	 * 
	 * @param params key-value parameters
	 * @return A String containing the url parameter.
	 * @throws MusixMatchException
	 */
	public static String getURLString(String methodName, Map<String, Object> params) throws Exception {
		String paramString = new String();

		paramString += methodName + "?";

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			try {
				paramString += entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new Exception("Problem encoding " + entry.getValue());
			}
			paramString += "&";
		}

		paramString = paramString.substring(0, paramString.length() - 1);

		return paramString;
	}

	public static String sendRequest(String request, Map <String, Object> params, String cookie, boolean ignoreLine) throws Exception {
		if (params != null)
			request  = HttpUtil.getURLString(request, params);

		System.out.println("Request: "+request);

		StringBuffer buffer = new StringBuffer();
		URL url = new URL(request);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (cookie != null)
			conn.setRequestProperty("Cookie", cookie);
		conn.connect();

		InputStream is = "gzip".equals(conn.getContentEncoding()) ? new GZIPInputStream(conn.getInputStream()) : conn.getInputStream();

		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String str;

		while ((str = in.readLine()) != null) {
			if (!ignoreLine && buffer.length() > 0)
				buffer.append('\n');
			buffer.append(str);
		}

		in.close();

		return buffer.toString();
	}

	public static byte[] sendRequestBinary(String request, Map <String, Object> params, String cookie) throws Exception {
		if (params != null)
			request  = HttpUtil.getURLString(request, params);

		System.out.println("RequestB: "+request);

		URL url = new URL(request);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (cookie != null)
			conn.setRequestProperty("Cookie", cookie);
		conn.connect();

		InputStream is = conn.getInputStream();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] ab = new byte[1024];

		int lent;
		while ((lent = is.read(ab)) > 0)
			bos.write(ab, 0, lent);

		return bos.toByteArray();
	}

}
