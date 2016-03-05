package it.maverick.jira;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;

import java.util.List;

/**
 * Created by Pasquale on 29/02/2016.
 */
public interface JiraServerConnection {

    List<JiraProject> getProjects();

    List<JiraSprint> getSprints(int projectId);

    List<JiraCard> getCards(int projectId, int sprintId);
}
