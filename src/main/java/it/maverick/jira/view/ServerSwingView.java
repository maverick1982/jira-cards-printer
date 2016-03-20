package it.maverick.jira.view;

import it.maverick.jira.JiraCardPrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * User: Pasquale
 * Date: 05/01/14
 * Time: 17.35
 */
public class ServerSwingView extends JPanel {

    private static final ResourceBundle RESOURCES    = ResourceBundle.getBundle("it.maverick.jira.resources");
    private static final ClassLoader    CLASS_LOADER = ServerSwingView.class.getClassLoader();

    private static final String    HOST_LABEL       = RESOURCES.getString("host.label");
    private static final String    USER_LABEL       = RESOURCES.getString("username.label");
    private static final String    PASSWORD_LABEL   = RESOURCES.getString("password.label");
    private static final String    CONNECT_LABEL    = RESOURCES.getString("connect.label");
    private static final String    DISCONNECT_LABEL = RESOURCES.getString("disconnect.label");
    private static final String    DEFAULT_HOST     = RESOURCES.getString("default.host.url");
    private static final ImageIcon CONNECT_ICON     = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("connect.server.icon")));
    private static final ImageIcon DISCONNECT_ICON  = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("disconnect.server.icon")));

    private final JLabel     hostLabel        = new JLabel(HOST_LABEL);
    private final JLabel     userLabel        = new JLabel(USER_LABEL);
    private final JLabel     passwordLabel    = new JLabel(PASSWORD_LABEL);
    private final JTextField host             = new JTextField(DEFAULT_HOST);
    private final JTextField user             = new JTextField();
    private final JTextField password         = new JPasswordField();
    private final JButton    connectButton    = new JButton(CONNECT_LABEL);
    private final JButton    disconnectButton = new JButton(DISCONNECT_LABEL);

    public ServerSwingView(final JiraCardPrinter jiraCardPrinter) {

        AbstractAction connectAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jiraCardPrinter.connect(host.getText(), user.getText(), password.getText());
            }
        };

        host.setPreferredSize(new Dimension(200, host.getPreferredSize().height));

        user.setPreferredSize(new Dimension(200, user.getPreferredSize().height));
        user.addActionListener(connectAction);

        List<String> users = new ArrayList<String>();
        Autocomplete autocomplete = new Autocomplete(user, users);
        user.getDocument().addDocumentListener(autocomplete);

        password.setPreferredSize(new Dimension(200, password.getPreferredSize().height));
        password.addActionListener(connectAction);

        connectButton.setIcon(CONNECT_ICON);
        connectButton.addActionListener(connectAction);

        disconnectButton.setIcon(DISCONNECT_ICON);
        disconnectButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jiraCardPrinter.disconnect();
            }
        });

        layoutComponent();
    }

    private void layoutComponent() {
        setLayout(new GridBagLayout());
        add(hostLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(host, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(userLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(user, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(passwordLabel, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(password, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(connectButton, new GridBagConstraints(6, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        add(disconnectButton, new GridBagConstraints(7, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    }

    public void setHostEnabled(boolean enabled) {
        host.setEnabled(enabled);
    }

    public void setUserEnabled(boolean enabled) {
        user.setEnabled(enabled);
    }

    public void setPasswordEnabled(boolean enabled) {
        password.setEnabled(enabled);
    }

    public void setConnectButtonEnabled(boolean enabled) {
        connectButton.setEnabled(enabled);
    }

    public void setDisconnectButtonEnabled(boolean enabled) {
        disconnectButton.setEnabled(enabled);
    }
}
