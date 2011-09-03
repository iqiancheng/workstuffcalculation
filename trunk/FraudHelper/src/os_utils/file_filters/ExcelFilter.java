package os_utils.file_filters;

import java.io.File;
import javax.swing.filechooser.*;

/**
 * Excel file extensions filter for JFileChooser
 * @author deko
 * @see FileFilter
 */
public class ExcelFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
    	
    	// allows to select  subdirectories 
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileExtensions.getExtension(f);
        if (extension != null) {
        	if (extension.equals(FileExtensions.XLS) ||
            		extension.equals(FileExtensions.XLSX)) {
        		return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Excel Spreadsheet:  *.xls, *.xlsx";
    }
}
