package topica.linhnv5.video.teaching.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class HttpUtil {

	public static String sendRequest(String requestString, String cookie) throws Exception {
		System.out.println("Request: "+requestString);

		StringBuffer buffer = new StringBuffer();
		URL url = new URL(requestString);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (cookie != null)
			conn.setRequestProperty("Cookie", cookie);
		conn.connect();

		InputStream is = "gzip".equals(conn.getContentEncoding()) ? new GZIPInputStream(conn.getInputStream()) : conn.getInputStream();

		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String str;

		while ((str = in.readLine()) != null)
			buffer.append(str);

		in.close();

		return buffer.toString();
	}

	public static byte[] sendRequestBinary(String requestString, String cookie) throws Exception {
		System.out.println("RequestB: "+requestString);

		URL url = new URL(requestString);

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
