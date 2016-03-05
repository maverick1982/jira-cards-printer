package it.maverick.jira;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.data.JiraUser;
import it.maverick.jira.view.JiraCardPrinterView;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pasquale on 29/02/2016.
 */
public class JiraCardPrinter implements Printable {

    private JiraServerConnection jiraServerConnection;

    private List<JiraCard> cardsToPrint;

    private JiraCardPrinterView jiraCardPrinterView = new JiraCardPrinterNullView();

    public void installView(JiraCardPrinterView jiraCardPrinterView) {
        this.jiraCardPrinterView = jiraCardPrinterView;
    }

    public void run() {
        jiraCardPrinterView.setVisible(true);
    }

    public void connect(final String host, final String userName, final String password) {
        jiraCardPrinterView.setHostEnabled(false);
        jiraCardPrinterView.setUserEnabled(false);
        jiraCardPrinterView.setPasswordEnabled(false);
        jiraCardPrinterView.setConnectButtonEnabled(false);
        jiraCardPrinterView.setDisconnectButtonEnabled(true);

        Thread thread = new Thread() {
            @Override
            public void run() {
                JiraUser jiraUser = new JiraUser(userName, password);
                JiraServer jiraServer = new DefaultJiraServer(host);
                jiraServerConnection = jiraServer.createConnection(jiraUser);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jiraCardPrinterView.setProjects(jiraServerConnection.getProjects());
                    }
                });
            }
        };
        thread.start();


    }

    public void disconnect() {
        jiraServerConnection = null;

        jiraCardPrinterView.setHostEnabled(true);
        jiraCardPrinterView.setUserEnabled(true);
        jiraCardPrinterView.setPasswordEnabled(true);
        jiraCardPrinterView.setConnectButtonEnabled(true);
        jiraCardPrinterView.setDisconnectButtonEnabled(false);
        jiraCardPrinterView.setProjects(new ArrayList<JiraProject>());
        jiraCardPrinterView.setSprints(new ArrayList<JiraSprint>());
        jiraCardPrinterView.setCards(new ArrayList<JiraCard>());
    }

    public void onProjectSelected(final int id) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                final List<JiraSprint> sprints = jiraServerConnection.getSprints(id);
                Collections.reverse(sprints);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jiraCardPrinterView.setSprints(sprints);
                    }
                });
            }
        };
        thread.start();
    }

    public void onSprintSelected(final int projectId, final int sprintId) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                final List<JiraCard> cards = jiraServerConnection.getCards(projectId, sprintId);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        jiraCardPrinterView.setCards(cards);
                    }
                });
            }
        };
        thread.start();
    }

    public void onPrint(List<JiraCard> cards) {
        cardsToPrint = cards;
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
              /* The job did not successfully complete */
            }
        }
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex < cardsToPrint.size()) {
            cardsToPrint.get(pageIndex).createCardPrint(graphics, pageFormat);
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

    private class JiraCardPrinterNullView implements JiraCardPrinterView {
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

        public void loadingCards() {
            // do nothing
        }
    }
}
