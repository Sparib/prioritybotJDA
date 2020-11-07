package sparib.prioritybot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import sparib.prioritybot.classes.Command;
import sparib.prioritybot.classes.Server;
import sparib.prioritybot.main.Bot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SetChannels extends Command {
    public String name() { return "Set Channels"; }

    public String description() { return "See or set the channels that the bot auto-locks"; }

    public String[] callers() { return new String[]{ "channels" }; }

    private final List<TextChannel> textChannels = new ArrayList<>();
    private String botMessageId = "";

    public void execute(Message message, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        Message botMessage = null;
        if (!botMessageId.equals("")) {
            botMessage = message.getChannel().retrieveMessageById(botMessageId).complete();
        }

        if (botMessage == null && !botMessageId.equals("")) {
            embed.setTitle("Critical Error!")
                    .setDescription("Please fill out this form and DM it to `Sparib#9710`\n```" +
                            "What command you ran:\n" +
                            "What you were doing beforehand:\n" +
                            "Time of incident (with timezone):```")
                    .setColor(Color.getHSBColor(0f, 1f, 0.58f));
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        Server server = null;
        for (String s : Bot.servers.keySet()) {
            if (s.equals(message.getGuild().getId())) {
                server = Bot.servers.get(s);
                break;
            }
        }

        if (server == null) {
            embed.setTitle("Error")
                    .setDescription("This server has not been set up yet!\nUse `pb setup` to setup this server.")
                    .setColor(Color.RED);
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        if (Bot.continueCommand == this && Bot.continueState == 1 && Bot.continueChannel == message.getChannel()) {
            if (message.getContentRaw().equalsIgnoreCase("stop")) {
                embed.setTitle("Stopped!")
                        .setColor(Color.RED);
                deleteMessage(message);
                message.getChannel().sendMessage(embed.build()).queue();
                return;
            }
            if (message.getContentRaw().equalsIgnoreCase("done")) {
                if (textChannels.isEmpty()) {
                    embed.setTitle("Set Channels Failed!")
                            .setDescription("You must set channels to lock!\n" +
                                            "You can rerun the command with `pb channels set`")
                            .setColor(Color.RED);
                    message.getChannel().sendMessage(embed.build()).queue();
                    Bot.resetContinue();
                    return;
                }
                server.setChannels(textChannels);
                Bot.datastoreHandler.updateChannels(message.getGuild().getId(), textChannels);
                Bot.resetContinue();
                return;
            }
            textChannels.addAll(message.getMentionedChannels());
            deleteMessage(message);
            return;
        }

        if (args == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Currently set channels:\n```");
            for (TextChannel c : server.getChannels()) {
                stringBuilder.append(String.format("#%s\n", c.getName()));
            }
            stringBuilder.append("```\nTo set the channels use `pb channels set`");

            embed.setTitle("Channels")
                    .setDescription(stringBuilder.toString())
                    .setColor(Color.GREEN);
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        if (args.length > 1 || !args[0].equalsIgnoreCase("set")) {
            embed.setTitle("Error")
                    .setDescription("This command only takes one argument." +
                                  "\nThat argument being \"set\"(without quotes)" +
                                  "\nTo see the currently set channels just use `pb channels`")
                    .setColor(Color.RED);
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        if (args[0].equalsIgnoreCase("set")) {
            embed.setTitle("Set Channels")
                    .setDescription("Tag the channel(s) you wish to set with #" +
                                  "\nType `done` when you are done" +
                                  "\nThis will remove all channels" +
                            " so make sure to mention the channels that were\nalready auto-locked if you want" +
                            " them to persist.")
                    .setColor(Color.BLUE);
            message.getChannel().sendMessage(embed.build()).queue();
            botMessageId = getBotMessageId(message);

            Bot.continueState = 1;
            Bot.continueCommand = this;
            Bot.continueChannel = message.getChannel();
        }
    }

    private void deleteMessage(Message message) {
        List<Message> history = message.getChannel().getHistory().retrievePast(1).complete();
        message.getChannel().deleteMessageById(history.get(0).getId()).complete();
    }

    private String getBotMessageId(Message message) {
        List<Message> history = message.getChannel().getHistory().retrievePast(1).complete();
        return history.get(0).getId();
    }
}
