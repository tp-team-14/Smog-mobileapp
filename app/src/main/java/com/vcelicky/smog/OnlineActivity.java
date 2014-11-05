package com.vcelicky.smog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jerry on 5. 11. 2014.
 */
public class OnlineActivity extends BaseActivity {
    private static final String TAG = OnlineActivity.class.getSimpleName();

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
        builder.setPositiveButton("Áno", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.d(TAG, "YES");
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d(TAG, "NO");
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
