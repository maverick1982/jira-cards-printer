package it.maverick.jira.view.renderer;

import it.maverick.jira.print.CardsPerPage;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Pasquale on 10/03/2016.
 */
public class CardsPerPageListCellRenderer implements ListCellRenderer<CardsPerPage> {

    public Component getListCellRendererComponent(JList<? extends CardsPerPage> list, CardsPerPage value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel jLabel = new JLabel(String.valueOf(value.getCardsPerPageInt()));
        jLabel.setOpaque(true);
        jLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        if (isSelected) {
            jLabel.setBackground(list.getSelectionBackground());
            jLabel.setForeground(list.getSelectionForeground());
        } else {
            jLabel.setBackground(Color.WHITE);
            jLabel.setForeground(list.getForeground());
        }

        return jLabel;
    }
}
