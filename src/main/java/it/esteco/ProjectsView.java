package it.esteco;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * User: Pasquale
 * Date: 04/01/14
 * Time: 12.08
 */
public class ProjectsView extends JPanel {

    private final ResourceBundle resources   = ResourceBundle.getBundle("it.esteco.resources");
    private final ClassLoader    classLoader = ProjectsView.class.getClassLoader();

    private final ProjectsLogic projectsLogic;

    private static final String    LOAD_PROJECTS_LABEL           = "Load Projects";
    private static final String    PRINT_LABEL                   = "Print All Cards";
    private static final String    PRINT_SELECTED_LABEL          = "Print Selected Cards";
    private static final String    PRINT_ICON_URL                = "print.cards.icon";
    private static final String    PRINT_SELECTED_CARDS_ICON_URL = "print.cards.icon";
    private static final String    LOAD_PROJECTS_ICON_URL        = "load.projects.icon";
    private final        ImageIcon PRINT_CARDS_ICON              = new ImageIcon(classLoader.getResource(resources.getString(PRINT_ICON_URL)));
    private final        ImageIcon PRINT_SELECTED_CARDS_ICON     = new ImageIcon(classLoader.getResource(resources.getString(PRINT_SELECTED_CARDS_ICON_URL)));
    private final        ImageIcon LOAD_PROJECTS_ICON            = new ImageIcon(classLoader.getResource(resources.getString(LOAD_PROJECTS_ICON_URL)));
    private JList<JiraProject> projectsList;
    private DefaultListModel<JiraProject> projectsListModel = new DefaultListModel<JiraProject>();
    private JList<JiraSprint> sprintsList;
    private DefaultListModel<JiraSprint> sprintsListModel = new DefaultListModel<JiraSprint>();
    private JList<JiraCard> cardsList;
    private DefaultListModel<JiraCard> cardsListModel = new DefaultListModel<JiraCard>();
    private       JButton printButton;
    private       JButton printSelectedButton;
    private final JButton loadProjectsButton;

    public ProjectsView(ProjectsLogic projectsLogic) {
        this.projectsLogic = projectsLogic;
        projectsList = new JList<JiraProject>(projectsListModel);
        projectsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!projectsList.isSelectionEmpty()) {
                    ProjectsView.this.projectsLogic.onProjectSelected(projectsList.getSelectedValue().getId());
                }
            }
        });

        sprintsList = new JList<JiraSprint>(sprintsListModel);
        sprintsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!projectsList.isSelectionEmpty() && !sprintsList.isSelectionEmpty()) {
                    ProjectsView.this.projectsLogic.onSprintSelected(projectsList.getSelectedValue().getId(), sprintsList.getSelectedValue().getId());
                }
            }
        });

        cardsList = new JList<JiraCard>(cardsListModel);

        loadProjectsButton = new JButton(LOAD_PROJECTS_LABEL);
        loadProjectsButton.setIcon(LOAD_PROJECTS_ICON);
        loadProjectsButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProjectsView.this.projectsLogic.onLoadProjects();
            }
        });

        printSelectedButton = new JButton(PRINT_SELECTED_LABEL);
        printSelectedButton.setIcon(PRINT_SELECTED_CARDS_ICON);
        printSelectedButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProjectsView.this.projectsLogic.onPrint(new ArrayList<JiraCard>(cardsList.getSelectedValuesList()));
            }
        });

        printButton = new JButton(PRINT_LABEL);
        printButton.setIcon(PRINT_CARDS_ICON);
        printButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProjectsView.this.projectsLogic.onPrint(Collections.list(cardsListModel.elements()));
            }
        });

        layoutComponent();
    }

    private void layoutComponent() {
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.add(loadProjectsButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        buttonsPanel.add(printSelectedButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        buttonsPanel.add(printButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        setLayout(new GridBagLayout());

        JScrollPane projectsScroll = new JScrollPane(projectsList);
        JScrollPane sprintScroll = new JScrollPane(sprintsList);
        JScrollPane cardsScroll = new JScrollPane(cardsList);
        add(projectsScroll, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        add(sprintScroll, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        add(cardsScroll, new GridBagConstraints(2, 0, 1, 1, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        add(buttonsPanel, new GridBagConstraints(0, 1, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));


    }

    public void setProjects(ArrayList<JiraProject> projectsListData) {
        projectsListModel.clear();
        for (JiraProject jiraProject : projectsListData) {
            projectsListModel.addElement(jiraProject);
        }
    }

    public void setSprints(ArrayList<JiraSprint> sprintsList) {
        sprintsListModel.clear();
        for (JiraSprint jiraSprint : sprintsList) {
            sprintsListModel.addElement(jiraSprint);
        }
    }

    public void setCards(ArrayList<JiraCard> cardsList) {
        cardsListModel.clear();
        for (JiraCard jiraCard : cardsList) {
            cardsListModel.addElement(jiraCard);
        }
    }
}
