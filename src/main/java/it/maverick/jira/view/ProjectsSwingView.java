package it.maverick.jira.view;

import it.maverick.jira.JiraCardPrinter;
import it.maverick.jira.data.JiraCard;
import it.maverick.jira.data.JiraProject;
import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.utils.CachedImages;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

/**
 * User: Pasquale
 * Date: 04/01/14
 * Time: 12.08
 */
public class ProjectsSwingView extends JPanel {

    private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("it.maverick.jira.resources");
    private static final ClassLoader CLASS_LOADER = ProjectsSwingView.class.getClassLoader();

    private static final String PRINT_LABEL = RESOURCES.getString("print.all.cards.label");
    private static final String PRINT_SELECTED_LABEL = RESOURCES.getString("print.selected.cards.label");
    private static final ImageIcon PRINT_CARDS_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("print.cards.icon")));
    private static final ImageIcon PRINT_SELECTED_CARDS_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("print.cards.icon")));

    private static final CachedImages CACHED_IMAGES = new CachedImages();
    private JLabel projectLabel = new JLabel("Project");
    private JComboBox<JiraProject> projectsCombo;
    private DefaultComboBoxModel<JiraProject> projectsComboModel = new DefaultComboBoxModel<JiraProject>();
    private JList<JiraProject> projectsList;
    private DefaultListModel<JiraProject> projectsListModel = new DefaultListModel<JiraProject>();
    private JList<JiraSprint> sprintsList;
    private DefaultListModel<JiraSprint> sprintsListModel = new DefaultListModel<JiraSprint>();
    private JList<JiraCard> cardsList;
    private DefaultListModel<JiraCard> cardsListModel = new DefaultListModel<JiraCard>();
    private JButton printButton;
    private JButton printSelectedButton;
    private final JiraCardPrinter jiraCardPrinter;

    public ProjectsSwingView(final JiraCardPrinter jiraCardPrinter) {
        this.jiraCardPrinter = jiraCardPrinter;

        projectsCombo = new JComboBox<JiraProject>(projectsComboModel);
        projectsCombo.setRenderer(new JiraProjectCellRenderer());
        projectsCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (projectsCombo.getSelectedItem() != null) {

                    jiraCardPrinter.onProjectSelected(((JiraProject) projectsCombo.getSelectedItem()).getId());
                }
            }
        });
        projectsList = new JList<JiraProject>(projectsListModel);
        projectsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!projectsList.isSelectionEmpty()) {
                    jiraCardPrinter.onProjectSelected(projectsList.getSelectedValue().getId());
                }
            }
        });

        sprintsList = new JList<JiraSprint>(sprintsListModel);
        sprintsList.setCellRenderer(new JiraSprintListRenderer());
        sprintsList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (projectsCombo.getSelectedItem() != null && !sprintsList.isSelectionEmpty()) {
                    jiraCardPrinter.onSprintSelected(((JiraProject) projectsCombo.getSelectedItem()).getId(), sprintsList.getSelectedValue().getId());
                }
            }
        });

        cardsList = new JList<JiraCard>(cardsListModel);
        cardsList.setCellRenderer(new JiraCardListRenderer());

        printSelectedButton = new JButton(PRINT_SELECTED_LABEL);
        printSelectedButton.setIcon(PRINT_SELECTED_CARDS_ICON);
        printSelectedButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jiraCardPrinter.onPrint(new ArrayList<JiraCard>(cardsList.getSelectedValuesList()));
            }
        });

        printButton = new JButton(PRINT_LABEL);
        printButton.setIcon(PRINT_CARDS_ICON);
        printButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jiraCardPrinter.onPrint(Collections.list(cardsListModel.elements()));
            }
        });

        layoutComponent();
    }

    private void layoutComponent() {
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.add(printSelectedButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        buttonsPanel.add(printButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        setLayout(new GridBagLayout());

        JScrollPane projectsScroll = new JScrollPane(projectsList);
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
        projectsListModel.clear();
        for (JiraProject jiraProject : projectsListData) {
            projectsListModel.addElement(jiraProject);
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

    private static class JiraProjectCellRenderer implements ListCellRenderer<JiraProject> {

        public Component getListCellRendererComponent(JList<? extends JiraProject> list, JiraProject value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new GridBagLayout());
            JLabel nameLabel = new JLabel();
            if (value != null) {
                nameLabel = new JLabel(value.getName());
            }
            if (isSelected) {
                panel.setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
            } else {
                panel.setBackground(Color.WHITE);
                nameLabel.setForeground(list.getForeground());
            }
            panel.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
            return panel;
        }
    }

    private static class JiraSprintListRenderer implements ListCellRenderer<JiraSprint> {

        private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("it.maverick.jira.resources");
        private static final ClassLoader CLASS_LOADER = ProjectsSwingView.class.getClassLoader();

        private static final ImageIcon SPRINT_ACTIVE_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("sprint.active.icon")));
        private static final ImageIcon SPRINT_CLOSED_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("sprint.closed.icon")));

        private static final Map<String, ImageIcon> STATUS_ICON_MAP = new HashMap<String, ImageIcon>();

        static {
            STATUS_ICON_MAP.put("ACTIVE", SPRINT_ACTIVE_ICON);
            STATUS_ICON_MAP.put("CLOSED", SPRINT_CLOSED_ICON);
        }

        public Component getListCellRendererComponent(JList<? extends JiraSprint> list, JiraSprint value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setToolTipText(value.toString());

            JLabel typeImageLabel = new JLabel();
            typeImageLabel.setIcon(STATUS_ICON_MAP.get(value.getState()));

            JLabel nameLabel = new JLabel(value.getName());
            if (isSelected) {
                panel.setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
            } else {
                panel.setBackground(Color.WHITE);
                nameLabel.setForeground(list.getForeground());
            }

            panel.add(typeImageLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
            panel.add(nameLabel, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 4), 0, 0));
            return panel;
        }
    }

    private static class JiraCardListRenderer implements ListCellRenderer<JiraCard> {

        public Component getListCellRendererComponent(JList<? extends JiraCard> list, JiraCard value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setToolTipText(value.toString());

            ImageIcon typeImage = new ImageIcon(value.getTypeImage());
            JLabel typeImageLabel = new JLabel();
            typeImageLabel.setIcon(typeImage);

            ImageIcon priorityImage = new ImageIcon(CACHED_IMAGES.getCachedImage(value.getPriorityUrl()));
            JLabel priorityImageLabel = new JLabel();
            priorityImageLabel.setIcon(priorityImage);

            JLabel nameLabel = new JLabel(value.getKey() + " - " + value.getSummary());
            if (isSelected) {
                panel.setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
            } else {
                panel.setBackground(Color.WHITE);
                nameLabel.setForeground(list.getForeground());
            }

            panel.add(typeImageLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
            panel.add(priorityImageLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 4, 2, 4), 0, 0));
            panel.add(nameLabel, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
            return panel;
        }
    }

}
