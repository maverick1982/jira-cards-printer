package it.maverick.jira;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.data.JiraUser;
import it.maverick.jira.exception.JiraConnectionException;
import it.maverick.jira.preferences.PreferencesStorage;
import it.maverick.jira.preferences.UserPreferences;
import it.maverick.jira.preferences.XmlPreferencesStorage;
import it.maverick.jira.print.CardsPerPage;
import it.maverick.jira.print.Page;
import it.maverick.jira.view.JiraCardPrinterNullView;
import it.maverick.jira.view.JiraCardPrinterView;

import javax.swing.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Pasquale on 29/02/2016.
 */
public class JiraCardPrinter {

    private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("it.maverick.jira.resources");

    private static final String DISCONNECTED_LABEL         = RESOURCES.getString("status.disconnected.label");
    private static final String CONNECTED_LABEL            = RESOURCES.getString("status.connected.label");
    private static final String CONNECTING_LABEL           = RESOURCES.getString("status.connecting.label");
    private static final String LOADING_LABEL              = RESOURCES.getString("status.loading.label");
    private static final String ALL_CARDS_COUNT_LABEL      = RESOURCES.getString("status.all.cards.count.label");
    private static final String SELECTED_CARDS_COUNT_LABEL = RESOURCES.getString("status.selected.cards.count.label");
    private static final String PAGES_NEEDED_COUNT         = RESOURCES.getString("status.pages.needed.count.label");

    private final PreferencesStorage preferencesStorage = new XmlPreferencesStorage();
    private final UserPreferences    userPreferences    = new UserPreferences(preferencesStorage, 10);

    private JiraServerConnection jiraServerConnection;
    private JiraCardPrinterView jiraCardPrinterView  = new JiraCardPrinterNullView();
    private CardsPerPage        selectedCardsPerPage = CardsPerPage.FOUR;
    private JiraProject selectedJiraProject;
    private JiraSprint  selectedJiraSprint;
    private List<JiraCard> jiraCards         = new ArrayList<JiraCard>();
    private List<JiraCard> selectedJiraCards = new ArrayList<JiraCard>();

    public void installView(JiraCardPrinterView jiraCardPrinterView) {
        this.jiraCardPrinterView = jiraCardPrinterView;
        List<String> users = userPreferences.getUsers();
        List<String> hosts = userPreferences.getHosts();
        jiraCardPrinterView.setUsersForHint(users);
        jiraCardPrinterView.setHostsForHint(hosts);
        if (!users.isEmpty()) {
            jiraCardPrinterView.setUser(users.get(users.size() - 1));
        }
        if (!hosts.isEmpty()) {
            jiraCardPrinterView.setHost(hosts.get(hosts.size() - 1));
        }
        jiraCardPrinterView.setCardsPerPage(selectedCardsPerPage);
        disconnect();
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
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jiraCardPrinterView.setConnectionStatus(CONNECTING_LABEL);
                        }
                    });

                    JiraUser jiraUser = new JiraUser(userName, password);
                    JiraServer jiraServer = new DefaultJiraServer(host);
                    jiraServerConnection = jiraServer.createConnection(jiraUser);

                    userPreferences.addUser(userName);
                    userPreferences.addHost(host);

                    final List<JiraProject> projects = jiraServerConnection.getProjects();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jiraCardPrinterView.setConnectionStatus(CONNECTED_LABEL);
                            jiraCardPrinterView.setProjects(projects);
                        }
                    });

//                    userPreferences.addUser(userName);
//                    userPreferences.addHost(host);
                } catch (JiraConnectionException e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jiraCardPrinterView.showErrorMessage("Connection error", "Connection error message");
                            disconnect();
                        }
                    });
                }
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
        setSprintsOnView(new ArrayList<JiraSprint>());
        jiraCardPrinterView.setConnectionStatus(DISCONNECTED_LABEL);
    }

    public void onProjectSelected(final JiraProject jiraProject) {
        selectedJiraProject = jiraProject;
        if (selectedJiraProject != null) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        final List<JiraSprint> sprints = jiraServerConnection.getSprints(selectedJiraProject.getId());
                        Collections.reverse(sprints);

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                setSprintsOnView(sprints);
                            }
                        });
                    } catch (JiraConnectionException e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                jiraCardPrinterView.showErrorMessage("Connection error", "Connection error message");
                            }
                        });
                    }

                }
            };
            thread.start();
        } else {
            setSprintsOnView(new ArrayList<JiraSprint>());
        }

    }

    private void setSprintsOnView(List<JiraSprint> sprints) {
        jiraCardPrinterView.setSprints(sprints);
        setCardsOnView(new ArrayList<JiraCard>());
    }

    public void onSprintSelected(final JiraSprint jiraSprint) {
        selectedJiraSprint = jiraSprint;
        if (selectedJiraProject != null && selectedJiraSprint != null) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                jiraCardPrinterView.setConnectionStatus(LOADING_LABEL);
                            }
                        });
                        jiraCards = jiraServerConnection.getCards(selectedJiraProject.getId(), selectedJiraSprint.getId());
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                jiraCardPrinterView.setConnectionStatus(CONNECTED_LABEL);
                                setCardsOnView(jiraCards);
                            }
                        });
                    } catch (JiraConnectionException e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                jiraCardPrinterView.setConnectionStatus(CONNECTED_LABEL);
                                jiraCardPrinterView.showErrorMessage("Connection error", "Connection error message");
                            }
                        });
                    }
                }
            };
            thread.start();
        } else {
            jiraCards = new ArrayList<JiraCard>();
            setCardsOnView(new ArrayList<JiraCard>());
        }

    }

    private void setCardsOnView(List<JiraCard> cards) {
        jiraCardPrinterView.setCards(cards);
        jiraCardPrinterView.setCardsStatus(String.format(ALL_CARDS_COUNT_LABEL, cards.size()));
        updatePagesCountView();
    }

    private void updatePagesCountView() {
        int allCardsPageCount = (int) Math.ceil((double) jiraCards.size() / selectedCardsPerPage.getCardsPerPageInt());
        int selectedCardsPageCount = (int) Math.ceil((double) selectedJiraCards.size() / selectedCardsPerPage.getCardsPerPageInt());
        jiraCardPrinterView.setPagesCount(String.format(PAGES_NEEDED_COUNT, allCardsPageCount));
        if (selectedCardsPageCount > 0) {
            jiraCardPrinterView.setPagesSelectedCount(String.format(PAGES_NEEDED_COUNT, selectedCardsPageCount));
        } else {
            jiraCardPrinterView.setPagesSelectedCount("");
        }
    }

    public void onPrint() {
        print(jiraCards);
    }

    public void onCardsPerPageSelected(CardsPerPage cardsPerPage) {
        selectedCardsPerPage = cardsPerPage;
        updatePagesCountView();
    }

    public void onCardsSelected(List<JiraCard> selectedCards) {
        selectedJiraCards = selectedCards;
        if (selectedJiraCards != null && !selectedJiraCards.isEmpty()) {
            jiraCardPrinterView.setCardsSelectionStatus(String.format(SELECTED_CARDS_COUNT_LABEL, selectedCards.size()));
        } else {
            jiraCardPrinterView.setCardsSelectionStatus("");
        }
        updatePagesCountView();
    }

    public void onPrintSelected() {
        print(selectedJiraCards);
    }

    private void print(List<JiraCard> jiraCardList) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Page(jiraCardList, selectedCardsPerPage));
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
              /* The job did not successfully complete */
            }
        }
    }
}
