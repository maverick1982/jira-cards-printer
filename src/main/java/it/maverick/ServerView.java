package it.maverick;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * User: Pasquale
 * Date: 05/01/14
 * Time: 17.35
 */
public class ServerView extends JPanel {

    private final ResourceBundle resources   = ResourceBundle.getBundle("it.maverick.resources");
    private final ClassLoader    classLoader = ServerView.class.getClassLoader();

    private JiraServer jiraServer;

    private static final String    HOST_LABEL          = "Host:";
    private static final String    USER_LABEL          = "User:";
    private static final String    PASSORD_LABEL       = "Password";
    private static final String    CONNECT_LABEL       = "Connect";
    private static final String    DISCONNECT_LABEL    = "Disconnect";
    private static final String    DEFAULT_HOST        = "jira.esteco.com";
    private static final String    CONNECT_ICON_URL    = "connect.server.icon";
    private static final String    DISCONNECT_ICON_URL = "disconnect.server.icon";
    private final        ImageIcon CONNECT_ICON        = new ImageIcon(classLoader.getResource(resources.getString(CONNECT_ICON_URL)));
    private final        ImageIcon DISCONNECT_ICON     = new ImageIcon(classLoader.getResource(resources.getString(DISCONNECT_ICON_URL)));
    private final JLabel     hostLabel;
    private final JLabel     userLabel;
    private final JLabel     passwordLabel;
    private final JTextField host;
    private final JTextField user;
    private final JTextField password;
    private final JButton    connectButton;
    private final JButton    disconnectButton;

    public ServerView(JiraServer jiraServer) {
        this.jiraServer = jiraServer;

        hostLabel = new JLabel(HOST_LABEL);
        host = new JTextField(DEFAULT_HOST);
        host.setPreferredSize(new Dimension(200, host.getPreferredSize().height));
        userLabel = new JLabel(USER_LABEL);
        user = new JTextField();
        user.setPreferredSize(new Dimension(200, user.getPreferredSize().height));
        passwordLabel = new JLabel(PASSORD_LABEL);
        password = new JPasswordField();
        password.setPreferredSize(new Dimension(200, password.getPreferredSize().height));

        connectButton = new JButton(CONNECT_LABEL);
        connectButton.setIcon(CONNECT_ICON);
        connectButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerView.this.jiraServer.setHost(host.getText());
                ServerView.this.jiraServer.setUser(user.getText());
                ServerView.this.jiraServer.setPassword(password.getText());
                ServerView.this.jiraServer.createConnection();
            }
        });

        disconnectButton = new JButton(DISCONNECT_LABEL);
        disconnectButton.setIcon(DISCONNECT_ICON);
        disconnectButton.setEnabled(false);
        disconnectButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerView.this.jiraServer.closeConnection();
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

    public void onServerConnected() {
        host.setEnabled(false);
        user.setEnabled(false);
        password.setEnabled(false);
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
    }

    public void onServerDisconnected() {
        host.setEnabled(true);
        user.setEnabled(true);
        password.setEnabled(true);
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
    }
}
