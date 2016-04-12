package it.maverick.jira.view.renderer;

import it.maverick.jira.ConnectionProtocol;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Pasquale on 09/03/2016.
 */
public class ConnectionProtocolCellRenderer implements ListCellRenderer<ConnectionProtocol> {

    public Component getListCellRendererComponent(JList<? extends ConnectionProtocol> list, ConnectionProtocol value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel nameLabel = new JLabel();
        if (value != null) {
            nameLabel = new JLabel(value.getFormattedString());
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
