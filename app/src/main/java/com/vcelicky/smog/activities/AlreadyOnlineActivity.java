package com.vcelicky.smog.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vcelicky.smog.R;
import com.vcelicky.smog.abs.BaseActivity;
import com.vcelicky.smog.models.Photo;
import com.vcelicky.smog.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 5. 11. 2014.
 */
public class AlreadyOnlineActivity extends BaseActivity {
    private static final String TAG = AlreadyOnlineActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        Log.d(TAG, "Started");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odoslať fotky");
        builder.setMessage("Niekoľko Vami odfotených reklám zatiaľ nebolo odoslaných z dôvodu " +
                "chýbajúceho pripojenia na internet. Chcete ich teraz odoslať?");
        // Add the buttons
        builder.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.d(TAG, "YES");
                dialog.dismiss();
                Gson gson = new Gson();
                try {
                    FileUtils.writeToJson(FileUtils.getUploadDirectory(), "]", true);
                    BufferedReader br = new BufferedReader(
                            new FileReader(FileUtils.getUploadDirectory() + File.separator + FileUtils.JSON_FILE_NAME));
                    List<Photo> photoList;
                    Type type = new TypeToken<List<Photo>>(){}.getType();
                    photoList = gson.fromJson(br, type);
                    Log.d(TAG, "photoList.get(0).getLatitude()" + photoList.get(0).getLatitude());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });
        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d(TAG, "NO");
                finish();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
