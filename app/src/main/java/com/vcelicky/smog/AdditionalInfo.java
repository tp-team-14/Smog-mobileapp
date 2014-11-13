package com.vcelicky.smog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Šani on 13. 11. 2013.
 */
public class AdditionalInfo extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);
        initListeners();

        //Create our Preview wie and set it as the content of our activity
        //mPreview = new CameraPreview(this, mCamera);

        //preview.addView(mPreview);
    }

    private void initListeners() {
       // preview = (ImageView) findViewById(R.id.photopreview);
        ImageButton captureButton = (ImageButton) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get an image from the camera


            }
        });
    }
}
