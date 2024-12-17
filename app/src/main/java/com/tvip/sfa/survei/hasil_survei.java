package com.tvip.sfa.survei;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.tvip.sfa.R;

public class hasil_survei extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_survei);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), tambah_survei.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Call Only, if you wants to clears the activity stack else ignore it.
        startActivity(intent);
        finish();
    }
}