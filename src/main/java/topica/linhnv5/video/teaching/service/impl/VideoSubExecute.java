package topica.linhnv5.video.teaching.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
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
import topica.linhnv5.video.teaching.lyric.Lyric;
import topica.linhnv5.video.teaching.lyric.LyricConverter;
import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.SubVideoTaskResult;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.service.VideoSubException;
import topica.linhnv5.video.teaching.zing.model.SearchItem;
import topica.linhnv5.video.teaching.zing.model.StreamResult;
import topica.linhnv5.video.teaching.zing.service.ZingMp3;

@Component
public class VideoSubExecute {

	@Autowired
	private ZingMp3 zingMp3;

	@Value("${video.teaching.infolder}")
	private String inFolder;

	@Value("${video.teaching.outfolder}")
	private String outFolder;

	@Autowired
	private FFprobe ffprobe;

	@Autowired
	private FFmpeg ffmpeg;

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

			// Get the lyric, format lrc
			SongLyric songLyric = LyricConverter.readLRC(ZingMp3.sendRequest(item.getLyric()));

			// Mark some text
			List<Lyric> listOfLyric = songLyric.getSong();

			for (Lyric l : listOfLyric)
				l.markSomeText();

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric);

			// Write subtitle to file
			FileOutputStream fos = new FileOutputStream(new File(input.getPath()+".srt"));
			fos.write(lyric.getBytes("UTF-8"));
			fos.close();

			//
			System.out.println("Do FFprobe");

			// Do ffprobe
			FFmpegProbeResult probeResult = ffprobe.probe(input.getPath());

			FFmpegStream stream = probeResult.getStreams().get(0);

			//
			System.out.println("Do FFmpeg");

			int width  = stream.width;
			int height = stream.height;

			// Make filter
			StringBuilder vfilter = new StringBuilder("subtitles='input/"+input.getName()+".srt'");

			for (Lyric l : listOfLyric) {
				if (l.getMark() != null)
					vfilter
						.append(",drawtext=text='").append(l.getMark()).append("'")
						.append(":enable='between(t,")
							.append(l.getFromTimestamp())
							.append(",")
							.append(l.getToTimestamp())
							.append(")'")
						.append(":box=1").append(":boxcolor=black")
						.append(":fontcolor=yellow")
						.append(":fontsize=40")
						.append(":x=").append(width/10)
						.append(":y=").append(height/2)
						;
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
	public Future<SubVideoTaskResult> doCreateSubVideoFromMusic(String track, String artist, Task<SubVideoTaskResult> task) throws VideoSubException {
		SubVideoTaskResult result = new SubVideoTaskResult();

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
			System.out.println("Lyric: "+item.getLyric());

			// Get the lyric, format lrc
			SongLyric songLyric = LyricConverter.readLRC(ZingMp3.sendRequest(item.getLyric()));

			// Mark some text
			List<Lyric> listOfLyric = songLyric.getSong();

			for (Lyric l : listOfLyric)
				l.markSomeText();

			// Convert it to srt
			String lyric = LyricConverter.writeSRT(songLyric);

			// Write subtitle to file
			FileOutputStream fos = new FileOutputStream(new File(inFolder+id+".srt"));
			fos.write(lyric.getBytes("UTF-8"));
			fos.close();

			// get stream mp3
			StreamResult streamResult = zingMp3.getStream(id);

			// Link address
			String link = streamResult.getData().getData().getLink320().equals("") ? streamResult.getData().getData().getLink128() : streamResult.getData().getData().getLink320();

			System.out.println("Link: "+link);

			// Get file
			byte[] mp3Data = ZingMp3.sendRequestBinary("http:"+link);

			// Write mp3 to file
			fos = new FileOutputStream(new File(inFolder+id+".mp3"));
			fos.write(mp3Data);
			fos.close();

			//
			System.out.println("Do FFprobe");

			// Do ffprobe
			FFmpegProbeResult probeResult = ffprobe.probe(inFolder+id+".mp3");

			int width  = 852;
			int height = 480;

			//
			System.out.println("Do FFmpeg");

			// Make filter
			StringBuilder vfilter = new StringBuilder("subtitles='input/"+id+".srt'");

			for (Lyric l : listOfLyric) {
				if (l.getMark() != null)
					vfilter
						.append(",drawtext=text='").append(l.getMark()).append("'")
						.append(":enable='between(t,")
							.append(l.getFromTimestamp())
							.append(",")
							.append(l.getToTimestamp())
							.append(")'")
						.append(":box=1").append(":boxcolor=black")
						.append(":fontcolor=yellow")
						.append(":fontsize=40")
						.append(":x=").append(width/10)
						.append(":y=").append(height/2)
						;
			}

//			ffmpeg.exe -loop 1 -i .\background.jpg -i .\ZW68I7AW.mp3 -y -r 30 -b 2500K -acodec ac3 -ab 384k -vcodec mpeg4 -vframes 6210 result.mp4

			// Do ffmpeg
			FFmpegBuilder builder = ffmpeg.builder()
				.addInput("D:\\Study\\Topica\\Project\\Video\\input\\background.jpg")
				.addExtraArgs("-loop", "1")
				.addInput(inFolder+id+".mp3")
				.overrideOutputFiles(true)
				.addOutput(outFolder+id+".mp4")
				.setAudioCodec("aac")
				.setAudioBitRate(384_000)
				.setAudioSampleRate(48_000)
				.setVideoCodec("mpeg4")
				.setVideoBitRate(2500_000)
				.setVideoFrameRate(30)
				.setFrames((int)probeResult.getFormat().duration*30)
				.addExtraArgs("-filter_complex", vfilter.toString())
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

			//
			System.out.println("Done! Set output to task");

			// Set full progress
			task.setStatus(Task.Status.FINISHED);
			task.setProgress((byte)100);

			// Set the output file
			result.setOutput(new File(outFolder+id+".mp4"));
		} catch(Exception e) {
			e.printStackTrace();
			task.setStatus(Task.Status.FINISHED);
			result.setException(e);
		}

		return new AsyncResult<SubVideoTaskResult>(result);
	}

}
