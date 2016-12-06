package com.jira;

import android.os.AsyncTask;
import android.util.Log;

import com.jira.data.JiraUser;
import com.jira.exception.JiraConnectionException;
import com.qrcodereader.MainActivity;

import org.json.JSONException;

/**
 * Created by focchioni on 12/5/2016.
 */

public class JiraInformationDownload extends AsyncTask<String, Void, IssueDetails> {

    private final String hostname;
    private final String username;
    private final String password;

    public JiraInformationDownload(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
    }

    @Override
    protected IssueDetails doInBackground(String... strings) {
        try {
            String issueId = strings[0];
            JiraUser user = new JiraUser(username, password);
            JiraServerConnection connection = new DefaultJiraServerConnection("https://" + hostname, user.getName(), user.getPassword());
            connection.getIssueDetails(issueId);
            return connection.getIssueDetails(issueId);
        } catch (JiraConnectionException e) {
            Log.w(MainActivity.TAG, e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
