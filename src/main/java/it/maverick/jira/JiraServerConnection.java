package it.maverick.jira;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.exception.JiraConnectionException;

import java.util.List;

/**
 * Created by Pasquale on 29/02/2016.
 */
public interface JiraServerConnection {

    List<JiraProject> getProjects() throws JiraConnectionException;

    List<JiraSprint> getSprints(int projectId) throws JiraConnectionException;

    List<JiraCard> getCards(int projectId, int sprintId) throws JiraConnectionException;
}
