/*
    Except where otherwise noted, all of the work under the src/main folder
    is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
    To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package sparib.prioritybot.main;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import sparib.prioritybot.classes.Command;
import sparib.prioritybot.classes.Server;
import sparib.prioritybot.handlers.CommandHandler;
import sparib.prioritybot.handlers.MessageHandler;
import sparib.prioritybot.handlers.PriorityHandler;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class Bot {
    // Bot client
    public static JDA bot;

    // Command handler
    public static CommandHandler commandHandler = new CommandHandler();

    // Priority handler
    public static PriorityHandler priorityHandler = new PriorityHandler();

    // Server map
    public static Map<String, Server> servers = new HashMap<String, Server>();

    // Continuation for commands
    public static Command continueCommand = null;
    public static int continueState = 0;
    public static MessageChannel continueChannel = null;

    public static void main(String[] args) throws LoginException {
        // Load dotenv and get the token
        Dotenv dotenv = Dotenv.load();
        final String token = dotenv.get("TOKEN");

        // Build the bot
        bot = JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new MessageHandler())
                .setActivity(Activity.watching("for pb setup"))
                .build();
    }

    public static void resetContinue() {
        continueChannel = null;
        continueCommand = null;
        continueState = 0;
    }
}
