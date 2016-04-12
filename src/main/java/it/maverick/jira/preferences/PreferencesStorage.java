package it.maverick.jira.preferences;

import it.maverick.jira.ConnectionProtocol;

import java.util.List;

/**
 * Created by Pasquale on 22/03/2016.
 */
public interface PreferencesStorage {

    List<String> getUsers();

    void removeUser(String user);

    void addUser(String user);

    List<String> getHosts();

    void removeHost(String host);

    void addHost(String host);

    void setProtocol(ConnectionProtocol connectionProtocol);

    ConnectionProtocol getProtocol();
}
