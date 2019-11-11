package topica.linhnv5.video.teaching.service.impl;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.model.Music;
import topica.linhnv5.video.teaching.repository.MusicRepository;
import topica.linhnv5.video.teaching.service.MusicService;
import topica.linhnv5.video.teaching.util.FileUtil;
import topica.linhnv5.video.teaching.zing.ZingMp3;
import topica.linhnv5.video.teaching.zing.model.SearchItem;
import topica.linhnv5.video.teaching.zing.model.StreamResult;

@Service
public class MusicServiceImpl implements MusicService {

	@Autowired
	private MusicRepository musicRepository;

	@Autowired
	private ZingMp3 zingMp3;

	@Value("${video.teaching.musicfolder}")
	private String musicFolder;

	/**
	 * If music not found, query music on music source like zingmp3, nhacuatui
	 * @param chart  chart name
	 * @param artist artist name
	 * @return the music
	 */
	private Music getMusicInSource(String chart, String artist) throws Exception {
		// Return music
		Music music = new Music();
		music.setChart(chart);
		music.setArtist(artist);
		music.setMusicSource("zing");

		// input name
		String inputFileName = chart.replaceAll(" ", "_")+"("+artist.replaceAll(" ", "_")+")_zing";

		//
		System.out.println("Search MP3");

		// Search item
		SearchItem item = zingMp3.getMatchingTrack(chart, artist);

		// The ID
		String id = item.getId();

		// Get link stream
		StreamResult streamResult = zingMp3.getStream(id);

		String link128 = streamResult.getData().getData().getLink128();
		String link320 = streamResult.getData().getData().getLink320();

		// Link address
		String link = link320.equals("") ? link128 : link320;

		// Print the lyric url
		System.out.println("Id: "+id+" lyric: "+item.getLyric());

		// Print link
		System.out.println("Link: "+link);

		// Get file
		FileOutputStream fos = null;

		// Check link
		if (link != null && !link.equals("")) {
			// File name
			File input = FileUtil.matchFileName(musicFolder, inputFileName+".mp3");

			System.out.println("Music file: "+input.getPath());

			try {
				// Get stream data
				byte[] mp3Data = zingMp3.sendRequestBinary("http:"+link);

				// Write mp3 to file
				fos = new FileOutputStream(input);
				fos.write(mp3Data);

				// Set file
				music.setFile(input.getName());
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch(Exception e) {
					}
				}
			}
		}

		// Check lyric
		if (item.getLyric() != null) {
			// Sub name
			File sub = new File(musicFolder+inputFileName+".lrc");

			System.out.println("Sub file: "+sub.getPath());

			try {
				// Get lyric data
				String lyric = zingMp3.sendRequest(item.getLyric());

				// Write subtitle to file
				fos = new FileOutputStream(sub);
				fos.write(lyric.getBytes("UTF-8"));

				// Set sub
				music.setSub(sub.getName());
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch(Exception e) {
					}
				}
			}
		}

		return music;
	}

	@Override
	public Music getMusic(String chart, String artist) {
		Music music = musicRepository.findByChartAndArtist(chart, artist);
		if (music == null) {
			try {
				music = getMusicInSource(chart, artist);
				musicRepository.save(music);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return music;
	}

}
