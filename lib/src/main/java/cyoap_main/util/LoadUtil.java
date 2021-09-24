package cyoap_main.util;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledSegment;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.ChoiceSet;
import javafx.fxml.FXMLLoader;
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
