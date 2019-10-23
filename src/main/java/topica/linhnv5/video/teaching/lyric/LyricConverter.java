package topica.linhnv5.video.teaching.lyric;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import topica.linhnv5.video.teaching.dictionary.model.WordInfo;

/**
 * Converter class, using to convert lyric from lrc to srt format
 * @author ljnk975
 */
public class LyricConverter {

	/********************************************************************
	 * Read the .lrc file
	 * 
	 * @param inSong     The object of SongLyric that will hold entire lyric from
	 *                   the file
	 * @param inFilename The filename that it reads from
	 * 
	 * @return Modify the state of the SongLyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 *********************************************************************/
	public static SongLyric readLRC(String inLyric) {
		// Define data structure
		SongLyric inSongLyric = new SongLyric();

		int offset = 0, i;

		while (true) {
			int next = inLyric.indexOf('[', offset+1);

			if (next < 0) {
				processLRC(inSongLyric, inLyric.substring(offset));
				break;
			}

			processLRC(inSongLyric, inLyric.substring(offset, next));
			offset = next;
		}

		List<Lyric> inSong = inSongLyric.getSong();

		for (i = 0; i < inSong.size(); i++) {
			 // End of lyric
			if (i == inSong.size() - 1) {
				inSong.get(i).setToTimestamp(inSong.get(i).getFromTimestamp()+5);
				continue;
			}
			inSong.get(i).setToTimestamp(inSong.get(i+1).getFromTimestamp());
		}

		return inSongLyric;
	}

	/************************************************************
	 * Process to read the .lrc file and save it to the {(seconds), (lyric)} format
	 * 
	 * @return Modify the state of SongLyric object
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 ************************************************************/
	public static void processLRC(SongLyric inSongLyric, String inLine) {
		// Define data structure
		double inTimestamp;
		String[] subLyric;
		Lyric lrc;

		// Initialize object
		lrc = new Lyric();

		// LRC data: [minutes:seconds.miliseconds]lyric

		// Split data into time [hours:minutes.seconds] and lyric
		subLyric = inLine.split("]");

		// Process time data into real number in memory
		subLyric[0] = subLyric[0].replace("[", "");
		subLyric[0] = subLyric[0].replace(":", "");

		// Set Processed to each line lyric object

		// Step 1: Save the timestamp in seconds from [minutesseconds.miliseconds] the
		// seconds used to be 100 for 1 minutes instead of 60
		try {
			inTimestamp = Double.parseDouble(subLyric[0]); // Convert to real number from string
			inTimestamp = (((int) (inTimestamp / 100)) * 60) + (inTimestamp % 100);

			// Step 2: Set the timestamp
			lrc.setFromTimestamp(inTimestamp);

			// Step 3: Set the lyric
			if (subLyric.length == 1) // If the lyric is blank
				lrc.setLyric(""); // Set a blank lyric
			else // If the lyric contain a lyric
				lrc.setLyric(subLyric[1]); // Set the lyric

			// Add each line lyric to the song lyric
			inSongLyric.addLyric(lrc);
		} catch(Exception e) {
		}
	}

