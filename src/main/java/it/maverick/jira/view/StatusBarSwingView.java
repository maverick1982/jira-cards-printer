package it.maverick.jira.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Pasquale on 09/03/2016.
 */
public class StatusBarSwingView extends JPanel {

    private final JLabel connectionStatusLabel         = new JLabel();
    private final JLabel cardsStatusLabel              = new JLabel();
    private final JLabel cardsSelectionStatusLabel     = new JLabel();
    private final JLabel pagesCountStatusLabel         = new JLabel();
    private final JLabel pagesCountSelectedStatusLabel = new JLabel();

    public StatusBarSwingView() {
        setLayout(new GridLayout(1, 0));

        JPanel statusPanel = createStatusBarPanel(FlowLayout.LEADING);
        statusPanel.add(connectionStatusLabel);

        JPanel allCardsPanel = createStatusBarPanel(FlowLayout.CENTER);
        allCardsPanel.add(cardsStatusLabel);
        allCardsPanel.add(pagesCountStatusLabel);

        JPanel selectedCardsPanel = createStatusBarPanel(FlowLayout.TRAILING);
        selectedCardsPanel.add(cardsSelectionStatusLabel);
        selectedCardsPanel.add(pagesCountSelectedStatusLabel);

        add(statusPanel);
        add(allCardsPanel);
        add(selectedCardsPanel);
    }

    private JPanel createStatusBarPanel(int align) {
        JPanel statusBarPanel = new JPanel(new FlowLayout(align, 0, 2));
        statusBarPanel.setBorder(BorderFactory.createEtchedBorder());
        return statusBarPanel;
    }

    public void setConnectionStatus(String connectionStatus) {
        connectionStatusLabel.setText(connectionStatus);
    }

    public void setCardsStatus(String cardsStatus) {
        cardsStatusLabel.setText(cardsStatus);
    }

    public void setCardsSelectionStatus(String cardsSelectionStatus) {
        cardsSelectionStatusLabel.setText(cardsSelectionStatus);
    }

    public void setPagesCountStatus(String pagesCountStatus) {
        pagesCountStatusLabel.setText(pagesCountStatus);
    }

    public void setPagesCountSelectedStatus(String pagesCountSelectedStatus) {
        pagesCountSelectedStatusLabel.setText(pagesCountSelectedStatus);
    }
}
