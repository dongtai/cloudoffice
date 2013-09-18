package apps.transmanager.weboffice.domain;

public class FileNames implements SerializableAdapter {
	private String filename;
	private String fileversion;
	private String filepath;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFileversion() {
		return fileversion;
	}
	public void setFileversion(String fileversion) {
		this.fileversion = fileversion;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
}
