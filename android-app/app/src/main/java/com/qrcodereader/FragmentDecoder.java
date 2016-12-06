package com.qrcodereader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.commons.SettingsActivity;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.jira.JiraInformationDownload;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.hardware.camera2.CameraCharacteristics.LENS_FACING;
import static android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP;
import static android.hardware.camera2.CameraMetadata.CONTROL_AF_STATE_ACTIVE_SCAN;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;
import static android.hardware.camera2.CaptureRequest.CONTROL_AF_MODE;


/**
 * FragmentDecoder.class
 * Created by Mihail on 28/10/15 <mihail@breadwallet.com>.
 * Copyright (c) 2015 breadwallet llc.
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class FragmentDecoder extends Fragment
        implements FragmentCompat.OnRequestPermissionsResultCallback {
    public static final int PREVIEW_SCALE_FACTOR = 10;
    private static final int sImageFormat = ImageFormat.YUV_420_888;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = FragmentDecoder.class.getName();
    private final CameraCaptureSession.CaptureCallback mCaptureCallback =
            new CameraCaptureSession.CaptureCallback() {
                private void process(CaptureResult result) {
                }

                @Override
                public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                                CaptureResult partialResult) {
                    process(partialResult);
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                               TotalCaptureResult result) {
                    process(result);
                }

            };
    private FrameLayout layout;
    private QRCodeReader mQrReader;
    private Map<DecodeHintType, Object> decodeHintTypeObjectHashtable;
    private String mCameraId;
    private AutoFitTextureView mTextureView;
    private AutoFitTextureView mOverlayView;
    //private SurfaceHolder holder;
    private String prevText = "";
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private Size mPreviewSize;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private ImageReader mImageReader;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CaptureRequest mPreviewRequest;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            getActivity().onBackPressed();
        }

    };
    private int count;
    private Map<String, AsyncTask<String, Void, String>> cache = new HashMap<>();
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {

                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image img = null;
                    img = reader.acquireLatestImage();
                    Result rawResult = null;
                    try {
                        if (img == null) throw new NullPointerException("cannot be null");
                        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        int width = img.getWidth();
                        int height = img.getHeight();
                        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        rawResult = mQrReader.decode(bitmap, decodeHintTypeObjectHashtable);
                        onQRCodeRead(rawResult, img);
                        count = 0;
                    } catch (ReaderException ignored) {
                        if (count > 8) {
                            cache.clear();
                            Canvas canvas = mOverlayView.lockCanvas();
                            try {
                                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            } finally {
                                mOverlayView.unlockCanvasAndPost(canvas);
                            }

                        }
                        count++;
                        /* Ignored */
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } finally {

                        mQrReader.reset();
                        if (img != null)
                            img.close();

                    }
                }

            };
    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                    openCamera(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                    configureTransform(width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {

                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture texture) {

                }

            };

    private static Bitmap imageToBitmap(Image img) {
        Bitmap bitmap = null;
        try {
            if (img != null) {
                Image.Plane[] planes = img.getPlanes();
                if (planes[0].getBuffer() == null) {
                    return null;
                }
                int width = img.getWidth();
                int height = img.getHeight();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;

                int offset = 0;
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                ByteBuffer buffer = planes[0].getBuffer();
                for (int i = 0; i < height; ++i) {
                    for (int j = 0; j < width; ++j) {
                        int pixel = 0;
                        pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                        pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                        pixel |= (buffer.get(offset + 2) & 0xff);       // B
                        pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                        bitmap.setPixel(j, i, pixel);
                        offset += pixelStride;
                    }
                    offset += rowPadding;
                }
                img.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != img) {
                img.close();
            }

        }
        return bitmap;
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static float[] centroid(float[] points) {
        float[] centroid = {0, 0};

        for (int i = 0; i < points.length; i += 2) {
            centroid[0] += points[i];
            centroid[1] += points[i + 1];
        }

        int totalPoints = points.length / 2;
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return centroid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_decoder, container, false);
        mQrReader = new QRCodeReader();
        decodeHintTypeObjectHashtable = new Hashtable<>();
        //decodeHintTypeObjectHashtable.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));
        decodeHintTypeObjectHashtable.put(DecodeHintType.CHARACTER_SET, "UTF-8");


        mTextureView = new AutoFitTextureView(getActivity());
        mOverlayView = new AutoFitTextureView(getActivity());
        mTextureView.setZ(0);
        mOverlayView.setZ(1);
        mOverlayView.setOpaque(false);


        //holder.setFormat(PixelFormat.TRANSPARENT);

        layout = (FrameLayout) rootView.findViewById(R.id.fragment_decoder_layout);

        ViewGroup.LayoutParams params1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTextureView.setLayoutParams(params1);
        mOverlayView.setLayoutParams(params2);
        layout.addView(mOverlayView);
        layout.addView(mTextureView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startBackgroundThread();
            }
        }, 700);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).

        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getActivity().getSystemService(getActivity().CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                if (characteristics.get(LENS_FACING) == LENS_FACING_FRONT) continue;

                StreamConfigurationMap map = characteristics.get(SCALER_STREAM_CONFIGURATION_MAP);

                // For still image captures, we use the largest available size.
                List<Size> outputSizes = Arrays.asList(map.getOutputSizes(sImageFormat));
                Size largest = Collections.max(outputSizes, new CompareSizesByArea());

                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, largest);
                Log.e(TAG, "WIDTH: " + mPreviewSize.getWidth() + " HEIGHT: " + mPreviewSize.getHeight());
                // We fit the aspect ratio of TextureView to the size of preview we picked.
                mImageReader = ImageReader.newInstance(mPreviewSize.getWidth() / PREVIEW_SCALE_FACTOR, mPreviewSize.getHeight() / PREVIEW_SCALE_FACTOR, sImageFormat, 2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);
                // Danger, W.R.! Attempting to use too large a preview size could exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.

