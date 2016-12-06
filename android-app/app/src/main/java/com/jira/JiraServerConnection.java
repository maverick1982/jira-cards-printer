package com.jira;


import com.jira.exception.JiraConnectionException;

import org.json.JSONException;

/**
 * Created by Pasquale on 29/02/2016.
 */
public interface JiraServerConnection {

    IssueDetails getIssueDetails(String issueID) throws JSONException, JiraConnectionException;
}
