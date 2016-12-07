package com.qrcodereader;

import android.graphics.Matrix;
import android.graphics.PointF;

import org.ejml.simple.SimpleMatrix;

/**
 * Created by mbacer on 06/12/16.
 */

public class MatrixTransform {

    private static Matrix matrix = new Matrix();

    public static Matrix getMatrix(PointF[] fromPts, PointF[] toPts) {
        matrix.reset();
        SimpleMatrix e = new SimpleMatrix(new double[][]{
                {fromPts[0].x, fromPts[0].y, 1, 0, 0, 0, -fromPts[0].x * toPts[0].x, -fromPts[0].y * toPts[0].x},
                {0, 0, 0, fromPts[0].x, fromPts[0].y, 1, -fromPts[0].x * toPts[0].y, -fromPts[0].y * toPts[0].y},
                {fromPts[1].x, fromPts[1].y, 1, 0, 0, 0, -fromPts[1].x * toPts[1].x, -fromPts[1].y * toPts[1].x},
                {0, 0, 0, fromPts[1].x, fromPts[1].y, 1, -fromPts[1].x * toPts[1].y, -fromPts[1].y * toPts[1].y},
                {fromPts[2].x, fromPts[2].y, 1, 0, 0, 0, -fromPts[2].x * toPts[2].x, -fromPts[2].y * toPts[2].x},
                {0, 0, 0, fromPts[2].x, fromPts[2].y, 1, -fromPts[2].x * toPts[2].y, -fromPts[2].y * toPts[2].y},
                {fromPts[3].x, fromPts[3].y, 1, 0, 0, 0, -fromPts[3].x * toPts[3].x, -fromPts[3].y * toPts[3].x},
                {0, 0, 0, fromPts[3].x, fromPts[3].y, 1, -fromPts[3].x * toPts[3].y, -fromPts[3].y * toPts[3].y}

        });

        SimpleMatrix e_invert = e.invert();
        SimpleMatrix vector = new SimpleMatrix(new double[][]{{toPts[0].x, toPts[0].y, toPts[1].x, toPts[1].y, toPts[2].x, toPts[2].y, toPts[3].x, toPts[3].y}});
        SimpleMatrix r = e_invert.mult(vector.transpose());
        float[] doubles = {(float) r.get(0), (float) r.get(1), (float) r.get(2),
                (float) r.get(3), (float) r.get(4), (float) r.get(5),
                (float) r.get(6), (float) r.get(7), 1};
        matrix.setValues(doubles);
        return matrix;
    }

}
