package it.maverick.jira.view;

import it.maverick.jira.JiraCardPrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * User: Pasquale
 * Date: 05/01/14
 * Time: 17.35
 */
public class ServerSwingView extends JPanel {

    private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("it.maverick.jira.resources");
    private static final ClassLoader CLASS_LOADER = ServerSwingView.class.getClassLoader();

    private static final String HOST_LABEL = RESOURCES.getString("host.label");
    private static final String USER_LABEL = RESOURCES.getString("username.label");
    private static final String PASSWORD_LABEL = RESOURCES.getString("password.label");
    private static final String CONNECT_LABEL = RESOURCES.getString("connect.label");
    private static final String DISCONNECT_LABEL = RESOURCES.getString("disconnect.label");
    private static final String DEFAULT_HOST = RESOURCES.getString("default.host.url");
    private static final ImageIcon CONNECT_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("connect.server.icon")));
    private static final ImageIcon DISCONNECT_ICON = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("disconnect.server.icon")));

    private final JLabel hostLabel;
    private final JLabel userLabel;
    private final JLabel passwordLabel;
    private final JTextField host;
    private final JTextField user;
    private final JTextField password;
    private final JButton connectButton;
    private final JButton disconnectButton;

    public ServerSwingView(final JiraCardPrinter jiraCardPrinter) {

        AbstractAction connectAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jiraCardPrinter.connect(host.getText(), user.getText(), password.getText());
            }
        };

        hostLabel = new JLabel(HOST_LABEL);
        host = new JTextField(DEFAULT_HOST);
        host.setPreferredSize(new Dimension(200, host.getPreferredSize().height));
        userLabel = new JLabel(USER_LABEL);
        user = new JTextField();
        user.setPreferredSize(new Dimension(200, user.getPreferredSize().height));
        user.addActionListener(connectAction);
        passwordLabel = new JLabel(PASSWORD_LABEL);
        password = new JPasswordField();
        password.setPreferredSize(new Dimension(200, password.getPreferredSize().height));
        password.addActionListener(connectAction);

        connectButton = new JButton(CONNECT_LABEL);
        connectButton.setIcon(CONNECT_ICON);
        connectButton.addActionListener(connectAction);

        disconnectButton = new JButton(DISCONNECT_LABEL);
        disconnectButton.setIcon(DISCONNECT_ICON);
        disconnectButton.setEnabled(false);
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
