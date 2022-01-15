package cyoap_main.command;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.controller.createGui.CreateGuiController;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CommandTimeline {
	public List<AbstractCommand> commandList = new ArrayList<>();

	public int command_now = 0;

	@JsonIgnore
	public boolean isCommandListUpdated = false;

	public CommandTimeline() {
	}

	public void addCommand(AbstractCommand command) {
		if (command_now != 0 || !commandList.isEmpty()) {
			commandList = commandList.subList(0, command_now + 1);
		}
		commandList.add(command);
		command_now = commandList.size() - 1;
		isCommandListUpdated = true;
	}

	public void undoCommand() {
		if (command_now >= 0) {
			var command = commandList.get(command_now);
			command.undo();
			command_now -= 1;
			isCommandListUpdated = true;
		}
	}

	public void redoCommand() {
		if (command_now < commandList.size() - 1) {
			command_now += 1;
			var command = commandList.get(command_now);
			command.execute();
			isCommandListUpdated = true;
		}
	}

	public void update() {
		if (isCommandListUpdated) {
			isCommandListUpdated = false;
			List<String> name_list = new ArrayList<>();
			for (var command : commandList) {
				name_list.add(command.getName());
			}
			CreateGuiController.instance.view_command_timeline.getItems().clear();
			CreateGuiController.instance.view_command_timeline.getItems().setAll(name_list);
			CreateGuiController.instance.view_command_timeline.getSelectionModel().select(command_now);
		}
	}

	public void excuteCommand(AbstractCommand command) {
		command.execute();
		addCommand(command);
	}

	public void save() {
		var path_timeline = JavaFxMain.instance.directory.getAbsolutePath() + "/timeline.gz";
		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File(path_timeline);
		if (file.exists()) {
			file.delete();
		}
		try {
			var outputStream = new FileOutputStream(path_timeline);
			var gzipOutputStream = new GZIPOutputStream(outputStream);
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(gzipOutputStream, this);
			gzipOutputStream.finish();
			gzipOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		var path_timeline = JavaFxMain.instance.directory.getAbsolutePath() + "/timeline.gz";
		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File(path_timeline);
		if (file.exists()) {
			try {
				var inputStream = new FileInputStream(path_timeline);
				var gzipInputStream = new GZIPInputStream(inputStream);
				var v = objectMapper.readValue(gzipInputStream, CommandTimeline.class);
				this.commandList = v.commandList;
				this.command_now = v.command_now;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isCommandListUpdated = true;
		update();
	}
}
