package it.esteco;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: Pasquale
 * Date: 03/01/14
 * Time: 23.15
 */
public class JiraCard {

    private int    id;
    private String key;
    private String typeName;
    private String summary;
    private String typeUrl;
    private String priorityUrl;
    private String priorityName;
    private double storyPoints;
    private String statusName;
    private String statusUrl;

    public JiraCard(int id, String key, String summary) {
        this.id = id;
        this.key = key;
        this.summary = summary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTypeUrl() {
        return typeUrl;
    }

    public void setTypeUrl(String typeUrl) {
        this.typeUrl = typeUrl;
    }

    public String getPriorityUrl() {
        return priorityUrl;
    }

    public void setPriorityUrl(String priorityUrl) {
        this.priorityUrl = priorityUrl;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public double getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(double storyPoints) {
        this.storyPoints = storyPoints;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public Image getTypeImage() {
        try {
            URL url = new URL(typeUrl);
            Image image = ImageIO.read(url);
            return image;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return this.key + " - " + this.summary + "[id:" + this.id + "]";
    }

}
