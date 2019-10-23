package topica.linhnv5.video.teaching.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
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
import topica.linhnv5.video.teaching.model.SubVideoTaskResult;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.WordInfo;
import topica.linhnv5.video.teaching.service.DictionaryService;
import topica.linhnv5.video.teaching.util.FileUtil;
import topica.linhnv5.video.teaching.zing.ZingMp3;
import topica.linhnv5.video.teaching.zing.model.SearchItem;
import topica.linhnv5.video.teaching.zing.model.StreamResult;

@Component
public class VideoSubExecute {

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private ZingMp3 zingMp3;

	@Value("${video.teaching.infolder}")
	private String inFolder;

	@Value("${video.teaching.outfolder}")
	private String outFolder;

	@Value("${video.teaching.fontfile}")
	private String fontFile;

	@Autowired
	private FFprobe ffprobe;

	@Autowired
	private FFmpeg ffmpeg;

	@Autowired
	private DictionaryService dictionary;

	@Value("${video.teaching.text.time}")
	private int textTime;

	/**
	 * Draw a box to video
	 * @param from
	 * @param to
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param color
	 * @param opacity
	 * @param t
	 * @return
	 */
	private String drawBox(double from, double to, int x, int y, int w, int h, String color, float opacity, int t) {
		return new StringBuilder(",drawbox=").append(x).append(":").append(y).append(":").append(w).append(":").append(h)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.append(":color=").append(color).append("@").append(opacity)
				.append(":t=").append(t < 0 ? "fill" : t)
				.toString();
	}

	/**
	 * Draw a text to video
	 * @param text
	 * @param from
	 * @param to
	 * @param x
	 * @param y
	 * @param color
	 * @param size
	 * @return
	 */
	private String drawText(String text, double from, double to, int x, int y, String color, long size) {
		StringBuilder buff = new StringBuilder(",drawtext=text='").append(text.replaceAll("\'", "\'\'").replaceAll("\"", "\"\"")).append("'")
				.append(":x=").append(x).append(":y=").append(y)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.append(":fontfile='").append(fontFile).append("'")
				.append(":fontcolor=").append(color)
				.append(":fontsize=").append(size);
		return buff.toString();
	}

	/**
	 * Draw a box of word, word type, word trans to video
	 * @param info
	 * @param from
	 * @param to
	 * @param x
	 * @param y
	 * @return
	 */
	private String drawWordInfo(WordInfo info, double from, double to, int x, int y) {
		// Color
		String wordColor = "yellow", boxColor = "black";

		// Capacity and maxw
		float boxCapacity = 0.5F; int maxw = 20;

		// text font size
		int textSize = 15, textW = 10;

		// x, y draw Text
		int xt = 20, yt = 20;

		// W, H of box
		int w = info.getWord().length()*15, h = 0;

		if (to - from < textTime)
			to = from + textTime;

		StringBuilder buff = new StringBuilder(drawText(info.getWord(), from, to, x+xt, y+yt, wordColor, textSize*2)); h += textSize + 5;

		if (info.getType() != null && !info.getType().equals("")) {
			buff.append(drawBox(from, to, x+xt, y+yt+h+5, info.getType().length() * textW, textSize, wordColor, 1.0F, 2));
			buff.append(drawText(info.getType(), from, to, x+xt+5, y+yt+h+7, wordColor, textSize));
			h += textSize + 10;
			if (info.getType().length()*textW > w)
				w = info.getType().length()*textW;
		}

		if (info.getPronoun() != null && !info.getPronoun().equals("")) {
			buff.append(drawText(info.getPronoun(), from, to, x+xt, y+yt+h, wordColor, textSize));
			h += textSize;
			if (info.getPronoun().length()*textW > w)
				w = info.getPronoun().length()*textW;
		}

		if (info.getTrans() != null && !info.getTrans().equals("")) {
			String[] trans = info.getTrans(maxw);

			for (String tran : trans) {
				buff.append(drawText(tran, from, to, x+xt, y+yt+h, wordColor, textSize));
				h += textSize;
				if (info.getTrans().length()*textW > w)
					w = info.getTrans().length()*textW;
			}
		}

		return new StringBuilder(drawBox(from, to, x, y, w+xt*2, h+yt*2, boxColor, boxCapacity, -1))
				.append(buff.toString())
				.toString();
	}

	@Async("threadPoolExecutor")
	public Future<SubVideoTaskResult> doAddSubToVideo(String track, String artist, String inputFileName, String inputSubName, Task<SubVideoTaskResult> task) {
		SubVideoTaskResult result = new SubVideoTaskResult();

		// Input and output file
		File input  = new File(inFolder  + inputFileName);
		File output = new File(outFolder + inputFileName);

		File sub    = null;

		try {
			// Get the lyric
			SongLyric songLyric = null;

			// If sub file specific
			if (inputSubName != null) {
				sub = new File(inFolder + inputSubName);

				songLyric = LyricConverter.readCSV(Files.lines(Paths.get(sub.getPath()), StandardCharsets.UTF_8).collect(Collectors.joining("\n")));
			} else {
				//
				System.out.println("Search MP3");

				// Search item
				SearchItem item = zingMp3.getMatchingTrack(track, artist);

				// Get the lyric
				songLyric = doGetSubtitle(item);
			}

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric);

			// Write subtitle to file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(sub = new File(input.getPath()+".srt"));
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

			try {
				//
				System.out.println("Do FFprobe");

				// Do ffprobe
				FFmpegProbeResult probeResult = ffprobe.probe(input.getPath());

				FFmpegStream stream = probeResult.getStreams().get(0);

				//
				System.out.println("Do FFmpeg");

				int width  = 640;
				int height = stream.height*640/stream.width;

				// Make filter
				StringBuilder vfilter = new StringBuilder("scale=640:-1[inscale];")
										.append("[inscale]subtitles=\'input/"+sub.getName()+"\'");

				for (Lyric l : songLyric.getSong()) {
					if (l.getMark() != null)
						vfilter.append(drawWordInfo(l.getMark(), l.getFromTimestamp(), l.getToTimestamp(), width/10, height/2-40));
				}

				vfilter.append("[in];")
						.append("movie=input/image/topica.png,scale=125:-1[watermark];")
						.append("[in][watermark]overlay=10:10");


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
						double percentage = progress.out_time_ns / duration_ns;

						// Set task progress
						task.setProgress((byte)(percentage * 100));

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
				});
				
