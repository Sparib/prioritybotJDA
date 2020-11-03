package sparib.prioritybot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import sparib.prioritybot.classes.Command;
import sparib.prioritybot.classes.Server;
import sparib.prioritybot.main.Bot;

import java.awt.*;

public class SetLockTime extends Command {
    public String name() { return "Lock Time"; }

    public String description() {
        return "Gets or set the lock time of the server.";
    }

    public String[] callers() {
        return new String[]{ "locktime" };
    }

    public void execute(Message message, String[] args) {
        EmbedBuilder embed;

        Server server = Bot.servers.get(message.getGuild().getId());
        if (args == null) {
            embed = new EmbedBuilder()
                    .setTitle("Lock Time")
                    .setDescription(String.format("The current lock time for this server is: \n`%d` second(s)", server.getLockTime()))
                    .setColor(Color.GREEN);
            message.getChannel().sendMessage(embed.build()).queue();
        }

        assert args != null;
        if (args.length > 1) {
            embed = new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("This only takes zero or one arguments!")
                    .setColor(Color.RED);
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        int newLockTime = 0;
        boolean integer = true;
        try {
            newLockTime = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
            integer = false;
        }

        if (!integer) {
            embed = new EmbedBuilder()
                    .setTitle("Error")
                    .setDescription("The lock time must be a number!")
                    .setColor(Color.RED);
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        server.setLockTime(newLockTime);
        embed = new EmbedBuilder()
                .setTitle("Success")
                .setDescription(String.format("The new lock time is:\n`%d` second(s).", newLockTime))
                .setColor(Color.GREEN);
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
