package sparib.prioritybot.classes;

import net.dv8tion.jda.api.entities.Message;

public abstract class Command {
    public abstract String name();
    public abstract String description();
    public abstract String[] callers();

    /**
     * What to be run on execution
     * @param message Message variable for reference in execution
     * @param args Arguments from command for ease of reference
     */
    public abstract void execute(Message message, String[] args);
}
