package sparib.prioritybot.handlers;

import net.dv8tion.jda.api.entities.Guild;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DatastoreHandler {
    public void saveServer(String xml) {

    }


    public static class ServerXML {
        private int lockTime;
        private String[] channels;

        public ServerXML() {
        }

        public ServerXML(int lockTime, String[] channels) {
            this.lockTime = lockTime;
            this.channels = channels;
        }
    }
}
