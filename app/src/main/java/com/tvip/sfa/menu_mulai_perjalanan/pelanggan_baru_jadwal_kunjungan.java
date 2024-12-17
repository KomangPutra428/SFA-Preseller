package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.tvip.sfa.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class pelanggan_baru_jadwal_kunjungan extends AppCompatActivity {
    static CheckBox week1;
    static CheckBox week2;
    static CheckBox week3;
    static CheckBox week4;
    static CheckBox day1;
    static CheckBox day2;
    static CheckBox day3;
    static CheckBox day4;
    static CheckBox day5;
    static CheckBox day6;
    static CheckBox day7;
    Button batal, lanjutkan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_pelanggan_baru_jadwal_kunjungan);
        week1 = findViewById(R.id.week1);
        week2 = findViewById(R.id.week2);
        week3 = findViewById(R.id.week3);
        week4 = findViewById(R.id.week4);

        day1 = findViewById(R.id.day1);
        day2 = findViewById(R.id.day2);
        day3 = findViewById(R.id.day3);
        day4 = findViewById(R.id.day4);
        day5 = findViewById(R.id.day5);
        day6 = findViewById(R.id.day6);
        day7 = findViewById(R.id.day7);

        batal = findViewById(R.id.batal);
        lanjutkan = findViewById(R.id.lanjutkan);

        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!week1.isChecked() && !week2.isChecked() && !week3.isChecked() && !week4.isChecked()){
                    new SweetAlertDialog(pelanggan_baru_jadwal_kunjungan.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Pilih Week")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else if (!day1.isChecked()
                        && !day2.isChecked()
                        && !day3.isChecked()
                        && !day4.isChecked()
                        && !day5.isChecked()
                        && !day6.isChecked()
                        && !day7.isChecked()){
                    new SweetAlertDialog(pelanggan_baru_jadwal_kunjungan.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Pilih Hari")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                } else {
                    Intent intent = new Intent(getApplicationContext(), pelanggan_baru_pembayaran.class);
                    startActivity(intent);
                }
            }
        });

    }
}