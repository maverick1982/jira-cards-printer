package it.maverick.jira;

import it.maverick.jira.preferences.PreferencesStorage;
import it.maverick.jira.preferences.UserPreferences;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pasquale on 22/03/2016.
 */
public class UserPreferencesTest {

    @Test
    public void testSetHttpProtocol() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.setProtocol(ConnectionProtocol.HTTP);

        assertEquals(ConnectionProtocol.HTTP, userPreferences.getProtocol());
    }

    @Test
    public void testSetHttpsProtocol() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.setProtocol(ConnectionProtocol.HTTPS);

        assertEquals(ConnectionProtocol.HTTPS, userPreferences.getProtocol());
    }

    @Test
    public void testAddOneUser() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addUser("user1");

        assertEquals(1, userPreferences.getUsers().size());
        assertEquals("user1", userPreferences.getUsers().get(0));
    }

    @Test
    public void testAddRepeatedUserWhenLimitIsFive() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addUser("user1");
        userPreferences.addUser("user1");

        assertEquals(1, userPreferences.getUsers().size());
        assertEquals("user1", userPreferences.getUsers().get(0));
    }

    @Test
    public void testAddRepeatedUserUpdateOrder() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addUser("user1");
        userPreferences.addUser("user2");
        userPreferences.addUser("user3");
        userPreferences.addUser("user1");

        assertEquals(3, userPreferences.getUsers().size());
        assertEquals("user2", userPreferences.getUsers().get(0));
        assertEquals("user3", userPreferences.getUsers().get(1));
        assertEquals("user1", userPreferences.getUsers().get(2));
    }

    @Test
    public void testAddTwoUserWhenLimitIsFive() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addUser("user1");
        userPreferences.addUser("user2");

        assertEquals(2, userPreferences.getUsers().size());
        assertEquals("user1", userPreferences.getUsers().get(0));
        assertEquals("user2", userPreferences.getUsers().get(1));
    }

    @Test
    public void testAddThreeUserWhenLimitIsTwo() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);
        userPreferences.addUser("user1");
        userPreferences.addUser("user2");
        userPreferences.addUser("user3");

        assertEquals(2, userPreferences.getUsers().size());
        assertEquals("user2", userPreferences.getUsers().get(0));
        assertEquals("user3", userPreferences.getUsers().get(1));
    }

    @Test
    public void testGetUsersFromStorage() throws Exception {
        List<String> users = Arrays.asList("user1", "user2");
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(users, new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);

        assertEquals(2, userPreferences.getUsers().size());
        assertEquals("user1", userPreferences.getUsers().get(0));
        assertEquals("user2", userPreferences.getUsers().get(1));
    }

    @Test
    public void testRemoveUsersFromStorageOnLoad() throws Exception {
        List<String> users = new ArrayList<String>(Arrays.asList("user1", "user2", "user3"));
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(users, new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);

        assertEquals(2, userPreferences.getUsers().size());
        assertEquals("user2", userPreferences.getUsers().get(0));
        assertEquals("user3", userPreferences.getUsers().get(1));
        assertEquals(2, preferencesStorage.getUsers().size());
        assertEquals("user2", preferencesStorage.getUsers().get(0));
        assertEquals("user3", preferencesStorage.getUsers().get(1));
    }

    @Test
    public void testAddUsersToStorage() throws Exception {
        List<String> users = new ArrayList<String>(Arrays.asList("user1", "user2"));
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(users, new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addUser("user3");

        assertEquals(3, userPreferences.getUsers().size());
        assertEquals("user1", userPreferences.getUsers().get(0));
        assertEquals("user2", userPreferences.getUsers().get(1));
        assertEquals("user3", userPreferences.getUsers().get(2));
        assertEquals(3, preferencesStorage.getUsers().size());
        assertEquals("user1", preferencesStorage.getUsers().get(0));
        assertEquals("user2", preferencesStorage.getUsers().get(1));
        assertEquals("user3", preferencesStorage.getUsers().get(2));
    }

    @Test
    public void testRemoveUsersFromStorageOnAdd() throws Exception {
        List<String> users = new ArrayList<String>(Arrays.asList("user1", "user2"));
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(users, new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);
        userPreferences.addUser("user3");

        assertEquals(2, userPreferences.getUsers().size());
        assertEquals("user2", userPreferences.getUsers().get(0));
        assertEquals("user3", userPreferences.getUsers().get(1));
        assertEquals(2, preferencesStorage.getUsers().size());
        assertEquals("user2", preferencesStorage.getUsers().get(0));
        assertEquals("user3", preferencesStorage.getUsers().get(1));
    }

    @Test
    public void testAddRepeatedUserUpdateStorageOrder() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addUser("user1");
        userPreferences.addUser("user2");
        userPreferences.addUser("user3");
        userPreferences.addUser("user1");

        assertEquals(3, preferencesStorage.getUsers().size());
        assertEquals("user2", preferencesStorage.getUsers().get(0));
        assertEquals("user3", preferencesStorage.getUsers().get(1));
        assertEquals("user1", preferencesStorage.getUsers().get(2));
    }

    @Test
    public void testAddOneHost() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addHost("host1");

        assertEquals(1, userPreferences.getHosts().size());
        assertEquals("host1", userPreferences.getHosts().get(0));
    }

    @Test
    public void testAddRepeatedHostWhenLimitIsFive() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addHost("host1");
        userPreferences.addHost("host1");

        assertEquals(1, userPreferences.getHosts().size());
        assertEquals("host1", userPreferences.getHosts().get(0));
    }

    @Test
    public void testAddRepeatedHostUpdateOrder() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addHost("host1");
        userPreferences.addHost("host2");
        userPreferences.addHost("host3");
        userPreferences.addHost("host1");

        assertEquals(3, userPreferences.getHosts().size());
        assertEquals("host2", userPreferences.getHosts().get(0));
        assertEquals("host3", userPreferences.getHosts().get(1));
        assertEquals("host1", userPreferences.getHosts().get(2));
    }

    @Test
    public void testAddTwoHostsWhenLimitIsFive() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addHost("host1");
        userPreferences.addHost("host2");

        assertEquals(2, userPreferences.getHosts().size());
        assertEquals("host1", userPreferences.getHosts().get(0));
        assertEquals("host2", userPreferences.getHosts().get(1));
    }

    @Test
    public void testAddThreeHostsWhenLimitIsTwo() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);
        userPreferences.addHost("host1");
        userPreferences.addHost("host2");
        userPreferences.addHost("host3");

        assertEquals(2, userPreferences.getHosts().size());
        assertEquals("host2", userPreferences.getHosts().get(0));
        assertEquals("host3", userPreferences.getHosts().get(1));
    }

    @Test
    public void testGetHttpProtocolFromStorage() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>(), ConnectionProtocol.HTTP);
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);

        assertEquals(ConnectionProtocol.HTTP, userPreferences.getProtocol());
    }

    @Test
    public void testGetHttpsProtocolFromStorage() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>(), ConnectionProtocol.HTTPS);
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);

        assertEquals(ConnectionProtocol.HTTPS, userPreferences.getProtocol());
    }

    @Test
    public void testGetHostFromStorage() throws Exception {
        List<String> hosts = Arrays.asList("host1", "host2");
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), hosts);
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);

        assertEquals(2, userPreferences.getHosts().size());
        assertEquals("host1", userPreferences.getHosts().get(0));
        assertEquals("host2", userPreferences.getHosts().get(1));
    }

    @Test
    public void testRemoveHostsFromStorageOnLoad() throws Exception {
        List<String> hosts = new ArrayList<String>(Arrays.asList("host1", "host2", "host3"));
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), hosts);
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);

        assertEquals(2, userPreferences.getHosts().size());
        assertEquals("host2", userPreferences.getHosts().get(0));
        assertEquals("host3", userPreferences.getHosts().get(1));
        assertEquals(2, preferencesStorage.getHosts().size());
        assertEquals("host2", preferencesStorage.getHosts().get(0));
        assertEquals("host3", preferencesStorage.getHosts().get(1));
    }

    @Test
    public void testAddHostToStorage() throws Exception {
        List<String> hosts = new ArrayList<String>(Arrays.asList("host1", "host2"));
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), hosts);
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addHost("host3");

        assertEquals(3, userPreferences.getHosts().size());
        assertEquals("host1", userPreferences.getHosts().get(0));
        assertEquals("host2", userPreferences.getHosts().get(1));
        assertEquals("host3", userPreferences.getHosts().get(2));
        assertEquals(3, preferencesStorage.getHosts().size());
        assertEquals("host1", preferencesStorage.getHosts().get(0));
        assertEquals("host2", preferencesStorage.getHosts().get(1));
        assertEquals("host3", preferencesStorage.getHosts().get(2));
    }

    @Test
    public void testRemoveHostsFromStorageOnAdd() throws Exception {
        List<String> hosts = new ArrayList<String>(Arrays.asList("host1", "host2"));
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), hosts);
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 2);
        userPreferences.addHost("host3");

        assertEquals(2, userPreferences.getHosts().size());
        assertEquals("host2", userPreferences.getHosts().get(0));
        assertEquals("host3", userPreferences.getHosts().get(1));
        assertEquals(2, preferencesStorage.getHosts().size());
        assertEquals("host2", preferencesStorage.getHosts().get(0));
        assertEquals("host3", preferencesStorage.getHosts().get(1));
    }

    @Test
    public void testAddRepeatedHostUpdateStorageOrder() throws Exception {
        PreferencesStorage preferencesStorage = new FakePreferencesStorage(new ArrayList<String>(), new ArrayList<String>());
        UserPreferences userPreferences = new UserPreferences(preferencesStorage, 5);
        userPreferences.addHost("host1");
        userPreferences.addHost("host2");
        userPreferences.addHost("host3");
        userPreferences.addHost("host1");

        assertEquals(3, preferencesStorage.getHosts().size());
        assertEquals("host2", preferencesStorage.getHosts().get(0));
        assertEquals("host3", preferencesStorage.getHosts().get(1));
        assertEquals("host1", preferencesStorage.getHosts().get(2));
    }

    private class FakePreferencesStorage implements PreferencesStorage {

        private List<String>       users;
        private List<String>       hosts;
        private ConnectionProtocol connectionProtocol;

        public FakePreferencesStorage(List<String> users, List<String> hosts) {
            this(users, hosts, ConnectionProtocol.HTTP);
        }

        public FakePreferencesStorage(List<String> users, List<String> hosts, ConnectionProtocol protocol) {
            this.users = users;
            this.hosts = hosts;
            this.connectionProtocol = protocol;
        }

        public synchronized List<String> getUsers() {
            return users;
        }

        public synchronized void removeUser(String user) {
            users.remove(user);
        }

        public void addUser(String user) {
            users.add(user);
        }

        public List<String> getHosts() {
            return hosts;
        }

        public void removeHost(String host) {
            hosts.remove(host);
        }

        public void addHost(String host) {
            hosts.add(host);
        }

        public void setProtocol(ConnectionProtocol connectionProtocol) {
            this.connectionProtocol = connectionProtocol;
        }

        public ConnectionProtocol getProtocol() {
            return connectionProtocol;
        }
    }
}