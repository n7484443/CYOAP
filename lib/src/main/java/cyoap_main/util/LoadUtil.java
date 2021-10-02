package cyoap_main.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledSegment;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.choice.ChoiceSet;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;

public class LoadUtil {
	public static LoadUtil instance;

	public LoadUtil() {
		instance = this;
	}

	public Pane loadFXML(String path) throws IOException {
		URL url = LoadUtil.class.getResource(path);
		Pane root = (Pane) FXMLLoader.load(url);
		return root;
	}
	

	public String loadCss(String path) throws IOException {
		return LoadUtil.class.getResource(path).toString();
	}

	public static List<Path> getSubPath(String url) throws URISyntaxException, IOException {
		URI uri = LoadUtil.class.getResource(url).toURI();
		Path myPath;
		if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            myPath = fileSystem.getPath(url);
        } else {
            myPath = Paths.get(uri);
        }
        Stream<Path> walk = Files.walk(myPath, 1);
        boolean b = true;
        List<Path> pathList = new ArrayList<Path>();
        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
        	if(b) {
        		b = false;
        		pathList.add(it.next());
        	}else {
            	it.next();
        	}
        }
        walk.close();
		
		return pathList;
	}
	
	public static File loadFolder() {
		DirectoryChooser chooser = new DirectoryChooser();
		return chooser.showDialog(JavaFxMain.instance.stage);
	}
	
	//dropped out 로드
	public static SimpleEntry<Image, String> loadImage(File f) {
		Image image = null;
		if (!f.toString().contains(".webp")) {
			image = new Image(f.toURI().toString());
		} else {
			int[] pixels = null;
			try {
				BufferedImage image_base = ImageIO.read(f);
				int width = image_base.getWidth();
				int height = image_base.getHeight();
				
				pixels = image_base.getRaster().getPixels(0, 0, width, height, (int[])null);
				
				for (int i = 0; i < width; i ++) {
					for (int j = 0; j < height; j ++) {
						pixels[i + j*width] = image_base.getRGB(i, j);
					}
				}
				
				WritableImage img = new WritableImage(width, height);
				img.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0,
						width);
				image = img;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		f = CreateSubImage(f);
		
		return new SimpleEntry<Image, String>(image, f.getName());
	}
	
	//일반적인 로드
	public static SimpleEntry<Image, String> loadImage(String s) {
		File f = null;
		if(s.contains(File.separator)) {
			f = new File(s);
			f = CreateSubImage(f);
		}else {
			f = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/images/" + s);
		}
		return loadImage(f);
	}

	private static File CreateSubImage(File f) {
		File folder = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/images");
		if(!folder.exists()){
			folder.mkdir();
		}
		File newf = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/images/" + f.getName());
		try {
			Files.copy(f.toPath(), newf.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newf;
	}

	public static void setupChoiceSet(ChoiceSet choiceSet) {
		for (var c : choiceSet.choiceSet_child) {
			if (!c.choiceSet_child.isEmpty()) {
				setupChoiceSet(c);
			} else {
				c.guiComponent.setUp(c);
			}
		}
	}

	public static void loadChoiceSetParents(ChoiceSet choiceSet) {
		for (var c : choiceSet.choiceSet_child) {
			if (!c.choiceSet_child.isEmpty()) {
				loadChoiceSetParents(c);
			} else {
				c.choiceSet_parent = choiceSet;
				choiceSet.guiComponent.hbox.getChildren().add(c.getAnchorPane());
			}
		}
	}
	
	public static void loadParagraph(InlineCssTextArea area, List<Paragraph<String, String, String>> p) {
		area.clear();
		for(var paragraph : p) {
			for(var d : paragraph.getStyledSegments()) {
				area.append(d.getSegment(), d.getStyle());
			}
			area.append(System.lineSeparator(), "");
		}
		area.recreateParagraphGraphic(0);
	}
	
	public static List<StyledSegment<String, String>> paragraphToSegment(List<Paragraph<String, String, String>> p, List<StyledSegment<String, String>> styleSeg) {
		styleSeg.clear();
		for(var a : p) {
			var b = a.getStyledSegments();		
			for(var c : b) {
				styleSeg.add(c);
			}
			styleSeg.add(new StyledSegment<String, String>(System.lineSeparator(), ""));
		}
		
		return styleSeg;
	}
	
	public static void loadSegment(InlineCssTextArea area, List<StyledSegment<String, String>> styleSeg) {
		area.clear();
		for(int i = 0; i < styleSeg.size(); i++) {
			var seg = styleSeg.get(i);
			if(seg == null && i != styleSeg.size() - 1) {
				area.append(System.lineSeparator(), "");
			}else {
				area.append(seg.getSegment(), seg.getStyle());
			}
		}
		area.recreateParagraphGraphic(0);
	}
}
