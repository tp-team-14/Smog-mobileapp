package com.vcelicky.smog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Šani on 13. 11. 2014.
 */
public class TakenPhotoPreview extends Activity {
    ImageView preview;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_photo_preview);
        initListeners();

        //Create our Preview wie and set it as the content of our activity
        //mPreview = new CameraPreview(this, mCamera);

        //preview.addView(mPreview);
    }

    private void initListeners() {
        preview = (ImageView) findViewById(R.id.photopreview);
        ImageButton uploadButton = (ImageButton) findViewById(R.id.button_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get an image from the camera


            }
        });

        ImageButton addInfoButton = (ImageButton) findViewById(R.id.button_additional_info);
        addInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get an image from the camera


            }
        });
    }
}
