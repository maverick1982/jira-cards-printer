package it.maverick.jira;

import it.maverick.jira.view.JiraCardPrinterSwingView;
import it.maverick.jira.view.JiraCardPrinterView;

import javax.swing.*;

/**
 * User: Pasquale
 * Date: 20/12/13
 * Time: 22.21
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.setProperty("jsse.enableSNIExtension", "false");
                JiraCardPrinter jiraCardPrinter = new JiraCardPrinter();
                JiraCardPrinterView jiraCardPrinterView = new JiraCardPrinterSwingView(jiraCardPrinter);
                jiraCardPrinter.installView(jiraCardPrinterView);
                jiraCardPrinter.run();
            }
        });
    }
}
