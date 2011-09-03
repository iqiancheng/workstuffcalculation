package os_utils.file_filters;


import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Text file extensions filter for JFileChooser. Allows to choose text files
 * @author deko
 * @see FileFilter
 */
public class TextFileFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileExtensions.getExtension(f);
        if (extension != null) {
            if (extension.equals(FileExtensions.TXT)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Text File:  *.txt";
    }
}
