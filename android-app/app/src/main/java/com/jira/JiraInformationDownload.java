package com.jira;

import android.os.AsyncTask;

import com.jira.data.JiraUser;

/**
 * Created by focchioni on 12/5/2016.
 */

public class JiraInformationDownload extends AsyncTask<String, Void, String> {

    private final String username;
    private final String password;

    public JiraInformationDownload(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String issueId = strings[0];
            JiraUser user = new JiraUser(username, password);
            JiraServerConnection connection = new DefaultJiraServerConnection("https://jira.esteco.com", user.getName(), user.getPassword());
            IssueDetails issueDetails = connection.getIssueDetails(issueId);
            return issueDetails.getStatus()+"\n"+issueDetails.getAssignee()+"\n"+issueDetails.getTester();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
