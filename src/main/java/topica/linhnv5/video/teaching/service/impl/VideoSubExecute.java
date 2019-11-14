package topica.linhnv5.video.teaching.service.impl;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.cloud.translate.Translate;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import topica.linhnv5.video.teaching.lyric.Lyric;
import topica.linhnv5.video.teaching.lyric.LyricConverter;
import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.Config;
import topica.linhnv5.video.teaching.model.Music;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.TaskExecute;
import topica.linhnv5.video.teaching.model.WordInfo;
import topica.linhnv5.video.teaching.service.DictionaryService;
import topica.linhnv5.video.teaching.service.MusicService;
import topica.linhnv5.video.teaching.service.TaskService;
import topica.linhnv5.video.teaching.util.FileUtil;

/**
 * Execute class, runnable execute task code
 * 
 * @author ljnk975
 *
 */
@Component
public class VideoSubExecute {

	@Autowired
	private ServletContext servletContext;

	@Value("${video.teaching.workingfolder}")
	private String workingFolder;

	@Value("${video.teaching.musicfolder}")
	private String musicFolder;

	@Value("${video.teaching.infolder}")
	private String inFolder;

	@Value("${video.teaching.outfolder}")
	private String outFolder;

	@Autowired
	private FFprobe ffprobe;

	@Autowired
	private FFmpeg ffmpeg;

	@Autowired
	private MusicService musicService;

	@Autowired
	private DictionaryService dictionary;

	@Autowired
	private TaskService taskService;

	@Value("${video.teaching.text.time}")
	private int textTime;

	/**
	 * Add subtitle filter to video
	 * @param sub
	 * @param fontColor
	 * @param fontSize
	 * @param opacity
	 * @return
	 */
	private String addSubtitle(File sub, Color fontColor, float fontSize, float opacity) {
		return new StringBuilder()
				.append("subtitles=\'input/"+sub.getName()+"\'")
				.append(":force_style='BorderStyle=4,BackColour=&H").append(Integer.toHexString((int)(255*opacity)).toUpperCase()).append("&")
				.append(",OutlineColour=&H").append(Integer.toHexString((int)(255*opacity)).toUpperCase()).append("&")
				.append(",Outline=1,Shadow=1,MarginV=20,MarginL=30,MarginR=30")
				.append(",Fontsize=").append(fontSize)
				.append(",PrimaryColour=&H").append(Integer.toHexString(fontColor.getBlue()).toUpperCase())
					.append(Integer.toHexString(fontColor.getGreen()).toUpperCase())
					.append(Integer.toHexString(fontColor.getRed()).toUpperCase())
					.append("&")
				.append("'")
				.toString();
	}

