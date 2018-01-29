package com.jakus.breweryjakus;

import android.graphics.Color;
import android.util.Log;
import android.widget.BaseAdapter;
import android.app.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sccomponents.gauges.ScArcGauge;
import com.sccomponents.gauges.ScGauge;


/**
 * Created by etomjak on 01/05/17.
 */

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;


    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        Log.i("LazyAdapter", "getItem");
        return data.get(position);
    }

    public Object getItemAtPosition(int position) {
        Log.i("LazyAdapter", "getItemAtPosition");
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView sensorId = (TextView)vi.findViewById(R.id.sensor_id);
        TextView serialId = (TextView) vi.findViewById(R.id.serial_id);
        TextView tempValue = (TextView) vi.findViewById(R.id.temp_value);
        TextView tempSd = (TextView) vi.findViewById(R.id.temp_sd);
        TextView SSR = (TextView) vi.findViewById(R.id.SSR);
        TextView MOD = (TextView) vi.findViewById(R.id.MOD);
        TextView sensorState = (TextView) vi.findViewById(R.id.SS);


        HashMap<String, String> sensor = new HashMap<String, String>();
        sensor = data.get(position);

        // Setting all values in listview
        sensorId.setText(sensor.get(MainActivity.TAG_ID_TO_SHOW));
        serialId.setText(sensor.get(MainActivity.TAG_MAC_TO_SHOW));
        tempValue.setText(sensor.get(MainActivity.TAG_TEMP_VALUE_TO_SHOW));
        tempSd.setText(sensor.get(MainActivity.TAG_TEMP_SD_TO_SHOW));
        SSR.setText(sensor.get(MainActivity.TAG_SSR_TO_SHOW));
        if (sensor.get(MainActivity.TAG_SSR).equals("1"))
            SSR.setBackgroundColor(Color.GREEN);
        else
            SSR.setBackgroundColor(Color.LTGRAY);
        MOD.setText(sensor.get(MainActivity.TAG_MOD_TO_SHOW));
        sensorState.setText(sensor.get(MainActivity.TAG_STATE_TO_SHOW));
        if (sensor.get(MainActivity.TAG_STATE).equals("1"))
            sensorState.setBackgroundColor(Color.GREEN);
        else
            sensorState.setBackgroundColor(Color.GRAY);

        // Find the components
        final ScArcGauge gauge = (ScArcGauge) vi.findViewById(R.id.gauge);

        // Get the indicator image and set the center pivot for a right rotation
        final ImageView indicator = (ImageView) vi.findViewById(R.id.indicator);
        indicator.setPivotX(20f);
        indicator.setPivotY(20f);

        // If you set the value from the xml that not produce an event so I will change the
        // value from code.
        //gauge.setLowValue(0);
        float currentTemp = Float.parseFloat(sensor.get(MainActivity.TAG_TEMP_VALUE));
        if (currentTemp < 0)
            currentTemp = 0.1f;

        gauge.setHighValue(currentTemp);

        Log.i("LazyAdapter", "setHighValue" + currentTemp);
        // Each time I will change the value I must write it inside the counter text.
        gauge.setOnEventListener(new ScGauge.OnEventListener() {
            @Override
            public void onValueChange(float lowValue, float highValue) {
                Log.i("LazyAdapter", "setOnEventListener - low" + lowValue);
                Log.i("LazyAdapter", "setOnEventListener - high" + highValue);
                // Convert the percentage value in an angle
                float temp = highValue / 36f * 100f;
                if (temp > 100f)
                    temp = 100f;
                float angle = gauge.percentageToAngle(temp);
                // Apply the angle to indicator
                indicator.setRotation(angle);
            }
        });

        return vi;
    }

}
