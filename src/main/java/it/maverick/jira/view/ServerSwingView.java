package it.maverick.jira.view;

import it.maverick.jira.ConnectionProtocol;
import it.maverick.jira.JiraCardPrinter;
import it.maverick.jira.view.renderer.ConnectionProtocolCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
    private static final ImageIcon CONNECT_ICON     = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("connect.server.icon")));
    private static final ImageIcon DISCONNECT_ICON  = new ImageIcon(CLASS_LOADER.getResource(RESOURCES.getString("disconnect.server.icon")));

    private final DefaultComboBoxModel<ConnectionProtocol> protocolComboModel = new DefaultComboBoxModel<ConnectionProtocol>();
    private final JComboBox<ConnectionProtocol>            protocolComboBox   = new JComboBox<ConnectionProtocol>(protocolComboModel);
    private final JLabel                                   hostLabel          = new JLabel(HOST_LABEL);
    private final JLabel                                   userLabel          = new JLabel(USER_LABEL);
    private final JLabel                                   passwordLabel      = new JLabel(PASSWORD_LABEL);
    private final JTextField                               host               = new JTextField();
    private final JTextField                               user               = new JTextField();
    private final JTextField                               password           = new JPasswordField();
    private final JButton                                  connectButton      = new JButton(CONNECT_LABEL);
    private final JButton                                  disconnectButton   = new JButton(DISCONNECT_LABEL);

    private final AbstractAction connectAction;

    public ServerSwingView(final JiraCardPrinter jiraCardPrinter) {

        connectAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ConnectionProtocol selectedProtocol = protocolComboBox.getItemAt(protocolComboBox.getSelectedIndex());
                jiraCardPrinter.connect(selectedProtocol, host.getText(), user.getText(), password.getText());
            }
        };

        protocolComboBox.setRenderer(new ConnectionProtocolCellRenderer());
        protocolComboModel.addElement(ConnectionProtocol.HTTPS);
        protocolComboModel.addElement(ConnectionProtocol.HTTP);

        configureTextField(host);
        configureTextField(user);
        configureTextField(password);

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

    private void configureTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(170, textField.getPreferredSize().height));
        textField.addActionListener(connectAction);
        setSelectionOnFocus(textField);
    }

    private void setSelectionOnFocus(final JTextField textField) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                textField.setSelectionStart(0);
                textField.setSelectionEnd(textField.getText().length());
            }
        });
    }

    private void layoutComponent() {
        setLayout(new GridBagLayout());
        add(hostLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(protocolComboBox, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(host, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(userLabel, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(user, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(passwordLabel, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(password, new GridBagConstraints(6, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        add(connectButton, new GridBagConstraints(7, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        add(disconnectButton, new GridBagConstraints(8, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    }

    public void setConnectionProtocolEnabled(boolean enabled) {
        protocolComboBox.setEnabled(enabled);
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

    public void setUsersForHint(List<String> users) {
        Autocomplete autocomplete = new Autocomplete(user, users);
        user.getDocument().addDocumentListener(autocomplete);
    }

    public void setHostsForHint(List<String> hosts) {
        Autocomplete autocomplete = new Autocomplete(host, hosts);
        host.getDocument().addDocumentListener(autocomplete);
    }

    public void setUser(String user) {
        this.user.setText(user);
    }

    public void setHost(String host) {
        this.host.setText(host);
    }

    public void setConnectionProtocol(ConnectionProtocol connectionProtocol) {
        protocolComboBox.setSelectedItem(connectionProtocol);
    }
}
