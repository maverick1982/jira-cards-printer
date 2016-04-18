package it.maverick.jira.utils;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: Pasquale
 * Date: 03/01/14
 * Time: 23.32
 */
public class JsonToJiraParser {

    public static JiraProject parseProject(String jsonStringProject) {
        try {
            JSONObject jsonProject = new JSONObject(jsonStringProject);
            int id = jsonProject.getInt("id");
            String name = jsonProject.getString("name");
            return new JiraProject(id, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JiraSprint parseSprint(String jsonStringSprint) {
        try {
            JSONObject jsonSprint = new JSONObject(jsonStringSprint);
            int id = jsonSprint.getInt("id");
            String name = jsonSprint.getString("name");
            String state = jsonSprint.getString("state");
            return new JiraSprint(id, name, state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JiraCard parseCard(String jsonStringCard) {
        try {
            JSONObject jsonCard = new JSONObject(jsonStringCard);
            JSONObject fields = jsonCard.getJSONObject("fields");
            int id = jsonCard.getInt("id");
            String key = jsonCard.getString("key");
            String summary = fields.getString("summary");
            JiraCard jiraCard = new JiraCard(id, key, summary);
            JSONObject issueType = fields.getJSONObject("issuetype");
            jiraCard.setTypeName(issueType.getString("name"));
            jiraCard.setTypeUrl(issueType.getString("iconUrl"));
            JSONObject issuePriority = fields.getJSONObject("priority");
            jiraCard.setPriorityUrl(issuePriority.getString("iconUrl"));
            jiraCard.setPriorityName(issuePriority.getString("name"));
            if (cardHasStoryPoints(jsonCard)) {
                jiraCard.setStoryPoints(fields.getInt("customfield_10083"));
            }
            return jiraCard;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean cardHasStoryPoints(JSONObject jsonCard) {
        try {
            jsonCard.getJSONObject("fields").getDouble("customfield_10083");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
