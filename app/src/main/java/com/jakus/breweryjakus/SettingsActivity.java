package com.jakus.breweryjakus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.SharedPreferences;

public class SettingsActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;
    private Button btn1;
    private EditText text1;
    private EditText text2;
    private EditText text3;
    private EditText text4;
    private EditText text5;
    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SettingsActivity", "onCreate START");
        setContentView(R.layout.activity_settings);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        btn1 = (Button) findViewById(R.id.btn1);;
        text1 = (EditText) findViewById(R.id.text1);
        text2 = (EditText) findViewById(R.id.text2);
        text3 = (EditText) findViewById(R.id.text3);
        text4 = (EditText) findViewById(R.id.text4);
        text5 = (EditText) findViewById(R.id.text5);

        preference = getSharedPreferences("URL_PREF", 0);

        textView1.setText("URL:");
        textView2.setText("IDs:");
        text1.setText(preference.getString("URL", "http://127.0.0.1:8080"));
        text2.setText(preference.getString("T0", "FERMENTOR"));
        text3.setText(preference.getString("T1", "A/C"));
        text4.setText(preference.getString("T2", "AIR"));
        text5.setText(preference.getString("T3", "SPARE"));

        btn1.setText("SAVE");



        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("SettingsActivity", "BTN1 - " + text1.getText());
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("URL", text1.getText().toString());
                editor.putString("T0", text2.getText().toString());
                editor.putString("T1", text3.getText().toString());
                editor.putString("T2", text4.getText().toString());
                editor.putString("T3", text5.getText().toString());
                editor.commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("Settings saved!!!");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                    }
                });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
            }
        });


        Log.d("SettingsActivity", "onCreate- END");


    }

}
