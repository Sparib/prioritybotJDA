package sparib.prioritybot.handlers;

import net.dv8tion.jda.api.entities.Message;
import sparib.prioritybot.classes.Command;
import sparib.prioritybot.main.Bot;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CommandHandler {
    public Map<String, Command> commands = new HashMap<>();

    public CommandHandler() {
        Path dirPath = Paths.get("./src/main/java/sparib/prioritybot/commands").toAbsolutePath().normalize();
        try (Stream<Path> paths = Files.walk(Paths.get(String.valueOf(dirPath)), 1)) {
            paths.map(Path::toString).filter(file -> file.endsWith(".java"))
                    .forEach(this::addCommand);
        } catch (IOException ignored) {}

    }

    private void addCommand(String path) {
        Command command = null;
        String[] pathParts = path.replace("\\", "/").split("/");
        path = "sparib.prioritybot.commands." + pathParts[pathParts.length - 1].replace(".java", "");
        try {
            command = (Command) Class.forName(path).getDeclaredConstructor().newInstance();
        } catch ( ClassNotFoundException
                | IllegalAccessException
                | InstantiationException
                | NoSuchMethodException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (command == null) { return; }
        for (int i = 0; i < command.callers().length; i++) {
            commands.put(command.callers()[i], command);
        }
    }

    public void runCommand(String command, String[] args, Message message) {
        if (Bot.continueCommand != null) {
            Bot.continueCommand.execute(message, args);
            return;
        }

        for (String key : commands.keySet()) {
            if (!command.equalsIgnoreCase(key)) { continue; }
            commands.get(key).execute(message, args);
            break;
        }
    }
}
