package sparib.prioritybot.handlers;

import net.dv8tion.jda.api.entities.TextChannel;
import org.w3c.dom.Text;
import sparib.prioritybot.classes.Server;
import sparib.prioritybot.main.Bot;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatastoreHandler {

    private Connection connection;
    private String connectionURL;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public void setConnectionURL(String user, String pass) {
        this.connectionURL = String.format("jdbc:mysql://localhost/java?user=%s&password=%s", user, pass);
    }

    /*
    public void writeServer(String serverID, int lockTime) throws Exception {
        try {

            // Make connection
            connection = DriverManager.getConnection(connectionURL);

            // Prepare prepared statement
            preparedStatement = connection.prepareStatement("INSERT INTO servers (server_id, lock_time)" +
                                                                "VALUES (?, ?)");

            // Set values
            preparedStatement.setString(1, serverID);
            preparedStatement.setInt(2, lockTime);

            preparedStatement.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public void deleteServer(String server_id) throws Exception {
        try {
            connection = DriverManager.getConnection(connectionURL);

            preparedStatement = connection.prepareStatement("DELETE FROM servers WHERE server_id = ?");

            preparedStatement.setString(1, server_id);

            preparedStatement.execute();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public void readDatabase() throws Exception {
        try {
            // Set up connection to database
            connection = DriverManager.getConnection(connectionURL);

            // Statements allow for sql queries
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM servers");
            writeResultSet(resultSet);
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }
     */

    public void postInitializationSetup() {
        try {
            connection = DriverManager.getConnection(connectionURL);

            statement = connection.createStatement();

            resultSet = statement.executeQuery(
                    "SELECT servers.server_id, lock_time, channel_id FROM servers CROSS JOIN channels;"
            );

            handleResultSet();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void handleResultSet() throws SQLException {
        HashMap<String, Server> serverHashMap = new HashMap<>();

        String serverID = "";
        int lockTime = 0;
        String prevServer = "";
        List<TextChannel> textChannelList = new ArrayList<>();
        int i = 0;
        while (resultSet.next()) {
            serverID = resultSet.getString("server_id");
            lockTime = resultSet.getInt("lock_time");
            if (serverID.equals(prevServer) || prevServer.equals("")) {
                String channelID = resultSet.getString("channel_id");
                TextChannel textChannel = Bot.bot.getTextChannelById(channelID);
                textChannelList.add(textChannel);
            } else {
                Server server = new Server(lockTime, new ArrayList<>(textChannelList));
                serverHashMap.put(serverID, server);
                textChannelList.clear();
            }
            prevServer = serverID;
            i++;
        }
        Server server = new Server(lockTime, new ArrayList<>(textChannelList));
        System.out.println(server.getChannels());
        serverHashMap.put(serverID, server);
        textChannelList.clear();

        Bot.servers.putAll(serverHashMap);
    }

    public void addServer(String serverID, Server server) {
        int lockTime = server.getLockTime();
        List<TextChannel> channels = server.getChannels();
        List<String> channelIDs = new ArrayList<>();
        channels.forEach(c -> { channelIDs.add(c.getId()); });

        try {
            connection = DriverManager.getConnection(connectionURL);
            preparedStatement = connection.prepareStatement("INSERT INTO servers (server_id, lock_time)" +
                                                                "VALUES (?, ?)");
            preparedStatement.setString(1, serverID);
            preparedStatement.setInt(2, lockTime);

            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("INSERT INTO channels (server_id, channel_id)" +
                                                                "VALUES (?, ?)");
            channels.forEach(c -> {
                try {
                    preparedStatement.setString(1, serverID);
                    preparedStatement.setString(2, c.getId());
                    preparedStatement.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (Exception ignored) {}
    }
}
