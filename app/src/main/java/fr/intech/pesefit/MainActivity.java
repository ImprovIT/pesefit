package fr.intech.pesefit;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient _apiClient;
    private UserDataManager _userDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _apiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        _apiClient.connect(); // call onConnected
        displayData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( _apiClient, 3000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Retrieve data from database, arrange it and display it with a graphe
     */
    public void displayData() {






        _userDataManager= new UserDataManager(this);
        _userDataManager.open();
        List<UserData> listUserData = new ArrayList<UserData>();


        UserData userDataActivity = _userDataManager.getLastUserData();
        if (userDataActivity != null) {

        // Display current activity as text
        TextView currentActivity = (TextView)findViewById(R.id.currentActivity);
        currentActivity.setText("En " + activityNameMapping(userDataActivity.getActivity()));

        }


        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.set(Calendar.MINUTE, 0);
        beginCalendar.set(Calendar.SECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);

        listUserData = _userDataManager.getUserDataByHour(beginCalendar.getTime().getTime(), endCalendar.getTime().getTime());

        _userDataManager.close();

        if(!listUserData.isEmpty()) {

            String date = DateUtils.formatDateTime(getApplicationContext(), listUserData.get(0).getDate(), DateUtils.FORMAT_SHOW_DATE);

            // LineChart is initialized from xml
            LineChart chart = (LineChart) findViewById(R.id.chart);

            List<Entry> entries = new ArrayList<Entry>();

            for (UserData userData : listUserData) {
                String formattedDate = DateUtils.formatDateTime(getApplicationContext(),
                        userData.getDate(), DateUtils.FORMAT_SHOW_TIME);
                int duration = (int) userData.getDuration() / 1000 / 60;
                int activity = activityMapping(userData.getActivity());

                //Log.e("Date : ", formattedDate);
                //Log.e("Durée : ", duration);
                //Log.e("Activité : ", activity);


                // turn data into Entry objects
                entries.add(new Entry(duration, activity));
            }

            //Entries need to be added to a DataSet sorted by their x-position
            Collections.sort(entries, new EntryXComparator());

            // add entries to dataset
            LineDataSet dataSet = new LineDataSet(entries, "Time Line");


            // Style
            dataSet.setColor(Color.BLUE);
            dataSet.setValueTextColor(Color.BLACK);

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.setDescription(date);

            YAxis yLeftAxis = chart.getAxisLeft();
            yLeftAxis.setAxisMinValue(0f); // start at zero
            yLeftAxis.setAxisMaxValue(4f); // the axis maximum is 100
            yLeftAxis.setGranularity(1f); // interval 1

            YAxis yRightAxis = chart.getAxisRight();
            yRightAxis.setEnabled(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1 / 60f); // Try 1 || 60 || 1/60

            // Format Y legend
            // the labels that should be drawn on the YAxis

            String[] values = new String[]{"Glandage", "Marche", "Course", "Vélo", "Véhicule"};
            yLeftAxis.setValueFormatter(new MyYAxisValueFormatter(values));


            // Format X legend
            //String[] xValues = new String[]{"00:00","1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","21:00","22:00","23:00"};
            //xAxis.setValueFormatter(new axisValueFormatter(xValues));

            chart.invalidate(); // refresh
        }
    }

    /**
     * Detect the activity from the return int from DetectedActivity
     * @param activity
     * @return activity
     */
    int activityMapping(int activity)
    {
        switch(activity)
        {
            case DetectedActivity.STILL: {
                return 0;
            }
            case DetectedActivity.WALKING: {
                return 1;
            }
            case DetectedActivity.RUNNING: {
                return 2;
            }
            case DetectedActivity.ON_BICYCLE: {
                return 3;
            }
            case DetectedActivity.IN_VEHICLE: {
                return 4;
            }

            default: {
                // Should throws an exception
                return -1;
            }
        }
    }



    String activityNameMapping(int activity)
    {
        switch(activity)
        {
            case DetectedActivity.STILL: {
                return "Glandage";
            }
            case DetectedActivity.WALKING: {
                return "Marche";
            }
            case DetectedActivity.RUNNING: {
                return "Course";
            }
            case DetectedActivity.ON_BICYCLE: {
                return "Vélo";
            }
            case DetectedActivity.IN_VEHICLE: {
                return "Véhicule";
            }

            default: {
                // Should throws an exception
                return "";
            }
        }
    }


}
