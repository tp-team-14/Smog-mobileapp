package com.vcelicky.smog;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jaro on 10. 10. 2014.
 */
public class CameraActivity extends BaseActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_COMPRESSED = 3;

    private static String urlString = "http://192.168.0.101:80";
    private byte[] imageByteArray = null;
    private String imageDataString = null;

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initListeners();

        //if device doesn't have a camera, then finish the activity
        if(!checkCameraHardware()) finish();

        //Create an instance of Camera
        mCamera = getCameraInstance();

        //Create our Preview wie and set it as the content of our activity
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void initListeners() {
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get an image from the camera
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    private boolean checkCameraHardware() {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            //this device has a camera
            Log.d(TAG, "Number of Cameras is " + String.valueOf(Camera.getNumberOfCameras()));
            return true;
        } else {
            //no camera on this device
            return false;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; //returns null if camera is unavailable
    }


    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            File compressedFile = getOutputMediaFile(MEDIA_TYPE_COMPRESSED);
            if(pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions!");
                return;
            }

            //Fill the file with image/video bytes
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(bytes);
                fos.close();

                //Transform to Base64 file
                imageDataString =
                        Base64.encodeToString(bytes, Base64.DEFAULT);
                imageByteArray =
                        Base64.decode(imageDataString, Base64.DEFAULT);

                //Create Base64 image
                fos = new FileOutputStream(compressedFile);
                fos.write(imageByteArray);
                fos.close();

                Toast.makeText(getApplicationContext(), "Photo was saved!", Toast.LENGTH_SHORT)
                        .show();

                //Start uploading a photo
                new UploadAsyncTask().execute();
                mCamera.startPreview();

            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    //Create a File for saving an image or video
    private static File getOutputMediaFile(int type) {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD Card is not mounted!");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        if(!mediaStorageDir.exists()) {
            if(!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        //Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        //Create a file depending on file type
        if(type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_" + timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_" + timeStamp + ".mp4");
        } else if(type == MEDIA_TYPE_COMPRESSED) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_" + timeStamp + "_base64.jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    private class UploadAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                HttpURLConnection conn = null;
                URL url = new URL(urlString);
                String attachmentName = "ahoj";
                String attachmentFileName = "ahoj.jpg";
                String boundary =  "*****";
                String twoHyphens = "--";
                String crlf = "\r\n";

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream request = new DataOutputStream(conn.getOutputStream());

                request.writeBytes(twoHyphens + boundary + crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName
                        + "\";filename=\"" + attachmentFileName + "\"" + crlf);
                request.writeBytes(crlf);

                if(imageByteArray == null)
                    Log.d(TAG, "imageByteArray == null");
                else
                    request.write(imageByteArray);

                request.writeBytes(crlf);
                request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);

                request.flush();
                request.close();

                InputStream responseStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();
                String response = stringBuilder.toString();
                Log.d(TAG, "response = " + response);
                responseStream.close();
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(getApplicationContext(), "Mehehe", Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }

}
