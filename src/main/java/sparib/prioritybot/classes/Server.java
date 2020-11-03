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


    /**
     *
     * @param lockTime The time a channel will be locked for after last response.
     * @param channels The channels that will auto-lock.
     */
    public Server(int lockTime, List<TextChannel> channels) {
        this.lockTime = lockTime;
        this.channels = channels;
    }

    /**
     * Re-sets the list of channels that will auto-lock.
     * @param channels The new list of channels to auto-lock.
     * @return The Server class. Useful for chaining.
     */
    public Server setChannels(List<TextChannel> channels) {
        this.channels = channels;

        return this;
    }

    /**
     * Gets the list of channels that will auto-lock.
     * @return The list of channels that will auto-lock.
     */
    public List<TextChannel> getChannels() {
        return this.channels;
    }

    public Server setLockTime(int lockTime) {
        this.lockTime = lockTime;

        return this;
    }

    /**
     * Gets the time a channel will be locked for after last response.
     * @return The time a channel will be locked for after last response.
     */
    public int getLockTime() { return this.lockTime; }
}
