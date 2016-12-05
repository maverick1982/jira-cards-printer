package com.jira;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jira.data.JiraUser;
import com.qrcodereader.R;

import java.util.concurrent.ExecutionException;

public class ConnectionJiraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jira);

        String username = getBaseContext().getString(R.string.username);
        String password = getBaseContext().getString(R.string.password);

        JiraInformationDownload jiraInformationDownload = new JiraInformationDownload(username, password);

        AsyncTask<String, Void, String> execute = jiraInformationDownload.execute("MF-10178");
        try {
            Toast.makeText(getBaseContext(),execute.get(), Toast.LENGTH_LONG).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
