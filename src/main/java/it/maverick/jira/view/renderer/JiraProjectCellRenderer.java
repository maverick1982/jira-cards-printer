package it.maverick.jira.view.renderer;

import it.maverick.jira.data.JiraProject;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Pasquale on 09/03/2016.
 */
public class JiraProjectCellRenderer implements ListCellRenderer<JiraProject> {

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
