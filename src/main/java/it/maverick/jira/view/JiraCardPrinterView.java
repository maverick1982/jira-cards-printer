package it.maverick.jira.view;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.print.CardsPerPage;

import java.util.List;

/**
 * Created by Pasquale on 29/02/2016.
 */
public interface JiraCardPrinterView {

    void setVisible(boolean visible);

    void setProjects(List<JiraProject> projects);

    void setSprints(List<JiraSprint> sprints);

    void setCards(List<JiraCard> cards);

    void setHostEnabled(boolean enabled);

    void setUserEnabled(boolean enabled);

    void setPasswordEnabled(boolean enabled);

    void setConnectButtonEnabled(boolean enabled);

    void setDisconnectButtonEnabled(boolean enabled);

    void showErrorMessage(String title, String message);

    void setCardsPerPage(CardsPerPage selectedCardsPerPage);

    void setConnectionStatus(String connectionStatus);

    void setCardsStatus(String cardsStatus);

    void setCardsSelectionStatus(String cardsSelectionStatus);

    void setPagesCount(String pagesCountStatus);

    void setPagesSelectedCount(String pagesCountSelectedStatus);
}
