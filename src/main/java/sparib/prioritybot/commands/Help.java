package sparib.prioritybot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import sparib.prioritybot.classes.Command;
import sparib.prioritybot.main.Bot;

import java.awt.*;
import java.util.Date;
import java.util.Map;

public class Help extends Command {
    @Override
    public String name() {
        return "Help";
    }

    @Override
    public String description() {
        return "Shows this help menu";
    }

    @Override
    public String[] callers() {
        return new String[]{"help", "info"};
    }

    @Override
    public void execute(Message message, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        Map<String, Command> commands = Bot.commandHandler.commands;

        int i = -1;
        Command[] commandTable = new Command[commands.size()];
        boolean duplicate = false;
        for (Command command : commands.values()) {
            duplicate = false;
            if (i != -1) {
                for (Command curCommand : commandTable) {
                    if (curCommand == command) {
                        duplicate = true;
                        break;
                    }
                }
            }

            if (duplicate) { continue; }

            i++;

            commandTable[i] = command;
            
            // Get command attributes
            String name = command.name();
            String description = command.description();
            String[] callers = command.callers();

            // Get callers
            StringBuilder commandData = new StringBuilder(description + "```");
            for (int k = 0; k < callers.length; k++) {
                commandData.append(callers[k]);
                if (k != callers.length - 1) { commandData.append(", "); }
            }
            commandData.append("```");
            
            MessageEmbed.Field commandField = new MessageEmbed.Field(name, commandData.toString(), false);
            embed.addField(commandField);
        }

        // Add constants
        embed.setColor(Color.CYAN)
                .setFooter("Command issued by " + message.getAuthor().getAsTag())
                .setTitle("Help Section")
                .setTimestamp(new Date().toInstant());

        // Send Embed
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
