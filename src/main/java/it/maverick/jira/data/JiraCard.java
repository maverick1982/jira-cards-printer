package it.maverick.jira.data;

import it.maverick.jira.utils.CachedImages;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
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

    private static final int          CORNER_IMAGE_SPACE = 5;
    private static final CachedImages CACHED_IMAGES      = new CachedImages();

    private final int    id;
    private final String key;
    private final String summary;
    private       String typeName;
    private       String typeUrl;
    private       String priorityUrl;
    private       String priorityName;
    private       double storyPoints;
    private       String statusName;
    private       String statusUrl;
    private       Image  typeImage;

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

    private void drawTitle(Graphics graphics, String text, int x, int y, int width, int height) {
        LineBreakMeasurer lineMeasurer = null;
        int paragraphStart = 0;
        int paragraphEnd = 0;
        Graphics2D g2d = (Graphics2D) graphics;
        int centerFontSize = (int) (height * 0.19);
        Font centerFont = new Font("Arial", Font.PLAIN, centerFontSize);
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

    private void drawCorner(Graphics graphics, int x, int y, int cornerWidth, int cornerHeight, String text, Image image) {
        Font cornerFont = new Font("Arial", Font.PLAIN, (int) (cornerWidth * 0.12));
        graphics.setFont(cornerFont);
        graphics.drawRect(x, y, cornerWidth, cornerHeight);
        FontMetrics metrics = graphics.getFontMetrics(cornerFont);
        int textHeight = metrics.getHeight();
        int textWidth = metrics.stringWidth(text);
        int textBaseline = (int) (((metrics.getStringBounds(text, graphics).getHeight() + 1) / 2) - ((metrics.getAscent() + metrics.getDescent()) / 2) + metrics.getAscent());

        int yMargin = y + (cornerHeight - textHeight) / 2 + textBaseline;
        int xMargin = x + (cornerWidth - textWidth) / 2;

        if (image != null) {
            int cornerImageSize = (int) (textHeight * 0.8);
            xMargin += (cornerImageSize + CORNER_IMAGE_SPACE) / 2;
            graphics.drawImage(image, xMargin - cornerImageSize - CORNER_IMAGE_SPACE, y + (cornerHeight - cornerImageSize) / 2, cornerImageSize, cornerImageSize, null);

        }
        graphics.drawString(text, xMargin, yMargin);
    }

    private void drawCorner(Graphics graphics, int x, int y, int cornerWidth, int cornerHeight, String text) {
        drawCorner(graphics, x, y, cornerWidth, cornerHeight, text, null);
    }

    public void createCardPrint(Graphics graphics, Point cardOrigin, int cardWidth, int cardHeight) {
        drawTitle(graphics, getSummary(), cardOrigin.x + (int) (0.03 * cardWidth), cardOrigin.y + (int) (0.2 * cardHeight), (int) (0.94 * cardWidth), (int) (cardHeight * 0.6));
        Graphics2D g2d = (Graphics2D) graphics;
        BasicStroke basicStroke = new BasicStroke(2);
        g2d.setStroke(basicStroke);
        g2d.drawRect(cardOrigin.x, cardOrigin.y, cardWidth, cardHeight);

        int cornerWidth = (int) (cardWidth * 0.38);
        int cornerHeight = (int) (cardHeight * 0.11);
        drawCorner(graphics, cardOrigin.x, cardOrigin.y, cornerWidth, cornerHeight, getKey());
        Image typeImage = CACHED_IMAGES.getCachedImage(getTypeUrl());
        drawCorner(graphics, cardOrigin.x + cardWidth - cornerWidth, cardOrigin.y, cornerWidth, cornerHeight, getTypeName(), typeImage);
        Image priorityImage = CACHED_IMAGES.getCachedImage(getPriorityUrl());
        drawCorner(graphics, cardOrigin.x, cardOrigin.y + cardHeight - cornerHeight, cornerWidth, cornerHeight, getPriorityName(), priorityImage);
        drawCorner(graphics, cardOrigin.x + cardWidth - cornerWidth, cardOrigin.y + cardHeight - cornerHeight, cornerWidth, cornerHeight, String.valueOf(getStoryPoints()));
    }
}
