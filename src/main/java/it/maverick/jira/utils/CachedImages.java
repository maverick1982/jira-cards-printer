package it.maverick.jira.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * User: Pasquale
 * Date: 05/01/14
 * Time: 11.16
 */
public class CachedImages {

    private final HashMap<String, Image> imagesCache = new HashMap<String, Image>();

    public Image getCachedImage(String stringURL) {
        Image image = imagesCache.get(stringURL);
        if (image == null) {
            try {
                URL url = new URL(stringURL);
                image = ImageIO.read(url);
                imagesCache.put(stringURL, image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

}
