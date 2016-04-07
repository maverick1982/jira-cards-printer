package it.maverick.jira.view.renderer;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.utils.CachedImages;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Pasquale on 09/03/2016.
 */
public class JiraCardListRenderer implements ListCellRenderer<JiraCard> {

    private static final CachedImages CACHED_IMAGES = new CachedImages();

    public Component getListCellRendererComponent(JList<? extends JiraCard> list, JiraCard value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setToolTipText(value.toString());

        ImageIcon typeImage = new ImageIcon(CACHED_IMAGES.getCachedImage(value.getTypeUrl()));
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
