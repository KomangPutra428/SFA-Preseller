package com.tvip.sfa.menu_mulai_perjalanan;

import androidx.appcompat.app.AppCompatActivity; import com.tvip.sfa.Perangkat.HttpsTrustManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tvip.sfa.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class sisa_stock extends AppCompatActivity {
    ImageButton qtystockminus, qtystockadd;
    ImageButton qtydisplayminus, qtydisplayadd;
    ImageButton qtyexpiredminus, qtyexpiredadd;

    static EditText qtystock;
    static EditText qtydisplay;
    static EditText qtyexpired;
    static EditText tglorder;

    static TextView namaproduk;

    Button reset, selanjutnya;

    int count, count1, count2;
    private SimpleDateFormat dateFormatter;
    private Calendar date;

    static String uang;

    static TextView listid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); HttpsTrustManager.allowAllSSL();
        setContentView(R.layout.activity_sisa_stock);
        namaproduk = findViewById(R.id.namaproduk);
        dateFormatter = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());



        uang = getIntent().getStringExtra("uang");

        String barang = getIntent().getStringExtra("nama_barang");
        String list = getIntent().getStringExtra("list");
        listid = findViewById(R.id.listid);
        listid.setText(list);

        qtystockminus = findViewById(R.id.qtystockminus);
        qtystockadd = findViewById(R.id.qtystockadd);

        qtydisplayminus = findViewById(R.id.qtydisplayminus);
        qtydisplayadd = findViewById(R.id.qtydisplayadd);

        qtyexpiredminus = findViewById(R.id.qtyexpiredminus);
        qtyexpiredadd = findViewById(R.id.qtyexpiredadd);

        qtystock = findViewById(R.id.qtystock);
        qtydisplay = findViewById(R.id.qtydisplay);
        qtyexpired = findViewById(R.id.qtyexpired);

        tglorder = findViewById(R.id.tglorder);

        reset = findViewById(R.id.reset);
        selanjutnya = findViewById(R.id.selanjutnya);
        namaproduk.setText(barang);


        count = 0;
        count1 = 0;
        count2 = 0;

        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM yyyy");
        String currentDateandTime2 = sdf2.format(new Date());
        tglorder.setText(currentDateandTime2);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                count1 = 0;
                count2 = 0;

                qtystock.setText("");
                qtydisplay.setText("");
                qtyexpired.setText("");
                tglorder.setText(currentDateandTime2);

            }
        });

        selanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtystock.setError(null);
                qtydisplay.setError(null);
                qtyexpired.setError(null);
                if(qtystock.getText().toString().length() == 0){
                    qtystock.setError("Qty Stok Wajib Di isi");
                } else if(qtydisplay.getText().toString().length() == 0){
                    qtydisplay.setError("Qty Display Wajib Di isi");
                } else if(qtyexpired.getText().toString().length() == 0){
                    qtyexpired.setError("Qty Expired Di isi");
                } else {
                    Intent intent = new Intent(getApplicationContext(), diskon.class);
                    startActivity(intent);
                }

            }
        });



        tglorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentDate = Calendar.getInstance();
                Calendar twoDaysAgo = (Calendar) currentDate.clone();
                twoDaysAgo.add(Calendar.DATE, 0);

                date = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date.set(year, monthOfYear, dayOfMonth);

                        tglorder.setText(dateFormatter.format(date.getTime()));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(sisa_stock.this, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(twoDaysAgo.getTimeInMillis());
                datePickerDialog.show();

            }
        });

        qtyexpiredadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count2++;
                qtyexpired.setText(String.valueOf(count2));
            }

        });
        qtyexpiredminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtyexpired.setText(String.valueOf(count2));
                if (count2 == 0) {
                    return;
                }
                count2--;
            }
        });

        qtydisplayadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count1++;
                qtydisplay.setText(String.valueOf(count1));
            }

        });
        qtydisplayminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtydisplay.setText(String.valueOf(count1));
                if (count1 == 0) {
                    return;
                }
                count1--;
            }
        });

        qtystockadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                qtystock.setText(String.valueOf(count));
            }

        });
        qtystockminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtystock.setText(String.valueOf(count));
                if (count == 0) {
                    return;
                }
                count--;
            }
        });
    }
}