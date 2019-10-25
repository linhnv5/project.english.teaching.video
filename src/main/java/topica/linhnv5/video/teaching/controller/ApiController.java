package topica.linhnv5.video.teaching.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.HttpHeaders;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import topica.linhnv5.video.teaching.model.TaskResponse;
import topica.linhnv5.video.teaching.lyric.LyricConverter;
import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.VideoTaskResult;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.TaskInfoResult;
import topica.linhnv5.video.teaching.service.TaskService;
import topica.linhnv5.video.teaching.service.VideoSubService;
import topica.linhnv5.video.teaching.util.FileUtil;

/**
 * Controller for api main, input a video, background image, Name of Track, Name of Artist <br/>
 * Return a task id if success or return error
 * @author ljnk975
 */
@Controller
@RequestMapping(path = "/api")
@Api("CreateVideoAPI")
public class ApiController {

	@Autowired
	private TaskService taskService;

	@Autowired
	private VideoSubService videoSubService;

	@Autowired
	private FFprobe ffprobe;

	@Value("${video.teaching.infolder}")
	private String inFolder;

	@PostMapping(path = "/create.fromvideo")
	@ApiOperation(value = "Create subbing video task from video", response = TaskResponse.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Result SUCCESS status if successful, ERROR if some error occur")
	})
	public ResponseEntity<TaskResponse> addSubFromVideo(
			@ApiParam(value = "Track name(must be define if video metadata not have title)", required = false)
			@RequestParam(value = "title", required = false)
				String title,
			@ApiParam(value = "Artist name(must be define if video metadata not have artist)", required = false)
			@RequestParam(value = "artist", required = false)
				String artist,
			@ApiParam(value = "Video input", required = true)
			@RequestParam(value = "video", required = true)
				MultipartFile video,
			@ApiParam(value = "Subtitle file (csv)", required = false)		
			@RequestParam(value = "sub", required = false)
				MultipartFile sub) {
		// The response
		TaskResponse response = null;

		try {
			// Info track, artist
			String trackName, artistName;

			// Check request
			if (video == null)
				throw new Exception("Video is not specific");

			// Move input video to input folder
			File inFile = FileUtil.matchFileName(inFolder, video.getOriginalFilename());

			video.transferTo(inFile);

			// if sub exists then move it to input folder
			File inSub = null;
			if (sub != null)
				sub.transferTo(inSub = FileUtil.matchFileName(inFolder, sub.getOriginalFilename()));

			// Get info of video file
			FFmpegProbeResult probeResult = ffprobe.probe(inFile.getPath());

			FFmpegFormat format = probeResult.getFormat();
			System.out.format("%nFile: '%s' ; Format: '%s' ; Duration: %.3fs\n", 
				format.filename, 
				format.format_long_name,
				format.duration
			);

			FFmpegStream stream = probeResult.getStreams().get(0);
			System.out.format("%nCodec: '%s' ; Width: %dpx ; Height: %dpx\n",
				stream.codec_long_name,
				stream.width,
				stream.height
			);

			// Get the artist and track
			trackName = format.tags.get("title");
			artistName = format.tags.get("artist");

			// Check if artist or track name null then get from request
			if (trackName == null)
				trackName = title;

			if (artistName == null)
				artistName = artist;

			// Check if track and artist exists
			if (trackName == null || artistName == null)
				throw new Exception("Can't get information about music name and music artist!");

			// Print the track name and artist name
			System.out.println("Request sub video track: "+trackName+" artist: "+artistName);

			// Create and return a task
			Task<VideoTaskResult> task = videoSubService.addSubToVideo(trackName, artistName, inFile.getName(), inSub == null ? null : inSub.getName());

			// 
			System.out.println("   return task id="+task.getId());

			// Return task name
			response = new TaskResponse("SUCCESS", "", task.getId());
		} catch (Exception e) {
			System.out.println("   err: "+e.getMessage());
			response = new TaskResponse("ERROR", e.getMessage(), "");
		}

		return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
	}

	@PostMapping(path = "/create.frommusic")
	@ApiOperation(value = "Create subbing video task from background and music", response = TaskResponse.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Result SUCCESS status if successful, ERROR if some error occur")
	})
	public ResponseEntity<TaskResponse> addSubFromMusic(
			@ApiParam(value = "Track name(must be define if video metadata not have title)", required = true)
			@RequestParam(value = "title", required = true)
				String title,
			@ApiParam(value = "Artist name(must be define if video metadata not have artist)", required = true)
			@RequestParam(value = "artist", required = true)
				String artist,
			@ApiParam(value = "Background of output video(image or short video)", required = false)
			@RequestParam(value = "background", required = false)
				MultipartFile background,
			@ApiParam(value = "Music of output video", required = false)
			@RequestParam(value = "music", required = false)
				MultipartFile music,
			@ApiParam(value = "Subtitle file (csv)", required = false)
			@RequestParam(value = "sub", required = false)
				MultipartFile sub) {
		// The response
		TaskResponse response = null;

		try {
			// Check if track and artist exists
			if ((music == null || sub == null) && (title == null || artist == null))
				throw new Exception("Can't get information about music name and music artist!");

			// Check request
			File inFile = null;
			if (background != null)
				background.transferTo(inFile = FileUtil.matchFileName(inFolder, background.getOriginalFilename()));

			// if sub exists then move it to input folder
			File inSub = null;
			if (sub != null)
				sub.transferTo(inSub = FileUtil.matchFileName(inFolder, sub.getOriginalFilename()));

			// if music exists then move it to input folder
			File inMusic = null;
			if (music != null)
				music.transferTo(inMusic = FileUtil.matchFileName(inFolder, music.getOriginalFilename()));

			// Print the track name and artist name
			System.out.println("Request sub image track: "+title+" artist: "+artist);

			// Create and return a task
			Task<VideoTaskResult> task = videoSubService.createSubVideoFromMusic(title, artist, inFile == null ? null : inFile.getName(), inMusic == null ? null : inMusic.getName(), inSub == null ? null : inSub.getName());

			// 
			System.out.println("   return task id="+task.getId());

			// Return task name
			response = new TaskResponse("SUCCESS", "", task.getId());
		} catch (Exception e) {
			System.out.println("   err: "+e.getMessage());
			response = new TaskResponse("ERROR", e.getMessage(), "");
		}

		return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/subtitle")
	@ApiOperation(value = "Get subtitle csv file", response = String.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Successful get the subtitle"),
		@ApiResponse(code = 204, message = "Error occur while get the subtitle")
	})
	public ResponseEntity<?> getSubtitle(@RequestParam("title") String title, @RequestParam("artist") String artist) throws Exception {
		// Print the track name and artist name
		System.out.println("Request sub track: "+title+" artist: "+artist);

		// 
		SongLyric lyric = null;

		// Create and return a task
		try {
			lyric = videoSubService.getSubtitle(title, artist);
		} catch(Exception e) {
			System.out.println("   err: "+e.getMessage());
		}

		if (lyric == null)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		// Return value
		MediaType mediaType = getMediaTypeForFileName("a.csv");

		byte[] data = LyricConverter.writeCSV(lyric).getBytes("UTF-8");
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));

		return ResponseEntity.ok()
				// Content-Disposition
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+(title+"_"+artist+".csv").replaceAll(" ", "_"))
				// Content-Type
				.contentType(mediaType)
				// Contet-Length
				.contentLength(data.length) //
				.body(resource);
	}

	@GetMapping(path = "/task")
	@ApiOperation(value = "Get task infomation", response = TaskInfoResult.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Return Task infomation"),
		@ApiResponse(code = 404, message = "TaskID is not found")
	})
	public ResponseEntity<TaskInfoResult> getTaskInfo(@RequestParam("id") String id) throws InterruptedException, ExecutionException {
		Task<VideoTaskResult> task = taskService.getTaskById(id, VideoTaskResult.class);

		if (task == null)
			return new ResponseEntity<TaskInfoResult>(HttpStatus.NOT_FOUND);
		
		TaskInfoResult result = new TaskInfoResult();
		if (task.getStatus() == Task.Status.FINISHED) {
			VideoTaskResult resultTask = task.get();
			if (resultTask.getException() != null) {
				result.setStatus("ERROR");
				result.setError(resultTask.getException().getMessage());
			} else
				result.setStatus("SUCCESS");
			result.setTimeConsume(task.getEndMilis()-task.getStartMilis());
		} else {
			result.setStatus("RUNNING");
			result.setTimeConsume(System.currentTimeMillis()-task.getStartMilis());
		}
		result.setProgress(task.getProgress());
		
		return new ResponseEntity<TaskInfoResult>(result, HttpStatus.OK);
	}

	@Autowired
	private ServletContext servletContext;

	/**
	 * Helper function, return media type of input file name
	 * @param fileName the file name
	 * @return media type of file name
	 */
	private MediaType getMediaTypeForFileName(String fileName) {
		// application/pdf
		// application/xml
		// image/gif, ...
		String mineType = servletContext.getMimeType(fileName);
		try {
			return MediaType.parseMediaType(mineType);
		} catch (Exception e) {
		}
		return MediaType.APPLICATION_OCTET_STREAM;
	}

	@GetMapping(path = "/result")
	@ApiOperation(value = "Get result video of task")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Return result video"),
		@ApiResponse(code = 404, message = "TaskID is not found"),
		@ApiResponse(code = 204, message = "Task isn't finnished yet")
	})
	public ResponseEntity<?> getTaskResult(@RequestParam("id") String id) throws InterruptedException, ExecutionException, FileNotFoundException {
		Task<VideoTaskResult> task = taskService.getTaskById(id, VideoTaskResult.class);

		if (task == null)
			return new ResponseEntity<String>("Task not found!", HttpStatus.NOT_FOUND);

		if (task.getStatus() != Task.Status.FINISHED)
			return new ResponseEntity<String>("Task not finish!", HttpStatus.NO_CONTENT);

		// get result
		VideoTaskResult resultTask = task.get();

		// Return value
		MediaType mediaType = getMediaTypeForFileName(resultTask.getOutput().getName());
//		System.out.println("fileName: " + resultTask.getOutput().getName());
//		System.out.println("mediaType: " + mediaType);

		InputStreamResource resource = new InputStreamResource(new FileInputStream(resultTask.getOutput()));

		return ResponseEntity.ok()
				// Content-Disposition
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resultTask.getOutput().getName())
				// Content-Type
				.contentType(mediaType)
				// Contet-Length
				.contentLength(resultTask.getOutput().length()) //
				.body(resource);
	}
	
}
