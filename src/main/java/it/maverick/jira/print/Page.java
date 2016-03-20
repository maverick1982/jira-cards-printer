package it.maverick.jira.print;

import it.maverick.jira.data.JiraCard;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

/**
 * Created by Pasquale on 07/03/2016.
 */
public class Page implements Printable {

    private final List<JiraCard> cardsToPrint;
    private final CardsPerPage   cardsPerPage;

    public Page(List<JiraCard> cards, CardsPerPage cardsPerPage) {
        cardsToPrint = cards;
        this.cardsPerPage = cardsPerPage;
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        int cardsPerPageInt = cardsPerPage.getCardsPerPageInt();

        int cardWidth = (int) pageFormat.getImageableWidth() / cardsPerPage.getColumnsCount();
        int cardHeight = (int) pageFormat.getImageableHeight() / cardsPerPage.getRowsCount();

        if (pageIndex < (double) cardsToPrint.size() / cardsPerPageInt) {
            int pageOffset = pageIndex * cardsPerPageInt;
            for (int i = 0; i < cardsPerPageInt; i++) {
                if (pageOffset + i >= cardsToPrint.size()) {
                    break;
                }
                int cardX = (int) pageFormat.getImageableX() + (Math.floorMod(i, cardsPerPage.getColumnsCount()) * cardWidth);
                int cardY = (int) pageFormat.getImageableY() + (Math.floorDiv(i, cardsPerPage.getColumnsCount()) * cardHeight);
                Point cardOrigin = new Point(cardX, cardY);
                JiraCard cardToPrint = cardsToPrint.get(pageOffset + i);
                cardToPrint.createCardPrint(graphics, cardOrigin, cardWidth, cardHeight);
            }
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

}
