package com.tvip.sfa.menu_mulai_perjalanan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.journeyapps.barcodescanner.CaptureActivity;

public class CustomCaptureActivity extends CaptureActivity {

    private Handler handler = new Handler();
    private boolean isResultHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start a timeout timer
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isResultHandled) {
                    // Provide feedback to the user
                    // Set the result as canceled and finish the activity
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }, 10000); // 10000 milliseconds = 10 seconds
    }

    @Override
    public void onBackPressed() {
        // Disable back button press while the scan is active
        if (!isResultHandled) {
            // Show a message or provide feedback if needed
            // Do nothing to prevent exiting the activity
        } else {
            super.onBackPressed(); // Allow back press if scan is handled
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isResultHandled = true; // Mark result as handled
    }
}
