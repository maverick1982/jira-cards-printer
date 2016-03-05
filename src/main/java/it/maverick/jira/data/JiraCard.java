package it.maverick.jira.data;

import it.maverick.jira.utils.CachedImages;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * User: Pasquale
 * Date: 03/01/14
 * Time: 23.15
 */
public class JiraCard {

    private final int id;
    private final String key;
    private final String summary;
    private String typeName;
    private String typeUrl;
    private String priorityUrl;
    private String priorityName;
    private double storyPoints;
    private String statusName;
    private String statusUrl;
    private Image typeImage;

    public JiraCard(int id, String key, String summary) {
        this.id = id;
        this.key = key;
        this.summary = summary;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
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
        if (typeImage == null) {
            try {
                URL url = new URL(typeUrl);
                typeImage = ImageIO.read(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return typeImage;
    }

    @Override
    public String toString() {
        return this.key + " - " + this.summary + "[id:" + this.id + "]";
    }


    int cornerSquareWidth = 280;
    int cornerSquareHeight = 80;
    int cornerFontSize = 40;
    int centerFontSize = 70;
    int cornerImageSize = 30;
    int cornerImageSpace = 5;
    Font centerFont = new Font("Arial", Font.PLAIN, centerFontSize);
    Font cornerFont = new Font("Arial", Font.PLAIN, cornerFontSize);
    private static CachedImages cachedImages = new CachedImages();

    public void createCardPrint(Graphics graphics, PageFormat pageFormat) {
        Point originPoint = new Point((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        Point marginPoint = new Point((int) pageFormat.getImageableWidth() + originPoint.x, (int) pageFormat.getImageableHeight() + originPoint.y);

        drawTitle(graphics, getSummary(), 70, 200, (int) marginPoint.getX() - 140);
        Graphics2D g2d = (Graphics2D) graphics;
        BasicStroke basicStroke = new BasicStroke(2);
        g2d.setStroke(basicStroke);
        g2d.drawRect(originPoint.x, originPoint.y, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

        drawCorner(graphics, originPoint.x, originPoint.y, getKey());
        Image typeImage = cachedImages.getCachedImage(getTypeUrl());
        drawCorner(graphics, marginPoint.x - cornerSquareWidth, originPoint.y, getTypeName(), typeImage);
        Image priorityImage = cachedImages.getCachedImage(getPriorityUrl());
        drawCorner(graphics, originPoint.x, marginPoint.y - cornerSquareHeight, getPriorityName(), priorityImage);
        drawCorner(graphics, marginPoint.x - cornerSquareWidth, marginPoint.y - cornerSquareHeight, "" + getStoryPoints());
    }

    private void drawTitle(Graphics graphics, String text, int x, int y, int width) {
        LineBreakMeasurer lineMeasurer = null;
        int paragraphStart = 0;
        int paragraphEnd = 0;
        Graphics2D g2d = (Graphics2D) graphics;
        AttributedString attributedString = new AttributedString(text);
        attributedString.addAttribute(TextAttribute.FONT, centerFont);
        attributedString.addAttribute(TextAttribute.SIZE, centerFontSize);
        g2d.setFont(centerFont);

        // Create a new LineBreakMeasurer from the paragraph.
        // It will be cached and re-used.
        if (lineMeasurer == null) {
            AttributedCharacterIterator paragraph = attributedString.getIterator();
            paragraphStart = paragraph.getBeginIndex();
            paragraphEnd = paragraph.getEndIndex();
            FontRenderContext frc = g2d.getFontRenderContext();
            lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        }

        // Set break width to width of Component.
        float breakWidth = (float) width;
        float drawPosY = y;
        // Set position to the index of the first character in the paragraph.
        lineMeasurer.setPosition(paragraphStart);

        // Get lines until the entire paragraph has been displayed.
        while (lineMeasurer.getPosition() < paragraphEnd) {

            // Retrieve next layout. A cleverer program would also cache
            // these layouts until the component is re-sized.
            TextLayout layout = lineMeasurer.nextLayout(breakWidth);

            int lineWidth = (int) layout.getBounds().getMaxX() - (int) layout.getBounds().getMinX();

            // Compute pen x position. If the paragraph is right-to-left we
            // will align the TextLayouts to the right edge of the panel.
            // Note: this won't occur for the English text in this sample.
            // Note: drawPosX is always where the LEFT of the text is placed.
            float drawPosX = layout.isLeftToRight() ? x + (width - lineWidth) / 2 : breakWidth - layout.getAdvance();

            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();

            // Draw the TextLayout at (drawPosX, drawPosY).
            layout.draw(g2d, drawPosX, drawPosY);

            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }
    }

    private void drawCorner(Graphics graphics, int x, int y, String text, Image image) {
        graphics.setFont(cornerFont);
        graphics.drawRect(x, y, cornerSquareWidth, cornerSquareHeight);
        FontMetrics metrics = graphics.getFontMetrics(cornerFont);
        int textHeight = metrics.getHeight();
        int textWidth = metrics.stringWidth(text);
        int textBaseline = (int) (((metrics.getStringBounds(text, graphics).getHeight() + 1) / 2) - ((metrics.getAscent() + metrics.getDescent()) / 2) + metrics.getAscent());

        int yMargin = y + (cornerSquareHeight - textHeight) / 2 + textBaseline;
        int xMargin = x + (cornerSquareWidth - textWidth) / 2;

        if (image != null) {
            xMargin += (cornerImageSize + cornerImageSpace) / 2;
//            graphics.drawImage(image, xMargin - cornerImageSize - cornerImageSpace / 2, yMargin - cornerImageSize, cornerImageSize, cornerImageSize, null);
            graphics.drawImage(image, xMargin - cornerImageSize - cornerImageSpace, y + (cornerSquareHeight - cornerImageSize) / 2, cornerImageSize, cornerImageSize, null);

        }
        graphics.drawString(text, xMargin, yMargin);
    }

    private void drawCorner(Graphics graphics, int x, int y, String text) {
        drawCorner(graphics, x, y, text, null);
    }
}
