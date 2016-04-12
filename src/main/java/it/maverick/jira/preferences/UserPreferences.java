package it.maverick.jira.preferences;

import it.maverick.jira.ConnectionProtocol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pasquale on 21/03/2016.
 */
public class UserPreferences {

    private final List<String>       users;
    private final PreferencesStorage preferencesStorage;
    private final List<String>       hosts;
    private       ConnectionProtocol lastProtocol;

    public UserPreferences(PreferencesStorage preferencesStorage, int historyLimit) {
        this.preferencesStorage = preferencesStorage;
        users = new LimitedQueue<String>(historyLimit);
        hosts = new LimitedQueue<String>(historyLimit);
        for (String userName : preferencesStorage.getUsers()) {
            users.add(userName);
        }

        for (String host : preferencesStorage.getHosts()) {
            hosts.add(host);
        }

        lastProtocol = preferencesStorage.getProtocol();

        updateStorage(preferencesStorage);
    }

    private void updateStorage(PreferencesStorage preferencesStorage) {
        List<String> toRemoveNames = new ArrayList<String>();
        for (String userName : preferencesStorage.getUsers()) {
            if (!users.contains(userName)) {
                toRemoveNames.add(userName);
            }
        }
        for (String toRemoveName : toRemoveNames) {
            preferencesStorage.removeUser(toRemoveName);
        }

        List<String> toRemoveHosts = new ArrayList<String>();
        for (String host : preferencesStorage.getHosts()) {
            if (!hosts.contains(host)) {
                toRemoveHosts.add(host);
            }
        }
        for (String toRemoveHost : toRemoveHosts) {
            preferencesStorage.removeHost(toRemoveHost);
        }

        preferencesStorage.setProtocol(lastProtocol);
    }

    public void addUser(String userName) {
        if (users.contains(userName)) {
            users.remove(userName);
            updateStorage(preferencesStorage);
        }
        users.add(userName);
        preferencesStorage.addUser(userName);
        updateStorage(preferencesStorage);
    }

    public List<String> getUsers() {
        return users;
    }

    public void addHost(String host) {
        if (hosts.contains(host)) {
            hosts.remove(host);
            updateStorage(preferencesStorage);
        }
        hosts.add(host);
        preferencesStorage.addHost(host);
        updateStorage(preferencesStorage);
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setProtocol(ConnectionProtocol connectionProtocol) {
        lastProtocol = connectionProtocol;
        updateStorage(preferencesStorage);
    }

    public ConnectionProtocol getProtocol() {
        return lastProtocol;
    }

    private static class LimitedQueue<T> extends LinkedList<T> {
        private final int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(T o) {
            super.add(o);
            while (size() > limit) {
                super.remove();
            }
            return true;
        }
    }
}
