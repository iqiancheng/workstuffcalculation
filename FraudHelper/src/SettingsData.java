import java.io.File;


public class SettingsData {
	private File inputFile;
	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the critPersent
	 */
	public int getCritPersent() {
		return critPersent;
	}

	/**
	 * @param critPersent the critPersent to set
	 */
	public void setCritPersent(int critPersent) {
		this.critPersent = critPersent;
	}

	/**
	 * @return the moreThan
	 */
	public int getMoreThan() {
		return moreThan;
	}

	/**
	 * @param moreThan the moreThan to set
	 */
	public void setMoreThan(int moreThan) {
		this.moreThan = moreThan;
	}

	private int critPersent;
	private int moreThan;

	public SettingsData() {
	}
}