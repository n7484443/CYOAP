package cyoap_main.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;

import cyoap_main.core.JavaFxMain;
import cyoap_main.design.controller.createGui.CreateGuiController;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class CommandTimeline {

	public List<AbstractCommand> commandList = new ArrayList<AbstractCommand>();

	public int command_now = 0;

	public boolean isCommandListUpdated = false;

	public CommandTimeline() {

	}

	public void addCommand(AbstractCommand command) {
		for (int i = command_now + 1; i < commandList.size(); i++) {
			commandList.remove(i);
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
			command.excute();
			isCommandListUpdated = true;
		}
	}

	public void update() {
		if (isCommandListUpdated) {
			isCommandListUpdated = false;
			List<String> name_list = new ArrayList<String>();
			for (var command : commandList) {
				name_list.add(command.getName());
			}
			CreateGuiController.instance.view_command_timeline.getItems().clear();
			CreateGuiController.instance.view_command_timeline.getItems().setAll(name_list);
			CreateGuiController.instance.view_command_timeline.getSelectionModel().select(command_now);
		}
	}

	public void excuteCommand(AbstractCommand command) {
		command.excute();
		addCommand(command);
	}

	public void save() {
		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/timeline.json");
		if (file.exists()) {
			file.delete();
		}
		try {
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/timeline.json"),
					StandardCharsets.UTF_8);
			objectMapper.writeValue(writer, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		ObjectMapper objectMapper = new ObjectMapper();
		File file = new File(JavaFxMain.instance.directory.getAbsolutePath() + "/timeline.json");
		if (file.exists()) {
			try {
				InputStreamReader reader = new InputStreamReader(
						new FileInputStream(JavaFxMain.instance.directory.getAbsolutePath() + "/timeline.json"),
						StandardCharsets.UTF_8);
				var v = objectMapper.readValue(reader, CommandTimeline.class);
				this.commandList = v.commandList;
				this.command_now = v.command_now;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(this.commandList.size());
		isCommandListUpdated = true;
		update();
	}
}
