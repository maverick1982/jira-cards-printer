package it.maverick.jira.view.renderer;

import it.maverick.jira.data.JiraCard;
import it.maverick.jira.utils.CachedImages;
import it.maverick.jira.view.ProjectsSwingView;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Created by Pasquale on 09/03/2016.
 */
public class JiraCardListRenderer implements ListCellRenderer<JiraCard> {

    private static final ResourceBundle RESOURCES    = ResourceBundle.getBundle("it.maverick.jira.resources");
    private static final ClassLoader    CLASS_LOADER = ProjectsSwingView.class.getClassLoader();

    private static final ImageIcon UNKNOWN_CARD_TYPE_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("unknown.card.type.icon")));

    private static final CachedImages CACHED_IMAGES = new CachedImages();

    public Component getListCellRendererComponent(JList<? extends JiraCard> list, JiraCard value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setToolTipText(value.toString());

        JLabel typeImageLabel = new JLabel();
        Image cachedImage = CACHED_IMAGES.getCachedImage(value.getTypeUrl());
        if(cachedImage != null) {
            ImageIcon typeImage = new ImageIcon(cachedImage);
            typeImageLabel.setIcon(typeImage);
        } else {
            typeImageLabel.setIcon(UNKNOWN_CARD_TYPE_ICON);
        }

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
