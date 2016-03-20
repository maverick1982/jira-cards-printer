package it.maverick.jira.view;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.print.CardsPerPage;

import java.util.List;

/**
 * Created by Pasquale on 05/03/2016.
 */
public class JiraCardPrinterNullView implements JiraCardPrinterView {

    public void setVisible(boolean visible) {
        // do nothing
    }

    public void setProjects(List<JiraProject> projects) {
        // do nothing
    }

    public void setSprints(List<JiraSprint> sprints) {
        // do nothing
    }

    public void setCards(List<JiraCard> cards) {
        // do nothing
    }

    public void setHostEnabled(boolean enabled) {
        // do nothing
    }

    public void setUserEnabled(boolean enabled) {
        // do nothing
    }

    public void setPasswordEnabled(boolean enabled) {
        // do nothing
    }

    public void setConnectButtonEnabled(boolean enabled) {
        // do nothing
    }

    public void setDisconnectButtonEnabled(boolean enabled) {
        // do nothing
    }

    public void showErrorMessage(String title, String message) {
        // do nothing
    }

    public void setCardsPerPage(CardsPerPage selectedCardsPerPage) {
        // do nothing
    }

    public void setConnectionStatus(String connectionStatus) {
        // do nothing
    }

    public void setCardsStatus(String cardsStatus) {
        // do nothing
    }

    public void setCardsSelectionStatus(String cardsSelectionStatus) {
        // do nothing
    }

    public void setPagesCount(String pagesCountStatus) {
        // do nothing
    }

    public void setPagesSelectedCount(String pagesCountSelectedStatus) {
        // do nothing
    }
}
