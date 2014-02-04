package it.maverick;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * User: Pasquale
 * Date: 04/01/14
 * Time: 11.44
 */
public class JiraServer {

    private String host;
    private int    port;
    private String user;
    private String password;

    private static HttpHost            targetHost;
    private static CloseableHttpClient httpClient;
    private static HttpClientContext   localContext;
    private        ServerView          serverView;

    public JiraServer() {
        this.port = 443;
    }

    public void createConnection() {
        targetHost = new HttpHost(host, port, "https");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(user, password));
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        serverView.onServerConnected();
    }

    private String doRequest(String request) {
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

    public ArrayList<JiraProject> getProjectsList() {
        ArrayList<JiraProject> projectsList = new ArrayList<JiraProject>();
        try {
            JSONObject jsonProjectsList = new JSONObject(doRequest("/rest/greenhopper/1.0/rapidview"));
            for (int i = 0; i < jsonProjectsList.optJSONArray("views").length(); i++) {
                JiraProject jiraProject = JsonToJiraParser.parseProject(jsonProjectsList.optJSONArray("views").getJSONObject(i).toString());
                projectsList.add(jiraProject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return projectsList;
    }

    public ArrayList<JiraSprint> getSprints(int projectId) {
        ArrayList<JiraSprint> sprintsList = new ArrayList<JiraSprint>();
        try {
            JSONObject jsonSprintsList = new JSONObject(doRequest("/rest/greenhopper/1.0/sprintquery/" + projectId));
            for (int i = 0; i < jsonSprintsList.optJSONArray("sprints").length(); i++) {
                JiraSprint jiraSprint = JsonToJiraParser.parseSprint(jsonSprintsList.optJSONArray("sprints").getJSONObject(i).toString());
                sprintsList.add(jiraSprint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sprintsList;
    }

    public ArrayList<JiraCard> getCards(int projectId, int sprintId) {
        ArrayList<JiraCard> cardsList = new ArrayList<JiraCard>();
        try {
            JSONObject jsonCardsList = new JSONObject(doRequest("/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=" + projectId + "&sprintId=" + sprintId));
            JiraCard jiraCard;
            for (int i = 0; i < jsonCardsList.getJSONObject("contents").getJSONArray("completedIssues").length(); i++) {
                jiraCard = JsonToJiraParser.parseCard(jsonCardsList.getJSONObject("contents").getJSONArray("completedIssues").getJSONObject(i).toString());
                cardsList.add(jiraCard);
            }
            for (int i = 0; i < jsonCardsList.getJSONObject("contents").getJSONArray("incompletedIssues").length(); i++) {
                jiraCard = JsonToJiraParser.parseCard(jsonCardsList.getJSONObject("contents").getJSONArray("incompletedIssues").getJSONObject(i).toString());
                cardsList.add(jiraCard);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardsList;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServerView(ServerView serverView) {
        this.serverView = serverView;
    }

    public void closeConnection() {
        try {
            httpClient.close();
            serverView.onServerDisconnected();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
