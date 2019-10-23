package topica.linhnv5.video.teaching.controller;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.net.HttpHeaders;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import topica.linhnv5.video.teaching.model.TaskVideoRequest;
import topica.linhnv5.video.teaching.model.TaskResponse;
import topica.linhnv5.video.teaching.lyric.LyricConverter;
import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.SubVideoTaskResult;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.TaskImageRequest;
import topica.linhnv5.video.teaching.model.TaskInfoResult;
import topica.linhnv5.video.teaching.service.TaskService;
import topica.linhnv5.video.teaching.service.VideoSubService;

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

	/**
	 * Generate new file name
	 * @param path Input path
	 * @param name Input file name
	 * @return new File
	 */
	public static synchronized File matchFileName(String path, String name) {
		File f;
		if (!(f = new File(path+name)).exists())
			return f;

		int id = 1;
		while ((f = new File(path+name+"_"+id)).exists());

		return f;
	}

	@PostMapping(path = "/create.fromvideo")
	@ApiOperation(value = "Create subbing video task from video", response = TaskResponse.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Result SUCCESS status if successful, ERROR if some error occur")
	})
	public ResponseEntity<TaskResponse> addSubFromVideo(@ModelAttribute TaskVideoRequest request) {
		// The response
		TaskResponse response = null;

		try {
			// Info track, artist
			String track, artist;

			// Check request
			if (request.getVideo() == null)
				throw new Exception("Video is not specific");

			// Move input video to input folder
			File inFile = new File(inFolder + request.getVideo().getOriginalFilename().replaceAll("\'", "").replaceAll(" ", "_"));

			request.getVideo().transferTo(inFile);

			// if sub exists then move it to input folder
			File inSub = null;
			if (request.getSub() != null)
				request.getSub().transferTo(inSub = new File(inFolder + request.getSub().getOriginalFilename().replaceAll("\'", "").replaceAll(" ", "_")));

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
			track = format.tags.get("title");
			artist = format.tags.get("artist");

			// Check if artist or track name null then get from request
			if (track == null)
				track = request.getTitle();

			if (artist == null)
				artist = request.getArtist();

			// Check if track and artist exists
			if (track == null || artist == null)
				throw new Exception("Can't get information about music name and music artist!");

			// Print the track name and artist name
			System.out.println("Track: "+track+" artist: "+artist);

			// Create and return a task
			Task<SubVideoTaskResult> task = videoSubService.addSubToVideo(track, artist, inFile.getName(), inSub == null ? null : inSub.getName());

			// 
			System.out.println("Return task id="+task.getId());

			// Return task name
			response = new TaskResponse("SUCCESS", "", task.getId());
		} catch (Exception e) {
			e.printStackTrace();
			response = new TaskResponse("ERROR", e.getMessage(), "");
		}

		return new ResponseEntity<TaskResponse>(response, HttpStatus.OK);
	}

	@PostMapping(path = "/create.fromimage")
	@ApiOperation(value = "Create subbing video task from background video or image", response = TaskResponse.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Result SUCCESS status if successful, ERROR if some error occur")
	})
	public ResponseEntity<TaskResponse> addSubFromImage(@ModelAttribute TaskImageRequest request) {
		// The response
		TaskResponse response = null;

		try {
			// Info track, artist
			String track, artist;

			// Check request
			File inFile = null;
			if (request.getBackground() != null)
				request.getBackground().transferTo(inFile = new File(inFolder + request.getBackground().getOriginalFilename().replaceAll("\'", "").replaceAll(" ", "_")));

			// if sub exists then move it to input folder
			File inSub = null;
			if (request.getSub() != null)
				request.getSub().transferTo(inSub = new File(inFolder + request.getSub().getOriginalFilename().replaceAll("\'", "").replaceAll(" ", "_")));

			// Get track and artist name
			track = request.getTitle();
			artist = request.getArtist();

			// Check if track and artist exists
			if (track == null || artist == null)
				throw new Exception("Can't get information about music name and music artist!");

			// Print the track name and artist name
			System.out.println("Track: "+track+" artist: "+artist);

			// Create and return a task
			Task<SubVideoTaskResult> task = videoSubService.createSubVideoFromMusic(track, artist, inFile == null ? null : inFile.getName(), inSub == null ? null : inSub.getName());

			// 
			System.out.println("Return task id="+task.getId());

			// Return task name
			response = new TaskResponse("SUCCESS", "", task.getId());
		} catch (Exception e) {
			e.printStackTrace();
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
	public ResponseEntity<String> getSubtitle(@RequestParam("title") String title, @RequestParam("artist") String artist) {
		// Print the track name and artist name
		System.out.println("Track: "+title+" artist: "+artist);

		// 
		SongLyric lyric = null;

		// Create and return a task
		try {
			lyric = videoSubService.getSubtitle(title, artist);
		} catch(Exception e) {
		}

		if (lyric == null)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		return new ResponseEntity<String>(LyricConverter.writeCSV(lyric), HttpStatus.OK);
	}

	@GetMapping(path = "/task")
	@ApiOperation(value = "Get task infomation", response = TaskInfoResult.class)
	@ApiResponses({
		@ApiResponse(code = 200, message = "Return Task infomation"),
		@ApiResponse(code = 404, message = "TaskID is not found")
	})
	public ResponseEntity<TaskInfoResult> getTaskInfo(@RequestParam("id") String id) throws InterruptedException, ExecutionException {
		Task<SubVideoTaskResult> task = taskService.getTaskById(id, SubVideoTaskResult.class);

		if (task == null)
			return new ResponseEntity<TaskInfoResult>(HttpStatus.NOT_FOUND);
		
		TaskInfoResult result = new TaskInfoResult();
		if (task.getStatus() == Task.Status.FINISHED) {
			SubVideoTaskResult resultTask = task.get();
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
		Task<SubVideoTaskResult> task = taskService.getTaskById(id, SubVideoTaskResult.class);

		if (task == null)
			return new ResponseEntity<String>("Task not found!", HttpStatus.NOT_FOUND);

		if (task.getStatus() != Task.Status.FINISHED)
			return new ResponseEntity<String>("Task not finish!", HttpStatus.NO_CONTENT);

		// get result
		SubVideoTaskResult resultTask = task.get();

		// Return value
		MediaType mediaType = getMediaTypeForFileName(resultTask.getOutput().getName());
		System.out.println("fileName: " + resultTask.getOutput().getName());
		System.out.println("mediaType: " + mediaType);

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