	/**
	 * Draw word box to image
	 * @param info
	 * @param opacity
	 * @return
	 * @throws Exception
	 */
	private BufferedImage drawWordInfoImage(WordInfo info, Color wordColor, Color typeColor, float opacity) throws Exception {
		// Color
		Color borderColor = Color.WHITE, boxColor = new Color(0, 0, 0, opacity);

		// Font
	    Font dynamicFont1 = new Font("Corbel", Font.BOLD,  63);
	    Font dynamicFont2 = new Font("Arial",  Font.PLAIN, 32);
	    Font dynamicFont3 = new Font("Arial",  Font.PLAIN, 30);
	    Font dynamicFont4 = new Font("Tahoma", Font.PLAIN, 35);

	    // minw
	    int minw = 300;

	    // Calc w, h
	    Canvas c = new Canvas();
		
	    String word = Character.toUpperCase(info.getWordDictionary().charAt(0))+info.getWordDictionary().substring(1);

	    int w1 = c.getFontMetrics(dynamicFont1).stringWidth(word);
		int h1 = c.getFontMetrics(dynamicFont1).getHeight();

	    String api  = info.getAPI();
	    String type = info.getTypeShort();

	    int w2 = 0;
	    int h2 = 0;

	    if (api != null) {
	    	w2 += 20 + c.getFontMetrics(dynamicFont2).stringWidth(api);
	    	h2 = c.getFontMetrics(dynamicFont2).getHeight();
	    }

	    int w3 = w2; int tw = 0;
	    int h3 = 0;

	    if (type != null) {
	    	if (w3 > 0)
	    		w3 += 10;
	    	tw = c.getFontMetrics(dynamicFont3).stringWidth(type);
	    	w3 += tw < 40 ? 50 : tw+10;
	    	h3 = c.getFontMetrics(dynamicFont3).getHeight();
	    }

	    // width of image
	    int w = Math.max(minw, Math.max(w1, w3)+60);

	    String[] trans = info.getTrans(w-60, c.getFontMetrics(dynamicFont4));
	    
	    // height of image
	    int h = h1 + (trans == null ? 0 : trans.length*(c.getFontMetrics(dynamicFont4).getHeight()+5)) + 100;

	    // Image
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();

		// draw background
		g.setColor(boxColor);
		g.fillRoundRect(0, 0, w, h, w/5, h/5);
		
	    // word dictionary
	    g.setColor(wordColor);
		g.setFont(dynamicFont1);
	    g.drawString(word, w/2-w1/2, 5+g.getFontMetrics().getAscent());

		// api
		if (api != null) {
			g.setColor(borderColor);
			g.setStroke(new BasicStroke(2f));
			g.drawRoundRect(30, 25+h1, w2, 50, 20, 20);

			g.setColor(typeColor);
		    g.setFont(dynamicFont2);
			g.drawString(api, 40, 50+h1-h2/2+g.getFontMetrics().getAscent());
		}

		// word type
		if (type != null) {
			g.setColor(borderColor);
			g.setStroke(new BasicStroke(2f));

			int tx = w2 > 0 ? w2 + 45 : 30, ty = 25+h1;
			if (tw < 40) {
				g.drawOval(tx, ty, 50, 50); tx += 25-tw/2;
			} else {
				g.drawRoundRect(tx, ty, tw+10, 50, 10, 10); tx += 5;
			}

			g.setColor(typeColor);
		    g.setFont(dynamicFont3);
			g.drawString(type, tx, 50+h1-h3/2+g.getFontMetrics().getAscent());
		}

	    // trans
		if (trans != null) {
			g.setColor(typeColor);
		    g.setFont(dynamicFont4);

			int yt = 95+h1;
			for (String tran : trans) {
				g.drawString(tran, 30, yt+g.getFontMetrics().getAscent());
			    yt += g.getFontMetrics().getHeight()+5;
			}
		}

		// inject img
		info.setImage(img);

		return img;
	}

	/**
	 * Draw a box of word, word type, word trans to video
	 * @param info
	 * @param from
	 * @param to
	 * @param opacity
	 * @param x
	 * @param y
	 * @param anchor
	 * @return
	 * @throws Exception 
	 */
	private String drawWordInfo(WordInfo info, double from, double to, float opacity, int x, int y, int vw, int vh, Color wordColor, Color typeColor, File tmpDir) throws Exception {
		BufferedImage wordImage = drawWordInfoImage(info, wordColor, typeColor, opacity);

		// Write image
		File wordFile = FileUtil.matchFileName(tmpDir.getPath(), info.getWordDictionary()+".png");
		ImageIO.write(wordImage, "PNG", wordFile);

		if (to - from < textTime)
			to = from + textTime;

		x = x - wordImage.getWidth()/2;  if (x < 0) x = 20; if (x > vw) x = vw - wordImage.getWidth()  - 20;
		y = y - wordImage.getHeight()/2; if (y < 0) y = 20; if (y > vh) y = vh - wordImage.getHeight() - 20;

		float scale = (float)vw / 1280;

		return new StringBuilder()
				.append("[in];")
				.append("movie=input/").append(tmpDir.getName()).append("/").append(wordFile.getName())
				.append(",scale=").append("iw*").append((float)(int)(scale*100)/100).append(":").append("-1")
				.append("[word];")
				.append("[in][word]overlay=").append(x).append(":").append(y)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.toString();
	}

	/**
	 * Check and mk input output dir
	 */
	private void checkInOutFolder() {
		FileUtil.checkAndMKDir(inFolder);
		FileUtil.checkAndMKDir(outFolder);
	}

