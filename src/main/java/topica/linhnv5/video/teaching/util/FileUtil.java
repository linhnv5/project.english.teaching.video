package topica.linhnv5.video.teaching.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Utility contain function to create new file, get file contain, ...
 * 
 * @author ljnk975
 */
public class FileUtil {

	/**
	 * Generate new file name
	 * @param path Input path
	 * @param name Input file name
	 * @return new File
	 */
	public static synchronized File matchFileName(String path, String name) {
		if (!path.endsWith(File.separator))
			path = path + File.separator;

		name = name.replaceAll("\'", "").replaceAll(" ", "_");

		checkAndMKDir(path);

		File f;
		if (!(f = new File(path+name)).exists())
			return f;

		int id = name.lastIndexOf('.'); String extension;

		if (id < 0) {
			extension = "";
		} else {
			extension = name.substring(id);
			name = name.substring(0, id);
		}

		id = 1;
		while ((f = new File(path+name+"_"+id+extension)).exists()) id++;

		return f;
	}

	/**
	 * Generate new dir name
	 * @param path Input path
	 * @param name Input file name
	 * @return new File
	 */
	public static synchronized File matchDirName(String path, String name) {
		if (!path.endsWith(File.separator))
			path = path + File.separator;

		name = name.replaceAll("\'", "").replaceAll(" ", "_");

		checkAndMKDir(path);

		File f;
		if (!(f = new File(path+name)).exists())
			return f;

		int id = name.lastIndexOf('.'); String extension;

		if (id < 0) {
			extension = "";
		} else {
			extension = name.substring(id);
			name = name.substring(0, id);
		}

		id = 1;
		while ((f = new File(path+name+"_"+id+extension)).exists()) id++;

		f.mkdirs();

		return f;
	}

	/**
	 * Check path if not exists then make new folder
	 * @param path path of folder to check
	 */
	public static synchronized void checkAndMKDir(String path) {
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
	}

	/**
	 * Read file to string buffer
	 * @param f file
	 * @return the file contain
	 * @throws Exception
	 */
	public static String getFileString(File f) throws Exception {
		return Files.lines(Paths.get(f.getPath()), StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
	}

	/**
	 * Read file content
	 * @param f file
	 * @return the file contain
	 * @throws Exception
	 */
	public static byte[] getFileContent(File f) throws Exception {
		return Files.readAllBytes(Paths.get(f.getPath()));
	}

}
