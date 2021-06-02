package cyoap_main.design;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javafx.scene.image.Image;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DataSet {
	public String title;
	public String describe;
	public String image_name;
	
	public DataSet(String title, String describe, Image image) {
		this.title = title;
		this.describe = describe;
		if(image != null)this.image_name = image.getUrl();
	}
	
	public DataSet(String title, String describe) {
		this.title = title;
		this.describe = describe;
	}
}
