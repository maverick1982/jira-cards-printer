package com.qrcodereader;


import android.graphics.Matrix;
import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by mbacer on 06/12/16.
 */
public class MatrixTransformTest extends ActivityInstrumentationTestCase2<MainActivity> {


        public MatrixTransformTest() {
            super(MainActivity.class);
        }

        @Override
        protected void setUp() throws Exception {
            super.setUp();

            // Starts the activity under test using the default Intent with:
            // action = {@link Intent#ACTION_MAIN}
            // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
            // All other fields are null or empty.
        }

        /**
         * Test if the test fixture has been set up correctly.
         */




    public void testMatrix() throws Exception {
        PointF[] fromPts=new PointF[4];
        fromPts[0]=new PointF(0,0);
        fromPts[1]=new PointF(0,10);
        fromPts[2]=new PointF(10,10);
        fromPts[3]=new PointF(5,0);

        PointF[] toPts=new PointF[4];
        toPts[0]=new PointF(0,0);
        toPts[1]=new PointF(0,10);
        toPts[2]=new PointF(10,10);
        toPts[3]=new PointF(10,0);


        Matrix matrix = MatrixTransform.getMatrix(fromPts,toPts);

        float[] pt = {2, 6};
        matrix.mapPoints(pt);
        assertEquals(2.0f,pt[0]);
        assertEquals(10.0f,pt[1]);

    }


}