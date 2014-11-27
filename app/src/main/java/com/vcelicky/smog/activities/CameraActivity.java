package com.vcelicky.smog.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vcelicky.smog.AsyncTaskCompleteListener;
import com.vcelicky.smog.models.Photo;
import com.vcelicky.smog.tasks.UploadPhotoTask;
import com.vcelicky.smog.utils.FileUtils;
import com.vcelicky.smog.utils.SerializationUtils;
import com.vcelicky.smog.views.CameraPreview;
import com.vcelicky.smog.R;
import com.vcelicky.smog.abs.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 10. 10. 2014.
 */
public class CameraActivity extends BaseActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_COMPRESSED = 2; //BASE64
    public static final String DIRECTORY_MAIN = "MyCameraApp";
    public static final String DIRECTORY_UPLOAD = "TO_UPLOAD";

    private FileOutputStream fs;
    private FileInputStream fis;

    private static List<Photo> photoList = new ArrayList<Photo>();
    private Camera mCamera;
    private boolean isWifiOrMobileOn;
    private Gson gson;
    private String gsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //Create an instance of Camera
        mCamera = getCameraInstance();

        setPreviews();
        initListeners();
        checkNetworkStatus();

        //if device doesn't have a camera, then finish the activity
        if(!checkCameraHardware()) finish();
        requestLocationUpdate();


        initGson();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        SerializationUtils.serialize((ArrayList)photoList, "serializedList.ser", this);
        Log.d(TAG, "onStop()");
        mLocationManager.removeUpdates(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void setPreviews() {
        //Create our Preview and set it as the content of our activity
        CameraPreview mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    private void initGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        gson = builder.create();
    }

    /**
     * Initializes all the listeners used in this activity.
     */
    private void initListeners() {
        Button captureButton = (Button) findViewById(R.id.button_capture);
        final Intent intent = new Intent(this, TakenActivity.class);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get an image from the camera
                mCamera.takePicture(null, null, mPicture);
                //startActivity(intent);
            }
        });

        Button testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deserializeTest();
            }
        });
    }

    private void deserializeTest() {
        try {
//            FileInputStream fis = new FileInputStream(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES) + DIRECTORY_MAIN + File.separator + "savedList.ser");
            List<Photo> testList = new ArrayList<Photo>();
            Log.d(TAG, "pred inicializovanim fis");
            fis = this.openFileInput("serializedList.ser");
            Log.d(TAG, "po inicizliaovani fis");
            testList = (ArrayList)SerializationUtils.deserialize(fis);
            Log.d(TAG, "Hura, deserializovanie. Size = " + testList.size() + " " + testList.get(0).getLatitude());
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a device has got a camera inside.
     * @return true if does, false otherwise
     */
    private boolean checkCameraHardware() {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            //this device has a camera
            return true;
        } else {
            //no camera on this device
            return false;
        }
    }

    /**
     * Release the camera when it's not used anymore or Activity that uses it is paused.
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Initializes Camera object by opening Camera.
     * @return if success, then initialized Camera
     */
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
            File compressedFile = FileUtils.getOutputMediaFile(MEDIA_TYPE_COMPRESSED, isWifiOrMobileOn);
            if(compressedFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions!");
                return;
            }

            //Fill the file with image/video bytes
            try {
                //Transform to Base64 file
                String imageDataString = Base64.encodeToString(bytes, Base64.DEFAULT);
                byte[] imageByteArray = Base64.decode(imageDataString, Base64.DEFAULT);

                //Create Base64 image
                FileOutputStream fos = new FileOutputStream(compressedFile);
                fos.write(imageByteArray);
                fos.close();

                Photo currentPhoto = new Photo();
                currentPhoto.setPath(compressedFile.getAbsolutePath());
                currentPhoto.setImageByteArray(imageByteArray);

                if(mCurrentLocation != null) {
                    Toast.makeText(CameraActivity.this,
                                    "Latitude = "
                                    + String.valueOf(mCurrentLocation.getLatitude())
                                    + "; longitude = "
                                    + String.valueOf(mCurrentLocation.getLongitude()), Toast.LENGTH_SHORT).show();
                    currentPhoto.setLatitude(mCurrentLocation.getLatitude());
                    currentPhoto.setLongitude(mCurrentLocation.getLongitude());
                    photoList.add(currentPhoto);
                    gsonString = gson.toJson(currentPhoto);
                    Log.d(TAG, "gson = " + gsonString);
                } else {
                    Toast.makeText(CameraActivity.this,
                            "Zatiaľ sa nepodarilo získať Vaše GPS súradnice. Skúste to opäť o chvíľu.",
                            Toast.LENGTH_LONG)
                            .show();
                }

                //Start uploading a photo if WiFi is connected and active
                if(isWifiOrMobileConnected(CameraActivity.this) && mCurrentLocation != null) {
                    new UploadPhotoTask(CameraActivity.this, new UploadPhotoCompleteListener()).execute(currentPhoto);
                } else {
                    //Wifi AND mobile web is OFFLINE
                    FileUtils.writeToJson(FileUtils.getUploadDirectory(), gsonString);
                }
                mCamera.startPreview();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private void checkNetworkStatus() {
        isWifiOrMobileOn = isWifiOrMobileConnected(this);
    }

    private class UploadPhotoCompleteListener implements AsyncTaskCompleteListener<Photo> {
        @Override
        public void onTaskComplete(Photo photo) {
            Log.d(TAG, "onTaskComplete, mehehe + " + photo.getLatitude());
        }
    }
}
