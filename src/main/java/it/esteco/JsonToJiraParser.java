package it.esteco;

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
            int id = jsonCard.getInt("id");
            String key = jsonCard.getString("key");
            String summary = jsonCard.getString("summary");
            JiraCard jiraCard = new JiraCard(id, key, summary);
            jiraCard.setTypeName(jsonCard.getString("typeName"));
            jiraCard.setTypeUrl(jsonCard.getString("typeUrl"));
            jiraCard.setPriorityUrl(jsonCard.getString("priorityUrl"));
            jiraCard.setPriorityName(jsonCard.getString("priorityName"));
            if (cardHasStoryPoints(jsonCard)) {
                jiraCard.setStoryPoints(jsonCard.getJSONObject("estimateStatistic").getJSONObject("statFieldValue").getDouble("value"));
            }
            jiraCard.setStatusName(jsonCard.getString("statusName"));
            jiraCard.setStatusUrl(jsonCard.getString("statusUrl"));
            return jiraCard;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean cardHasStoryPoints(JSONObject jsonCard) {
        try {
            if (jsonCard.getJSONObject("estimateStatistic") == null ||
                    jsonCard.getJSONObject("estimateStatistic").getJSONObject("statFieldValue") == null ||
                    jsonCard.getJSONObject("estimateStatistic").getJSONObject("statFieldValue").getDouble("value") == Double.NaN)
                return false;
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}