//                int orientation = getResources().getConfiguration().orientation;
//                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//                } else {
                mTextureView.setAspectRatio(MainActivity.screenParametersPoint.x,
                        MainActivity.screenParametersPoint.y - getStatusBarHeight()); //portrait only
                mOverlayView.setAspectRatio(MainActivity.screenParametersPoint.x,
                        MainActivity.screenParametersPoint.y - getStatusBarHeight()); //portrait only
//                }

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the camera specified by {@link FragmentDecoder#mCameraId}.
     */
    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        CameraManager manager = (CameraManager) getActivity().getSystemService(getActivity().CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    private void requestCameraPermission() {
        if (FragmentCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (mCaptureSession != null) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (mImageReader != null) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        try {
            mBackgroundThread.quitSafely();
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Log.e(TAG, "mPreviewSize.getWidth(): " + mPreviewSize.getWidth() + ", mPreviewSize.getHeight(): "
                    + mPreviewSize.getHeight());

            Surface surface = new Surface(texture);
            Surface mImageSurface = mImageReader.getSurface();
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mImageSurface);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(mImageSurface, surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            Log.e(TAG, "onConfigured");
                            if (mCameraDevice == null) return;

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CONTROL_AF_MODE, CONTROL_AF_STATE_ACTIVE_SCAN);
                                // Flash is automatically enabled when necessary.
//                                mPreviewRequestBuilder.set(CONTROL_AE_MODE, CONTROL_AE_MODE_ON_AUTO_FLASH); // no need for flash now

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                                        mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */

    private void configureTransform(int viewWidth, int viewHeight) {

       /* if (mTextureView == null || mPreviewSize == null) return;

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);*/
    }

    public void onQRCodeRead(final Result result, Image image) throws NotFoundException, ExecutionException, InterruptedException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        String hostname = preferences.getString(SettingsActivity.HOSTNAME_KEY, "");
        String username = preferences.getString(SettingsActivity.USERNAME_KEY, "");
        String password = preferences.getString(SettingsActivity.PASSWORD_KEY, "");
        String key = result.getText();
        AsyncTask<String, Void, String> asyncTask = cache.get(key);
        if (asyncTask == null) {
            cache.clear();
            JiraInformationDownload jiraInformationDownload = new JiraInformationDownload(hostname, username, password);
            asyncTask = jiraInformationDownload.execute(key);
            cache.put(key, asyncTask);
        }
        ResultPoint[] resultPoints = result.getResultPoints();
        Matrix transform = new Matrix();
        Canvas canvas = mOverlayView.lockCanvas();
        try {
            Rect clipBounds = image.getCropRect();

            transform.postRotate(90);
            transform.postTranslate(clipBounds.height(), 0);

            float sx = mOverlayView.getRatioWidth() / (float) clipBounds.height();
            float sy = mOverlayView.getHeight() / (float) clipBounds.width();
            transform.postScale(sx, sy);


            PointF p0 = getPointF(resultPoints[0], transform, clipBounds);
            PointF p1 = getPointF(resultPoints[1], transform, clipBounds);
            PointF p2 = getPointF(resultPoints[2], transform, clipBounds);

            float dx = p2.x - p1.x;
            float dy = p2.y - p1.y;

            PointF p3 = new PointF(p0.x + dx, p0.y + dy);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            paint.setColor(Color.argb(255, 241, 226, 95));
            float vx = p0.x - p1.x;
            float vy = p0.y - p1.y;

            float v = (float) (Math.sqrt(vx * vx + vy * vy) / 1.75);
            paint.setStrokeWidth(v);

            Path wallpath = new Path();
            wallpath.reset(); // only needed when reusing this path for a new build
            wallpath.moveTo(p0.x, p0.y); // used for first point
            wallpath.lineTo(p1.x, p1.y);
            wallpath.lineTo(p2.x, p2.y);
            wallpath.lineTo(p3.x, p3.y);
            wallpath.lineTo(p0.x, p0.y);
            wallpath.lineTo(p1.x, p1.y);
            canvas.drawPath(wallpath, paint);
            Paint foreground = new Paint();
            foreground.setColor(Color.BLACK);
            float textSize = vy / 2;
            foreground.setTextSize(textSize);
            String text = "loading " + key;
            if (asyncTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                String s = asyncTask.get();
                if (s == null) {
                    text = "N/A";
                } else {
                    text = s;

                }
            }
            String[] split = text.split("\n");
            for (int i = 0; i < split.length; i++) {
                drawString(canvas, p0, p1, foreground, split[i], i, textSize, (int) dx);
            }

        } finally {
            mOverlayView.unlockCanvasAndPost(canvas);
        }
    }

    private void drawString(Canvas canvas, PointF p0, PointF p1, Paint foreground, String text, int i, float textSize, int dx) {
        float v1 = foreground.measureText(text);
        canvas.drawText(text, p0.x + (dx / 2) - (v1 / 2), p1.y + (i * textSize), foreground);//// TODO:  getRobe(result)
    }

//    private String getRobe(Result result) {
//        JiraInformationDownload jiraInformationDownload = new JiraInformationDownload(username, password);
//
//        AsyncTask<String, Void, String> execute = jiraInformationDownload.execute("MF-10178");
//        try {
//            return execute.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return "NONEEE";
////        try {
////            Toast.makeText(getBaseContext(),execute.get(), Toast.LENGTH_LONG).show();
////        } catch (InterruptedException | ExecutionException e) {
////            e.printStackTrace();
////        }
//    }

    @NonNull
    private PointF getPointF(ResultPoint topLeft, Matrix transform, Rect clipBounds) {
        float[] floats = new float[2];
        floats[0] = topLeft.getX();
        floats[1] = topLeft.getY();
        transform.mapPoints(floats);
        Log.d("resultPoints", "rot: " + floats[0] + "," + floats[1]);
        float cx = floats[0];
        float cy = floats[1];
        Log.d("resultPoints", "scaled: " + cx + "," + cy);
        return new PointF(cx, cy);
    }

    public int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;

        Log.e(TAG, "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);
        return statusBarHeight + titleBarHeight;
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    private static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    public static class ConfirmationDialog extends DialogFragment {
        /**
         * Shows OK/Cancel confirmation dialog about camera permission.
         */


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentCompat.requestPermissions(parent,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

}
