package it.maverick.jira;

import it.maverick.jira.data.JiraUser;

/**
 * Created by Pasquale on 29/02/2016.
 */
public interface JiraServer {

    JiraServerConnection createConnection(JiraUser jiraUser);
}
