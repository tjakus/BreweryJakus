package com.jakus.breweryjakus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SensorActivity extends AppCompatActivity {

    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private EditText text1;
    private EditText text2;
    private RadioGroup radioGroup1;
    private RadioButton radioButton;
    private RadioButton radioC;
    private RadioButton radioH;
    private Switch switch1;

    private SharedPreferences preference;
    //private static String url = "http://jakus.ddns.net:8080";
    private static String tempUrl = "";
    //private static String url = "http://192.168.11.190:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SensorActivity", "onCreate START");
        setContentView(R.layout.activity_sensor);
        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        btn1 = (Button) findViewById(R.id.btn1);
        text1 = (EditText) findViewById(R.id.text1);
        text2 = (EditText) findViewById(R.id.text2);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioC = (RadioButton) findViewById(R.id.radioC);
        radioH = (RadioButton) findViewById(R.id.radioH);
        switch1 = (Switch) findViewById(R.id.simpleSwitch1);

        preference = getSharedPreferences("URL_PREF", 0);

        final Intent intent = getIntent();

        Log.d("SensorActivity", intent.getStringExtra("id_to_show"));
        textView.setText(intent.getStringExtra("id_to_show"));
        textView1.setText("Temperature set:");
        textView2.setText("Temperature difference:");
        textView3.setText("Sensor mode:");
        text1.setText(intent.getStringExtra("TS"));
        text2.setText(intent.getStringExtra("TD"));
        switch1.setText("Sensor state: ");
        btn1.setText("SEND");
        Log.d("MD ", intent.getStringExtra("MD"));
        if (intent.getStringExtra("MD").compareToIgnoreCase("C") == 0) {
            radioGroup1.check(R.id.radioC);
        }
        else if (intent.getStringExtra("MD").compareToIgnoreCase("H") == 0) {
            radioGroup1.check(R.id.radioH);
        }
        if (intent.getStringExtra("SS").compareToIgnoreCase("0") == 0) {
            switch1.setChecked(false);
        }
        else if (intent.getStringExtra("SS").compareToIgnoreCase("1") == 0) {
            switch1.setChecked(true);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("SensorActivity", "BTN1 - " + text1.getText());
                Log.d("SensorActivity", "BTN1 - " + text2.getText());
                int selectedId = radioGroup1.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButton = (RadioButton) findViewById(selectedId);
                Log.d("SensorActivity", "BTN1 - " + radioButton.getText());
                Log.d("SensorActivity", "BTN1 - " + switch1.isChecked());


                tempUrl = preference.getString("URL", "http://127.0.0.1:8080") + "/?TS_" + intent.getStringExtra("position") + "=" + text1.getText() + "&";
                tempUrl = tempUrl + "/?DF_" + intent.getStringExtra("position") + "=" + text2.getText() + "&";
                tempUrl = tempUrl + "/?MD_" + intent.getStringExtra("position") + "=" + radioButton.getText().charAt(0) + "&";
                if (switch1.isChecked()) {
                    tempUrl = tempUrl + "/?SS_" + intent.getStringExtra("position") + "=1&";
                }
                else {
                    tempUrl = tempUrl + "/?SS_" + intent.getStringExtra("position") + "=0&";
                }

                Log.d("SensorActivity", "BTN1 URL - " + tempUrl);
                new SetSensors().execute();
            }
        });

        Log.d("SensorActivity", "onCreate- END");


    }



    /**
     * Async task class to get json by making HTTP call
     */
    private class SetSensors extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialog;
        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            Log.i("SetSensors.onPreExecute", "START");
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SensorActivity.this);
            pDialog.setMessage("Sending sensor data.\nPlease wait...");
            pDialog.setCancelable(true);
            pDialog.show();
            Log.i("SetSensors.onPreExecute", "END");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.i("SetSensors.doInBackgrou", "START");
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            jsonStr = webreq.makeWebServiceCall(tempUrl);
            Log.i("SetSensors.doInBackgrou", tempUrl);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("setSensors.onPostExecut", "START");
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (jsonStr == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SensorActivity.this);
                builder.setMessage("Connection error!!!");
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
            Log.i("SetSensors.onPostExecut", "END");

        }
    }
}
