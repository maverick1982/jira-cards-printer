package it.maverick.jira.print;

/**
 * Created by Pasquale on 08/03/2016.
 */
public enum CardsPerPage {
    ONE(1, 1, 1), TWO(2, 1, 2), FOUR(4, 2, 2), EIGHT(8, 2, 4);

    private final int cardsPerPage;
    private final int columnsCount;
    private final int rowsCount;

    CardsPerPage(int cardsPerPage, int columnsCount, int rowsCount) {
        this.cardsPerPage = cardsPerPage;
        this.columnsCount = columnsCount;
        this.rowsCount = rowsCount;
    }

    public int getCardsPerPageInt() {
        return cardsPerPage;
    }

    public int getColumnsCount() {
        return columnsCount;
    }

    public int getRowsCount() {
        return rowsCount;
    }
}
