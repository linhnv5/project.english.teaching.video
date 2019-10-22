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

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import topica.linhnv5.video.teaching.model.ApiRequest;
import topica.linhnv5.video.teaching.model.ApiResponse;
import topica.linhnv5.video.teaching.model.SubVideoTaskResult;
import topica.linhnv5.video.teaching.model.Task;
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
public class ApiController {

	@Autowired
	private TaskService taskService;

	@Autowired
	private VideoSubService videoSubService;

	@Autowired
	private FFprobe ffprobe;

	@Value("${video.teaching.infolder}")
	private String inFolder;

	@PostMapping(path = "/create")
	public ResponseEntity<ApiResponse> addSubForVideo(@ModelAttribute ApiRequest request) {
		// The response
		ApiResponse response = null;

		try {
			// Info track, artist
			String track, artist;

			// Check request
			if (request.getVideo() != null) {
				// Move input video to input folder
				File inFile = new File(inFolder + request.getVideo().getOriginalFilename().replaceAll("\'", "").replaceAll(" ", "_"));

				request.getVideo().transferTo(inFile);

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
				Task<SubVideoTaskResult> task = videoSubService.addSubToVideo(inFile.getName(), track, artist);

				// 
				System.out.println("Return task id="+task.getId());

				// Return task name
				response = new ApiResponse("SUCCESS", 200, "", task.getId());
			} else {
				// Get track and artist name
				track = request.getTitle();
				artist = request.getArtist();

				// Check if track and artist exists
				if (track == null || artist == null)
					throw new Exception("Can't get information about music name and music artist!");

				// Print the track name and artist name
				System.out.println("Track: "+track+" artist: "+artist);

				// Create and return a task
				Task<SubVideoTaskResult> task = videoSubService.createSubVideoFromMusic(track, artist);

				// 
				System.out.println("Return task id="+task.getId());

				// Return task name
				response = new ApiResponse("SUCCESS", 200, "", task.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new ApiResponse("ERROR", 200, e.getMessage(), -1);
		}

		return new ResponseEntity<ApiResponse>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/task")
	public ResponseEntity<TaskInfoResult> getTaskInfo(@RequestParam int id) throws InterruptedException, ExecutionException {
		Task<SubVideoTaskResult> task = taskService.getTaskById(id, SubVideoTaskResult.class);

		TaskInfoResult result = new TaskInfoResult();

		if (task == null) {
			result.setStatus("ERROR");
			result.setError("TaskId not found!");
		} else {
			if (task.getStatus() == Task.Status.FINISHED) {
				SubVideoTaskResult resultTask = task.get();
				if (resultTask.getException() != null) {
					result.setStatus("ERROR");
					result.setError(resultTask.getException().getMessage());
				} else
					result.setStatus("SUCCESS");
			} else
				result.setStatus("RUNNING");
			result.setProgress(task.getProgress());
		}
		
		return new ResponseEntity<TaskInfoResult>(result, HttpStatus.OK);
	}

	@Autowired
	private ServletContext servletContext;

	public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
		// application/pdf
		// application/xml
		// image/gif, ...
		String mineType = servletContext.getMimeType(fileName);
		try {
			MediaType mediaType = MediaType.parseMediaType(mineType);
			return mediaType;
		} catch (Exception e) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	@GetMapping(path = "/result")
	public ResponseEntity<?> getTaskResult(@RequestParam int id) throws InterruptedException, ExecutionException, FileNotFoundException {
		Task<SubVideoTaskResult> task = taskService.getTaskById(id, SubVideoTaskResult.class);

		if (task == null)
			return new ResponseEntity<String>("Task not found!", HttpStatus.NOT_FOUND);

		if (task.getStatus() != Task.Status.FINISHED)
			return new ResponseEntity<String>("Task not finish!", HttpStatus.NO_CONTENT);

		// get result
		SubVideoTaskResult resultTask = task.get();

		// Return value
		MediaType mediaType = getMediaTypeForFileName(this.servletContext, resultTask.getOutput().getName());
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
