package it.maverick.jira.preferences;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pasquale on 21/03/2016.
 */
public class XmlPreferencesStorage implements PreferencesStorage {

    private static final String PREFERENCES_PATH = System.getProperty("user.home") + File.separator + ".JiraCardsPrinter";

    private static final String PREFERENCES_XML_FILE_NAME = "preferences.xml";
    private static final String TAG_USER_PREFERENCES      = "userPreferences";
    private static final String TAG_USERS                 = "users";
    private static final String TAG_USER                  = "user";
    private static final String ATTRIBUTE_USER_NAME       = "userName";
    private static final String TAG_HOSTS                 = "hosts";
    private static final String TAG_HOST                  = "host";
    private static final String ATTRIBUTE_HOST_NAME       = "hostName";

    private final File            xmlFile;
    private       DocumentBuilder documentBuilder;
    private       Document        document;


    public XmlPreferencesStorage() {
        File customDir = new File(PREFERENCES_PATH);
        if (!customDir.exists()) {
            customDir.mkdirs();
        }
        xmlFile = new File(customDir, PREFERENCES_XML_FILE_NAME);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        if (!xmlFile.exists()) {
            createXmlFile();
        } else {
            loadXmlFile();
        }
    }

    private void loadXmlFile() {
        try {
            document = documentBuilder.parse(xmlFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createXmlFile() {
        document = documentBuilder.newDocument();

        Element rootElement = document.createElement(TAG_USER_PREFERENCES);
        document.appendChild(rootElement);

        Element hostsElement = document.createElement(TAG_HOSTS);
        rootElement.appendChild(hostsElement);

        Element usersElement = document.createElement(TAG_USERS);
        rootElement.appendChild(usersElement);

        flushXml();
    }

    private void flushXml() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFile.getPath());
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


    public void addUser(String user) {
        Node users = document.getElementsByTagName(TAG_USERS).item(0);
        Element userElement = document.createElement(TAG_USER);
        userElement.setAttribute(ATTRIBUTE_USER_NAME, user);
        users.appendChild(userElement);

        flushXml();
    }

    public List<String> getHosts() {
        List<String> hostsName = new ArrayList<String>();
        Node hosts = document.getElementsByTagName(TAG_HOSTS).item(0);
        NodeList hostNodes = hosts.getChildNodes();
        for (int i = 0; i < hostNodes.getLength(); i++) {
            Node hostNode = hostNodes.item(i);
            hostsName.add(hostNode.getAttributes().getNamedItem(ATTRIBUTE_HOST_NAME).getNodeValue());
        }
        return hostsName;
    }

    public void removeHost(String host) {
        Node hosts = document.getElementsByTagName(TAG_HOSTS).item(0);
        NodeList hostNodes = hosts.getChildNodes();
        for (int i = 0; i < hostNodes.getLength(); i++) {
            Node hostNode = hostNodes.item(i);
            if (hostNode.getAttributes().getNamedItem(ATTRIBUTE_HOST_NAME).getNodeValue().equals(host)) {
                if (hostNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element hostElement = (Element) hostNode;
                    hostElement.getParentNode().removeChild(hostElement);
                }
            }
        }
        flushXml();
    }

    public void addHost(String host) {
        Node hosts = document.getElementsByTagName(TAG_HOSTS).item(0);
        Element hostElement = document.createElement(TAG_HOST);
        hostElement.setAttribute(ATTRIBUTE_HOST_NAME, host);
        hosts.appendChild(hostElement);

        flushXml();
    }

    public List<String> getUsers() {
        List<String> usersName = new ArrayList<String>();
        Node users = document.getElementsByTagName(TAG_USERS).item(0);
        NodeList userNodes = users.getChildNodes();
        for (int i = 0; i < userNodes.getLength(); i++) {
            Node userNode = userNodes.item(i);
            usersName.add(userNode.getAttributes().getNamedItem(ATTRIBUTE_USER_NAME).getNodeValue());
        }
        return usersName;
    }

    public void removeUser(String user) {
        Node users = document.getElementsByTagName(TAG_USERS).item(0);
        NodeList userNodes = users.getChildNodes();
        for (int i = 0; i < userNodes.getLength(); i++) {
            Node userNode = userNodes.item(i);
            if (userNode.getAttributes().getNamedItem(ATTRIBUTE_USER_NAME).getNodeValue().equals(user)) {
                if (userNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) userNode;
                    userElement.getParentNode().removeChild(userElement);
                }
            }
        }
        flushXml();
    }
}
