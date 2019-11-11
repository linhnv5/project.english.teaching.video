package topica.linhnv5.video.teaching.service.impl;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import topica.linhnv5.video.teaching.model.Music;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.TaskExecute;
import topica.linhnv5.video.teaching.model.WordInfo;
import topica.linhnv5.video.teaching.service.DictionaryService;
import topica.linhnv5.video.teaching.service.MusicService;
import topica.linhnv5.video.teaching.service.TaskService;
import topica.linhnv5.video.teaching.util.FileUtil;

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

	/*
	private String drawBox(double from, double to, int x, int y, int w, int h, String color, float opacity, int t) {
		return new StringBuilder(",drawbox=").append(x).append(":").append(y).append(":").append(w).append(":").append(h)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.append(":color=").append(color).append("@").append(opacity)
				.append(":t=").append(t < 0 ? "fill" : t)
				.toString();
	}

	private String drawText(String text, double from, double to, int x, int y, String color, int size) {
		StringBuilder buff = new StringBuilder(",drawtext=text='").append(text.replaceAll("\'", "\'\'").replaceAll("\"", "\"\"")).append("'")
				.append(":x=").append(x).append(":y=").append(y)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.append(":fontfile='").append(fontFile).append("'")
				.append(":fontcolor=").append(color)
				.append(":fontsize=").append(size);
		return buff.toString();
	}
	*/

	private String addLogo(int width, int height) {
		return new StringBuilder()
				.append("[in];")
				.append("movie=image/logo.jpg,scale=").append(width/10).append(":-1[watermark];")
				.append("[in][watermark]overlay=10:10")
				.toString();
	}

	private String addSubtitle(File sub) {
		return new StringBuilder()
				.append("subtitles=\'input/"+sub.getName()+"\'")
				.append(":force_style='BorderStyle=4,BackColour=&H88000000,OutlineColour=&H88000000,Outline=1,Shadow=1,MarginV=20'")
				.toString();
	}

	private File drawWordInfoToImage(WordInfo info, float scale, File tmpDir) throws Exception {
		// Color
		Color wordColor = Color.YELLOW, dicColor = Color.WHITE,
				borderColor = Color.WHITE, boxColor = new Color(0, 0, 0, 0x88);

		// Capacity and maxw
		int maxw = (int) (200*scale), maxh = (int) (200*scale);

		// text font size
		float textSize = 15*scale;

		// x, y draw Text
		int xt = 5, yt = 5;

		// W, H of box
		int w = 0, h = 0;

		// Image
		BufferedImage img = new BufferedImage(maxw, maxh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();

		// fill transparent
		Composite oldComp = g.getComposite();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, maxw, maxh);
		g.setComposite(oldComp);

		// Font
	    Font dynamicFont32Pt = new Font(null, Font.BOLD, (int)(textSize*2));
	    Font dynamicFont16Pt = new Font(null, Font.PLAIN, (int)textSize);

	    // word dictionary
	    g.setFont(dynamicFont32Pt);
	    g.setColor(wordColor);
	    g.drawString(info.getWordDictionary(), xt, yt+g.getFontMetrics().getAscent());

	    w = Math.max(w, g.getFontMetrics().stringWidth(info.getWordDictionary()));
	    h += g.getFontMetrics().getHeight()+5;

	    // word type and api
	    g.setFont(dynamicFont16Pt);

		int tw = 0;
		int th = 0;

		// api
		g.setColor(dicColor);
		if (info.getAPI() != null && !info.getAPI().equals("")) {
			int tw2 = g.getFontMetrics().stringWidth(info.getAPI());
			int th2 = g.getFontMetrics().getHeight();

			g.setColor(borderColor);
			g.setStroke(new BasicStroke(2f));
			g.drawRoundRect(xt, yt+h, tw2+10, th2+10, 5, 5);

			g.setColor(dicColor);
			g.drawString(info.getAPI(), xt+5, yt+h+5+g.getFontMetrics().getAscent());

			tw = tw2+15;
			th = th2+15;
		}

		// word type
		if (info.getTypeShort() != null && !info.getTypeShort().equals("")) {
			int tw2 = g.getFontMetrics().stringWidth(info.getTypeShort());
			int th2 = g.getFontMetrics().getHeight();

			g.setColor(borderColor);
			g.setStroke(new BasicStroke(2f));
			g.drawOval(xt+tw, yt+h, tw2+10, th2+10);

			g.setColor(dicColor);
			g.drawString(info.getTypeShort(), xt+tw+5, yt+h+5+g.getFontMetrics().getAscent());

			tw += tw2+15;
			th = th2+15;
		}

	    w = Math.max(w, tw);
	    h += th;

	    // trans
		if (info.getTrans() != null && !info.getTrans().equals("")) {
			String[] trans = info.getTrans(maxw, g.getFontMetrics());

			for (String tran : trans) {
				g.drawString(tran, xt, yt+h+g.getFontMetrics().getAscent());

			    w = Math.max(w, g.getFontMetrics().stringWidth(tran));
			    h += g.getFontMetrics().getHeight()+2;
			}
		}

		// final w, h
		w = xt*2 + w;
		h = yt*2 + h;

		// Image Out
		BufferedImage img2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		g = (Graphics2D) img2.getGraphics();

		// draw background
		g.setColor(boxColor);
		g.fillRoundRect(0, 0, w, h, w/10, h/10);

		// draw image
		g.drawImage(img, 0, 0, null);

		// draw border
//		g.setColor(borderColor);
//		g.setStroke(new BasicStroke(4f));
//		g.drawRoundRect(0, 0, w+xt*2, h+xt*2, w/10, h/10);

		// Write image
		File f = FileUtil.matchFileName(tmpDir.getPath(), info.getWordDictionary()+".png");

		System.out.println("Write "+f.getPath());

		ImageIO.write(img2, "PNG", f);

		return f;
	}

	/**
	 * Draw a box of word, word type, word trans to video
	 * @param info
	 * @param from
	 * @param to
	 * @param x
	 * @param y
	 * @return
	 * @throws Exception 
	 */
	private String drawWordInfo(WordInfo info, double from, double to, int x, int y, float scale, File tmpDir) throws Exception {
		File wordImage = drawWordInfoToImage(info, scale, tmpDir);

		if (to - from < textTime)
			to = from + textTime;

		return new StringBuilder()
				.append("[in];")
				.append("movie=input/").append(tmpDir.getName()).append("/").append(wordImage.getName()).append("[word];")
				.append("[in][word]overlay=").append(x).append(":").append(y)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.toString();

		/*
		// Color
		String wordColor = "yellow", dicColor = "white", boxColor = "black";

		// Capacity and maxw
		float boxCapacity = 0.5F; int maxw = 20;

		// text font size
		int textSize = (int) (15*scale), textW = 10;

		// x, y draw Text
		int xt = 20, yt = 20;

		// W, H of box
		int w = info.getWordDictionary().length()*15, h = 0;

		StringBuilder buff = new StringBuilder(drawText(info.getWordDictionary(), from, to, x+xt, y+yt, wordColor, textSize*2)); h += textSize + 5;

		if (info.getType() != null && !info.getType().equals("")) {
			buff.append(drawBox(from, to, x+xt, y+yt+h+5, info.getType().length() * textW, textSize, dicColor, 1.0F, 2));
			buff.append(drawText(info.getType(), from, to, x+xt+5, y+yt+h+7, dicColor, textSize));
			h += textSize + 10;
			if (info.getType().length()*textW > w)
				w = info.getType().length()*textW;
		}

		if (info.getAPI() != null && !info.getAPI().equals("")) {
			buff.append(drawText(info.getAPI(), from, to, x+xt, y+yt+h, dicColor, textSize));
			h += textSize;
			if (info.getAPI().length()*textW > w)
				w = info.getAPI().length()*textW;
		}

		if (info.getTrans() != null && !info.getTrans().equals("")) {
			String[] trans = info.getTrans(maxw);

			for (String tran : trans) {
				buff.append(drawText(tran, from, to, x+xt, y+yt+h, dicColor, textSize));
				h += textSize;
				if (info.getTrans().length()*textW > w)
					w = info.getTrans().length()*textW;
			}
		}

		return new StringBuilder(drawBox(from, to, x, y, w+xt*2, h+yt*2, boxColor, boxCapacity, -1))
				.append(buff.toString())
				.toString();
		*/
	}

	private void checkInOutFolder() {
		FileUtil.checkAndMKDir(inFolder);
		FileUtil.checkAndMKDir(outFolder);
	}

	private String srtLyric(SongLyric songLyric) throws Exception {
		return LyricConverter.writeSRT(songLyric, "red");
	}

	@Async("threadPoolExecutor")
	public void doAddSubToVideo(String track, String artist, String inputFileName, String inputSubName, TaskExecute execute) {
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
			sub = new File(inFolder+inputFileName+".srt");

			// Get the lyric
			SongLyric songLyric = null;

			// If sub file specific
			if (inputSubName != null)
				songLyric = LyricConverter.readCSV(Files.lines(Paths.get(inFolder+inputSubName), StandardCharsets.UTF_8).collect(Collectors.joining("\n")), dictionary);
			else {
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
			String lyric = srtLyric(songLyric);

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

				int width  = stream.width*2/2;
				int height = stream.height*2/2;

				float scale = (float)width / 640;

				// Make filter
				StringBuilder vfilter = new StringBuilder("scale=").append(width).append(":").append(height).append("[in];[in]")
										.append(addSubtitle(sub));

				for (Lyric l : songLyric.getSong()) {
					if (l.getMark() != null)
						vfilter.append(drawWordInfo(l.getMark(), l.getFromTimestamp(), l.getToTimestamp(), width/10, height/2-40, scale, tmpDir));
				}

				vfilter.append(addLogo(width, height));

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
		taskService.saveTask(task);
	}

	@Async("threadPoolExecutor")
	public void doCreateSubVideoFromMusic(String track, String artist, String inputBackName, String inputMusicFileName, String inputSubName, TaskExecute execute) {
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
			tmpDir = FileUtil.matchDirName(inFolder, input.getName().substring(0, input.getName().length()-4));

			// sub file
			sub = new File(inFolder+input.getName()+".srt");

			// If sub file specific
			SongLyric songLyric = null;

			if (inputSubName != null) {
				songLyric = LyricConverter.readCSV(Files.lines(Paths.get(inFolder + inputSubName), StandardCharsets.UTF_8).collect(Collectors.joining("\n")), dictionary);
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
			String lyric = srtLyric(songLyric);

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
			output = new File(outFolder+tmpDir.getName()+".mp4");

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

				int width  = stream2.width/2*2;
				int height = stream2.height/2*2;

				float scale = (float)width / 640;

				// Do ffmpeg
				FFmpegBuilder builder = ffmpeg.builder()
						.addInput(back.getPath())
						.addInput(input.getPath());

				// Make filter
				StringBuilder vfilter = new StringBuilder("[0:v]scale=").append(width).append(":").append(height);

				vfilter
					.append("[in];[in]")
					.append(addSubtitle(sub));

				for (Lyric l : songLyric.getSong()) {
					if (l.getMark() != null)
						vfilter.append(drawWordInfo(l.getMark(), l.getFromTimestamp(), l.getToTimestamp(), width/10, height/2-40, scale, tmpDir));
				}

				vfilter.append(addLogo(width, height));

				builder.setComplexFilter(vfilter.toString());

				if (mp4)
					builder.addExtraArgs("-stream_loop", "-1");
				else
					builder.addExtraArgs("-loop", "1");

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
					.addExtraArgs("-frames:v", String.valueOf((int)Math.floor(stream.duration*30)))
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
		taskService.saveTask(task);
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
