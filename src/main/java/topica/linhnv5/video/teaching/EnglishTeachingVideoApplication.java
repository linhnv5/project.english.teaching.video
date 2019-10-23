package topica.linhnv5.video.teaching;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.RunProcessFunction;

@SpringBootApplication
public class EnglishTeachingVideoApplication {

	@Value("${ffmpeg.path}")
	private String ffmpegPath;

	@Value("${ffprobe.path}")
	private String ffprobePath;

	@Value("${video.teaching.workingfolder}")
	private String workingDir;

	@Value("${google.translate.apikey}")
	private String gooleAPIKey;

	@Bean
	public RunProcessFunction getRunProcessFunction() {
		RunProcessFunction function = new RunProcessFunction();
		function.setWorkingDirectory(workingDir);
		return function;
	}

	@Autowired
	private RunProcessFunction runProcessFunction;

	@Bean
	public FFmpeg getFFmpeg() throws IOException {
		return new FFmpeg(ffmpegPath, runProcessFunction);
	}

	@Bean
	public FFprobe getFFprobe() throws IOException {
		return new FFprobe(ffprobePath, runProcessFunction);
	}

	@SuppressWarnings("deprecation")
	@Bean
	public Translate getTranslate() {
		return TranslateOptions.newBuilder().setApiKey(gooleAPIKey).build().getService();
	}

	public static void main(String[] args) {
		SpringApplication.run(EnglishTeachingVideoApplication.class, args);
	}

}
