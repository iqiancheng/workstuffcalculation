package os_utils;

import java.io.File;
import java.io.IOException;
/**
 * Class provides OS functions like  getCurrentDirectory(), etc
 * @author deko
 *
 */
public class OsUtils {
	/**
	 * Function returns current working directory path ( as File object)
	 * @return currentDir  Current working directory
	 * @throws IOException
	 */
	public static File getCurrentOsDirectory() {
		File currentDir = null;
		try {
			currentDir = new File(new File(".").getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return currentDir;
	}

}
