package fr.intech.pesefit;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;


import java.util.ArrayList;
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

        listUserData = _userDataManager.getUserDataByHour(new Date().getTime() - 3*60*60*1000, new Date().getTime());

        for ( UserData userData : listUserData ) {
            userData.getDate();
            userData.getDuration();
            userData.getActivity();
        }

        _userDataManager.close();

        //UserData lastUserData = _userDataManager.getLastUserData();

        /*EditText text = (EditText) findViewById(R.id.text47);
        text.setText(String.valueOf(lastUserData.getDate()) + " " +
                String.valueOf(lastUserData.getDuration()) +" "
                + String.valueOf(lastUserData.getActivity()));
                */
    }
}
