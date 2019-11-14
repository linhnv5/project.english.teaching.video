package topica.linhnv5.video.teaching.lyric;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import topica.linhnv5.video.teaching.model.WordInfo;
import topica.linhnv5.video.teaching.service.DictionaryService;

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

		try {
			BufferedReader bufRdr = new BufferedReader(new StringReader(inLyric));

	        //Read the first line
	        String line = bufRdr.readLine();
	        while (line != null) //Proceed to read till the last line if there is a first line
	        {
	            processLRC(inSongLyric, line);   
	            line = bufRdr.readLine();       
	        }
	        
	        bufRdr.close();
		} catch(Exception e) {
		}

		Collections.sort(inSongLyric.getSong(), (lrc1, lrc2) -> (int)Math.signum(lrc1.getFromTimestamp()-lrc2.getFromTimestamp()));
		List<Lyric> inSong = inSongLyric.getSong();

		for (int i = 0; i < inSong.size(); i++) {
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
		String lyric;
		Lyric lrc;

		// Initialize object
		lrc = new Lyric();

		// LRC data: [minutes:seconds.miliseconds]lyric

		// Split data into time [hours:minutes.seconds] and lyric
		subLyric = inLine.split("]");

		// Lyric
		lyric = subLyric.length == 1 || subLyric[subLyric.length-1].startsWith("[") ? "" : subLyric[subLyric.length-1];

		// Loop lyric
		for (int i = subLyric.length-2; i >= 0; i--) {
			// Process time data into real number in memory
			subLyric[i] = subLyric[i].replace("[", "").replace(":", "");

			// Set Processed to each line lyric object

			// Step 1: Save the timestamp in seconds from [minutesseconds.miliseconds] the
			// seconds used to be 100 for 1 minutes instead of 60
			try {
				inTimestamp = Double.parseDouble(subLyric[i]); // Convert to real number from string
				inTimestamp = (((int) (inTimestamp / 100)) * 60) + (inTimestamp % 100);

				// Step 2: Set the timestamp
				lrc.setFromTimestamp(inTimestamp);

				// Step 3: Set the lyric
				lrc.setLyric(lyric);

				// Add each line lyric to the song lyric
				inSongLyric.addLyric(lrc);
			} catch(Exception e) {
			}
		}
	}

	/********************************************************************
	 * Read the .csv file
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
	public static SongLyric readCSV(String inLyric, DictionaryService dictionary) throws Exception {
		// Define data structure
		SongLyric inSongLyric = new SongLyric();

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
			lrc.setLyricTrans(csv.getLyricTrans() == null ? "" : csv.getLyricTrans());

			if (csv.getMark() != null && !csv.getMark().equals("")) {
				boolean add = true;

				WordInfo wordInfo = new WordInfo(csv.getMark());
				wordInfo.setWordDictionary(csv.getMarkDictionary());
				wordInfo.setType(csv.getType());
				wordInfo.setAPI(csv.getApi());
				wordInfo.setTrans(csv.getTrans());

				if (wordInfo.getWordDictionary() == null || wordInfo.getType() == null || wordInfo.getAPI() == null || wordInfo.getTrans() == null) {
					WordInfo wordInfo2 = dictionary.searchWord(wordInfo.getWord());

					if (wordInfo2 != null) {
						if (wordInfo.getWordDictionary() == null)
							wordInfo.setWordDictionary(wordInfo2.getWordDictionary());

						if (wordInfo.getType() == null)
							wordInfo.setType(wordInfo2.getType());
						
						if (wordInfo.getAPI() == null)
							wordInfo.setAPI(wordInfo2.getAPI());
						
						if (wordInfo.getTrans() == null)
							wordInfo.setTrans(wordInfo2.getTrans());
					} else
						add = false;
				}

				if (add) {
					inSongLyric.getMark().add(wordInfo);
					lrc.setMark(wordInfo);
				}
			}

			inSongLyric.addLyric(lrc);
        }
        
        System.out.println("Lyric: "+inSongLyric.getSong().size());

		return inSongLyric;
	}

	@SuppressWarnings("resource")
	public static SongLyric readExcel(byte[] inLyric, DictionaryService dictionary) throws Exception {
		// Define data structure
		SongLyric inSongLyric = new SongLyric();

		ByteArrayInputStream is = new ByteArrayInputStream(inLyric);

		// Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(is);

        // Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        // Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();

        // Ignore header
        rowIterator.next();
    
        while (rowIterator.hasNext()) {
        	Row row = rowIterator.next();

        	int cellnum = 0;

        	Lyric lrc = new Lyric();
			lrc.setFromTimestamp(row.getCell(cellnum++).getNumericCellValue());
			lrc.setToTimestamp(row.getCell(cellnum++).getNumericCellValue());
			lrc.setLyric(row.getCell(cellnum++).getStringCellValue());

			Cell cell = row.getCell(cellnum++);
			if (cell != null)
				lrc.setLyricTrans(cell.getStringCellValue());

			cell = row.getCell(cellnum++);

			String mark;
			if (cell != null && (mark = cell.getStringCellValue()) != null && !mark.equals("")) {
				boolean add = true;

				WordInfo wordInfo = new WordInfo(mark);

				cell = row.getCell(cellnum++);
				if (cell != null)
					wordInfo.setWordDictionary(cell.getStringCellValue());

				cell = row.getCell(cellnum++);
				if (cell != null)
					wordInfo.setType(cell.getStringCellValue());

				cell = row.getCell(cellnum++);
				if (cell != null)
					wordInfo.setAPI(cell.getStringCellValue());

				cell = row.getCell(cellnum++);
				if (cell != null)
					wordInfo.setTrans(cell.getStringCellValue());

				if (wordInfo.getWordDictionary() == null || wordInfo.getType() == null || wordInfo.getAPI() == null || wordInfo.getTrans() == null) {
					WordInfo wordInfo2 = dictionary.searchWord(wordInfo.getWord());

					if (wordInfo2 != null) {
						if (wordInfo.getWordDictionary() == null)
							wordInfo.setWordDictionary(wordInfo2.getWordDictionary());

						if (wordInfo.getType() == null)
							wordInfo.setType(wordInfo2.getType());
						
						if (wordInfo.getAPI() == null)
							wordInfo.setAPI(wordInfo2.getAPI());
						
						if (wordInfo.getTrans() == null)
							wordInfo.setTrans(wordInfo2.getTrans());
					} else
						add = false;
				}

				if (add) {
					inSongLyric.getMark().add(wordInfo);
					lrc.setMark(wordInfo);
				}
			}

			inSongLyric.addLyric(lrc);
        }
        
        System.out.println("Lyric: "+inSongLyric.getSong().size());

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
	public static String writeSRT(SongLyric inSongLyric, String markColor) throws Exception {
		// Define data structure
		int i, from_hours, from_minutes, from_seconds, from_miliseconds, to_hours, to_minutes, to_seconds,
				to_miliseconds;

		StringBuilder buff = new StringBuilder();
		
//		System.out.println(">> INFO: Converting to .srt ..");

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
			buff.append(lyric.getLyric(markColor)).append('\n');
			buff.append('\n');
		}

//		System.out.println(">> INFO: Succesfully convert!");

		return buff.toString();
	}

	/********************************************************************
	 * Write the file to the format of .csv file
	 * 
	 * @param inSong     The object of SongLyric that hold entire lyric
	 * @param inFilename The filename that it will write to
	 * 
	 * @return Output to the .csv file
	 * 
	 * @author IntelleBitnify
	 * @version 1.0 (10/6/2019)
	 * @throws Exception 
	 *********************************************************************/
	@SuppressWarnings("resource")
	public static String writeCSV(SongLyric inSongLyric) {
		StringWriter writer = new StringWriter();

		CSVWriter csvWriter = new CSVWriter(writer);
		csvWriter.writeNext(new String[] {"from", "end", "lyric", "lyricTrans", "mark", "markDictionary", "markType", "markAPI", "markTrans"}, false);

		try {
			inSongLyric.getSong().stream().map(lrc -> {
				LyricCSV csv = new LyricCSV();
				csv.setStart(lrc.getFromTimestamp());
				csv.setEnd(lrc.getToTimestamp());
				csv.setLyric(lrc.getSourceLyric());
				csv.setLyricTrans(lrc.getLyricTrans());
				if (lrc.getMark() != null) {
					csv.setMark(lrc.getMark().getWord());
					csv.setMarkDictionary(lrc.getMark().getWordDictionary());
					csv.setType(lrc.getMark().getType());
					csv.setApi(lrc.getMark().getAPI());
					csv.setTrans(lrc.getMark().getTrans());
				}
				return csv;
			}).forEach(csv -> {
				csvWriter.writeNext(new String[] {String.valueOf(csv.getStart()), String.valueOf(csv.getEnd()), csv.getLyric(), csv.getLyricTrans(), csv.getMark(), csv.getMarkDictionary(), csv.getType(), csv.getApi(), csv.getTrans()}, false);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return writer.getBuffer().toString();
	}

	@SuppressWarnings("resource")
	public static byte[] writeExcel(SongLyric inSongLyric) {
		// Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Data");

        int rownum = 0, cellnum = 0;

        // Header
        Row row = sheet.createRow(rownum++); Cell cell;

		String[] header = new String[] {"from", "end", "lyric", "lyricTrans", "mark", "markDictionary", "markType", "markAPI", "markTrans"};

		for (cellnum = 0; cellnum < header.length; cellnum++) {
			cell = row.createCell(cellnum, CellType.STRING);
			cell.setCellValue(header[cellnum]);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			// 
			for (Lyric lrc : inSongLyric.getSong()) {
		        row = sheet.createRow(rownum++);

				cellnum = 0;

				cell = row.createCell(cellnum++, CellType.NUMERIC);
				cell.setCellValue(lrc.getFromTimestamp());

				cell = row.createCell(cellnum++, CellType.NUMERIC);
				cell.setCellValue(lrc.getToTimestamp());

				cell = row.createCell(cellnum++, CellType.STRING);
				cell.setCellValue(lrc.getSourceLyric());

				cell = row.createCell(cellnum++, CellType.STRING);
				cell.setCellValue(lrc.getLyricTrans());

				if (lrc.getMark() != null) {
					cell = row.createCell(cellnum++, CellType.STRING);
					cell.setCellValue(lrc.getMark().getWord());

					cell = row.createCell(cellnum++, CellType.STRING);
					cell.setCellValue(lrc.getMark().getWordDictionary());

					cell = row.createCell(cellnum++, CellType.STRING);
					cell.setCellValue(lrc.getMark().getType());

					cell = row.createCell(cellnum++, CellType.STRING);
					cell.setCellValue(lrc.getMark().getAPI());

					cell = row.createCell(cellnum++, CellType.STRING);
					cell.setCellValue(lrc.getMark().getTrans());
				}
			}

			// Auto size
			for (cellnum = 0; cellnum < header.length; cellnum++)
				sheet.autoSizeColumn(cellnum);

			// Write the workbook in file system
	        workbook.write(bos);
	        bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

        return bos.toByteArray();
	}

}
