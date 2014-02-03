package it.esteco;

/**
 * User: Pasquale
 * Date: 03/01/14
 * Time: 23.12
 */
public class JiraSprint {

    private final int    id;
    private final String name;
    private final String state;

    public JiraSprint(int id, String name, String state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.state + ") [id:" + this.id + "]";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