	public static SongLyric readCSV(String inLyric) {
		// Define data structure
		SongLyric inSongLyric = new SongLyric();

		try {
			Reader reader = new StringReader(inLyric);

			CsvToBean<LyricCSV> csvToBean = new CsvToBeanBuilder<LyricCSV>(reader)
	                .withType(LyricCSV.class)
	                .withIgnoreLeadingWhiteSpace(true)
	                .build();

	        Iterator<LyricCSV> csvUserIterator = csvToBean.iterator();

	        while (csvUserIterator.hasNext()) {
	        	LyricCSV csv = csvUserIterator.next();
				Lyric lrc = new Lyric();
				lrc.setFromTimestamp(csv.getStart());
				lrc.setToTimestamp(csv.getEnd());
				lrc.setLyric(csv.getLyric() == null ? "" : csv.getLyric());

				if (csv.getMark() != null && !csv.getMark().equals("")) {
					WordInfo wordInfo = new WordInfo(csv.getMark());
					wordInfo.setWord(csv.getMarkInfi());
					wordInfo.setType(csv.getType());
					wordInfo.setPronoun(csv.getApi());
					wordInfo.setTrans(csv.getTrans());
					lrc.setMark(wordInfo);
				}

				inSongLyric.addLyric(lrc);
	        }
	        
	        System.out.println("Lyric: "+inSongLyric.getSong().size());
		} catch(Exception e) {
		}

		return inSongLyric;
	}

	
	/********************************************************************
	 * Write the file to the format of .srt file
	 * 
	 * @param inSong     The object of SongLyric that hold entire lyric
	 * @param inFilename The filename that it will write to
	 * 
	 * @return Output to the .srt file
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 * @throws Exception 
	 *********************************************************************/
	public static String writeSRT(SongLyric inSongLyric) throws Exception {
		// Define data structure
		int i, from_hours, from_minutes, from_seconds, from_miliseconds, to_hours, to_minutes, to_seconds,
				to_miliseconds;

		StringBuilder buff = new StringBuilder();
		
		System.out.println(">> INFO: Converting to .srt ..");

		// Define default value
		List<Lyric> inSong = inSongLyric.getSong();

		// SRT data:
		// index
		// time_from --> time_to
		// lyric
		//

		for (i = 0; i < inSong.size(); i++) {
			// Format the timestamp
			Lyric lyric = inSong.get(i);

			// Step 1: Time from
			from_hours   = (int) (lyric.getFromTimestamp()) / 3600;
			from_minutes = (int) (lyric.getFromTimestamp()) / 60; // 181/60 = 3
			from_seconds = (int) (lyric.getFromTimestamp()) % 60; // 181-(3*60) = 1
			from_miliseconds = (int) (lyric.getFromTimestamp() * 1000) % 1000; // .000

			// Step 2: Time to
			to_hours   = (int) (lyric.getToTimestamp()) / 3600;
			to_minutes = (int) (lyric.getToTimestamp()) / 60;
			to_seconds = (int) (lyric.getToTimestamp()) % 60;
			to_miliseconds = (int) (lyric.getToTimestamp() * 1000) % 1000;

			// Begin to write the file based on srt format
			buff.append(i + 1).append('\n'); // Index in SRT file
			buff.append(String.format("%02d", from_hours));
			buff.append(":");
			buff.append(String.format("%02d", from_minutes));
			buff.append(":");
			buff.append(String.format("%02d", from_seconds));
			buff.append(",");
			buff.append(String.format("%03d", from_miliseconds));
			buff.append(" --> ");
			buff.append(String.format("%02d", to_hours));
			buff.append(":");
			buff.append(String.format("%02d", to_minutes));
			buff.append(":");
			buff.append(String.format("%02d", to_seconds));
			buff.append(",");
			buff.append(String.format("%03d", to_miliseconds)).append('\n');
			buff.append(lyric.getLyric()).append('\n');
			buff.append('\n');
		}

		System.out.println(">> INFO: Succesfully convert!");

		return buff.toString();
	}

	public static String writeCSV(SongLyric inSongLyric) {
		StringWriter writer = new StringWriter();

		StatefulBeanToCsv<LyricCSV> beanToCsv = new StatefulBeanToCsvBuilder<LyricCSV>(writer)
                .withQuotechar('\"')
                .build();

		try {
			beanToCsv.write(inSongLyric.getSong().stream().map(lrc -> {
				LyricCSV csv = new LyricCSV();
				csv.setStart(lrc.getFromTimestamp());
				csv.setEnd(lrc.getToTimestamp());
				csv.setLyric(lrc.getLyricWithoutMark());
				if (lrc.getMark() != null) {
					csv.setMark(lrc.getMark().getInfi());
					csv.setMarkInfi(lrc.getMark().getWord());
					csv.setType(lrc.getMark().getType());
					csv.setApi(lrc.getMark().getPronoun());
					csv.setTrans(lrc.getMark().getTrans());
				}
				return csv;
			}).collect(Collectors.toList()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return writer.getBuffer().toString();
	}

}
