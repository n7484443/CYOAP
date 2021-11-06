package cyoap_main.util;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import net.lingala.zip4j.ZipFile;
import org.apache.maven.artifact.versioning.ComparableVersion;
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
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class LoadUtil {
    public static LoadUtil instance;

    public static boolean isIDE = true;

    public LoadUtil() {
        instance = this;
    }

    public Pane loadFXML(String path) throws IOException {
        URL url = LoadUtil.class.getResource(path);
        Pane root = FXMLLoader.load(url);
        return root;
    }

    public String loadCss(String path) throws IOException {
        return LoadUtil.class.getResource(path).toString();
    }

    public static String loadFile(String str_path) throws URISyntaxException {
        URL url = LoadUtil.class.getClassLoader().getResource(str_path);
        Path path = Paths.get(url.toURI());
        if (Files.exists(path)) {
            try (FileChannel channel = FileChannel.open(path)) {
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) Files.size(path));
                channel.read(byteBuffer);
                byteBuffer.flip();

                return Charset.defaultCharset().decode(byteBuffer).toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("no file with " + str_path);
        }

        return null;
    }

    public InputStream loadInternalImage(String path) throws IOException {
        return LoadUtil.class.getResourceAsStream(path);
    }

    public static List<Path> getSubPath(String url) throws URISyntaxException, IOException {
        URI uri = LoadUtil.class.getResource(url).toURI();
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            myPath = fileSystem.getPath(url);
        } else {
            myPath = Paths.get(uri);
        }
        Stream<Path> walk = Files.walk(myPath, 1);
        boolean b = true;
        List<Path> pathList = new ArrayList<Path>();
        for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
            if (b) {
                b = false;
                pathList.add(it.next());
            } else {
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
        if (!f.toString().contains(".webp") && !f.toString().contains(".jpeg")) {
            image = new Image(f.toURI().toString());
        } else {
            int[] pixels = null;
            try {
                BufferedImage image_base = ImageIO.read(f);
                int width = image_base.getWidth();
                int height = image_base.getHeight();

                pixels = image_base.getRaster().getPixels(0, 0, width, height, (int[]) null);

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        pixels[i + j * width] = image_base.getRGB(i, j);
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
        File f;
        if (s.contains(File.separator)) {
            f = new File(s);
            f = CreateSubImage(f);
        } else {
            f = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/images/" + s);
        }
        return loadImage(f);
    }

    private static File CreateSubImage(File f) {
        File folder = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/images");
        if (!folder.exists()) {
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
                c.guiComponent.setUp();
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
        for (var paragraph : p) {
            for (var d : paragraph.getStyledSegments()) {
                area.append(d.getSegment(), d.getStyle());
            }
            if(p.size() - 1 != p.indexOf(paragraph)){
                area.append(System.lineSeparator(), "");
            }
        }
        area.recreateParagraphGraphic(0);
    }

    public static List<StyledSegment<String, String>> paragraphToSegment(List<Paragraph<String, String, String>> p, List<StyledSegment<String, String>> styleSeg) {
        styleSeg.clear();
        for (var a : p) {
            var b = a.getStyledSegments();
            for (var c : b) {
                styleSeg.add(c);
            }
            if(p.size() - 1 != p.indexOf(a)){
                styleSeg.add(new StyledSegment<String, String>(System.lineSeparator(), ""));
            }
        }

        return styleSeg;
    }

    public static void loadSegment(InlineCssTextArea area, List<StyledSegment<String, String>> styleSeg) {
        area.clear();
        for (int i = 0; i < styleSeg.size(); i++) {
            var seg = styleSeg.get(i);
            if (seg == null && i != styleSeg.size() - 1) {
                area.append(System.lineSeparator(), "");
            } else {
                area.append(seg.getSegment(), seg.getStyle());
            }
        }
        area.recreateParagraphGraphic(0);
    }

    public static int loadLatestVersion() {
        if (!isIDE) {
            String repoURL = "n7484443/CYOAP";
            try {
                System.setProperty("https.protocols", "TLSv1.2");
                GitHub github = GitHub.connectAnonymously();

                GHRepository repo = github.getRepository(repoURL);
                var latestRelease = repo.getLatestRelease();
                var listAssets = latestRelease.listAssets();
                var asset = listAssets.toList().get(0).getBrowserDownloadUrl();

                System.out.println("latest version is : " + latestRelease.getName());
                ComparableVersion version_github = new ComparableVersion(latestRelease.getName());
                ComparableVersion version_this = new ComparableVersion(JavaFxMain.version);
                if (version_this.compareTo(version_github) < 0) {
                    System.out.println("download start with : " + asset);
                    File f = download(asset);
                    if (f == null) return -1;//no file
                    unzip(f);
                    f.delete();
                    System.out.println("download end");
                    return 1;//complete
                }
                return 0;//no latest version
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return -2;//ide
    }

    private static void unzip(File f) throws IOException {
        var dir = f.getPath().replace("cyoap_update.zip", "newImage");
        new ZipFile(f).extractAll(dir);
        File f_bat = new File(dir + "/start.bat");
        f_bat.delete();
    }

    public static File download(String data) throws IOException {
        File f = new File(System.getProperty("user.dir"));
        f = f.getParentFile().getParentFile();
        String OUTPUT_FILE_PATH = f.getPath() + "/cyoap_update.zip";
        InputStream in = new URL(data).openStream();
        Path p = Paths.get(OUTPUT_FILE_PATH);
        if(Files.exists(p)){
            Files.delete(p);
        }
        Files.copy(in, p);
        return new File(OUTPUT_FILE_PATH);
    }
}
