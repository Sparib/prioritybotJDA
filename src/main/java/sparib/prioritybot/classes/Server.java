package sparib.prioritybot.classes;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Text;
import sparib.prioritybot.main.Bot;

import java.util.List;

public class Server {
    private List<TextChannel> channels;
    private int lockTime;


    public Server(int lockTime, List<TextChannel> channels) {
        this.lockTime = lockTime;
        this.channels = channels;
    }

    public Server setChannels(List<TextChannel> channels) {
        this.channels = channels;

        return this;
    }

    public List<TextChannel> getChannels() {
        return this.channels;
    }

    public int getLockTime() { return this.lockTime; }
}
