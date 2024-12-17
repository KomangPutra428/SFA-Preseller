package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.tvip.sfa.R;

public class posm extends AppCompatActivity {
    LinearLayout materi, cek, historyposm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_posm);

        materi = findViewById(R.id.materi);
        cek = findViewById(R.id.cek);
        historyposm = findViewById(R.id.historyposm);

        historyposm.setVisibility(View.GONE);

        materi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent material = new Intent(getBaseContext(), materi_posm.class);
                startActivity(material);
            }
        });

        cek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cek = new Intent(getBaseContext(), cek_posm.class);
                startActivity(cek);
            }
        });

        historyposm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cek = new Intent(getBaseContext(), history_posm.class);
                startActivity(cek);
            }
        });

    }
}