	/**
	 * Normalize config
	 * @param config
	 */
	private Config fullConfig(Config config) {
		if (config == null)
			config = new Config();

		// lyric
		if (config.getLyricOpacity() <= 0 || config.getLyricOpacity() >= 1)
			config.setLyricOpacity(0.5F);
		if (config.getLyricSize() <= 0)
			config.setLyricSize(20);
		if (config.getLyricTransSize() == 0)
			config.setLyricTransSize(config.getLyricSize()*3/4);
		else if (config.getLyricTransSize() < 0)
			config.setLyricTransSize(config.getLyricSize()*(-config.getLyricTransSize()));
		if (config.getLyricColor() == null)
			config.setLyricColor(Color.white);
		if (config.getLyricMarkColor() == null)
			config.setLyricMarkColor(Color.red);

		// word box
		if (config.getWordBoxOpacity() <= 0 || config.getWordBoxOpacity() >= 1)
			config.setWordBoxOpacity(0.5F);
		if (config.getWordBoxPrimaryColor() == null)
			config.setWordBoxPrimaryColor(new Color(247, 173, 70));
		if (config.getWordBoxSecondaryColor() == null)
			config.setWordBoxSecondaryColor(Color.white);
		if (config.getWordBoxX() == 0)
			config.setWordBoxX(-0.1F);
		if (config.getWordBoxY() == 0)
			config.setWordBoxY(-0.5F);
		
		return config;
	}

