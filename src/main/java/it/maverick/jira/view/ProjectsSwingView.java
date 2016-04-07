package it.maverick.jira.view;

import it.maverick.jira.JiraCardPrinter;
import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.print.CardsPerPage;
import it.maverick.jira.view.renderer.CardsPerPageListCellRenderer;
import it.maverick.jira.view.renderer.JiraCardListRenderer;
import it.maverick.jira.view.renderer.JiraProjectCellRenderer;
import it.maverick.jira.view.renderer.JiraSprintListRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.ResourceBundle;

/**
 * User: Pasquale
 * Date: 04/01/14
 * Time: 12.08
 */
public class ProjectsSwingView extends JPanel {

    private static final ResourceBundle RESOURCES    = ResourceBundle.getBundle("it.maverick.jira.resources");
    private static final ClassLoader    CLASS_LOADER = ProjectsSwingView.class.getClassLoader();

    private static final String    PROJECT_LABEL             = RESOURCES.getString("project.label");
    private static final String    CARDS_PER_PAGE_LABEL      = RESOURCES.getString("cards.per.page.label");
    private static final String    PRINT_LABEL               = RESOURCES.getString("print.all.cards.label");
    private static final String    PRINT_SELECTED_LABEL      = RESOURCES.getString("print.selected.cards.label");
    private static final ImageIcon PRINT_CARDS_ICON          = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("print.cards.icon")));
    private static final ImageIcon PRINT_SELECTED_CARDS_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("print.cards.icon")));

    private final JLabel                             projectLabel           = new JLabel(PROJECT_LABEL);
    private final DefaultComboBoxModel<JiraProject>  projectsComboModel     = new DefaultComboBoxModel<JiraProject>();
    private final JComboBox<JiraProject>             projectsCombo          = new JComboBox<JiraProject>(projectsComboModel);
    private final DefaultListModel<JiraSprint>       sprintsListModel       = new DefaultListModel<JiraSprint>();
    private final JList<JiraSprint>                  sprintsList            = new JList<JiraSprint>(sprintsListModel);
    private final DefaultListModel<JiraCard>         cardsListModel         = new DefaultListModel<JiraCard>();
    private final JList<JiraCard>                    cardsList              = new JList<JiraCard>(cardsListModel);
    private final JButton                            printButton            = new JButton(PRINT_LABEL);
    private final JButton                            printSelectedButton    = new JButton(PRINT_SELECTED_LABEL);
    private final DefaultComboBoxModel<CardsPerPage> cardsPerPageComboModel = new DefaultComboBoxModel<CardsPerPage>(CardsPerPage.values());
    private final JComboBox<CardsPerPage>            cardsPerPageComboBox   = new JComboBox<CardsPerPage>(cardsPerPageComboModel);

    public ProjectsSwingView(final JiraCardPrinter jiraCardPrinter) {

        projectsCombo.setMaximumRowCount(10);
        projectsCombo.setRenderer(new JiraProjectCellRenderer());
        projectsCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                jiraCardPrinter.onProjectSelected((JiraProject) projectsCombo.getSelectedItem());
            }
        });

        sprintsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sprintsList.setCellRenderer(new JiraSprintListRenderer());
        sprintsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                jiraCardPrinter.onSprintSelected(sprintsList.getSelectedValue());
            }
        });

        cardsList.setCellRenderer(new JiraCardListRenderer());
        cardsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                jiraCardPrinter.onCardsSelected(cardsList.getSelectedValuesList());
            }
        });

        cardsPerPageComboBox.setPreferredSize(new Dimension(50, 30));
        cardsPerPageComboBox.setRenderer(new CardsPerPageListCellRenderer());
        cardsPerPageComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                jiraCardPrinter.onCardsPerPageSelected((CardsPerPage) e.getItem());
            }
        });

        printSelectedButton.setIcon(PRINT_SELECTED_CARDS_ICON);
        printSelectedButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jiraCardPrinter.onPrintSelected();
            }
        });

        printButton.setIcon(PRINT_CARDS_ICON);
        printButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jiraCardPrinter.onPrint();
            }
        });

        layoutComponent();
    }

    private void layoutComponent() {
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.add(new JLabel(CARDS_PER_PAGE_LABEL), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        buttonsPanel.add(cardsPerPageComboBox, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        buttonsPanel.add(printSelectedButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        buttonsPanel.add(printButton, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        setLayout(new GridBagLayout());

        JScrollPane sprintScroll = new JScrollPane(sprintsList);
        JScrollPane cardsScroll = new JScrollPane(cardsList);
        add(projectLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        add(projectsCombo, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        add(sprintScroll, new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        add(cardsScroll, new GridBagConstraints(2, 0, 1, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        add(buttonsPanel, new GridBagConstraints(0, 2, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
    }

    public void setProjects(List<JiraProject> projectsListData) {
        projectsComboModel.removeAllElements();
        for (JiraProject jiraProject : projectsListData) {
            projectsComboModel.addElement(jiraProject);
        }
    }

    public void setSprints(List<JiraSprint> sprintsList) {
        sprintsListModel.clear();
        for (JiraSprint jiraSprint : sprintsList) {
            sprintsListModel.addElement(jiraSprint);
        }
    }

    public void setCards(List<JiraCard> cardsList) {
        cardsListModel.clear();
        for (JiraCard jiraCard : cardsList) {
            cardsListModel.addElement(jiraCard);
        }
    }

    public void selectCardsPerPage(CardsPerPage selectedCardsPerPage) {
        cardsPerPageComboModel.setSelectedItem(selectedCardsPerPage);
    }

}
