package it.maverick.jira;

import it.maverick.jira.data.JiraUser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Created by Pasquale on 29/02/2016.
 */
public class DefaultJiraServer implements JiraServer {

    private final HttpHost targetHost;

    public DefaultJiraServer(ConnectionProtocol connectionProtocol, String host) {
        targetHost = new HttpHost(host, 443, connectionProtocol.toString());
    }

    public JiraServerConnection createConnection(JiraUser jiraUser) {

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(jiraUser.getName(), jiraUser.getPassword()));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        return new DefaultJiraServerConnection(httpClient, targetHost, localContext);
    }
}
