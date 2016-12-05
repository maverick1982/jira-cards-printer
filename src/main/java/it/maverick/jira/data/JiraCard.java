package it.maverick.jira.data;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import it.maverick.jira.utils.CachedImages;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.EnumMap;
import java.util.Map;

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

    @Override
    public String toString() {
        return this.key + " - " + this.summary + " [id:" + this.id + "]";
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
        Font cornerFont = new Font("Arial", Font.PLAIN, (int) (cornerHeight * 0.6));
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
        int lineWidth = (int) basicStroke.getLineWidth();

        int cornerWidth = (int) (cardWidth * 0.38);
        int cornerHeight = (int) (cardHeight * 0.11);
        int priorityCornerWidth = (int) (cardWidth * 0.22);
        int pointsCornerWidth = (int) (cardWidth * 0.12);
//        int barcodeWidth = cardWidth - priorityCornerWidth - pointsCornerWidth - 2 * lineWidth;
        int barcodeWidth = (int) (cardHeight * 0.25);
        drawCorner(graphics, cardOrigin.x, cardOrigin.y, cornerWidth, cornerHeight, getKey());
        Image typeImage = CACHED_IMAGES.getCachedImage(getTypeUrl());
        drawCorner(graphics, cardOrigin.x + cardWidth - cornerWidth, cardOrigin.y, cornerWidth, cornerHeight, getTypeName(), typeImage);
        Image priorityImage = CACHED_IMAGES.getCachedImage(getPriorityUrl());
        drawCorner(graphics, cardOrigin.x, cardOrigin.y + cardHeight - cornerHeight, priorityCornerWidth, cornerHeight, getPriorityName(), priorityImage);
        drawCorner(graphics, cardOrigin.x + cardWidth - pointsCornerWidth, cardOrigin.y + cardHeight - cornerHeight, pointsCornerWidth, cornerHeight, String.valueOf(getStoryPoints()));
        drawQRCode(graphics, cardOrigin.x + cardWidth / 2 - barcodeWidth / 2, cardOrigin.y + cardHeight - barcodeWidth - (int) basicStroke.getLineWidth(), barcodeWidth, barcodeWidth, key);
//        drawQRCode(graphics, cardOrigin.x + priorityCornerWidth + lineWidth, cardOrigin.y + cardHeight - cornerHeight, barcodeWidth, cornerHeight, String.valueOf(id));
    }

    private void drawQRCode(Graphics graphics, int x, int y, int barCodeWidth, int barCodeHeight, String id) {
        try {
            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Now with zxing version 3.2.1 you could change border barCodeWidth (white border barCodeWidth to just 1)
            hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
//            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            Writer qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(id, BarcodeFormat.QR_CODE, barCodeWidth, barCodeHeight, hintMap);

//            Writer qrCodeWriter = new Code39Writer();
//            BitMatrix byteMatrix = qrCodeWriter.encode(id, BarcodeFormat.CODE_39, barCodeWidth, barCodeHeight, hintMap);
            int crunchifyWidth = byteMatrix.getWidth();
            int crunchifyHeight = byteMatrix.getHeight();
            BufferedImage image = new BufferedImage(crunchifyWidth, crunchifyHeight, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            graphics.setColor(Color.WHITE);
            graphics.fillRect(x, y, crunchifyWidth, crunchifyHeight);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < crunchifyWidth; i++) {
                for (int j = 0; j < crunchifyHeight; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(x + i, y + j, 1, 1);
                    }
                }
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
