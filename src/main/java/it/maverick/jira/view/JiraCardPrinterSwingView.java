package it.maverick.jira.view;

import it.maverick.jira.JiraCardPrinter;
import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.print.CardsPerPage;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pasquale on 29/02/2016.
 */
public class JiraCardPrinterSwingView extends JFrame implements JiraCardPrinterView {

    private static final Logger LOGGER = Logger.getLogger(JiraCardPrinterSwingView.class.getName());

    private static final ResourceBundle RESOURCES    = ResourceBundle.getBundle("it.maverick.jira.resources");
    private static final ClassLoader    CLASS_LOADER = JiraCardPrinterSwingView.class.getClassLoader();

    private static final String FRAME_TITLE = RESOURCES.getString("application.title");

    private final ProjectsSwingView  projectsView;
    private final ServerSwingView    serverSwingView;
    private final StatusBarSwingView statusBarView;

    public JiraCardPrinterSwingView(JiraCardPrinter jiraCardPrinter) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
//                    UIManager.setLookAndFeel(info.getClassName());
                    final NimbusLookAndFeel laf = new NimbusLookAndFeel();
                    UIManager.setLookAndFeel(laf);
                    UIDefaults defaults = laf.getDefaults();
                    defaults.put("List[Selected].textForeground",
                            laf.getDerivedColor("nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0, false));
                    defaults.put("List[Selected].textBackground",
                            laf.getDerivedColor("nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0, false));
                    defaults.put("List[Disabled+Selected].textBackground",
                            laf.getDerivedColor("nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0, false));
                    defaults.put("List[Disabled].textForeground",
                            laf.getDerivedColor("nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0, false));
                    defaults.put("List:\"List.cellRenderer\"[Disabled].background",
                            laf.getDerivedColor("nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0, false));
                    break;
                }
            }
//        } catch (ClassNotFoundException e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//        } catch (InstantiationException e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//        } catch (IllegalAccessException e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        projectsView = new ProjectsSwingView(jiraCardPrinter);
        serverSwingView = new ServerSwingView(jiraCardPrinter);
        statusBarView = new StatusBarSwingView();

        setSize(1024, 768);
        setTitle(FRAME_TITLE);
        final ImageIcon APPLICATION_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("application.icon")));
        setIconImage(APPLICATION_ICON.getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(serverSwingView, BorderLayout.PAGE_START);
        add(statusBarView, BorderLayout.AFTER_LAST_LINE);
        add(projectsView);
    }

    public void setProjects(List<JiraProject> projects) {
        projectsView.setProjects(projects);
    }

    public void setSprints(List<JiraSprint> sprints) {
        projectsView.setSprints(sprints);
    }

    public void setCards(List<JiraCard> cards) {
        projectsView.setCards(cards);
    }

    public void setHostEnabled(boolean enabled) {
        serverSwingView.setHostEnabled(enabled);
    }

    public void setUserEnabled(boolean enabled) {
        serverSwingView.setUserEnabled(enabled);
    }

    public void setPasswordEnabled(boolean enabled) {
        serverSwingView.setPasswordEnabled(enabled);
    }

    public void setConnectButtonEnabled(boolean enabled) {
        serverSwingView.setConnectButtonEnabled(enabled);
    }

    public void setDisconnectButtonEnabled(boolean enabled) {
        serverSwingView.setDisconnectButtonEnabled(enabled);
    }

    public void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void setCardsPerPage(CardsPerPage selectedCardsPerPage) {
        projectsView.selectCardsPerPage(selectedCardsPerPage);
    }

    public void setConnectionStatus(String connectionStatus) {
        statusBarView.setConnectionStatus(connectionStatus);
    }

    public void setCardsStatus(String cardsStatus) {
        statusBarView.setCardsStatus(cardsStatus);
    }

    public void setCardsSelectionStatus(String cardsSelectionStatus) {
        statusBarView.setCardsSelectionStatus(cardsSelectionStatus);
    }

    public void setPagesCount(String pagesCountStatus) {
        statusBarView.setPagesCountStatus(pagesCountStatus);
    }

    public void setPagesSelectedCount(String pagesCountSelectedStatus) {
        statusBarView.setPagesCountSelectedStatus(pagesCountSelectedStatus);
    }

    public void setUsersForHint(List<String> users) {
        serverSwingView.setUsersForHint(users);
    }

    public void setHostsForHint(List<String> hosts) {
        serverSwingView.setHostsForHint(hosts);
    }

    public void setUser(String user) {
        serverSwingView.setUser(user);
    }

    public void setHost(String host) {
        serverSwingView.setHost(host);
    }
}
