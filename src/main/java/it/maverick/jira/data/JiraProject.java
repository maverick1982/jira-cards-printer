package it.maverick.jira.data;

/**
 * User: Pasquale
 * Date: 03/01/14
 * Time: 23.10
 */
public class JiraProject {

    private final int    id;
    private final String name;

    public JiraProject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name + " [id:" + this.id + "]";
    }
}
