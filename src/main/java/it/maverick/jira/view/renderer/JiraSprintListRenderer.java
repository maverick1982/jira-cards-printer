package it.maverick.jira.view.renderer;

import it.maverick.jira.data.JiraSprint;
import it.maverick.jira.view.ProjectsSwingView;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Pasquale on 09/03/2016.
 */
public class JiraSprintListRenderer implements ListCellRenderer<JiraSprint> {

    private static final ResourceBundle RESOURCES    = ResourceBundle.getBundle("it.maverick.jira.resources");
    private static final ClassLoader    CLASS_LOADER = ProjectsSwingView.class.getClassLoader();

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
