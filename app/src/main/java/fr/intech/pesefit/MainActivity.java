package fr.intech.pesefit;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        test();

        // To make vertical bar chart, initialize graph id this way
        /*BarChart barChart = (BarChart) findViewById(R.id.chart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(4f, 0));
        entries.add(new BarEntry(8f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(12f, 3));
        entries.add(new BarEntry(18f, 4));
        entries.add(new BarEntry(9f, 5));

        BarDataSet dataSet = new BarDataSet(entries, "# of Calls");

        BarChart chart = new BarChart(this);
        setContentView(chart);

        BarData data = new BarData(dataSet);
        chart.setData(data);

        chart.setDescription("# of times Alice called Bob");*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        test();
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

    public void test() {
        _userDataManager= new UserDataManager(this);
        _userDataManager.open();
        List<UserData> listUserData = new ArrayList<UserData>();

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.set(Calendar.MINUTE, 0);
        beginCalendar.set(Calendar.SECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);

        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
        System.out.println("beginDate : " + sdf.format(beginCalendar.getTime()));
        System.out.println("endDate : " + sdf.format(endCalendar.getTime()));
        */

        listUserData = _userDataManager.getUserDataByHour(beginCalendar.getTime().getTime(), endCalendar.getTime().getTime());


        String date = DateUtils.formatDateTime(getApplicationContext(),listUserData.get(0).getDate(), DateUtils.FORMAT_SHOW_DATE);
        for ( UserData userData : listUserData ) {
            String formattedDate = DateUtils.formatDateTime(getApplicationContext(),
                    userData.getDate(), DateUtils.FORMAT_SHOW_TIME);
            String duration = String.valueOf(userData.getDuration() / 1000 / 60) + "min";
            String activity = activityMapping(userData.getActivity());

            Log.e("Date : ", formattedDate);
            Log.e("Durée : ", duration);
            Log.e("Activité : ", activity);
        }

        _userDataManager.close();

    }

    String activityMapping(int activity)
    {
        switch(activity)
        {
            case DetectedActivity.IN_VEHICLE: {
                return "Véhicule";
            }
            case DetectedActivity.ON_BICYCLE: {
                return "Vélo";
            }
            case DetectedActivity.RUNNING: {
                return "Course";
            }
            case DetectedActivity.STILL: {
                return "Glandage";
            }
            case DetectedActivity.WALKING: {
                return "Marche";
            }
            default: {
                // Should throw an exception
                return "";
            }
        }
    }

}
