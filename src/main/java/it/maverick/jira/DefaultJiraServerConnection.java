package it.maverick.jira;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.exception.JiraConnectionException;
import it.maverick.jira.utils.JsonToJiraParser;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pasquale on 29/02/2016.
 */
public class DefaultJiraServerConnection implements JiraServerConnection {

    private static final Logger LOGGER = Logger.getLogger(DefaultJiraServerConnection.class.getName());

    private final CloseableHttpClient httpClient;
    private final HttpHost            targetHost;
    private final HttpClientContext   localContext;

    public DefaultJiraServerConnection(CloseableHttpClient httpClient, HttpHost targetHost, HttpClientContext localContext) {
        this.httpClient = httpClient;
        this.targetHost = targetHost;
        this.localContext = localContext;
    }

    private String doRequest(String request) {
        try {
            HttpGet getRequest = new HttpGet(request);
            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(targetHost, getRequest, localContext);

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
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    public List<JiraProject> getProjects() throws JiraConnectionException {
        List<JiraProject> projectsList = new ArrayList<JiraProject>();
        try {
            JSONObject jsonProjectsList = new JSONObject(doRequest("/rest/agile/1.0/board?type=scrum"));
            for (int i = 0; i < jsonProjectsList.optJSONArray("values").length(); i++) {
                JiraProject jiraProject = JsonToJiraParser.parseProject(jsonProjectsList.optJSONArray("values").getJSONObject(i).toString());
                projectsList.add(jiraProject);
            }
        } catch (Exception e) {
            throw new JiraConnectionException(e);
        }
        return projectsList;
    }

    public List<JiraSprint> getSprints(int projectId) throws JiraConnectionException {
        List<JiraSprint> sprintsList = new ArrayList<JiraSprint>();
        try {
            int page = 0;
            int sprintCount;
            do {
                int sprintStartIndex = 50 * page;
                JSONObject jsonSprintsList = new JSONObject(doRequest("/rest/agile/1.0/board/" + projectId + "/sprint?startAt=" + sprintStartIndex));
                sprintCount = jsonSprintsList.optJSONArray("values").length();
                for (int i = 0; i < sprintCount; i++) {
                    JiraSprint jiraSprint = JsonToJiraParser.parseSprint(jsonSprintsList.optJSONArray("values").getJSONObject(i).toString());
                    sprintsList.add(jiraSprint);
                }
                page++;
            } while (sprintCount > 0);

        } catch (Exception e) {
            throw new JiraConnectionException(e);
        }
        return sprintsList;
    }

    public List<JiraCard> getCards(int projectId, int sprintId) throws JiraConnectionException {
        List<JiraCard> cardsList = new ArrayList<JiraCard>();
        try {
            JSONObject jsonCardsList = new JSONObject(doRequest("/rest/agile/1.0/board/" + projectId + "/sprint/" + sprintId + "/issue"));
            JiraCard jiraCard;
            for (int i = 0; i < jsonCardsList.getJSONArray("issues").length(); i++) {
                jiraCard = JsonToJiraParser.parseCard(jsonCardsList.getJSONArray("issues").getJSONObject(i).toString());
                cardsList.add(jiraCard);
            }
        } catch (Exception e) {
            throw new JiraConnectionException(e);
        }
        return cardsList;
    }
}
