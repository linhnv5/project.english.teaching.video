package topica.linhnv5.video.teaching.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

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
import topica.linhnv5.video.teaching.dictionary.Dictionary;
import topica.linhnv5.video.teaching.dictionary.model.WordInfo;
import topica.linhnv5.video.teaching.lyric.Lyric;
import topica.linhnv5.video.teaching.lyric.LyricConverter;
import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.SubVideoTaskResult;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.service.VideoSubException;
import topica.linhnv5.video.teaching.zing.ZingMp3;
import topica.linhnv5.video.teaching.zing.model.SearchItem;
import topica.linhnv5.video.teaching.zing.model.StreamResult;

@Component
public class VideoSubExecute {

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
	private Dictionary dictionary;

	private String drawBox(double from, double to, int x, int y, int w, int h, String color, float opacity) {
		return new StringBuilder(",drawbox=").append(x).append(":").append(y).append(":").append(w).append(":").append(h)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.append(":color=").append(color).append("@").append(opacity).append(":t=fill")
				.toString();
	}

	private String drawText(String text, double from, double to, int x, int y, String color, long size) {
		return new StringBuilder(",drawtext=text='").append(text.replaceAll("\'", "\'\'").replaceAll("\"", "\"\"")).append("'")
				.append(":x=").append(x).append(":y=").append(y)
				.append(":enable='between(t,").append(from).append(",").append(to).append(")'")
				.append(":fontfile='").append(fontFile).append("'")
				.append(":fontcolor=").append(color)
				.append(":fontsize=").append(size)
				.toString();
	}

	private String drawWordInfo(WordInfo info, double from, double to, int x, int y) {
		String wordColor = "yellow", boxColor = "black"; float boxCapacity = 0.75F;
		int w = info.getWordWithType().length()*15, h = 0;

		StringBuilder buff = new StringBuilder(drawText(info.getWordWithType(), from, to, x+5, y+5, wordColor, 25)); h += 30;

		if (info.getPronoun() != null && !info.getPronoun().equals("")) {
			buff.append(drawText(info.getPronoun(), from, to, x+5, y+5+h, wordColor, 20));
			h += 20;
			if (info.getPronoun().length() * 10 > w)
				w = info.getPronoun().length()*10;
		}

		if (info.getTrans() != null && !info.getTrans().equals("")) {
			buff.append(drawText(info.getTrans(),   from, to, x+5, y+5+h, wordColor, 20));
			h += 20;
			if (info.getTrans().length() * 10 > w)
				w = info.getTrans().length()*10;
		}

		return new StringBuilder(drawBox(from, to, x, y, w+10, h+10, boxColor, boxCapacity))
				.append(buff.toString())
				.toString();
	}

	@Async("threadPoolExecutor")
	public Future<SubVideoTaskResult> doAddSubToVideo(String inputFileName, String track, String artist, Task<SubVideoTaskResult> task) {
		SubVideoTaskResult result = new SubVideoTaskResult();

		// Input and output file
		File input  = new File(inFolder  + inputFileName);
		File output = new File(outFolder + inputFileName);

		try {
			//
			System.out.println("Search MP3");

			// Search item
			SearchItem item = zingMp3.getMatchingTrack(track, artist);

			// If not found or not have lyric
			if (item == null || item.getLyric() == "")
				throw new Exception("Music not found");

			// Print the lyric url
			System.out.println("Lyric: "+item.getLyric());

			// Check lyric
			if (item.getLyric() == null)
				throw new Exception("Could not find the lyric!");

			// Get the lyric, format lrc
			SongLyric songLyric;

			try {
				songLyric = LyricConverter.readLRC(ZingMp3.sendRequest(item.getLyric()));
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("An error occur when get the lyric!");
			}

			// Mark some text
			List<Lyric> listOfLyric = songLyric.getSong();

			int nText = 0;

			for (Lyric l : listOfLyric) {
				if (r.nextInt(100) < 40) {
					if (l.markSomeText(dictionary) != null)
						nText++;
				}
			}

			System.out.println("NText: "+nText);

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric);

			// Write subtitle to file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File(input.getPath()+".srt"));
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
										.append("[inscale]subtitles=\'input/"+input.getName()+".srt\'");

				for (Lyric l : listOfLyric) {
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

	public static Random r = new Random();

	@Async("threadPoolExecutor")
	public Future<SubVideoTaskResult> doCreateSubVideoFromMusic(String track, String artist, Task<SubVideoTaskResult> task) throws VideoSubException {
		SubVideoTaskResult result = new SubVideoTaskResult();

		String inputFileName = track.replaceAll(" ", "_")+"("+artist.replaceAll(" ", "_")+").mp4";

		// Input and output file
		File input  = new File(inFolder  + inputFileName);
		File output = new File(outFolder + inputFileName);

		// Back image
		File back   = new File(inFolder  + "image" + File.separator + "back" + r.nextInt(5) + ".jpg");

		try {
			//
			System.out.println("Search MP3");

			// Search item
			SearchItem item = zingMp3.getMatchingTrack(track, artist);

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
			} catch(Exception e) {
				e.printStackTrace();
				throw new Exception("An error occur when get the lyric!");
			}

			// Mark some text
			List<Lyric> listOfLyric = songLyric.getSong();

			int nText = 0;

			for (Lyric l : listOfLyric) {
				if (r.nextInt(100) < 40) {
					if (l.markSomeText(dictionary) != null)
						nText++;
				}
			}

			System.out.println("NText: "+nText);

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric);

			// Write subtitle to file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File(input.getPath()+".srt"));
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
				StreamResult streamResult = zingMp3.getStream(id);

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

				// Do ffprobe
				FFmpegProbeResult probeResult  = ffprobe.probe(input.getPath());

				FFmpegProbeResult probeResult2 = ffprobe.probe(back.getPath());
				FFmpegStream stream = probeResult2.getStreams().get(0);

				//
				System.out.println("Do FFmpeg");

				int width  = 640;
				int height = stream.height*640/stream.width;

				// Make filter
				StringBuilder vfilter = new StringBuilder("[0:v]scale=640:-2[in];")
											.append("[in]subtitles='input/"+input.getName()+".srt'")
											;

				for (Lyric l : listOfLyric) {
					if (l.getMark() != null)
						vfilter.append(drawWordInfo(l.getMark(), l.getFromTimestamp(), l.getToTimestamp(), width/10, height/2-40));
				}

				vfilter.append("[bg];")
						.append("movie=input/image/topica.png,scale=125:-1[watermark];")
						.append("[bg][watermark]overlay=10:10")
						;

				// Do ffmpeg
				FFmpegBuilder builder = ffmpeg.builder()
					.addInput(back.getPath())
					.addInput(input.getPath())
					.addExtraArgs("-loop", "1")
					.setComplexFilter(vfilter.toString())
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
					.addExtraArgs("-shortest")
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
			result.setOutput(new File(outFolder+id+".mp4"));
		} catch(Exception e) {
			task.setStatus(Task.Status.FINISHED);
			result.setException(e);
		}

		return new AsyncResult<SubVideoTaskResult>(result);
	}

}