				// Run job
				job.run();
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Error occur when create video file!");
			}

			//
			System.out.println("Done! Set output to task");

			// Set full progress
			task.setStatus(Task.Status.FINISHED);
			task.setProgress((byte)100);

			// Set the output file
			result.setOutput(output);
		} catch(Exception e) {
			e.printStackTrace();
			task.setStatus(Task.Status.FINISHED);
			result.setException(e);
		}

		return new AsyncResult<SubVideoTaskResult>(result);
	}

	@Async("threadPoolExecutor")
	public Future<SubVideoTaskResult> doCreateSubVideoFromMusic(String track, String artist, String inputBackName, String inputSubName, Task<SubVideoTaskResult> task) {
		SubVideoTaskResult result = new SubVideoTaskResult();

		String inputFileName = track.replaceAll(" ", "_")+"("+artist.replaceAll(" ", "_")+")";

		// Input and output file
		File input  = FileUtil.matchFileName(inFolder, inputFileName + ".mp3");
		File output = new File(outFolder + input.getName().substring(0, input.getName().length()-4)+".mp4");

		// Sub file
		File sub    = null;

		// Back image
		File back   = new File(inFolder  + (inputBackName != null ? inputBackName : "image" + File.separator + "back" + ThreadLocalRandom.current().nextInt(5) + ".jpg"));

		boolean mp4 = !servletContext.getMimeType(back.getName()).startsWith("image");

		try {
			//
			System.out.println("Search MP3");

			// Search item
			SearchItem item = zingMp3.getMatchingTrack(track, artist);

			// Get the lyric
			SongLyric songLyric = null;

			// If sub file specific
			if (inputSubName != null) {
				sub = new File(inFolder + inputSubName);

				songLyric = LyricConverter.readCSV(Files.lines(Paths.get(sub.getPath()), StandardCharsets.UTF_8).collect(Collectors.joining("\n")));
			} else {
				// Get the lyric
				songLyric = doGetSubtitle(item);
			}

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric);

			// Write subtitle to file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(sub = new File(input.getPath()+".srt"));
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

			// get stream mp3
			String link;

			try {
				// Get link stream
				StreamResult streamResult = zingMp3.getStream(item.getId());

				String link128 = streamResult.getData().getData().getLink128();
				String link320 = streamResult.getData().getData().getLink320();

				// Link address
				link = link320.equals("") ? link128 : link320;
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("An error occur when get the mp3 stream info!");
			}

			// Print link
			System.out.println("Link: "+link);

			// Get file
			fos = null;

			try {
				// Get stream data
				byte[] mp3Data = ZingMp3.sendRequestBinary("http:"+link);

				// Write mp3 to file
				fos = new FileOutputStream(input);
				fos.write(mp3Data);
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("Could not get mp3 file!");
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch(Exception e) {
					}
				}
			}

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

				int width  = 640;
				int height = stream2.height*640/stream2.width;

				// Make filter
				StringBuilder vfilter = new StringBuilder("[0:v]scale=640:-2");

				if (mp4)
					vfilter.append(",loop=-1:start=0:size=").append(stream2.nb_frames);

				vfilter.append("[in];").append("[in]subtitles='input/"+sub.getName()+"'");

				for (Lyric l : songLyric.getSong()) {
					if (l.getMark() != null)
						vfilter.append(drawWordInfo(l.getMark(), l.getFromTimestamp(), l.getToTimestamp(), width/10, height/2-40));
				}

				vfilter
					.append("[bg];")
					.append("movie=input/image/topica.png,scale=125:-1[watermark];")
					.append("[bg][watermark]overlay=10:10")
					;

				// Do ffmpeg
				FFmpegBuilder builder = ffmpeg.builder()
					.addInput(back.getPath())
					.addInput(input.getPath())
					.setComplexFilter(vfilter.toString())
					;

				if (!mp4)
					builder
					.addExtraArgs("-loop", "1")
					;
				
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
						if (task.getProgress() < percentage) {
							task.setProgress(percentage);

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

			// Set full progress
			task.setStatus(Task.Status.FINISHED);
			task.setProgress((byte)100);

			// Set the output file
			result.setOutput(output);
		} catch(Exception e) {
			task.setStatus(Task.Status.FINISHED);
			result.setException(e);
		}

		return new AsyncResult<SubVideoTaskResult>(result);
	}

	public SearchItem doGetMatchingTrack(String track, String artist) throws Exception {
		return zingMp3.getMatchingTrack(track, artist);
	}

	@Autowired
	private Translate translate;

	public SongLyric doGetSubtitle(SearchItem item) throws Exception {
		// If not found or not have lyric
		if (item == null || item.getLyric() == "")
			throw new Exception("Music not found");

		// The ID
		String id = item.getId();

		// Print the lyric url
		System.out.println("Id: "+id+" lyric: "+item.getLyric());

		// Check lyric
		if (item.getLyric() == null)
			throw new Exception("Could not find the lyric!");

		// Get the lyric, format lrc
		SongLyric songLyric;

		try {
			songLyric = LyricConverter.readLRC(ZingMp3.sendRequest(item.getLyric()));

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

}
