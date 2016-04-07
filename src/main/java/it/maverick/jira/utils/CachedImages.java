package it.maverick.jira.utils;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
                DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
                MyTransCoder transcoder = new MyTransCoder();
                TranscodingHints hints = new TranscodingHints();
                hints.put(ImageTranscoder.KEY_WIDTH, 16F);
                hints.put(ImageTranscoder.KEY_HEIGHT, 16F);
                hints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION, impl);
                hints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
                hints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
                hints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, SVGConstants.SVG_SVG_TAG);
                hints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, false);
                transcoder.setTranscodingHints(hints);
                TranscoderInput ti = new TranscoderInput(new URL(stringURL).toURI().toString());
                transcoder.transcode(ti, null);
                image = transcoder.getImage();
                imagesCache.put(stringURL, image);
            } catch (TranscoderException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    /**
     * Created by Pasquale on 07/04/2016.
     */
    public static class MyTransCoder extends ImageTranscoder {

        private BufferedImage image = null;

        public BufferedImage createImage(int w, int h) {
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            return image;
        }

        public void writeImage(BufferedImage img, TranscoderOutput out) {
        }

        public BufferedImage getImage() {
            return image;
        }
    }
}
