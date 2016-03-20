package it.maverick.jira.data;

/**
 * Created by Pasquale on 29/02/2016.
 */
public class JiraUser {

    private final String name;
    private final String password;

    public JiraUser(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