	@Async("threadPoolExecutor")
	public void doAddSubToVideo(String track, String artist, String inputFileName, String inputSubName, Config config, TaskExecute execute) {
		// Config
		config = fullConfig(config);

		// Input and output file
		File input, sub, tmpDir, output;

		// The task
		Task task = execute.getTask();

		// type
		task.setType(Task.TASK_FROM_VIDEO_TYPE);

		try {
			// Check input and output folder
			checkInOutFolder();

			// Input and output file
			input  = new File(inFolder  + inputFileName);
			output = new File(outFolder + inputFileName);

			// task input
			task.setInputFile(input.getName());

			// Tmp dir
			tmpDir = new File(input.getPath()+"_tmp");

			// sub file
			sub = new File(tmpDir.getPath().replace("_tmp", ".srt"));

			// Get the lyric
			SongLyric songLyric = null;

			// If sub file specific
			if (inputSubName != null) {
				songLyric = LyricConverter.readExcel(FileUtil.getFileContent(new File(inFolder+inputSubName)), dictionary);
			} else {
				// Search item
				Music music = musicService.getMusic(track, artist);

				// Music not found
				if (music == null)
					throw new Exception("Music not found!");

				// Sub not found
				if (music.getSub() == null)
					throw new Exception("Sub not found!");

				// Set task music
				task.setMusic(music);

				// Get the lyric
				songLyric = doGetSubtitle(music);
			}

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric, config.getLyricTransSize(), config.getLyricMarkColor());

			// Write subtitle to file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(sub);
				fos.write(lyric.getBytes("UTF-8"));
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Could not write subtitles file, progress fail!");
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch(Exception e) {
					}
				}
			}

			// task sub
			task.setSubFile(sub.getName());

			try {
				//
				System.out.println("Do FFprobe");

				// Do ffprobe
				FFmpegProbeResult probeResult = ffprobe.probe(input.getPath());

				FFmpegStream stream = probeResult.getStreams().get(0);

				//
				System.out.println("Do FFmpeg");

				int width  = (config.getW() > 0 ? config.getW() : stream.width)/2*2;
				int height = (config.getH() > 0 ? config.getH() : stream.height)/2*2;

				// Make filter
				StringBuilder vfilter = new StringBuilder("scale=").append(width).append(":").append(height).append("[in];[in]")
										.append(addSubtitle(sub, config.getLyricColor(), config.getLyricSize(), config.getLyricOpacity()));

				int x = (int) (config.getWordBoxX() < 0 ? width*(-config.getWordBoxX()) : config.getWordBoxX());
				int y = (int) (config.getWordBoxY() < 0 ? height*(-config.getWordBoxY()) : config.getWordBoxY());
				float opacity = config.getWordBoxOpacity();

				for (Lyric l : songLyric.getSong()) {
					if (l.getMark() != null)
						vfilter.append(drawWordInfo(l.getMark(), l.getFromTimestamp(), l.getToTimestamp(), opacity, x, y, width, height, config.getWordBoxPrimaryColor(), config.getWordBoxSecondaryColor(), tmpDir));
				}

				// Do ffmpeg
				FFmpegBuilder builder = ffmpeg.builder()
					.addInput(input.getPath())
					.overrideOutputFiles(true)
					.addOutput(output.getPath())
					.setFormat("mp4")
					.setVideoCodec("libx264")
					.setVideoFilter(vfilter.toString())
					.done();

				// create ffmpeg executor
				FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

				// Run a one-pass encode
				FFmpegJob job = executor.createJob(builder, new ProgressListener() {

					// Using the FFmpegProbeResult determine the duration of the input
					final double duration_ns = probeResult.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

					@Override
					public void progress(Progress progress) {
						byte percentage = (byte) (progress.out_time_ns * 100 / duration_ns);

						// Set task progress
						if (execute != null && execute.getProgress() < percentage) {
							execute.setProgress(percentage);

							// Print out interesting information about the progress
							System.out.println(String.format(
								"[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
								percentage * 100,
								progress.status,
								progress.frame,
								FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
								progress.fps.doubleValue(),
								progress.speed
							));
						}
					}
				});
				
				// Run job
				job.run();
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Error occur when create video file!");
			}

			//
			System.out.println("Done! Set output to task");

			// Set time consuming
			task.setTimeConsume(System.currentTimeMillis()-execute.getStartMilis());

			// Set the output file
			task.setOutputFile(output.getName());
		} catch(Exception e) {
			e.printStackTrace();
			task.setError(e.toString());
		}

		// Save task
		taskService.saveResult(execute);
	}

	@Async("threadPoolExecutor")
	public void doCreateSubVideoFromMusic(String track, String artist, String inputBackName, String inputMusicFileName, String inputSubName, Config config, TaskExecute execute) {
		// Config
		config = fullConfig(config);

		// Input and output file
		File input, back, sub, tmpDir, output;

		// The task
		Task task = execute.getTask();

		// type
		task.setType(Task.TASK_FROM_VIDEO_TYPE);

		try {
			// Check in out folder
			checkInOutFolder();

			// The music
			Music music = null;

			// if music file specific
			if (inputMusicFileName != null) {
				input = new File(inFolder+inputMusicFileName);

				// task input
				task.setInputFile(input.getName());
			} else {
				music = musicService.getMusic(track, artist);

				// check music
				if (music == null)
					throw new Exception("Music not found!");

				// Check music file
				if (music.getFile() == null)
					throw new Exception("Music stream (mp3) not found or not provide!");

				// Set task music
				task.setMusic(music);

				// Input file
				input = new File(musicFolder+music.getFile());

				// 
				System.out.println("Music: "+input.getPath());
			}

			// Tmp dir
			tmpDir = FileUtil.matchDirName(inFolder, input.getName().substring(0, input.getName().length()-4)+"_tmp");

			// sub file
			sub = new File(tmpDir.getPath().replace("_tmp", "")+".srt");

			// If sub file specific
			SongLyric songLyric = null;

			if (inputSubName != null) {
				songLyric = LyricConverter.readExcel(FileUtil.getFileContent(new File(inFolder+inputSubName)), dictionary);
			} else {
				if (music == null) {
					music = musicService.getMusic(track, artist);

					// check music
					if (music == null)
						throw new Exception("Music not found!");

					// Check music file
					if (music.getFile() == null)
						throw new Exception("Music stream (mp3) not found or not provide!");

					// Set task music
					task.setMusic(music);
				}
				songLyric = doGetSubtitle(music);
			}

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric, config.getLyricTransSize(), config.getLyricMarkColor());

			// Write subtitle to file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(sub);
				fos.write(lyric.getBytes("UTF-8"));
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Could not write subtitles file, progress fail!");
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch(Exception e) {
					}
				}
			}

			// Set task lyric
			task.setSubFile(sub.getName());

			if (inputBackName != null) {
				back = new File(inFolder + inputBackName);

				// Set task back file
				task.setBackFile(back.getName());
			} else
				back = new File(workingFolder + "image" + File.separator + "back" + ThreadLocalRandom.current().nextInt(5) + ".jpg");

			// back mp4 ?
			boolean mp4 = !servletContext.getMimeType(back.getName()).startsWith("image");

			// output file
			output = new File(outFolder+tmpDir.getName().replace("_tmp", "")+".mp4");

			try {
				//
				System.out.println("Do FFprobe");

				// Do ffprobe for music
				FFmpegProbeResult probeResult  = ffprobe.probe(input.getPath());
				FFmpegStream stream = probeResult.getStreams().get(0);

				// Do ffprobe for back
				FFmpegProbeResult probeResult2 = ffprobe.probe(back.getPath());
				FFmpegStream stream2 = probeResult2.getStreams().get(0);

				//
				System.out.println("Do FFmpeg");

				int width  = (config.getW() > 0 ? config.getW() : stream2.width)/2*2;
				int height = (config.getH() > 0 ? config.getH() : stream2.height)/2*2;

				// Do ffmpeg
				FFmpegBuilder builder = ffmpeg.builder()
						.addInput(back.getPath())
						.addInput(input.getPath());

				// Make filter
				StringBuilder vfilter = new StringBuilder("[0:v]scale=").append(width).append(":").append(height);

				vfilter
					.append("[in];[in]")
					.append(addSubtitle(sub, config.getLyricColor(), config.getLyricSize(), config.getLyricOpacity()));

				int x = (int) (config.getWordBoxX() < 0 ? width*(-config.getWordBoxX()) : config.getWordBoxX());
				int y = (int) (config.getWordBoxY() < 0 ? height*(-config.getWordBoxY()) : config.getWordBoxY());
				float opacity = config.getWordBoxOpacity();

				for (Lyric l : songLyric.getSong()) {
					if (l.getMark() != null)
						vfilter.append(drawWordInfo(l.getMark(), l.getFromTimestamp(), l.getToTimestamp(), opacity, x, y, width, height, config.getWordBoxPrimaryColor(), config.getWordBoxSecondaryColor(), tmpDir));
				}

				builder.setComplexFilter(vfilter.toString());

				if (mp4)
					builder.addExtraArgs("-stream_loop", "-1");
				else
					builder.addExtraArgs("-loop", "1");

				int frames = (int)Math.floor(stream.duration*30);

				builder
					.overrideOutputFiles(true)
					.addOutput(output.getPath())
					.setFormat("mp4")
					.setAudioCodec("aac")
					.setAudioBitRate(384_000)
					.setAudioSampleRate(48_000)
					.setVideoCodec("libx264")
					.setVideoBitRate(2500_000)
					.setVideoFrameRate(30)
					.addExtraArgs("-map", "0:v")
					.addExtraArgs("-map", "1:a")
					.addExtraArgs("-map_metadata", "1")
					.addExtraArgs("-frames:v", String.valueOf(frames))
					.done();

				// create ffmpeg executor
				FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

				// Run a one-pass encode
				FFmpegJob job = executor.createJob(builder, new ProgressListener() {

					// Using the FFmpegProbeResult determine the duration of the input
					final double duration_ns = probeResult.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

					@Override
					public void progress(Progress progress) {
						byte percentage = (byte) (progress.out_time_ns * 100 / duration_ns);

						// Set task progress
						if (execute != null && execute.getProgress() < percentage) {
							execute.setProgress(percentage);

							// Print out interesting information about the progress
							System.out.println(String.format(
								"[%d%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
								percentage,
								progress.status,
								progress.frame,
								FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
								progress.fps.doubleValue(),
								progress.speed
							));
						}
					}
				});
				
				// Run job
				job.run();
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Error occur when create video file!");
			}

			//
			System.out.println("Done! Set output to task");

			// Set time consuming
			task.setTimeConsume(System.currentTimeMillis()-execute.getStartMilis());

			// Set the output file
			task.setOutputFile(output.getName());
		} catch(Exception e) {
			e.printStackTrace();
			task.setError(e.toString());
		}

		// Save task
		taskService.saveResult(execute);
	}

	@Autowired
	private Translate translate;

	private SongLyric doGetSubtitle(Music music) throws Exception {
		// Get the lyric, format lrc
		SongLyric songLyric;

		try {
			songLyric = LyricConverter.readLRC(FileUtil.getFileString(new File(musicFolder+music.getSub())));

			// 
			System.out.println("Translate Lyric");

			// Translate
			songLyric.translate(translate);

			// 
			System.out.println("Mark lyric");

			// Mark random
			songLyric.markRandomText(dictionary, textTime);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception("An error occur when get the lyric!");
		}

		return songLyric;
	}

	public SongLyric doGetSubtitle(String track, String artist) throws Exception {
		// Search item
		Music music = musicService.getMusic(track, artist);

		// Music not found
		if (music == null)
			throw new Exception("Music not found!");

		// Sub not found
		if (music.getSub() == null)
			throw new Exception("Sub not found!");

		return doGetSubtitle(music);
	}

}
