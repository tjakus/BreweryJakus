package com.jakus.breweryjakus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import com.sccomponents.widgets.*;
import android.widget.AdapterView.OnItemClickListener;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences preference;
    // URL to get contacts JSON
    //private static String url = "http://jakus.ddns.net:8080";
    //private static String url = "http://192.168.11.190:8000";

    // JSON Node names
    public static final String TAG_ID = "id";
    public static final String TAG_ID_TO_SHOW = "id_to_show";
    public static final String TAG_TEMP_SET = "TS";
    public static final String TAG_TEMP_DIFFERENCE = "TD";
    public static final String TAG_TEMP_SD_TO_SHOW = "TS_to_show";
    public static final String TAG_TEMP_VALUE = "TV";
    public static final String TAG_TEMP_VALUE_TO_SHOW = "TV_to_show";
    public static final String TAG_SSR = "SSR";
    public static final String TAG_SSR_TO_SHOW = "SSR_to_show";
    public static final String TAG_POSITION = "position";
    public static final String TAG_MOD = "MD";
    public static final String TAG_MOD_TO_SHOW = "MD_to_show";
    public static final String TAG_STATE = "SS";
    public static final String TAG_STATE_TO_SHOW= "SS_to_show";
    public static final String TAG_MAC= "MAC";
    public static final String TAG_MAC_TO_SHOW= "MAC_to_show";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "START");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FAB.onClick", "START");
                new GetSensors().execute();
                Log.d("FAB.onClick", "END");
            }
        });

        preference = getSharedPreferences("URL_PREF", 0);

        new GetSensors().execute();
        Log.d("onCreate", "END");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d("onCreateOptionsMenu", "START");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d("onOptionsItemSelected", "START");
        int id = item.getItemId();

        if (id == R.id.action_serial)
            startActivity(new Intent(this, SerialActivity.class));
        else
            startActivity(new Intent(this, SettingsActivity.class));
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings || id == R.id.action_serial) {
            Log.d("onOptionsItemSelected", "return true");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        Log.i("GetSensors.ParseJSON", "START");
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> sensorList = new ArrayList<HashMap<String, String>>();

                JSONArray sensors = new JSONArray(json);

                // looping through all sensors
                for (int i = 0; i < sensors.length(); i++) {
                    JSONObject jsonObject = sensors.getJSONObject(i);

                    String id = jsonObject.getString(TAG_ID);
                    String temp_set = jsonObject.getString(TAG_TEMP_SET);
                    String temp_difference = jsonObject.getString(TAG_TEMP_DIFFERENCE);
                    String temp_value = jsonObject.getString(TAG_TEMP_VALUE);
                    String SSR = jsonObject.getString(TAG_SSR);
                    String MOD = jsonObject.getString(TAG_MOD);
                    String sens_state = jsonObject.getString(TAG_STATE);
                    String MAC = jsonObject.getString(TAG_MAC);


                    // tmp hashmap for single student
                    HashMap<String, String> sensorMap = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    sensorMap.put(TAG_ID, id);
                    sensorMap.put(TAG_ID_TO_SHOW, id + " - " + preference.getString("T" + i, "free text"));
                    sensorMap.put(TAG_TEMP_SET, temp_set);
                    sensorMap.put(TAG_TEMP_DIFFERENCE, temp_difference);
                    if (MOD.equalsIgnoreCase("C")) {
                        sensorMap.put(TAG_TEMP_SD_TO_SHOW, "Range: " + temp_set + " + " + temp_difference + " C");
                    }
                    else {
                        sensorMap.put(TAG_TEMP_SD_TO_SHOW, "Range: " + temp_set + " - " + temp_difference + " C");
                    }
                    sensorMap.put(TAG_TEMP_VALUE, temp_value);
                    sensorMap.put(TAG_TEMP_VALUE_TO_SHOW, "Current temp: " + temp_value + " C");
                    sensorMap.put(TAG_SSR, SSR);
                    sensorMap.put(TAG_SSR_TO_SHOW, "SSR state: " + SSR);
                    sensorMap.put(TAG_POSITION, "" + (i));
                    sensorMap.put(TAG_MOD, MOD);
                    sensorMap.put(TAG_MOD_TO_SHOW, "Mode: " + MOD);
                    sensorMap.put(TAG_STATE, sens_state);
                    sensorMap.put(TAG_STATE_TO_SHOW, "Sensor state: " + sens_state);
                    sensorMap.put(TAG_MAC_TO_SHOW, "SerialId: " + MAC);

                    // adding student to students list
                    sensorList.add(sensorMap);
                }
                Log.i("GetSensors.ParseJSON", "START");
                return sensorList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
            return null;
        }
    }

    public void onListClick(View view, HashMap sensorMap) {
        Log.d("onListClick", "START");
        Intent intent = new Intent(this, SensorActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(TAG_ID_TO_SHOW, sensorMap.get(TAG_ID_TO_SHOW).toString());
        intent.putExtra(TAG_TEMP_SET, sensorMap.get(TAG_TEMP_SET).toString());
        intent.putExtra(TAG_TEMP_DIFFERENCE, sensorMap.get(TAG_TEMP_DIFFERENCE).toString());
        intent.putExtra(TAG_TEMP_VALUE, sensorMap.get(TAG_TEMP_VALUE).toString());
        intent.putExtra(TAG_SSR, sensorMap.get(TAG_SSR).toString());
        intent.putExtra(TAG_POSITION, sensorMap.get(TAG_POSITION).toString());
        intent.putExtra(TAG_MOD, sensorMap.get(TAG_MOD).toString());
        intent.putExtra(TAG_STATE, sensorMap.get(TAG_STATE).toString());
        intent.putExtra(TAG_MAC_TO_SHOW, sensorMap.get(TAG_MAC_TO_SHOW).toString());
        startActivity(intent);
        Log.d("onListClick", "END");
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetSensors extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> sensorList = null;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            Log.i("GetSensors.onPreExecute", "START");
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading sensor data.\nPlease wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            Log.i("GetSensors.onPreExecute", "END");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.i("GetSensors.doInBackgrou", "START");
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall(preference.getString("URL", "http://127.0.0.1:8080"));

            Log.i("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                sensorList = ParseJSON(jsonStr);
            }
            Log.i("GetSensors.doInBackgrou", "END");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i("GetSensors.onPostExecut", "START");
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            if (sensorList != null) {


                LazyAdapter adapter = new LazyAdapter(com.jakus.breweryjakus.MainActivity.this, sensorList);
                ListView listview = (ListView) findViewById(R.id.list);
                listview.setAdapter(adapter);

                /*ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, sensorList,
                        R.layout.list_item, new String[]{TAG_ID_TO_SHOW, TAG_TEMP_VALUE_TO_SHOW, TAG_TEMP_SD_TO_SHOW, TAG_SSR_TO_SHOW, TAG_MOD_TO_SHOW, TAG_STATE_TO_SHOW}, new int[]{R.id.id,
                        R.id.temp_value, R.id.temp_sd, R.id.SSR, R.id.MOD, R.id.SS});*/



                //listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                listview.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position,
                                            long arg3) {
                        Log.d("OnItemClick", "START");
                        HashMap value = (HashMap) adapter.getItemAtPosition(position);


                        Log.d("OnItemClick", value.toString());
                        onListClick(v, value);
                    }
                });
                listview.setAdapter(adapter);
            }
            else {
                Log.d("GetSensors.onPostExecut", "START ALERT");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                Log.d("GetSensors.onPostExecut", "END ALERT");
            }
            Log.i("GetSensors.onPostExecut", "END");

        }
    }
}