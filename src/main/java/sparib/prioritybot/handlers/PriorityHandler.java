package sparib.prioritybot.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.w3c.dom.Text;
import sparib.prioritybot.classes.Server;
import sparib.prioritybot.main.Bot;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PriorityHandler {
    public HashMap<TextChannel, ScheduledExecutorService> closedChannels = new HashMap<>();

    final EnumSet<Permission> writePerm = EnumSet.of(Permission.MESSAGE_WRITE);
    HashMap<TextChannel, Member> waitingChannels = new HashMap<>();
    TextChannel currentChannel;
    Member waitingMember = null;
    Role publicRole = null;
    int openTime;

    public void handleMessage(Message message) {
        for (String s : Bot.servers.keySet()) {
            if (s.equals(message.getGuild().getId())) {
                openTime = Bot.servers.get(s).getLockTime();
                break;
            }
        }

        for (TextChannel c : closedChannels.keySet()) {
            if (c == message.getTextChannel()) {
                ScheduledExecutorService currentService = closedChannels.get(c);

                currentService.shutdownNow();
                closedChannels.remove(c);

                ScheduledExecutorService newService = Executors.newSingleThreadScheduledExecutor();
                newService.schedule(this::resetPerms, openTime, TimeUnit.SECONDS);

                closedChannels.put(c, newService);
                return;
            }
        }

        currentChannel = message.getTextChannel();
        publicRole = message.getGuild().getPublicRole();

        boolean isBot = false;
        if (message.getAuthor().isBot()) {
            isBot = true;
            for (TextChannel c : waitingChannels.keySet()) {
                if (currentChannel == c) {
                    waitingMember = waitingChannels.get(c);
                    break;
                }
            }
            if (waitingMember == null) { return; }
        }

        if (!isBot) {
            waitingChannels.put(currentChannel, message.getMember());
            return;
        }

        waitingChannels.remove(currentChannel);

        currentChannel.putPermissionOverride(publicRole).setDeny(writePerm).complete();
        currentChannel.putPermissionOverride(waitingMember).setAllow(writePerm).complete();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Channel locked!")
                .setDescription("Channel locked for " + openTime + " second(s).")
                .setColor(Color.RED);

        message.getChannel().sendMessage(embed.build()).queue();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(this::resetPerms, openTime, TimeUnit.SECONDS);

        this.closedChannels.put(currentChannel, executorService);
    }

    public void resetPermsPublic(Message message) {
        TextChannel channel = message.getTextChannel();
        Role publicRole = message.getGuild().getPublicRole();
        Member closingMember = message.getMember();
        assert closingMember != null;

        channel.putPermissionOverride(publicRole).reset().complete();
        for (PermissionOverride po : channel.getMemberPermissionOverrides()) {
            if (po.getMember() == closingMember) {
                po.delete().complete();
            }
        }
    }

    private void resetPerms() {
        currentChannel.putPermissionOverride(publicRole).reset().complete();
        for (PermissionOverride po : currentChannel.getMemberPermissionOverrides()) {
            if (po.getMember() == waitingMember) {
                po.delete().complete();
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Channel unlocked!")
                .setColor(Color.GREEN);

        currentChannel.sendMessage(embed.build()).queue();
    }
}
