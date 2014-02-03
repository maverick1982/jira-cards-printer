package it.esteco;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

/**
 * User: Pasquale
 * Date: 20/12/13
 * Time: 22.21
 */
public class Main {

    private static final ResourceBundle resources   = ResourceBundle.getBundle("it.esteco.resources");
    private static final ClassLoader    classLoader = Main.class.getClassLoader();

    private static final String FRAME_TITLE = "Jira Cards Printer - v0.2 Alpha";
    private static HttpHost            targetHost;
    private static CloseableHttpClient httpClient;
    private static HttpClientContext   localContext;

    /**
     * https://<instance>/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=<id>&sprintId=<id>
     * https://<instance>/rest/greenhopper/1.0/sprintquery/<id>
     * https://<instance>/rest/greenhopper/1.0/rapidview
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setSize(1024, 768);
                frame.setTitle(FRAME_TITLE);
                frame.setIconImage(new ImageIcon(classLoader.getResource(resources.getString("application.icon"))).getImage());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                JiraServer jiraServer = new JiraServer();
                ServerView serverView = new ServerView(jiraServer);
                jiraServer.setServerView(serverView);

                ProjectsLogic projectsLogic = new ProjectsLogic(jiraServer);
                ProjectsView projectsView = new ProjectsView(projectsLogic);
                projectsLogic.setProjectsView(projectsView);

                frame.setLayout(new BorderLayout());
                frame.add(serverView, BorderLayout.PAGE_START);
                frame.add(projectsView);

            }
        });

    }

    public static String doRequest(String request) {
        try {
            HttpGet getRequest = new HttpGet(request);
            getRequest.addHeader("accept", "application/json");
//            long start = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(targetHost, getRequest, localContext);
//            System.out.println("-------> " + (System.currentTimeMillis() - start));

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String resp = "";
            String output;
            while ((output = br.readLine()) != null) {
                resp += output;
            }
            return resp;
        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }
}
