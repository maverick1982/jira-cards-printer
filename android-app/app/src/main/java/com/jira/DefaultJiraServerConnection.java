package com.jira;


import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by Pasquale on 29/02/2016.
 */
public class DefaultJiraServerConnection implements JiraServerConnection {

    private final String host;
    private final String username;
    private final String password;


    public DefaultJiraServerConnection(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    private String getIssueStatus(JSONObject jsonStatus) throws JSONException {
        return ((JSONObject) ((JSONObject) jsonStatus.get("fields")).get("status")).getString("name");
    }

    private String getIssueAssignee(JSONObject jsonStatus) throws JSONException {
        return ((JSONObject) ((JSONObject) jsonStatus.get("fields")).get("assignee")).getString("displayName");
    }

    private String getIssueTester(JSONObject jsonStatus) throws JSONException {
        Object issueTester = ((JSONObject) jsonStatus.get("fields")).get("customfield_10610");
        if (issueTester == JSONObject.NULL) {
            return null;
        }
        JSONObject jsonObject = (JSONObject) issueTester;
        return jsonObject.getString("displayName");
    }


    @Override
    public IssueDetails getIssueDetails(String issueID) {
        IssueDetails issueDetails = new IssueDetails();

        String request = doRequest("/rest/api/2/issue/" + issueID + "?fields=status,assignee,customfield_10610");

        JSONObject jsonStatus;
        try {
            jsonStatus = new JSONObject(request);
            issueDetails.setStatus(getIssueStatus(jsonStatus));
            issueDetails.setAssignee(getIssueAssignee(jsonStatus));
            issueDetails.setTester(getIssueTester(jsonStatus));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return issueDetails;
    }

    private String doRequest(String request) {
        try {
            String userpass = username + ":" + password;
            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
            URL url = new URL(host + request);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            // Create the SSL connection
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line).append('\n');
            }
            return total.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
