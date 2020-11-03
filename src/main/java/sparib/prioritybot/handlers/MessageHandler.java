package sparib.prioritybot.handlers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sparib.prioritybot.classes.Server;
import sparib.prioritybot.main.Bot;

public class MessageHandler extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // If the message was sent by the bot then return
        if (event.getAuthor() == Bot.bot.getSelfUser()) { return; }

        // Message one and message two
        Message message = event.getMessage();
        String content = message.getContentRaw();

        // Also get the server because why not
        Server server = Bot.servers.get(message.getGuild().getId());

        if (content.equalsIgnoreCase("pb unlock") || content.equals("pb ul")) {
            Bot.commandHandler.runCommand("unlock", null, message);
            return;
        }

        // Check if the current channel is one that will lock
        TextChannel channel = null;
        if (server != null) {
            for (TextChannel c : server.getChannels()) {
                if (message.getTextChannel() == c) {
                    channel = c;
                    break;
                }
            }
        }

        if (channel != null) {
            Bot.priorityHandler.handleMessage(message);
            return;
        }

        String[] array = content.split("\\s+");

        if (array.length > 1) {
            if (!array[0].equalsIgnoreCase("pb")) { return; }

            String command = array[1];
            String[] args = null;
            if (array.length > 2) {
                args = new String[array.length - 2];
                System.arraycopy(array, 2, args, 0, args.length);
            }

            Bot.commandHandler.runCommand(command, args, message);
            return;
        }

        if (Bot.continueCommand != null && Bot.continueChannel == message.getChannel())
        { Bot.commandHandler.runCommand(array[0], null, message); }
    }
}
