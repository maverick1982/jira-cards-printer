package it.esteco;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;

/**
 * User: Pasquale
 * Date: 04/01/14
 * Time: 12.25
 */
public class ProjectsLogic implements Printable {

    private ProjectsView        projectsView;
    private JiraServer          jiraServer;
    private ArrayList<JiraCard> cardsToPrint;
    private CachedImages cachedImages = new CachedImages();

    public ProjectsLogic(JiraServer jiraServer) {
        this.jiraServer = jiraServer;
    }

    public ProjectsView getProjectsView() {
        return projectsView;
    }

    public void setProjectsView(ProjectsView projectsView) {
        this.projectsView = projectsView;
    }

    public void onLoadProjects() {
        projectsView.setProjects(jiraServer.getProjectsList());
        projectsView.setSprints(new ArrayList<JiraSprint>());
        projectsView.setCards(new ArrayList<JiraCard>());
    }

    public void onProjectSelected(int projectId) {
        projectsView.setSprints(jiraServer.getSprints(projectId));
        projectsView.setCards(new ArrayList<JiraCard>());
    }

    public void onSprintSelected(int projectId, int sprintId) {
        projectsView.setCards(jiraServer.getCards(projectId, sprintId));
    }

    public void onPrint(ArrayList<JiraCard> cardsToPrint) {
        this.cardsToPrint = cardsToPrint;

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
              /* The job did not successfully complete */
            }
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex < cardsToPrint.size()) {
            createCardPrint(graphics, pageFormat, pageIndex);
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

    int  cornerSquareWidth  = 280;
    int  cornerSquareHeight = 80;
    int  cornerFontSize     = 40;
    int  centerFontSize     = 70;
    int  cornerImageSize    = 30;
    int  cornerImageSpace   = 5;
    Font centerFont         = new Font("Arial", Font.PLAIN, centerFontSize);
    Font cornerFont         = new Font("Arial", Font.PLAIN, cornerFontSize);

    private void createCardPrint(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        Point originPoint = new Point((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
        Point marginPoint = new Point((int) pageFormat.getImageableWidth() + originPoint.x, (int) pageFormat.getImageableHeight() + originPoint.y);

        JiraCard jiraCard = cardsToPrint.get(pageIndex);

        drawTitle(graphics, jiraCard.getSummary(), 70, 200, (int) marginPoint.getX() - 140);
        Graphics2D g2d = (Graphics2D) graphics;
        BasicStroke basicStroke = new BasicStroke(2);
        g2d.setStroke(basicStroke);
        g2d.drawRect(originPoint.x, originPoint.y, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

        drawCorner(graphics, originPoint.x, originPoint.y, jiraCard.getKey());
        Image typeImage = cachedImages.getCachedImage(jiraCard.getTypeUrl());
        drawCorner(graphics, marginPoint.x - cornerSquareWidth, originPoint.y, jiraCard.getTypeName(), typeImage);
        Image priorityImage = cachedImages.getCachedImage(jiraCard.getPriorityUrl());
        drawCorner(graphics, originPoint.x, marginPoint.y - cornerSquareHeight, jiraCard.getPriorityName(), priorityImage);
        drawCorner(graphics, marginPoint.x - cornerSquareWidth, marginPoint.y - cornerSquareHeight, "" + jiraCard.getStoryPoints());
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
