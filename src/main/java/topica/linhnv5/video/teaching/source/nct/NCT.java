package topica.linhnv5.video.teaching.source.nct;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import topica.linhnv5.video.teaching.source.nct.model.Track;
import topica.linhnv5.video.teaching.source.nct.model.TrackList;
import topica.linhnv5.video.teaching.util.HashUtil;
import topica.linhnv5.video.teaching.util.HttpUtil;

@Service
public class NCT {

	public String sendRequest(String request, Map <String, Object> params) throws NCTServiceException {
		try {
			return HttpUtil.sendRequest(request, params, null, false);
		} catch (Exception e) {
			throw new NCTServiceException(e.getMessage());
		}
	}

	public byte[] sendRequestBinary(String requestString, Map <String, Object> params) throws NCTServiceException {
		try {
			return HttpUtil.sendRequestBinary(requestString, params, null);
		} catch (Exception e) {
			throw new NCTServiceException(e.getMessage());
		}
	}

	@Value("${nct.lyric.seckey}")
	private String secretKey;

	public String getLyric(Track track) throws Exception {
		return HashUtil.decodeRC4(sendRequest(track.getLyric(), null), secretKey);
	}

	/**
	 * Search track in zingmp3, return infomation about track include lyric, music stream, ...
	 * @param trackName  name of track
	 * @param artistName name of artist
	 * @return           search result, null if not found
	 * @throws ZingServiceException exception occur when doin zingmp3 api
	 */
	public Track getMatchingTrack(String trackName, String artistName) throws NCTServiceException {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("title", trackName);
		params.put("singer", artistName);

		String response;
		Document doc;
/*
		response = sendRequest("http://www.nhaccuatui.com/tim-nang-cao", params);

		try {
			doc = Jsoup.parse(response);
		} catch(Exception e) {
			throw new NCTServiceException(e.getMessage());
		}
*/
		try {
			doc = Jsoup.connect(HttpUtil.getURLString("http://www.nhaccuatui.com/tim-nang-cao", params)).get();
		} catch(Exception e) {
			throw new NCTServiceException(e.getMessage());
		}

		Elements list = doc.select(".search_returns_list .list_song .item_content");

		final String xml = "https://www.nhaccuatui.com/flash/xml?html5=true&key1=";
		for (int i = 0; i < list.size(); i++) {
			Element element = list.get(i);

			String title  = element.select(".name_song").html();
			String artist = element.select(".name_singer").html();

			System.out.println("  Title: "+title+" artist: "+artist);

			if (StringUtils.containsIgnoreCase(artist, artistName) && StringUtils.containsIgnoreCase(title, trackName)) {
				String link = element.select(".name_song").attr("href");

				response = sendRequest(link, null);

				int k = response.indexOf(xml);
				if (k >= 0) {
					link = response.substring(k, response.indexOf("\";", k));
					response = sendRequest(link, null);

					try {
						JAXBContext jaxbContext = JAXBContext.newInstance(TrackList.class);
						Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						TrackList root = (TrackList) jaxbUnmarshaller.unmarshal(new StringReader(response));
						return root.getTrack();
					} catch (Exception e) {
						throw new NCTServiceException(e.getMessage());
					}
				}
			}
		}

		return null;
	}

}
