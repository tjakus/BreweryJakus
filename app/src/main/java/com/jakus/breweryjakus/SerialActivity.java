package com.jakus.breweryjakus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SerialActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private Button btn1;
    private EditText text1;
    private Spinner serialSpinner;
    private SharedPreferences preference;
    private static String tempUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);

        Log.d("SerialActivity", "onCreate START");

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        btn1 = (Button) findViewById(R.id.btn1);;
        text1 = (EditText) findViewById(R.id.text10);
        serialSpinner = (Spinner) findViewById(R.id.mac_spinner);
        String[] items = new String[] {"Sensor T0", "Sensor T1", "Sensor T2", "Sensor T3"};
        Log.d("SerialActivity", "before spinner");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        serialSpinner.setAdapter(adapter);
        Log.d("SerialActivity", "after spinner");

        preference = getSharedPreferences("URL_PREF", 0);

        textView1.setText("Sensor:");
        textView2.setText("Serial:");
        text1.setText("28-FF-00-00-00-00-00-00");
        btn1.setText("SEND");



        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("SerialActivity", "BTN1 - " + text1.getText());
                Log.d("SerialActivity", "BTN1 - " + serialSpinner.getSelectedItem());
                Log.d("SerialActivity", "BTN1 - " + serialSpinner.getSelectedItemId());

                String[] macArray = text1.getText().toString().split("-");
                String mac = "";
                int i = 0;
                if (macArray.length == 8) {
                    while (i<8) {
                        mac = mac + Integer.parseInt(macArray[i],16) + "-";
                        i++;
                    }
                }

                Log.d("SerialActivity", "BTN1 - value: " + mac);

                tempUrl = preference.getString("URL", "http://127.0.0.1:8080") + "/?MAC_" + serialSpinner.getSelectedItemId() + "=" + mac + "&";

                Log.d("SerialActivity", "BTN1 - " + tempUrl);

                new SetSerial().execute();

            }
        });


        Log.d("SettingsActivity", "onCreate- END");
    }



    /**
     * Async task class to get json by making HTTP call
     */
    private class SetSerial extends AsyncTask<Void, Void, Void> {


        ProgressDialog pDialog;
        String jsonStr = null;

        @Override
        protected void onPreExecute() {
            Log.i("SetSerial.onPreExecute", "START");
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SerialActivity.this);
            pDialog.setMessage("Sending Sensor serialId.\nPlease wait...");
            pDialog.setCancelable(true);
            pDialog.show();
            Log.i("SetSerial.onPreExecute", "END");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.i("SetSerial.doInBackgrou", "START");
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            jsonStr = webreq.makeWebServiceCall(tempUrl);
            Log.i("SetSerial.doInBackgrou", tempUrl);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("SetSerial.onPostExecut", "START");
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (jsonStr == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SerialActivity.this);
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
            Log.i("SetSerial.onPostExecut", "END");

        }
    }
}
