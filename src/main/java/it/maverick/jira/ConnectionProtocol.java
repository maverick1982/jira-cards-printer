package it.maverick.jira;

/**
 * Created by Pasquale on 12/04/2016.
 */
public enum ConnectionProtocol {
    HTTP("http"),
    HTTPS("https");

    private String protocolString;

    ConnectionProtocol(String protocolString) {
        this.protocolString = protocolString;
    }

    @Override
    public String toString() {
        return protocolString;
    }

    public String getFormattedString() {
        return protocolString + "://";
    }
}
