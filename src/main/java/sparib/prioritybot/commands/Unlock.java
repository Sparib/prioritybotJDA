package sparib.prioritybot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import sparib.prioritybot.classes.Command;
import sparib.prioritybot.main.Bot;

import java.awt.*;

public class Unlock extends Command {
    @Override
    public String name() {
        return "Unlock";
    }

    @Override
    public String description() {
        return "Unlocks a currently locked channel early.";
    }

    @Override
    public String[] callers() {
        return new String[]{ "unlock", "ul" };
    }

    @Override
    public void execute(Message message, String[] args) {
        boolean isLocked = false;
        for (TextChannel c : Bot.priorityHandler.closedChannels.keySet()) {
            if (message.getTextChannel() == c) {
                isLocked = true;
                System.out.println(isLocked);
                break;
            }
        }

        System.out.println(isLocked);

        if (!isLocked) {
            message.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("This channel isn't locked!")
                    .setColor(Color.RED).build()).queue();
            return;
        }

        Bot.priorityHandler.resetPermsPublic(message);
        Bot.priorityHandler.closedChannels.get(message.getTextChannel()).shutdownNow();
        Bot.priorityHandler.closedChannels.remove(message.getTextChannel());

        message.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Channel Unlocked!")
                .setColor(Color.GREEN).build()).queue();
    }
}
