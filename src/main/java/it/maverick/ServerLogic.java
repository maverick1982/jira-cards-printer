package it.maverick;

/**
 * User: Pasquale
 * Date: 05/01/14
 * Time: 17.49
 */
public class ServerLogic {

    ServerView serverView;
    private JiraServer jiraServer;

    public ServerLogic(JiraServer jiraServer) {
        this.jiraServer = jiraServer;
    }

    public ServerView getServerView() {
        return serverView;
    }

    public void setServerView(ServerView serverView) {
        this.serverView = serverView;
    }

    public void onConnection() {
        jiraServer.createConnection();
    }

}
