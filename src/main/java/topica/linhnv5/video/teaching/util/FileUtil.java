package topica.linhnv5.video.teaching.util;

import java.io.File;

public class FileUtil {

	/**
	 * Generate new file name
	 * @param path Input path
	 * @param name Input file name
	 * @return new File
	 */
	public static synchronized File matchFileName(String path, String name) {
		name = name.replaceAll("\'", "").replaceAll(" ", "_");

		File f;
		if (!(f = new File(path+name)).exists())
			return f;

		int id = 1;
		while ((f = new File(path+name+"_"+id)).exists());

		return f;
	}

}
