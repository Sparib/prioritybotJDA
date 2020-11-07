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

public class Setup extends Command {
    @Override
    public String name() {
        return "Setup";
    }

    @Override
    public String description() {
        return "Set up your server's priority channels";
    }

    @Override
    public String[] callers() {
        return new String[]{"setup"};
    }

    private int time;
    private final List<TextChannel> channels = new ArrayList<>();
    private String botMessageId = "";

    @Override
    public void execute(Message message, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        Message botMessage = null;
        if (!botMessageId.equals("")) {
            botMessage = message.getChannel().retrieveMessageById(botMessageId).complete();
        }

        if (botMessage == null && !botMessageId.equals("")) {
            embed = new EmbedBuilder()
                    .setTitle("Critical Error!")
                    .setDescription("Please fill out this form and DM it to `Sparib#9710`\n```" +
                                    "What command you ran:\n" +
                                    "What you were doing beforehand:\n" +
                                    "Time of incident (with timezone):```")
                    .setColor(Color.getHSBColor(0f, 1f, 0.58f));
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        if (message.getContentRaw().equalsIgnoreCase("stop")) {
            deletePrev(message);
            Bot.continueCommand = null;
            embed.setTitle("Stopped!").setColor(Color.RED);
            botMessage.editMessage(embed.build()).queue();
            return;
        }

        if (Bot.continueCommand == null) {
            embed.setTitle("Setup 1/3").setDescription(
                    "Respond with the length of time (seconds) a channel should be locked after a message is sent"
            ).setColor(Color.MAGENTA);
            message.getChannel().sendMessage(embed.build()).complete();
            botMessageId = getBotMessageId(message);
            Bot.continueCommand = this;
            Bot.continueState = 1;
            Bot.continueChannel = message.getChannel();
        } else if (Bot.continueState == 1) {
            deletePrev(message);
            String response = message.getContentRaw();
            try {
                time = Integer.parseInt(response);
            } catch (NumberFormatException e) {
                embed.setTitle("Your response must be a number!").setColor(Color.RED);
                botMessage.editMessage(embed.build()).queue();
                return;
            }

            embed.setTitle("Setup 2/2").setDescription(
                    "Respond by doing #(channel) or by saying \"done\" to finish this step."
            ).setColor(Color.MAGENTA);
            botMessage.editMessage(embed.build()).queue();
            Bot.continueState = 2;
        } else if (Bot.continueState == 2) {
            if (message.getContentRaw().equalsIgnoreCase("done")) {
                deletePrev(message);
                embed.setTitle("Setup Finished!").setColor(Color.GREEN)
                        .setDescription("For the bot to work correctly, you must set it to the highest role.");
                botMessage.editMessage(embed.build()).queue();
                Server server = new Server(time, channels);
                Bot.servers.put(message.getGuild().getId(), server);
                Bot.datastoreHandler.addServer(message.getGuild().getId(), server);
                Bot.resetContinue();
                System.out.println(Bot.servers);
                return;
            }

            deletePrev(message);
            List<TextChannel> mentionedChannels = message.getMentionedChannels();
            this.channels.addAll(mentionedChannels);
        }
    }

    private void deletePrev(Message message) {
        List<Message> history = message.getChannel().getHistory().retrievePast(1).complete();
        message.getChannel().deleteMessageById(history.get(0).getId()).complete();
    }

    private String getBotMessageId(Message message) {
        List<Message> history = message.getChannel().getHistory().retrievePast(1).complete();
        return history.get(0).getId();
    }
}
