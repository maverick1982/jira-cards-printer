package it.maverick.jira;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.oned.Code39Reader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Pasquale on 11/05/2016.
 */
public class CodesReader {

    private static final ClassLoader  CLASS_LOADER = CodesReader.class.getClassLoader();
    private static final InputStream  TEST_IMAGE   = CLASS_LOADER.getResourceAsStream("it/maverick/jira/images/board3.jpg");
    private static final List<String> COLUMN_NAMES = Arrays.asList("TODO", "DOING", "TECHREVIEW\n", "TEST", "DONE");

    public static void main(String[] args) {
        BufferedImage bfi = null;


        try (BufferedInputStream bfin = new BufferedInputStream(TEST_IMAGE, 8196)) {
            bfi = ImageIO.read(bfin);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bfi != null) {
            LuminanceSource ls = new BufferedImageLuminanceSource(bfi);
            BinaryBitmap bmp = new BinaryBitmap(new HybridBinarizer(ls));

            Reader reader = new Code39Reader(false);
            GenericMultipleBarcodeReader greader = new GenericMultipleBarcodeReader(reader);
            QRCodeMultiReader qrCodeMultiReader = new QRCodeMultiReader();

//            QRCodeMultiReader greader = new QRCodeMultiReader();
            Result[] results = null;

            Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<>();
//            decodeHints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
//            decodeHints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.CODE_39));
            decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            decodeHints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            try {
//                Result result = reader.decode(bmp, decodeHints);
                results = qrCodeMultiReader.decodeMultiple(bmp, decodeHints);
//                for (Result result : results) {
//                    System.out.println("Text: " + result.getText());
//                }

                List<Point> columnPoints = getColumnPoints(results);
                List<String> columnNames = getColumnNames(results);
                List<Point> columnNamePoints = getColumnNamePointss(results);
                List<Column> columns = createColumns(columnPoints, columnNames, columnNamePoints);

                for (Column column : columns) {
                    column.addCards(results);
                    System.out.println(column);
                }

            } catch (NotFoundException | NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("no buffered image");
        }
    }

    private static List<Point> getColumnNamePointss(Result[] results) {
        List<Point> columnNamePoints = new ArrayList<>();
        for (Result result : results) {
            if (COLUMN_NAMES.contains(result.getText())) {
                ResultPoint resultPoint = result.getResultPoints()[0];
                columnNamePoints.add(new Point((int) resultPoint.getX(), (int) resultPoint.getY()));
            }
        }
        return columnNamePoints;
    }

    private static List<Column> createColumns(List<Point> columnPoints, List<String> columnNames, List<Point> columnNamePoints) {
        List<Column> columns = new ArrayList<>();
        List<Point> sortedColumnPoints = new ArrayList<>(columnPoints);
        sortedColumnPoints.sort(new ColumnPointsXComparator());
        for (int i = 0; i < sortedColumnPoints.size() - 3; i = i + 2) {
            List<Point> sortedYPoints = new ArrayList<>();
            sortedYPoints.add(sortedColumnPoints.get(i));
            sortedYPoints.add(sortedColumnPoints.get(i + 1));
            sortedYPoints.add(sortedColumnPoints.get(i + 2));
            sortedYPoints.add(sortedColumnPoints.get(i + 3));
            sortedYPoints.sort(new ColumnPointsYComparator());

            Point[] points = new Point[4];
            sortedYPoints.toArray(points);

            String columnName = null;
            for (int j = 0; j < columnNamePoints.size(); j++) {
                if (contains(points, columnNamePoints.get(j))) {
                    columnName = columnNames.get(j);
                    break;
                }
            }
            columns.add(new Column(columnName, points));


        }
        return columns;
    }

    public static boolean contains(Point[] points, Point test) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y) &&
                    (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;
    }

    private static List<String> getColumnNames(Result[] results) {
        List<String> columnNames = new ArrayList<>();
        for (Result result : results) {
            if (COLUMN_NAMES.contains(result.getText())) {
                columnNames.add(result.getText());
            }
        }
        return columnNames;
    }

    private static List<Point> getColumnPoints(Result[] results) {
        List<Point> columnPoints = new ArrayList<>();
        for (Result result : results) {
            if ("COL".equals(result.getText())) {
                ResultPoint resultPoint = result.getResultPoints()[0];
                columnPoints.add(new Point((int) resultPoint.getX(), (int) resultPoint.getY()));
            }
        }
        return columnPoints;
    }

    private static class Column {

        private final String  columnName;
        private final Point[] points;
        private final List<String> cards = new ArrayList<>();

        public Column(String columnName, Point[] points) {
            this.columnName = columnName;
            this.points = points;
        }

        @Override
        public String toString() {
            String cardsString = "\n";
            for (String card : cards) {
                cardsString += "- " + card + "\n";
            }
            return columnName + ": " + Arrays.toString(points) + cardsString;
        }

        public void addCards(Result[] results) {
            for (Result result : results) {
                String text = result.getText();
                if (!COLUMN_NAMES.contains(text) && !"COL".equals(text)) {
                    ResultPoint resultPoint = result.getResultPoints()[0];
                    if (contains(points, new Point((int) resultPoint.getX(), (int) resultPoint.getY()))) {
                        cards.add(result.getText());
                    }
                }
            }
        }
    }

    private static class ColumnPointsXComparator implements java.util.Comparator<Point> {
        @Override
        public int compare(Point o1, Point o2) {
            return Double.compare(o1.getX(), o2.getX());
        }
    }

    private static class ColumnPointsYComparator implements java.util.Comparator<Point> {
        @Override
        public int compare(Point o1, Point o2) {
            return Double.compare(o1.getY(), o2.getY());
        }
    }
}
